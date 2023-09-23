package com.fashion.service;

import com.fashion.dao.OrderDao;
import com.fashion.dao.ProductDao;
import com.fashion.dao.UserDao;
import com.fashion.dto.dashboard.Chart;
import com.fashion.dto.dashboard.DashboardDTO;
import com.fashion.dto.dashboard.DashboardRequest;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
public class DashboardService extends BaseService {
    private OrderDao orderDao;
    private UserDao userDao;
    private ProductDao productDao;

    public DashboardDTO statisticMoney(Date date) {
        return this.doSelect(session -> {
            DashboardDTO dashboardDTO = this.orderDao.statisticMoney(date, session);
            if (Objects.nonNull(date)) {
                dashboardDTO.setTotalOrder(this.orderDao.countByDate(date, session));
            } else {
                dashboardDTO.setTotalOrder(this.orderDao.count(session));
            }
            return dashboardDTO;
        });
    }

    public List<Chart> statisticBill(DashboardRequest request) {
        return this.doSelect(session -> this.orderDao.statisticBill(request.getMonth(), request.getYear(), session));
    }

    public List<Chart> statisticUser(DashboardRequest request) {
        return this.doSelect(session -> this.userDao.statisticUser(request.getMonth(), request.getYear(), session));
    }

    public List<Chart> statisticProduct(DashboardRequest request) {
        return this.doSelect(session -> this.productDao.statisticProduct(request.getMonth(), request.getYear(), session));
    }

    public List<Chart> statisticRevenue(DashboardRequest request) {
        return this.doSelect(session -> this.orderDao.statisticRevenue(request, session));
    }

    public List<Chart> statisticProfit(DashboardRequest request) {
        return this.doSelect(session -> this.orderDao.statisticProfit(request, session));
    }

    public List<Chart> statisticCost(DashboardRequest request) {
        return this.doSelect(session -> this.orderDao.statisticCost(request, session));
    }
}
