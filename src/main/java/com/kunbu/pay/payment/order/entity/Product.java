package com.kunbu.pay.payment.order.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="product")
public class Product {

    public static final Integer STATUS_UP = 1;
    public static final Integer STATUS_DOWN = 2;
    public static final Integer STATUS_DELETE = -1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "icon")
    private String icon;

    @Column(name = "price")
    private Long price;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "ext_info")
    private String extInfo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    private LocalDateTime updateTime;

    public @interface Update {}

}
