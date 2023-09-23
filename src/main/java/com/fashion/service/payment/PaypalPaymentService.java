package com.fashion.service.payment;

import com.fashion.annotation.Inject;
import com.fashion.constant.AppConstant;
import com.fashion.constant.MethodPayment;
import com.fashion.dto.base.Result;
import com.fashion.dto.exchangerate.ExchangeRateResponse;
import com.fashion.dto.order.CreateOrderRequest;
import com.fashion.entity.Cart;
import com.fashion.entity.Product;
import com.fashion.entity.ProductDetail;
import com.fashion.service.exchangerate.ExchangeRateCaller;
import com.fashion.service.shipping.DefaultShippingService;
import com.fashion.service.shipping.IShippingService;
import com.fashion.service.tax.DefaultTaxService;
import com.fashion.service.tax.ITaxService;
import com.fashion.utils.CurrencyUtil;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import lombok.Setter;
import lombok.SneakyThrows;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Setter
public class PaypalPaymentService extends AbstractPaymentService {

    private APIContext apiContext;
    private Properties properties;
    private ExchangeRateCaller exchangeRateCaller;

    @Setter(onParam_ = { @Inject(usedBean = DefaultShippingService.class )})
    private IShippingService shippingService;

    @Setter(onParam_ = { @Inject(usedBean = DefaultTaxService.class) })
    private ITaxService taxService;

    @Override
    public Result<CreateOrderRequest> payment(CreateOrderRequest createOrderRequest) {
        return this.tryCatchWithTransaction(session -> {
            List<Cart> carts = this.getMyCartByIds(createOrderRequest, session);
            Map<Long, ProductDetail> productDetailMap = this.getProductDetail(carts, session);
            this.validateQuantityProduct(carts, productDetailMap);
            Payment payment = this.authorizePayment(createOrderRequest, carts, productDetailMap, session);
            String approvalLink = this.getApprovalLink(payment);
            return Result.<CreateOrderRequest>builder()
                    .isSuccess(true)
                    .successUrl("external:" + approvalLink)
                    .build();
        }, createOrderRequest);
    }

    public Result<CreateOrderRequest> createOrder(CreateOrderRequest request) {
        return this.tryCatchWithTransaction(session -> {
            this.createOrder(request, session);
            return Result.<CreateOrderRequest>builder()
                    .isSuccess(true)
                    .message("Đặt hàng thành công")
                    .build();
        }, request);
    }

    @SneakyThrows
    public Payment authorizePayment(CreateOrderRequest createOrderRequest, List<Cart> carts, Map<Long, ProductDetail> productDetailMap, Session session) {
        Payer payer = getPayerInformation(createOrderRequest);
        RedirectUrls redirectUrls = getRedirectURLs(createOrderRequest);
        List<Transaction> listTransaction = getTransactionInformation(createOrderRequest, carts, productDetailMap, session);

        Payment requestPayment = new Payment();
        requestPayment.setTransactions(listTransaction);
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("sale");
        return requestPayment.create(apiContext);
    }

    private Payer getPayerInformation(CreateOrderRequest request) {
        PayerInfo payerInfo = new PayerInfo()
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName());
        return new Payer()
                .setPaymentMethod(MethodPayment.PAYPAL.name())
                .setPayerInfo(payerInfo);
    }

    private RedirectUrls getRedirectURLs(CreateOrderRequest request) {
        String cartIds = request.getCartId()
                .stream()
                .map(x -> "cartId=" + x)
                .collect(Collectors.joining("&"));
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(this.properties.getProperty("paypal.after-approval.cancel-url") + "?_uuid=" + request.getUuid() +"&" + cartIds);
        redirectUrls.setReturnUrl(this.properties.getProperty("paypal.after-approval.success-url") + "?_uuid=" + request.getUuid());
        return redirectUrls;
    }

    private List<Transaction> getTransactionInformation(CreateOrderRequest createOrderRequest, List<Cart> carts, Map<Long, ProductDetail> productDetailMap, Session session) {
        String currency = this.properties.getProperty("paypal.currency");
        ExchangeRateResponse rate = this.exchangeRateCaller.getRate();
        Map<String, BigDecimal> rates = rate.getRates();
        BigDecimal vndRate = rates.get("VND");
        vndRate.setScale(2, RoundingMode.HALF_EVEN);
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();

        BigDecimal totalTax = AppConstant.ZERO;
        BigDecimal subTotalPrice = AppConstant.ZERO;

        for (Cart cart : carts) {
            ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
            Product product = this.productDao.findById(productDetail.getProductId(), session).orElseThrow();

            BigDecimal price = CurrencyUtil.calculateDiscountPrice(productDetail.getPrice(), productDetail.getPercentDiscount(), cart.getQuantity());
            BigDecimal feeTax = this.taxService.calculateFeeTax(cart);
            subTotalPrice = subTotalPrice.add(price);
            totalTax = totalTax.add(feeTax);

            Item item = new Item();
            item.setCurrency(currency);
            item.setName(product.getCode() + " - " + product.getName());
            item.setPrice(CurrencyUtil.calculateOriginalAndDiscount(productDetail.getPrice(), productDetail.getPercentDiscount()).divide(vndRate, 2, RoundingMode.HALF_EVEN).toString());
            item.setTax(feeTax.toString());
            item.setQuantity(String.valueOf(cart.getQuantity()));
            items.add(item);
        }
        subTotalPrice = subTotalPrice.divide(vndRate, 2, RoundingMode.HALF_EVEN);
        totalTax = totalTax.divide(vndRate, 2, RoundingMode.HALF_EVEN);
        itemList.setItems(items);

        BigDecimal feeShipping = this.shippingService.calculateFeeShipping(createOrderRequest);
        Details details = new Details();
        details.setShipping(feeShipping.toString());
        details.setSubtotal(subTotalPrice.toString());
        details.setTax(totalTax.toString());

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(subTotalPrice.add(feeShipping).add(totalTax).toString());
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setItemList(itemList);

        List<Transaction> listTransaction = new ArrayList<>();
        listTransaction.add(transaction);

        return listTransaction;
    }

    private String getApprovalLink(Payment approvedPayment) {
        return approvedPayment.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                .map(Links::getHref)
                .findFirst()
                .orElse(null);
    }
}
