package com.fashion.service;

import com.fashion.constant.MethodPayment;
import com.fashion.constant.OrderStatus;
import com.fashion.constant.RoleConstant;
import com.fashion.dao.CartDao;
import com.fashion.dao.OrderDao;
import com.fashion.dao.OrderDetailDao;
import com.fashion.dao.ProductDetailDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.cart.CartResponse;
import com.fashion.dto.notify.EmailNotification;
import com.fashion.dto.order.OrderDetailResponse;
import com.fashion.dto.order.OrderResponse;
import com.fashion.dto.order.OrderSearchRequest;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.UserResponse;
import com.fashion.entity.Order;
import com.fashion.entity.ProductDetail;
import com.fashion.service.notify.EmailNotifyService;
import com.fashion.service.paypal.PaypalService;
import com.fashion.utils.ObjectUtil;
import com.paypal.api.payments.Refund;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Setter
@Slf4j
public class OrderService extends BaseService {

    private OrderDao orderDao;
    private OrderDetailDao orderDetailDao;
    private ProductDetailDao productDetailDao;
    private CartDao cartDao;
    private PaypalService paypalService;

    private EmailNotifyService emailNotifyService;

    public PageResponse<OrderResponse> paginateOrderList(PageRequest<OrderSearchRequest> request) {
        return this.doSelect(session -> {
            PageResponse<Order> paged = this.orderDao.searchOrder(request, session);
            List<OrderResponse> orderResponses = ObjectUtil.mapList(paged.getItems(), OrderResponse.class);
            return new PageResponse<>(paged, orderResponses);
        });
    }

    public List<CartResponse> getCheckoutProduct(List<Long> cartIds, UserResponse currentUser) {
        return this.doSelect(session -> {
            return this.cartDao.getMyCart(currentUser.getEmail(), cartIds, session);
        });
    }

    public OrderResponse findByCode(String orderCode) {
        return this.doSelect(session -> {
            OrderResponse orderResponse = this.orderDao.findById(orderCode, session)
                    .map(x -> ObjectUtil.map(x, OrderResponse.class))
                    .orElseThrow(() -> new IllegalArgumentException(orderCode + " không tìm thấy"));
            List<OrderDetailResponse> od = this.orderDetailDao.findDetailByOrderId(orderCode, session);
            orderResponse.setOrderDetails(od);
            return orderResponse;
        });
    }

    public Result<String> changeStatusOrder(String orderCode, String note, OrderStatus status, UserResponse userResponse) {
        return this.tryCatchWithTransaction(session -> {
            Order order = this.orderDao.findById(orderCode, session).orElseThrow();
            boolean ok = status.getCondition().test(order, userResponse);
            if (!ok) {
                throw new IllegalArgumentException("Không thể cập nhật trạng thái: " + status.getValue());
            }

            if (OrderStatus.APPROVED.equals(status) || OrderStatus.CANCEL.equals(status) || OrderStatus.REJECT.equals(status)) {
                List<ProductDetail> productDetails = this.checkQuantityProduct(orderCode, status)
                        .apply(session);
                productDetails.forEach(detail -> this.productDetailDao.save(detail, session));
            }

            if (RoleConstant.USER.equals(userResponse.getRole())) {
                order.setUserNote(note);
            }

            if (RoleConstant.ADMIN.equals(userResponse.getRole())) {
                order.setAdminNote(note);
            }

            order.setStatus(status);
            this.orderDao.save(order, session);

            if ((OrderStatus.CANCEL.equals(status) || OrderStatus.REJECT.equals(status)) && MethodPayment.PAYPAL.equals(order.getMethodPayment())) {
                this.refundPaypalOrder(order);
            }
            this.sendEmail(order);
            return Result.<String>builder()
                    .isSuccess(true)
                    .message("Cập nhật trạng thái " + status.getValue() + " thành công")
                    .build();
        }, orderCode);
    }

    private void sendEmail(Order order) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setTemplate(true);
        emailNotification.setHtml(true);
        emailNotification.setReceiver(order.getCreatedBy());
        emailNotification.setTitle(order.getStatus().getValue() + " đơn hàng");
        emailNotification.setData(Map.of("order", order));
        emailNotification.setBody("/email/change-status-order.html");
        this.emailNotifyService.sendAsyncNotify(emailNotification);
    }

    private void refundPaypalOrder(Order order) {
        String paymentIdPaypal = order.getPaymentIdPaypal();
        if (StringUtils.isBlank(paymentIdPaypal)) return;
        Refund refund = this.paypalService.refund(paymentIdPaypal);
    }

    private Function<Session, List<ProductDetail>> checkQuantityProduct(String orderCode, OrderStatus orderStatus) {
        return session -> {
            List<OrderDetailResponse> details = this.orderDetailDao.findDetailByOrderId(orderCode, session);
            List<Long> ids = details.stream()
                    .map(OrderDetailResponse::getProductDetailId)
                    .collect(Collectors.toList());
            List<ProductDetail> productDetails = this.productDetailDao.findByIds(ids, session);
            Map<Long, ProductDetail> pDetailMap = productDetails
                    .stream()
                    .collect(Collectors.toMap(ProductDetail::getId, x -> x));
            Predicate<OrderDetailResponse> productHandler = d -> false;
            if (OrderStatus.APPROVED.equals(orderStatus)) {
                productHandler = this.handleProductApproveOrder(pDetailMap);
            } else if (OrderStatus.CANCEL.equals(orderStatus) || OrderStatus.REJECT.equals(orderStatus)) {
                productHandler = this.handleProductCancelOrder(pDetailMap);
            }
            String message = details
                    .stream()
                    .filter(productHandler)
                    .map(d -> String.format("%s-%s (%s-%s) không đủ số lượng", d.getProductCode(), d.getProductName(), d.getColor(), d.getSize()))
                    .collect(Collectors.joining("<br>"));
            if (StringUtils.isNotBlank(message)) {
                throw new IllegalArgumentException(message);
            }
            return productDetails;
        };
    }

    private Predicate<OrderDetailResponse> handleProductCancelOrder(Map<Long, ProductDetail> pDetailMap) {
        return d -> {
            ProductDetail productDetail = pDetailMap.get(d.getProductDetailId());
            productDetail.setQuantity( productDetail.getQuantity() + d.getQuantity() );
            return false;
        };
    }

    private Predicate<OrderDetailResponse> handleProductApproveOrder(Map<Long, ProductDetail> pDetailMap) {
        return d -> {
            ProductDetail productDetail = pDetailMap.get(d.getProductDetailId());
            if (productDetail.getQuantity() >= d.getQuantity()) {
                productDetail.setQuantity( productDetail.getQuantity() - d.getQuantity() );
                return false;
            }
            return true;
        };
    }
}
