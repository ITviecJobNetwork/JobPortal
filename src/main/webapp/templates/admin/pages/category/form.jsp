<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="_model" value="${ requestScope.get( param.name ) }"  />
<form method="post" action="${ contextPath }/admin/category/create">
    <input type="hidden" value="${ _model.id }" name="id"/>
    <input type="hidden" name="redirectUrlFallback" value="${ contextPath }/admin/category" />
    <div class="row">
        <div class="col">
            <label class="required">Mã danh mục</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="code" value="${ _model.code }" ${ _model.id ne null ? 'readonly' : '' } required>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col">
            <label class="required">Tên danh mục</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="name" value="${ _model.name }" required>
        </div>
    </div>

    <div class="mt-3 d-flex justify-content-center">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
        <button class="btn btn-primary ml-2">Lưu lại</button>
    </div>
</form>