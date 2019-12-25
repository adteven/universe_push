# Push Connector

**NOTE:** 部署时注意kiev-server.xml更新导致的无法预知的错误

## 通讯协议设计
推送服务与客户端交互统一采用的协议，协议主要包括消息头与消息体.对于不同的设备可以适当采取合适的鉴权协议

### 消息头
* 标识位 0xf8 代表推送协议开始
* 版本号 代表协议版本号，供以后升级判断使用
* 信令类型  
订阅，心跳，推送
* 消息体长度 2个字节存储消息体长度
* 附加信息  
加密，压缩算法

### 二进制协议
消息头暂时是5个byte
第一个字节代表信令类型，接着四个个字节代表body消息体长度

### 消息体
消息体主要是指在不同信令类型，消息体所装载的数据，这里字段暂时使用json格式，由各个类型的信令自行决定具体的消息格式


## 心跳格式
* 消息体格式
```json
{"interval":30 //单位为s}
```

## 订阅请求
* 请求消息体
````json
{"token":"test"}
````

## 功能改进
* push-connector消息订阅与发布变化为kafka

## 基本功能
* 提供根据唯一id的RPC统一推送接口
* 提供维护各个链接的接口
* 用于维护与客户端的链接，在建立链接后，用唯一id标识该链接
* 链接网关尽量逻辑简单，只提供链接功能，维护客户端的链接关系，对于群组的维护可以在推送业务层去处理

**NOTE:** 除此之外服务connect将转请求至相关的服务，例如订阅服务，群组服务,消息缓存服务

## 需求分析
> 集群模式如何给单个用户推送消息  
  
因为不同用户可能链接不同的push-connector,所以推送的时候首先要找到这个用户所链接的connector，然后再向其推送消息

> 群推送  
  
给某一用户组推送，可以根据用户id找到其所在的网络，然后在调远程接口进行推送，这种方式需要维护一个链接关系表

> 消息发布订阅模型  
  
客户端与connector的映射关系可以在redis中维护，可以采用redis的发布订阅模型通知其他connector进行后续操作
基于redis发布订阅模型可能由压力过大导致消息丢失，这种广播的模式，使用消息中间件也是一样的，因此基于此，需要一个路由层，对发送过来的token进行统一路由在发送到相应的push-connector上

## 负载均衡
不同push-connector需要进行负载均衡，这里使用nginx的tcp代理模块实现

# websocket 协议实现
为了实现web端消息共享发送，因此实现websocket协议
## 基本原理
* 共享客户端登录状态  
客户端登录策略采用统一的方式，利用用户名和统一识别id进行登录识别验证
* 通讯协议格式
web端通信统一采用json数据格式，进行websocket协议解析之后，转为服务端内部的二进制消息格式，便于统一处理
* 消息解包  
由于与web客户端交互的时websocket协议，所以web客户端发送的消息现在基本是基于json格式，在进行消息解码后需要发送给以前基于protobuf协议内部消息处理模块，因此需再进行格式转换
在回复客户端消息前，消息需要在此进行protobuf向json格式转换，进行转发给web客户端


## 消息格式
* 基本数据结构  

```json
{
	"signal": "connect",
	"sub_signal": "conect_ack",
	"message_id": 0,
	"content": ""
}
```

curl -H "Content-Type:application/json" -X POST --data '{"mobile": 13900000001,"code":556677,"clientId":"bccdb58cfdb34d861576810441000"}' http://push.comsince.cn:8080/login

返回测试登录结果

```json
{
	"code": 0,
	"message": "success",
	"result": {
		"userId": "4A4A4Aaa",
		"token": "Y07NMt60eQrcLimH6TPAeU4LvABnuN+N9b9ytv2BNGsdODgWWsf1gVOCFf+gPBbyX7PkBLRWl2H3UlXwcX8F4mGMcZfnn/tpn1zH3itSEgwFJIICehRkHm3j78QGSZ2ADClVzK4HWPEme2lNpPqfSdLlxEZ/NQytazBayTNlCfQ=",
		"register": false
	}
}
```

## 消息解析
由于websocket 采用的json格式为的数据结构，为了复用以前protobuf的数据格式
* decode 操作
decode之后做相应的数据转换，在进行websocket消息格式解码后转为proto消息格式
* encode 操作
对消息进行encode操作符合websocket协议格式，但是在之前需要对proto格式消息转换为json消息格式