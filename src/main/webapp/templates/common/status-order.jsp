<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <c:if test="${ param._value eq 'PENDING' or param._value eq 'APPROVED'}">
        <c:set var="classStatus" value="text-primary" scope="page"/>
    </c:if>
    <c:if test="${ param._value eq 'CANCEL' or param._value eq 'REJECT' }">
        <c:set var="classStatus" value="text-danger" scope="page"/>
    </c:if>
    <c:if test="${ param._value eq 'DELIVERING' }">
        <c:set var="classStatus" value="text-warning" scope="page"/>
    </c:if>
    <c:if test="${ param._value eq 'DONE' }">
        <c:set var="classStatus" value="text-success" scope="page"/>
    </c:if>
    <strong class="${ classStatus }">${ param._label }</strong>
</div>