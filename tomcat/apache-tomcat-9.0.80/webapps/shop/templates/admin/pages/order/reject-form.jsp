<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="${ contextPath }/admin/order/change-status">
    <input type="hidden" name="status" value="REJECT" />
    <input type="hidden" name="oCode" value="${ _item.code }" />
    <div class="row">
        <div class="col-md-12">
            <label class="required">Lý do từ chối</label>
            <textarea name="adminNote" rows="5" placeholder="Nhập lý do từ chối" class="form-control" required maxlength="4000"></textarea>
        </div>
    </div>

    <div class="mt-3 d-flex justify-content-center">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-primary ml-2">Lưu lại</button>
    </div>
</form>