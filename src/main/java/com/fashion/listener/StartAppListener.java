package com.fashion.listener;

import com.fashion.annotation.Order;
import com.fashion.config.AutoConfig;
import com.fashion.constant.AppConstant;
import com.fashion.constant.PWEncoder;
import com.fashion.dao.BaseDao;
import com.fashion.password.DelegatePasswordEncoder;
import com.fashion.utils.ContextUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class StartAppListener implements ServletContextListener {

    private Properties properties;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        long start = System.currentTimeMillis();
        Map<String, Object> applicationContext = new HashMap<>();
        ServletContext servletContext = sce.getServletContext();
        String realPath = servletContext.getRealPath("/");
        this.properties = this.loadApplicationConfig();
        properties.setProperty("system.real-path", realPath);
        String encodeType = properties.getProperty("security.password.encode.type", "NOOP");
        PWEncoder pwEncoder = PWEncoder.valueOf(encodeType);

        servletContext.setAttribute(AppConstant.APPLICATION_CONFIG_KEY, properties);
        applicationContext.put(Properties.class.getName(), properties);
        applicationContext.put(DelegatePasswordEncoder.class.getName(), new DelegatePasswordEncoder(pwEncoder));

        this.createBean(applicationContext, AppConstant.CONFIG_PACKAGE, AutoConfig.class);
        this.createBean(applicationContext, AppConstant.DAO_PACKAGE, BaseDao.class);
        this.createBean(applicationContext, AppConstant.SERVICE_PACKAGE, Object.class);
        this.injectBean(applicationContext);
        servletContext.setAttribute(AppConstant.BEAN_CONTAINER, applicationContext);
        log.info("context created: {}", System.currentTimeMillis() - start);
    }

    @SneakyThrows
    private <T> void createBean(Map<String, Object> context, String packageScanner, Class<T> parentClass) {
        Set<Class<? extends T>> clazzes = this.getBeanAsClass(context, packageScanner, parentClass);
        for (Class<? extends T> clazz : clazzes) {
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            if (instance instanceof AutoConfig) {
                AutoConfig autoConfig = (AutoConfig) instance;
                autoConfig.init(context, this.properties);
            } else {
                context.put(clazz.getName(), instance);
            }
        }
    }

    private <T> Set<Class<? extends T>> getBeanAsClass(Map<String, Object> context, String repositoryScanner, Class<T> parentClass) {
        Reflections reflections = new Reflections(repositoryScanner, new SubTypesScanner(false));
        return reflections.getSubTypesOf(parentClass)
                .stream()
                .filter(x -> !x.isInterface())
                .filter(x -> !Modifier.isAbstract(x.getModifiers()))
                .sorted((x1, x2) -> {
                    boolean presentOrder1 = x1.isAnnotationPresent(Order.class);
                    boolean presentOrder2 = x2.isAnnotationPresent(Order.class);

                    int priority1 = 0;
                    int priority2 = 0;

                    if (presentOrder1) {
                        Order annotation = x1.getAnnotation(Order.class);
                        priority1 = annotation.value();
                    }

                    if (presentOrder2) {
                        Order annotation = x2.getAnnotation(Order.class);
                        priority2 = annotation.value();
                    }

                    return priority1 > priority2 ? -1 : 1;
                }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void injectBean(Map<String, Object> context) {
        context.values()
                .stream()
                .filter(x -> x.getClass().getPackageName().startsWith("com.fashion"))
                .filter(x -> !Environment.class.equals(x.getClass()))
                .forEach(bean -> {
                    ContextUtil.injectBean(bean, context);
                });
    }

    @SneakyThrows
    private Properties loadApplicationConfig() {
        InputStream resourceAsStream = StartAppListener.class.getResourceAsStream("/application.properties");
        Properties properties = new Environment();
        properties.load(resourceAsStream);
        return properties;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute(AppConstant.BEAN_CONTAINER);
        servletContext.removeAttribute(AppConstant.APPLICATION_CONFIG_KEY);
    }

    public class Environment extends Properties {

        public String handleKey(String key) {
            String value = System.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
            value = super.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
            return System.getenv(key);
        }

        @Override
        public String getProperty(String key) {
            String value = this.handleKey(key);
            Matcher matcher = Pattern.compile("\\$\\{(\\w|\\.|\\-)*\\}", Pattern.CASE_INSENSITIVE)
                    .matcher(value);
            while (matcher.find()) {
                String group = matcher.group();
                String replace = this.getProperty(group.substring(2, group.length() - 1));
                value = value.replace(group, replace);
            }
            return value;
        }
    }
}