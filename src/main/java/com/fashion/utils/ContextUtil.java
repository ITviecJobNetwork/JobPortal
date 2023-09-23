package com.fashion.utils;

import com.fashion.annotation.Inject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@UtilityClass
@Slf4j
public class ContextUtil {

    public void injectBean(Object bean, Map<String, Object> context) {
        Method[] methods = recursiveAllMethod(bean.getClass());
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                try {
                    Object[] beans = getBeans(method, context);
                    log.info("inject: {} to {} with setter {}", beans, bean.getClass().getName(), method.getName());
                    method.invoke(bean, beans);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(ExceptionUtils.getStackTrace(e), e);
                }
            }
        }
    }

    public Object[] getBeans(Method method, Map<String, Object> context) {
        return Arrays.stream(method.getParameters())
                .map(param -> {
                    if (param.isAnnotationPresent(Inject.class)) {
                        Inject annotation = param.getAnnotation(Inject.class);
                        return context.get(annotation.usedBean().getName());
                    }
                    return context.get(param.getType().getName());
                })
                .toArray();
    }

    public Method[] recursiveAllMethod(Class<?> classBean) {
        Method[] methods = classBean.getDeclaredMethods();
        do {
            classBean = classBean.getSuperclass();
            methods = ArrayUtils.addAll(methods, classBean.getDeclaredMethods());
        } while (!Object.class.equals(classBean));
        return methods;
    }
}
