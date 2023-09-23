<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Banner Section Begin -->
<section class="banner spad">
    <div class="container">
        <div class="row">
            <div class="col-lg-7 offset-lg-4">
                <c:set var="firstProductImage" value="${ productImage[0] }" scope="page" />
                <c:if test="${ firstProductImage ne null }">
                    <div class="banner__item">
                        <div class="banner__item__pic">
<%--                            <img src="${contextPath}/resources/img/banner/banner-1.jpg" alt="">--%>
                            <img src="${ firstProductImage.imageUrl }" alt="${ firstProductImage.productName }" />
                        </div>
                        <div class="banner__item__text">
                            <h2>${ firstProductImage.productName }</h2>
                            <a href="${ contextPath }/shopping/detail?pCode=${ firstProductImage.productCode }">Shop now</a>
                        </div>
                    </div>
                </c:if>
            </div>
            <div class="col-lg-5">
                <c:set var="secondProductImage" value="${ productImage[1] }" scope="page" />
                <c:if test="${ secondProductImage ne null }">
                    <div class="banner__item banner__item--middle">
                        <div class="banner__item__pic">
<%--                            <img src="${contextPath}/resources/img/banner/banner-2.jpg" alt="">--%>
                            <img src="${ secondProductImage.imageUrl }" alt="${ secondProductImage.productName }" />
                        </div>
                        <div class="banner__item__text">
                            <h2>${ secondProductImage.productName }</h2>
                            <a href="${ contextPath }/shopping/detail?pCode=${ secondProductImage.productCode }">Shop now</a>
                        </div>
                    </div>
                </c:if>
            </div>
            <div class="col-lg-7">
                <c:set var="thirdProductImage" value="${ productImage[2] }" scope="page" />
                <c:if test="${ thirdProductImage ne null }">
                    <div class="banner__item banner__item--last">
                        <div class="banner__item__pic">
<%--                            <img src="${contextPath}/resources/img/banner/banner-3.jpg" alt="">--%>
                            <img src="${ thirdProductImage.imageUrl }" alt="${ thirdProductImage.productName }" />
                        </div>
                        <div class="banner__item__text">
                            <h2>${ thirdProductImage.productName }</h2>
                            <a href="${ contextPath }/shopping/detail?pCode=${ thirdProductImage.productCode }">Shop now</a>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</section>
<!-- Banner Section End -->
