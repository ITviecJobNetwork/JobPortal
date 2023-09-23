package com.fashion.servlet.admin;

import com.fashion.servlet.base.AbstractLayoutServlet;

public class AdminLayoutServlet extends AbstractLayoutServlet {

    @Override
    protected String getLayout() {
        return "/admin/layout";
    }
}
