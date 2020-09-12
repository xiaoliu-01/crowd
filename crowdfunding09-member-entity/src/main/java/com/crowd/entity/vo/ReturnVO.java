package com.crowd.entity.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// �ر����ͣ�0 - ʵ��ر��� 1 ������Ʒ�ر�
	private Integer type;
	// ֧�ֽ��
	private Integer supportmoney;
	// �ر����ݽ���
	private String content;
	// �ܻر�������0 Ϊ������
	private Integer count;
	// �Ƿ����Ƶ��ʹ���������0 ��ʾ���޹���1 ��ʾ�޹�
	private Integer signalpurchase;
	// ��������޹�����ô������޹�����
	private Integer purchase;
	// �˷ѣ���0��Ϊ����
	private Integer freight;
	//�Ƿ񿪷�Ʊ��0 - ������Ʊ�� 1 - ����Ʊ
	private Integer invoice;
	//�ڳ�����󷵻��ر���Ʒ����
	private Integer returndate;
	//˵��ͼƬ·��
	private String describPicPath;
}
