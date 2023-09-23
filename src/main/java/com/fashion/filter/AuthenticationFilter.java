package com.fashion.filter;

import com.fashion.constant.AppConstant;
import com.fashion.constant.RoleConstant;
import com.fashion.dto.user.UserResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        Object sessionUser = session.getAttribute(AppConstant.SESSION_USER);
        if (Objects.isNull(sessionUser)) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
