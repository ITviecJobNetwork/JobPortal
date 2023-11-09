package com.fashion.service.mail;

import com.fashion.dto.notify.EmailNotification;
import lombok.Setter;
import lombok.SneakyThrows;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Setter
public class EmailSender {

    private Session session;
    private Properties properties;
    private TemplateEngine templateEngine;


    @SneakyThrows({ MessagingException.class, UnsupportedEncodingException.class })
    public void send(EmailNotification emailNotification) {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(properties.getProperty("mail.username"), "Fashion-Shop"));
        msg.setSubject(emailNotification.getTitle(), "UTF-8");
        msg.setSentDate(new Date());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailNotification.getReceiver(), false));

        if (emailNotification.isHtml()) {
            String bodyContent = emailNotification.getBody();
            if (emailNotification.isTemplate()) {
                Map<String, Object> data = emailNotification.getData();
                Context context = new Context();
                context.setVariables(data);
                bodyContent = this.templateEngine.process(bodyContent, context);
            }
            msg.setContent(bodyContent, "text/html; charset=UTF-8");
        } else {
            msg.setText(emailNotification.getBody(), "utf-8");
        }
        Transport.send(msg);
    }
}
