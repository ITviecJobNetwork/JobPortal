<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Product List</title>
</head>
<body>
<h2>Product List</h2>
<c:forEach var="product" items="${products}">
    <p>${product.productName} - ${product.description} - ${product.price}</p>
</c:forEach>
</body>
</html>
