package com.base.rabbitmqproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName RabbitSender.java
 * @Description TODO
 * @createTime 2019/12/13/ 22:29:00
 */
@Component
public class RabbitSender {

    @Autowired
    public RabbitTemplate rabbitTemplate;


    //确认模式,发送给mq服务器之后,返回结果(服务器签收或者不签收)
    final RabbitTemplate.ConfirmCallback confirmCallback=new RabbitTemplate.ConfirmCallback(){
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("correlationData:"+correlationData);
            System.err.println("ack:"+ack);
            if (!ack){
                System.err.println("异常处理......");
            }
        }
    };


    //返回模式,如果发送到服务器之后,没有找到对应的队列,就执行这个方法
    final RabbitTemplate.ReturnCallback returnCallback=new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange,
                                    String routingKey) {
            System.err.println("return exchange:"+exchange+",routingKey:"+routingKey+",replyCode:"+replyCode+",replyText:"+replyText+
                    ",消息是:"+message);
        }
    };


    //发送对象方法
    public void sendObj(Object obj, Map<String,Object> propertiesMap) throws Exception{

        //将对象转成json字符
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(obj);

        //设置消息头
        MessageProperties messageProperties = new MessageProperties();
        //消息的类型为application/json
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        //设置消息头自定义属性
        messageProperties.setHeader("num",123);

        //声明消息(json.getBytes():消息主体,messageProperties:消息头)
        Message message=new Message(json.getBytes(),messageProperties);

        //唯一标识,用来confirmCallback的时候,区分是那条消息
        CorrelationData correlationData=new CorrelationData(UUID.randomUUID()+"");

        //发送消息
        this.rabbitTemplate.convertAndSend("topic001","springboot.abc",message,correlationData);
        //设置确认回调
        this.rabbitTemplate.setConfirmCallback(confirmCallback);
        //设置返回回调
        this.rabbitTemplate.setReturnCallback(returnCallback);
    }
}
