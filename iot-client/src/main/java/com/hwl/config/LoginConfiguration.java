package com.hwl.config;

import lombok.Data;

/**
 * @ClassName LoginConfiguration
 * @Description 网关登录配置信息
 * @Author huangWenLong
 * @Date 2018/12/10 17:01
 **/
@Data
public class LoginConfiguration {
    private String deviceID;
    private String deviceSecret;
}
