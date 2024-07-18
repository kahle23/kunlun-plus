

# 跨域配置（Spring boot 应用）
目前 spring boot （spring mvc），spring 并未像网关一样提供一个配置项。所以增加了相应的配置项（注意：针对于 spring mvc 的，不适用于 spring 网关的）。具体如下所示。
```yml
spring:
  extension:
    web:
      # 跨域统一走配置，不走代码
      cors:
        enabled: true
        configurations:
          '[/**]':
            allowed-credentials: true
            allowed-origins: "*"
            allowed-headers: "*"
            allowed-methods: "*"
```


# XxlJob 配置
配置
```yml
spring:
  extension:
    xxl-job:
      enabled: true
      admin-addresses: http://10.0.0.1:8086/xxl-job-admin/
      access-token: ""
      executor:
        log-path: /data/app/run/test/logs/xxl-job
        app-name: dev-test-server
        ip: 10.0.0.2
        port: 9913
```

# 异步注解线程池配置
异步注解线程池配置。
异步注解 @async 配置线程池、最大线程数和拒绝策略，不需要启动类加@EnableAsync  。
```yml
spring:
  extension:                                 
    async:
      enabled: true    # 总开关，用于打开异步功能，并且配置异步线程池
      thread-name-prefix: "async-executor"  # 线程名称前缀
      core-pool-size: 4       # 核心线程数
      max-pool-size: 8        # 最大线程数
      queue-capacity: 8       # 队列容量
      keep-alive-seconds: 600     # 非核心线程存活秒数
      reject-policy: caller_runs  # 线程拒绝策略
```






