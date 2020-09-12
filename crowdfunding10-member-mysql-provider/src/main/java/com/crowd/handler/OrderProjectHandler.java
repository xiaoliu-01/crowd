package com.crowd.handler;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crowd.entity.vo.AddressVO;
import com.crowd.entity.vo.OrderProjectVO;
import com.crowd.entity.vo.OrderVO;
import com.crowd.service.OrderProjectService;
import com.crowd.util.ResultEntity;

@RestController
public class OrderProjectHandler {
    
	@Autowired
	private OrderProjectService orderProjectService;
	
	@RequestMapping(value = "/save/order/remote")
	public ResultEntity<String> saveOrderRemote(@RequestBody OrderVO orderVO){
		
		try {
			orderProjectService.saveOrder(orderVO);
			return ResultEntity.successWithoutData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/save/address/remote")
	ResultEntity<String> saveAddressRemote(@RequestBody AddressVO addressVO){
		try {
			 orderProjectService.saveAddressRemote(addressVO);
			 return ResultEntity.successWithoutData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/get/address/vo/remote")
	public ResultEntity<List<AddressVO>> getAddressVORemote(@RequestParam("memberId")Integer memberId){
		
		try {
			List<AddressVO> addressVOs = orderProjectService.selectAddressByMemberId(memberId);
			return ResultEntity.successWithData(addressVOs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	@RequestMapping("/get/order/project/vo/remote")
	public ResultEntity<OrderProjectVO> getOrderProjectVORemote(@RequestParam("projectId")Integer projectId,
																@RequestParam("returnId") Integer returnId)
	{
		
		try {
			OrderProjectVO resultEntity = orderProjectService.getOrderProjectVO(projectId,returnId);
			return ResultEntity.successWithData(resultEntity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	};
	
}
