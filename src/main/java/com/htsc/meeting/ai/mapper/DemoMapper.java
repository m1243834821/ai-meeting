package com.htsc.meeting.ai.mapper;

import com.htsc.meeting.ai.entity.DemoModel;

/**
 * DemoMapper
 */
public interface DemoMapper {

    /**
     * 根据ID查询名称
     *
     * @param id 主键ID
     * @return Demo实体
     */
    DemoModel queryById(Long id);

}
