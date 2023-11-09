<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="table-responsive">
    <table class="table table-bordered w-100" id="dataTable">
        <thead>
        <tr>
            <td class="table-column-header" data-col="stt" data-show="true">STT
            </td>
            <th class="table-column-header" data-col="image" data-show="true">Hình ảnh</th>
            <td class="table-column-header" data-col="code" data-show="true">Mã sản phẩm
            </td>
            <th class="table-column-header" data-col="name" data-show="true">Tên sản phẩm
            </th>
            <th class="table-column-header" data-col="price" data-show="true">Giá
            </th>
            <th class="table-column-header" data-col="categoryName" data-show="true">Loại sản phẩm
            </th>
            <th class="table-column-header" data-col="createdDate" data-show="true">Ngày tạo
            </th>
            <th class="table-column-header" data-col="updatedDate" data-show="true">Ngày cập nhật
            </th>
            <th class="table-column-header" data-col="status" data-show="true">Trạng thái
            </th>
            <th>Thao tác
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${ paging.items }" varStatus="_status">
            <tr>
                <td data-col="stt">${ _status.index + 1 + ( (paging.page - 1) * paging.pageSize ) }</td>
                <td data-col="image">
                    <img width="100%" height="100px" src="${ item.image }" alt="${ item.name }"
                         style="max-width: 135px;"/>
                </td>
                <td data-col="code">${ item.code }</td>
                <td data-col="name">${ item.name }</td>
                <td data-col="price">
                    <div class="d-flex align-items-center">
                        <jsp:include page="../../../common/currency.jsp">
                            <jsp:param name="_value" value="${ item.minPrice }"/>
                        </jsp:include>
                        <b class="mx-2">-</b>
                        <jsp:include page="../../../common/currency.jsp">
                            <jsp:param name="_value" value="${ item.maxPrice }"/>
                        </jsp:include>
                    </div>
                </td>
                <td data-col="categoryName">${ item.categoryName }</td>
                <td data-col="createdDate">
                    <fmt:formatDate value="${ item.createdDate }" pattern="dd/MM/yyyy"/>
                </td>
                <td data-col="updatedDate">
                    <fmt:formatDate value="${ item.updatedDate }" pattern="dd/MM/yyyy"/>
                </td>
                <td data-col="status">
                    <span class="${ item.active ? 'text-success' : 'text-danger'}">${ item.active ? 'Hoạt động' : 'Ngưng hoạt động' }</span>
                </td>
                <td class="user-action">
                    <div>
                        <i class="fa fa-ellipsis-v cursor-pointer" aria-hidden="true"></i>
                        <c:set var="_item" value="${ item }" scope="request"/>
                        <jsp:include page="table-action.jsp"/>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>