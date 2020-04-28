# mq-proxy
基于spring的无侵入式消息落地


### 加入@EnableLefitMqProxy
```java
@SpringBootApplication
@EnableLefitMqProxy
public class UserApplication
{
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

```

```java
<dependency>
    <groupId>com.lefit.proxy</groupId>
    <artifactId>mq-proxy</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

