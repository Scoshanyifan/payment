package com.kunbu.pay.payment.order.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="biz_order")
public class BizOrder implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "biz_order_no")
    private String bizOrderNo;

    @Column(name = "order_amount")
    private Long orderAmount;

    @Column(name = "order_status")
    private Integer orderStatus;

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
