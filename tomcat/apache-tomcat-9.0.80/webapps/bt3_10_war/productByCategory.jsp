<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Products by Category</title>
</head>
<body>
<h2>Products by Category</h2>
<c:forEach var="product" items="${products}">
    <p>${product.productName} - ${product.description} - ${product.price}</p>
</c:forEach>
</body>
</html>
