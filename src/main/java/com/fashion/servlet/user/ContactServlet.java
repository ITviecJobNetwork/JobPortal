package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.dto.base.PageContent;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "contactServlet", urlPatterns = "/contact")
public class ContactServlet extends UserLayoutServlet {

    @HttpMethod
    public PageContent index(HttpServletRequest req, HttpServletResponse res) {
        return PageContent.builder()
                .url("/user/pages/contact/index")
                .css(List.of("/user/css/contact", "/user/css/map"))
                .title("Contact | Fashion Shop")
                .build();
    }
}
