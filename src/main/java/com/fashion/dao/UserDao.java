package com.fashion.dao;

import com.fashion.dto.dashboard.Chart;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.user.UserSearchRequest;
import com.fashion.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class UserDao extends BaseDao<User, Long> {

    @Override
    protected Class<User> entityClass() {
        return User.class;
    }

    public List<User> findByEmails(List<String> emails, Session session) {
        Query<User> query = session.createQuery("FROM User u WHERE u.email IN :emails", User.class);
        query.setParameter("emails", emails);
        return query.getResultList();
    }

    public Optional<User> findByEmail(String email, Session session) {
        Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return this.findReturnOptional(query);
    }

    public List<Chart> statisticUser(Integer month, Integer year, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT u.active, count(1) FROM _user u WHERE 1 = 1");
        Map<String, Object> param = new HashMap<>();
        if (Objects.nonNull(month)) {
            sqlBuilder.append(" AND MONTH(u.created_date) = :month");
            param.put("month", month);
        }

        if (Objects.nonNull(year)) {
            sqlBuilder.append(" AND YEAR(u.created_date) = :year");
            param.put("year", year);
        }
        sqlBuilder.append(" GROUP BY u.active");
        Query<Tuple> query = session.createNativeQuery(sqlBuilder.toString(), Tuple.class);
        param.forEach(query::setParameter);
        Map<Boolean, BigInteger> result = query.getResultStream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, Boolean.class), tuple -> tuple.get(1, BigInteger.class)));
        BigInteger active = result.get(true);
        BigInteger inactive = result.get(false);
        return List.of(
                new Chart("Hoạt động", Objects.nonNull(active) ? active.toString() : "0"),
                new Chart("Không hoạt động", Objects.nonNull(inactive) ? inactive.toString() : "0")
        );
    }

    public PageResponse<User> searchUser(PageRequest<UserSearchRequest> request, Session session) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM _user u WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();
        UserSearchRequest dataSearch = request.getData();

        if (StringUtils.isNotBlank(dataSearch.getEmail())) {
            sqlBuilder.append(" AND u.email LIKE :email");
            params.put("email", dataSearch.getEmail());
        }

        if (Objects.nonNull(dataSearch.getActive())) {
            sqlBuilder.append(" AND u.active = :active");
            params.put("active", dataSearch.getActive());
        }
        sqlBuilder.append(" ORDER BY u.created_date DESC");
        NativeQuery<User> query = session.createNativeQuery(sqlBuilder.toString(), User.class);
        params.forEach(query::setParameter);
        return this.paginate(query, request, session);
    }
}
