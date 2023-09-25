<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="table-responsive">
    <table class="table table-bordered w-100" id="dataTable">
        <thead>
        <tr>
            <th>STT</th>
            <th>Họ và tên</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ</th>
            <th>Ngày đăng ký</th>
            <th>Trạng thái</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${ paging.items }" varStatus="_status">
            <tr>
                <td>${ _status.index + 1 + ( (paging.page - 1) * paging.pageSize )}</td>
                <td>${ item.fullName }</td>
                <td>${ item.email }</td>
                <td>${ item.phone }</td>
                <td>${ item.address }</td>
                <td>
                    <fmt:formatDate value="${item.createdDate}" pattern="dd/MM/yyyy"/>
                </td>
                <td>${ item.active ? 'Hoạt động' : 'Không hoạt động' }</td>
                <td class="user-action">
                    <div>
                        <i class="fa fa-ellipsis-v cursor-pointer" aria-hidden="true"></i>
                        <c:set var="_item" value="${ item }" scope="request" />
                        <jsp:include page="table-action.jsp" />
                    </div>
                </td>
            </tr>

            <jsp:include page="../../../common/modal.jsp">
                <jsp:param name="id" value="update-user-${ requestScope._item.id }"/>
                <jsp:param name="_title" value="Cập nhật người dùng" />
                <jsp:param name="bodyComponent" value="/admin/pages/user/form.jsp"/>
                <jsp:param name="name" value="_item" />
            </jsp:include>
        </c:forEach>

        </tbody>
    </table>
</div>