package com.kunbu.pay.payment.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="user_order")
public class UserOrder implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_amount")
    private String orderAmount;

    @Column(name = "order_status")
    private String orderstatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="create_time")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="update_time")
    private Date updateTime;

    public @interface Update {}
}
