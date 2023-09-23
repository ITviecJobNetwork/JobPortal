<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set scope="page" var="currentUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<!-- Header Section End --><!-- Offcanvas Menu Begin -->
<div class="offcanvas-menu-overlay"></div>
<div class="offcanvas-menu-wrapper">
    <div class="offcanvas__option">
        <div class="offcanvas__links">
            <a href="${ contextPath }/auth">Sign in</a>
            <a href="${ contextPath }/admin/home">Go to admin</a>
        </div>
    </div>
    <div class="offcanvas__nav__option">
        <a href="#" class="search-switch"><img src="${contextPathStatic}/img/icon/search.png" alt=""></a>
        <a href="#"><img src="${contextPathStatic}/img/icon/cart.png" alt=""> <span>0</span></a>
        <div class="price">$0.00</div>
    </div>
    <div id="mobile-menu-wrap"></div>
    <div class="offcanvas__text">
        <p>Free shipping, 30-day return or refund guarantee.</p>
    </div>
</div>
<!-- Offcanvas Menu End -->

<!-- Header Section Begin -->
<header class="header">
    <div class="header__top">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 col-md-7">
                    <div class="header__top__left">
                        <p>Free shipping, 30-day return or refund guarantee.</p>
                    </div>
                </div>
                <div class="col-lg-6 col-md-5">
                    <div class="header__top__right">
                        <div class="header__top__links">
                            <a href="${ contextPath}/auth">Sign in</a>
                            <a href="${ contextPath }/admin/home">Go to admin</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-lg-3 col-md-3">
                <div class="header__logo">
                    <a href="${ contextPath }/home"><img src="${contextPathStatic}/img/logo.png" alt=""></a>
                </div>
            </div>
            <div class="col-lg-6 col-md-6">
                <nav class="header__menu mobile-menu">
                    <ul>
                        <li class="${fn:startsWith(currentUri, contextPath += '/home') ? 'active' : ''}"><a href="${contextPath}/home">Home</a></li>
                        <li class="${fn:startsWith(currentUri, contextPath += '/shopping') ? 'active' : ''}"><a href="${contextPath}/shopping">Shop</a></li>
                        <li class="${fn:startsWith(currentUri, contextPath += '/order') ? 'active' : ''}"><a href="${contextPath}/order">Order</a></li>
                        <li class="${fn:startsWith(currentUri, contextPath += '/contact') ? 'active' : ''}"><a href="${contextPath}/contact">Contacts</a></li>
                        <li class="${fn:startsWith(currentUri, contextPath += '/about-us') ? 'active' : ''}"><a href="${contextPath}/about-us">About Us</a></li>
                    </ul>
                </nav>
            </div>
            <div class="col-lg-3 col-md-3">
                <div class="header__nav__option">
                    <a href="#" class="search-switch"><img src="${contextPathStatic}/img/icon/search.png" alt=""></a>
                    <a href="${contextPath}/cart"><img src="${contextPathStatic}/img/icon/cart.png" alt=""> <span>0</span></a>
                    <div class="price">$0.00</div>
                </div>
            </div>
        </div>
        <div class="canvas__open"><i class="fa fa-bars"></i></div>
    </div>
</header>