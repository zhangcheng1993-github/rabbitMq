package com.base.rabbitmqspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName EntFileTest.java
 * @Description TODO
 * @createTime 2019/12/08/ 14:55:00
 */
public class EntFileTest extends  RabbitmqSpringApplicationTests {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testAdmin(){
        //声明交换器
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        //声明队列
        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));



        // 第一个参数：具体的队列
        // 第二个参数：绑定的类型
        // 第三个参数：交换机
        // 第四个参数：路由key
        // 第五个参数：arguments 参数
        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE,
                "test.direct", "direct", new HashMap<>()));


        //BindingBuilder 链式编程
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.topic.queue", false))     //直接创建队列
                        .to(new TopicExchange("test.topic", false, false))  //直接创建交换机 建立关联关系
                        .with("user.#"));   //指定路由Key

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.fanout.queue", false))
                        .to(new FanoutExchange("test.fanout", false, false)));

        //清空队列数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);

    }




    @Test
    public void testSendMessage() throws Exception {

        //1 创建设置消息配置参数
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述..");
        messageProperties.getHeaders().put("type", "自定义消息类型..");

        //创建消息体,配置参数
        Message message = new Message("根据路由规则,会发送到交换器topic001,队列:queue001上".getBytes(), messageProperties);

        //转换并发送消息到topic001交换器,带上路由key:spring.abc
        rabbitTemplate.convertAndSend("topic001","spring.abc",message);
        //转换并发送消息到topic001交换器,带上路由key:mq.abc
        rabbitTemplate.convertAndSend("topic001","mq.abc","根据路由规则,会发送到交换器topic001,队列:queue003上");
        //转换并发送消息到topic002交换器,带上路由key:rabbit.abc
        rabbitTemplate.convertAndSend("topic002","rabbit.abc","根据路由规则,会发送到交换器topic002,队列:queue002上");
    }


    @Test
    public void testSendMessage2() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

        //send只能发送Message对象
        rabbitTemplate.send("topic001", "spring.abc", message);

        //convertAndSend转换后再发送,可以发送object
        rabbitTemplate.convertAndSend("topic001", "mq.abc", "hello object message send!");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
    }

    @Test
    public void testSendMessage4Text() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }



    @Test
    public void testSendJsonMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("test1001消息订单");
        order.setContent("test1001订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.out.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }


    @Test
    public void testSendJavaMessage() throws Exception {
        Order order = new Order();
        order.setId("1002");
        order.setName("test1002消息订单");
        order.setContent("test1002订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.out.println("order  json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        //注意这里要写你的实体类路径
        messageProperties.getHeaders().put("__TypeId__", "com.base.rabbitmqspring.Order");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }



    @Test
    public void testSendMappingMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Order order = new Order();
        order.setId("1001");
        order.setName("1001订单消息");
        order.setContent("1001订单描述信息");
        String json1 = mapper.writeValueAsString(order);
        System.out.println("order java: " + json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);

        Packaged pack = new Packaged();
        pack.setId("1002");
        pack.setName("1002包裹消息");
        pack.setDescription("1002包裹描述信息");
        String json2 = mapper.writeValueAsString(pack);
        System.out.println("pack  java: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }


}
