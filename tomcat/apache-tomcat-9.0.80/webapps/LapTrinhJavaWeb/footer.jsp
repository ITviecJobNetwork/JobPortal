<%--
  Created by IntelliJ IDEA.
  User: Vo Phuong Nam
  Date: 10/7/2023
  Time: 4:28 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<style>
  .footer{
    margin: 2rem;
  }
</style>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.GregorianCalendar, java.util.Calendar" %>
<%
  GregorianCalendar currentDate = new GregorianCalendar();
  int currentYear = currentDate.get(Calendar.YEAR);
%>
<body>
<p class="footer">&copy; Copyright <%= currentYear %> Mike Murach &amp; Associates</p>
</body>
</html>

