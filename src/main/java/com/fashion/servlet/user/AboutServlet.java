package com.fashion.servlet.user;

import com.fashion.annotation.HttpMethod;
import com.fashion.dto.base.PageContent;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "aboutServlet", urlPatterns = "/about-us")
public class AboutServlet extends UserLayoutServlet {

    @HttpMethod
    public PageContent index(HttpServletRequest req, HttpServletResponse res) {
        return PageContent.builder()
                .url("/user/pages/about-us/index")
                .css(List.of("/user/css/about", "/user/css/testimonial", "/user/css/counter"))
                .title("About | Fashion Shop")
                .build();
    }
}
