package com.kunbu.pay.payment.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name="user_order")
public class OrderJournal implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /** 业务唯一标识，包括订单 */
    @Column(name="biz_id")
    private String bizId;

    @Column(name="user_id")
    private String userId;

    /** 账单类型 */
    @Column(name="bill_type")
    private Integer billType;

    /** 支付类型 */
    @Column(name="pay_type")
    private Integer payType;

    @Column(name="amount")
    private Long amount;

    @Column(name="remark")
    private String remark;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    private LocalDateTime updateTime;

    public @interface Update {}

    public static OrderJournal build(String bizId, String userId, Integer billType, Integer payType, Long amount, String remark) {
        OrderJournal orderJournal = new OrderJournal();
        orderJournal.setBizId(bizId);
        orderJournal.setUserId(userId);
        orderJournal.setBillType(billType);
        orderJournal.setPayType(payType);
        orderJournal.setAmount(amount);
        orderJournal.setRemark(remark);
        LocalDateTime current = LocalDateTime.now();
        orderJournal.setCreateTime(current);
        orderJournal.setUpdateTime(current);
        return orderJournal;
    }
}
