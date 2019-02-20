package com.hwl.context;

import com.hwl.config.Configuration;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import com.hwl.handler.MyChannelDuplexHandler;
import io.netty.channel.*;
import lombok.Getter;

/**
 * @ClassName Context
 * @Description 作为Agent运行的上下文环境
 * @Author huangWenLong
 * @Date 2018/12/10 17:23
 **/
public class Context {
    @Getter
    private Configuration configuration;

    public Context(Configuration configuration) {
        this.configuration = configuration;
    }


    /**
     * @Author huangwenlong
     * @Description 包装需要发送的信息
     * @Date 20:17 2018/12/13
     * @Param [receiver, data]
     * @return com.hwl.entity.NettyMessage
     **/
    public NettyMessage createNettyMessage(String receiver,Object data){
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.INSTANT_MESSAGING.value());
        header.setSender(this.configuration.getLoginConfiguration().getDeviceID());
        header.setSecret(this.configuration.getLoginConfiguration().getDeviceSecret());
        header.setReceiver(receiver);
        nettyMessage.setHeader(header);
        nettyMessage.setBody(data);
        return nettyMessage;
    }

    /**
     * @Author huangwenlong
     * @Description 跳过
     * @Date 21:35 2018/12/13
     * @Param [ctx]
     * @return void
     **/
    public void loginFinishNotify(Channel channel){
       configuration.getChannelHandlerMap().values()
                .forEach(channelHandler -> {
                    if(channelHandler instanceof MyChannelDuplexHandler) {
                        try {
                            ((MyChannelDuplexHandler) channelHandler).LoginFinish(channel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
