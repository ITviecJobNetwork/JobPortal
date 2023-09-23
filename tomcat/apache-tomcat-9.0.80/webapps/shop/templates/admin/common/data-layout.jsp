<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isSearch" value="${ param.searchComponent ne null }" />
<div class="d-flex justify-content-between">
    <h1 class="h3 mb-2 text-gray-800">${ param.title }</h1>
    <c:if test="${ isSearch }">
        <a data-toggle="collapse" href="#advancedSearch" role="button">
            Tìm kiếm nâng cao
        </a>
    </c:if>
</div>

<c:if test="${ isSearch }">
    <div class="collapse" id="advancedSearch">
        <jsp:include page="../pages/${ param.searchComponent }"/>
    </div>
</c:if>

<div class="card shadow my-4">
    <div class="card-header d-flex justify-content-between py-3">
        <h6 class="m-0 font-weight-bold text-primary">${ param.subTitle }</h6>
        <c:if test="${ param.actionComponent ne null }">
            <jsp:include page="../pages/${ param.actionComponent }" />
        </c:if>
    </div>
    <div class="card-body">
        <jsp:include page="../pages/${ param.tableComponent }" />
        <c:if test="${ param.isPagination }">
            <jsp:include page="paging.jsp" />
        </c:if>
    </div>
</div>