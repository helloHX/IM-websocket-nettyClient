package com.hwl.customer;

import com.hwl.context.Context;
import com.hwl.entity.NettyMessage;
import com.hwl.handler.MyChannelDuplexHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName CustomerHandler
 * @Description TODO
 * @Author huangWenLong
 * @Date 2018/12/13 20:37
 **/
@Slf4j
@ChannelHandler.Sharable
public class CustomerHandler extends MyChannelDuplexHandler {

    @Override
    public void LoginFinish(Channel channel) {
        log.info("登录成功");
       channel.writeAndFlush(context.createNettyMessage("server","你好太阳！！这里是地球"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        System.out.println("地球收到太阳的消息:" + msg);
        ctx.writeAndFlush(context.createNettyMessage(nettyMessage.getHeader().getSender(),"地球收到" + nettyMessage.getBody()));
    }
}
