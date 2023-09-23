<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../common/data-layout.jsp">
    <jsp:param name="title" value="Quản Lý Danh Mục"/>
    <jsp:param name="searchComponent" value="category/search-form.jsp"/>
    <jsp:param name="subTitle" value="Danh sách danh mục"/>
    <jsp:param name="tableComponent" value="category/table.jsp"/>
    <jsp:param name="isPagination" value="true"/>
    <jsp:param name="actionComponent" value="category/action.jsp"/>
</jsp:include>