package com.base.rabbitMQ;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName 连接工具类
 * @Description TODO
 * @Author zhangCheng
 * @Date 2019/12/4 10:46
 * @Version 1.0
 */
public class ConnectionUtil {

    public static Connection getConnection() throws IOException, TimeoutException {
        //传建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接ip地址
        factory.setHost("127.0.0.1");
        //设置端口
        factory.setPort(5672);
        //设置账户密码:
        factory.setUsername("zhangcheng");
        factory.setPassword("zhangcheng");
        //设置vhost
        factory.setVirtualHost("/");
        return factory.newConnection();
    }

}
