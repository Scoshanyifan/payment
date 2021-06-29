package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * order 是关键字
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value="select * from `order` where user_id =?1",nativeQuery = true)
    List<Order> findAllByUserId(String userId);

    @Query(value="select * from `order` where order_id =?1",nativeQuery = true)
    Order findFirstByOrderId(String orderId);

    @Transactional
    @Modifying
    @Query(value="update `order` set order_status = ?1 where order_id =?2 and order_status = ?3",nativeQuery = true)
    int updateOrderStatus(Integer newStatus, String orderId, Integer oldStatus);

    @Transactional
    @Modifying
    @Query(value="update `order` set pay_type = ?1 where order_id =?2 and order_status = ?3",nativeQuery = true)
    int updateOrderPayType(Integer payType, String orderId, Integer status);

    @Transactional
    @Modifying
    @Query(value = "insert into `order` (user_id, order_id, order_amount, order_status, biz_type, create_time, update_time) " +
            "values (:#{#order.userId}, :#{#order.orderId}, :#{#order.orderAmount}, :#{#order.orderStatus}, :#{#order.bizType}, :#{#order.createTime}, :#{#order.updateTime})",nativeQuery = true)
    void saveOrder(@Param("order") Order order);

}
