package com.fashion.config;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Map;
import java.util.Properties;

public class MailAutoConfig implements AutoConfig {

    @Override
    public void init(Map<String, Object> beanContainer, Properties config) {
        Properties props = this.getPropertiesConfig(config);
        Authenticator auth = this.getAuthenticator(config);
        Session session = Session.getInstance(props, auth);
        beanContainer.put(Session.class.getName(), session);
    }

    private Properties getPropertiesConfig(Properties systemConfig) {
        Properties props = new Properties();
        props.put("mail.smtp.host", systemConfig.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", systemConfig.getProperty("mail.smtp.port"));
        props.put("mail.smtp.auth", systemConfig.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", systemConfig.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.starttls.required", systemConfig.getProperty("mail.smtp.starttls.required"));
        props.put("mail.smtp.ssl.protocols", systemConfig.getProperty("mail.smtp.ssl.protocols"));
        props.put("mail.smtp.socketFactory.class", systemConfig.getProperty("mail.smtp.socketFactory.class"));
        return props;
    }

    private Authenticator getAuthenticator(Properties systemConfig) {
        return new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(systemConfig.getProperty("mail.username"), systemConfig.getProperty("mail.password"));
            }
        };
    }
}
