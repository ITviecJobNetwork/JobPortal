package com.fashion.servlet.base;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.annotation.RequestPart;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.Result;
import com.fashion.dto.user.UserResponse;
import com.fashion.exception.AuthenticationException;
import com.fashion.exception.ConstraintException;
import com.fashion.servlet.advice.DefaultServletAdvice;
import com.fashion.servlet.advice.IServletAdvice;
import com.fashion.utils.ContextUtil;
import com.fashion.utils.ObjectUtil;
import com.fashion.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractServletMapping extends HttpServlet {

    protected ServletContext context;
    private Properties properties;
    private Map<String, Method> methodMapping;
    private IServletAdvice servletAdvice;

    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp, Object obj) throws ServletException, IOException;
    protected abstract Object mappingRequestObject(HttpServletRequest req, Parameter x);

    @Override
    public void init() throws ServletException {
        this.context = this.getServletContext();
        this.properties = (Properties) this.context.getAttribute(AppConstant.APPLICATION_CONFIG_KEY);
        this.methodMapping = this.getMethodMapping();
        this.servletAdvice = new DefaultServletAdvice(this.methodMapping);
        ContextUtil.injectBean(this, (Map<String, Object>) this.context.getAttribute(AppConstant.BEAN_CONTAINER));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.servletAdvice.handle(method -> {
            Object[] params = this.mappingParameterMethod(method, req, resp);
            Object obj = method.invoke(this, params);

            // handle HttpMethod GET but DELETE action
            if (obj instanceof Result) {
                this.doPost(req, resp, obj);
                return;
            }
            this.doGet(req, resp, obj);
        }, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.servletAdvice.handle(method -> {
            Object[] params = this.mappingParameterMethod(method, req, resp);
            Object result = method.invoke(this, params);
            this.doPost(req, resp, result);
        }, req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp, Object obj) throws ServletException, IOException {
        Result<?> result = (Result<?>) obj;
        req.setAttribute(AppConstant.RESULT_KEY, result);
        String url = result.isSuccess() ? result.getSuccessUrl() : result.getFailUrl();
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("UnKnow url to redirect");
        }
        if (url.startsWith("redirect:")) {
            resp.sendRedirect(req.getContextPath() +  url.replace("redirect:", ""));
            return;
        }

        if (url.startsWith("external:")) {
            resp.sendRedirect(url.replace("external:", ""));
            return;
        }
        String forwardUrl = url.replace("forward:", "");
        req.getRequestDispatcher(forwardUrl).forward(req, resp);
    }

    private Object[] mappingParameterMethod(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> state = new HashMap<>();
        req.getSession().setAttribute(AppConstant.STATE_KEY, state);
        return Arrays.stream(method.getParameters())
                .map(x -> {
                    Class<?> type = x.getType();
                    if (HttpSession.class.equals(type)) return req.getSession();
                    if (HttpServletRequest.class.equals(type)) return req;
                    if (HttpServletResponse.class.equals(type)) return resp;
                    if (Map.class.equals(type)) return state;
                    if (UserResponse.class.equals(type)) return this.getCurrentUser(req);
                    if (x.isAnnotationPresent(RequestObjectParam.class)) return this.handleMappingRequestObject(req, x);
                    if (x.isAnnotationPresent(RequestPart.class)) return ObjectUtil.mappingRequestPart(req, x.getAnnotation(RequestPart.class));
                    if (x.isAnnotationPresent(RequestParam.class)) {
                        RequestParam annotation = x.getAnnotation(RequestParam.class);
                        if (List.class.equals(type)) {
                            return mappingRequestParamAsList(req, x);
                        }
                        String val = this.mappingRequestParam(req, annotation);
                        return ObjectUtil.convertValue(val, type);
                    }
                    return null;
                })
                .toArray();
    }

    private List<Object> mappingRequestParamAsList(HttpServletRequest req, Parameter parameter) {
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        Class<?> genericType = ObjectUtil.getGenericType(parameter);
        String[] values = req.getParameterValues(annotation.value());
        List<Object> arg = Arrays.stream(values)
                .map(x -> ObjectUtil.convertValue(x, genericType))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(arg)) {
            throw new IllegalArgumentException(annotation.value() + " is missing");
        }
        return arg;

    }

    private String mappingRequestParam(HttpServletRequest req, RequestParam annotation) {
        String parameter = req.getParameter(annotation.value());
        if (StringUtils.isBlank(parameter)) {
            parameter = annotation.defaultVal();
        }
        if (annotation.required() && StringUtils.isBlank(parameter)) {
            throw new IllegalArgumentException(annotation.value() + " is missing");
        }
        return parameter;
    }

    protected UserResponse getCurrentUser(HttpServletRequest req) {
        Object userResponse = req.getSession().getAttribute(AppConstant.SESSION_USER);
        if (Objects.isNull(userResponse)) {
            throw new AuthenticationException("unauthentication");
        }
        return (UserResponse) userResponse;
    }

    private Object handleMappingRequestObject(HttpServletRequest req, Parameter x) throws ConstraintException {
        Object o = this.mappingRequestObject(req, x);
        if (x.isAnnotationPresent(Valid.class)) {
            Set<ConstraintViolation<Object>> validate = ValidationUtil.validate(o);
            if (CollectionUtils.isNotEmpty(validate)) {
                throw new ConstraintException(o, validate);
            }
        }
        return o;
    }

    protected String getPrefix() {
        return this.getProperty("view.prefix", "templates");
    }

    protected String getSuffix() {
        return this.getProperty("view.suffix", ".html");
    }

    protected String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    private Map<String, Method> getMethodMapping() {
        return Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(HttpMethod.class))
                .collect(Collectors.toMap(method -> {
                    HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
                    return httpMethod.method().name() + AppConstant.UNDER_SCORE + httpMethod.value();
                }, Function.identity()));
    }

    @Override
    public void destroy() {
        this.methodMapping = null;
        this.properties = null;
    }
}