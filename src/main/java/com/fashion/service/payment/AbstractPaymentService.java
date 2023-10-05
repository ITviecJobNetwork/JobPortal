package com.fashion.service.payment;

import com.fashion.constant.OrderStatus;
import com.fashion.dao.*;
import com.fashion.dto.order.CreateOrderRequest;
import com.fashion.entity.*;
import com.fashion.service.BaseService;
import com.fashion.utils.CurrencyUtil;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Setter
public  abstract class AbstractPaymentService extends BaseService implements IPaymentService {

    protected OrderDao orderDao;
    protected CartDao cartDao;
    protected ProductDetailDao productDetailDao;
    protected ProductDao productDao;
    protected ColorProductDao colorProductDao;
    protected OrderDetailDao orderDetailDao;

    public void createOrder(CreateOrderRequest createOrderRequest, Session session) {
        List<Cart> carts = this.getMyCartByIds(createOrderRequest, session);
        Map<Long, ProductDetail> productDetailMap = this.getProductDetail(carts, session);
        this.validateQuantityProduct(carts, productDetailMap)
                .accept(session);
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setNote(createOrderRequest.getNote());
        order.setAddress(createOrderRequest.getAddress());
        order.setCreatedBy(createOrderRequest.getCreatedBy());
        order.setPhone(createOrderRequest.getPhone());
        order.setFullName(createOrderRequest.getFullName());
        order.setMethodPayment(createOrderRequest.getMethod());
        order.setTotal(this.calculateTotalPrice(carts, productDetailMap));
        order.setPaymentIdPaypal(createOrderRequest.getPaymentId());
        String code = this.orderDao.save(order, session);
        order.setCode(code);

        for (Cart cart : carts) {
            ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderCode(code);
            orderDetail.setCost(productDetail.getCost());
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setPrice(productDetail.getPrice());
            orderDetail.setPercentDiscount(productDetail.getPercentDiscount());
            orderDetail.setProductDetailId(productDetail.getId());
            this.orderDetailDao.save(orderDetail, session);
            productDetail.setQuantity(productDetail.getQuantity() - cart.getQuantity());
            this.productDetailDao.save(productDetail, session);
            this.cartDao.delete(cart, session);
        }
    }

    protected List<Cart> getMyCartByIds(CreateOrderRequest createOrderRequest, Session session) {
        return this.cartDao.findByIdsAndEmail(createOrderRequest.getCartId(), createOrderRequest.getCreatedBy(), session);
    }

    protected List<Long> getProductDetailIds(List<Cart> carts) {
        return carts.stream()
                .map(Cart::getProductDetailId)
                .collect(Collectors.toList());
    }

    protected Map<Long, ProductDetail> getProductDetail(List<Cart> carts, Session session) {
        List<Long> productDetailIds = this.getProductDetailIds(carts);
        return this.productDetailDao.findByIds(productDetailIds, session)
                .stream()
                .collect(Collectors.toMap(ProductDetail::getId, v -> v));
    }

    protected Consumer<Session> validateQuantityProduct(List<Cart> carts, Map<Long, ProductDetail> productDetailMap) {
        return session -> {
            String errorMessage = carts
                    .stream()
                    .filter(cart -> {
                        ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
                        return cart.getQuantity() > productDetail.getQuantity();
                    })
                    .map(cart -> {
                        ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
                        Product product = this.productDao.findById(productDetail.getProductId(), session).orElseThrow();
                        if (!product.getActive()) {
                            return String.format("%s không còn hỗ trợ", product.getName());
                        }
                        ColorProduct cp = this.colorProductDao.findById(productDetail.getColorProductId(), session).orElseThrow();
                        if (!productDetail.getActive()) {
                            return String.format("%s-%s-%s không còn hỗ trợ", product.getName(), cp.getName(), productDetail.getSize());
                        }
                        return String.format("%s-%s-%s không đủ số lượng", product.getName(), cp.getName(), productDetail.getSize());
                    })
                    .collect(Collectors.joining("</br>"));
            if (StringUtils.isNotBlank(errorMessage)) {
                throw new IllegalArgumentException(errorMessage);
            }
        };
    }

    protected BigDecimal calculateTotalPrice(List<Cart> carts, Map<Long, ProductDetail> productDetailMap) {
        return carts.stream()
                .map(cart -> {
                    ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
                    return CurrencyUtil.calculateDiscountPrice(productDetail.getPrice(), productDetail.getPercentDiscount(), cart.getQuantity());
                })
                .reduce(new BigDecimal("0"), BigDecimal::add);
    }
}
