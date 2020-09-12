package com.crowd.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailReturnVO {
	
	// �ر���Ϣ����
	private Integer returnId;
	
	// ��ǰ��λ��֧�ֵĽ��
	private Integer supportMoney;
	
	// �����޹���ȡֵΪ0ʱ���޶ȡֵΪ1ʱ���޶�
	private Integer signalPurchase;
	
	// �����޶�����
	private Integer purchase;
	
	// ��ǰ�õ�λ֧��������
	private Integer supproterCount;
	
	// �˷ѣ�ȡֵΪ0ʱ��ʾ����
	private Integer freight;
	
	// �ڳ�ɹ�������췢��
	private Integer returnDate;
	
	// �ر�����
	private String content;

}
