package com.kunbu.pay.payment.order;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_amount")
    private Long orderAmount;

    @Column(name = "order_status")
    private Integer orderStatus;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "origin_product_name")
    private String originProductName;

    @Column(name = "pay_type")
    private Integer payType;

    @Column(name = "biz_type")
    private Integer bizType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    private LocalDateTime updateTime;

    public @interface Update {}
}
