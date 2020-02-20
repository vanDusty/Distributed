package cn.van.distributed.packet.mapper;

import cn.van.distributed.packet.entity.RedRacketRecordDO;

public interface RedRacketRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RedRacketRecordDO record);

    int insertSelective(RedRacketRecordDO record);

    RedRacketRecordDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RedRacketRecordDO record);

    int updateByPrimaryKey(RedRacketRecordDO record);
}