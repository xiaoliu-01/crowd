package com.crowd.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.crowd.Constant.CrowdConstant;
import com.crowd.api.MySQLRemoteService;
import com.crowd.config.OSSProperties;
import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.MemberConfirmInfoVO;
import com.crowd.entity.vo.MemberLoginVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.entity.vo.ReturnVO;
import com.crowd.util.CrowdUtil;
import com.crowd.util.ResultEntity;

@Controller
public class ProjectConsumerHandler {
	
	@Autowired
	private OSSProperties ossProperties;
	@Autowired
	private MySQLRemoteService mySQLRemoteService;
	
	@RequestMapping(value = "/get/project/detail/{projectId}")
	public String getProjectDetailByProjectId(@PathVariable("projectId") Integer projectId , ModelMap modelMap) {
		ResultEntity<DetailProjectVO> detailProjectVOResult = mySQLRemoteService.getDetailProjectVORemote(projectId);
		if(ResultEntity.FAILED.equals(detailProjectVOResult.getOperationResult())) {
			throw new RuntimeException(CrowdConstant.ATTR_NAME_DETAILPROJECTVO_IS_NOT);
		}
		DetailProjectVO  detailProjectVOData = detailProjectVOResult.getQueryData();
		modelMap.addAttribute("detailProjectVO", detailProjectVOData);
		return "project-show-detail";
	}
	
	@RequestMapping(value = "/create/confirm")
	public String saveConfirm(ModelMap modelMap, HttpSession session, MemberConfirmInfoVO memberConfirmInfoVO) {
		// 1.�� Session ���ȡ֮ǰ��ʱ�洢�� ProjectVO ����
		ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
		// 2.�ǿ��ж�
		if(projectVO == null) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		// 3.��ȷ����Ϣ�������õ� projectVO ������
		projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);
		// 4.�� Session ���ȡ��ǰ��¼���û�
		MemberLoginVO  memberLoginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		if(memberLoginVO == null) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		Integer id = memberLoginVO.getId();
		
