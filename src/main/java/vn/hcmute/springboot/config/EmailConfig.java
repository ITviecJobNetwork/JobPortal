package vn.hcmute.springboot.config;

import java.util.Properties;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {


  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost("smtp.gmail.com");
    javaMailSender.setPort(Integer.parseInt("587"));
    javaMailSender.setUsername("namvo.010202@gmail.com");
    javaMailSender.setPassword("adtk iohv rfnu duna");

    Properties props = javaMailSender.getJavaMailProperties();
    props.put("mail.smtp.starttls.enable", "true");
    return javaMailSender;
  }
}