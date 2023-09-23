<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Vo Phuong Nam
  Date: 9/15/2023
  Time: 9:43 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Thank you for joining</title>
</head>
<body>
	<h1>Thank you for joining Email List </h1>	
	<p>Here is the information you entered: </p>

	<ul>

         <li><p><b>First Name:</b>
            <%= request.getParameter("firstName")%>
         </p></li>
         <li><p><b>Last  Name:</b>
            <%= request.getParameter("lastName")%>
         </p></li>
         <li><p><b>Email:</b>
            <%= request.getParameter("email")%>
         </p></li>
         
      </ul>
	<p>To enter another email address, click on back button in your browser or the return button show below </p>	
<form action="index.html" method="get">
    <input type="hidden" name="action" value="join">
    <input type="submit" value="Return">
</form>
</body>
</html>
