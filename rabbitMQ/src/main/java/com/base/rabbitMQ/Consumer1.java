package com.base.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName Consumer1
 * @Description 消费者1
 * @Author zhangCheng
 * @Date 2019/12/4 14:09
 * @Version 1.0
 */
public class Consumer1 {

    public static void main(String[] args) {
        try {
            //获取连接
            Connection conn =ConnectionUtil.getConnection();
            //创建信道
            final  Channel channel =conn.createChannel();

            //交换机名称变量
            String EXCHANGE_NAME = "mq.exchange";
            //队列名变量
            String QUEUE_NAME = "queue_name";
            //路由key规则变量
            String ROUTING_KEY = "mq.#";


            Map<String, Object> agruments = new HashMap<String, Object>();
            // 设置队列最大消息数量为5
            agruments.put("x-max-length", 5);
            // 设置死信交换器
            agruments.put("x-dead-letter-exchange", "dlx.exchange");

            //创建交换器(名称|类型|durable是否持久化|autoDelete没有队列使用了,是否删除|arguments扩展参数)
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC,true,false,null);
            //创建队列(名称|durable是否持久化|exclusive是否排外|autodelete没有消费者使用了,是否删除|arguments扩展参数)
            channel.queueDeclare(QUEUE_NAME,true,false,false,agruments);
            //按照路由规则,绑定队列和交换器( 队列名称|交换器名称|路由规则)
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);


            //要进行死信队列的声明
            channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
            channel.queueDeclare("dlx.queue", true, false, false, null);
            channel.queueBind("dlx.queue", "dlx.exchange", "#");


            //获取指定的条数channel.basicQos(5)
            channel.basicQos(5);

            //声明消费方法
            Consumer consumer=new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    if (message.contains("3")){
                        // requeue：重新入队列，false：直接丢弃，相当于告诉队列可以直接删除掉
                        channel.basicNack(envelope.getDeliveryTag(),false, false);
                    }else {
                        //手动签收
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        System.out.println(message);
                    }

                }
            };
            //执行消费(队列名称|autoAck是否自动签收|callback消费者对象)
            channel.basicConsume(QUEUE_NAME,false,consumer);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
