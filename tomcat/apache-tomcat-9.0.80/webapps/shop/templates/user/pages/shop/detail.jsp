<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Shop Details Section Begin -->
<section class="shop-details">
    <div class="product__details__pic">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="product__details__breadcrumb">
                        <a href="${ contextPath }/home">Trang chủ</a>
                        <a href="${ contextPath }/shopping">Mua sắm</a>
                        <span>${ product.name }</span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-3 col-md-3">
                    <ul class="nav nav-tabs nice-scroll" role="tablist" style="max-height: 518px; overflow: hidden">
                        <li class="nav-item">
                            <a class="nav-link active" data-toggle="tab" href="#color-image-primary${ product.id }" role="tab">
                                <div class="product__thumb__pic set-bg" data-setbg="${ product.image }"></div>
                            </a>
                        </li>
                        <c:forEach var="item" items="${ product.details }">
                            <c:set var="_color" value="${ item.key }" />
                            <li class="nav-item">
                                <a class="nav-link" data-toggle="tab" href="#color-image${ _color.id }" role="tab">
                                    <div class="product__thumb__pic set-bg" data-setbg="${ _color.imageUrl }"></div>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="col-lg-6 col-md-9">
                    <div class="tab-content">
                        <div class="tab-pane active" id="color-image-primary${ product.id }" role="tabpanel">
                            <div class="product__details__pic__item">
                                <img src="${ product.image }" alt="">
                            </div>
                        </div>
                        <c:forEach var="item" items="${ product.details }">
                            <c:set var="_color" value="${ item.key }" />
                            <div class="tab-pane" id="color-image${ _color.id }" role="tabpanel">
                                <div class="product__details__pic__item">
                                    <img src="${ _color.imageUrl }" alt="">
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="product__details__content">
        <div class="container">
            <div class="row d-flex justify-content-center">
                <div class="col-lg-8">
                    <form action="${ contextPath }/cart/add" method="post" class="product__details__text">
                        <h4>${ product.name }</h4>
                        <div class="d-flex justify-content-center align-items-center mb-2">
                            <h3 id="new-price" class="mb-0"></h3>
                            <span id="old-price"></span>
                        </div>
                        <p>${ product.shortDescription }</p>
                        <div class="product__details__option d-flex flex-column align-items-center" style="gap: 20px">
                            <div class="product__details__option__color">
                                <span>Color:</span>
                                <c:forEach var="item" items="${ product.details }" varStatus="_status">
                                    <c:set var="_color" value="${ item.key }" />
                                    <c:forEach var="size" items="${ item.value }">
                                        <input
                                            id="${ _color.color }-${ size.size }"
                                            type="hidden"
                                            data-qty="${ size.quantity }"
                                            data-price="${ size.price }"
                                            data-discount="${ size.discount }"
                                            data-active="${ size.active }"
                                            data-id="${ size.id }"
                                        />
                                    </c:forEach>
                                    <input class="option-color-input" type="radio" name="optionColor" id="color-input-${ _color.id }" value="${ _color.color }" ${ _status.index eq 0 ? 'checked' : ''}>
                                    <label class="option-color" for="color-input-${ _color.id }">${ _color.color }</label>
                                </c:forEach>
                            </div>
                            <div class="product__details__option__size">
                                <span>Size:</span>
                                <c:forEach var="size" items="${ product.sizesSet }" varStatus="_status">
                                    <input type="radio" name="optionSize" id="${ size }" value="${ size }" ${ _status.index eq 0 ? 'checked' : ''}>
                                    <label for="${ size }">${ size }</label>
                                </c:forEach>
                            </div>
                            <div class="product__details__option__qty">
                                <span>Số lượng:</span>
                                <label id="qty"></label>
                            </div>
                        </div>
                        <div class="product__details__cart__option">
                            <div class="quantity">
                                <div class="pro-qty">
                                    <input id="input-qty" type="text" value="1" name="quantity" readonly>
                                    <input id="input-id" type="hidden" name="productDetailId" readonly />
                                </div>
                            </div>
                            <button id="btn-add-cart" class="primary-btn">Thêm giỏ hàng</button>
                        </div>
                        <div class="product__details__last__option">
                            <ul>
                                <li><span>Loại sản phẩm:</span> ${ product.categoryName }</li>
                            </ul>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="product__details__tab">
                        <ul class="nav nav-tabs" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active" data-toggle="tab" href="#tabs-5"
                                role="tab">Mô Tả</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" data-toggle="tab" href="#tabs-6" role="tab">Bình Luận(${ product.comments.size() })</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="tabs-5" role="tabpanel">
                                <div class="product__details__tab__content">
                                    <div class="product__details__tab__content__item">
                                        <c:out value="${ product.description }" escapeXml="false" />
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="tabs-6" role="tabpanel">
                                <jsp:include page="comment.jsp" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<!-- Shop Details Section End -->

<!-- Related Section Begin -->
<section class="related spad">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                <h3 class="related-title">Sản phẩm liên quan</h3>
            </div>
        </div>
        <div class="row">
            <c:set var="_products" value="${ product.relatedProducts }" scope="request" />
            <jsp:include page="../home/product.jsp">
                <jsp:param name="_nameData" value="_products"/>
            </jsp:include>
        </div>
    </div>
</section>
<!-- Related Section End -->