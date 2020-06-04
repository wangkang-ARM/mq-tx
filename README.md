![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142240%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142319%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142335%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142346%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142358%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142416%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142427%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142449%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142459%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142511%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/WX20200515-142518%402x.png)
![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/12344321.png)
# mq-proxy
基于spring的无侵入式消息落地


### 加入@EnableLefitMqProxy
```java
@SpringBootApplication
@EnableLefitMqTransaction
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

