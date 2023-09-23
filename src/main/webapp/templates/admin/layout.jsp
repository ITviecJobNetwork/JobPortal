<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <c:set scope="application" var="contextPath" value="${pageContext.request.contextPath}"  />
    <c:set scope="application" var="currentUri" value="${requestScope['javax.servlet.forward.request_uri']}" />
    <c:set scope="application" var="logoutModalId" value="logoutModal" />
    <c:set scope="application" var="changePasswordId" value="change-password-user" />

    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>${ title }</title>

    <!-- Custom fonts for this template-->
    <link href="${contextPath}/static/common/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="${contextPath}/static/sb-admin-2.min.css" rel="stylesheet">
    <c:forEach var="css" items="${ _css }">
        <link href="${contextPath}/static/${ css }.css" rel="stylesheet">
    </c:forEach>
    <style>
        .required:after {
            content: ' *';
            color: red;
        }

        .cursor-pointer {
            cursor: pointer !important;
        }

        .ck-editor__editable_inline {
            min-height: 400px;
        }

        .preview-image {
            width: 50px;
            height: 50px;
        }

        .scalable {
            transition: transform .5s;
            transform-origin: left;
        }

        .scalable:hover {
            transform: scale(2);
        }
    </style>
</head>
<jsp:include page="../common/notifier.jsp" />
<c:if test="${ requestScope._openModal ne null }">
    <span id="_openModal" data-val="#${ requestScope._openModal }"></span>
</c:if>
<body id="page-top">
    <jsp:include page="common/spinner.jsp" />
    <!-- Page Wrapper -->
    <div id="wrapper">
        <jsp:include page="common/sidebar.jsp" />
        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">
            <!-- Main Content -->
            <div id="content">
                <jsp:include page="common/topbar.jsp" />
                <!-- Begin Page Content -->
                <div class="container-fluid">
                    <jsp:include page="../../${pageContent}" />
                </div>
                <!-- /.container-fluid -->
            </div>
            <!-- End of Main Content -->
        </div>
        <!-- End of Content Wrapper -->

    </div>
    <!-- End of Page Wrapper -->

    <!-- Scroll to Top Button-->
    <a class="scroll-to-top rounded d-flex justify-content-center align-items-center" href="#page-top">
        <i class="fa fa-arrow-up"></i>
    </a>

    <!-- Logout Modal-->
    <jsp:include page="../common/modal.jsp">
        <jsp:param name="id" value="${ logoutModalId }"/>
        <jsp:param name="_title" value="Ready to Leave?"/>
        <jsp:param name="content" value="Select 'Logout' below if you are ready to end your current session."/>
        <jsp:param name="successUrl" value="${ contextPath }/auth/logout"/>
    </jsp:include>

    <jsp:include page="../common/modal.jsp">
        <jsp:param name="_title" value="Đổi mật khẩu"/>
        <jsp:param name="id" value="${ changePasswordId }"/>
        <jsp:param name="bodyComponent" value="/user/common/change-password-form.jsp"/>
    </jsp:include>

    <!-- Bootstrap core JavaScript-->
    <script src="${contextPath}/static/common/js/jquery-3.3.1.min.js"></script>
    <script src="${contextPath}/static/common/js/bootstrap.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="${contextPath}/static/admin/js/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="${contextPath}/static/admin/js/admin.min.js"></script>
    <script src="${contextPath}/static/common/js/sweetalert2.js"></script>
    <script src="${contextPath}/static/admin/js/spinner.js"></script>
    <script src="${contextPath}/static/common/js/notifier.js" ></script>
    <script src="${contextPath}/static/common/js/utilities.js" ></script>
    <c:forEach var="js" items="${_js}">
        <script src="${contextPath}/static/${js}.js" type="text/javascript" charset="UTF-8"></script>
    </c:forEach>
</body>
</html>
