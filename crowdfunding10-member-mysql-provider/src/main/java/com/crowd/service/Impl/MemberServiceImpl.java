package com.crowd.service.Impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crowd.entity.po.MemberOP;
import com.crowd.entity.po.MemberOPExample;
import com.crowd.entity.po.MemberOPExample.Criteria;
import com.crowd.mapper.MemberOPMapper;
import com.crowd.service.MemberService;
import com.sun.rmi.rmid.ExecOptionPermission;
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
    
	@Resource
	private MemberOPMapper memberOPMapper;
	
	@Override
	public MemberOP getMemberOPByLoginAcctRemote(String loginAcct) {
		// TODO Auto-generated method stub
		MemberOPExample example = new MemberOPExample();
		Criteria createCriteria = example.createCriteria();
		// 封装查询条件
		createCriteria.andLoginacctEqualTo(loginAcct);
		// 执行查询
		List<MemberOP> list = memberOPMapper.selectByExample(example);
		// 返回值合理化判断
		if(list == null || list.size()==0) {
			return null;
		}
		// 返回结果
		return list.get(0);
	}
	
	
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	@Override
	public void saveMember(MemberOP memberOP) {
		// TODO Auto-generated method stub
		memberOPMapper.insertSelective(memberOP);
	}

}
