package com.hwl.config;

import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Configuration
 * @Description Agent 启动配置信息
 * @Author huangWenLong
 * @Date 2018/12/10 16:48
 **/
@Data
public class Configuration {
    private String serverIp;
    private int serverPort;
    private String localIp;
    private int localPort;
    //心跳评率
    private int heartBeatRate = 5000;

    private Map<String,ChannelHandler> channelHandlerMap = new HashMap<>();

    public void addChannelHandler(String name,ChannelHandler channelHandler){
        channelHandlerMap.put(name, channelHandler);
    }

    public Configuration(String serverIp, int serverPort, String localIp, int localPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.localIp = localIp;
        this.localPort = localPort;
    }

    private LoginConfiguration loginConfiguration;
}
