<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="table-responsive">
    <table class="table table-bordered w-100" id="dataTable">
        <thead>
        <tr>
            <th>STT</th>
            <th>Mã danh mục</th>
            <th>Tên danh mục</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${ paging.items }" varStatus="_status">
            <tr>
                <td>${ _status.index + 1 + ( (paging.page - 1) * paging.pageSize ) }</td>
                <td>${ item.code }</td>
                <td>${ item.name }</td>
                <td>${ item.active ? 'Hoạt động' : 'Dừng hoạt động' }</td>
                <td class="d-flex justify-content-center">
                    <button title="Chỉnh sửa danh mục" class="btn btn-primary" data-toggle="modal" data-target="#update-${ item.code }">
                        <i class="fa fa-pencil-square-o text-white" aria-hidden="true"></i>
                    </button>
                    <c:set var="_item" value="${ item }" scope="request"  />
                    <jsp:include page="../../../common/modal.jsp">
                        <jsp:param name="id" value="update-${ item.code }"/>
                        <jsp:param name="_title" value="Cập nhật danh mục"/>
                        <jsp:param name="bodyComponent" value="/admin/pages/category/form.jsp" />
                        <jsp:param name="name" value="_item" />
                    </jsp:include>

                    <c:if test="${ item.active }">
                        <button title="Khóa danh mục" class="btn btn-danger ml-3" data-toggle="modal" data-target="#delete-${ item.code }">
                            <i class="fa fa-trash text-white" aria-hidden="true"></i>
                        </button>
                    </c:if>

                    <c:if test="${ !item.active }">
                        <button title="Mở khóa danh mục" class="btn btn-success ml-3" data-toggle="modal" data-target="#delete-${ item.code }">
                            <i class="fa fa-check text-white" aria-hidden="true"></i>
                        </button>
                    </c:if>

                    <jsp:include page="../../../common/modal.jsp">
                        <jsp:param name="id" value="delete-${ item.code }"/>
                        <jsp:param name="_title" value="${ item.active ? 'Xác nhận xóa danh mục' : 'Xác nhận mở danh mục'}"/>
                        <jsp:param name="content" value="Bạn có chắc chắn muốn ${ item.active ? 'xóa' : 'mở' } danh mục ${ item.code } không?"/>
                        <jsp:param name="successUrl" value="${ contextPath }/admin/category/delete?code=${ item.code }"/>
                    </jsp:include>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>