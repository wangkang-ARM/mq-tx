package com.lefit.mq.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: lefit-user-parent
 * @Package: com.lefit.mq.task
 * @ClassName: HistoryTaskService
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/6/10 下午3:58
 * @Version: 1.0
 */
@Component
@EnableScheduling
public class HistoryTaskJob {

    @Autowired
    private ExecuteMsgTakeService executeMsgTakeService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void runfirst(){
        System.out.println("********backUp job******" + System.currentTimeMillis());
        executeMsgTakeService.historyBackUp();
    }

}
