<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Vo Phuong Nam
  Date: 9/15/2023
  Time: 9:43 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thank you</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap/dist/js/bootstrap.min.js"></script>
    <style>
        body{
            margin: 2rem;
            padding:2rem;
        }
    </style>
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
    <li><p><b>Date of birth:</b>
        <%= request.getParameter("dateOfBirth")%>
    </p></li>
    <li><p><b>HeardFrom:</b>
        <%= request.getParameter("heardFrom")%>
    </p></li>
    <li><p><b>Do you like this club?</b>
        <%= request.getParameter("announce")%>
    </p></li>
    <li><p><b>Contact via:</b>
        <%= request.getParameter("contactMethod")%>
    </p></li>
</ul>
<p>To enter another email address, click on back button in your browser or the return button show below </p>
<form action="4.1+6.2.html" method="post" id="myForm">
    <input type="hidden" name="action" value="join">
    <input type="submit" value="Return" class="btn btn-primary">
    <input type="submit" value="Home" class="btn btn-success" onclick="goToHomePage()">
</form>
<script>
    function goToHomePage(){
        document.getElementById("myForm").action="index.html"
        document.getElementById("myForm").submit();

    }
</script>



</body>
</html>
