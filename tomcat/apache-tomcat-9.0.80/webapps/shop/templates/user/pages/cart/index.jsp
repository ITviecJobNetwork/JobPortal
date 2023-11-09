<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../../common/breadcrumb.jsp">
    <jsp:param name="bc1" value="Cart"/>
</jsp:include>
<!-- Shopping Cart Section Begin -->
<section class="shopping-cart spad pt-5">
    <div class="container">
        <form method="post" class="row">
            <div class="col-lg-8">
                <div class="shopping__cart__table">
                    <table>
                        <thead>
                            <tr>
                                <th>
                                    <input id="cart-selector" type="checkbox" style="width: 50px" />
                                </th>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Total</th>
                                <th class="cart__close">
                                    <c:if test="${ not empty myCart }">
                                        <a href="${ contextPath }/cart/delete-all">
                                            <i class="fa fa-close"></i>
                                        </a>
                                    </c:if>
                                    <c:if test="${ empty myCart }">
                                        <a href="javascript:void(0)">
                                            <i class="fa fa-close"></i>
                                        </a>
                                    </c:if>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cart" items="${ myCart }">
                                <tr class="${ cart.quantity > cart.maxQuantity ? 'border border-warning' : '' }" style="border-width: 2px;">
                                    <c:choose>
                                        <c:when test="${ cart.discount gt 0}">
                                            <c:set var="newPrice" value="${ cart.price * ( (100 - cart.discount) / 100 ) }" scope="page" />
                                            <c:set var="oldPrice" value="${ cart.price }" scope="page" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="newPrice" value="${ cart.price }" />
                                        </c:otherwise>
                                    </c:choose>
                                    <input type="hidden" value="${ cart.cartId }" name="id">
                                    <td>
                                        <input type="checkbox" name="cartId" value="${ cart.cartId }" style="width: 50px"  data-newPrice="${ newPrice }" data-oldPrice="${ oldPrice }" data-quantity="${ cart.quantity }"/>
                                    </td>
                                    <td class="product__cart__item">
                                        <a href="${ contextPath }/shopping/detail?pCode=${ cart.productCode }" class="product__cart__item__pic">
                                            <img class="w-100 h-100" src="${ cart.productDetailImage }" alt="${ cart.productName }">
                                        </a>
                                        <div class="product__cart__item__text">
                                            <a href="${ contextPath }/shopping/detail?pCode=${ cart.productCode }" class="h6">
                                                ${ cart.productName } - ${ cart.color } - ${ cart.size }
                                            </a>
                                            <div class="d-flex">
                                                <h5>
                                                    <jsp:include page="../../../common/currency.jsp">
                                                        <jsp:param name="_value" value="${ newPrice }"/>
                                                    </jsp:include>
                                                </h5>
                                                <span class="old-price ml-2 ${ cart.price eq newPrice ? 'd-none' : '' }">
                                                    <jsp:include page="../../../common/currency.jsp">
                                                        <jsp:param name="_value" value="${ oldPrice }"/>
                                                    </jsp:include>
                                                </span>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="quantity__item">
                                        <div class="quantity">
                                            <div class="pro-qty-2">
                                                <input value="${ cart.quantity }" name="quantity" max="${ cart.maxQuantity }" readonly>
                                            </div>
                                            <div>
                                                <strong>Max:&nbsp;</strong>
                                                <span>${ cart.maxQuantity }</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="cart__price">
                                        <jsp:include page="../../../common/currency.jsp">
                                            <jsp:param name="_value" value="${ newPrice * cart.quantity }"/>
                                        </jsp:include>
                                    </td>
                                    <td class="cart__close">
                                        <a href="${ contextPath }/cart/delete?cId=${ cart.cartId }">
                                            <i class="fa fa-close"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                </div>
                <div class="row">
                    <div class="col-lg-6 col-md-6 col-sm-6">
                        <div class="continue__btn">
                            <a href="${ contextPath }/shopping">Tiếp tục mua sắm</a>
                        </div>
                    </div>
                    <div class="col-lg-6 col-md-6 col-sm-6">
                        <div class="continue__btn update__btn">
                            <button formaction="${ contextPath }/cart/update" ><i class="fa fa-spinner"></i> Cập nhật</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="cart__total">
                    <h6>Tổng giá trị (<span id="quantity-cart">0</span>) </h6>
                    <ul>
                        <li>Giá gốc <span id="original-price">0</span></li>
                        <li>Giảm giá <span id="discount-price">0</span></li>
                        <li>Tổng <span id="total-price">0</span></li>
                    </ul>
                    <button id="btn-checkout" data-href="${ contextPath }/order/checkout" class="primary-btn disabled text-white w-100" formmethod="GET">Đặt hàng</button>
                </div>
            </div>
        </form>
    </div>
</section>
<!-- Shopping Cart Section End -->