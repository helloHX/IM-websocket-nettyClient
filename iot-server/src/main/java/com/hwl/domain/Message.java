package com.hwl.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName Message
 * @Description TODO
 * @Author ASUS
 * @Date 2018/11/20 22:10
 **/
@Data
public class Message {
    private String sender;
    private String receiver;
    private Object body;
}
