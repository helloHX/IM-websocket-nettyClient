package com.hwl.handler;

import com.alibaba.fastjson.JSON;
import com.hwl.context.Context;
import com.hwl.customer.DataSentServer;
import com.hwl.domain.Message;
import com.hwl.domain.WSMessage;
import com.hwl.domain.WSMessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName WSMsgForwardHandler
 * @Description TODO
 * @Author HuangWenlong
 * @Date 2018/11/22 14:51
 **/
@Slf4j
@ChannelHandler.Sharable
public class WSMsgForwardHandler extends ChannelInboundHandlerAdapter {
    private Context context;

    public WSMsgForwardHandler(Context context) {
        this.context = context;
    }

    /**
     * @Author huangwenlong
     * @Description 自定义消息读取处理
     * @Date 19:48 2018/11/20
     * @Param [ctx, msg]
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        WSMessage wsMessage = JSON.parseObject((String) msg, WSMessage.class);
        if (WSMessageType.INSTANT_MESSAGING.value().equals(wsMessage.getType())) {

            Message message = new Message();
            message.setReceiver(wsMessage.getReceiver());
            message.setSender(wsMessage.getSender());
            message.setBody(wsMessage.getBody());
            DataSentServer.sentMessage(message);
            log.info("webSocket receive a string message --->:" + msg);
        }else{
            ctx.fireChannelRead(wsMessage);
        }
    }
}
