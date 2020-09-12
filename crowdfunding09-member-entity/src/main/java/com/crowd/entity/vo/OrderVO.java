package com.crowd.entity.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	// ����
	private Integer id;
	
	// ������
	private String orderNum;
	
	// ֧������ˮ����
	private String payOrderNum;
	
	// �������
	private Double orderAmount;
	
	// �Ƿ񿪷�Ʊ
	private Integer invoice;
	
	// ��Ʊ̧ͷ
	private String invoiceTitle;
	
	// ��ע
	private String orderRemark;
	
	private Integer addressId;
	
	private OrderProjectVO orderProjectVO;
}
