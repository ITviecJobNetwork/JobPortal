<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="hero.jsp" />
<jsp:include page="banner.jsp" />
<jsp:include page="product.jsp">
    <jsp:param name="label" value="Bán Chạy Nhất" />
    <jsp:param name="_nameData" value="bestSeller"/>
</jsp:include>
<jsp:include page="product.jsp">
    <jsp:param name="label" value="Sản Phẩm Mới" />
    <jsp:param name="_nameData" value="newest"/>
</jsp:include>
<jsp:include page="product.jsp">
    <jsp:param name="label" value="Sốc Sales" />
    <jsp:param name="_nameData" value="hotSales"/>
</jsp:include>
<%--<jsp:include page="category.jsp" />--%>