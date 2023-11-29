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

    public void sendOtpToEmail(String fullName,String email, String otp) throws MessagingException {
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
                                box-sizing: border-box;
                                border: 1px solid #e7e7e7;
                                border-radius: 5px;
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
                                 background: linear-gradient(to left, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }
                            .header img logo {
                                 overflow-clip-margin: content-box;
                                 overflow: clip;
                            }                   \s
                            
                        </style>
                    </head>
                    <body>
                       <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
                       </div>
                        <div class="container">
                            <div class="fullName">Hello %s</div>
                            <h1>OTP Verification</h1>
                            <p>Thank you for registering with us. Your One Time Password (OTP) is:</p>
                            <div class="otp">%s</div>
                            <p>Please use this OTP to complete your account verification.</p>
                            <p><strong>Thank you for choosing us!</strong></p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                               <img src="https://ci6.googleusercontent.com/proxy/NjOu3s3nFdBUONQj9O0ktDdjC5WxtAQNrRKM1TG-2NQWRdryBS-Bx0U9szshS12TgzhMJYnxoraZRYd4UjjfSEEx0m2Irx8s7Ap7H3nHTOjRveO4wfhZm9xxqXVRQDE60FJrAf_ek-CxAC-YqJqDsijBb3fCtbVPc4LNPtmGS-xF0qA=s0-d-e1-ft#https://itviec.com/assets/mails/logo-footer-11a1a4f9da6eceebada93c51a35ab4aa76d574bfb58880c5167700cf589a7ea9.png" alt="Company Logo">
                        </div>
                    </body>
                </html>
                        
                """.formatted(fullName,otp), true);

        javaMailSender.send(mimeMessage);
    }


    @Override
    public void sendNewPasswordToEmail(String fullName,String email, String newPassword) throws MessagingException {
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
                                 background: linear-gradient(to left, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }                   \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
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
                                 background: linear-gradient(to right, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }                 \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
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
    public void sendApplicationUpdateEmail(ApplicationForm applicationForm,String reason) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(applicationForm.getCandidate().getUsername());
        mimeMessageHelper.setSubject("Application Status Update");
        String rejectContent = """
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
                                 background: linear-gradient(to right, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }                 \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
                       </div>
                        <div class="container">
                            <div class="username">Hello %s</div>
                            
                            <h1>Your CV has not been approved</h1>
                            <p>Because of %s</p>
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                """.formatted(applicationForm.getCandidate().getUsername(),reason);
        String approvedContent = """
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
                                 background: linear-gradient(to right, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }                 \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
                       </div>
                        <div class="container">
                            <div class="username">Hello %s</div>
                            <h1>Your CV has been %s</h1>
                            
                        </div>
                        <div class="footer">
                              <p>Need help? Contact us at <a href="mailto:namvo.010202@gmail.com">namvo.010202@gmail.com</a></p>
                               <p>&copy; 2023 ITViec. All rights reserved.</p>
                        </div>
                    </body>
                </html>
                """.formatted(applicationForm.getCandidate().getUsername(),applicationForm.getStatus());

        if (applicationForm.getStatus().equals(ApplicationStatus.REJECTED)) {
            mimeMessageHelper.setText(rejectContent);
        }
       else{
           mimeMessageHelper.setText(approvedContent);
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
                                 background: linear-gradient(to right, #54151c, #121212);
                                 padding: 10px;
                                 text-align: center;
                                 display: flex;        /* Establishes flex container */
                                
                           }
                         
                           .header img {
                                max-height: 60px;
                           }                 \s
                           .header img.robby-logo {
                               margin-left: auto; /* Pushes the image to the far right */
                           }                 \s
                        </style>
                    </head>
                    <body>
                        <div class="header">
                                      <img class="company-logo" src="https://ci5.googleusercontent.com/proxy/5R6tqKxblgiFiYicXqxWrIU9EXWsSp_V-ISvQh2ifk3YI9a1slctTn0yYa0oqOtl4uTW3PieCAhmR2ETVO86GgIXrJj74Td6-Cpy7ULFDoz3-LaeF1DS99y9AckcVrjZqHOmLalNRq3hA7_d-_MtXhoDnEPw-GShCZ11Uw=s0-d-e1-ft#https://itviec.com/assets/mails/logo-5f3371a704b475a80f27523e1bcfc4853c03bd7e32b8893971074a64d48bdd6c.png" alt="Company Logo">
                                      <img class="robby-logo" src="https://ci3.googleusercontent.com/proxy/LiwLsNl3KZ4TrBBh2ueCTiEEIUpOB_1iNfwOepxdnvMN9RJLf9mfUbH5VnIpXNIUy22t8NtLDviI-ChAHvh9eZsUhc-OGftDnij-tLl-o0CxX7bHHN2zFN-tuS7XoVN3gh4WIJHZxB9znohrEUMyA8S_f0dnPwiw6e5fczbuBa4ouC4uqnSQi6Wn=s0-d-e1-ft#https://itviec.com/assets/mails/robby-subscription-a000c03aa20a8f2397802b9b2addb7974f6352bf61491e0a0fc9c18f08a56b9d.png" alt="Robby">
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