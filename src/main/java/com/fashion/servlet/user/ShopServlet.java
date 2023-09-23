package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.category.CategoryDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.product.ProductDetailResponse;
import com.fashion.dto.product.ProductResponse;
import com.fashion.dto.product.ProductSearchRequest;
import com.fashion.exception.BusinessException;
import com.fashion.service.CategoryService;
import com.fashion.service.ProductDetailService;
import com.fashion.service.ProductService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@WebServlet(name = "shopServlet", urlPatterns = "/shopping/*")
@Setter
public class ShopServlet extends UserLayoutServlet {

    private ProductService productService;
    private CategoryService categoryService;
    private ProductDetailService productDetailService;

    @HttpMethod
    public PageContent index(
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "12") Integer pageSize,
            @RequestObjectParam ProductSearchRequest productSearchRequest,
            HttpServletRequest req
    ) {
        PageRequest<ProductSearchRequest> pageRequest = new PageRequest<>();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        pageRequest.setData(productSearchRequest);
        PageResponse<ProductResponse> productResponsePageResponse = this.productService.paginateProductList(pageRequest);
        List<CategoryDTO> activeCategory = this.categoryService.findActiveCategory();
        Set<String> sizes = this.productDetailService.getAllSize();

        req.setAttribute(AppConstant.PAGING_KEY, productResponsePageResponse);
        req.setAttribute("categories", activeCategory);
        req.setAttribute("sizes", sizes);
        return PageContent.builder()
                .url("/user/pages/shop/index")
                .css(List.of("/user/css/shop", "/user/css/product", "/user/css/paging"))
                .title("Shopping | Fashion Shop")
                .build();
    }

    @HttpMethod(value = "/detail")
    public PageContent detail(
            HttpServletRequest request,
            @RequestParam("pCode") String productCode
    ) {
        Result<ProductDetailResponse> result = this.productService.getDetailProduct(productCode, AppConstant.NUMBER_BEST_SELLER_SHOW);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), request.getContextPath() + "/home");
        }
        request.setAttribute("product", result.getData());
        return PageContent.builder()
                .url("/user/pages/shop/detail")
                .css(List.of("/user/css/shop", "/user/css/product", "/user/css/product-detail"))
                .js(List.of("/user/js/product-detail"))
                .title(result.getData().getName() + " | Fashion Shop")
                .build();
    }
}