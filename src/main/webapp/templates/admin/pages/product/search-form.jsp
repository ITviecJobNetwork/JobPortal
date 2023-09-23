<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form action="${contextPath}/admin/product">
    <div class="row">
        <div class="col">
            <label>Tên, mã sản phẩm</label>
            <input class="form-control" placeholder="Nhập thông tin...." name="codeName" value="${ param.codeName }">
        </div>
        <div class="col">
            <label>Loại sản phẩm</label>
            <select class="custom-select" name="categoryId">
                <option value="${ null }">--Tất cả--</option>
                <c:forEach var="item" items="${ categories }">
                    <option value="${ item.id }" ${ item.id eq param.categoryId ? 'selected' : ''}>${ item.code } - ${ item.name }</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="row mt-4">
        <div class="col">
            <label>Từ ngày</label>
            <div>
                <input type="date" class="form-control" name="fromDate" value="${ param.fromDate }">
            </div>
        </div>
        <div class="col form-group">
            <label>Đến ngày</label>
            <div>
                <input type="date" class="form-control" name="toDate" value="${ param.toDate }">
            </div>
        </div>
    </div>
    <div class="row justify-content-center">
        <button class="btn btn-primary">Tìm kiếm</button>
    </div>
</form>