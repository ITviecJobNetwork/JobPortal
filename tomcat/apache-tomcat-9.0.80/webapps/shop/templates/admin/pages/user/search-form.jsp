<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form action="${contextPath}/admin/user" class="mt-3">
    <div class="row">
        <div class="col-md-4">
            <label>Email</label>
            <input type="email" class="form-control" placeholder="Nhập thông tin...." name="email" value="${ param.email }">
        </div>
        <div class="col-md-4">
            <label>Trạng thái</label>
            <select class="custom-select" name="active">
                <option value="${ null }" ${ param.active eq '' ? 'selected' : '' }>--Tất cả--</option>
                <option value="${ true }" ${ param.active eq 'true' ? 'selected' : '' }>Hoạt động</option>
                <option value="${ false }" ${ param.active eq 'false' ? 'selected' : '' }>Không hoạt động</option>
            </select>
        </div>
        <div class="d-flex align-items-end">
            <button class="btn btn-primary">Tìm kiếm</button>
        </div>
    </div>
</form>