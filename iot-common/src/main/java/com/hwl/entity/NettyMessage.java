package com.hwl.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NettyMessage {
    private Header header;
    private Object body;
}
