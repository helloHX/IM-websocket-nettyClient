#websocket
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
 ```# IM-websocket-nettyClient
