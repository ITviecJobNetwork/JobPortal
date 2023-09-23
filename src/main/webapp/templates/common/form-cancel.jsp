<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form method="${ param._methodSubmit }" action="${ param._urlSubmit }">
    <textarea class="form-control" rows="3" name="reasonCancel" placeholder="Nhập lý do hủy hóa đơn"></textarea>
    <div class="d-flex justify-content-center align-items-center mt-3">
        <button class="btn btn-secondary mr-3" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-danger">Xác nhận</button>
    </div>
</form>