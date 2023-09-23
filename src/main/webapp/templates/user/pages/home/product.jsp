<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="items" value="${ requestScope.get( param._nameData ) }" scope="page" />
<c:if test="${ not empty items }">
    <!-- Product Section Begin -->
    <section class="product spad w-100">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <ul class="filter__controls text-left mb-3">
                        <li class="active" style="color: #111111;">${ param.label }</li>
                    </ul>
                </div>
            </div>
            <div class="row product__filter">
                <c:forEach var="product" items="${ items }">
                    <c:set var="product" value="${ product }" scope="request" />
                    <jsp:include page="../shop/product-card.jsp">
                        <jsp:param name="lg" value="3"/>
                        <jsp:param name="md" value="6"/>
                        <jsp:param name="sm" value="6"/>
                    </jsp:include>
                </c:forEach>
            </div>
        </div>
    </section>
    <!-- Product Section End -->
</c:if>