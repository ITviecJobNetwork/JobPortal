package com.fashion.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    private String encodedType;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encodedType = filterConfig.getInitParameter("encode");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(this.encodedType);
        servletResponse.setCharacterEncoding(this.encodedType);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
