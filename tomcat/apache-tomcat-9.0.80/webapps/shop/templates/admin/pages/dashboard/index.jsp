<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:formatNumber var="revenue" value="${ totalStatistic.revenue }" currencyCode="VND" currencySymbol="đ" scope="page"  />
<fmt:formatNumber var="cost" value="${ totalStatistic.cost }" currencyCode="VND" currencySymbol="đ" scope="page"  />
<fmt:formatNumber var="profit" value="${ totalStatistic.profit }" currencyCode="VND" currencySymbol="đ" scope="page"  />

<fmt:formatDate var="currentdate" value="${ now }" pattern="yyyy-MM-dd" />
<fmt:formatNumber var="todayRevenue" value="${ todayStatistic.revenue }" currencyCode="VND" currencySymbol="đ" scope="page"  />
<fmt:formatNumber var="todayCost" value="${ todayStatistic.cost }" currencyCode="VND" currencySymbol="đ" scope="page"  />
<fmt:formatNumber var="todayProfit" value="${ todayStatistic.profit }" currencyCode="VND" currencySymbol="đ" scope="page"  />

<!-- Page Heading -->
<jsp:include page="statistic.jsp">
    <jsp:param name="_title" value="Tổng số"/>
    <jsp:param name="_revenue" value="${ pageScope.revenue }"/>
    <jsp:param name="_cost" value="${ pageScope.cost }"/>
    <jsp:param name="_profit" value="${ pageScope.profit }"/>
    <jsp:param name="_totalOrder" value="${ totalStatistic.totalOrder }"/>
    <jsp:param name="_hrefOrder" value="${ contextPath }/admin/order"/>
</jsp:include>

<!-- Page Heading -->
<jsp:include page="statistic.jsp">
    <jsp:param name="_title" value="Hôm nay"/>
    <jsp:param name="_revenue" value="${ pageScope.todayRevenue }"/>
    <jsp:param name="_cost" value="${ pageScope.todayCost }"/>
    <jsp:param name="_profit" value="${ pageScope.todayProfit }"/>
    <jsp:param name="_totalOrder" value="${ todayStatistic.totalOrder }"/>
    <jsp:param name="_hrefOrder" value="${ contextPath }/admin/order?fromDate=${ currentDate }&toDate=${ currentDate }"/>
</jsp:include>

<div class="row">
    <jsp:include page="form-search.jsp" />

    <div class="d-flex">
        <!-- Pie Chart -->
        <div class="col-xl-4 col-lg-5">
            <jsp:include page="pie.jsp">
                <jsp:param name="_id" value="chartBill"/>
                <jsp:param name="_title" value="Đơn hàng"/>
            </jsp:include>
        </div>

        <!-- Pie Chart -->
        <div class="col-xl-4 col-lg-5">
            <jsp:include page="pie.jsp">
                <jsp:param name="_id" value="chartUser"/>
                <jsp:param name="_title" value="Người dùng"/>
            </jsp:include>
        </div>

        <!-- Pie Chart -->
        <div class="col-xl-4 col-lg-5">
            <jsp:include page="pie.jsp">
                <jsp:param name="_id" value="chartProduct"/>
                <jsp:param name="_title" value="Sản phẩm"/>
            </jsp:include>
        </div>
    </div>

    <div class="col-md-12">
        <jsp:include page="revenue-area.jsp">
            <jsp:param name="_id" value="revenue"/>
            <jsp:param name="_title" value="Doanh thu"/>
        </jsp:include>
    </div>

    <div class="col-md-12">
        <jsp:include page="revenue-area.jsp">
            <jsp:param name="_id" value="cost"/>
            <jsp:param name="_title" value="Chi phí"/>
        </jsp:include>
    </div>

    <div class="col-md-12">
        <jsp:include page="revenue-area.jsp">
            <jsp:param name="_id" value="profit"/>
            <jsp:param name="_title" value="Lợi nhuận"/>
        </jsp:include>
    </div>
</div>