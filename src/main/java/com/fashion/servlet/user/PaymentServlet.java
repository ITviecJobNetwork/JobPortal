package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.order.CreateOrderRequest;
import com.fashion.dto.user.UserResponse;
import com.fashion.exception.BusinessException;
import com.fashion.factory.PaymentFactory;
import com.fashion.service.payment.CODPaymentService;
import com.fashion.service.payment.IPaymentService;
import com.fashion.service.payment.PaypalPaymentService;
import com.fashion.service.paypal.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AccessLevel;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(name = "paymentServlet", urlPatterns = "/payment/*")
@Setter
public class PaymentServlet extends UserLayoutServlet {

    @Setter(AccessLevel.NONE)
    private PaymentFactory paymentFactory;

    private CODPaymentService codPaymentService;

    private PaypalPaymentService paypalPaymentService;

    private PaypalService paypalService;

    @Override
    public void init() throws ServletException {
        super.init();
        paymentFactory = new PaymentFactory(
                this.codPaymentService,
                this.paypalPaymentService
        );
    }

    @HttpMethod(method = HttpMethod.Method.POST)
    public Result<CreateOrderRequest> createOrder(
            @RequestObjectParam @Valid CreateOrderRequest createOrderRequest,
            HttpServletRequest req,
            UserResponse userResponse,
            Map<String, Object> state,
            HttpSession session
    ) {
        createOrderRequest.setUuid(UUID.randomUUID().toString());
        IPaymentService instance = this.paymentFactory.getInstance(createOrderRequest.getMethod());
        createOrderRequest.setCreatedBy(userResponse.getEmail());
        Result<CreateOrderRequest> result = instance.payment(createOrderRequest);
        if (!result.isSuccess()) {
            this.throwCreateOrderException(result.getMessage(), createOrderRequest, state, req);
        }
        state.put(AppConstant.RESULT_KEY, result);
        session.setAttribute(createOrderRequest.getUuid(), createOrderRequest);
        return result;
    }

    @HttpMethod("/execute")
    public Result<CreateOrderRequest> executePayment(
            @RequestParam("_uuid") String uuid,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Map<String, Object> state,
            HttpServletRequest req,
            HttpSession httpSession
    ) {
        Object dataCreateOrder = httpSession.getAttribute(uuid);
        if (Objects.isNull(dataCreateOrder)) throw new IllegalArgumentException("Order was not created");
        CreateOrderRequest createOrderRequest = (CreateOrderRequest) dataCreateOrder;
        try {
            Payment payment = this.paypalService.executePayment(paymentId, payerId);
            createOrderRequest.setPaymentId(payment.getId());
        } catch (PayPalRESTException ex) {
            throwCreateOrderException("Thanh toán thất bại", createOrderRequest, state, req);
        }
        Result<CreateOrderRequest> result = this.paypalPaymentService.createOrder(createOrderRequest);
        if (!result.isSuccess()) {
            this.throwCreateOrderException(result.getMessage(), createOrderRequest, state, req);
        }
        result.setSuccessUrl("redirect:/order");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(value = "/cancel")
    public PageContent cancelOrderPaypal(@RequestParam("_uuid") String uuid, HttpSession session, HttpServletRequest req) {
        session.removeAttribute(uuid);
        return PageContent.builder()
                .isRedirect(true)
                .url(req.getContextPath() + "/order/checkout?" + req.getQueryString())
                .build();
    }

    private void throwCreateOrderException(String errorMessage, CreateOrderRequest createOrderRequest, Map<String, Object> state, HttpServletRequest req) {
        String ids = createOrderRequest.getCartId()
                .stream()
                .map(id -> String.format("cartId=%s", id))
                .collect(Collectors.joining("&"));
        state.put(AppConstant.TRANSFORM_DATA_KEY, createOrderRequest);
        throw new BusinessException(errorMessage, req.getContextPath() + "/order/checkout?" + ids);
    }
}
