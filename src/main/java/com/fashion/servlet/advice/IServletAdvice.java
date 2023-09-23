package com.fashion.servlet.advice;

import com.fashion.constant.AppConstant;
import com.fashion.dto.base.Result;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public interface IServletAdvice {

    void handle(MethodExecutor runner, HttpServletRequest req, HttpServletResponse resp) throws IOException;

    default void redirectIfException(HttpServletRequest req, HttpServletResponse resp, @NonNull String message) throws IOException {
        String redirectUrlFallback = req.getParameter("redirectUrlFallback");
        if (StringUtils.isBlank(redirectUrlFallback)) {
            this.redirectPreviousUrl(req, resp, message);
            return;
        }
        this.redirectIfException(req, resp, message, redirectUrlFallback);
    }

    default void redirectPreviousUrl(HttpServletRequest req, HttpServletResponse resp, @NonNull String message) throws IOException {
        String previousUrl = req.getHeader("referer"); // previous url
        if (StringUtils.isBlank(previousUrl)) {
            // todo: check session admin or user to get home page
            // default: user home page
            Properties configs = (Properties) req.getServletContext().getAttribute(AppConstant.APPLICATION_CONFIG_KEY);
            String userHomePage = configs.getProperty("view.user.home", "/");
            this.redirectIfException(req, resp, message, req.getContextPath() + userHomePage);
            return;
        }
        this.redirectIfException(req, resp, message, previousUrl);
    }

    default void redirectIfException(HttpServletRequest req, HttpServletResponse resp, @NonNull String message, String urlRedirect) throws IOException {
        Result<Void> result = new Result<>();
        result.setMessage(message);
        result.setSuccess(false);
        Map<String, Object> state = (Map<String, Object>) req.getSession().getAttribute(AppConstant.STATE_KEY);
        if (Objects.isNull(state)) state = new HashMap<>();
        state.put(AppConstant.RESULT_KEY, result);
        req.getSession().setAttribute(AppConstant.STATE_KEY, state);
        resp.sendRedirect(urlRedirect);
    }

    default void handleUnKnowException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(500);
    }

    default void logError(Throwable ex, Logger log) {
        log.error(ExceptionUtils.getStackTrace(ex), ex);
    }

    @FunctionalInterface
    interface MethodExecutor {
        void run(Method method) throws IllegalAccessException, InvocationTargetException, ServletException, IOException;

        default void runAndThrowable(Method method) throws Throwable {
            try {
                this.run(method);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}
