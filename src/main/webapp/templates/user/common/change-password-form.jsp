<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form method="post" action="${ contextPath }/user/change-password">
    <div class="row">
        <div class="col">
            <label class="required">Mật khẩu hiện tại</label>
            <input type="password" class="form-control" name="password" required>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label class="required">Mật khẩu mới</label>
            <input type="password" class="form-control" name="newPassword" required>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label class="required">Nhập lại mật khẩu mới</label>
            <input type="password" class="form-control" name="repeatPassword" required>
        </div>
    </div>

    <div class="mt-3 d-flex justify-content-center">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-primary ml-2">Lưu lại</button>
    </div>
</form>