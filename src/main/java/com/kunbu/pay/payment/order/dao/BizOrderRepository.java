package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.BizOrder;
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
public interface BizOrderRepository extends JpaRepository<BizOrder, Long>, JpaSpecificationExecutor<BizOrder> {

    @Query(value="select * from biz_order where user_id =?1",nativeQuery = true)
    List<BizOrder> findAllByUserId(String userId);

    @Query(value="select * from biz_order where biz_order_no =?1",nativeQuery = true)
    BizOrder findByOrderNo(String bizOrderNo);

    @Transactional
    @Modifying
    @Query(value="update biz_order set order_status = ?1 where biz_order_no =?2 and order_status = ?3",nativeQuery = true)
    int updateOrderStatus(Integer newStatus, String orderId, Integer oldStatus);

    @Transactional
    @Modifying
    @Query(value="update biz_order set pay_type = ?1 where biz_order_no =?2 and order_status = ?3",nativeQuery = true)
    int updateOrderPayType(Integer payType, String orderId, Integer status);

    @Transactional
    @Modifying
    @Query(value = "insert into biz_order (user_id, biz_order_no, order_amount, order_status, biz_type, create_time, update_time) " +
            "values (:#{#bizOrder.userId}, :#{#bizOrder.bizOrderNo}, :#{#bizOrder.orderAmount}, :#{#bizOrder.orderStatus}, :#{#bizOrder.bizType}, :#{#bizOrder.createTime}, :#{#bizOrder.updateTime})",nativeQuery = true)
    void saveOrder(@Param("bizOrder") BizOrder bizOrder);

}
