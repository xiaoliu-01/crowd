package com.crowd.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crowd.entity.po.MemberOP;
import com.crowd.entity.vo.AddressVO;
import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.OrderProjectVO;
import com.crowd.entity.vo.OrderVO;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.util.ResultEntity;

@FeignClient(name = "crowd-mysql")
public interface MySQLRemoteService {
   
	@RequestMapping(value = "/get/memberOP/byLoginAcct/remote")
	ResultEntity<MemberOP> getMemberOPByLoginAcctRemote(@RequestParam("loginAcct")String loginAcct);
	
	@RequestMapping(value = "/save/member/remote")
	ResultEntity<String> saveMember(@RequestBody MemberOP memberOP);
    
	@RequestMapping(value = "/save/object/vo/remote")
	ResultEntity<String> saveObjectVORemote(@RequestBody ProjectVO projectVO,@RequestParam("id") Integer id);
    
	@RequestMapping(value = "/get/portal/type/project/remote")
	public ResultEntity<List<PortalTypeVO>> getPortalTypeProjectDataRemote();
	
	@RequestMapping(value = "/get/project/detail/remote/{projectId}")
	public ResultEntity<DetailProjectVO> getDetailProjectVORemote(@PathVariable("projectId") Integer projectId);
    
	@RequestMapping(value = "/get/order/project/vo/remote")
	public ResultEntity<OrderProjectVO> getOrderProjectVORemote(@RequestParam("projectId")Integer projectId,@RequestParam("returnId")Integer returnId);
    
	@RequestMapping(value = "/get/address/vo/remote")
	public ResultEntity<List<AddressVO>> getAddressVORemote(@RequestParam("memberId")Integer memberId);
    
	@RequestMapping(value = "/save/address/remote")
	public ResultEntity<String> saveAddressRemote(@RequestBody AddressVO addressVO);
    
	@RequestMapping(value = "/save/order/remote")
	public ResultEntity<String> saveOrderRemote(@RequestBody OrderVO orderVO);
}
