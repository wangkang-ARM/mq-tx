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
# mq-tx
基于spring的无侵入式消息落地


### 使用方式
- 依赖pom

```java
<dependency>
    <groupId>com.lefit.tx</groupId>
    <artifactId>mq-tx</artifactId>
    <version>1.0.1</version>
</dependency>
```
- 嵌入代码
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

//使用重写后的@MapperScan
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
- 初始化表
```sql
#初始化消息表 表名对应topic名
#例如以下 用户测试服务topic的名称为lefit_user_topic_test表名即为lefit_user_topic_test其他字段固定
CREATE TABLE `lefit_user_topic_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag` varchar(128) NOT NULL DEFAULT '' COMMENT '消息tag',
  `body` varchar(1024) NOT NULL DEFAULT '' COMMENT '消息体',
  `message_id` varchar(128) NOT NULL DEFAULT '' COMMENT '消息id',
  `message_key` varchar(128) NOT NULL DEFAULT '0' COMMENT '消息key',
  `lable` int(11) NOT NULL DEFAULT '0' COMMENT '消费服务标签',
  `retry_num` int(2) NOT NULL DEFAULT '0' COMMENT '重试10次',
  `ctime` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新增用户topic';
```
- 增加配置

以key=value形式（服务名={"services":[服务器ip#标签], "topics":[需落库topic]}）

例如：lefit-user={"services": ["172.16.12.101#111","172.16.12.102#222"],"topics": ["LEFIT_USER_TOPIC_TEST"]}

- 已经集成Apollo服务
    
    ![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/1.png)
    ![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/2.png)
    ![image](https://github.com/wangkang-ARM/mq-proxy/blob/master/images/3.png)
    
- 未集成本地配置
    
     例如：lefit-user={"services": ["172.16.12.101#111","172.16.12.102#222"],"topics": ["LEFIT_USER_TOPIC_TEST"]}

- 调用方式
    - 声明式事务
    ```java
    @Transactional
    public void addUserSourceTag(UUserSourceTag userSourceTag) {
        //业务代码...
        //内部存在消息发送
        
    }
    ```
    - 编程式
    ```java
         @Autowired
         private TransactionTemplate transactionTemplate;
         @Autowired
         private MessageTransactionApi messageTransactionApi;
         
         public void addUserSourceTag(UUserSourceTag tag) {
              transactionTemplate.execute(new TransactionCallback<String>() {

                @Override
                public String doInTransaction(TransactionStatus transactionStatus) {
                    String result = null;
                    try {
                        int i =uUserSourceTagMapper.insert(tag);
                        
                        Message message = new Message();
                        message.setKey("USER_INSERT_" + System.currentTimeMillis());
                        message.setTopic("LEFIT_USER_TOPIC_TEST");
                        message.setTag("USER_INSERT");
                        message.setBody("xxxxxxxxx");
                        
                        SendResult res = messageTransactionApi.proxyProcessorMqMessage(message);
                        result = res.getMessageId();
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        System.out.println("Transfer error!");
                        throw e;
                    }
                    return result;
                }
            });
         }
         
    ```

