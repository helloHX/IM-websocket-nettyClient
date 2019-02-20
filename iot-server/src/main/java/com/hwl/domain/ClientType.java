package com.hwl.domain;

public enum ClientType {

    NETTY_CLIENT((byte)0),WEBSOCKET_CLIENT((byte)1);
    private byte value;

    private ClientType(byte value) {
        this.value = value;
    }

    public byte getValue(){
       return this.value;
    }
}
