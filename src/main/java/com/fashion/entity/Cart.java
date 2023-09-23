package com.fashion.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "_cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "email")
    private String email;

    @Column(name = "product_detail_id")
    private long productDetailId;
}
