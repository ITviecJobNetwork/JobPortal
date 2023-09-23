<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <c:set scope="application" var="contextPath" value="${pageContext.request.contextPath}"  />
    <c:set scope="application" var="contextPathStatic" value="${contextPath}/static" />
    <c:set scope="application" var="contextPathResource" value="${contextPathStatic}/user" />
    <c:set scope="application" var="currentUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
    <c:set scope="application" var="logoutId" value="logout-user" />
    <c:set scope="application" var="updateUserId" value="update-user" />
    <c:set scope="application" var="changePasswordId" value="change-password-user" />

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
        <link rel="stylesheet" href="${contextPathStatic}${css}.css" type="text/css">
    </c:forEach>
    <style>
        .required:after {
            content: ' *';
            color: var(--danger);
        }
    </style>
</head>

<body>
    <jsp:include page="../admin/common/spinner.jsp" />
    <div style="height: fit-content">
        <jsp:include page="../common/notifier.jsp" />
        <!-- Page Preloder -->
        <div id="preloder">
            <div class="loader"></div>
        </div>
        <jsp:include page="common/header.jsp" />
        <jsp:include page="../../${pageContent}" />
        <jsp:include page="common/footer.jsp" />
    </div>

    <jsp:include page="../common/modal.jsp">
        <jsp:param name="_title" value="Cập nhật thông tin"/>
        <jsp:param name="id" value="${ updateUserId }"/>
        <jsp:param name="bodyComponent" value="/user/common/user-info-form.jsp"/>
    </jsp:include>

    <jsp:include page="../common/modal.jsp">
        <jsp:param name="_title" value="Đổi mật khẩu"/>
        <jsp:param name="id" value="${ changePasswordId }"/>
        <jsp:param name="bodyComponent" value="/user/common/change-password-form.jsp"/>
    </jsp:include>

    <jsp:include page="../common/modal.jsp">
        <jsp:param name="id" value="${ logoutId }"/>
        <jsp:param name="_title" value="Xác nhận?"/>
        <jsp:param name="content" value="Bạn có muốn đăng xuất tài khoản."/>
        <jsp:param name="successUrl" value="${ contextPath }/auth/logout"/>
    </jsp:include>

    <script src="${contextPathStatic}/common/js/jquery-3.3.1.min.js"></script>
    <script src="${contextPathStatic}/common/js/bootstrap.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.nice-select.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.nicescroll.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.magnific-popup.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.countdown.min.js"></script>
    <script src="${contextPathStatic}/common/js/jquery.slicknav.js"></script>
    <script src="${contextPathStatic}/common/js/mixitup.min.js"></script>
    <script src="${contextPathStatic}/common/js/owl.carousel.min.js"></script>
    <script src="${contextPathStatic}/common/js/sweetalert2.js"></script>
    <script src="${contextPathStatic}/common/js/notifier.js"></script>
    <script src="${contextPathStatic}/common/js/utilities.js"></script>
    <script src="${contextPathStatic}/admin/js/spinner.js"></script>
    <script src="${contextPathResource}/js/main.js"></script>
    <c:forEach var="js" items="${ _js }">
        <script src="${contextPathStatic}${js}.js"></script>
    </c:forEach>
</body>
</html>
