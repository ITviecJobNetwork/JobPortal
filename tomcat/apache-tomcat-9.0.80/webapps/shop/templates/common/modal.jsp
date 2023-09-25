<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="${ param.id }" tabindex="-1" role="dialog" style="z-index: 10000">
    <div class="modal-dialog ${ param._sz }" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">${ param._title }</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">
                <c:if test="${ param.bodyComponent ne null}">
                    <jsp:include page="..${ param.bodyComponent }" />
                </c:if>
                <c:if test="${ param.content ne null }">
                    ${ param.content }
                </c:if>
            </div>
            <c:if test="${ param.successUrl ne null }">
                <div class="modal-footer">
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy bỏ</button>
                    <a class="btn btn-primary" href="${ param.successUrl }">Đồng ý</a>
                </div>
            </c:if>
        </div>
    </div>
</div>
