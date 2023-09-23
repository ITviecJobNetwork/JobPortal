<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="sessionUser" value="${ sessionScope.SESSION_USER }" scope="page" />
<c:if test="${ sessionUser ne null }">
    <form method="post" action="${ pageContext.request.contextPath }/comment/add" class="w-100 bg-white mb-0">
        <input type="hidden" name="productId" value="${ product.id }" />
        <div class="d-flex flex-start w-100">
            <img class="rounded-circle shadow-1-strong mr-3"
                 src="${ pageContext.request.contextPath }/static/img/undraw_profile.svg" alt="avatar"
                 width="40"
                 height="40"/>
            <div class="form-outline w-100 position-relative">
                <textarea class="form-control" id="comment-product" rows="4" name="content" required>${ _data.content }</textarea>
                <span class="position-absolute" style="bottom: 10px; right: 10px;"><span id="comment-length">0</span>/200</span>
            </div>
        </div>
        <div class="d-flex justify-content-end mt-2 pt-1">
            <button class="btn btn-primary btn-sm">Bình luận</button>
        </div>
    </form>
</c:if>

<c:if test="${ sessionUser eq null }">
    <a class="w-100 text-center" href="${ pageContext.request.contextPath }/auth">Đăng nhập để bình luận sản phẩm</a>
</c:if>