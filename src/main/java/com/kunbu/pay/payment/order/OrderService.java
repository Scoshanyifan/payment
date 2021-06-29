package com.kunbu.pay.payment.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.constant.BizTypeEnum;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.order.dao.OrderRepository;
import com.kunbu.pay.payment.order.dao.ProductRepository;
import com.kunbu.pay.payment.order.entity.Order;
import com.kunbu.pay.payment.order.entity.OrderCreateDto;
import com.kunbu.pay.payment.order.entity.OrderItemDto;
import com.kunbu.pay.payment.order.entity.Product;
import com.kunbu.pay.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(rollbackFor = Exception.class)
    public ApiResult createOrder(OrderCreateDto orderCreateDto) {
        // 校验订单信息
        List<OrderItemDto> itemDtoList = orderCreateDto.getItems();
        if (CollectionUtil.isEmpty(itemDtoList)) {
            return ApiResult.failure("订单信息错误");
        }
        List<Long> pidList = Lists.newArrayList();
        for (OrderItemDto item : itemDtoList) {
            if (item.getProductId() == null) {
                return ApiResult.failure("商品信息缺失");
            } else {
                pidList.add(item.getProductId());
            }
            if (item.getCount() <= 0) {
                return ApiResult.failure("商品数量错误");
            }
        }
        List<Product> productList = productRepository.getByProductIdList(pidList);
        if (CollectionUtil.isEmpty(productList) || productList.size() != pidList.size()) {
            return ApiResult.failure("商品信息错误");
        }
        Map<Long, OrderItemDto> pid2OrderItemMap = itemDtoList.stream().collect(Collectors.toMap(x -> x.getProductId(), x -> x));
        Long totalAmount = 0L;
        for (Product product : productList) {
            if (product.getStatus() != Product.STATUS_UP) {
                return ApiResult.failure("商品状态错误");
            }
            totalAmount += pid2OrderItemMap.get(product.getId()).getCount() * product.getPrice();
        }

        Order order = new Order();
        order.setOrderId(IdUtil.fastSimpleUUID());
        order.setUserId(orderCreateDto.getUserId());
        order.setOrderAmount(totalAmount);
        order.setBizType(BizTypeEnum.ORDER.getType());
        order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        LocalDateTime current = LocalDateTime.now();
        order.setCreateTime(current);
        order.setUpdateTime(current);
        orderRepository.saveOrder(order);


        // TODO 定时任务，15min关闭订单
        return ApiResult.success();
    }

    public ApiResult payOrder(String orderId, String userId, Integer payType) {
        if (payType == null || PayTypeEnum.of(payType) == null) {
            return ApiResult.failure("订单信息错误");
        }
        Order order = orderRepository.findFirstByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            log.error(">>> payOrder order error, orderId:{}, userId:{}", orderId, userId);
            return ApiResult.failure("订单不存在");
        }
        if (!OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
            return ApiResult.failure("订单状态异常");
        }
        int upRes = orderRepository.updateOrderPayType(payType, order.getOrderId(), OrderStatusEnum.WAIT_PAY.getStatus());
        if (upRes <= 0) {
            log.error(">>> payOrder updateOrderPayType failure, order:{}", order);
            return ApiResult.failure("订单更新失败");
        } else {
            // 调用支付 TODO MQ
            String sign = paymentService.paySign(orderId, payType);
            log.info(">>> payOrder sign:{}", sign);
            return ApiResult.success(sign);
        }
    }

}
