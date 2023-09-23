package com.fashion.servlet.restful;

import com.fashion.servlet.base.AbstractServletMapping;
import com.fashion.utils.ObjectUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Parameter;

public class BaseRestfulServlet extends AbstractServletMapping {

    @Override
    protected Object mappingRequestObject(HttpServletRequest req, Parameter x) {
        try {
            return ObjectUtil.getObjectMapper().readValue(req.getInputStream(), x.getType());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, Object response) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectUtil.getObjectMapper().writeValue(writer, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, Object obj) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectUtil.getObjectMapper().writeValue(writer, obj);
    }
}
