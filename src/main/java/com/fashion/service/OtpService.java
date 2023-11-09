package com.fashion.service;

import com.fashion.annotation.Order;
import com.fashion.constant.AppConstant;
import com.fashion.dao.UserDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.notify.EmailNotification;
import com.fashion.dto.user.OtpDTO;
import com.fashion.entity.User;
import com.fashion.service.notify.EmailNotifyService;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

@Order(Integer.MAX_VALUE + 1)
@Setter
public class OtpService extends BaseService {

    private UserDao userDao;
    private EmailNotifyService emailNotifyService;
    private Properties properties;

    @Setter(value = AccessLevel.NONE)
    private Random random = new Random();

    public String generateOtp() {
        int i = random.nextInt(1_000_000);
        return String.valueOf(i);
    }

    public OtpDTO sendOtpToEmail(String email, HttpSession httpSession) {
        String otp = this.generateOtp();
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setReceiver(email);
        emailNotification.setTitle("Xác nhận đăng ký tài khoản");
        emailNotification.setBody("/email/register-account");
        emailNotification.setHtml(true);
        emailNotification.setTemplate(true);
        emailNotification.setData(Map.of("email",email, "otp", otp));
        this.emailNotifyService.sendAsyncNotify(emailNotification);

        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setEmail(email);
        otpDTO.setOtp(otp);
        httpSession.setAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY, otpDTO);
        return otpDTO;
    }
    public Result<String> verifyOtp(OtpDTO otpDTO, String otp, HttpSession httpSession) {
        return this.tryCatchWithTransaction(session -> {
            Calendar instance = Calendar.getInstance();
            instance.setTime(otpDTO.getCreatedDate());
            String aliveTimeOtp = properties.getProperty("security.otp.alive-time");
            int time = NumberUtils.toInt(aliveTimeOtp, 0);
            instance.add(Calendar.SECOND, time);
            Date expireTime = instance.getTime();
            if (otpDTO.getCreatedDate().after(expireTime)) {
                httpSession.removeAttribute(AppConstant.SessionKey.OPT_ACTIVE_ACCOUNT_KEY);
                throw new IllegalArgumentException("OTP đã hết hạn. Vui lòng gửi lại.");
            }
            if (!otpDTO.getOtp().equals(otp)) {
                throw new IllegalArgumentException("OTP không chính xác");
            }
            User user = this.userDao.findByEmail(otpDTO.getEmail(), session).orElseThrow();
            user.setIsOtp(true);
            this.userDao.save(user, session);
            return Result.<String>builder()
                    .isSuccess(true)
                    .message("Xác thực thành công")
                    .build();
        }, otpDTO.getEmail());
    }
}
