package com.hwl.handler;

import com.alibaba.fastjson.JSON;
import com.hwl.cache.ChannelManager;
import com.hwl.context.Context;
import com.hwl.domain.ClientType;
import com.hwl.domain.WSMessage;
import com.hwl.domain.WSMessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import static com.hwl.handler.Constants.SERVER_NAME;

/**
 * @ClassName WSLoginAuthRespHandler
 * @Description 对websocket客户端进行验证
 * @Author HuangwWenlong
 * @Date 2018/11/22 15:39
 **/

@ChannelHandler.Sharable
@Slf4j
public class WSLoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private ChannelManager channelManager;
    private Context context;

    public WSLoginAuthRespHandler(Context context) {
        this.context = context;
        this.channelManager = this.context.getChannelManager();
    }

    /**
     * @return void
     * @Author huangwenlong
     * @Description 负责wsSocket Client 的注册
     * @Date 21:25 2018/11/23
     * @Param [channelHandlerContext, message]
     **/
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        WSMessage wsMessage = JSON.parseObject(message.toString(), WSMessage.class);
        //判断是不是注册请求
        if (WSMessageType.LOGIN_REQ.value().equals(wsMessage.getType())) {
            WSMessage respMessage = null;
            //如果已经登录则拒绝登录
            boolean ok = false;
            if (channelManager.isRegistered(wsMessage.getSender())) {
                respMessage = createLoginRespWSMessage(wsMessage.getSender(), false);
            } else {
                //登录账号是否处于白名单上
                if (channelManager.isWhiteCount(wsMessage.getSender())) {
                    ok = true;
                    respMessage = createLoginRespWSMessage(wsMessage.getSender(), true);
                } else {
                    //不在白名单上的站点拒绝连接
                    respMessage = createLoginRespWSMessage(wsMessage.getSender(), false);
                }
            }
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSON(respMessage).toString()));
            if(ok) {
                register(channelHandlerContext.channel(), wsMessage.getSender());
            }
        } else {
            //对于没有注册的结点不接受信息,并断开连接
            if (channelManager.isRegistered(wsMessage.getSender())) {
                channelHandlerContext.fireChannelRead(message);
            }else{
                channelHandlerContext.channel().writeAndFlush(createLoginRespWSMessage(wsMessage.getSender(), false));
                channelHandlerContext.close();
            }
        }
    }


    /**
     * @Author huangwenlong
     * @Description 建立channel对应的客户端的联系
     * @Date 20:13 2018/11/20
     * @Param [channel, signal]
     * @return void
     **/
    private void register(Channel channel, String signal){
        log.info("------***--- device (" + signal +") online ------****----" );
        channel.attr(Constants.OWNER).set(signal);
        channelManager.register(signal,channel, ClientType.WEBSOCKET_CLIENT);
    }

    /**
     * @Author huangwenlong
     * @Description 在channel关闭之前清理channelManager对channel的管理
     * @Date 20:01 2018/12/11
     * @Param [ctx]
     * @return void
     **/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelManager.nodeOffline(ctx.channel().attr(Constants.OWNER).get());
    }

    /**
     * @return com.icode.message_server.domain.WSMessage
     * @Author huangwenlong
     * @Description 向websocket 客户端发送连接结果
     * @Date 19:38 2018/11/22
     * @Param [receive, status]
     **/
    private WSMessage createLoginRespWSMessage(String receive, Boolean status) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setType(WSMessageType.LOGIN_RESP.value());
        wsMessage.setSender(SERVER_NAME);
        wsMessage.setReceiver(receive);
        wsMessage.setBody(status);
        return wsMessage;
    }
}
