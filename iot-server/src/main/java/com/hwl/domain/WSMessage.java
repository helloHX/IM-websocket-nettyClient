package com.hwl.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName WSMessage
 * @Description TODO
 * @Author ASUS
 * @Date 2018/11/22 16:28
 **/
@Setter
@Getter
@ToString
public class WSMessage {
    String type;
    String sender;
    String receiver;
    Object body;
}
