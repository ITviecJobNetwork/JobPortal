<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${ param._title }</h1>
</div>
<!-- Content Row -->
<div class="row">

    <!-- Earnings (Monthly) -->
    <div class="col-xl-3 col-md-6 mb-4">
        <jsp:include page="card.jsp">
            <jsp:param name="borderColorLeft" value="border-left-primary"/>
            <jsp:param name="titleColor" value="text-primary"/>
            <jsp:param name="title" value="Doanh Số"/>
            <jsp:param name="_href" value="#"/>
            <jsp:param name="value" value="${ param._revenue } ₫"/>
            <jsp:param name="icon" value="fa fa-money"/>
        </jsp:include>
    </div>

    <!-- Earnings (Monthly) -->
    <div class="col-xl-3 col-md-6 mb-4">
        <jsp:include page="card.jsp">
            <jsp:param name="borderColorLeft" value="border-left-success"/>
            <jsp:param name="titleColor" value="text-success"/>
            <jsp:param name="title" value="Lợi nhuận"/>
            <jsp:param name="_href" value="#"/>
            <jsp:param name="value" value="${ param._cost } ₫"/>
            <jsp:param name="icon" value="fa fa-money"/>
        </jsp:include>
    </div>

    <!-- Earnings (Monthly) -->
    <div class="col-xl-3 col-md-6 mb-4">
        <jsp:include page="card.jsp">
            <jsp:param name="borderColorLeft" value="border-left-warning"/>
            <jsp:param name="titleColor" value="text-warning"/>
            <jsp:param name="title" value="Chi phí"/>
            <jsp:param name="_href" value="#"/>
            <jsp:param name="value" value="${ param._profit } ₫"/>
            <jsp:param name="icon" value="fa fa-money"/>
        </jsp:include>
    </div>

    <!-- Pending Requests -->
    <div class="col-xl-3 col-md-6 mb-4">
        <jsp:include page="card.jsp">
            <jsp:param name="borderColorLeft" value="border-left-info"/>
            <jsp:param name="titleColor" value="text-info"/>
            <jsp:param name="title" value="Đơn hàng"/>
            <jsp:param name="_href" value="${ param._hrefOrder }"/>
            <jsp:param name="value" value="${ param._totalOrder }"/>
            <jsp:param name="icon" value="fa fa-tasks"/>
        </jsp:include>
    </div>
</div>