package com.fashion.servlet.auth;

import com.fashion.dto.base.PageContent;
import com.fashion.servlet.base.AbstractServletMapping;
import com.fashion.utils.ObjectUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class AuthLayoutServlet extends AbstractServletMapping {

    @Override
    protected Object mappingRequestObject(HttpServletRequest req, Parameter x) {
        return ObjectUtil.getRequestBody(req, x);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, Object obj) throws ServletException, IOException {
        PageContent pageContent = (PageContent) obj;
        if (Objects.isNull(pageContent)) {
            resp.sendError(404);
            return;
        }
        if (pageContent.isRedirect()) {
            resp.sendRedirect(pageContent.getUrl());
            return;
        }
        pageContent.setUrl("/" + this.getPrefix() + pageContent.getUrl() + this.getSuffix());
        req.getRequestDispatcher(pageContent.getUrl()).forward(req,resp);
    }
}
