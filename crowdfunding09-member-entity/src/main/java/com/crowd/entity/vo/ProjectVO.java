package com.crowd.entity.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// ���� id ����
	private List<Integer> typeIdList;
    //��ǩ id ����
	private List<Integer> tagIdList;
    //��Ŀ����
	private String projectName;
    //��Ŀ����
	private String projectDescription;
    //�ƻ��Ｏ�Ľ��
	private Integer money;
    //�Ｏ�ʽ������
	private Integer day;
    //������Ŀ������
	private String createdate;
    //ͷͼ��·��
	private String headerPicturePath;
    //����ͼƬ��·��
	private List<String> detailPicturePathList;
    //��������Ϣ
	private MemberLauchInfoVO memberLauchInfoVO;
    //�ر���Ϣ����
	private List<ReturnVO> returnVOList;
    //������ȷ����Ϣ
	private MemberConfirmInfoVO memberConfirmInfoVO;
}
