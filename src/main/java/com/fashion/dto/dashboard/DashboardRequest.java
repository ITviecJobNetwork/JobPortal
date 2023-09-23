package com.fashion.dto.dashboard;

import com.fashion.constant.StatisticType;
import lombok.Data;

@Data
public class DashboardRequest {
    private StatisticType type;
    private Integer date;
    private Integer month;
    private Integer year;
}