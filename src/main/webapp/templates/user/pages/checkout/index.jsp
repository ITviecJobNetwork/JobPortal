<jsp:include page="../../common/breadcrumb.jsp">
    <jsp:param name="bc1" value="Shop"/>
</jsp:include>

<!-- Checkout Section Begin -->
<section class="checkout spad pt-5">
    <div class="container">
        <div class="checkout__form">
            <form method="post" action="${ contextPath }/payment">
                <div class="row">
                    <div class="col-lg-8 col-md-6">
                        <h6 class="checkout__title">THÔNG TIN HÓA ĐƠN</h6>
                        <div class="row">
                            <div class="col-lg-6">
                                <div class="checkout__input">
                                    <p class="required">Tên</p>
                                    <input type="text" name="firstName" value="${ _data.firstName}" required>
                                </div>
                            </div>
                            <div class="col-lg-6">
                                <div class="checkout__input">
                                    <p class="required">Họ</p>
                                    <input type="text" name="lastName" value="${ _data.lastName}" required>
                                </div>
                            </div>
                        </div>
                        <div class="checkout__input">
                            <p class="required">Địa chỉ</p>
                            <input type="text" name="address" value="${ _data.address }" placeholder="Địa chỉ chi tiết" class="checkout__input__add" required>
                        </div>
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="checkout__input">
                                    <p class="required">Điện thoại</p>
                                    <input type="text" name="phone" value="${ _data.phone }" required>
                                </div>
                            </div>
                        </div>
                        <div class="checkout__input">
                            <p>Ghi chú (không bắt buộc)</p>
                            <input type="text" name="note" value="${ _data.note }" placeholder="Chỉ nhận hàng giờ hành chính." maxlength="200">
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-6">
                        <jsp:include page="order-list.jsp" />
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>
<!-- Checkout Section End -->