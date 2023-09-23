package com.fashion.service;

import com.fashion.dto.base.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

@Data
@Slf4j
public class BaseService {

    private SessionFactory sessionFactory;

    protected <T> T doWithTransaction(Function<Session, T> consumer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            T t = consumer.apply(session);
            transaction.commit();
            return t;
        } catch (Exception ex) {
            this.rollback(transaction);
            throw ex;
        } finally {
            session.close();
        }
    }


    private void rollback(Transaction transaction) {
        try {
            transaction.rollback();
        } catch (Exception ignore) {}
    }

    protected <T> T doSelect(Function<Session, T> handler) {
        Session session = sessionFactory.openSession();
        return handler.apply(session);
    }

    protected <T> Result<T> tryCatch(Function<T, Result<T>> handler, T data) {
        try {
            return handler.apply(data);
        } catch (Exception ex) {
            return Result.<T>builder()
                    .isSuccess(false)
                    .message(ex.getMessage())
                    .data(data)
                    .build();
        }
    }

    protected <T> Result<T> tryCatchWithDoSelect(Function<Session, Result<T>> handler) {
        return this.tryCatch(data -> this.doSelect(handler), null);
    }

    protected <T> Result<T> tryCatchWithTransaction(Function<Session, Result<T>> handler, T data) {
        try {
            Result<T> tResult = this.doWithTransaction(handler);
            tResult.setSuccess(true);
            return tResult;
        } catch (Exception ex) {
            return Result.<T>builder()
                    .isSuccess(false)
                    .message(ex.getMessage())
                    .data(data)
                    .build();
        }
    }

    protected Result<Void> tryCatchWithTransaction(Function<Session, Result<Void>> handler ) {
        return this.tryCatchWithTransaction(handler, null);
    }
}