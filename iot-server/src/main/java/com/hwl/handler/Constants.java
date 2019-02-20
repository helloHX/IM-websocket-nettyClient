package com.hwl.handler;

import io.netty.util.AttributeKey;

/**
 * @ClassName Constants
 * @Description TODO
 * @Author huangwenlong
 * @Date 2018/11/22 21:03
 **/
public interface Constants {
    String SERVER_NAME = "server";
    String FROM = "from";
    String TO= "to";
    AttributeKey<String> OWNER = AttributeKey.valueOf(Constants.FROM);
}
