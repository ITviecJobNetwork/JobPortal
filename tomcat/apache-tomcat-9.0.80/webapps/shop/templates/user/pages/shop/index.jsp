<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../../common/breadcrumb.jsp">
    <jsp:param name="bc1" value="shop"/>
</jsp:include>

<!-- Shop Section Begin -->
<section class="shop spad pt-5">
    <div class="container">
        <div class="row">
            <div class="col-lg-3">
                <jsp:include page="shop-sidebar.jsp" />
            </div>
            <div class="col-lg-9">
                <div class="row">
                    <c:forEach var="item" items="${ paging.items }">
                        <c:set var="product" value="${ item }" scope="request" />
                        <jsp:include page="product-card.jsp">
                            <jsp:param name="lg" value="4"/>
                            <jsp:param name="md" value="6"/>
                            <jsp:param name="sm" value="6"/>
                        </jsp:include>
                    </c:forEach>
                </div>
                <div class="row">
                    <jsp:include page="../../common/paging.jsp" />
                </div>
            </div>
        </div>
    </div>
</section>
<!-- Shop Section End -->