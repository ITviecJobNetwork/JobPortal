package com.fashion.service;

import com.fashion.annotation.Inject;
import com.fashion.constant.AppConstant;
import com.fashion.constant.RoleConstant;
import com.fashion.dao.UserDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.*;
import com.fashion.entity.User;
import com.fashion.password.DelegatePasswordEncoder;
import com.fashion.password.PasswordEncoder;
import com.fashion.utils.ObjectUtil;
import lombok.Setter;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Setter
public class UserService extends BaseService {

    private UserDao userDao;
    private OtpService otpService;

    @Setter(onParam_ = @Inject(usedBean = DelegatePasswordEncoder.class))
    private PasswordEncoder passwordEncoder;

    public PageResponse<UserResponse> paginateUserList(PageRequest<UserSearchRequest> request) {
        return this.doSelect(session -> {
            PageResponse<User> userPageResponse = this.userDao.searchUser(request, session);
            List<UserResponse> items = ObjectUtil.mapList(userPageResponse.getItems(), UserResponse.class);
            return new PageResponse<>(userPageResponse, items);
        });
    }

    public Result<String> lockOrUnlock(String email) {
        return this.tryCatchWithTransaction(session -> {
            User user = this.userDao.findByEmail(email, session)
                    .orElseThrow(() -> new IllegalArgumentException(email + " không tồn tại"));
            user.setActive(!user.getActive());
            this.userDao.save(user, session);
            return Result.<String>builder()
                    .message((user.getActive() ? "Mở khóa" : "Khóa") + " người dùng thành công")
                    .isSuccess(true)
                    .build();
        }, email);
    }

    public Result<UserUpdateRequest> updateUser(UserUpdateRequest request) {
        return this.tryCatchWithTransaction(session -> {
            User _u = this.userDao.findById(request.getId(), session)
                    .orElseThrow(() -> new IllegalArgumentException(request.getId() + " không tìm thấy"));
            _u.setAddress(request.getAddress());
            _u.setFullName(request.getFullName());
            _u.setPhone(request.getPhone());
            request.setEmail(_u.getEmail());
            this.userDao.save(_u, session);
            return Result.<UserUpdateRequest>builder()
                    .isSuccess(true)
                    .message("Cập nhật người dùng thành công")
                    .build();
        }, request);
    }

    public Result<ChangePasswordDTO> changePassword(ChangePasswordDTO request) {
        return this.tryCatchWithTransaction(session -> {
            if (!request.getNewPassword().equals(request.getRepeatPassword())) {
                throw new IllegalArgumentException("Xác nhận mật khẩu không trùng khớp");
            }
            User _u = this.userDao.findByEmail(request.getEmail(), session).orElseThrow();
            if (!this.passwordEncoder.matches(request.getPassword(), _u.getPassword())) {
                throw new IllegalArgumentException("Mật khẩu không chính xác");
            }

            String newPassword = this.passwordEncoder.encode(request.getNewPassword());
            _u.setPassword(newPassword);
            this.userDao.save(_u, session);
            return Result.<ChangePasswordDTO>builder()
                    .isSuccess(true)
                    .message("Đổi mật khẩu thành công")
                    .build();
        }, request);
    }

    public Result<UserResponse> login(LoginDTO loginDTO, HttpSession httpSession) {
        return this.tryCatchWithDoSelect(session -> {
            User user = this.userDao.findByEmail(loginDTO.getEmail(), session)
                    .orElseThrow(() -> new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác"));
            if (!user.getIsOtp()) {
                this.otpService.sendOtpToEmail(user.getEmail(), httpSession);
                UserResponse userResponse = ObjectUtil.map(user, UserResponse.class);
                return Result.<UserResponse>builder()
                        .data(userResponse)
                        .isSuccess(true)
                        .message("Vui lòng xác thực tài khoản.")
                        .build();
            }
            if (!user.getActive()) {
                throw new IllegalArgumentException("Tài khoản đã bị khóa.");
            }
            if (!this.passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
            }
            UserResponse userResponse = ObjectUtil.map(user, UserResponse.class);
            httpSession.setAttribute(AppConstant.SESSION_USER, userResponse);
            return Result.<UserResponse>builder()
                    .data(userResponse)
                    .message("Đăng nhập thành công.")
                    .isSuccess(true)
                    .build();
        });
    }

    public Result<UserDTO> register(@Valid UserDTO userDTO, HttpSession httpSession) {
        return this.tryCatchWithTransaction(session -> {
            if (!userDTO.getPassword().equals(userDTO.getRepeatPassword())) {
                throw new IllegalArgumentException("Mật khẩu không trùng nhau");
            }
            Optional<User> byEmail = this.userDao.findByEmail(userDTO.getEmail(), session);
            if (byEmail.isPresent()) {
                throw new IllegalArgumentException(userDTO.getEmail() + " đã tồn tại");
            }

            User user = new User();
            String encode = this.passwordEncoder.encode(userDTO.getPassword());
            user.setFullName(String.format("%s %s", userDTO.getLastName(), userDTO.getFirstName()));
            user.setPassword(encode);
            user.setActive(true);
            user.setIsOtp(false);
            user.setEmail(userDTO.getEmail());
            user.setRole(RoleConstant.USER);
            this.userDao.save(user, session);
            this.otpService.sendOtpToEmail(user.getEmail(), httpSession);
            return Result.<UserDTO>builder()
                    .isSuccess(true)
                    .message("Đã gửi mã xác thực tới email " + user.getEmail() + ". Vui lòng kiếm tra.")
                    .build();
        }, userDTO);
    }
}
