<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:choose>
    <c:when test="${ sessionScope.SESSION_USER eq null }">
        <a href="${ contextPath }/auth">Đăng nhập</a>
    </c:when>
    <c:otherwise>
        <div class="user-info position-relative">
            <a href="javascript:void(0)">Xin chào, ${ sessionScope.SESSION_USER.fullName }</a>
            <ul class="d-none bg-white position-absolute border" style="list-style: none">
                <li class="border-bottom">
                    <p data-toggle="modal" data-target="#${ updateUserId }">Cập nhật thông tin</p>
                </li>
                <li class="border-bottom">
                    <p data-toggle="modal" data-target="#${ changePasswordId }">Đổi mật khẩu</p>
                </li>
                <li class="border-bottom">
                    <p data-toggle="modal" data-target="#${ logoutId }">Đăng xuất</p>
                </li>
            </ul>
        </div>

        <c:if test="${ sessionScope.SESSION_USER.role eq 'ADMIN' }">
            <a href="${ contextPath }/admin/home">Vào trang admin</a>
        </c:if>
    </c:otherwise>
</c:choose>