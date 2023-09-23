<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="hero.jsp" />
<jsp:include page="banner.jsp" />
<jsp:include page="product.jsp">
    <jsp:param name="label" value="Best Sellers" />
</jsp:include>
<jsp:include page="product.jsp">
    <jsp:param name="label" value="New Arrivals" />
</jsp:include>
<jsp:include page="product.jsp">
    <jsp:param name="label" value="Hot Sales" />
</jsp:include>
<jsp:include page="category.jsp" />