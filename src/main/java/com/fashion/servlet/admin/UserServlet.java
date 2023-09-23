package com.fashion.servlet.admin;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.UserResponse;
import com.fashion.dto.user.UserSearchRequest;
import com.fashion.dto.user.UserUpdateRequest;
import com.fashion.service.UserService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminUserServlet", urlPatterns = "/admin/user/*")
@Setter
public class UserServlet extends AdminLayoutServlet {

    private static final String REDIRECT_USER_HOME = "redirect:/admin/user";

    private UserService userService;

    @HttpMethod
    public PageContent index(
            HttpServletRequest req,
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "5") Integer pageSize,
            @RequestObjectParam UserSearchRequest userSearchRequest
    ) {
        PageRequest<UserSearchRequest> pageRequest = new PageRequest<>();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        pageRequest.setData(userSearchRequest);
        PageResponse<UserResponse> paged = this.userService.paginateUserList(pageRequest);
        req.setAttribute(AppConstant.PAGING_KEY, paged);
        return PageContent.builder()
                .url("/admin/pages/user/index")
                .title("ADMIN | Quản lý người dùng")
                .css(List.of("admin/css/user"))
                .build();
    }

    @HttpMethod(value = "/lock")
    public Result<String> lockOrUnlockUser(@RequestParam("email") String email, Map<String, Object> state) {
        Result<String> result = this.userService.lockOrUnlock(email);
        result.setSuccessUrl(REDIRECT_USER_HOME);
        result.setFailUrl(REDIRECT_USER_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/update")
    public Result<UserUpdateRequest> updateUser(@RequestObjectParam UserUpdateRequest request, Map<String, Object> state) {
        Result<UserUpdateRequest> result = this.userService.updateUser(request);
        result.setFailUrl(REDIRECT_USER_HOME);
        result.setSuccessUrl(REDIRECT_USER_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        if (!result.isSuccess()) {
            state.put(AppConstant.MODAL_ID_KEY, "update-user-fail");
            state.put(AppConstant.MODAL_DATA_FAIL_KEY, result.getData());
        }
        return result;
    }
}
