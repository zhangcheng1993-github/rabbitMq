package com.base.rabbitmqspring;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName TextMessageConverter.java
 * @Description TODO
 * @createTime 2019/12/08/ 20:40:00
 */
public class TextMessageConverter implements MessageConverter {

    //Object转换为msg
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    //msg对象转换为Object
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        if(null != contentType && contentType.contains("text")) {
            return new String(message.getBody());
        }
        return message.getBody();
    }
}
