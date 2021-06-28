package com.kunbu.pay.payment.dao;

import com.kunbu.pay.payment.entity.OrderJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderJournalRepository extends JpaRepository<OrderJournal, Long>, JpaSpecificationExecutor<OrderJournal> {

    int countByBizIdAndPayTypeAndBillType(String bizId, Integer payType, Integer billType);

}
