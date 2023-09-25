<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="../../common/breadcrumb.jsp">
    <jsp:param name="bc1" value="Order"/>
</jsp:include>
<!-- Shopping Cart Section Begin -->
<section class="shopping-cart spad pt-5">
    <div class="container">
        <div class="row mb-4">
            <form method="get" class="w-100">
                <div class="row">
                    <div class="col-md-3 d-flex align-items-center">
                        <label class="col-md-3">Mã</label>
                        <input class="form-control" name="code" value="${ param.code }"/>
                    </div>
                    <div class="col-md-3 d-flex align-items-center">
                        <label class="col-md-3">Từ ngày</label>
                        <input type="date" class="form-control" name="fromDate" value="${ param.fromDate }"/>
                    </div>
                    <div class="col-md-3 d-flex align-items-center">
                        <label class="col-md-4">Đến ngày</label>
                        <input type="date" class="form-control" name="toDate" value="${ param.toDate }"/>
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-primary">Tìm kiếm</button>
                    </div>
                </div>
            </form>
        </div>
        <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Mã hóa đơn</th>
                        <th>Số điện thoại</th>
                        <th>Địa chỉ</th>
                        <th>Ngày mua</th>
                        <th>Tổng tiền</th>
                        <th>Thông tin</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${ paging.items }">
                        <tr>
                            <td>
                                <div style="max-width: 200px">${ item.code }</div>
                            </td>
                            <td>${ item.phone }</td>
                            <td>
                                <div style="max-width: 150px">
                                    ${ item.address }
                                </div>
                            </td>
                            <td>
                                <fmt:formatDate value="${ item.createdDate }" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                            <td>
                                <jsp:include page="../../../common/currency.jsp">
                                    <jsp:param name="_value" value="${ item.total }"/>
                                </jsp:include>
                            </td>
                            <td>
                                <div class="d-flex flex-column" style="gap: 5px">
                                    <div class="d-flex" style="gap: 5px">
                                        <b>Hình thức:</b>
                                        <jsp:include page="../../../common/method-payment-icon.jsp">
                                            <jsp:param name="_value" value="${ item.methodPayment }"/>
                                        </jsp:include>
                                    </div>
                                    <div class="d-flex" style="gap: 5px">
                                        <b>Trạng thái:</b>
                                        <jsp:include page="../../../common/status-order.jsp">
                                            <jsp:param name="_value" value="${ item.status }"/>
                                            <jsp:param name="_label" value="${ item.status.value }" />
                                        </jsp:include>
                                    </div>
                                    <c:if test="${ item.adminNote ne null }">
                                        <div class="d-flex" style="gap: 5px">
                                            <b>Admin hủy:</b>
                                            <span class="dots" style="max-width: 100px;" title="${ item.adminNote }">${ item.adminNote }</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${ item.userNote ne null }">
                                        <div class="d-flex" style="gap: 5px">
                                            <b>User hủy:</b>
                                            <span class="dots" style="max-width: 100px" title="${ item.userNote }">${ item.userNote }</span>
                                        </div>
                                    </c:if>
                                </div>
                            </td>
                            <td class="user-action">
                                <div>
                                    <i class="fa fa-ellipsis-v cursor-pointer" aria-hidden="true"></i>
                                    <c:set var="_item" value="${ item }" scope="request"/>
                                    <jsp:include page="table-action.jsp"/>
                                </div>
                            </td>

                        </tr>

                        <c:if test="${  item.status eq 'PENDING' }">
                            <jsp:include page="../../../common/modal-cancel.jsp">
                                <jsp:param name="id" value="order-${ item.code }"/>
                                <jsp:param name="_title" value="Hủy đơn hàng"/>
                                <jsp:param name="_methodSubmit" value="post"/>
                                <jsp:param name="_urlSubmit" value="${ contextPath }/order/cancel?oCode=${ item.code }"/>
                            </jsp:include>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        <jsp:include page="../../common/paging.jsp" />
    </div>
</section>
<!-- Shopping Cart Section End -->

<jsp:include page="../../../common/modal.jsp">
    <jsp:param name="id" value="order-detail"/>
    <jsp:param name="_title" value="Chi tiết đơn hàng" />
    <jsp:param name="bodyComponent" value="/admin/pages/order/order-detail.jsp"/>
    <jsp:param name="_sz" value="modal-xl"/>
</jsp:include>