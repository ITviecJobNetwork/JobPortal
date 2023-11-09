<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Fashion Shop - Login</title>

    <!-- Custom fonts for this template-->
    <link href="${pageContext.request.contextPath}/static/common/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="${pageContext.request.contextPath}/static/sb-admin-2.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Open+Sans" />

</head>
<style>

    /* Style the Google Sign-In button */
  .google-signin-button {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 20px;
    padding: 8px 12px;
    cursor: pointer;
    width: 100%;
  }

  /* Style the Google icon */
  .google-icon {
    margin-right: 8px;
    width: 20px;
    height: 20px;
    background-image: url('https://storage.googleapis.com/support-kms-prod/ZAl1gIwyUsvfwxoW9ns47iJFioHXODBbIkrK'); /* Replace with the URL of the Google icon image */
    background-size: cover;
    border-radius: 50%;
  }


</style>
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
                        <div class="col-lg-6 d-none d-lg-block bg-login-image"></div>
                        <div class="col-lg-6">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">Xin chào mừng!</h1>
                                </div>
                                <form method="post" class="user">
                                    <div class="form-group">
                                        <input
                                                id="exampleInputEmail"
                                                type="email"
                                                class="form-control form-control-user"
                                                placeholder="Nhập địa chỉ email..."
                                                name="email"
                                                value="${ _data.email }"
                                                required
                                        >
                                    </div>
                                    <div class="form-group">
                                        <input
                                                id="exampleInputPassword"
                                                type="password"
                                                class="form-control form-control-user"
                                                placeholder="Password"
                                                name="password"
                                                value="${ _data.password }"
                                                required
                                        >
                                    </div>



                                    <button class="btn btn-primary btn-user btn-block">
                                        Đăng nhập
                                    </button>
                                    <p class="text-center">or</p>
                                    <!-- Add this code inside your existing form -->
                                    <div class="form-group text-center">
                                        <button type="button" class="google-signin-button">
                                            <span class="google-icon"></span>
                                            <a href="https://accounts.google.com/o/oauth2/auth?scope=email&redirect_uri=http://localhost:8082/shop/login-google&response_type=code
    &client_id=977768771060-ht5icp0b27fpi9cht1bjb1devcupov62.apps.googleusercontent.com&approval_prompt=force">Login With Google</a>
                                        </button>
                                    </div>


                                    <div class="text-center text-lg-start mt-4 pt-2">
                                        <p class="small fw-bold mt-2 pt-1 mb-0">Bạn chưa có tài khoản? <a href="${pageContext.request.contextPath}/auth/register"
                                                                                                          class="link-danger">Đăng ký</a></p>
                                    </div>
<%--                                    <div class="text-center justify-content-between align-items-center  small">--%>
<%--                                        <a href="${pageContext.request.contextPath}/auth/forgot-password" class="text-primary">Quên mật khẩu?</a>--%>
<%--                                    </div>--%>
                                </form>
                                <hr>

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
<script src="https://apis.google.com/js/platform.js" async defer></script>


<%--    <!-- Core plugin JavaScript-->--%>
<%--    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>--%>

<%--    <!-- Custom scripts for all pages-->--%>
<%--    <script src="js/admin.min.js"></script>--%>

</body>

</html>