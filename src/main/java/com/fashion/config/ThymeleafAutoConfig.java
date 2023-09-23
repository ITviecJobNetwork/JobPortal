package com.fashion.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Map;
import java.util.Properties;

public class ThymeleafAutoConfig implements AutoConfig {

    @Override
    public void init(Map<String, Object> beanContainer, Properties config) {
        TemplateEngine templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(config.getProperty("system.real-path") + "/" + config.getProperty("view.prefix"));
        templateResolver.setSuffix(".thymeleaf.html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("utf-8");
        templateEngine.setTemplateResolver(templateResolver);
        beanContainer.put(TemplateEngine.class.getName(), templateEngine);
    }
}
