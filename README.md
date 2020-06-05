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


### 使用方式

```java
@SpringBootApplication
//加入@EnableLefitMqTransaction
@EnableLefitMqTransaction
public class UserApplication
{
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

//使用重写mybatis后的MapperScan
import com.lefit.mq.annotation.MapperScan;
@MapperScan(value = {"com.lefit.xxx.xxx"}, sqlSessionFactory={"sqlSessionFactoryBean"})
public class DatabaseConfig {
    .....
    @Bean
    @Qualifier("sqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/user/*.xml"));
        return sqlSessionFactoryBean.getObject();
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

