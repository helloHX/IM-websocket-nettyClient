package com.hwl.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public final class Header {
    private int crcCode = 0xabef0101;
    private int length;
    private long sessionID;
    private byte type;
    private String sender;
    private String secret;
    private String receiver;
    private byte priority;
    private Map<String, Object> attachment = new HashMap<String, Object>();

}
