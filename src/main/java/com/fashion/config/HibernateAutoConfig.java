package com.fashion.config;

import lombok.Data;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Data
public class HibernateAutoConfig implements AutoConfig {

    private SessionFactory sessionFactory;
    private String hbnFile;
    private String entityPackage;

    @Override
    public void init(Map<String, Object> beanContainer, Properties config) {
        this.hbnFile = config.getProperty("hibernate.config.location", "hibernate.cfg.xml");
        this.entityPackage = config.getProperty("hibernate.package.entity", "com.fashion.entity");
        Configuration configuration = this.load(this.hbnFile);
        beanContainer.put(SessionFactory.class.getName(), configuration.buildSessionFactory());
    }

    private Configuration load(String hbnFile) {
        Configuration configure = new Configuration()
                .configure(hbnFile);
        this.loadEntity(configure);
        return configure;
    }

    private void loadEntity(Configuration configure) {
        Reflections reflections = new Reflections(this.entityPackage, new SubTypesScanner(false));
        Set<Class<?>> daoClasses = reflections.getSubTypesOf(Object.class);
        daoClasses.stream().forEach(configure::addAnnotatedClass);
    }
}
