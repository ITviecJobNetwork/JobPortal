package com.fashion.servlet.admin;

import com.fashion.annotation.HttpMethod;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.dashboard.DashboardDTO;
import com.fashion.service.DashboardService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@WebServlet(name = "dashboardServlet", urlPatterns = { "/admin/home" })
@Setter
public class DashboardServlet extends AdminLayoutServlet {

    private DashboardService dashboardService;

    @HttpMethod
    public PageContent dashboard(HttpServletRequest req) {
        Date now = new Date();
        DashboardDTO totalStatistic = this.dashboardService.statisticMoney(null);
        DashboardDTO todayStatistic = this.dashboardService.statisticMoney(now);
        req.setAttribute("totalStatistic", totalStatistic);
        req.setAttribute("todayStatistic", todayStatistic);
        req.setAttribute("now", now);
        return PageContent.builder()
                .url("/admin/pages/dashboard/index")
                .js(List.of("admin/js/chart.min", "admin/js/dashboard", "admin/js/dashboard-chart"))
                .title("ADMIN | Thống kê")
                .build();
    }

}
