<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="d-none bg-white pl-0 rounded border">
    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-search mr-2" aria-hidden="true"></i>
            <a href="${ contextPath }/admin/product/detail?code=${ _item.code }">Chi tiết</a>
        </div>
    </li>

    <li class="border-bottom">
        <div class="p-2 d-flex align-items-center">
            <i class="fa fa-pencil mr-2" aria-hidden="true"></i>
            <a href="${ contextPath }/admin/product/update?pCode=${ _item.code }">Chỉnh sửa</a>
        </div>
    </li>

    <c:if test="${ requestScope._item.active }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-lock mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/product/lock?code=${ _item.code }" title="Khóa sản phẩm">Khóa sản phẩm</a>
            </div>
        </li>
    </c:if>

    <c:if test="${ not requestScope._item.active }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-unlock-alt mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/product/lock?code=${ _item.code }" title="Mở khóa sản phẩm">Mở khóa</a>
            </div>
        </li>
    </c:if>
    <c:if test="${ requestScope._item.active }">
        <li class="border-bottom">
            <div class="p-2 d-flex align-items-center">
                <i class="fa fa-unlock-alt mr-2" aria-hidden="true"></i>
                <a href="${ contextPath }/admin/product/delete?code=${ _item.code }" title="Xóa sản phẩm">Xóa Sản Phẩm</a>
            </div>
        </li>
    </c:if>
</ul>