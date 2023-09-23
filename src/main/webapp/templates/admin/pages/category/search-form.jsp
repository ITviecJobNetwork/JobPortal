<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form method="get" action="${contextPath}/admin/category" class="mt-3">
    <input type="hidden" name="page" value="1" />
    <input type="hidden" name="pageSize" value="5" />
    <div class="row">
        <div class="col-md-5">
            <input class="form-control" placeholder="Tên, mã danh mục" name="data" value="${ param.data }">
        </div>
        <div class="justify-content-center">
            <button class="btn btn-primary">Tìm kiếm</button>
        </div>
    </div>
</form>