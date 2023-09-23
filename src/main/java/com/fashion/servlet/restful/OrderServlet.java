package com.fashion.servlet.restful;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.RoleConstant;
import com.fashion.dto.order.OrderResponse;
import com.fashion.dto.user.UserResponse;
import com.fashion.service.OrderService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "restOrderServlet", urlPatterns = "/rest/order/*")
@Setter
public class OrderServlet extends BaseRestfulServlet {

    private OrderService orderService;

    @HttpMethod
    public OrderResponse getOrderDetailByOrderId(@RequestParam("orderId") String code, UserResponse userResponse, HttpServletResponse resp) throws IOException {
        OrderResponse byCode = this.orderService.findByCode(code);
        if (RoleConstant.ADMIN.equals(userResponse.getRole())) {
            return byCode;
        }

        if (byCode.getCreatedBy().equals(userResponse.getEmail())) {
            return byCode;
        }
        resp.sendError(403);
        return null;
    }

}
