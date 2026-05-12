package com.codexlab.aimurder.web.service;

import com.codexlab.aimurder.domain.script.definition.ScriptDefinition;

import java.util.List;

/**
 * 副本仓储接口。
 * 用于获取系统中的副本模板定义。
 */
public interface ScriptRepository {

    /**
     * 根据副本标识获取副本定义。
     *
     * @param scriptId 副本标识
     * @return 副本定义
     */
    ScriptDefinition findById(String scriptId);

    /**
     * 获取默认副本定义。
     *
     * @return 默认副本定义
     */
    ScriptDefinition getDefaultScript();

    /**
     * 列出全部可用副本。
     *
     * @return 副本定义列表
     */
    List<ScriptDefinition> findAll();
}
