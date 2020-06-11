package com.lefit.mq.repository.dao;

import com.lefit.mq.repository.model.MsgEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName: lefit-marketing
 * @Package: com.lefit.marketing.argue.mapper
 * @ClassName: ArgueMapper
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2019/3/5 下午4:01
 * @Version: 1.0
 */
public interface MqBackUpMapper {

    void batchInsert(@Param("tableName") String tableName, @Param("list") List<MsgEntity> list);
}
