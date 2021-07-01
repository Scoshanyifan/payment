package com.kunbu.pay.payment.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.constant.BizTypeEnum;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.order.dao.SubOrderRepository;
import com.kunbu.pay.payment.order.dao.BizOrderRepository;
import com.kunbu.pay.payment.order.dao.ProductRepository;
import com.kunbu.pay.payment.order.entity.*;
import com.kunbu.pay.payment.order.entity.dto.OrderCreateDto;
import com.kunbu.pay.payment.order.entity.dto.OrderItemDto;
import com.kunbu.pay.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private BizOrderRepository bizOrderRepository;

    @Autowired
    private SubOrderRepository subOrderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;

    public ApiResult getOrderInfo(String bizOrderNo) {
        return ApiResult.success(bizOrderRepository.findByOrderNo(bizOrderNo));
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResult createOrder(OrderCreateDto orderCreateDto) {
        // 校验订单信息
        List<OrderItemDto> itemDtoList = orderCreateDto.getItems();
        if (CollectionUtil.isEmpty(itemDtoList)) {
            return ApiResult.failure("订单信息错误 bizOrder info error");
        }
        Set<Long> pidSet = Sets.newHashSet();
        for (OrderItemDto item : itemDtoList) {
            if (item.getProductId() == null) {
                return ApiResult.failure("商品信息缺失 good info none");
            } else {
                pidSet.add(item.getProductId());
            }
            if (item.getProductNumber() == null || item.getProductNumber() <= 0) {
                return ApiResult.failure("商品数量错误 good number error");
            }
        }
        List<BizProduct> bizProductList = productRepository.getByProductIdList(Lists.newArrayList(pidSet));
        log.info(">>> bizProductList:{}", bizProductList);
        if (CollectionUtil.isEmpty(bizProductList) || bizProductList.size() != pidSet.size()) {
            return ApiResult.failure("商品信息错误 good info error");
        }
        Map<Long, List<OrderItemDto>> pid2OrderItemMap = itemDtoList.stream().collect(Collectors.groupingBy(x -> x.getProductId()));
        Long totalAmount = 0L;
        for (BizProduct bizProduct : bizProductList) {
            if (!bizProduct.getProductStatus().equals(BizProduct.STATUS_UP)) {
                return ApiResult.failure("商品状态错误 good productStatus error");
            }
            for (OrderItemDto itemDto : pid2OrderItemMap.get(bizProduct.getId())) {
                totalAmount += itemDto.getProductNumber() * bizProduct.getProductPrice();
                itemDto.setProductName(bizProduct.getProductName());
                itemDto.setProductPrice(bizProduct.getProductPrice());
            }
        }

        String bizOrderNo = IdUtil.fastSimpleUUID();
        // 主订单
        BizOrder bizOrder = new BizOrder();
        bizOrder.setBizOrderNo(bizOrderNo);
        bizOrder.setUserId(orderCreateDto.getUserId());
        bizOrder.setOrderAmount(totalAmount);
        bizOrder.setBizType(BizTypeEnum.ORDER.getType());
        bizOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        LocalDateTime current = LocalDateTime.now();
        bizOrder.setCreateTime(current);
        bizOrder.setUpdateTime(current);
        bizOrderRepository.saveOrder(bizOrder);
        // 子订单
        List<SubOrder> orderItemList = Lists.newArrayList();
        for (OrderItemDto itemDto : itemDtoList) {
            SubOrder subOrder = new SubOrder();
            subOrder.setAmount(itemDto.getProductPrice() * itemDto.getProductNumber());
            subOrder.setSubOrderNo(IdUtil.fastSimpleUUID());
            subOrder.setSubOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
            subOrder.setBizOrderNo(bizOrderNo);
            subOrder.setProductId(itemDto.getProductId());
            subOrder.setProductName(itemDto.getProductName());
            subOrder.setProductNumber(itemDto.getProductNumber());
            subOrder.setProductPrice(itemDto.getProductPrice());
            subOrder.setRealPrice(itemDto.getProductPrice());
            subOrder.setUserId(orderCreateDto.getUserId());
            subOrder.setCreateTime(current);
            subOrder.setUpdateTime(current);
            orderItemList.add(subOrder);
        }
        subOrderRepository.saveAll(orderItemList);
        // TODO 定时任务，15min关闭订单
        return ApiResult.success(bizOrderNo);
    }

    public ApiResult payOrder(String bizOrderNo, String userId, Integer payType) {
        if (payType == null || PayTypeEnum.of(payType) == null) {
            return ApiResult.failure("订单信息错误 bizOrder info error");
        }
        BizOrder bizOrder = bizOrderRepository.findByOrderNo(bizOrderNo);
        if (bizOrder == null || !bizOrder.getUserId().equals(userId)) {
            log.error(">>> payOrder bizOrder error, bizOrderNo:{}, userId:{}", bizOrderNo, userId);
            return ApiResult.failure("订单不存在 bizOrder none");
        }
        if (!OrderStatusEnum.WAIT_PAY.getStatus().equals(bizOrder.getOrderStatus())) {
            return ApiResult.failure("订单状态异常 bizOrder productStatus error");
        }
        int upRes = bizOrderRepository.updateOrderPayType(payType, bizOrder.getBizOrderNo(), OrderStatusEnum.WAIT_PAY.getStatus());
        if (upRes <= 0) {
            log.error(">>> payOrder updateOrderPayType failure, bizOrder:{}", bizOrder);
            return ApiResult.failure("订单更新失败 bizOrder update failure");
        } else {
            // 调用支付 TODO MQ
            String sign = paymentService.paySign(bizOrderNo, payType);
            log.info(">>> payOrder sign:{}", sign);
            return ApiResult.success(sign);
        }
    }

}
