package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.constant.OrderStatus;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.cart.CartResponse;
import com.fashion.dto.order.OrderResponse;
import com.fashion.dto.order.OrderSearchRequest;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.UserResponse;
import com.fashion.exception.BusinessException;
import com.fashion.service.OrderService;
import com.fashion.utils.CurrencyUtil;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet(name = "orderServlet", urlPatterns = "/order/*")
@Setter
public class OrderServlet extends UserLayoutServlet {

    private OrderService orderService;

    @HttpMethod
    public PageContent index(
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "5") Integer pageSize,
            @RequestObjectParam OrderSearchRequest orderSearchRequest,
            UserResponse userResponse,
            HttpServletRequest req
    ) {
        orderSearchRequest.setCreatedBy(userResponse.getEmail());
        PageRequest<OrderSearchRequest> pageRequest = new PageRequest<>();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        pageRequest.setData(orderSearchRequest);
        PageResponse<OrderResponse> paging = this.orderService.paginateOrderList(pageRequest);
        req.setAttribute(AppConstant.PAGING_KEY, paging);
        return PageContent.builder()
                .url("/user/pages/order/index")
                .css(List.of("/user/css/shopping-cart", "/user/css/paging", "/user/css/user-action"))
                .js(List.of("/admin/js/order"))
                .title("Order | Fashion Shop")
                .build();
    }

    @HttpMethod("/checkout")
    public PageContent checkout(
            @RequestParam("cartId") List<Long> cartIds,
            HttpServletRequest req,
            UserResponse userResponse
    ) {
        List<CartResponse> checkoutProduct = this.orderService.getCheckoutProduct(cartIds, userResponse);
        BigDecimal newPrice = new BigDecimal("0");
        BigDecimal oldPrice = new BigDecimal("0");

        for (CartResponse cp : checkoutProduct) {
            newPrice = CurrencyUtil.calculateDiscountPrice(cp.getPrice(), cp.getDiscount(), cp.getQuantity());
            oldPrice = CurrencyUtil.calculateOriginalPrice(cp.getPrice(), cp.getQuantity());
        }
        req.setAttribute("newPrice", newPrice);
        req.setAttribute("oldPrice", oldPrice);
        req.setAttribute("productList", checkoutProduct);
        return PageContent.builder()
                .url("/user/pages/checkout/index")
                .css(List.of("/user/css/checkout"))
                .title("Checkout | Fashion Shop")
                .build();
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/cancel")
    public Result<String> cancelOrder(
            @RequestParam("oCode") String orderCode,
            @RequestParam("reasonCancel") String reasonCancel,
            UserResponse userResponse,
            Map<String, Object> state,
            HttpServletRequest req
    ) {
        Result<String> result = this.orderService.changeStatusOrder(orderCode, reasonCancel, OrderStatus.CANCEL, userResponse);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), req.getContextPath() + "/order?" + req.getQueryString());
        }

        result.setSuccessUrl("redirect:/order?" + req.getQueryString());
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
}
