package com.fashion.servlet.base;

import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.utils.ObjectUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Objects;

public abstract class AbstractLayoutServlet extends AbstractServletMapping {

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
        if (!pageContent.isRedirect()) {
            pageContent.setUrl("/" + this.getPrefix() + pageContent.getUrl() + this.getSuffix());
        }

        if (pageContent.isRedirect()) {
            resp.sendRedirect(pageContent.getUrl());
            return;
        }
        req.setAttribute(AppConstant.TITLE_KEY, pageContent.getTitle());
        req.setAttribute(AppConstant.PAGE_CONTENT_KEY, pageContent.getUrl());
        req.setAttribute(AppConstant.CSS_KEY, pageContent.getCss());
        req.setAttribute(AppConstant.JS_KEY, pageContent.getJs());
        req.setAttribute(AppConstant.PARAMETER_KEY, req.getParameterMap());
        String layout = "/" + this.getPrefix() + this.getLayout() + this.getSuffix();
        req.getRequestDispatcher(layout).forward(req, resp);
    }

    /**
     * Not include prefix and suffix
     * @return layout url
     */
    protected abstract String getLayout();
}
