package com.kunbu.pay.payment.order;

import cn.hutool.http.server.HttpServerRequest;
import com.kunbu.pay.payment.biz.ali.AlipayConstant;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.entity.OrderCreateDto;
import com.kunbu.pay.payment.util.PropertyPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public String createOrder(@RequestBody OrderCreateDto orderCreateDto, HttpServerRequest request) {
        log.info(">>> createOrder, order:{}", orderCreateDto);
        orderCreateDto.setUserId("2233");
        orderService.createOrder(orderCreateDto);

        return "pay";
    }

    @PostMapping("/pay")
    public void pay(@RequestParam String orderId, @RequestParam Integer payType, HttpServletResponse response) throws Exception {
        ApiResult payResult = orderService.payOrder(orderId, "2233", payType);
        if (payResult.isSuccess()) {
            response.setContentType("text/html;charset=" + PropertyPayUtil.getValue(AlipayConstant.CONFIG_CHARSET));
            response.getWriter().write(payResult.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

}
