[![Build Status](https://travis-ci.org/comsince/universe_push.svg?branch=master)](https://travis-ci.org/comsince/universe_push)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/comsince/universe_push/blob/master/LICENCE)


# 支持集群的分布式即时聊天系统

## 适合快速部署的聊天系统
这是一个聊天系统的简单架构，解决大量用户需要即时通讯的解决方案，基于RPC框架Dubbo,SpringBoot构建微服务应用，提供Docker快速部署的解决方案。
提供Android客户端类似微信功能，包括`好友添加`，`私聊`，`群聊`，等基本功能

![image](attachment/chat-show.gif)

> __<font color="#dd0000">扫码体验APK下载</font>__

![image](attachment/qr-chat.png)

**NOTE:** 本apk基于[android-chat](https://github.com/comsince/android-chat)构建替换为java协议栈开发
* 请选择其中任何一个帐号密码进行登录即可
```properties
帐号：13800000000, 13800000001, 13800000002
密码：556677
```

## 服务说明
聊天系统为了适应大规模用户的链接请求，将服务分为`链接服务`和`消息服务`，它们都是独立的，可以单独部署也可以集群部署
### 链接服务
用于解决用户的链接请求，支撑百万级用户的链接，可单击部署，可集群部署，目前为了快速部署，暂时不启用集群部署。如果你存在大规模用户链接，可以启动集群模式
### 消息服务
用于用户处理用户管理，会话管理，离线消息处理，群组管理等功能，是整个即时通讯系统的业务处理模块

## 自动化构建
增加持续集成的好处
* 随时随地发布软件
* 任何一次构建都能触发一次发布
* 只需发布一次artifact,即可随时发布

以下是发布持续交付工作流图
![image](https://cloud.githubusercontent.com/assets/6069066/14159789/0dd7a7ce-f6e9-11e5-9fbb-a7fe0f4431e3.png)

## 如何启动服务
本机部署只需要两个SpringBoot服务，一个Mysql服务，一个zookeeper服务

### 部署前准备
* 安装docker与docker-composer
* 确保编译此项目`mvn clean package -Dmaven.test.skip=true`

### 生产模式
这种模式下，所有的镜像都会从Docker Hub下载，只需要复制`docker-compose.yml`,在该目录下执行`docker-compose up`即可

### 开发模式
如果你希望自己编译镜像，你必须克隆此代码，并在本地编译此项目。然后执行`docker-compose -f docker-compose.yml -f docker-compose-dev.yml up`  
**NOTE:** 如果你希望直接部署，[参考脚本部署](README-Linux.md)


## 欢迎为此项目作出贡献
该项目时开源项目，欢迎提出建议或者提供意见反馈

## 感谢
此项目时在参考其他项目基础上完成，在此表示感谢
* [t-io](https://github.com/tywo45/t-io)
* [wildfirechat](https://gitee.com/wildfirechat/server)