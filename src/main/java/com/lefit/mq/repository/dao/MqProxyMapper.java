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
public interface MqProxyMapper {

    long insertSelective(MsgEntity msgEntity);

    int updateMessageId(@Param("tableName") String tableName, @Param("id") Long id, @Param("messageId") String messageId);

    int updateRetryNum(@Param("tableName") String tableName, @Param("id") Long id);

    List<MsgEntity> queryFailMsg(@Param("tableName") String tableName, @Param("lable") Integer lable);

    List<MsgEntity> queryBackUp(@Param("tableName") String tableName);

    void deleteBackupAlready(@Param("tableName") String tableName, @Param("list") List<Long> list);
}
