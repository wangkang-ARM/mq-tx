package com.lefit.mq.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.proxy
 * @ClassName: AfterTransactionService
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/19 下午4:53
 * @Version: 1.0
 */
@Component
public class AfterTransactionService extends TransactionSynchronizationAdapter {

    public static final ExecutorService executorService = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(100));

    @Override
    public void afterCommit() {
        Map<String, LinkedBlockingQueue<Runnable>> afterStack =  AfterTransactionOpt.getAfterTransactionOpts().get();
        //将任务提交到线程池执行
        LinkedBlockingQueue<Runnable> executeQueue =  afterStack.get(AfterTransactionOpt.getTransactionName());
        while(!executeQueue.isEmpty()){
            //poll 非阻塞方法
            executorService.submit(executeQueue.poll());
        }
    }

    //事务执行完成后删除操作
    @Override
    public void afterCompletion(int status) {
        AfterTransactionOpt.getAfterTransactionOpts().remove();
        Assert.isNull(AfterTransactionOpt.getAfterTransactionOpts().get(),"getAfterTransactionOpts为空null");
    }
}
