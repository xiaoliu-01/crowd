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
		// 一、保存ProjectPO对象
		// 1.创建空的ProjectPO对象
		ProjectPO projectPO = new ProjectPO();
		// 2.把projectVO中的属性复制到projectPO中
		BeanUtils.copyProperties(projectVO, projectPO);
		// 3.把memberId设置到projectPO中
		projectPO.setMemberid(id);
		// 4.生成创建时间
		String createDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		projectPO.setCreatedate(createDate);
		// 5.status设置成0，表示即将开始
		projectPO.setStatus(0);
		// 6.执行保存
		int insertSelective = projectPOMapper.insertSelective(projectPO);
		// 7.获取自增值
		Integer projectId  = projectPO.getId();
		// 二、保存项目、分类的关联关系信息
		List<Integer> typeIdList = projectVO.getTypeIdList();
		projectPOMapper.insertTypeRelationship(typeIdList, projectId);
		// 三、保存项目、标签的关联关系信息
		List<Integer> tagIdList = projectVO.getTagIdList();
		projectPOMapper.insertTagRelationship(tagIdList, projectId);
		// 四、保存项目中详情图片路径信息
		List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
		
		projectItemPicPOMapper.insertPathList(projectId,detailPicturePathList);
		// 五、保存项目发起人信息
		MemberLauchInfoVO memberLauchInfoVO = projectVO.getMemberLauchInfoVO();
		MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
		BeanUtils.copyProperties(memberLauchInfoVO, memberLaunchInfoPO);
		memberLaunchInfoPO.setMemberid(id);
		launchInfoPOMapper.insertSelective(memberLaunchInfoPO);
		// 六、保存项目回报信息
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
		// 七、保存项目确认信息
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
		// 1.查询得到DetailProjectVO对象
		DetailProjectVO selectDetailProjectVO = projectPOMapper.selectDetailProjectVO(projectId);
		
		// 2.根据status确定statusText
		Integer status = selectDetailProjectVO.getStatus();
		
		switch (status) {
		case 0:
			selectDetailProjectVO.setStatusText("审核中...");
			break;
		case 1:
			selectDetailProjectVO.setStatusText("众筹中...");
			break;
		case 2:
			selectDetailProjectVO.setStatusText("众筹完成！");
			break;
		case 3:
			selectDetailProjectVO.setStatusText("已关闭");
			break;
		default:
			break;
		}
		// 3.根据deployeDate计算lastDay
		String deployDay = selectDetailProjectVO.getDeployDate();
		
		Date currentDay = new Date();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date deployDays = simpleDateFormat.parse(deployDay);
			
			// 获取当前当前日期的时间戳
			long currentTimeStamp  = currentDay.getTime();
			
			// 获取众筹日期的时间戳
			long deployTimeStamp  = deployDays.getTime();
			
			// 两个时间戳相减计算当前已经过去的时间
			long pastDays = (currentTimeStamp - deployTimeStamp) /1000 /60 /60 / 24 ;
			
			// 获取总的众筹天数
			Integer totalDays  = selectDetailProjectVO.getDay();
			
			// 依照总众筹天数与已过天数计算出剩余天数
			Integer lastDays = (int) (totalDays - pastDays);
			
			// 将剩余天数封装回 DetailProjectVO 对象
			selectDetailProjectVO.setLastDay(lastDays);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return selectDetailProjectVO;
	}
}
