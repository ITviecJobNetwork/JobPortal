<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${ requestScope.result ne null }">
    <span id="${ result.success ? '_successMessage' : '_errorMessage'}" class="d-none">
        <c:out value="${ result.message }" escapeXml="true" />
    </span>
</c:if>