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
		// 1.从 Session 域读取之前临时存储的 ProjectVO 对象
		ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
		// 2.非空判断
		if(projectVO == null) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		// 3.将确认信息数据设置到 projectVO 对象中
		projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);
		// 4.从 Session 域读取当前登录的用户
		MemberLoginVO  memberLoginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		if(memberLoginVO == null) {
			throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
		}
		Integer id = memberLoginVO.getId();
		
		// 5.调用远程方法保存 projectVO 对象
		ResultEntity<String> saveResultEntity = mySQLRemoteService.saveObjectVORemote(projectVO,id);
		// 6.判断远程的保存操作是否成功
		if(ResultEntity.FAILED.equals(saveResultEntity.getOperationResult())) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,saveResultEntity.getOperationMessage());
		}
		// 7.保存成功，移除session
		session.removeAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
		// 8.如果远程保存成功则跳转到最终完成页面
		return "redirect:http://www.crowd.com/project/create/success";

	}
	
	@ResponseBody
	@RequestMapping("/create/save/return.json")
	public ResultEntity<String> saveReturn(ReturnVO returnVO, HttpSession session) {
		
		try {
			// 1.从session域中读取之前缓存的ProjectVO对象
			ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
			
			// 2.判断projectVO是否为null
			if(projectVO == null) {
				return ResultEntity.failed(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
			}
			
			// 3.从projectVO对象中获取存储回报信息的集合
			List<ReturnVO> returnVOList = projectVO.getReturnVOList();
			
			// 4.判断returnVOList集合是否有效
			if(returnVOList == null || returnVOList.size() == 0) {
				
				// 5.创建集合对象对returnVOList进行初始化
				returnVOList = new ArrayList<>();
				// 6.为了让以后能够正常使用这个集合，设置到projectVO对象中
				projectVO.setReturnVOList(returnVOList);
			}
			
			// 7.将收集了表单数据的returnVO对象存入集合
			returnVOList.add(returnVO);
			
			projectVO.setReturnVOList(returnVOList);
			
			// 8.把数据有变化的ProjectVO对象重新存入Session域，以确保新的数据最终能够存入Redis
			session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
			
			// 9.所有操作成功完成返回成功
			return ResultEntity.successWithoutData();
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResultEntity.failed(e.getMessage());
		}
		
	}
	
	@ResponseBody
	@RequestMapping("/create/upload/return/picture.json")
	public ResultEntity<String> uploadReturnPicture(@RequestParam("returnPicture") MultipartFile returnPicture) throws IOException{
		// 1.执行文件上传
		ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),returnPicture.getInputStream(),
				ossProperties.getBucketName(), ossProperties.getBucketDomain(), returnPicture.getOriginalFilename());
		// 2.返回上传结果
		return uploadFileToOss;
	}

	
	
	/**
	 * 
	 * @param projectVO          // 接收除了上传图片之外的其他普通数据
	 * @param headerPicture      // 接收上传的头图
	 * @param detailPictureList  // 接收上传的详情图片 
	 * @param session            // 用来将收集了一部分数据的 ProjectVO 对象存入 Session 域
	 * @param modelMap           // 用来在当前操作失败后返回上一个表单页面时携带提示消息
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/create/project/information")
	public String saveProjectBasicInfo(ProjectVO projectVO ,MultipartFile headerPicture ,List<MultipartFile> detailPictureList,
									HttpSession session ,ModelMap modelMap) throws IOException 
	{   
		// 一、完成头图上传
		// 1.获取当前 headerPicture 对象是否为空
		boolean headerPictureEmpty = headerPicture.isEmpty();
		if(headerPictureEmpty) {
			// 2.如果没有上传头图则返回到表单页面并显示错误消息
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_EMPTY);
			return "project-launch";
		}
		// 3.如果不为空，则执行上传
		ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),headerPicture.getInputStream(),
				ossProperties.getBucketName(), ossProperties.getBucketDomain(), headerPicture.getOriginalFilename());
		
		// 4.判断头图是否上传成功
		if(!ResultEntity.SUCCESS.equals(uploadFileToOss.getOperationResult())) {
			// 5.如果不成功，则返回错误信息
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_UPLOAD_FAILED);
			return "project-launch";
		}
		
		// 6.如果上传成功，则取出头图路径
		String data = uploadFileToOss.getQueryData();
		// 7.把路劲存入到projectVO对象
		projectVO.setHeaderPicturePath(data);
		
		// 二、上传详情图片
		// 1.创建一个用来存放详情图片路径的集合
		List<String> detailPicturePathList = new ArrayList<String>();
        // 2.检查detailPictureList是否有效
		if(detailPictureList == null || detailPictureList.size() == 0) {
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
			return "project-launch";
		}
		
		// 3.遍历detailPicturePathList
		for (MultipartFile detailPicture : detailPictureList) {
			boolean detailPictureEmpty = detailPicture.isEmpty();
			// 4.判断详细图片是否为空
			if(detailPictureEmpty) {
				modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
				return "project-launch";
			}
			// 5.执行上传
			ResultEntity<String> detailUploadResultEntity  = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(),detailPicture.getInputStream(),
					ossProperties.getBucketName(), ossProperties.getBucketDomain(), detailPicture.getOriginalFilename());
			// 6.检查上传是否成功
			if(!ResultEntity.SUCCESS.equals(detailUploadResultEntity.getOperationResult())){
				modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_UPLOAD_FAILED);
				return "project-launch";
			}
			// 7.上传成功，则存入到detailPicturePathList
			String detailData = detailUploadResultEntity.getQueryData();
			detailPicturePathList.add(detailData);
		}
		// 10.将存放了详情图片访问路径的集合存入 ProjectVO 中
		projectVO.setDetailPicturePathList(detailPicturePathList);
		// 三、把结果存入到Session域，并进行页面跳转
		session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
		
		// 2.以完整的访问路径前往下一个收集回报信息的页面
		return "redirect:http://www.crowd.com/project/return/info/page";
	}
}
