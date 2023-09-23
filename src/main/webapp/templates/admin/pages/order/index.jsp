<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../common/data-layout.jsp">
    <jsp:param name="title" value="Quản Lý Hóa Đơn"/>
    <jsp:param name="searchComponent" value="order/search-form.jsp"/>
    <jsp:param name="subTitle" value="Danh sách hóa đơn"/>
    <jsp:param name="tableComponent" value="order/table.jsp"/>
    <jsp:param name="isPagination" value="true"/>
    <jsp:param name="actionComponent" value="order/action.jsp"/>

</jsp:include>

<jsp:include page="../../../common/modal.jsp">
    <jsp:param name="id" value="order-detail"/>
    <jsp:param name="_title" value="Chi tiết đơn hàng" />
    <jsp:param name="bodyComponent" value="/admin/pages/order/order-detail.jsp"/>
    <jsp:param name="_sz" value="modal-xl"/>
</jsp:include>