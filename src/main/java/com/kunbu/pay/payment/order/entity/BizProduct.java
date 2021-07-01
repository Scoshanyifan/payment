package com.kunbu.pay.payment.order.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="biz_product")
public class BizProduct {

    public static final Integer STATUS_UP = 1;
    public static final Integer STATUS_DOWN = 2;
    public static final Integer STATUS_DELETE = -1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_icon")
    private String productIcon;

    @Column(name = "product_price")
    private Long productPrice;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "product_status")
    private Integer productStatus;

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
