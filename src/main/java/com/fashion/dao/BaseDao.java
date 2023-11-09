package com.fashion.dao;

import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.Parameter;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public abstract class BaseDao<E, I> {

    protected abstract Class<E> entityClass();

    public List<E> findAll(Session session) {
        String entityName = this.getEntityName();
        return session
                .createQuery("FROM " + entityName, this.entityClass())
                .getResultList();
    }

    public long count(Session session) {
        return this.findAll(session).size();
    }

    public Optional<E> findById(I id, Session session) {
        E entity = session.find(this.entityClass(), id);
        return Optional.ofNullable(entity);
    }

    public I save(E entity, Session session) {
        return (I) session.save(entity);
    }

    public void deleteById(I id, Session session) {
        E entity = this.findById(id, session)
                .orElseThrow(() -> new IllegalArgumentException("Not Found By Id: " + id));
        this.delete(entity, session);
    }

    public void delete(E entity, Session session) {
        session.delete(entity);
    }

    protected  <T> Optional<T> findReturnOptional(Query<T> query) {
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resultList.get(0));
    }

    protected <T, R> PageResponse<R> paginate(NativeQuery<T> query, PageRequest<?> request, Session session, Function<T, R> rowMapper) {
        String queryString = query.getQueryString();

        String sqlCount = new StringBuilder("SELECT COUNT(1) FROM (")
                .append(queryString)
                .append(") as counter")
                .toString();
        query.setFirstResult((request.getPage() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        List<R> resultList = query.getResultStream()
                .map(rowMapper)
                .collect(Collectors.toList());
        NativeQuery<BigInteger> queryCount = session.createNativeQuery(sqlCount);
        for (Parameter<?> parameter : queryCount.getParameters()) {
            Object parameterValue = query.getParameterValue(parameter.getName());
            queryCount.setParameter(parameter.getName(), parameterValue);
        }
        int total = queryCount.getSingleResult().intValue();

        return PageResponse.<R>builder()
                .items(resultList)
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .total(total)
                .build();
    }

    protected <T> PageResponse<T> paginate(NativeQuery<T> query, PageRequest<?> request, Session session) {
        return this.paginate(query, request, session, Function.identity());
    }

    protected String getEntityName() {
        Class<E> entityClass = this.entityClass();
        return entityClass.getSimpleName();
    }
}
