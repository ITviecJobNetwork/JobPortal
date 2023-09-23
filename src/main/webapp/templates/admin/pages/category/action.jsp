<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="id" value="createCategory" />
<button class="btn btn-primary" data-toggle="modal" data-target="#${ id }">
    <i class="fa fa-plus" aria-hidden="true"></i>
    Tạo mới
</button>
<jsp:include page="../../../common/modal.jsp">
    <jsp:param name="id" value="${ id }"/>
    <jsp:param name="_title" value="Tạo mới danh mục"/>
    <jsp:param name="bodyComponent" value="/admin/pages/category/form.jsp" />
    <jsp:param name="name" value="_data"/>
</jsp:include>