package com.base.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName ConsumerDLX
 * @Description 死信监听
 * @Author zhangCheng
 * @Date 2019/12/5 15:48
 * @Version 1.0
 */
public class ConsumerDLX {

    private static final String QUEUE_NAME = "dlx.queue";

    public static void main(String[] args){
        try {
            //获取连接
            Connection conn =ConnectionUtil.getConnection();
            //创建信道
            final Channel channel =conn.createChannel();

            System.out.println("死信消费者启动 ..........");

            Consumer consumer=new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.err.println("死信队列接收到消息：" + new String(body));
                    System.err.println("deliveryTag:" + envelope.getDeliveryTag());
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            channel.basicConsume(QUEUE_NAME, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
