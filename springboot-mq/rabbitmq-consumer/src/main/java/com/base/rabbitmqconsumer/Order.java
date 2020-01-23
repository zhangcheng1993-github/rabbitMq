package com.base.rabbitmqconsumer;

import java.io.Serializable;

/**
 * @author zc
 * @version 1.0.0
 * @ClassName Order.java
 * @Description TODO
 * @createTime 2019/12/13/ 23:14:00
 */
public class Order implements Serializable {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
