package com.fashion.service.notify;

import com.fashion.annotation.Order;
import com.fashion.dto.notify.EmailNotification;
import com.fashion.service.mail.EmailSender;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Order(Integer.MAX_VALUE)
@Slf4j
public class EmailNotifyService extends AsyncableNotifyService<EmailNotification> {

    @Setter
    private EmailSender emailSender;

    @Override
    public void sendNotify(EmailNotification notification) {
        long start = System.currentTimeMillis();
        log.info("send email: {}", notification.getReceiver());
        this.emailSender.send(notification);
        log.info("send mail successfully: {}", System.currentTimeMillis() - start);
    }
}
