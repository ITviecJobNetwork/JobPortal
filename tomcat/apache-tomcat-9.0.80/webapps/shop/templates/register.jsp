<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Fashion Shop - Register</title>

    <!-- Custom fonts for this template-->
    <link href="${pageContext.request.contextPath}/static/common/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;600;700;800;900&display=swap" rel="stylesheet">
    <!-- Custom styles for this template-->
    <link href="${pageContext.request.contextPath}/static/sb-admin-2.min.css" rel="stylesheet">

</head>

<body>
    <jsp:include page="./common/notifier.jsp" />
    <div class="container">

        <div class="card o-hidden border-0 shadow-lg my-5">
            <div class="card-body p-0">
                <!-- Nested Row within Card Body -->
                <div class="row">
                    <div class="col-lg-5 d-none d-lg-block bg-register-image"></div>
                    <div class="col-lg-7">
                        <div class="p-5">
                            <div class="text-center">
                                <h1 class="h4 text-gray-900 mb-4">Tạo tài khoản</h1>
                            </div>
                            <form class="user" method="post">
                                <div class="form-group row">
                                    <div class="col-sm-6 mb-3 mb-sm-0">
                                        <input class="form-control form-control-user" placeholder="Họ" name="lastName" value="${ _data.lastName }" required>
                                    </div>
                                    <div class="col-sm-6">
                                        <input class="form-control form-control-user" placeholder="Tên" name="firstName" value="${ _data.firstName }" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <input type="email" class="form-control form-control-user" placeholder="Địa chỉ email" name="email" value="${ _data.email }" required>
                                </div>
                                <div class="form-group row">
                                    <div class="col-sm-6 mb-3 mb-sm-0">
                                        <input type="password" class="form-control form-control-user" placeholder="Password" name="password" value="${ _data.password }" required>
                                    </div>
                                    <div class="col-sm-6">
                                        <input type="password" class="form-control form-control-user" placeholder="Repeat Password" name="repeatPassword" value="${ _data.repeatPassword }" required>
                                    </div>
                                </div>
                                <button class="btn btn-primary btn-user btn-block">
                                    Đăng ký
                                </button>
                            </form>
                            <div class="text-center">
                                <a class="small" href="${ pageContext.request.contextPath }/auth">Đăng nhập ngay!</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/common/js/jquery-3.3.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/sweetalert2.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/notifier.js"></script>
</body>

</html>