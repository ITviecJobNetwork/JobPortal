<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Sidebar -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- Sidebar - Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="${contextPath}/admin/home">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fa fa-yelp" aria-hidden="true"></i>
        </div>
        <div class="sidebar-brand-text mx-3">Fashion</div>
    </a>

    <!-- Divider -->
    <hr class="sidebar-divider my-0">

    <jsp:include page="../common/link.jsp">
        <jsp:param name="link" value="${contextPath}/admin/home"/>
        <jsp:param name="label" value="Dashboard"/>
        <jsp:param name="icon" value="fa fa-tachometer"/>
        <jsp:param name="active" value="${fn:startsWith(currentUri, contextPath += '/admin/home') ? true : false}" />
    </jsp:include>

    <!-- Divider -->
    <hr class="sidebar-divider">

    <jsp:include page="../common/link.jsp">
        <jsp:param name="link" value="${contextPath}/admin/category"/>
        <jsp:param name="label" value="Quản lý danh mục"/>
        <jsp:param name="icon" value="fa fa-table"/>
        <jsp:param name="active" value="${fn:startsWith(currentUri, contextPath += '/admin/category') ? true : false}" />
    </jsp:include>

    <jsp:include page="../common/link.jsp">
        <jsp:param name="link" value="${contextPath}/admin/product"/>
        <jsp:param name="label" value="Quản lý sản phẩm"/>
        <jsp:param name="icon" value="fa fa-table"/>
        <jsp:param name="active" value="${fn:startsWith(currentUri, contextPath += '/admin/product') ? true : false}" />
    </jsp:include>

    <jsp:include page="../common/link.jsp">
        <jsp:param name="link" value="${contextPath}/admin/user"/>
        <jsp:param name="label" value="Quản lý khách hàng"/>
        <jsp:param name="icon" value="fa fa-table"/>
        <jsp:param name="active" value="${fn:startsWith(currentUri, contextPath += '/admin/user') ? true : false}" />
    </jsp:include>

    <jsp:include page="../common/link.jsp">
        <jsp:param name="link" value="${contextPath}/admin/order"/>
        <jsp:param name="label" value="Quản lý đơn hàng"/>
        <jsp:param name="icon" value="fa fa-table"/>
        <jsp:param name="active" value="${fn:startsWith(currentUri, contextPath += '/admin/order') ? true : false}" />
    </jsp:include>

    <!-- Divider -->
    <hr class="sidebar-divider d-none d-md-block">

    <!-- Sidebar Toggler (Sidebar) -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>

</ul>
<!-- End of Sidebar -->