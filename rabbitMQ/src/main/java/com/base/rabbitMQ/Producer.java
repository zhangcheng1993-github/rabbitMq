package com.base.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName 生产者
 * @Description TODO
 * @Author zhangCheng
 * @Date 2019/12/4 10:56
 * @Version 1.0
 */
public class Producer {

    public static void main(String[] args) {
        try {
            //获得连接
            Connection connection=ConnectionUtil.getConnection();
            //获取信道
            Channel channel =connection.createChannel();
            //交换机名称
            String EXCHANGE_NAME = "mq.exchange";

            //路由key名称
            String ROUTING_KEY = "mq.key";
            //消息
            String MSG="a rabbit msg";

            //4 指定我们的消息投递模式: 消息的确认模式
            channel.confirmSelect();

            //设置消息参数
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)  //消息持久化
                    .contentEncoding("UTF-8")  //设置消息编码
                    .expiration("6000")  //设置消息过期时间
                    .build();

            //发送消息(MSG) ,到交换器(EXCHANGE_NAME)上,带上路由key(ROUTING_KEY)
            //(名称|routingKey路由key|
            // mandatory:如果是false,消息没有找到对应的队列就自动删除,如果是true,就执行basic.return方法将消息返还给生产者|
            // immediate:为true时如果exchange在将消息route到queue时发现对应的queue上没有消费者，那么这条消息不会放入队列中。
            // 当与消息routeKey关联的所有queue(一个或多个)都没有消费者时，该消息会通过basic.return方法返还给生产者。)
            for (int i = 0; i < 1; i++) {
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,true,properties, (MSG + i).getBytes("UTF-8"));
            }

            //return监听器,如果发出的消息,没有找到队列,就做以下处理
            channel.addReturnListener(new ReturnListener(){
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("handleReturn");
                    System.out.println("replyCode:" + replyCode);
                    System.out.println("replyText:" + replyText);
                    System.out.println("exchange:" + exchange);
                    System.out.println("routingKey:" + routingKey);
                    System.out.println("properties:" + properties);
                    System.out.println("body:" + new String(body));
                }
            });

            //Confirm监听,监控消息是否投递到了rabbitMq服务器上
            channel.addConfirmListener(new ConfirmListener(){
                public void handleAck(long l, boolean b) throws IOException {
                    System.out.println(String.format("投递成功，标识：%d", l));
                }

                public void handleNack(long l, boolean b) throws IOException {
                    System.out.println("投递失败，标识：" + l);
                }
            });
            //关闭信道
            channel.close();
            //关闭连接
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
