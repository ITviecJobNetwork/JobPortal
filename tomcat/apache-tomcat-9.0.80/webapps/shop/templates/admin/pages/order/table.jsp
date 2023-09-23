<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="table-responsive">
    <table class="table table-bordered w-100" id="dataTable">
        <thead>
        <tr>
            <th class="table-column-header" data-col="stt" data-show="true">STT</th>
            <th class="table-column-header" data-col="code" data-show="true">Mã hóa đơn</th>
            <th class="table-column-header" data-col="createdBy" data-show="true">Người mua</th>
            <th class="table-column-header" data-col="phone" data-show="true">Số điện thoại</th>
            <th class="table-column-header" data-col="address" data-show="true">Địa chỉ</th>
            <th class="table-column-header" data-col="createdDate" data-show="true">Ngày mua</th>
            <th class="table-column-header" data-col="method" data-show="true">Hình thức TT</th>
            <th class="table-column-header" data-col="userNote" data-show="false">User lý do</th>
            <th class="table-column-header" data-col="adminNote" data-show="false">Admin lý do</th>
            <th class="table-column-header" data-col="status" data-show="true">Trạng thái</th>
            <th class="table-column-header" data-col="total" data-show="true">Tổng tiền</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${ paging.items }" varStatus="_status">
            <tr>
                <td data-col="stt">${ _status.index + 1 + ( (paging.page - 1) * paging.pageSize )}</td>
                <td data-col="code">${ item.code }</td>
                <td data-col="createdBy">${ item.createdBy }</td>
                <td data-col="phone">${ item.phone }</td>
                <td data-col="address">${ item.address }</td>
                <td data-col="createdDate">
                    <fmt:formatDate value="${ item.createdDate }" pattern="dd/MM/YYYY HH:mm"/>
                </td>
                <td data-col="method">
                    <h6>
                        <c:if test="${ item.methodPayment eq 'PAYPAL'}">
                            <i class="fa fa-paypal text-primary mr-2" aria-hidden="true"></i>
                            PayPal
                        </c:if>

                        <c:if test="${ item.methodPayment eq 'COD'}">
                            <i class="fa fa-money text-success mr-2" aria-hidden="true"></i>
                            COD
                        </c:if>
                    </h6>
                </td>
                <td data-col="userNote">${ item.userNote }</td>
                <td data-col="adminNote">${ item.adminNote }</td>
                <td data-col="status">
                    <c:if test="${ item.status eq 'PENDING' or item.status eq 'APPROVED'}">
                        <c:set var="classStatus" value="text-primary" scope="page"/>
                    </c:if>
                    <c:if test="${ item.status eq 'CANCEL' or item.status eq 'REJECT' }">
                        <c:set var="classStatus" value="text-danger" scope="page"/>
                    </c:if>
                    <c:if test="${ item.status eq 'DELIVERING' }">
                        <c:set var="classStatus" value="text-warning" scope="page"/>
                    </c:if>
                    <c:if test="${ item.status eq 'DONE' }">
                        <c:set var="classStatus" value="text-success" scope="page"/>
                    </c:if>
                    <strong class="${ classStatus }">${ item.status.value }</strong>
                </td>
                <td data-col="total">
                    <jsp:include page="../../../common/currency.jsp">
                      <jsp:param name="_value" value="${ item.total }"/>
                    </jsp:include>
                </td>
                <td class="user-action">
                    <div>
                        <i class="fa fa-ellipsis-v cursor-pointer" aria-hidden="true"></i>
                        <c:set var="_item" value="${ item }" scope="request"/>
                        <jsp:include page="table-action.jsp"/>
                    </div>
                </td>
            </tr>

            <jsp:include page="../../common/modal.jsp">
                <jsp:param name="id" value="reject-user-${ _item.code }"/>
                <jsp:param name="_title" value="Từ chối đơn hàng-${ _item.code }"/>
                <jsp:param name="bodyComponent" value="order/reject-form.jsp"/>
            </jsp:include>
        </c:forEach>
        </tbody>
    </table>
</div>