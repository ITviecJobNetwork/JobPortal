<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="checkout__order">
    <h4 class="order__title">ĐƠN HÀNG</h4>
    <div class="checkout__order__products">Sản phẩm <span>Tổng</span></div>
    <ul class="checkout__total__products">
        <c:forEach var="item" items="${ productList }">
            <li class="d-flex justify-content-between align-items-center border-top pt-2">
                <input type="hidden" name="cartId" value="${ item.cartId }" />
                <span class="d-flex flex-column">
                    <span class="font-weight-bold">${ item.productName }</span>
                    <span style="font-size: 13px">${ item.color } - ${ item.size }</span>
                </span>
                <span>
                  <jsp:include page="../../../common/currency.jsp">
                      <jsp:param name="_value" value="${ item.price * ( (100 - item.discount) / 100 ) * item.quantity }"/>
                  </jsp:include>
                </span>
            </li>
        </c:forEach>
    </ul>
    <ul class="checkout__total__all">
        <li>
            Giá gốc
            <span>
                <jsp:include page="../../../common/currency.jsp">
                    <jsp:param name="_value" value="${ oldPrice }"/>
                </jsp:include>
            </span>
        </li>
        <li>
            Giảm giá
            <span>
                <jsp:include page="../../../common/currency.jsp">
                    <jsp:param name="_value" value="${ oldPrice - newPrice }"/>
                </jsp:include>
            </span>
        </li>
        <li>
            Tổng
            <span>
                <jsp:include page="../../../common/currency.jsp">
                    <jsp:param name="_value" value="${ newPrice }"/>
                </jsp:include>
            </span>
        </li>
    </ul>
    <div class="checkout__input__checkbox">
        <label for="cod">
            COD
            <input type="radio" id="cod" name="method" value="COD" ${ _data.method eq 'COD' ? 'checked' : '' } ${ _data eq null ? 'checked' : '' }>
            <span class="checkmark"></span>
        </label>
    </div>
    <div class="checkout__input__checkbox">
        <label for="paypal">
            Paypal
            <input type="radio" id="paypal" name="method" value="PAYPAL" ${ _data.method eq 'PAYPAL' ? 'checked' : '' }>
            <span class="checkmark"></span>
        </label>
    </div>
    <button class="site-btn">Thanh toán</button>
</div>