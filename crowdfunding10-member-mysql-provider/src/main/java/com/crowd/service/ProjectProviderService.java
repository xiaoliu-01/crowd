package com.crowd.service;

import java.util.List;


import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.entity.vo.ProjectVO;

public interface ProjectProviderService {

	void saveObjectVO(ProjectVO projectVO, Integer id);

	List<PortalTypeVO> getPortalTypeVO();
  
	DetailProjectVO selectDetailProjectVO(Integer projectId);
	
}
