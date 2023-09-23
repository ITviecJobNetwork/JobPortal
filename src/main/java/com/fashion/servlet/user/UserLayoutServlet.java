package com.fashion.servlet.user;

import com.fashion.servlet.base.AbstractLayoutServlet;

public class UserLayoutServlet extends AbstractLayoutServlet {

    @Override
    protected String getLayout() {
        return "/user/layout";
    }

}
