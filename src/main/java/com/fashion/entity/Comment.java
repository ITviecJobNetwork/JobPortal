package com.fashion.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "product_id")
    private long productId;

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "parent_id")
    private Long parentId;
}
