package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.Order;
import com.kunbu.pay.payment.order.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<Orderitem, Long>, JpaSpecificationExecutor<Orderitem> {

    List<Orderitem> findAllByUserId(String userId);

    List<Orderitem> findAllByOrderId(String orderId);

    @Modifying
    @Query(value="update order_item set status = ?1 where item_id =?2 and status = ?3",nativeQuery = true)
    int updateOrderItemStatus(Integer newStatus, String itemId, Integer oldStatus);


}
