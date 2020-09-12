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
		// 1.调用远程接口执行保存
		mySQLRemoteService.saveAddressRemote(addressVO);
		
		// 2.从session域中取出orderProjectVO对象
		OrderProjectVO orderProjectVO = (OrderProjectVO)session.getAttribute("orderProjectVO");
		// 3.从orderProjectVO对象中取出orderProjectVO
		Integer returnCount = orderProjectVO.getReturnCount();
		
		// 3.重定向到指定页面
		return "redirect:http://www.crowd.com/order/confirm/order/"+returnCount;
	}	
	
	@RequestMapping("/confirm/order/{returnCount}")
	public String showConfirmOrderInfo(@PathVariable("returnCount") Integer returnCount,
										HttpSession session) {
	   // 1.把接收到的回报数量合并到 Session 域
		OrderProjectVO orderProjectVO = (OrderProjectVO)session.getAttribute("orderProjectVO");
		orderProjectVO.setReturnCount(returnCount);
		session.setAttribute("orderProjectVO", orderProjectVO);
		
		// 2.获取当前已登录用户的 id
		MemberLoginVO loginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		Integer memberId = loginVO.getId();
		
		// 3.查询收获地址
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
