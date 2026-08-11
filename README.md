<h1 align="center">Baibao</h1>
<p align="center"><b>Baibao 是一个基于常用业务场景进行封装的 Java 技术框架。</b></p>
<!-- Baibao is a Java technology framework that is encapsulated based on common business scenarios. -->

## 简介

Baibao 是一个基于常用业务场景进行封装的 Java 技术框架。它的设计思想是通过**门面模式（Facade）**为使用者提供一个统一的接口去和其他模块交互：使用者无需了解各模块的实现细节，也不会和某种具体实现耦合，更利于扩展和维护。框架本身是一个基础库，通过 Spring Boot 自动装配接入应用。

## 功能列表

下面按主题大致介绍各工具的作用与场景。

### 调用与编排
- **Action 调用引擎**：任何逻辑都可表达为 `f(input) -> output`，通过「Action 名称 + 入参 + 出参」统一调用。
- **AOP 动态代理、Bean 容器、上下文（Context）**：基于 JDK 的动态代理；无 Spring 场景下的 Bean 容器与依赖反转；登录人等信息的上下文抽象。

### 数据与转换
- **类型转换**：含泛型的复杂转换，如 `List<User>` 转 `PageList<Member>`。
- **序列化 / 反序列化**：对象与可传输/可存储字节序列之间的互转。
- **Bean / Json / Xml 转换**：对象（含 Map）间属性复制；与 fastjson / jackson / gson 互转；与 XML 互转。
- **比对、校验、元组**：图片/文件/对象差异比对；邮箱/手机号/正则等校验；KeyValue / Pair / Triple 等元组。

### 存储与数据库
- **对象存储**：阿里 OSS、MinIO、华为 OBS，及本地存储。
- **数据库**：基于原生 JDBC 的操作工具，以及 MyBatis-Plus 基类封装。

### 安全
- **加解密**：AES、DES、RSA 等；Hash 类 MD5、SHA 系列；Hmac 系列。
- **编解码**：Base64、Hex、Unicode 等。

### 网络与基础设施
- **Http 请求**：发送/接收 HTTP 请求，与 Web 服务通信。
- **缓存、锁**：缓存提高访问速度；基于 ReentrantLock 的本地可重入锁。
- **反射、脚本引擎、渲染器**：运行时检查/修改程序结构；在 Java 中执行其他语言（如 nashorn）；基于模板的文本渲染。
- **时间、追踪、Mock**：日期时间的转换/格式化/时区；埋点、异常告警、操作日志；批量生成测试数据。
- **ID 生成、代码生成**：雪花 ID、UUID；基于模板引擎的代码片段/文件生成。

### 业务能力
- **信息识别**：银行卡、公司、设备、IP 归属地。
- **数据处理**：数据填充（data fill）、分页。
- **工具查询**：汇率、HSCode、天气、国家码。
- **通用基础**：DTO 基类（BaseData / BaseQuery）、常量、枚举、Jsoup 工具。

## 调用示例

以 Action 为例，将任意逻辑注册为一个名称后统一调用（部分示例需预先配置或实现）：

```java
// 假设框架包有两种订单分润算法 "order-share1"、"order-share2"
OrderDTO order = ...;
// 按名称调用并填充分润信息
ActionUtil.execute("order-share1", order, Object.class);
// 切换算法只需改名称
ActionUtil.execute("order-share2", order, Object.class);
```

## 快速开始

- **环境要求**：JDK 8+。
- **Maven 坐标**：

```xml
<dependency>
    <groupId>io.github.kahle23</groupId>
    <artifactId>baibao</artifactId>
    <version>1.0.26_3</version>
</dependency>
```

- **自动装配**：引入依赖后，相关能力通过 Spring Boot 自动装配（`spring.factories`）按需生效，多数第三方依赖以 `provided` 形式声明，使用时再按需引入。

## 技术栈

Spring Boot 2.3、Spring Cloud（OpenFeign / Gateway）、MyBatis & MyBatis-Plus、PageHelper、Elasticsearch 6.7、Redisson、Quartz、Seata、Hutool、EasyExcel、Apache POI、jackson / fastjson / gson、XStream、Freemarker / Velocity / Beetl、Aliyun OSS / MinIO / 华为 OBS、GraalVM JS、jjwt、Jsoup 等。

## 许可证

[GNU General Public License v3](https://www.gnu.org/licenses/gpl-3.0.txt)
