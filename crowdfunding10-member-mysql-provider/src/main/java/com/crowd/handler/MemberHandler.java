package com.crowd.handler;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crowd.Constant.CrowdConstant;
import com.crowd.entity.po.MemberOP;
import com.crowd.service.MemberService;
import com.crowd.util.ResultEntity;

@RestController
public class MemberHandler {
    
	@Resource
	private MemberService memberService; 
	
	
	@PostMapping(value = "/save/member/remote")
	public ResultEntity<String> saveMember(@RequestBody MemberOP memberOP){
		try {
			memberService.saveMember(memberOP);
			return ResultEntity.successWithoutData();
		} catch (Exception e) {
		  if(e instanceof DuplicateKeyException) {
			  return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_NUMBER_IS_NOT_UNIQUE);
			  
		  }
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/get/memberOP/byLoginAcct/remote")
	public ResultEntity<MemberOP> getMemberOPByLoginAcctRemote(@RequestParam("loginAcct")String loginAcct){
		try {
			// ���ò�ѯ������ִ�в�ѯ���ɹ�����ȥ���
			MemberOP memberOP = memberService.getMemberOPByLoginAcctRemote(loginAcct);
			return ResultEntity.successWithData(memberOP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// �����ѯʧ�ܣ��ҷ���ʧ����Ϣ
			return ResultEntity.failed(e.getMessage());
		}
	};
	
} 
