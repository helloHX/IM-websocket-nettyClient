package com.hwl.cache;

import com.hwl.domain.ClientType;
import com.hwl.domain.DeviceShadow;
import io.netty.channel.Channel;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName ChannelManager
 * @Description 管理连接在Netty服务器上的站点
 * @Author huangwenlong
 * @Date 2018/11/20 20:45
 **/
public class ChannelManager {

    /**
     * 可通过redis，db替换
     */
    private Map<String,DeviceShadow> allNode = new ConcurrentHashMap<>();

    //通过客户端连接的结点与通道id的映射关系
    private Set<String> nodeCheck = ConcurrentHashMap.<String> newKeySet();
    //同websocket连接结点id与通道id的映射关系
//    private Set<String> wsNodeCheck = ConcurrentHashMap.<String> newKeySet();
    //客户端结点的白名单
    private List<String> whiteList = new CopyOnWriteArrayList<>();
    //websocket的白名单
//    private List<String> wsWhiteList = new CopyOnWriteArrayList<>();

    public boolean isRegistered(String sign){
        return nodeCheck.contains(sign);
    }

    public boolean isWhiteCount(String sign) {
        return whiteList.contains(sign);
    }
    /**
     * 加载并促使化DeviceShadow，可以通过读redis或者是数据库中的内容替换此处的初始化
     */
    private ChannelManager() {
        //结点信息模拟
        DeviceShadow deviceShadow1 = new DeviceShadow();
        deviceShadow1.setCacheMessage(false);
        deviceShadow1.setDeviceId("t1");
        deviceShadow1.setCacheMessage(true);
        deviceShadow1.setSecret("123456");

        DeviceShadow deviceShadow2 = new DeviceShadow();
        deviceShadow2.setCacheMessage(false);
        deviceShadow2.setCacheMessage(true);
        deviceShadow2.setDeviceId("from");
        deviceShadow2.setSecret("123456");

        allNode.put(deviceShadow1.getDeviceId(), deviceShadow1);
        allNode.put(deviceShadow2.getDeviceId(), deviceShadow2);
        whiteList.add("t1");
        whiteList.add("from");
    }

    private static class SingletonHolder{
        private static ChannelManager instance = new ChannelManager();
    }

    public static ChannelManager getInstance(){
        return SingletonHolder.instance;
    }



    /**
     * @return void
     * @Author huangwenlong
     * @Description 将登陆的账号保存在内存
     * @Date 20:57 2018/11/20
     * @Param [sender, channelId]
     **/
    public void register(String sign, Channel channel,ClientType clientType) {
        nodeOnline(sign, channel,clientType);
        nodeCheck.add(sign);
    }

    /**
     *
     * @param sign
     */
    public void loginOut(String sign) {
        DeviceShadow deviceShadow = allNode.get(sign);
        deviceShadow.online = false;
        nodeCheck.remove(sign);
    }

    /**
     * @return void
     * @Author huangwenlong
     * @Description websocket client 的注册
     * @Date 17:14 2018/11/22
     * @Param [sender, channel]
     **/
//    public void wsRegister(String sign, Channel channel) {
//        nodeOnline(sign, channel,ClientType.WEBSOCKET_CLIENT);
//        wsNodeCheck.add(sign);
//    }


    /**
     * @Author huangwenlong
     * @Description 标记设备下线
     * @Date 16:21 2018/12/12
     * @Param [sign]
     * @return void
     **/
    public void nodeOffline(String sign){
        if(sign != null) {
            loginOut(sign);
        }
    }

    /**
     * @Author huangwenlong
     * @Description 标记设备上线
     * @Date 16:21 2018/12/12
     * @Param [sign, channel, clientType]
     * @return void
     **/
    public void nodeOnline(String sign, Channel channel,ClientType clientType){
        DeviceShadow deviceShadow = allNode.get(sign);
        deviceShadow.onLine(sign, channel, clientType);
    }

    /**
     * @return io.netty.channel.Channel
     * @Author huangwenlong
     * @Description 通过标志获取channel
     * @Date 21:41 2018/11/20
     * @Param [sender]
     **/
    public DeviceShadow getChannel(String sign) {
        return allNode.get(sign);
    }

    /**
     * @Author huangwenlong
     * @Description 判断ws结点是否已经注册
     * @Date 16:22 2018/12/12
     * @Param [sign]
     * @return boolean
     **/
//    public boolean isWSRegistered(String sign) {
//        return wsNodeCheck.contains(sign);
//    }

    /**
     * @return boolean
     * @Author huangwenlong
     * @Description 判断是否在webSocket白名单上
     * @Date 20:17 2018/11/22
     * @Param [sender]
     **/
//    public boolean isWSWhiteList(String sign) {
//        return wsWhiteList.contains(sign);
//    }

    /**
     * @return io.netty.channel.Channel
     * @Author huangwenlong
     * @Description 通过sign 获取websocketChannel
     * @Date 20:36 2018/11/22
     * @Param [sender]
     **/
//    public Channel getWSChannel(String sign) {
//        ChannelId id = wsNodeCheck.get(sign);
//        if (id != null) {
//            return wsGroup.find(id);
//        } else {
//            return null;
//        }
//    }
}
