<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="card shadow mb-4">
    <!-- Card Header - Dropdown -->
    <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
        <h6 class="m-0 font-weight-bold text-primary">${ param._title }</h6>
    </div>
    <!-- Card Body -->
    <div class="card-body">
        <div class="chart-area">
            <canvas id="${ param._id }"></canvas>
        </div>
    </div>
</div>