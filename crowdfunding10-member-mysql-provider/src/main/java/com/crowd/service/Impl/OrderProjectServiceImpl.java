package com.crowd.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crowd.Constant.CrowdConstant;
import com.crowd.entity.po.AddressPO;
import com.crowd.entity.po.AddressPOExample;
import com.crowd.entity.po.AddressPOExample.Criteria;
import com.crowd.entity.po.OrderPO;
import com.crowd.entity.po.OrderProjectPO;
import com.crowd.entity.vo.AddressVO;
import com.crowd.entity.vo.OrderProjectVO;
import com.crowd.entity.vo.OrderVO;
import com.crowd.mapper.AddressPOMapper;
import com.crowd.mapper.OrderPOMapper;
import com.crowd.mapper.OrderProjectPOMapper;
import com.crowd.service.OrderProjectService;
import com.crowd.util.ResultEntity;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
@Transactional(readOnly = true)
@Service
public class OrderProjectServiceImpl implements OrderProjectService {
   
	@Autowired
	private OrderProjectPOMapper orderProjectPOMapper;
	
	@Autowired
	private OrderPOMapper orderPOMapper;
	
	@Autowired
	private AddressPOMapper addressPOMapper;
	
	@Override
	public OrderProjectVO  getOrderProjectVO(Integer projectId, Integer returnId) {
		List<OrderProjectVO> selectOrderProjectVO = orderProjectPOMapper.selectOrderProjectVO(returnId);
		if(selectOrderProjectVO == null || selectOrderProjectVO.size() ==0) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		return selectOrderProjectVO.get(0);
		
	}

	@Override
	public List<AddressVO> selectAddressByMemberId(Integer memberId) {
		// TODO Auto-generated method stub
		AddressPOExample example = new AddressPOExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andMemberIdEqualTo(memberId);
		List<AddressPO> AddressPOList = addressPOMapper.selectByExample(example);
		List<AddressVO> AddressVOList = new ArrayList<>();
		for (AddressPO addressPO : AddressPOList) {
              AddressVO addressVO = new AddressVO();
              BeanUtils.copyProperties(addressPO, addressVO);
			  AddressVOList.add(addressVO);
		}
		return AddressVOList;
	}
    
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	@Override
	public void saveAddressRemote(AddressVO addressVO) {
		// TODO Auto-generated method stub
		AddressPO addressPO = new AddressPO();
		BeanUtils.copyProperties(addressVO, addressPO);
		addressPOMapper.insertSelective(addressPO);
	}
    
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	@Override
	public void saveOrder(OrderVO orderVO) {
		// TODO Auto-generated method stub
		OrderPO orderPO = new OrderPO();
		OrderProjectPO orderProjectPO = new OrderProjectPO();
		BeanUtils.copyProperties(orderVO, orderPO);
		BeanUtils.copyProperties(orderVO.getOrderProjectVO(), orderProjectPO);
		orderPOMapper.insert(orderPO);
		Integer id = orderPO.getId();
		orderProjectPO.setOrderId(id);
		orderProjectPOMapper.insert(orderProjectPO);
		
	}
}
