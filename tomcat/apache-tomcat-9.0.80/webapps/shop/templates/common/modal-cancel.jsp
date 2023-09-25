<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="modal.jsp">
  <jsp:param name="id" value="${ param.id }"/>
  <jsp:param name="_title" value="${ param._title }"/>
  <jsp:param name="bodyComponent" value="/common/form-cancel.jsp"/>
</jsp:include>