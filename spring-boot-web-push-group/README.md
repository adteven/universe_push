# Push Group
# 新版本IM服务
此服务将会改造成IM的后台服务，实现即时通讯基础接口服务,此项目将基于野火IM服务接口进行相应的改造





# 旧版本服务
提供群组推送相关api

## 基本功能
* 加入群组功能
* 退出群组功能
* 消息群发功能
* 私聊消息功能

## 协议说明

群组消息依赖与推送基本的消息指令,只是在body消息体设计相关的协议既可,同时提供http接口与二进制协议

### 消息体

* 信令名称
CONTACT 属于交互信令

* 请求消息体

> 群聊

```json
{
    "from":"source_token",
    "group":"comsince",
    "type":0,  //群聊消息
    "messageType":0,
    "message":"消息内容，可以自定义，方便用扩展图片，视频类消息"
}
```

> 私聊

```json
{
    "from":"source_token",
    "to":"des_token",
    "group":"comsince",
    "type":0,  //群聊消息
    "messageType":0,
    "message":"消息内容，可以自定义，方便用扩展图片，视频类消息"
}
```

* 响应消息体

```json
{
    "from":"source_token",
    "group":"comsince",
    "type":0,
    "message":"消息内容"
}
```