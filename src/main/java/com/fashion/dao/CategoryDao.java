package com.fashion.dao;

import com.fashion.dto.category.CategoryDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.entity.Category;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryDao extends BaseDao<Category, Long> {

    @Override
    protected Class<Category> entityClass() {
        return Category.class;
    }

    public PageResponse<CategoryDTO> searchCategory(PageRequest<String> pageRequest, Session session) {
        String codeName = pageRequest.getData();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM _category c WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (Objects.nonNull(codeName) && !codeName.isBlank()) {
            sqlBuilder.append(" AND (c.code LIKE :codeName OR c.name LIKE :codeName)");
            params.put("codeName", "%" + codeName + "%");
        }
        sqlBuilder.append(" ORDER BY c.created_date DESC");
        String sqlCount = new StringBuilder()
                .append("SELECT COUNT(1) FROM (")
                .append(sqlBuilder)
                .append(") as counter")
                .toString();

        NativeQuery<Tuple> queryData = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        NativeQuery<BigInteger> queryCounter = session.createNativeQuery(sqlCount);
        params.forEach((name, value) -> {
            queryCounter.setParameter(name, value);
            queryData.setParameter(name, value);
        });
        queryData.setMaxResults(pageRequest.getPageSize());
        queryData.setFirstResult((pageRequest.getPage() - 1) * pageRequest.getPageSize());
        List<CategoryDTO> items = queryData.getResultStream()
                .map(tuple -> new CategoryDTO(
                        tuple.get("id", BigInteger.class).longValue(),
                        tuple.get("code", String.class),
                        tuple.get("name", String.class),
                        tuple.get("active", Boolean.class)
                ))
                .collect(Collectors.toList());
        return PageResponse.<CategoryDTO>builder()
                .items(items)
                .page(pageRequest.getPage())
                .pageSize(pageRequest.getPageSize())
                .total(queryCounter.getSingleResult().intValue())
                .build();
    }

    public Optional<Category> findByCode(String code, Session session) {
        Query<Category> query = session.createQuery("FROM Category c WHERE c.code = :code", Category.class);
        query.setParameter("code", code);
        return this.findReturnOptional(query);
    }

    public List<Category> findActiveCategory(Session sesion) {
        Query<Category> query = sesion.createQuery("FROM Category c WHERE c.active = true", Category.class);
        return query.getResultList();
    }
}
