package com.fashion.entity;

import com.fashion.constant.MethodPayment;
import com.fashion.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "_order")
public class Order {

    @Id
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @Column(name = "code")
    private String code;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_payment")
    private MethodPayment methodPayment;

    @Column(name = "total")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "admin_note", length = 4000)
    private String adminNote;

    @Column(name = "user_note", length = 4000)
    public String userNote;

    @Column(name = "note", length = 200)
    public String note;

    @Column(name = "payment_id_paypal", unique = true)
    private String paymentIdPaypal;
}