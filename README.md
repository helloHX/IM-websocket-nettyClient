#功能
支持iot-client客户端与web客户端（websocket）简单通信
#用法
##服务端启动
根据需求在iot-server配置服务器信息,
然后运行启动iot-server中的App.java

```aidl
Configuration configuration = new Configuration();
        configuration.setHeartBeatRate(5000);
        configuration.setIp("localhost");
        configuration.setPort(1212);
        configuration.setWPort(1224);
        configuration.setWsPath("ws//localhost:1224/websocket");
        configuration.setSocketServerEnable(true);
        configuration.setWSocketServerEnable(true);
        SocketServer socketServer = new SocketServer(configuration);
        socketServer.start();
```
#websocket
web端开启websocket连接到服务器，
按下列格式向服务器发送消息
##注册
```
{
    "type":"login_req",
    "senderId":5,
    "receiverId":0,
    "body":null
 }
 ```
 ##成功注册响应
 ```$xslt
{
    "type":"login_resp",
    "senderId":0,
    "receiverId":5,
    "body":true
    }
```
##失败注册响应
```$xslt
{
    "type":"login_resp",
    "senderId":0,
    "receiverId":5,
    "body":false
    }
```

##发送消息
```
{
    "type":"instant_message",
    "senderId":5,
    "receiverId":6,
    "body":"message content"
 }
 ```
 # nettyClient
实现 MyChannelDuplexHandler，在其中添加自己对消息的处理逻辑。
配置修改iot-client中App.java中的配置，运行iot-client的App。
 ```aidl
LoginConfiguration loginConfiguration = new LoginConfiguration();
        loginConfiguration.setDeviceID("t1");
        loginConfiguration.setDeviceSecret("123456");
        Configuration configuration = new Configuration("localhost", 1212,
                "localhost", 1223);
        configuration.setLoginConfiguration(loginConfiguration);
        configuration.addChannelHandler("customer", new CustomerHandler());
        IOTA iota = new IOTA(configuration);
        new Thread(iota::bind).start();
```
 
