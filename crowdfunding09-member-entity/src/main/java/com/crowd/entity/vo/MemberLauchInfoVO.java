package com.crowd.entity.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLauchInfoVO implements Serializable {

	private static final long serialVersionUID = 1L;
	//�򵥽���
	private String descriptionSimple;
	//��ϸ����
	private String descriptionDetail;
	//��ϵ�绰
	private String phoneNum;
	//�ͷ��绰
	private String serviceNum;
}
