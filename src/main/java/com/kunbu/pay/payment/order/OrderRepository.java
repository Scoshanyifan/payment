package com.kunbu.pay.payment.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByUserId(String userId);

    Order findFirstByOrderId(String orderId);

    @Modifying
    @Query(value="update order set order_status = ?1 where order_id =?2 and order_status = ?3",nativeQuery = true)
    int updateOrderStatus(Integer newStatus, String orderId, Integer oldStatus);


}
