package com.lefit.mq.processor;

import com.lefit.mq.annotation.ExecutorInterface;
import org.apache.rocketmq.common.utils.ThreadUtils;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.proxy
 * @ClassName: AfterTransactionOpt
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/19 下午4:49
 * @Version: 1.0
 */
@Component
public class AfterTransactionOpt implements ExecutorInterface {

    @Autowired
    AfterTransactionService afterTransactionService;

    private static final ThreadLocal<Map<String, LinkedBlockingQueue<Runnable>>> afterTransactionOpts;

    static {
        afterTransactionOpts = new NamedThreadLocal<>("thread-local");
    }

    @Override
    public void execute(Runnable runnable) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            //manager的synchronizations容器是set,可以随便注册,不会重复
            TransactionSynchronizationManager.registerSynchronization(afterTransactionService);
            // Executable executables = getExecutablesCreateIfNecessary();
            Map<String,  LinkedBlockingQueue<Runnable>> stack = this.getIfNesses();
            try {
                stack.get(this.getTransactionName()).add(runnable);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            afterTransactionOpts.set(stack);
        }else{
            System.out.println("事务后置操作*必须*在一个活跃的事务中");
        }
    }

    /**
     * 获取当前线程执行数据栈
     * @return
     */
    public Map<String,  LinkedBlockingQueue<Runnable>> getIfNesses(){
        Map<String,  LinkedBlockingQueue<Runnable>> stack = null;
        //如果没有
        if((stack = afterTransactionOpts.get())==null){
            stack = new HashMap<>();
        }

        if(stack.get(this.getTransactionName())==null){
            LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>(1);
            stack.put(this.getTransactionName(),runnables);
        }
        return stack;
    }
    /**
     * 获取当前事务名称
     * @return
     */
    public static String getTransactionName(){
        String transName = "defaulttrans";
        transName = TransactionSynchronizationManager.getCurrentTransactionName();
        return transName;
    }

    public AfterTransactionService getAfterTransactionService() {
        return afterTransactionService;
    }

    public void setAfterTransactionService(AfterTransactionService afterTransactionService) {
        this.afterTransactionService = afterTransactionService;
    }

    public static ThreadLocal<Map<String, LinkedBlockingQueue<Runnable>>> getAfterTransactionOpts() {
        return afterTransactionOpts;
    }
}
