package com.hwl.handler;

import com.hwl.context.Context;
import com.hwl.customer.DataSentServer;
import com.hwl.domain.Message;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyMsgForwardHandler
 * @Description 自定义消息处理器
 * @Author huangwenlong
 * @Date 2018/11/20 17:43
 **/
@Slf4j
@ChannelHandler.Sharable
public class NettyMsgForwardHandler extends ChannelInboundHandlerAdapter {

    private Context context;

    public NettyMsgForwardHandler(Context context) {
        this.context = context;
    }

    /**
     * @Author huangwenlong
     * @Description 自定义消息读取处理,并转发
     * @Date 19:48 2018/11/20
     * @Param [ctx, msg]
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader().getType() == MessageType.INSTANT_MESSAGING.value()) {
            Object to = message.getHeader().getReceiver() ;
            String from = ctx.channel().attr(Constants.OWNER).get();

            Message nmsg = new Message();
            nmsg.setBody(message.getBody());
            nmsg.setReceiver(to.toString());
            nmsg.setSender(from);
            DataSentServer.sentMessage(nmsg);
        }
        log.info("receive a NettyMessage --->:" + message.getBody());
    }
}
