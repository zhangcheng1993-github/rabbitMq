package com.base.rabbitmqspring;

import java.util.Map;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName MessageDelegate.java
 * @Description TODO
 * @createTime 2019/12/08/ 19:44:00
 */
public class MessageDelegate {

    //普通消费方法,不分队列
    public void consumeMessage(byte[] messageBody) {
        System.err.println("默认方法, 消息内容:" + new String(messageBody));
    }

    //队列为queue001的消息被监听到了,执行此方法
    public void method1(String messageBody) {
        System.err.println("method1 收到消息内容:" + new String(messageBody));
    }

    //队列为queue002的消息被监听到了,执行此方法
    public void method2(String messageBody) {
        System.err.println("method2 收到消息内容:" + new String(messageBody));
    }

    //json格式字符串传过来,通过转换器,自动转成java对象map
    public void consumeMapMessage(Map messageBody) {
        System.err.println("map方法, 消息内容:" + messageBody);
    }

    //json格式字符串传过来,通过转换器和映射器,转换为具体对象
    public void consumeJavaTypeMapperMessage(Order order) {
        System.err.println("order对象, 消息内容, id: " + order.getId() +", name: " + order.getName() +
                ", content: "+ order.getContent());
    }
}
