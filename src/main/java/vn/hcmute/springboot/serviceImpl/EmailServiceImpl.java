package vn.hcmute.springboot.serviceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
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
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 5px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                    }
                    h1 {
                        color: #333;
                    }
                    h2 {
                        color: #007BFF;
                    }
                    p {
                        color: #777;
                    }
                </style>
            </head>
            <body>
                
                <div class="container">
                    <h1>OTP Verification</h1>
                    <p>Thank you for registering. Your OTP is:</p>
                    <h2>%s</h2>
                    <p>Please use this OTP to verify your account.</p>
                    <h2>Thank you</h2>
                </div>
            </body>
        </html>
                
        """.formatted(otp), true);
    javaMailSender.send(mimeMessage);
  }

  @Override
  public void sendNewPasswordToEmail(String email, String newPassword) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
    mimeMessageHelper.setTo(email);
    mimeMessageHelper.setSubject("Forgot Password");
    mimeMessageHelper.setText("""
        <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 5px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                    }
                    h1 {
                        color: #333;
                    }
                    h2 {
                        color: #007BFF;
                    }
                    p {
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Forgot Password</h1>
                    <p>Thank you for registering. Your new password is:</p>
                    <h2>%s</h2>
                    <p>Please use this password to login in to website.</p>
                    <h2>Thank you</h2>
                </div>
            </body>
        </html>
                
        """.formatted(newPassword), true);
    javaMailSender.send(mimeMessage);  }

  @Override
  public void sendConfirmRegistrationToRecruiter(String email, String password) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
    mimeMessageHelper.setTo(email);
    mimeMessageHelper.setSubject("Confirmation for registration to receive job notification");
    String htmlContent = """
            <html>
                <head>
                  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #ffffff;
                            border-radius: 5px;
                            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            color: #333;
                        }
                        h2 {
                            color: #007BFF;
                        }
                        p {
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                          <h1>JobPortal ít nhưng mà chất</h1>
                          <p>Cảm ơn bạn đã đăng ký nhận thông tin công việc</p>
                          <p>Hi there,
                                     Our Job Robot has received your instructions!
                                     
                                     He will send you all new Java jobs in Ho Chi Minh as soon as they appear on our site, up to one email per day.
                                     
                                     Delivering jobs to you makes him happy.
                                     
                                     I wish you the best of luck in your search for a new job!</p>
                          <h2>%s</h2>
                          <h2>%s</h2>
                          <h2>Xin cảm ơn!</h2>
                          
                    </div>
                </body>
            </html>
        """.formatted(email, password);
    mimeMessageHelper.setText(htmlContent, true);
    javaMailSender.send(mimeMessage);
  }
}