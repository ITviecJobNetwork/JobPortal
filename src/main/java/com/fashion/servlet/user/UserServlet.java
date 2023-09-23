package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.Result;
import com.fashion.dto.user.ChangePasswordDTO;
import com.fashion.dto.user.UserResponse;
import com.fashion.dto.user.UserUpdateRequest;
import com.fashion.exception.BusinessException;
import com.fashion.service.UserService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@WebServlet(name = "userServlet", urlPatterns = "/user/*")
@Setter
public class UserServlet extends UserLayoutServlet {

    private UserService userService;

    @HttpMethod(value = "/change-password", method = HttpMethod.Method.POST)
    public Result<ChangePasswordDTO> changePassword(
            @RequestObjectParam @Valid ChangePasswordDTO changePasswordDTO,
            Map<String, Object> state,
            UserResponse userResponse,
            HttpSession httpSession,
            HttpServletRequest req
    ) {
        changePasswordDTO.setEmail(userResponse.getEmail());
        Result<ChangePasswordDTO> result = this.userService.changePassword(changePasswordDTO);
        if (!result.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, result.getData());
            throw new BusinessException(result.getMessage(), req.getContextPath() +  "/home");
        }
        result.setSuccessUrl("redirect:/auth");
        state.put(AppConstant.RESULT_KEY, result);
        httpSession.invalidate();
        return result;
    }

    @HttpMethod(value = "/update", method = HttpMethod.Method.POST)
    public Result<UserUpdateRequest> updateUser(
            @RequestObjectParam @Valid UserUpdateRequest userUpdateRequest,
            Map<String, Object> state,
            UserResponse userResponse,
            HttpSession httpSession,
            HttpServletRequest req
    ) {
        if (!userResponse.getEmail().equals(userUpdateRequest.getEmail())) {
            throw new BusinessException("Bạn không có quyền cập nhật thông tin.", req.getContextPath() + "/home");
        }
        userUpdateRequest.setEmail(userResponse.getEmail());
        userUpdateRequest.setId(userResponse.getId());
        Result<UserUpdateRequest> result = this.userService.updateUser(userUpdateRequest);
        if (!result.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, userUpdateRequest);
            throw new BusinessException(result.getMessage(), "/home");
        }
        userResponse.setPhone(userUpdateRequest.getPhone());
        userResponse.setFullName(userUpdateRequest.getFullName());
        userResponse.setAddress(userUpdateRequest.getAddress());
        result.setSuccessUrl("redirect:/home");
        state.put(AppConstant.RESULT_KEY, result);
        httpSession.setAttribute(AppConstant.SESSION_USER, userResponse);
        return result;
    }
}
