package com.atguigu.process.service;

import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-30
 */
public interface OaProcessTypeService extends IService<ProcessType> {
    //查询所有的审批分类和每个分类所有的审批模板
    List<ProcessType> findProcessType();
}