<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Fashion Shop - Verify OTP</title>

    <!-- Custom fonts for this template-->
    <link href="${pageContext.request.contextPath}/static/common/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="${pageContext.request.contextPath}/static/sb-admin-2.min.css" rel="stylesheet">

</head>

<body>
    <jsp:include page="./common/notifier.jsp" />
    <div class="container">

        <!-- Outer Row -->
        <div class="row justify-content-center">

            <div class="col-xl-10 col-lg-12 col-md-9">

                <div class="card o-hidden border-0 shadow-lg my-5">
                    <div class="card-body p-0">
                        <!-- Nested Row within Card Body -->
                        <div class="row">
                            <div class="col-lg-6 d-none d-lg-block bg-password-image"></div>
                            <div class="col-lg-6">
                                <div class="p-5">
                                    <div class="text-center">
                                        <h1 class="h4 text-gray-900 mb-2">Xác thực OTP</h1>
                                    </div>
                                    <form class="user" action="${ pageContext.request.contextPath }/auth/otp" method="post">
                                        <div class="form-group">
                                            <input
                                                type="text"
                                                class="form-control form-control-user"
                                                name="otp"
                                                value="${ param.otp }"
                                                placeholder="Nhập OTP">
                                        </div>
                                        <button class="btn btn-primary btn-user btn-block">
                                            Xác thực
                                        </button>
                                    </form>
                                    <hr>
                                    <div class="text-center">
                                        <a class="small" href="${ pageContext.request.contextPath }/auth/send-otp-active">Gửi lại mã</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

        </div>

    </div>

    <!-- Bootstrap core JavaScript-->
    <script src="${pageContext.request.contextPath}/static/common/js/jquery-3.3.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/sweetalert2.js"></script>
    <script src="${pageContext.request.contextPath}/static/common/js/notifier.js"></script>

</body>

</html>