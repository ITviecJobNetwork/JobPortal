package com.fashion.servlet.admin;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.category.CategoryDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.product.CreateProductRequest;
import com.fashion.dto.product.ProductResponse;
import com.fashion.dto.product.ProductSearchRequest;
import com.fashion.exception.BusinessException;
import com.fashion.service.CategoryService;
import com.fashion.service.ProductDetailService;
import com.fashion.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminProductServlet", urlPatterns = "/admin/product/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
@Setter
public class ProductServlet extends AdminLayoutServlet {

    private static final String REDIRECT_PRODUCT_HOME = "redirect:/admin/product";

    private ProductService productService;
    private CategoryService categoryService;
    private ProductDetailService productDetailService;

    @HttpMethod
    public PageContent index(
            HttpServletRequest request,
            @RequestObjectParam ProductSearchRequest productSearchRequest,
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "5") Integer pageSize
    ) {
        PageRequest<ProductSearchRequest> pagedRequest = new PageRequest<>();
        pagedRequest.setData(productSearchRequest);
        pagedRequest.setPage(page);
        pagedRequest.setPageSize(pageSize);
        PageResponse<ProductResponse> productResponse = this.productService.paginateProductList(pagedRequest);
        request.setAttribute(AppConstant.PAGING_KEY, productResponse);
        request.setAttribute("categories", this.categoryService.findAll());
        return PageContent.builder()
                .url("/admin/pages/product/index")
                .title("ADMIN | Quản lý sản phẩm")
                .js(List.of("admin/js/column-controller"))
                .css(List.of("admin/css/column-controller", "admin/css/user"))
                .build();
    }

    @HttpMethod(value = "/detail")
    public PageContent detailProduct(
            HttpServletRequest req,
            @RequestParam("code") String productCode,
            Map<String, Object> state
    ) {
        Result<ProductResponse> detailProduct = this.productDetailService.getDetailProduct(productCode);

        if (detailProduct.isSuccess()) {
            req.setAttribute("_product", detailProduct.getData());
            return PageContent.builder()
                    .title("ADMIN | Quản lý sản phẩm")
                    .url("/admin/pages/product/detail/index")
                    .js(List.of("admin/js/column-controller"))
                    .css(List.of("admin/css/column-controller"))
                    .build();
        }
        state.put(AppConstant.RESULT_KEY, detailProduct);
        return PageContent.builder()
                .isRedirect(true)
                .url(req.getContextPath() + "/admin/product")
                .build();
    }

    @HttpMethod(value = "/detail/lock")
    public Result<Long> unlockOrLockDetail(
            HttpServletRequest req,
            @RequestParam("id") Long id,
            @RequestParam("pCode") String productCode,
            Map<String, Object> state
    ) {
        Result<Long> result = this.productDetailService.unlockOrLockDetail(id);
        result.setFailUrl("redirect:/admin/product/detail?code=" + productCode);
        result.setSuccessUrl("redirect:/admin/product/detail?code=" + productCode);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(value = "/create")
    public PageContent createProduct(HttpServletRequest req) {
        List<CategoryDTO> categories = this.categoryService.findActiveCategory();
        req.setAttribute("categories", categories);
        return PageContent.builder()
                .url("/admin/pages/product/create-product")
                .title("ADMIN | Tạo mới sản phẩm")
                .js(List.of("admin/js/core-ckeditor/ckeditor", "admin/js/ckeditor", "admin/js/create-product"))
                .css(List.of("admin/css/create-product"))
                .build();
    }


    @HttpMethod(method = HttpMethod.Method.POST, value = "/create")
    public Result<CreateProductRequest> createProduct(
            HttpServletRequest req,
            HttpSession httpSession,
            @RequestObjectParam @Valid CreateProductRequest createProductRequest,
            Map<String, Object> state
    ) {
        createProductRequest.setColorSize(req);
        Result<CreateProductRequest> result = this.productService.createProduct(createProductRequest, httpSession);
        result.setSuccessUrl(REDIRECT_PRODUCT_HOME);
        result.setFailUrl("redirect:/admin/product/create");
        state.put(AppConstant.RESULT_KEY, result);
        if (!result.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, result.getData());
        }
        return result;
    }

    @HttpMethod(value = "/update")
    public PageContent updateProduct(
            HttpServletRequest req,
            @RequestParam("pCode") String productCode,
            Map<String, Object> state
    ) {
        Result<ProductResponse> detailProduct = this.productDetailService.getDetailProduct(productCode);
        if (!detailProduct.isSuccess()) {
            state.put(AppConstant.RESULT_KEY, detailProduct);
            return PageContent.builder()
                    .isRedirect(true)
                    .url(req.getContextPath() + "/admin/product")
                    .build();
        }
        List<CategoryDTO> categories = this.categoryService.findAll();
        req.setAttribute("categories", categories);
        ProductResponse productResponse = detailProduct.getData();
        productResponse.setColorsAndSize();
        req.setAttribute(AppConstant.TRANSFORM_DATA_KEY, productResponse);
        return PageContent.builder()
                .url("/admin/pages/product/create-product")
                .title("ADMIN | Cập nhật sản phẩm")
                .js(List.of("admin/js/core-ckeditor/ckeditor", "admin/js/ckeditor", "admin/js/create-product"))
                .css(List.of("admin/css/create-product"))
                .build();
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/update")
    public Result<CreateProductRequest> updateProduct(
            HttpServletRequest req,
            HttpSession httpSession,
            @RequestObjectParam CreateProductRequest createProductRequest,
            Map<String, Object> state
    ) {
        createProductRequest.setColorSize(req);
        Result<CreateProductRequest> result = this.productService.updateProduct(createProductRequest, httpSession);
        if (!result.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, result.getData());
            throw new BusinessException(result.getMessage(), req.getContextPath() + "/admin/product/update?pCode=" + createProductRequest.getCode());
        }
        result.setSuccessUrl(REDIRECT_PRODUCT_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(value = "/lock")
    public Result<String> lockOrUnlockProduct(@RequestParam("code") String code, Map<String, Object> state) {
        Result<String> result = this.productService.lockOrUnlockProduct(code);
        result.setSuccessUrl(REDIRECT_PRODUCT_HOME);
        result.setFailUrl(REDIRECT_PRODUCT_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
    @HttpMethod(value = "/delete")
    public Result<String> deleteProduct(@RequestParam("code") String code, Map<String, Object> state) {
        Result<String> result = this.productService.deleteProduct(code);
        result.setSuccessUrl(REDIRECT_PRODUCT_HOME);
        result.setFailUrl(REDIRECT_PRODUCT_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
}
