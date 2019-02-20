package com.hwl.config;

import lombok.Data;

/**
 * @ClassName Configuration
 * @Description TODO
 * @Author huangWenLong
 * @Date 2018/12/11 16:14
 **/
@Data
public class Configuration {
    private String ip;
    private boolean wSocketServerEnable;
    private boolean socketServerEnable;
    //心跳评率
    private int heartBeatRate = 5000;
    private int port;
    private int wPort;
    private String wsPath;
}
