package com.kunbu.pay.payment.order;

import cn.hutool.http.server.HttpServerRequest;
import com.kunbu.pay.payment.biz.ali.AlipayConstant;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.dao.ProductRepository;
import com.kunbu.pay.payment.order.entity.dto.OrderCreateDto;
import com.kunbu.pay.payment.util.PropertyPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @ResponseBody
    @GetMapping("/get")
    public ApiResult getOrderInfo(@RequestParam String bizOrderNo) {
        return orderService.getOrderInfo(bizOrderNo);
    }

    @ResponseBody
    @GetMapping("/product")
    public ApiResult getProductInfo(@RequestParam Long productId) {
        return ApiResult.success(productRepository.getByProductId(productId));
    }

    @PostMapping("/create")
    @ResponseBody
    public ApiResult createOrder(@RequestBody OrderCreateDto orderCreateDto, HttpServerRequest request) {
        log.info(">>> createOrder, order:{}", orderCreateDto);
        orderCreateDto.setUserId("2233");
        return orderService.createOrder(orderCreateDto);
    }

    @GetMapping("/pay")
    public void pay(@RequestParam String bizOrderNo, @RequestParam Integer payType, HttpServletResponse response) throws Exception {
        ApiResult payResult = orderService.payOrder(bizOrderNo, "2233", payType);
        if (payResult.isSuccess()) {
            response.setContentType("text/html;charset=" + PropertyPayUtil.getValue(AlipayConstant.CONFIG_CHARSET));
            response.getWriter().write(payResult.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

}
