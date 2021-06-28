package com.kunbu.pay.payment.order;

import cn.hutool.core.util.IdUtil;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.dto.OrderCreateDto;
import com.kunbu.pay.payment.order.dto.OrderItemDto;
import com.kunbu.pay.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    public ApiResult createOrder(OrderCreateDto orderCreateDto) {
        Order order = new Order();
        order.setOrderId(IdUtil.fastSimpleUUID());
        order.setUserId("2233");
        Long totalAmount = 0L;
        for (OrderItemDto itemDto : orderCreateDto.getItems()) {
            totalAmount += itemDto.getCount() * itemDto.getPrice();
        }
        order.setOrderAmount(totalAmount);
        order.setBizType(BizTypeEnum.ORDER.getType());
        order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        order.setPayType(orderCreateDto.getPayType());
        LocalDateTime current = LocalDateTime.now();
        order.setCreateTime(current);
        order.setUpdateTime(current);
        orderRepository.save(order);

        // TODO 定时任务，15min关闭

        return ApiResult.success();
    }

    public ApiResult payOrder(String orderId, String userId) {
        // 查询订单，校验
        Order order = orderRepository.findFirstByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            log.error(">>> payOrder order error, orderId:{}, userId:{}", orderId, userId);
            return  ApiResult.failure("订单不存在");
        }
        
        // 调用支付 TODO MQ


        return ApiResult.success();
    }

}
