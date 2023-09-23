package com.fashion.dao;

import com.fashion.constant.OrderStatus;
import com.fashion.constant.StatisticType;
import com.fashion.dto.dashboard.Chart;
import com.fashion.dto.dashboard.DashboardDTO;
import com.fashion.dto.dashboard.DashboardRequest;
import com.fashion.dto.order.OrderSearchRequest;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class OrderDao extends BaseDao<Order, String> {

    @Override
    protected Class<Order> entityClass() {
        return Order.class;
    }

    public DashboardDTO statisticMoney(Date date, Session session) {
        String sql = new StringBuilder("SELECT")
                .append("  SUM( od.cost ) as cost")
                .append("  ,SUM( (od.price * od.quantity) * (100 - od.percentDiscount) / 100 ) as revenue")
                .append("  ,SUM( (od.price * od.quantity) * (100 - od.percentDiscount) / 100) - SUM( od.cost ) as profit")
                .append(" FROM Order o")
                .append("  JOIN OrderDetail od ON o.code = od.orderCode")
                .append(" WHERE o.status = 'DONE'")
                .append(Objects.nonNull(date) ? " AND date(o.createdDate) = :createdDate" : "")
                .toString();

        Query<Tuple> query = session.createQuery(sql, Tuple.class);
        if (Objects.nonNull(date)) {
            query.setParameter("createdDate", date);
        }
        return this.findReturnOptional(query)
                .map(result -> {
                    DashboardDTO dashboardDTO = new DashboardDTO();
                    dashboardDTO.setCost(result.get("cost", BigDecimal.class));
                    dashboardDTO.setProfit(result.get("profit", BigDecimal.class));
                    dashboardDTO.setRevenue(result.get("revenue", BigDecimal.class));
                    return dashboardDTO;
                })
                .orElseGet(DashboardDTO::new);
    }

    public long countByDate(Date date, Session session) {
        Query query = session.createQuery("SELECT COUNT(o) FROM Order o WHERE date(o.createdDate) = :createdDate");
        query.setParameter("createdDate", date);
        return (Long) query.getSingleResult();
    }

    public List<Chart> statisticBill(Integer month, Integer year, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT o.status, count(1) FROM _order o WHERE 1 = 1");
        Map<String, Object> param = new HashMap<>();
        if (Objects.nonNull(month)) {
            sqlBuilder.append(" AND MONTH(o.created_date) = :month");
            param.put("month", month);
        }

        if (Objects.nonNull(year)) {
            sqlBuilder.append(" AND YEAR(o.created_date) = :year");
            param.put("year", year);
        }
        sqlBuilder.append(" GROUP BY o.status");

        Query<Tuple> query = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        param.forEach(query::setParameter);
        Map<String, BigInteger> result = query.getResultStream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, BigInteger.class)));
        return Arrays.stream(OrderStatus.values())
                .map(status -> {
                    BigInteger count = result.get(status.name());
                    if (Objects.nonNull(count)) {
                        return new Chart(status.getValue(), count.toString());
                    }
                    return new Chart(status.getValue(), "0");
                })
                .collect(Collectors.toList());
    }

    public PageResponse<Order> searchOrder(PageRequest<OrderSearchRequest> request, Session session) {
        OrderSearchRequest dataSearch = request.getData();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM _order o WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (StringUtils.isNotBlank(dataSearch.getCode())) {
            sqlBuilder.append(" AND o.code LIKE :code");
            params.put("code", "%" + dataSearch.getCode() + "%");
        }

        if (StringUtils.isNotBlank(dataSearch.getMethodPayment())) {
            sqlBuilder.append(" AND o.method_payment = :methodPayment");
            params.put("methodPayment", dataSearch.getMethodPayment());
        }

        if (StringUtils.isNotBlank(dataSearch.getPhone())) {
            sqlBuilder.append(" AND o.phone = :phone");
            params.put("phone", dataSearch.getPhone());
        }

        if (StringUtils.isNotBlank(dataSearch.getAddress())) {
            sqlBuilder.append(" AND o.address LIKE :address");
            params.put("address", "%" + dataSearch.getAddress() + "%");
        }

        if (StringUtils.isNotBlank(dataSearch.getCreatedBy())) {
            sqlBuilder.append(" AND o.created_by LIKE :createdBy");
            params.put("createdBy", "%" + dataSearch.getCreatedBy() + "%");
        }

        if (StringUtils.isNotBlank(dataSearch.getFromDate())) {
            sqlBuilder.append(" AND o.created_date >= :fromDate");
            params.put("fromDate", dataSearch.getFromDate());
        }

        if (StringUtils.isNotBlank(dataSearch.getToDate())) {
            sqlBuilder.append(" AND o.created_date <= :toDate");
            params.put("toDate", dataSearch.getToDate());
        }

        if (Objects.nonNull(dataSearch.getStatus())) {
            sqlBuilder.append(" AND o.status = :status");
            params.put("status", dataSearch.getStatus().name());
        }

        sqlBuilder.append(" ORDER BY o.created_date DESC");
        NativeQuery<Order> query = session.createNativeQuery(sqlBuilder.toString(), Order.class);
        params.forEach(query::setParameter);
        return this.paginate(query, request, session);
    }

    public List<Chart> statisticRevenue(DashboardRequest request, Session session) {
        return this.statistic(request, "SUM( (od.price * od.quantity) * (100 - od.percent_discount) / 100 )", session);
    }

    public List<Chart> statisticProfit(DashboardRequest request, Session session) {
        return this.statistic(request, "SUM( (od.price * od.quantity) * (100 - od.percent_discount) / 100) - SUM( od.cost ) as profit", session);
    }

    public List<Chart> statisticCost(DashboardRequest request, Session session) {
        return this.statistic(request, "SUM( od.cost ) as cost", session);
    }

    public List<Chart> statistic(DashboardRequest request, String sql, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT")
                .append(StatisticType.MONTH.equals(request.getType()) ? " DAY(o.created_date)" : " MONTH(o.created_date)")
                .append(", ")
                .append(sql)
                .append(" FROM _order o")
                .append(" JOIN _order_detail od ON o.code = od.order_code")
                .append(" WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();
        if (StatisticType.MONTH.equals(request.getType())) {
            sqlBuilder.append(" AND MONTH(o.created_date) = :month")
                    .append(" GROUP BY DAY(o.created_date)")
                    .append(" ORDER BY DAY(o.created_date) ASC");
            params.put("month", request.getMonth());
        } else if (StatisticType.YEAR.equals(request.getType())) {
            sqlBuilder.append(" AND YEAR(o.created_date) = :year")
                    .append(" GROUP BY MONTH(o.created_date)")
                    .append(" ORDER BY MONTH(o.created_date) ASC");
            params.put("year", request.getYear());
        } else {
            sqlBuilder.append(" AND YEAR(o.created_date) = :year")
                    .append(" GROUP BY MONTH(o.created_date)")
                    .append(" ORDER BY MONTH(o.created_date) ASC");
            params.put("year", Calendar.getInstance().get(Calendar.YEAR));
        }
        Query<Tuple> query = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        params.forEach(query::setParameter);
        return query.stream()
                .map(tuple -> {
                    Integer month = tuple.get(0, Integer.class);
                    BigDecimal total = tuple.get(1, BigDecimal.class);
                    return new Chart((StatisticType.YEAR.equals(request.getType()) ? "Tháng " : "Ngày ") + month, total.toString());
                })
                .collect(Collectors.toList());
    }
}
