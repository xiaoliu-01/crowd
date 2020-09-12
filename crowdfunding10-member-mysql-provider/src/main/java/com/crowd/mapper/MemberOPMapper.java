package com.crowd.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crowd.entity.po.MemberOP;
import com.crowd.entity.po.MemberOPExample;
@Mapper
public interface MemberOPMapper {
    int countByExample(MemberOPExample example);

    int deleteByExample(MemberOPExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MemberOP record);

    int insertSelective(MemberOP record);

    List<MemberOP> selectByExample(MemberOPExample example);

    MemberOP selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MemberOP record, @Param("example") MemberOPExample example);

    int updateByExample(@Param("record") MemberOP record, @Param("example") MemberOPExample example);

    int updateByPrimaryKeySelective(MemberOP record);

    int updateByPrimaryKey(MemberOP record);
}