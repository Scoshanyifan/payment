package com.kunbu.pay.payment.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.constant.BizTypeEnum;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.order.dao.OrderItemRepository;
import com.kunbu.pay.payment.order.dao.OrderRepository;
import com.kunbu.pay.payment.order.dao.ProductRepository;
import com.kunbu.pay.payment.order.entity.*;
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
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;

    public ApiResult getOrderInfo(String orderId) {
        return ApiResult.success(orderRepository.findFirstByOrderId(orderId));
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResult createOrder(OrderCreateDto orderCreateDto) {
        // 校验订单信息
        List<OrderItemDto> itemDtoList = orderCreateDto.getItems();
        if (CollectionUtil.isEmpty(itemDtoList)) {
            return ApiResult.failure("订单信息错误 order info error");
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
        List<Product> productList = productRepository.getByProductIdList(Lists.newArrayList(pidSet));
        log.info(">>> productList:{}",productList);
        if (CollectionUtil.isEmpty(productList) || productList.size() != pidSet.size()) {
            return ApiResult.failure("商品信息错误 good info error");
        }
        Map<Long, List<OrderItemDto>> pid2OrderItemMap = itemDtoList.stream().collect(Collectors.groupingBy(x -> x.getProductId()));
        Long totalAmount = 0L;
        for (Product product : productList) {
            if (!product.getStatus().equals(Product.STATUS_UP)) {
                return ApiResult.failure("商品状态错误 good status error");
            }
            for (OrderItemDto itemDto : pid2OrderItemMap.get(product.getId())) {
                totalAmount += itemDto.getProductNumber() * product.getPrice();
                itemDto.setProductName(product.getName());
                itemDto.setProductPrice(product.getPrice());
            }
        }

        String orderId = IdUtil.fastSimpleUUID();
        // 主订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(orderCreateDto.getUserId());
        order.setOrderAmount(totalAmount);
        order.setBizType(BizTypeEnum.ORDER.getType());
        order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        LocalDateTime current = LocalDateTime.now();
        order.setCreateTime(current);
        order.setUpdateTime(current);
        orderRepository.saveOrder(order);
        // 子订单
        List<Orderitem> orderItemList = Lists.newArrayList();
        for (OrderItemDto itemDto : itemDtoList) {
            Orderitem orderitem = new Orderitem();
            orderitem.setAmount(itemDto.getProductPrice() * itemDto.getProductNumber());
            orderitem.setItemId(IdUtil.fastSimpleUUID());
            orderitem.setItemStatus(OrderStatusEnum.WAIT_PAY.getStatus());
            orderitem.setOrderId(orderId);
            orderitem.setProductId(itemDto.getProductId());
            orderitem.setProductName(itemDto.getProductName());
            orderitem.setProductNumber(itemDto.getProductNumber());
            orderitem.setProductPrice(itemDto.getProductPrice());
            orderitem.setRealPrice(itemDto.getProductPrice());
            orderitem.setUserId(orderCreateDto.getUserId());
            orderitem.setCreateTime(current);
            orderitem.setUpdateTime(current);
            orderItemList.add(orderitem);
        }
        orderItemRepository.saveAll(orderItemList);
        // TODO 定时任务，15min关闭订单
        return ApiResult.success(orderId);
    }

    public ApiResult payOrder(String orderId, String userId, Integer payType) {
        if (payType == null || PayTypeEnum.of(payType) == null) {
            return ApiResult.failure("订单信息错误 order info error");
        }
        Order order = orderRepository.findFirstByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            log.error(">>> payOrder order error, orderId:{}, userId:{}", orderId, userId);
            return ApiResult.failure("订单不存在 order none");
        }
        if (!OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
            return ApiResult.failure("订单状态异常 order status error");
        }
        int upRes = orderRepository.updateOrderPayType(payType, order.getOrderId(), OrderStatusEnum.WAIT_PAY.getStatus());
        if (upRes <= 0) {
            log.error(">>> payOrder updateOrderPayType failure, order:{}", order);
            return ApiResult.failure("订单更新失败 order update failure");
        } else {
            // 调用支付 TODO MQ
            String sign = paymentService.paySign(orderId, payType);
            log.info(">>> payOrder sign:{}", sign);
            return ApiResult.success(sign);
        }
    }

}
