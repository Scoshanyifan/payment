package com.kunbu.pay.payment.order.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="order_item")
public class Orderitem implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "amount")
    private Long amount;

    /**
     * 子订单状态，
     */
    @Column(name = "item_status")
    private Integer itemStatus;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private Long productPrice;

    @Column(name = "real_price")
    private Long realPrice;

    @Column(name = "product_number")
    private Integer productNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    private LocalDateTime updateTime;

    public @interface Update {}
}