		// 5.����Զ�̷������� projectVO ����
		ResultEntity<String> saveResultEntity = mySQLRemoteService.saveObjectVORemote(projectVO,id);
		// 6.�ж�Զ�̵ı�������Ƿ�ɹ�
		if(ResultEntity.FAILED.equals(saveResultEntity.getOperationResult())) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,saveResultEntity.getOperationMessage());
		}
		// 7.����ɹ����Ƴ�session
		session.removeAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
		// 8.���Զ�̱���ɹ�����ת���������ҳ��
		return "redirect:http://www.crowd.com/project/create/success";

	}
	
	@ResponseBody
	@RequestMapping("/create/save/return.json")
	public ResultEntity<String> saveReturn(ReturnVO returnVO, HttpSession session) {
		
		try {
			// 1.��session���ж�ȡ֮ǰ�����ProjectVO����
			ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
			
			// 2.�ж�projectVO�Ƿ�Ϊnull
			if(projectVO == null) {
				return ResultEntity.failed(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
			}
			
			// 3.��projectVO�����л�ȡ�洢�ر���Ϣ�ļ���
			List<ReturnVO> returnVOList = projectVO.getReturnVOList();
			
			// 4.�ж�returnVOList�����Ƿ���Ч
			if(returnVOList == null || returnVOList.size() == 0) {
				
				// 5.�������϶����returnVOList���г�ʼ��
				returnVOList = new ArrayList<>();
				// 6.Ϊ�����Ժ��ܹ�����ʹ��������ϣ����õ�projectVO������
				projectVO.setReturnVOList(returnVOList);
			}
			
			// 7.���ռ��˱����ݵ�returnVO������뼯��
			returnVOList.add(returnVO);
			
			projectVO.setReturnVOList(returnVOList);
			
			// 8.�������б仯��ProjectVO�������´���Session����ȷ���µ����������ܹ�����Redis
			session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
			
			// 9.���в����ɹ���ɷ��سɹ�
			return ResultEntity.successWithoutData();
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResultEntity.failed(e.getMessage());
		}
		
	}
	
	@ResponseBody
	@RequestMapping("/create/upload/return/picture.json")
	public ResultEntity<String> uploadReturnPicture(@RequestParam("returnPicture") MultipartFile returnPicture) throws IOException{
		// 1.ִ���ļ��ϴ�
		ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),returnPicture.getInputStream(),
				ossProperties.getBucketName(), ossProperties.getBucketDomain(), returnPicture.getOriginalFilename());
		// 2.�����ϴ����
		return uploadFileToOss;
	}

	
	
	/**
	 * 
	 * @param projectVO          // ���ճ����ϴ�ͼƬ֮���������ͨ����
	 * @param headerPicture      // �����ϴ���ͷͼ
	 * @param detailPictureList  // �����ϴ�������ͼƬ 
	 * @param session            // �������ռ���һ�������ݵ� ProjectVO ������� Session ��
	 * @param modelMap           // �����ڵ�ǰ����ʧ�ܺ󷵻���һ����ҳ��ʱЯ����ʾ��Ϣ
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/create/project/information")
	public String saveProjectBasicInfo(ProjectVO projectVO ,MultipartFile headerPicture ,List<MultipartFile> detailPictureList,
									HttpSession session ,ModelMap modelMap) throws IOException 
	{   
		// һ�����ͷͼ�ϴ�
		// 1.��ȡ��ǰ headerPicture �����Ƿ�Ϊ��
		boolean headerPictureEmpty = headerPicture.isEmpty();
		if(headerPictureEmpty) {
			// 2.���û���ϴ�ͷͼ�򷵻ص���ҳ�沢��ʾ������Ϣ
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_EMPTY);
			return "project-launch";
		}
		// 3.�����Ϊ�գ���ִ���ϴ�
		ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),headerPicture.getInputStream(),
				ossProperties.getBucketName(), ossProperties.getBucketDomain(), headerPicture.getOriginalFilename());
		
		// 4.�ж�ͷͼ�Ƿ��ϴ��ɹ�
		if(!ResultEntity.SUCCESS.equals(uploadFileToOss.getOperationResult())) {
			// 5.������ɹ����򷵻ش�����Ϣ
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_UPLOAD_FAILED);
			return "project-launch";
		}
		
		// 6.����ϴ��ɹ�����ȡ��ͷͼ·��
		String data = uploadFileToOss.getQueryData();
		// 7.��·�����뵽projectVO����
		projectVO.setHeaderPicturePath(data);
		
		// �����ϴ�����ͼƬ
		// 1.����һ�������������ͼƬ·���ļ���
		List<String> detailPicturePathList = new ArrayList<String>();
        // 2.���detailPictureList�Ƿ���Ч
		if(detailPictureList == null || detailPictureList.size() == 0) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
			return "project-launch";
		}
		
		// 3.����detailPicturePathList
		for (MultipartFile detailPicture : detailPictureList) {
			boolean detailPictureEmpty = detailPicture.isEmpty();
			// 4.�ж���ϸͼƬ�Ƿ�Ϊ��
			if(detailPictureEmpty) {
				modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
				return "project-launch";
			}
			// 5.ִ���ϴ�
			ResultEntity<String> detailUploadResultEntity  = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),detailPicture.getInputStream(),
					ossProperties.getBucketName(), ossProperties.getBucketDomain(), detailPicture.getOriginalFilename());
			// 6.����ϴ��Ƿ�ɹ�
			if(!ResultEntity.SUCCESS.equals(detailUploadResultEntity.getOperationResult())){
				modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_UPLOAD_FAILED);
				return "project-launch";
			}
			// 7.�ϴ��ɹ�������뵽detailPicturePathList
			String detailData = detailUploadResultEntity.getQueryData();
			detailPicturePathList.add(detailData);
		}
		// 10.�����������ͼƬ����·���ļ��ϴ��� ProjectVO ��
		projectVO.setDetailPicturePathList(detailPicturePathList);
		// �����ѽ�����뵽Session�򣬲�����ҳ����ת
		session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
		
		// 2.�������ķ���·��ǰ����һ���ռ��ر���Ϣ��ҳ��
		return "redirect:http://www.crowd.com/project/return/info/page";
	}
}
