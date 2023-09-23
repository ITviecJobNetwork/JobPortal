package com.fashion.entity;

import com.fashion.constant.ImageType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "_image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ImageType type;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;
}
