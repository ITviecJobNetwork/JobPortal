<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="d-none bg-white pl-0 rounded border">
    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-search mr-2" aria-hidden="true"></i>
            <a href="#" class="show-detail-order" data-orderId="${ _item.code }" data-urlData="${ contextPath }/rest/order">Chi tiết</a>
        </div>
    </li>

    <c:if test="${ _item.status eq 'PENDING' }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-times mr-2" aria-hidden="true"></i>
                <a href="#" data-toggle="modal" data-target="#reject-user-${ _item.code }">Từ chối</a>
            </div>
        </li>
    </c:if>

    <c:if test="${ _item.status eq 'PENDING' }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-check mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/order/change-status?status=APPROVED&oCode=${ _item.code }">Phê duyệt</a>
            </div>
        </li>
    </c:if>

    <c:if test="${ _item.status eq 'APPROVED' }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-truck mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/order/change-status?status=DELIVERING&oCode=${ _item.code }">Vận chuyển</a>
            </div>
        </li>
    </c:if>

    <c:if test="${ _item.status eq 'DELIVERING' }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-money mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/order/change-status?status=DONE&oCode=${ _item.code }">Hoàn thành</a>
            </div>
        </li>
    </c:if>
</ul>