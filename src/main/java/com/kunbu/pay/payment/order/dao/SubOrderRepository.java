package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.SubOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubOrderRepository extends JpaRepository<SubOrder, Long>, JpaSpecificationExecutor<SubOrder> {

    @Modifying
    @Query(value="update sub_order set sub_order_status = ?1 where sub_order_no =?2 and sub_order_status = ?3",nativeQuery = true)
    int updateOrderItemStatusByNo(Integer newStatus, String subOrderNo, Integer oldStatus);

    @Modifying
    @Query(value="update sub_order set sub_order_status = ?1 where biz_order_no =?2 and sub_order_status = ?3",nativeQuery = true)
    int updateSubOrderStatusByBizOrder(Integer newStatus, String bizOrderNo, Integer oldStatus);

}
