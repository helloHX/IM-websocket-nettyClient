package com.hwl.domain;

public enum WSMessageType {

    LOGIN_REQ(
            (String) "login_req"), LOGIN_RESP((String) "login_resp"),INSTANT_MESSAGING((String) "instant_message");

    private String value;

    private WSMessageType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
