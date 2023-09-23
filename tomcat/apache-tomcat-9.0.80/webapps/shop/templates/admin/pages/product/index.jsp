<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../common/data-layout.jsp">
    <jsp:param name="title" value="Quản Lý Sản Phẩm"/>
    <jsp:param name="searchComponent" value="product/search-form.jsp"/>
    <jsp:param name="subTitle" value="Danh sách sản phẩm"/>
    <jsp:param name="tableComponent" value="product/table.jsp"/>
    <jsp:param name="isPagination" value="true"/>
    <jsp:param name="actionComponent" value="product/action.jsp"/>
</jsp:include>