package com.crowd.service.Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crowd.Constant.CrowdConstant;
import com.crowd.entity.po.MemberConfigInfoPO;
import com.crowd.entity.po.MemberLaunchInfoPO;
import com.crowd.entity.po.ProjectPO;
import com.crowd.entity.po.ReturnPO;
import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.MemberConfirmInfoVO;
import com.crowd.entity.vo.MemberLauchInfoVO;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.entity.vo.ReturnVO;
import com.crowd.mapper.MemberConfigInfoPOMapper;
import com.crowd.mapper.MemberLaunchInfoPOMapper;
import com.crowd.mapper.ProjectItemPicPOMapper;
import com.crowd.mapper.ProjectPOMapper;
import com.crowd.mapper.ReturnPOMapper;
import com.crowd.service.ProjectProviderService;

@Transactional(readOnly = true)
@Service
public class ProjectProviderServiceImpl implements ProjectProviderService {
  
	@Autowired
	private ProjectPOMapper projectPOMapper;
    
	@Autowired
	private ProjectItemPicPOMapper projectItemPicPOMapper;
	
	@Autowired
	private MemberLaunchInfoPOMapper launchInfoPOMapper;
	
	@Autowired
	private ReturnPOMapper returnPOMapper ;
	
	@Resource
	private MemberConfigInfoPOMapper configInfoPOMapper ;
	
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	@Override
	public void saveObjectVO(ProjectVO projectVO, Integer id) {
		// һ������ProjectPO����
		// 1.�����յ�ProjectPO����
		ProjectPO projectPO = new ProjectPO();
		// 2.��projectVO�е����Ը��Ƶ�projectPO��
		BeanUtils.copyProperties(projectVO, projectPO);
		// 3.��memberId���õ�projectPO��
		projectPO.setMemberid(id);
		// 4.���ɴ���ʱ��
		String createDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		projectPO.setCreatedate(createDate);
		// 5.status���ó�0����ʾ������ʼ
		projectPO.setStatus(0);
		// 6.ִ�б���
		int insertSelective = projectPOMapper.insertSelective(projectPO);
		// 7.��ȡ����ֵ
		Integer projectId  = projectPO.getId();
		// ����������Ŀ������Ĺ�����ϵ��Ϣ
		List<Integer> typeIdList = projectVO.getTypeIdList();
		projectPOMapper.insertTypeRelationship(typeIdList, projectId);
		// ����������Ŀ����ǩ�Ĺ�����ϵ��Ϣ
		List<Integer> tagIdList = projectVO.getTagIdList();
		projectPOMapper.insertTagRelationship(tagIdList, projectId);
		// �ġ�������Ŀ������ͼƬ·����Ϣ
		List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
		
		projectItemPicPOMapper.insertPathList(projectId,detailPicturePathList);
		// �塢������Ŀ��������Ϣ
		MemberLauchInfoVO memberLauchInfoVO = projectVO.getMemberLauchInfoVO();
		MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
		BeanUtils.copyProperties(memberLauchInfoVO, memberLaunchInfoPO);
		memberLaunchInfoPO.setMemberid(id);
		launchInfoPOMapper.insertSelective(memberLaunchInfoPO);
		// ����������Ŀ�ر���Ϣ
		List<ReturnVO> returnVOList = projectVO.getReturnVOList();
		ArrayList<ReturnPO> arrayList = new ArrayList<>();
		if(returnVOList == null || returnVOList.size() == 0) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		for (ReturnVO returnVO  : returnVOList) {
			ReturnPO returnPO = new ReturnPO();
			BeanUtils.copyProperties(returnVO, returnPO);
			arrayList.add(returnPO);
		}
		returnPOMapper.insertReturnPOBatch(arrayList, projectId);
		// �ߡ�������Ŀȷ����Ϣ
		MemberConfirmInfoVO memberConfirmInfoVO = projectVO.getMemberConfirmInfoVO();
		MemberConfigInfoPO memberConfigInfoPO = new MemberConfigInfoPO();
		BeanUtils.copyProperties(memberConfirmInfoVO, memberConfigInfoPO);
		memberConfigInfoPO.setMemberid(id);
		configInfoPOMapper.insertSelective(memberConfigInfoPO);
	}

	@Override
	public List<PortalTypeVO> getPortalTypeVO() {
		// TODO Auto-generated method stub
		return projectPOMapper.selectPortalTypeVOList();
	}

	@Override
	public DetailProjectVO selectDetailProjectVO(Integer projectId)  {
		// 1.��ѯ�õ�DetailProjectVO����
		DetailProjectVO selectDetailProjectVO = projectPOMapper.selectDetailProjectVO(projectId);
		
		// 2.����statusȷ��statusText
		Integer status = selectDetailProjectVO.getStatus();
		
		switch (status) {
		case 0:
			selectDetailProjectVO.setStatusText("�����...");
			break;
		case 1:
			selectDetailProjectVO.setStatusText("�ڳ���...");
			break;
		case 2:
			selectDetailProjectVO.setStatusText("�ڳ���ɣ�");
			break;
		case 3:
			selectDetailProjectVO.setStatusText("�ѹر�");
			break;
		default:
			break;
		}
		// 3.����deployeDate����lastDay
		String deployDay = selectDetailProjectVO.getDeployDate();
		
		Date currentDay = new Date();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date deployDays = simpleDateFormat.parse(deployDay);
			
			// ��ȡ��ǰ��ǰ���ڵ�ʱ���
			long currentTimeStamp  = currentDay.getTime();
			
			// ��ȡ�ڳ����ڵ�ʱ���
			long deployTimeStamp  = deployDays.getTime();
			
			// ����ʱ���������㵱ǰ�Ѿ���ȥ��ʱ��
			long pastDays = (currentTimeStamp - deployTimeStamp) /1000 /60 /60 / 24 ;
			
			// ��ȡ�ܵ��ڳ�����
			Integer totalDays  = selectDetailProjectVO.getDay();
			
			// �������ڳ��������ѹ����������ʣ������
			Integer lastDays = (int) (totalDays - pastDays);
			
			// ��ʣ��������װ�� DetailProjectVO ����
			selectDetailProjectVO.setLastDay(lastDays);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return selectDetailProjectVO;
	}
}
