package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.product.ProductImageDTO;
import com.fashion.dto.product.ProductResponse;
import com.fashion.service.ProductService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@WebServlet(name = "homeServlet", loadOnStartup = 1, urlPatterns = {"/home"})
@Setter
public class HomeServlet extends UserLayoutServlet {

    private ProductService productService;

    @HttpMethod
    public PageContent index(HttpServletRequest req) {
        List<ProductImageDTO> productImage = this.productService.getProductImage(AppConstant.NUMBER_IMAGE_SHOW_HOME);
        List<ProductResponse> bestSeller = this.productService.getBestSeller(AppConstant.NUMBER_BEST_SELLER_SHOW);
        List<ProductResponse> newest = this.productService.getNewest(AppConstant.NUMBER_BEST_SELLER_SHOW);
        List<ProductResponse> hotSales = this.productService.getHotSales(AppConstant.NUMBER_BEST_SELLER_SHOW);
        req.setAttribute("productImage", productImage);
        req.setAttribute("bestSeller", bestSeller);
        req.setAttribute("newest", newest);
        req.setAttribute("hotSales", hotSales);
        return PageContent.builder()
                .url("/user/pages/home/index")
                .title("Home | Fashion shop")
                .css(List.of("/user/css/product", "/user/css/banner", "/user/css/hero"))
                .build();
    }
}
