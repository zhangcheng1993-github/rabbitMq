package com.base.rabbitmqconsumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName RabbitReceiver.java
 * @Description TODO
 * @createTime 2019/12/13/ 23:13:00
 */
@Component
public class RabbitReceiver {

    //创建交换器和队列,并且设置绑定关系
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value ="queue001",durable = "true"),
                    exchange = @Exchange(value = "topic001",durable = "true",type = "topic",ignoreDeclarationExceptions = "true"),
                    key = "springboot.*"
            )
    )
    //这个是消息处理方法
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, @Headers Map<String,Object> headers, Channel channel) throws Exception{

        System.err.println("消费端："+order.getId());
        System.err.println("自定义属性："+headers.get("num"));
        //获取当前消息的deliveryTag
        Long deliveryTag=(Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //通过deliveryTag,手工ack
        channel.basicAck(deliveryTag,false);

    }
}
