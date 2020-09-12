package com.crowd.service;

import java.util.List;

import com.crowd.entity.vo.AddressVO;
import com.crowd.entity.vo.OrderProjectVO;
import com.crowd.entity.vo.OrderVO;

public interface OrderProjectService {

	OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId);

	List<AddressVO> selectAddressByMemberId(Integer memberId);

	void saveAddressRemote(AddressVO addressVO);

	void saveOrder(OrderVO orderVO);
}
