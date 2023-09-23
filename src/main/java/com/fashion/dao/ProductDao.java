package com.fashion.dao;

import com.fashion.constant.AppConstant;
import com.fashion.constant.ImageType;
import com.fashion.dto.dashboard.Chart;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.product.*;
import com.fashion.entity.ColorProduct;
import com.fashion.entity.Product;
import com.fashion.entity.ProductDetail;
import com.fashion.utils.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProductDao extends BaseDao<Product, Long> {

    @Override
    protected Class<Product> entityClass() {
        return Product.class;
    }

    public Optional<Product> findByCode(String code, Session session) {
        Query<Product> query = session.createQuery("FROM Product p WHERE p.code = :code", Product.class);
        query.setParameter("code", code);
        return this.findReturnOptional(query);
    }

    public Map<ColorProductDTO, List<ProductDetailRequest>> getSizeAndColorProduct(long productId, Session session) {
        String sql = new StringBuilder("SELECT cd, pd, img.url as imageUrl FROM ProductDetail pd")
                .append(" JOIN ColorProduct cd ON pd.colorProductId = cd.id")
                .append(" LEFT JOIN Image img ON img.objectId = cd.id AND img.type = :type")
                .append(" WHERE pd.productId = :productId")
                .toString();
        Query<Tuple> query = session.createQuery(sql, Tuple.class);
        query.setParameter("productId", productId);
        query.setParameter("type", ImageType.PRODUCT_COLOR);
        return query.getResultStream()
                .map(tuple -> {
                    ColorProduct colorProduct = tuple.get(0, ColorProduct.class);
                    ProductDetail productDetail = tuple.get(1, ProductDetail.class);
                    return ProductDetailRequest.builder()
                            .id(productDetail.getId())
                            .cost(productDetail.getCost())
                            .price(productDetail.getPrice())
                            .size(productDetail.getSize())
                            .quantity(productDetail.getQuantity())
                            .discount(productDetail.getPercentDiscount())
                            .active(productDetail.getActive())
                            .color(ColorProductDTO.builder()
                                    .color(colorProduct.getName())
                                    .id(colorProduct.getId())
                                    .imageUrl(tuple.get("imageUrl", String.class))
                                    .build())
                            .build();
                })
                .collect(Collectors.groupingBy(ProductDetailRequest::getColor, Collectors.toList()));
    }

    public PageResponse<ProductResponse> searchProduct(PageRequest<ProductSearchRequest> request, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT p.*, c.name as categoryName, i.url as image, MAX(pd.price) as maxPrice, MIN(pd.price) as minPrice")
                .append(" FROM _product p")
                .append("  JOIN _category c ON c.id = p.category_id")
                .append("  JOIN _image i ON i.type = 'PRODUCT' AND i.object_id = p.id")
                .append("  JOIN _product_detail pd ON pd.product_id = p.id")
                .append(" WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();
        ProductSearchRequest data = request.getData();

        if (StringUtils.isNotBlank(data.getCodeName())) {
            sqlBuilder.append(" AND (p.code LIKE :codeName OR p.name LIKE :codeName)");
            params.put("codeName", "%" + data.getCodeName() + "%");
        }

        if (Objects.nonNull(data.getCategoryId())) {
            sqlBuilder.append(" AND p.category_id = :categoryId");
            params.put("categoryId", data.getCategoryId());
        }

        if (StringUtils.isNotBlank(data.getFromDate())) {
            sqlBuilder.append(" AND p.created_date >= :fromDate");
            params.put("fromDate", data.getFromDate());
        }

        if (StringUtils.isNotBlank(data.getToDate())) {
            sqlBuilder.append(" AND p.created_date <= :toDate");
            params.put("toDate", data.getToDate());
        }

        if (StringUtils.isNotBlank(data.getSize())) {
            sqlBuilder.append(" AND pd.size = :size");
            params.put("size", data.getSize());
        }
        sqlBuilder.append(" GROUP BY p.code");

        if (Objects.nonNull(data.getMinPrice()) || Objects.nonNull(data.getMaxPrice())) {
            sqlBuilder.append(" HAVING 1 = 1");
            if (Objects.nonNull(data.getMinPrice())) {
                sqlBuilder.append(" AND minPrice >= :minPrice");
                params.put("minPrice", data.getMinPrice());
            }

            if (Objects.nonNull(data.getMaxPrice())) {
                sqlBuilder.append(" AND maxPrice <= :maxPrice");
                params.put("maxPrice", data.getMaxPrice());
            }
        }

        sqlBuilder.append(" ORDER BY p.created_date DESC");
        NativeQuery<Tuple> nativeQuery = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        params.forEach(nativeQuery::setParameter);
        return this.paginate(nativeQuery, request, session, tuple -> {
            Map<String, Object> dataMap = new HashMap<>();
            tuple.getElements()
                .stream()
                .map(TupleElement::getAlias)
                .forEach(alias -> {
                    String key = Pattern.compile("_([a-z])")
                            .matcher(alias)
                            .replaceAll(m -> m.group(1).toUpperCase());
                    Object value = tuple.get(alias);
                    dataMap.put(key, value);
                });
            return ObjectUtil.getObjectMapper().convertValue(dataMap, ProductResponse.class);
        });
    }

    public List<Chart> statisticProduct(Integer month, Integer year, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT p.name, count(1) FROM _product p")
                .append("  JOIN _product_detail pd ON pd.product_id = p.id")
                .append("  JOIN _order_detail od ON od.product_detail_id = pd.id")
                .append("  JOIN _order o ON o.code = od.order_code")
                .append(" WHERE 1 = 1");
        Map<String, Object> param = new HashMap<>();
        if (Objects.nonNull(month)) {
            sqlBuilder.append(" AND MONTH(o.created_date) = :month");
            param.put("month", month);
        }

        if (Objects.nonNull(year)) {
            sqlBuilder.append(" AND YEAR(o.created_date) = :year");
            param.put("year", year);
        }
        sqlBuilder.append(" GROUP BY p.id");

        Query<Tuple> query = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        param.forEach(query::setParameter);
        return query.getResultStream()
                .map(tutple -> {
                    String categoryName = tutple.get(0, String.class);
                    BigInteger count = tutple.get(1, BigInteger.class);
                    return new Chart(categoryName, count.toString());
                })
                .collect(Collectors.toList());
    }

    public List<ProductImageDTO> getProductImage(int limit,Session session) {
        String sql = new StringBuilder("SELECT p.id, p.name, p.code, i.url FROM Product p")
                .append(" JOIN Image i ON p.id = i.objectId")
                .append(" WHERE i.type = 'PRODUCT' AND p.active = true AND p.isShowHome = true")
                .toString();
        Query<Tuple> query = session.createQuery(sql, Tuple.class);
        query.setMaxResults(limit);
        return query.getResultStream()
                .map(tuple -> ProductImageDTO.builder()
                        .productId(tuple.get(0, Long.class))
                        .productName(tuple.get(1, String.class))
                        .productCode(tuple.get(2, String.class))
                        .imageUrl(tuple.get(3, String.class))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public void updateStatusProductByCategoryId(boolean active, Long categoryId, Session session) {
        String sql = new StringBuilder("UPDATE _product p, _product_detail pd SET  pd.active = :status, p.active = :status")
                .append(" WHERE pd.product_id = p.id and p.category_id = :categoryId")
                .toString();
        NativeQuery nativeQuery = session.createNativeQuery(sql);
        nativeQuery.setParameter("status", active);
        nativeQuery.setParameter("categoryId", categoryId);
        nativeQuery.executeUpdate();
    }

    public List<ProductResponse> getBestSeller(int limit, Session session) {
        String sql = new StringBuilder("SELECT p.id, p.code, p.name, img.url, MIN(pd.price), MAX(pd.price), count(1) as count")
                .append(" FROM _product p")
                .append("  JOIN _image img ON img.object_id = p.id")
                .append("  JOIN _product_detail pd ON p.id = pd.product_id")
                .append("  JOIN _order_detail od ON pd.id = od.product_detail_id")
                .append(" WHERE img.type = 'PRODUCT' AND p.active = true")
                .append(" GROUP BY p.code")
                .append(" ORDER BY count DESC")
                .toString();
        return this.getProductInUserHome(sql, limit, session);
    }

    public List<ProductResponse> getNewest(int limit, Session session) {
        String sql = new StringBuilder("SELECT p.id, p.code, p.name, img.url, MIN(pd.price), MAX(pd.price)")
                .append(" FROM _product p")
                .append("  JOIN _image img ON img.object_id = p.id")
                .append("  JOIN _product_detail pd ON p.id = pd.product_id")
                .append(" WHERE img.type = 'PRODUCT' AND p.active = true AND DATEDIFF(now(), p.created_date) <= ?1")
                .append(" GROUP BY p.code")
                .append(" ORDER BY p.created_date DESC")
                .toString();
        return this.getProductInUserHome(sql, limit, session, AppConstant.DATE_INDICATE_NEWEST);
    }

    public List<ProductResponse> getHotSales(int limit, Session session) {
        String sql = new StringBuilder("SELECT p.id, p.code, p.name, img.url, MIN(pd.price), MAX(pd.price), MAX(pd.percent_discount) as discount")
                .append(" FROM _product p")
                .append("  JOIN _image img ON img.object_id = p.id")
                .append("  JOIN _product_detail pd ON p.id = pd.product_id")
                .append(" WHERE img.type = 'PRODUCT' AND p.active = true")
                .append(" GROUP BY p.code")
                .append(" HAVING discount >= ?1")
                .append(" ORDER BY discount DESC")
                .toString();
        return this.getProductInUserHome(sql, limit, session, AppConstant.PERCENT_INDICATE_HOT_SALE);
    }

    public List<ProductResponse> getByCategoryId(int limit, Long categoryId, Session session) {
        String sql = new StringBuilder("SELECT p.id, p.code, p.name, img.url, MIN(pd.price), MAX(pd.price)")
                .append(" FROM _product p")
                .append("  JOIN _image img ON img.object_id = p.id AND img.type = 'PRODUCT'")
                .append("  JOIN _product_detail pd ON p.id = pd.product_id")
                .append(" WHERE p.active = true AND p.category_id = ?1")
                .append(" GROUP BY p.code")
                .toString();
        return this.getProductInUserHome(sql, limit, session, categoryId);
    }

    private List<ProductResponse> getProductInUserHome(String sql, int limit, Session session, Object... params) {
        Function<Tuple, ProductResponse> mapper = tuple -> ProductResponse.builder()
                .id(tuple.get(0, BigInteger.class).longValue())
                .code(tuple.get(1, String.class))
                .name(tuple.get(2, String.class))
                .image(tuple.get(3, String.class))
                .minPrice(tuple.get(4, BigDecimal.class))
                .maxPrice(tuple.get(5, BigDecimal.class))
                .build();
        return this.getProductInUserHome(sql, limit, mapper, session, params);

    }

    private List<ProductResponse> getProductInUserHome(String sql, int limit,  Function<Tuple, ProductResponse> mapper, Session session, Object...params) {
        Query<Tuple> query = session.createNativeQuery(sql, Tuple.class);
        if (Objects.nonNull(params)) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        query.setMaxResults(limit);
        return query.getResultStream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}