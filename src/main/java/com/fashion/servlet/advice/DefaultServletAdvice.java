package com.fashion.servlet.advice;

import com.fashion.annotation.HttpMethod;
import com.fashion.constant.AppConstant;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ConstraintException;
import com.fashion.exception.RequestParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultServletAdvice implements IServletAdvice {

    private final Map<String, Method> methodMapping;

    @Override
    public void handle(MethodExecutor runner, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpMethod.Method httpMethod = HttpMethod.Method.valueOf(req.getMethod().toUpperCase());
            String pathInfo = req.getPathInfo();
            Method method = methodMapping.get(httpMethod + AppConstant.UNDER_SCORE + (Objects.isNull(pathInfo) ? StringUtils.EMPTY : pathInfo));
            if (Objects.isNull(method)) {
                resp.sendError(404);
                return;
            }
            runner.runAndThrowable(method);
        } catch (BusinessException ex) {
            this.logError(ex, log);
            this.handleBusinessException(ex, req, resp);
        } catch (ConstraintException ex) {
            this.logError(ex, log);
            this.handleValidateObjectException(ex, req, resp);
        } catch (RequestParamException ex) {
            this.logError(ex, log);
            this.handleRequestParamException(ex, req, resp);
        } catch (Throwable ex) {
            this.logError(ex, log);
            this.handleUnKnowException(ex, req, resp);
        }
    }

    private void handleBusinessException(BusinessException ex, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (ex.isRedirectPreviousUrl() || StringUtils.isBlank(ex.getRedirectUrl())) {
            this.redirectPreviousUrl(req, resp, ex.getMessage());
            return;
        }
        this.redirectIfException(req, resp, ex.getMessage(), ex.getRedirectUrl());
    }

    private void handleMethodInvokeException(Exception ex, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String redirectUrlFallback = req.getParameter("redirectUrlFallback");
        if (StringUtils.isNotBlank(redirectUrlFallback)) {
            this.redirectIfException(req, resp, ex.getMessage());
            return;
        }
        resp.sendError(500);
    }

    private void handleValidateObjectException(ConstraintException ex, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String message = ex.getConstraints()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("</br>"));
        this.redirectIfException(req, resp, message);
    }

    private void handleRequestParamException(RequestParamException ex,HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.redirectIfException(req, resp, ex.getMessage());
    }
}