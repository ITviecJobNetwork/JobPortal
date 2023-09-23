<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="card shadow mb-4">
    <!-- Card Header - Dropdown -->
    <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
        <h6 class="m-0 font-weight-bold text-primary">${ param._title }</h6>
    </div>
    <!-- Card Body -->
    <div class="card-body">
        <div class="chart-pie pt-4 pb-2">
            <canvas id="${ param._id }"></canvas>
        </div>
<%--        <div class="mt-4 text-center small">--%>
<%--            <span class="mr-2">--%>
<%--                <i class="fas fa-circle text-primary"></i> Direct--%>
<%--            </span>--%>
<%--            <span class="mr-2">--%>
<%--                <i class="fas fa-circle text-success"></i> Social--%>
<%--            </span>--%>
<%--            <span class="mr-2">--%>
<%--                <i class="fas fa-circle text-info"></i> Referral--%>
<%--            </span>--%>
<%--        </div>--%>
    </div>
</div>