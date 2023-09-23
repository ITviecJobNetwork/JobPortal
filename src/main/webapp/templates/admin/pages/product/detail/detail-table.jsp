<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="table-responsive">
    <table class="table table-bordered w-100" id="dataTable">
        <thead>
        <tr>
            <td class="table-column-header" data-col="stt" data-show="true">STT</th>
            <td class="table-column-header" data-col="color" data-show="true">Màu</th>
            <td class="table-column-header" data-col="size" data-show="true">Size</th>
            <td class="table-column-header" data-col="quantity" data-show="true">Số lượng</th>
            <td class="table-column-header" data-col="cost" data-show="true">Giá nhập</th>
            <td class="table-column-header" data-col="price" data-show="true">Giá bán</th>
            <td class="table-column-header" data-col="discount" data-show="true">Giảm giá (%)</th>
            <td class="table-column-header" data-col="status" data-show="true">Trạng thái</th>
            <td>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${ _product.details }">
            <c:forEach var="pd" items="${ item.value }" varStatus="_status">
                <tr>
                    <td data-col="stt">${ _status.index + 1 }</td>
                    <c:if test="${ _status.index eq 0 }">
                        <td data-col="color" rowspan="${ item.value.size() }">
                            <h5>${ item.key.color }</h5>
                            <img width="100%" height="100px" src="${ item.key.imageUrl }" alt="${ item.key.color }" style="max-width: 135px;" class="scalable"/>
                        </td>
                    </c:if>
                    <td data-col="size">${ pd.size }</td>
                    <td data-col="quantity">${ pd.quantity }</td>
                    <td data-col="cost">
                        <jsp:include page="../../../../common/currency.jsp">
                            <jsp:param name="_value" value="${ pd.cost }"/>
                        </jsp:include>
                    </td>
                    <td data-col="price">
                        <jsp:include page="../../../../common/currency.jsp">
                            <jsp:param name="_value" value="${ pd.price }"/>
                        </jsp:include>
                    </td>
                    <td data-col="discount">${ pd.discount }</td>
                    <td data-col="status">
                        <span class="${ pd.active ? 'text-success' : 'text-danger'}">${ pd.active ? 'Hoạt động' : 'Ngưng hoạt động' }</span>
                    </td>
                    <td>
                        <c:if test="${ pd.active }">
                            <a href="${contextPath}/admin/product/detail/lock?id=${ pd.id }&pCode=${ param.code }" title="Khóa sản phẩm" class="btn btn-danger text-white">
                                <i class="fa fa-lock"></i>
                            </a>
                        </c:if>

                        <c:if test="${ not pd.active }">
                            <a href="${ contextPath }/admin/product/detail/lock?id=${ pd.id }&pCode=${ param.code }" title="Mở khóa sản phẩm" class="btn btn-success text-white">
                                <i class="fa fa-unlock"></i>
                            </a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </c:forEach>
        </tbody>
    </table>
</div>