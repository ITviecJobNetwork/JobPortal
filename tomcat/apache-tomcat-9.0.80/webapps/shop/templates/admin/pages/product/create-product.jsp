<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div>
    <a href="${ contextPath }/admin/product">Quay lại</a>
    <form method="post" enctype="multipart/form-data" class="d-flex flex-column mt-3" style="gap: 20px;">
        <input type="hidden" name="id" value="${ _data.id }" />
        <div>
            <a data-toggle="collapse" href="#basicInfo" role="button" class="h3 text-dark">
                <i class="fa fa-chevron-down" aria-hidden="true"></i>
                Thông tin cơ bản
            </a>
            <div class="collapse show mt-2 card p-3" id="basicInfo">
                <div class="row">
                    <div class="col-md-12">
                        <label for="isShowHome">Hiển thị home page</label>
                        <input id="isShowHome" type="checkbox" name="isShowHome" value="true" ${ _data.isShowHome ? 'checked' : '' } />
                    </div>
                </div>
                <div class="row mt-3">
                    <div class="col-md-6">
                        <label class="required">Mã sản phẩm</label>
                        <input class="form-control" placeholder="Nhập mã sản phẩm" name="code" value="${ _data.code }" ${ currentUri eq contextPath += '/admin/product/update' ? 'readonly' : ''} required/>
                    </div>

                    <div class="col-md-6">
                        <label class="required">Tên sản phẩm</label>
                        <input class="form-control" placeholder="Nhập tên sản phẩm" name="name" value="${ _data.name }" required/>
                    </div>
                </div>

                <div class="row mt-3">
                    <div class="col-md-6">
                        <label class=" ${ currentUri eq contextPath += '/admin/product/update' ? '' : 'required' }">Ảnh đại diện</label>
                        <input class="form-control" type="file" name="image" accept="image/png" ${ currentUri eq contextPath += '/admin/product/update' ? '' : 'required'}/>
                    </div>
                    <div class="col-md-6">
                        <label class="required">Loại sản phẩm</label>
                        <select class="custom-select" name="categoryId">
                            <c:forEach var="item" items="${ categories }">
                                <option value="${ item.id }" ${ _data.categoryId eq item.id ? 'selected' : '' }>${ item.code } - ${ item.name }</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="row mt-3">
                    <div class="col-md-12">
                        <label class="required">Mô tả ngắn</label>
                        <textarea class="form-control" name="shortDescription" rows="5" maxlength="200">${ _data.shortDescription }</textarea>
                    </div>
                </div>

                <div class="row mt-3">
                    <div class="col-md-12">
                        <label class="required">Mô tả sản phẩm</label>
                        <textarea id="ckeditor" name="description">${ _data.description }</textarea>
                    </div>
                </div>
            </div>
        </div>

        <div>
            <a data-toggle="collapse" href="#detailInfo" role="button" class="h3 text-dark">
                <i class="fa fa-chevron-down" aria-hidden="true"></i>
                Thông tin chi tiết
            </a>
            <div class="collapse show mt-2 card p-3" id="detailInfo">
                <div class="row">
                    <div class="col-md-6">
                        <fieldset class="border p-3">
                            <legend class="mb-0" style="width: fit-content">Màu</legend>
                            <div class="d-flex">
                                <input id="color-input" class="form-control" placeholder="Nhập màu ( 20 ký tự )" maxlength="20"/>
                                <button type="button" class="btn btn-primary ml-1" id="add-color-btn">
                                    <i class="fa fa-plus" aria-hidden="true"></i>
                                </button>
                            </div>
                            <input type="hidden" name="colors" id="colors" value="${ _data.colors }" required readonly/>
                            <div id="color-container" class="d-flex flex-wrap mt-3" style="gap: 10px">
                                <c:if test="${ _data.colors ne null }">
                                    <c:forEach var="color" items="${fn:split(_data.colors,',')}">
                                        <div class="card px-3 py-2 delete cursor-pointer" title="Click to delete">${ color }</div>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </fieldset>
                    </div>

                    <div class="col-md-6">
                        <fieldset class="border p-3">
                            <legend class="mb-0" style="width: fit-content">Size</legend>
                            <div class="d-flex">
                                <input id="size-input" class="form-control" placeholder="Nhập size ( 5 ký tự )" maxlength="5"/>
                                <button type="button" class="btn btn-primary ml-1" id="add-size-btn">
                                    <i class="fa fa-plus" aria-hidden="true"></i>
                                </button>
                            </div>
                            <input type="hidden" name="sizes" id="sizes" value="${ _data.sizes}" required readonly/>
                            <div id="size-container" class="d-flex flex-wrap mt-3" style="gap: 10px">
                                <c:if test="${ _data.sizes ne null }">
                                    <c:forEach var="size" items="${fn:split(_data.sizes,',')}">
                                        <div class="card px-3 py-2 delete cursor-pointer" title="Click to delete">${ size }</div>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </fieldset>
                    </div>
                </div>

                <div class="row mt-3 mx-0">
                    <fieldset class="border p-3 w-100">
                        <legend class="mb-0" style="width: fit-content">Cấu hình</legend>
                        <div class="table-responsive">
                            <table id="color-size-table" class="table table-bordered w-100">
                                <thead>
                                    <tr>
                                        <th>Màu</th>
                                        <th>Size</th>
                                        <th>Số lượng</th>
                                        <th>Giá nhập</th>
                                        <th>Giá bán</th>
                                        <th>Giảm giá (%)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${ _data.productDetails }">
                                        <c:forEach var="pd" items="${ item.value }" varStatus="_status">
                                            <tr class="${ item.key } ${ pd.size }">
                                                <c:if test="${ _status.index eq 0}">
                                                    <td class="color" rowspan="${ item.value.size() }" data-name="${ item.key }">
                                                        <div>
                                                            <label>${ item.key }</label>
                                                            <label for="color-${ item.key }" class="d-flex cursor-pointer">
                                                                <input id="color-${ item.key }" type="file" class="d-none" name="${ item.key }"/>
                                                                <i class="fa fa-upload ${ pd.color.imageUrl ne null ? 'd-none' : ''}"></i>
                                                                <c:if test="${ pd.color.imageUrl ne null }">
                                                                    <img src="${ pd.color.imageUrl }" class="preview-image scalable" />
                                                                </c:if>
                                                            </label>
                                                        </div>
                                                    </td>
                                                </c:if>
                                                <td class="size">${ pd.size }</td>
                                                <td>
                                                    <input class="form-control" type="number" min="0" value="${ pd.quantity }" name="quantity-${ item.key }-${ pd.size }" />
                                                </td>
                                                <td>
                                                    <input class="form-control" type="number" min="0" value="${ pd.cost }" name="cost-${ item.key }-${ pd.size }" />
                                                </td>
                                                <td>
                                                    <input class="form-control" type="number" min="0" value="${ pd.price }" name="price-${ item.key }-${ pd.size }" />
                                                </td>
                                                <td>
                                                    <input class="form-control" type="number" min="0" value="${ pd.discount }" name="discount-${ item.key }-${ pd.size }" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </fieldset>
                </div>
            </div>
        </div>

        <div class="d-flex justify-content-center">
            <a class="btn btn-secondary" href="${ contextPath }/admin/product">Hủy bỏ</a>
            <button class="btn btn-primary ml-3">Lưu lại</button>
        </div>
    </form>
</div>