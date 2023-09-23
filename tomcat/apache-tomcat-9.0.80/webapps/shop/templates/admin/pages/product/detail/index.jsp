<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <a href="${ contextPath }/admin/product" class="mb-3">Quay lại</a>
    <jsp:include page="../../../common/data-layout.jsp">
        <jsp:param name="title" value="Chi tiết Sản Phẩm"/>
        <jsp:param name="subTitle" value="${ requestScope._product.code } - ${ requestScope._product.name }"/>
        <jsp:param name="tableComponent" value="product/detail/detail-table.jsp"/>
        <jsp:param name="isPagination" value="false"/>
        <jsp:param name="actionComponent" value="product/detail/action.jsp"/>
    </jsp:include>
</div>