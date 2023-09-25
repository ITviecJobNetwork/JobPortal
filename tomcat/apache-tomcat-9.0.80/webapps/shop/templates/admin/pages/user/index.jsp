<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../common/data-layout.jsp">
    <jsp:param name="title" value="Quản Lý Người Dùng"/>
    <jsp:param name="searchComponent" value="user/search-form.jsp"/>
    <jsp:param name="subTitle" value="Danh sách người dùng"/>
    <jsp:param name="tableComponent" value="user/table.jsp"/>
    <jsp:param name="isPagination" value="true"/>
</jsp:include>

<jsp:include page="../../../common/modal.jsp">
    <jsp:param name="id" value="update-user-fail"/>
    <jsp:param name="_title" value="Cập nhật người dùng" />
    <jsp:param name="bodyComponent" value="/admin/pages/user/form.jsp"/>
    <jsp:param name="name" value="_dataFail" />
</jsp:include>