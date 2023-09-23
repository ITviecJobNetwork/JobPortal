package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.cart.AddToCartRequest;
import com.fashion.dto.cart.CartResponse;
import com.fashion.dto.cart.UpdateCart;
import com.fashion.dto.user.UserResponse;
import com.fashion.exception.BusinessException;
import com.fashion.service.CartService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@WebServlet(name = "cartServlet", urlPatterns = "/cart/*")
@Setter
public class CartServlet extends UserLayoutServlet {

    private CartService cartService;

    @HttpMethod
    public PageContent index(HttpServletRequest req, UserResponse currentUser) {
        List<CartResponse> myCart = this.cartService.getMyCart(currentUser);
        req.setAttribute("myCart", myCart);
        return PageContent.builder()
                .url("/user/pages/cart/index")
                .css(List.of("/user/css/shopping-cart"))
                .js(List.of("/user/js/cart"))
                .title("Cart | Fashion Shop")
                .build();
    }

    @HttpMethod(value = "/delete")
    public Result<Long> deleteCart(@RequestParam("cId") Long cartId, Map<String, Object> state) {
        Result<Long> result = this.cartService.deleteById(cartId);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), true);
        }
        result.setSuccessUrl("redirect:/cart");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(value = "/delete-all")
    public Result<Object> deleteAllCart(
            UserResponse userResponse,
            Map<String, Object> state
    ) {
        Result<Object> result = this.cartService.deleteAll(userResponse);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), true);
        }
        result.setSuccessUrl("redirect:/cart");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/add")
    public Result<AddToCartRequest> addToCart(
            @RequestObjectParam @Valid AddToCartRequest addToCartRequest,
            Map<String, Object> state,
            UserResponse currentUser
    ) {
        Result<AddToCartRequest> result = this.cartService.addToCart(addToCartRequest, currentUser);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), true);
        }
        result.setSuccessUrl("redirect:/cart");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/update")
    public Result<Void> updateCart(
            @RequestObjectParam List<UpdateCart> updateCart,
            UserResponse userResponse,
            Map<String, Object> state
    ) {
        Result<Void> result = this.cartService.updateCart(updateCart, userResponse);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), true);
        }
        result.setSuccessUrl("redirect:/cart");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
}
