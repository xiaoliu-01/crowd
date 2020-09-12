package com.crowd.handler;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crowd.Constant.CrowdConstant;
import com.crowd.api.MySQLRemoteService;
import com.crowd.entity.po.AddressPO;
import com.crowd.entity.vo.AddressVO;
import com.crowd.entity.vo.MemberLoginVO;
import com.crowd.entity.vo.OrderProjectVO;
import com.crowd.util.ResultEntity;
@Controller
public class OrderHandler {
   
	@Autowired
    private MySQLRemoteService mySQLRemoteService;
	
	@RequestMapping(value = "/save/address")
    public String saveAddressRemote(AddressVO addressVO,HttpSession session){
		// 1.����Զ�̽ӿ�ִ�б���
		mySQLRemoteService.saveAddressRemote(addressVO);
		
		// 2.��session����ȡ��orderProjectVO����
		OrderProjectVO orderProjectVO = (OrderProjectVO)session.getAttribute("orderProjectVO");
		// 3.��orderProjectVO������ȡ��orderProjectVO
		Integer returnCount = orderProjectVO.getReturnCount();
		
		// 3.�ض���ָ��ҳ��
		return "redirect:http://www.crowd.com/order/confirm/order/"+returnCount;
	}	
	
	@RequestMapping("/confirm/order/{returnCount}")
	public String showConfirmOrderInfo(@PathVariable("returnCount") Integer returnCount,
										HttpSession session) {
	   // 1.�ѽ��յ��Ļر������ϲ��� Session ��
		OrderProjectVO orderProjectVO = (OrderProjectVO)session.getAttribute("orderProjectVO");
		orderProjectVO.setReturnCount(returnCount);
		session.setAttribute("orderProjectVO", orderProjectVO);
		
		// 2.��ȡ��ǰ�ѵ�¼�û��� id
		MemberLoginVO loginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		Integer memberId = loginVO.getId();
		
		// 3.��ѯ�ջ��ַ
		ResultEntity<List<AddressVO>> resultEntity = mySQLRemoteService.getAddressVORemote(memberId);
		
		if(ResultEntity.SUCCESS.equals(resultEntity.getOperationResult())) {
			List<AddressVO> queryData = resultEntity.getQueryData();
			session.setAttribute("addressVOList", queryData);
		}
		
		return "confirm_order";
		
	}
	
	@RequestMapping(value = "/confirm/return/info/{projectId}/{returnId}")
	public String showReturnConfirmInfo(@PathVariable("projectId")Integer projectId,@PathVariable("returnId") Integer returnId
										,HttpSession session) 
	{   
		System.out.println("returnId="+returnId+"  projectId="+projectId);
		
		ResultEntity<OrderProjectVO> resultEntity = mySQLRemoteService.getOrderProjectVORemote(projectId, returnId);
		if(ResultEntity.SUCCESS.equals(resultEntity.getOperationResult())) {
			OrderProjectVO queryData = resultEntity.getQueryData();
			System.out.println(queryData);
			session.setAttribute("orderProjectVO", queryData);
		}
		
		return "confirm_return";
	}

	
}
