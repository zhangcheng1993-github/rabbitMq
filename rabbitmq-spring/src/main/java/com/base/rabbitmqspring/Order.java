package com.base.rabbitmqspring;

/**
 * @ClassName Order
 * @Description TODO
 * @Author zhangCheng
 * @Date 2019/12/9 14:14
 * @Version 1.0
 */
public class Order {

    private String id;

    private String name;

    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
