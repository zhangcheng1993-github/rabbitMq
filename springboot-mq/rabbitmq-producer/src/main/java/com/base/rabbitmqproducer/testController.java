package com.base.rabbitmqproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName testController.java
 * @Description TODO
 * @createTime 2019/12/13/ 22:50:00
 */
@Controller
public class testController {

    @Autowired
    public RabbitSender rabbitSender;


    @RequestMapping(value = "/testSenderOrder",method = RequestMethod.GET)
    public void  testSenderOrder() throws Exception{
        Order order=new Order();
        order.setId("1");
        order.setName("zc");
        Map<String,Object> propertiesMap=new HashMap<>();
        rabbitSender.sendObj(order,propertiesMap);
    }
}
