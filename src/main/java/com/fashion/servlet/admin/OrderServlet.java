package com.fashion.servlet.admin;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.constant.MethodPayment;
import com.fashion.constant.OrderStatus;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.order.OrderResponse;
import com.fashion.dto.order.OrderSearchRequest;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.UserResponse;
import com.fashion.service.OrderService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminOrderServlet", urlPatterns = "/admin/order/*")
@Setter
public class OrderServlet extends AdminLayoutServlet {

    private OrderService orderService;

    @HttpMethod
    public PageContent index(
            HttpServletRequest request,
            @RequestObjectParam OrderSearchRequest orderSearchRequest,
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "5") Integer pageSize
    ) {
        PageRequest<OrderSearchRequest> pageRequest = new PageRequest<>();
        pageRequest.setData(orderSearchRequest);
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        PageResponse<OrderResponse> paged = this.orderService.paginateOrderList(pageRequest);
        request.setAttribute(AppConstant.PAGING_KEY, paged);
        request.setAttribute("methodPayments", MethodPayment.values());
        request.setAttribute("statuses", OrderStatus.values());
        return PageContent.builder()
                .url("/admin/pages/order/index")
                .title("ADMIN | Quản lý hóa đơn")
                .js(List.of("admin/js/order", "admin/js/column-controller"))
                .css(List.of("admin/css/user", "admin/css/column-controller"))
                .build();
    }

    @HttpMethod(value = "/change-status")
    public Result<String> approveOrder(
            @RequestParam("status") OrderStatus status,
            @RequestParam("oCode") String orderCode,
            @RequestParam(value = "adminNote", required = false) String adminNote,
            UserResponse userResponse,
            Map<String, Object> state
    ) {
        Result<String> result = this.orderService.changeStatusOrder(orderCode, adminNote, status, userResponse);
        result.setSuccessUrl("redirect:/admin/order");
        result.setFailUrl("redirect:/admin/order");
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
}
