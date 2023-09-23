package com.fashion.dao;

import com.fashion.dto.order.OrderDetailResponse;
import com.fashion.entity.OrderDetail;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailDao extends BaseDao<OrderDetail, Long> {

    @Override
    protected Class<OrderDetail> entityClass() {
        return OrderDetail.class;
    }

    public List<OrderDetail> findByOrderId(String orderId, Session session) {
        Query<OrderDetail> query = session.createQuery("FROM OrderDetail od WHERE od.orderCode = :orderId", OrderDetail.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    public List<OrderDetailResponse> findDetailByOrderId(String orderId, Session session) {
        String sql = new StringBuilder("SELECT od, p.name as name, p.code as productCode, img.url as image, pd.size as size, cp.name as color")
                .append(" FROM OrderDetail od")
                .append("  JOIN ProductDetail pd ON pd.id = od.productDetailId")
                .append("  JOIN ColorProduct cp ON cp.id = pd.colorProductId")
                .append("  JOIN Product p ON p.id = pd.productId")
                .append("  JOIN Image img ON img.objectId = p.id AND img.type = 'PRODUCT'")
                .append(" WHERE od.orderCode = :orderCode")
                .toString();
        Query<Tuple> query = session.createQuery(sql, Tuple.class);
        query.setParameter("orderCode", orderId);
        return query.getResultStream()
                .map(tuple -> {
                    OrderDetail orderDetail = tuple.get(0, OrderDetail.class);
                    OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                    orderDetailResponse.setCost(orderDetail.getCost());
                    orderDetailResponse.setProductDetailId(orderDetail.getProductDetailId());
                    orderDetailResponse.setDiscount(orderDetail.getPercentDiscount());
                    orderDetailResponse.setOrderCode(orderDetail.getOrderCode());
                    orderDetailResponse.setId(orderDetail.getId());
                    orderDetailResponse.setPrice(orderDetail.getPrice());
                    orderDetailResponse.setQuantity(orderDetail.getQuantity());
                    orderDetailResponse.setProductCode(tuple.get("productCode", String.class));
                    orderDetailResponse.setProductName(tuple.get("name", String.class));
                    orderDetailResponse.setProductImg(tuple.get("image", String.class));
                    orderDetailResponse.setSize(tuple.get("size", String.class));
                    orderDetailResponse.setColor(tuple.get("color", String.class));
                    return orderDetailResponse;
                })
                .collect(Collectors.toList());

    }
}
