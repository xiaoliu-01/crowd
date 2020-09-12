package com.crowd.handler;

import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crowd.Constant.CrowdConstant;
import com.crowd.api.MySQLRemoteService;
import com.crowd.api.RedisRemoteService;
import com.crowd.config.ShortMessageProperties;
import com.crowd.entity.po.MemberOP;
import com.crowd.entity.vo.MemberLoginVO;
import com.crowd.entity.vo.MemberVO;
import com.crowd.util.CrowdUtil;
import com.crowd.util.ResultEntity;

@Controller
public class MemberHandler {
   
	@Autowired
	private ShortMessageProperties shortMessageProperties ;
	
	@Autowired
	private RedisRemoteService redisRemoteService;
	
    @Autowired
    private MySQLRemoteService mySQLRemoteService ;
	
    @RequestMapping(value = "/auth/member/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:http://www.crowd.com/";
    } 
    
    
    @RequestMapping(value = "/auth/member/do/login")
    public String login(@RequestParam("loginacct")String loginacct,
                                      @RequestParam("userpswd") String userpswd,
                                      ModelMap modelMap,HttpSession session)
    {   

		// 1.����Զ�̽ӿڸ��ݵ�¼�˺Ų�ѯMemberPO����
    	ResultEntity<MemberOP> resultEntity = mySQLRemoteService.getMemberOPByLoginAcctRemote(loginacct);
    	if(ResultEntity.FAILED.equals(resultEntity.getOperationResult())) {
    		modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getOperationMessage());
    		return "member-login";
    	}
    	MemberOP memberOP = resultEntity.getQueryData();
    	if(memberOP == null) {
    		modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
    		return "member-login";
    	}
    	// 2.�Ƚ�����
    	String userpswdFormDataBase = memberOP.getUserpswd();
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    	boolean matcheResult = bCryptPasswordEncoder.matches(userpswd, userpswdFormDataBase);
    	if(!matcheResult) {
    		modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
    		return "member-login";
    	}
    	// 3.����MemberLoginVO�������Session��
    	MemberLoginVO memberLoginVO = new MemberLoginVO(memberOP.getId(),memberOP.getUsername(),memberOP.getEmail());
    	session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);
//    	MemberLoginVO attribute = (MemberLoginVO) session.getAttribute("loginMember");
//    	System.out.println("attribute:"+attribute);
    	// 4.ҳ���ض��򵽻�Ա����ҳ
    	return "redirect:http://www.crowd.com/auth/member/to/center/page";
    	//return "member-center";
    }
    
    @RequestMapping(value = "/auth/do/member/register")
	public String  register(MemberVO memberVO , ModelMap modelMap){
		// 1.�ӱ���ȡ�û�������ֻ���
		String phoneNum = memberVO.getPhoneNum();
		// 2.�û��ֻ���ƴ��redis��Ӧ��Key
		String key = CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
		// 3.��ƴ�ӵ�keyȡ��redis�ж�Ӧ��Value
		ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);
		// 4.����ѯ�����Ƿ���Ч
		String result = resultEntity.getOperationResult();
		if(ResultEntity.FAILED.equals(result)) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getOperationMessage());
			return "member-reg";
		}
		String redisCode = resultEntity.getQueryData();
		if(redisCode == null) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_NOT_EXISTS );
			return "member-reg";
		}
		// 5.��ȡ����value����ύ����֤����бȶ�
		String formCode = memberVO.getCode();
		if(!Objects.equals(redisCode, formCode)) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_INVALID);
			return "member-reg";
		}
		// 6.�ȶ�һ�£���ɾ��redis�д�ŵ���֤��
		redisRemoteService.removeRedisKeyRemote(key);
		// 7.ִ���������
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String userpswd = memberVO.getUserpswd();
		// ��.���ܺ�
		String encode = bCryptPasswordEncoder.encode(userpswd);
		memberVO.setUserpswd(encode);
		// 8.ִ�б���
		// ��.����MemberOP����
		MemberOP memberOP = new MemberOP();
		// �ڸ�������
		BeanUtils.copyProperties(memberVO, memberOP);
		// ��.����Զ�̽ӿ�ִ�б���
		ResultEntity<String> saveMember = mySQLRemoteService.saveMember(memberOP);
		if(ResultEntity.FAILED.equals(saveMember.getOperationResult())) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveMember.getOperationMessage());
			return "member-reg";
		}
		// ʹ���ض������ˢ���������������ִ��ע������
		return "redirect:/auth/member/to/login/page";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/auth/member/send/short/message.json")
	public ResultEntity<String> sendMessage(@RequestParam("phoneNum")String phoneNum ){
		// 1.������֤�뵽phoneNum�ֻ�
		ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(shortMessageProperties.getHost(), 
										shortMessageProperties.getPath(), 
										shortMessageProperties.getSkin(), phoneNum, 
										shortMessageProperties.getAppcode(), 
										shortMessageProperties.getSign());
		// 2.�ж϶��ŷ��ͽ��
		if(ResultEntity.SUCCESS.equals(sendMessageResultEntity.getOperationResult())) {
			// 3.������ͳɹ�������֤�����Redis
			// �ٴ���һ�������Ľ���л�ȡ������ɵ���֤��
			String code = sendMessageResultEntity.getQueryData();
			
			// ��ƴ��һ��������Redis�д洢���ݵ�key
			String key = CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
			
			// �۵���Զ�̽ӿڴ���Redis
			ResultEntity<String> sendCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code,900);
			if(ResultEntity.SUCCESS.equals(sendCodeResultEntity.getOperationResult())) {
				 return ResultEntity.successWithoutData();
			}else {
				return sendCodeResultEntity;
			}
		}else {
			return sendMessageResultEntity;
		}
	}
}
