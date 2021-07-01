package com.kunbu.pay.payment.order.dao;

import com.kunbu.pay.payment.order.entity.BizProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<BizProduct, Long>, JpaSpecificationExecutor<BizProduct> {

    List<BizProduct> findAllByUserId(String userId);

    @Query(value="select * from biz_product where id = ?1 ",nativeQuery = true)
    BizProduct getByProductId(Long id);

    @Query(value="select * from biz_product where id in (:idList) ",nativeQuery = true)
    List<BizProduct> getByProductIdList(@Param("idList") List<Long> idList);

}
