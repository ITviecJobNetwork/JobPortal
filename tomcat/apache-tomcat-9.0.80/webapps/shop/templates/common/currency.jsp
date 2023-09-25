<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:formatNumber
    value="${ param._value }"
    type="currency"
    currencyCode="VND"
    currencySymbol="đ"
    maxFractionDigits="0"
/>