package com.hwl.domain;

import com.alibaba.fastjson.JSON;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @ClassName DeviceShadow
 * @Description 设备在服务器上的影子
 * @Author huangWenLong
 * @Date 2018/12/11 22:32
 **/
@Slf4j
@Data
public class DeviceShadow {
    private ConcurrentLinkedDeque<Message> messageCache = new ConcurrentLinkedDeque<>();
    private String deviceId;
    private String secret;
    private boolean cacheMessage;
    public volatile boolean online;
    public volatile Channel channel;
    private ClientType clientType;

    /**
     * @Author huangwenlong
     * @Description 向设备影子发送数据
     * @Date 23:42 2018/12/11
     * @Param [message]
     * @return void
     **/
    public void sentMessage(Message message) {
        if(online && channel.isOpen()){
            switch (clientType){
                case NETTY_CLIENT:
                    sentMsgToNettyClient(message);
                    break;
                case WEBSOCKET_CLIENT:
                    sentMsgToWSClient(message);
                    break;
            }
        }else{
            if(cacheMessage){
                cacheMessage(message);
            }else{
                log.info("device :" + deviceId + "in offline recevie message throw away" + message);
            }

        }
    }

    /**
     * @Author huangwenlong
     * @Description 将对不在线站点的信息缓存到队列队列中，等待站点登录后补发,此处可以替换到redis中
     * @Date 22:52 2018/12/11
     * @Param [message]
     * @return void
     **/
    public void cacheMessage(Message message){
        messageCache.add(message);
    }

    /**
     * 将缓存在影子中的消息及时下发
     */
    public void sentCacheMessage(){
        List<Message> copy = new ArrayList<>(messageCache);
        messageCache.clear();
        if(this.online){
            copy.forEach(this::sentMessage);
        }
    }


    public void onLine(String sign, Channel channel,ClientType clientType){
        this.online = true;
        this.channel = channel;
        this.clientType = clientType;
        sentCacheMessage();
    }
    /**
     * @return void
     * @Author huangwenlong
     * @Description 将消息转发给Netty客户端
     * @Date 20:41 2018/11/22
     * @Param [to, from, body]
     **/
    public void sentMsgToNettyClient(Message message) {

        if (channel != null) {
            NettyMessage nettyMessage = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.INSTANT_MESSAGING.value());
            header.setReceiver(deviceId);
            header.setSender(message.getSender());
            nettyMessage.setBody(message.getBody());
            nettyMessage.setHeader(header);
            channel.writeAndFlush(nettyMessage);
        }
    }
    /**
     * @Author huangwenlong
     * @Description 将消息转发给Websocket客户端
     * @Date 20:42 2018/11/22
     * @Param [to, from, body]
     * @return void
     **/
    public void sentMsgToWSClient(Message message){
        if(channel != null){
            WSMessage wsMessage = new WSMessage();
            wsMessage.setType(WSMessageType.INSTANT_MESSAGING.value());
            wsMessage.setReceiver(message.getReceiver());
            wsMessage.setSender(deviceId);
            wsMessage.setBody(message.getBody());
            channel.writeAndFlush(createWSMessage(wsMessage));
        }
    }

    /**
     * @Author huangwenlong
     * @Description 封装针对WebClient的消息
     * @Date 22:50 2018/12/11
     * @Param [o]
     * @return io.netty.handler.codec.http.websocketx.TextWebSocketFrame
     **/
    public TextWebSocketFrame createWSMessage(Object o){
        String message = JSON.toJSON(o).toString();
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message);
        return textWebSocketFrame;
    }

}
