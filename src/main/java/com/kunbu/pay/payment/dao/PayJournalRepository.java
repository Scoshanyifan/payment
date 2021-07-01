package com.kunbu.pay.payment.dao;

import com.kunbu.pay.payment.entity.PayJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PayJournalRepository extends JpaRepository<PayJournal, Long>, JpaSpecificationExecutor<PayJournal> {

    int countByBizIdAndPayTypeAndBillType(String bizId, Integer payType, Integer billType);

}
