<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="d-none bg-white pl-0 rounded border">
    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-money mr-2" aria-hidden="true"></i>
            <a href="${ contextPath }/admin/order?createdBy=${ requestScope._item.email }">Đơn hàng</a>
        </div>
    </li>

    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-pencil mr-2" aria-hidden="true"></i>
            <a href="#" data-toggle="modal" data-target="#update-user-${ requestScope._item.id}">Chỉnh sửa</a>
        </div>
    </li>

    <c:if test="${ requestScope._item.active }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-lock mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/user/lock?email=${ requestScope._item.email }">Khóa</a>
            </div>
        </li>
    </c:if>

    <c:if test="${ !requestScope._item.active }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-unlock-alt mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/user/lock?email=${ requestScope._item.email }">Mở khóa</a>
            </div>
        </li>
    </c:if>
</ul>