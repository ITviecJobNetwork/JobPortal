package com.fashion.servlet.auth;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.constant.RoleConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.user.LoginDTO;
import com.fashion.dto.user.OtpDTO;
import com.fashion.dto.user.UserDTO;
import com.fashion.dto.user.UserResponse;
import com.fashion.exception.BusinessException;
import com.fashion.service.OtpService;
import com.fashion.service.UserService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@WebServlet(name = "authServlet", urlPatterns = {"/auth/*"})
@Setter
public class AuthServlet extends AuthLayoutServlet {

    private OtpService otpService;
    private UserService userService;
    private Properties properties;

    @HttpMethod(method = HttpMethod.Method.GET)
    public PageContent login() {
        return PageContent.builder()
                .url("/login")
                .build();
    }

    @HttpMethod(value = "/register", method = HttpMethod.Method.GET)
    public PageContent getRegister() {
        return PageContent.builder()
                .url("/register")
                .build();
    }

    @HttpMethod(value = "/send-otp-active")
    public PageContent sendOtpActiveAccount(HttpSession session, HttpServletRequest req, Map<String, Object> state) {
        Object otp = session.getAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY);
        if (Objects.isNull(otp)) {
            throw new BusinessException("Chưa có OTP", req.getContextPath() + "/auth");
        }
        OtpDTO otpDTO = (OtpDTO) otp;
        this.otpService.sendOtpToEmail(otpDTO.getEmail(), session);
        return PageContent.builder()
                .url(req.getContextPath() + "/auth/otp")
                .isRedirect(true)
                .build();
    }

    @HttpMethod("/otp")
    public PageContent otp(HttpSession session, HttpServletRequest req, Map<String, Object> state) {
        Object otp = session.getAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY);
        if (Objects.isNull(otp)) {
            throw new BusinessException("Chưa có OTP", req.getContextPath() + "/auth");
        }
        return PageContent.builder()
                .url("/otp")
                .build();
    }

    @HttpMethod(value = "/otp", method = HttpMethod.Method.POST)
    public Result<String> postOtp(@RequestParam("otp") String otp, HttpSession session, HttpServletRequest req, Map<String, Object> state) {
        Object sessionOtp = session.getAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY);
        if (Objects.isNull(otp)) {
            throw new BusinessException("Chưa có OTP", req.getContextPath() + "/auth");
        }
        Result<String> result = this.otpService.verifyOtp((OtpDTO) sessionOtp, otp, session);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), req.getContextPath() + "/auth/otp");
        }
        result.setSuccessUrl("redirect:/auth");
        state.put(AppConstant.RESULT_KEY, result);
        session.removeAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY);
        return result;
    }

    @HttpMethod(method = HttpMethod.Method.POST)
    public Result<UserResponse> postLogin(
            @RequestObjectParam @Valid LoginDTO loginDTO,
            HttpSession httpSession,
            HttpServletRequest req,
            Map<String, Object> state
    ) {
        Result<UserResponse> loginResult = this.userService.login(loginDTO, httpSession);
        if (!loginResult.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, loginDTO);
            throw new BusinessException(loginResult.getMessage(), req.getContextPath() + "/auth");
        }
        UserResponse data = loginResult.getData();
        if (!data.getIsOtp()) {
            loginResult.setSuccessUrl("redirect:/auth/otp");
            return loginResult;
        }

        if (RoleConstant.ADMIN.equals(data.getRole())) {
            String urlAdminHome = properties.getProperty("view.admin.home");
            loginResult.setSuccessUrl("redirect:" + urlAdminHome);
        } else if (RoleConstant.USER.equals(data.getRole())) {
            String urlUserHome = properties.getProperty("view.user.home");
            loginResult.setSuccessUrl("redirect:" + urlUserHome);
        }
        return loginResult;
    }

    @HttpMethod(value = "/register", method = HttpMethod.Method.POST)
    public Result<UserDTO> postRegister(@RequestObjectParam @Valid UserDTO userDTO, HttpServletRequest req, HttpSession httpSession, Map<String, Object> state) {
        Result<UserDTO> register = this.userService.register(userDTO, httpSession);
        if (!register.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, register.getData());
            throw new BusinessException(register.getMessage(), req.getContextPath() + "/auth/register");
        }
        register.setSuccessUrl("redirect:/auth/otp");
        state.put(AppConstant.RESULT_KEY, register);
        return register;
    }

    @HttpMethod("/logout")
    public PageContent logout(HttpSession httpSession, HttpServletRequest request) {
        httpSession.invalidate();
        return PageContent.builder()
                .isRedirect(true)
                .url(request.getContextPath() + "/auth")
                .build();
    }
}