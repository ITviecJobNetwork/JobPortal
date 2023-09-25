<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="d-none bg-white pl-0 rounded border">
    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-search mr-2" aria-hidden="true"></i>
            <a href="javascript:void(0)" class="show-detail-order" data-orderId="${ _item.code }" data-urlData="${ contextPath }/rest/order">Chi tiết</a>
        </div>
    </li>

    <c:if test="${ _item.status eq 'PENDING' }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-times mr-2" aria-hidden="true"></i>
                <a href="javascript:void(0)" data-toggle="modal" data-target="#order-${ _item.code }">Hủy bỏ</a>
            </div>
        </li>
    </c:if>
</ul>