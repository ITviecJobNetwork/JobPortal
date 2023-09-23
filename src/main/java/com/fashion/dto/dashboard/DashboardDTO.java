package com.fashion.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;
    private long totalOrder;

    public void setRevenue(BigDecimal revenue) {
        if (Objects.isNull(revenue)) revenue = new BigDecimal("0");
        this.revenue = revenue;
    }

    public void setCost(BigDecimal cost) {
        if (Objects.isNull(cost)) cost = new BigDecimal("0");
        this.cost = cost;
    }

    public void setProfit(BigDecimal profit) {
        if (Objects.isNull(profit)) profit = new BigDecimal("0");
        this.profit = profit;
    }
}
