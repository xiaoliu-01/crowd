package com.crowd.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crowd.Constant.CrowdConstant;
import com.crowd.api.MySQLRemoteService;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.util.ResultEntity;

@Controller
public class PortalHandler {
	
	@Autowired
	private MySQLRemoteService mySQLRemoteService; 
	
	@RequestMapping(value = "/")
	public String showPortalPage(ModelMap modelMap ) {
		
		ResultEntity<List<PortalTypeVO>> portalTypeProjectDataRemote = mySQLRemoteService.getPortalTypeProjectDataRemote();
		if(ResultEntity.SUCCESS.equals(portalTypeProjectDataRemote.getOperationResult())) {
			List<PortalTypeVO> queryData = portalTypeProjectDataRemote.getQueryData();
			modelMap.addAttribute(CrowdConstant.ATTR_NAME_PORTAL_DATA, queryData);
		}
		
		
		return "portal";
	}
}
