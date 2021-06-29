package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.Order;
import com.kunbu.pay.payment.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findAllByUserId(String userId);

    Product getById(Long id);

    @Query(value="select * from product where id in (:idList) ",nativeQuery = true)
    List<Product> getByProductIdList(@Param("idList") List<Long> idList);

}
