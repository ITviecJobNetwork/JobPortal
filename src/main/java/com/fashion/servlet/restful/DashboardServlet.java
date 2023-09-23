package com.fashion.servlet.restful;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.dto.dashboard.Chart;
import com.fashion.dto.dashboard.DashboardRequest;
import com.fashion.service.DashboardService;
import lombok.Setter;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "restDashboardServlet", urlPatterns = "/admin/rest/dashboard/*")
@Setter
public class DashboardServlet extends BaseRestfulServlet {

    private DashboardService dashboardService;

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/bill")
    public List<Chart> statisticChartBill(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticBill(dashboardRequest);
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/user")
    public List<Chart> statisticChartUser(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticUser(dashboardRequest);
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/product")
    public List<Chart> statisticChartProduct(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticProduct(dashboardRequest);
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/revenue")
    public List<Chart> statisticChartRevenue(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticRevenue(dashboardRequest);
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/profit")
    public List<Chart> statisticChartProfit(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticProfit(dashboardRequest);
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/chart/cost")
    public List<Chart> statisticChartCost(
            @RequestObjectParam DashboardRequest dashboardRequest
    ) {
        return dashboardService.statisticCost(dashboardRequest);
    }
}
