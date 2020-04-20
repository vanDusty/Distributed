package cn.van.distributed.packet.mapper;

import cn.van.distributed.packet.entity.RedRacketDO;

public interface RedRacketMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RedRacketDO record);

    int insertSelective(RedRacketDO record);

    RedRacketDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RedRacketDO record);

    int updateByPrimaryKey(RedRacketDO record);
}