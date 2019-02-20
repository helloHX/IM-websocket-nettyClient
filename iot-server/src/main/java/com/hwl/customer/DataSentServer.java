package com.hwl.customer;

import com.hwl.cache.ChannelManager;
import com.hwl.domain.DeviceShadow;
import com.hwl.domain.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DataSentServer
 * @Description 通过DataSentServer给连接到服务器上的设备发送信息
 * @Author huangWenLong
 * @Date 2018/12/11 21:40
 **/
@Slf4j
public class DataSentServer {

    public static final String SERVER_NAME = "server";

    //全局的通道管理器
    public static  ChannelManager channelManager = ChannelManager.getInstance();


    /**
     * @Author huangwenlong
     * @Description 指定消息接收者，与消息内容，发送消息
     * @Date 16:43 2018/12/12
     * @Param [receiver, data]
     * @return void
     **/
    public static void sentMessage(String receiver,Object data){
        Message message = new Message();
        message.setSender(SERVER_NAME);
        message.setReceiver(receiver);
        message.setBody(data);
        sentMessage(message);
    }

    /**
     * @Author huangwenlong
     * @Description 将消息发向设备
     * @Date 16:42 2018/12/12
     * @Param [message]
     * @return void
     **/
    public static void sentMessage(Message message){
        if(channelManager != null) {
            DeviceShadow deviceShadow = channelManager.getChannel(message.getReceiver());
            if (deviceShadow != null) {
                deviceShadow.sentMessage(message);
            } else {
                System.out.println("server received ------>" + message.getBody());
                log.error("device is not exists ,please send to an exist device");
            }
        }else{
            log.error("channelManager is null,please initial channelManner");
        }
    }
}
