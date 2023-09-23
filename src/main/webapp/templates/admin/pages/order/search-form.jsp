<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form action="${contextPath}/admin/order" class="mt-3">
    <div class="row">
        <div class="col-md-4">
            <label>Mã hóa đơn</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="code" value="${ param.code }">
        </div>
        <div class="col-md-4">
            <label>Mã khách hàng</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="createdBy" value="${ param.createdBy }">
        </div>
        <div class="col-md-4">
            <label>Hình thức thanh toán</label>
            <select class="custom-select" name="methodPayment">
                <option value="${ null }">-- Tất cả --</option>
                <c:forEach var="method" items="${ methodPayments }">
                    <option value="${ method.name() }" ${ param.methodPayment eq method.name() ? 'selected' : '' }>${ method.name() }</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col-md-4">
            <label>Số điện thoại</label>
            <div>
                <input class="form-control" name="phone" value="${ param.phone }">
            </div>
        </div>
        <div class="col-md-4">
            <label>Từ ngày</label>
            <div>
                <input type="date" class="form-control" name="fromDate" value="${ param.fromDate }">
            </div>
        </div>
        <div class="col md-4">
            <label>Đến ngày</label>
            <div>
                <input type="date" class="form-control" name="toDate" value="${ param.toDate }">
            </div>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col-md-4">
            <label>Địa chỉ</label>
            <div>
                <input class="form-control" name="address" value="${ param.address }">
            </div>
        </div>
        <div class="col-md-4">
            <label>Trạng thái</label>
            <select class="custom-select" name="status">
                <option value="${ null }">-- Tất cả --</option>
                <c:forEach var="status" items="${ statuses }">
                    <option value="${ status.name() }" ${ param.status eq status.name() ? 'selected' : '' }>${ status.value }</option>
                </c:forEach>
            </select>
        </div>
        <div class="col d-flex align-items-end">
            <button class="btn btn-primary">Tìm kiếm</button>
        </div>
    </div>
</form>