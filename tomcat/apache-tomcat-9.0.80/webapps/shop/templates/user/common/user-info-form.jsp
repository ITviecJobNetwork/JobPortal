<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form method="post" action="${ contextPath }/user/update">
    <div class="row">
        <div class="col">
            <label class="required">Họ và tên</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="fullName" value="${ sessionScope.SESSION_USER.fullName }" required>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label>Email</label>
            <input type="email" class="form-control" placeholder="Nhập thông tin...." name="email" value="${ sessionScope.SESSION_USER.email }" readonly>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label>Số điện thoại</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="phone" value="${ sessionScope.SESSION_USER.phone }" required>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label>Địa chỉ</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="address" value="${ sessionScope.SESSION_USER.address }" required>
        </div>
    </div>

    <div class="mt-3 d-flex justify-content-center">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-primary ml-2">Lưu lại</button>
    </div>
</form>