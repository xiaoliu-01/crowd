package com.crowd.mapper;

import com.crowd.entity.po.MemberConfigInfoPO;
import com.crowd.entity.po.MemberConfigInfoPOExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface MemberConfigInfoPOMapper {
    int countByExample(MemberConfigInfoPOExample example);

    int deleteByExample(MemberConfigInfoPOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MemberConfigInfoPO record);

    int insertSelective(MemberConfigInfoPO record);

    List<MemberConfigInfoPO> selectByExample(MemberConfigInfoPOExample example);

    MemberConfigInfoPO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MemberConfigInfoPO record, @Param("example") MemberConfigInfoPOExample example);

    int updateByExample(@Param("record") MemberConfigInfoPO record, @Param("example") MemberConfigInfoPOExample example);

    int updateByPrimaryKeySelective(MemberConfigInfoPO record);

    int updateByPrimaryKey(MemberConfigInfoPO record);
}