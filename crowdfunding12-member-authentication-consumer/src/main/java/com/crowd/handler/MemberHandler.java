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

		// 1.调用远程接口根据登录账号查询MemberPO对象
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
    	// 2.比较密码
    	String userpswdFormDataBase = memberOP.getUserpswd();
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    	boolean matcheResult = bCryptPasswordEncoder.matches(userpswd, userpswdFormDataBase);
    	if(!matcheResult) {
    		modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
    		return "member-login";
    	}
    	// 3.创建MemberLoginVO对象存入Session域
    	MemberLoginVO memberLoginVO = new MemberLoginVO(memberOP.getId(),memberOP.getUsername(),memberOP.getEmail());
    	session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);
//    	MemberLoginVO attribute = (MemberLoginVO) session.getAttribute("loginMember");
//    	System.out.println("attribute:"+attribute);
    	// 4.页面重定向到会员中心页
    	return "redirect:http://www.crowd.com/auth/member/to/center/page";
    	//return "member-center";
    }
    
    @RequestMapping(value = "/auth/do/member/register")
	public String  register(MemberVO memberVO , ModelMap modelMap){
		// 1.从表单获取用户输入的手机号
		String phoneNum = memberVO.getPhoneNum();
		// 2.用户手机号拼接redis对应的Key
		String key = CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
		// 3.用拼接的key取出redis中对应的Value
		ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);
		// 4.检查查询操作是否有效
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
		// 5.把取出的value与表单提交的验证码进行比对
		String formCode = memberVO.getCode();
		if(!Objects.equals(redisCode, formCode)) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_INVALID);
			return "member-reg";
		}
		// 6.比对一致，则删除redis中存放的验证码
		redisRemoteService.removeRedisKeyRemote(key);
		// 7.执行密码加密
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String userpswd = memberVO.getUserpswd();
		// ①.加密后
		String encode = bCryptPasswordEncoder.encode(userpswd);
		memberVO.setUserpswd(encode);
		// 8.执行保存
		// ①.创建MemberOP对象
		MemberOP memberOP = new MemberOP();
		// ②复制属性
		BeanUtils.copyProperties(memberVO, memberOP);
		// ③.调用远程接口执行保存
		ResultEntity<String> saveMember = mySQLRemoteService.saveMember(memberOP);
		if(ResultEntity.FAILED.equals(saveMember.getOperationResult())) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveMember.getOperationMessage());
			return "member-reg";
		}
		// 使用重定向避免刷新浏览器导致重新执行注册流程
		return "redirect:/auth/member/to/login/page";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/auth/member/send/short/message.json")
	public ResultEntity<String> sendMessage(@RequestParam("phoneNum")String phoneNum ){
		// 1.发送验证码到phoneNum手机
		ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(shortMessageProperties.getHost(), 
										shortMessageProperties.getPath(), 
										shortMessageProperties.getSkin(), phoneNum, 
										shortMessageProperties.getAppcode(), 
										shortMessageProperties.getSign());
		// 2.判断短信发送结果
		if(ResultEntity.SUCCESS.equals(sendMessageResultEntity.getOperationResult())) {
			// 3.如果发送成功，则将验证码存入Redis
			// ①从上一步操作的结果中获取随机生成的验证码
			String code = sendMessageResultEntity.getQueryData();
			
			// ②拼接一个用于在Redis中存储数据的key
			String key = CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
			
			// ③调用远程接口存入Redis
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
