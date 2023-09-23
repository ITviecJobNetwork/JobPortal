<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="_model" value="${ requestScope.get( param.name ) }"  />
<form method="post" action="${ contextPath }/admin/user/update">
    <input type="hidden" value="${ _model.id }" name="id"/>
    <div class="row">
        <div class="col">
            <label class="required">Họ và tên</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="fullName" value="${ _model.fullName }" required>
        </div>

        <div class="col">
            <label>Email</label>
            <input type="email" class="form-control" placeholder="Nhập thông tin...." name="email" value="${ _model.email }" readonly>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label>Số điện thoại</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="phone" value="${ _model.phone }" required>
        </div>

        <div class="col">
            <label>Địa chỉ</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="address" value="${ _model.address }" required>
        </div>
    </div>

    <div class="mt-3 d-flex justify-content-center">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-primary ml-2">Lưu lại</button>
    </div>
</form>