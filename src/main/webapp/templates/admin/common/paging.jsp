<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url scope="request" var="url" value="${ applicationScope.currentUri }" context="/">
    <c:param name="_x_" value="" />
    <c:forEach var="p" items="${ _parameter }">
        <c:if test="${ p.key ne 'page' and p.key ne '_x_' }">
            <c:param name="${p.key}" value="${ fn:join(p.value, ',') }" />
        </c:if>
    </c:forEach>
</c:url>
<nav class="d-flex justify-content-start justify-content-md-end mt-2">
    <ul class="pagination">
        <c:if test="${ requestScope.paging.page gt 1 }">
            <li class="page-item">
                <a class="page-link" href="${url}&page=${ requestScope.paging.page - 1 }" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                    <span class="sr-only">Previous</span>
                </a>
            </li>
        </c:if>
        <c:forEach var="page" items="${ requestScope.paging.pages }">
            <c:if test="${ '' += page eq '...' }">
                <li class="page-item">
                    <a class="page-link">${ page }</a>
                </li>
            </c:if>
            <c:if test="${ '' += page ne '...' }">
                <li class="page-item ${ page eq requestScope.paging.page ? 'active' : '' }">
                    <a class="page-link" href="${url}&page=${ page }">${ page }</a>
                </li>
            </c:if>
        </c:forEach>
        <c:if test="${ requestScope.paging.page lt requestScope.paging.totalPage }">
            <li class="page-item">
                <a class="page-link" href="${url}&page=${ requestScope.paging.page + 1 }" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                    <span class="sr-only">Next</span>
                </a>
            </li>
        </c:if>
    </ul>
</nav>