package com.kob.backend.mapper.tomato;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.tomato.TomatoUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TomatoUserMapper extends BaseMapper<TomatoUser> {
}
