# zodiac

## 项目入口与验证

Zodiac 是一个 Java 8 的分布式注册与 RPC 实验。注册中心与 RPC 实现分属独立 Maven 模块，示例通过接口和注解表达调用端与服务端的关系。

- [注册中心](zodiac-server/) 提供注册相关的服务端实现。
- [RPC 代理](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/core/RpcProxy.java) 是阅读客户端代理的入口。
- [示例应用](zodiac-example/) 用于阅读接口、提供方和调用方之间的接线。

用 `mvn clean verify` 检查聚合构建。运行注册中心前，按本机环境选择数据目录和可用端口。

### 分开验证注册与调用

1. **注册中心。** 从 [注册入口](zodiac-server/src/main/java/com/tcsl/zodiac/server/controller/RegisterController.java) 和 [JRaft 配置](zodiac-server/src/main/java/com/tcsl/zodiac/server/core/jraft/JraftConfig.java) 阅读注册请求和集群接入。试跑集群前，为各实例准备独立的数据目录与端口。
2. **RPC 调用。** 从 [示例调用方](zodiac-example/example-1/src/main/java/com/tcsl/zodiac/example1/controller/ExampleController.java) 的 `@RpcReference` 追到 [RPC 代理](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/core/RpcProxy.java)，再对照 [example-2](zodiac-example/example-2/) 的服务端实现。RPC 示例需要可用的 Spring Cloud 服务发现环境。

当前离线编译缺少 Maven 父 POM，需先解析旧版 Spring Boot 与 Spring Cloud 依赖。README 中的数据目录示例需要替换成本机路径。

### 从接口调用追到网络请求

调用方示例通过 `@RpcReference` 声明接口依赖。 [示例控制器](zodiac-example/example-1/src/main/java/com/tcsl/zodiac/example1/controller/ExampleController.java)

- [RpcProxy](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/core/RpcProxy.java) 使用 Java 动态代理，把接口名、方法名、参数类型和实参放入请求，为请求分配 ID。
- 代理通过连接上下文获取目标连接，发送请求并等待对应响应。可以继续看 [ConnectContext](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/core/ConnectContext.java) 和 [FutureRpcResponse](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/netty/FutureRpcResponse.java)。
- 提供方使用 Spring Bean 后处理器发现 RPC 服务。 [RpcServiceBeanPostProcessor](zodiac-rpc/src/main/java/com/tcsl/zodiac/rpc/spring/RpcServiceBeanPostProcessor.java)

注册中心与这一调用路径有各自的阅读入口。注册和心跳从 [RegisterController](zodiac-server/src/main/java/com/tcsl/zodiac/server/controller/RegisterController.java) 与 [BeatHeartController](zodiac-server/src/main/java/com/tcsl/zodiac/server/controller/BeatHeartController.java) 开始，集群接线从 JRaft 配置开始。

下方保留单机、集群和 RPC 示例配置。


#### 介绍
# zodiac-server  分布式注册中心

默认集群启动 

-- 启动参数

--port=8043 --serverAddress=127.0.0.1:8083 --initialServers=127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 --dataPath=E:\TEST\node3
--port=8042 --serverAddress=127.0.0.1:8082 --initialServers=127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 --dataPath=E:\TEST\node2
--port=8041 --serverAddress=127.0.0.1:8081 --initialServers=127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083 --dataPath=E:\TEST\node1

单机启动参数
--m=standalone


# zodiac-rpc  rpc服务调用

依赖于springcloud 任意注册中心
服务调用
@RpcReference(serverUrl = "example2")
UserService  userService;


服务提供

@RpcService
public class UserServiceImpl {
}
