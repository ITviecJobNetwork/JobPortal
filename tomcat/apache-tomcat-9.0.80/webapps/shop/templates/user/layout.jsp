<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <c:set scope="application" var="contextPath" value="${pageContext.request.contextPath}"  />
    <c:set scope="application" var="contextPathStatic" value="${contextPath}/static" />
    <c:set scope="application" var="contextPathResource" value="${contextPathStatic}/user" />

    <meta charset="UTF-8">
    <meta name="description" content="Male_Fashion Template">
    <meta name="keywords" content="Male_Fashion, unica, creative, html">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${ title }</title>

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Css Styles -->
    <link rel="stylesheet" href="${contextPathStatic}/common/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/font-awesome.min.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/elegant-icons.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/magnific-popup.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/nice-select.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/owl.carousel.min.css" type="text/css">
    <link rel="stylesheet" href="${contextPathStatic}/common/css/slicknav.min.css" type="text/css">
    <link rel="stylesheet" href="${contextPathResource}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${contextPathResource}/css/header.css" type="text/css">
    <link rel="stylesheet" href="${contextPathResource}/css/footer.css" type="text/css">
    <c:forEach var="css" items="${_css}">
        <link rel="stylesheet" href="${contextPathResource}/css/${css}.css" type="text/css">
    </c:forEach>
</head>

<body>
    <!-- Page Preloder -->
    <div id="preloder">
        <div class="loader"></div>
    </div>
    <jsp:include page="common/header.jsp" />
    <jsp:include page="../../${pageContent}" />
    <jsp:include page="common/footer.jsp" />
    <!-- Search Begin -->
    <div class="search-model">
        <div class="h-100 d-flex align-items-center justify-content-center">
            <div class="search-close-switch">+</div>
            <form class="search-model-form">
                <input type="text" id="search-input" placeholder="Search here.....">
            </form>
        </div>
    </div>
    <!-- Search End -->
    <script src="${contextPathStatic}/common/js/jquery-3.3.1.min.js"></script>
    <script src="${contextPathStatic}/common/js/bootstrap.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.nice-select.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.nicescroll.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.magnific-popup.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.countdown.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.slicknav.js"></script>
    <script src="${contextPathStatic}/common/js/mixitup.min.js"></script>
    <script src="${contextPathStatic}/common/js/owl.carousel.min.js"></script>
    <script src="${contextPathResource}/js/main.js"></script>
</body>
</html>
