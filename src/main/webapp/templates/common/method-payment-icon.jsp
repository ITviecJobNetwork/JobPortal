<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<h6>
  <c:if test="${ param._value eq 'PAYPAL'}">
    <i class="fa fa-paypal text-primary mr-2" aria-hidden="true"></i>
    PayPal
  </c:if>

  <c:if test="${ param._value eq 'COD'}">
    <i class="fa fa-money text-success mr-2" aria-hidden="true"></i>
    COD
  </c:if>
</h6>