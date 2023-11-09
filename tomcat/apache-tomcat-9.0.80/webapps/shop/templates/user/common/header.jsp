<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<style>
  .cart-link {
    display: flex;
  }



    .cart-label {
      margin-left: 5px; /* Điều chỉnh khoảng cách từ biểu tượng */
    }

</style>

<div class="offcanvas-menu-overlay"></div>
<div class="offcanvas-menu-wrapper">
    <div class="offcanvas__option">
        <div class="offcanvas__links">
            <jsp:include page="status-login.jsp" />
        </div>
    </div>
    <div class="offcanvas__nav__option">
        <a href="${contextPath}/cart"><img src="${contextPathStatic}/img/icon/cart.png" alt=""></a>
    </div>
    <div id="mobile-menu-wrap"></div>
<%--    <div class="offcanvas__text">--%>
<%--        <p>Free shipping, 30-day return or refund guarantee.</p>--%>
<%--    </div>--%>
</div>
<!-- Offcanvas Menu End -->

<!-- Header Section Begin -->
<header class="header">
    <div class="header__top">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 col-md-7">
                </div>
                <div class="col-lg-6 col-md-5">
                    <div class="header__top__right">
                        <div class="header__top__links">
                            <jsp:include page="status-login.jsp" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="container">
            <div class="row">
                <div class="col-md-3">
                    <div class="header__logo">
                        <a href="${ contextPath }/home"><img src="${contextPathStatic}/img/logo.png" alt=""></a>
                    </div>
                </div>
                <div class="col-md-7">
                    <nav class="header__menu mobile-menu">
                        <ul>
                            <li class="${fn:startsWith(currentUri, contextPath += '/home') ? 'active' : ''}"><a href="${contextPath}/home">Trang chủ</a></li>
                            <li class="${fn:startsWith(currentUri, contextPath += '/shopping') ? 'active' : ''}"><a href="${contextPath}/shopping">Mua sắm</a></li>
                            <li class="${fn:startsWith(currentUri, contextPath += '/order') ? 'active' : ''}"><a href="${contextPath}/order">Đơn hàng</a></li>
                            <li class="${fn:startsWith(currentUri, contextPath += '/contact') ? 'active' : ''}"><a href="${contextPath}/contact">Liên hệ</a></li>
                            <li class="${fn:startsWith(currentUri, contextPath += '/about-us') ? 'active' : ''}"><a href="${contextPath}/about-us">Về chúng tôi</a></li>
                        </ul>
                    </nav>
                </div>
                <div class="col-md-2">
                    <div class="header__nav__option">
                        <a href="${contextPath}/cart" class="cart-link"><img src="${contextPathStatic}/img/icon/cart.png" alt=""></a>
                        <a href="${contextPath}/cart">Giỏ hàng</a>
                    </div>
                </div>
            </div>
            <div class="canvas__open"><i class="fa fa-bars"></i></div>
        </div>
</header>