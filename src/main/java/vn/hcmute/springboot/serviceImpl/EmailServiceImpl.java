package vn.hcmute.springboot.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.ApplicationStatus;
import vn.hcmute.springboot.model.JobStatus;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.service.EmailService;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender javaMailSender;

    public void sendOtpToEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        mimeMessageHelper.setText("""
                <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background-color: #e9eff1;
                                color: #4a4a4a;
                                margin: 0;
                                padding: 0;
                            }
                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                background: #fff;
                                border-radius: 8px;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
                                padding: 30px;
                                text-align: center;
                            }
                            h1 {
                                color: #007bff;
                                font-size: 24px;
                                margin-bottom: 10px;
                            }
                            h2 {
                                color: #333;
                                font-size: 20px;
                                margin-top: 5px;
                            }
                            p {
                                font-size: 16px;
                                line-height: 1.5;
                                color: #666;
                            }
                            .otp {
                                display: inline-block;
                                margin: 20px auto;
                                padding: 10px 20px;
                                font-size: 24px;
                                font-weight: bold;
                                color: #007bff;
                                background-color: #f0f8ff;
                                border: 1px solid #b6dfff;
                                border-radius: 5px;
                            }
                            .footer {
                                 text-align: center;
                                 padding: 20px;
                                 font-size: 14px;
                                 color: #777;
                                 background-color: #f8f8f8;
                                 border-top: 1px solid #e7e7e7;
                                  }
                            .footer a {
                                  color: #007bff;
                                  text-decoration: none;
                                   }
                            .header {
                                  background-color: #000;
                                  padding: 10px;
                                  text-align: center;
                            }
                            .header img {
                                  max-height: 60px;
                            }                   \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                              <img src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                        </div>
                        <div class="container">
                            <div class="username">%s</div>
                            <h1>OTP Verification</h1>
                            <p>Thank you for registering with us. Your One Time Password (OTP) is:</p>
                            <div class="otp">%s</div>
                            <p>Please use this OTP to complete your account verification.</p>
                            <p><strong>Thank you for choosing us!</strong></p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                        
                """.formatted(email,otp), true);

        javaMailSender.send(mimeMessage);
    }


    @Override
    public void sendNewPasswordToEmail(String email, String newPassword) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Send new password");
        mimeMessageHelper.setText("""
                <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background-color: #e9eff1;
                                color: #4a4a4a;
                                margin: 0;
                                padding: 0;
                            }
                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                background: #fff;
                                border-radius: 8px;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
                                padding: 30px;
                                text-align: center;
                            }
                            h1 {
                                color: #007bff;
                                font-size: 24px;
                                margin-bottom: 10px;
                            }
                            h2 {
                                color: #333;
                                font-size: 20px;
                                margin-top: 5px;
                            }
                            p {
                                font-size: 16px;
                                line-height: 1.5;
                                color: #666;
                            }
                            .newPassword {
                                display: inline-block;
                                margin: 20px auto;
                                padding: 10px 20px;
                                font-size: 24px;
                                font-weight: bold;
                                color: #007bff;
                                background-color: #f0f8ff;
                                border: 1px solid #b6dfff;
                                border-radius: 5px;
                            }
                            .footer {
                                 text-align: center;
                                 padding: 20px;
                                 font-size: 14px;
                                 color: #777;
                                 background-color: #f8f8f8;
                                 border-top: 1px solid #e7e7e7;
                                  }
                            .footer a {
                                  color: #007bff;
                                  text-decoration: none;
                                   }
                            .header {
                                  background-color: #000;
                                  padding: 10px;
                                  text-align: center;
                            }
                            .header img {
                                  max-height: 60px;
                            }                   \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                              <img src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                        </div>
                        <div class="container">
                            <div class="username">Hello %s</div>
                            
                            <h1>Forgot Password</h1>
                            <p>Your new password is:</p>
                            <div class="newPassword">%s</div>
                            <p>Please use this password to complete your process requirement.</p>
                            <p><strong>Thank you for choosing us!</strong></p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                        
                """.formatted(email,newPassword), true);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendConfirmRegistrationToRecruiter(String email, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Confirmation for registration to become a member in JobPortal");
        String htmlContent = """
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background-color: #e9eff1;
                                color: #4a4a4a;
                                margin: 0;
                                padding: 0;
                            }
                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                background: #fff;
                                border-radius: 8px;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
                                padding: 30px;
                                text-align: center;
                            }
                            h1 {
                                color: #007bff;
                                font-size: 24px;
                                margin-bottom: 10px;
                            }
                            h2 {
                                color: #333;
                                font-size: 20px;
                                margin-top: 5px;
                            }
                            p {
                                font-size: 16px;
                                line-height: 1.5;
                                color: #666;
                            }
                            .password {
                                display: inline-block;
                                margin: 20px auto;
                                padding: 10px 20px;
                                font-size: 24px;
                                font-weight: bold;
                                color: #007bff;
                                background-color: #f0f8ff;
                                border: 1px solid #b6dfff;
                                border-radius: 5px;
                            }
                            .footer {
                                 text-align: center;
                                 padding: 20px;
                                 font-size: 14px;
                                 color: #777;
                                 background-color: #f8f8f8;
                                 border-top: 1px solid #e7e7e7;
                                  }
                            .footer a {
                                  color: #007bff;
                                  text-decoration: none;
                                   }
                            .header {
                                  background-color: #000;
                                  padding: 10px;
                                  text-align: center;
                            }
                            .header img {
                                  max-height: 60px;
                            }                   \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                              <img src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                        </div>
                        <div class="container">
                            <div class="username">Hello %s</div>
                            
                            <h1>Confirm Registration</h1>
                            <p>Your password is:</p>
                            <div class="password">%s</div>
                            <p>Please use this password to login into JobPortal.</p>
                            <p><strong>Thank you for choosing us!</strong></p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                """.formatted(email, password);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }

    @Override
    @Async
    public void sendApplicationUpdateEmail(ApplicationForm applicationForm) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(applicationForm.getCandidate().getUsername());
        mimeMessageHelper.setSubject("Application Status Update");

        if (applicationForm.getStatus().equals(ApplicationStatus.REJECTED)) {
            mimeMessageHelper.setText("Đơn ứng tuyển của bạn đã bị từ chối. Trạng thái mới: " + applicationForm.getStatus());
        }
        if (applicationForm.getStatus().equals(ApplicationStatus.APPROVED)) {
            mimeMessageHelper.setText("Đơn ứng tuyển của bạn đã được chấp nhận. Trạng thái mới: " + applicationForm.getStatus());
        }
        if (applicationForm.getStatus().equals(ApplicationStatus.DELIVERED)) {
            mimeMessageHelper.setText("Đơn ứng tuyển của bạn đã được gửi. Trạng thái mới: " + applicationForm.getStatus());
        }
        if (applicationForm.getStatus().equals(ApplicationStatus.SUBMITTED)) {
            mimeMessageHelper.setText("Đơn ứng tuyển của bạn đã được nộp. Trạng thái mới: " + applicationForm.getStatus());
        }
        if (applicationForm.getStatus().equals(ApplicationStatus.PENDING)) {
            mimeMessageHelper.setText("Đơn ứng tuyển của bạn đang chờ duyệt. Trạng thái mới: " + applicationForm.getStatus());
        }

        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendReasonDeActiveUser(String email, String reason, UserStatus status) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reason for deActive account");
        if (status.equals(UserStatus.INACTIVE)) {
            mimeMessageHelper.setText("Tài khoản của bạn đã bị vô hiệu hóa trong vòng 3 ngày. Lý do: " + reason);
        }
        if (status.equals(UserStatus.ACTIVE)) {
            mimeMessageHelper.setText("Tài khoản của bạn đã được kích hoạt chúc mừng bạn: " + reason);
        }
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendReasonToActiveFromUser(String userName, String adminEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(adminEmail);
        mimeMessageHelper.setSubject("Reason for active account");
        mimeMessageHelper.setText("Tôi muốn khôi phục lại tài khoản của mình mong được chấp nhận from " + userName);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendEmailActiveFromAdmin(String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reason for active account");
        mimeMessageHelper.setText("Tài khoản của bạn đã được kích hoạt chúc mừng bạn: ");
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendEmailUpdateStatusPostJobForRecruiter(String email, JobStatus status) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Update status for posting job by Admin for Recruiter");
        String emailContent="""
                <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background-color: #e9eff1;
                                color: #4a4a4a;
                                margin: 0;
                                padding: 0;
                            }
                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                background: #fff;
                                border-radius: 8px;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
                                padding: 30px;
                                text-align: center;
                            }
                            h1 {
                                color: #007bff;
                                font-size: 24px;
                                margin-bottom: 10px;
                            }
                            h2 {
                                color: #333;
                                font-size: 20px;
                                margin-top: 5px;
                            }
                            p {
                                font-size: 16px;
                                line-height: 1.5;
                                color: #666;
                            }
                            .jobStatus {
                                display: inline-block;
                                margin: 20px auto;
                                padding: 10px 20px;
                                font-size: 24px;
                                font-weight: bold;
                                color: #007bff;
                                background-color: #f0f8ff;
                                border: 1px solid #b6dfff;
                                border-radius: 5px;
                            }
                            .footer {
                                 text-align: center;
                                 padding: 20px;
                                 font-size: 14px;
                                 color: #777;
                                 background-color: #f8f8f8;
                                 border-top: 1px solid #e7e7e7;
                                  }
                            .footer a {
                                  color: #007bff;
                                  text-decoration: none;
                                   }
                            .header {
                                  background-color: #000;
                                  padding: 10px;
                                  text-align: center;
                            }
                            .header img {
                                  max-height: 60px;
                            }                   \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                              <img src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                        </div>
                        <div class="container">
                            <div class="username">Hello %s</div>
                            <h1>Update status for posting job into system</h1>
                            <p>Thank you for collaborating with us. Your job status about job after updating is:</p>
                            <div class="jobStatus">%s</div>
                            <p><strong>Thank you for choosing us!</strong></p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                        
                """.formatted(email,status);


        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }
}