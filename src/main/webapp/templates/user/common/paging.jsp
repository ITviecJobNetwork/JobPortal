<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:url scope="request" var="url" value="${ applicationScope.currentUri }" context="/">
    <c:param name="_x_" value="" />
    <c:forEach var="p" items="${ _parameter }">
        <c:if test="${ p.key ne 'page' and p.key ne '_x_' }">
            <c:param name="${p.key}" value="${ fn:join(p.value, ',') }" />
        </c:if>
    </c:forEach>
</c:url>

<div class="w-100">
    <div class="product__pagination">
        <c:forEach var="page" items="${ requestScope.paging.pages }">
            <c:if test="${ '' += page eq '...' }">
                <span>...</span>
            </c:if>
            <c:if test="${ '' += page ne '...' }">
                <a class="${ page eq requestScope.paging.page ? 'active' : '' }" href="${url}&page=${ page }">${ page }</a>
            </c:if>
        </c:forEach>
    </div>
</div>