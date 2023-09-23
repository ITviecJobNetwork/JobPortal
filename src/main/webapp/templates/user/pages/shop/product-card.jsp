<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="col-lg-${ param.lg } col-md-${ param.md } col-sm-${ param.sm }">
    <div class="product__item">
        <%--                        <div class="product__item__pic set-bg" data-setbg="${contextPath}/resources/img/product/product-1.jpg">--%>
        <div class="product__item__pic set-bg" data-setbg="${ product.image }">
            <%--                            <span class="label">New</span>--%>
            <ul class="product__hover">
                <li>
                    <a href="${ contextPath }/shopping/detail?pCode=${ product.code }">
                        <img src="${contextPathStatic}/img/icon/search.png" alt="">
                    </a>
                </li>
            </ul>
        </div>
        <div class="product__item__text">
            <h6 title="${ product.name }">${ product.name }</h6>
            <c:choose>
                <c:when test="${ product.minPrice eq product.maxPrice }">
                    <h5>
                        <jsp:include page="../../../common/currency.jsp">
                            <jsp:param name="_value" value="${ product.maxPrice }"/>
                        </jsp:include>
                    </h5>
                </c:when>
                <c:otherwise>
                    <div class="d-flex align-items-center" style="gap: 10px">
                        <h5>
                            <jsp:include page="../../../common/currency.jsp">
                                <jsp:param name="_value" value="${ product.minPrice }"/>
                            </jsp:include>
                        </h5>
                        <i class="fa fa-minus" aria-hidden="true"></i>
                        <h5>
                            <jsp:include page="../../../common/currency.jsp">
                                <jsp:param name="_value" value="${ product.maxPrice }"/>
                            </jsp:include>
                        </h5>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>