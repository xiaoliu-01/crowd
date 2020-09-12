package com.crowd.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.service.ProjectProviderService;
import com.crowd.util.ResultEntity;

@RestController
public class ProjectProviderHandler {
  
	@Autowired
	private ProjectProviderService projectProviderService;
	
	@RequestMapping(value = "/get/project/detail/remote/{projectId}")
	public ResultEntity<DetailProjectVO> getDetailProjectVORemote(@PathVariable("projectId") Integer projectId){
		
		try {
			DetailProjectVO selectDetailProjectVO = projectProviderService.selectDetailProjectVO(projectId);
			return  ResultEntity.successWithData(selectDetailProjectVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  ResultEntity.failed(e.getMessage());
		}
		
	}
	
	@RequestMapping(value = "/get/portal/type/project/remote")
	public ResultEntity<List<PortalTypeVO>> getPortalTypeProjectDataRemote(){
		try {
			List<PortalTypeVO> portalTypeVOs = projectProviderService.getPortalTypeVO();
			 return ResultEntity.successWithData(portalTypeVOs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/save/object/vo/remote")
	public ResultEntity<String> saveObjectVORemote(@RequestBody ProjectVO projectVO,@RequestParam("id") Integer id){
		
		// 调用本地方法执行保存
		try {
			projectProviderService.saveObjectVO(projectVO,id);
			// 保存成功返回成功信息
			return ResultEntity.successWithoutData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 保存失败，返回失败信息
			return ResultEntity.failed(e.getMessage());
		}
	}
}
