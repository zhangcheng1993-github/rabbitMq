package com.base.rabbitmqspring;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName RabbitMQConfig.java
 * @Description TODO
 * @createTime 2019/12/08/ 13:09:00
 */
@Configuration
@ComponentScan({"com.base.rabbitmqspring"})
public class RabbitMQConfig {

    //声明连接工厂
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1:5672");
        connectionFactory.setUsername("zhangcheng");
        connectionFactory.setPassword("zhangcheng");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    //声明rabbitAdmin
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    //声明RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }




    //声明一个交换器(名称:topic001)
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    //声明一个队列(名称:queue001)
    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    //声明一个绑定,queue001队列绑定到topic001交换器上,路由规则是spring.*
    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }


    //声明一个队列(名称:queue002)
    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    //声明一个绑定,queue002队列绑定到topic001交换器上,路由规则是mq.*
    @Bean
    public Binding binding002() {
        //同一个Exchange绑定了2个队列
        return BindingBuilder.bind(queue002()).to(exchange001()).with("mq.*");
    }



    //声明一个交换器(名称:topic002)
    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    //声明一个队列(名称:queue003)
    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }

    //声明一个绑定,queue003队列绑定到topic002交换器上,路由规则是rabbit.*
    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange002()).with("rabbit.*");
    }


    //声明消费监听容器,实际上就是用来设置消费者属性,并且消费消息的
//    @Bean
//    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
//        //创建容器
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        //添加多个队列进行监听
//        container.setQueues(queue001(), queue002(), queue003());
//        //设置多个并发消费者一起消费，并支持运行时动态修改。
//        container.setConcurrentConsumers(1);
//        //最大消费者数量
//        container.setMaxConcurrentConsumers(5);
//        //设置重回队列，一般设置false
//        container.setDefaultRequeueRejected(false);
//        //设置自动签收机制
//        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
//        //设置listener外露
//        container.setExposeListenerChannel(true);
//
//        //消费端标签生成策略(生成每个消费者的标签)
//        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
//            @Override
//            public String createConsumerTag(String queue) {
//                //每个消费端都有自己独立的标签
//                return queue + "_" + UUID.randomUUID().toString();
//            }
//        });

        //消息监听(消息来了,做什么操作,实际上就算消费消息)
//        container.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("----------消费者: " + message.getMessageProperties().getConsumerTag() + ",消费消息:" + msg);
//            }
//        });

//
        //适配器模式
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        container.setMessageListener(adapter);
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);
//
//
//         //2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
//
//        //声明一个适配器
//         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//         //设置转换器
//         adapter.setMessageConverter(new TextMessageConverter());
//         //声明队列和消费方法对应的Map
//         Map<String, String> queueOrTagToMethodName = new HashMap<>();
//         queueOrTagToMethodName.put("queue001", "method1");
//         queueOrTagToMethodName.put("queue002", "method2");
//         //队列名称 和 方法名称对应,就就算说,对应的队列消息被监听到了,会被不同的方法消费
//         adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//         //给容器设置监听(传入适配器对象)
//         container.setMessageListener(adapter);
//
//        return container;
//    }


//    @Bean   //二进制消息自动转化为java对象(map或者list)
//    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setQueues(queue001(), queue002(), queue003());    //监听的队列
//        container.setConcurrentConsumers(1);    //当前的消费者数量
//        container.setMaxConcurrentConsumers(5); //  最大的消费者数量
//        container.setDefaultRequeueRejected(false); //是否重回队列
//        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //签收模式
//        container.setExposeListenerChannel(true);
//        container.setConsumerTagStrategy(new ConsumerTagStrategy() {    //消费端的标签策略
//            @Override
//            public String createConsumerTag(String queue) {
//                return queue + "_" + UUID.randomUUID().toString();
//            }
//        });

          //支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMapMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
//        return container;
//    }


//    @Bean   //二进制消息自动转化为java对象(由生产者决定转换为具体的哪个java对象)
//    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setQueues(queue001(), queue002(), queue003());    //监听的队列
//        container.setConcurrentConsumers(1);    //当前的消费者数量
//        container.setMaxConcurrentConsumers(5); //  最大的消费者数量
//        container.setDefaultRequeueRejected(false); //是否重回队列
//        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //签收模式
//        container.setExposeListenerChannel(true);
//        container.setConsumerTagStrategy(new ConsumerTagStrategy() {    //消费端的标签策略
//            @Override
//            public String createConsumerTag(String queue) {
//                return queue + "_" + UUID.randomUUID().toString();
//            }
//        });
//
//        // 4  DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        //指定消费方法
//        adapter.setDefaultListenerMethod("consumeJavaTypeMapperMessage");
//        //声明转换器
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        //声明对象映射器
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//
//        //设置具体的映射关系
//        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
//        idClassMapping.put("order", Order.class);
//        idClassMapping.put("packaged", Packaged.class);
//        javaTypeMapper.setIdClassMapping(idClassMapping);
//
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
//        return container;
//    }


        @Bean   //二进制消息自动转化为java对象(由生产者决定转换为具体的哪个java对象)
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003());    //监听的队列
        container.setConcurrentConsumers(1);    //当前的消费者数量
        container.setMaxConcurrentConsumers(5); //  最大的消费者数量
        container.setDefaultRequeueRejected(false); //是否重回队列
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //签收模式
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {    //消费端的标签策略
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });

        // 4  DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        //指定消费方法
        adapter.setDefaultListenerMethod("consumeJavaTypeMapperMessage");
        //声明转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //声明对象映射器
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();


        //设置具体的映射关系
        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
        idClassMapping.put("order", Order.class);
        idClassMapping.put("packaged", Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMapping);

        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        return container;
    }



    //设置全局消息转换器,其实就算前面几种转换器,集中起来,设一定的规则,消息参数属于那种类型,就执行那个转换器
    @Bean
    public  SimpleMessageListenerContainer  messageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003());    //监听的队列
        container.setConcurrentConsumers(1);    //当前的消费者数量
        container.setMaxConcurrentConsumers(5); //  最大的消费者数量
        container.setDefaultRequeueRejected(false); //是否重回队列
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //签收模式
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {    //消费端的标签策略
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });

        // 创建适配器
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());


        //声明Json转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter =new Jackson2JsonMessageConverter();
        //声明映射规则
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("order",Order.class);
        idClassMapping.put("packaged",Packaged.class);
        //创建映射器
        DefaultJackson2JavaTypeMapper jackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //创建映射规则
        jackson2JavaTypeMapper.setIdClassMapping(idClassMapping);
        //给转换器设置映射器
        jackson2JsonMessageConverter.setJavaTypeMapper(jackson2JavaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);

        //声明自定义消息转换器
        TextMessageConverter textMessageConverter = new TextMessageConverter();


        //声明全局消息转换器,并且设置规则,消息参数是什么类型,就执行对应的转换器转换数据
        ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter = new ContentTypeDelegatingMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("text",textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("html/text",textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("xml/text",textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("text/plain",textMessageConverter);

        contentTypeDelegatingMessageConverter.addDelegate("json",jackson2JsonMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("application/json",jackson2JsonMessageConverter);

        adapter.setMessageConverter(contentTypeDelegatingMessageConverter);
        //设置处理器的消费消息的默认方法
        adapter.setDefaultListenerMethod("onMessage");
        container.setMessageListener(adapter);
        return container;
    }


}
