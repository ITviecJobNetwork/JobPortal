package com.fashion.listener;

import com.fashion.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class StateToProsListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest servletRequest = (HttpServletRequest) sre.getServletRequest();
        String uri = servletRequest.getRequestURI();
        String contextPath = servletRequest.getContextPath();
        if (uri.startsWith(contextPath + "/static") || uri.startsWith(contextPath + "/resources")) return;
        log.info("{}", uri);
        HttpSession session = servletRequest.getSession();
        Object state = session.getAttribute(AppConstant.STATE_KEY);
        if (Objects.nonNull(state)) {
            Map<String, Object> stateMap = (Map<String, Object>) state;
            stateMap.forEach(servletRequest::setAttribute);
            session.removeAttribute(AppConstant.STATE_KEY);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletRequestListener.super.requestDestroyed(sre);
    }
}
