package com.crowd.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowd.entity.po.MemberOP;
import com.crowd.entity.vo.DetailProjectVO;
import com.crowd.entity.vo.DetailReturnVO;
import com.crowd.mapper.MemberOPMapper;
import com.crowd.mapper.ProjectPOMapper;
@SpringBootTest
@RunWith(SpringRunner.class)
public class CrowdTest {
    
	@Autowired
	private DataSource dataSource;
	
	@Resource
	private MemberOPMapper memberOPMapper;
	
	@Resource
	private ProjectPOMapper projectPOMapper;
	
	@Test
	public void testLoadDetailProjectVO() {
		
		Integer projectId = 14;
		
		DetailProjectVO detailProjectVO = projectPOMapper.selectDetailProjectVO(projectId);
		
		logger.info(detailProjectVO.getProjectId() + "");
		logger.info(detailProjectVO.getProjectName());
		logger.info(detailProjectVO.getProjectDesc());
		logger.info(detailProjectVO.getFollowerCount() + "");
		logger.info(detailProjectVO.getStatus() + "");
		logger.info(detailProjectVO.getMoney() + "");
		logger.info(detailProjectVO.getSupportMoney() + "");
		logger.info(detailProjectVO.getPercentage() + "");
		logger.info(detailProjectVO.getDeployDate()+ "");
		logger.info(detailProjectVO.getDay()+ "");
		logger.info(detailProjectVO.getSupporterCount() + "");
		logger.info(detailProjectVO.getHeaderPicturePath());
		
		List<String> detailPicturePathList = detailProjectVO.getDetailPicturePathList();
		for (String path : detailPicturePathList) {
			logger.info("detail path="+path);
		}
		
		List<DetailReturnVO> detailReturnVOList = detailProjectVO.getDetailReturnVOList();
		for (DetailReturnVO detailReturnVO : detailReturnVOList) {
			logger.info(detailReturnVO.getReturnId() + "");
			logger.info(detailReturnVO.getSupportMoney() + "");
			logger.info(detailReturnVO.getSignalPurchase() + "");
			logger.info(detailReturnVO.getPurchase() + "");
			logger.info(detailReturnVO.getSupproterCount() + "");
			logger.info(detailReturnVO.getFreight() + "");
			logger.info(detailReturnVO.getReturnDate() + "");
			logger.info(detailReturnVO.getContent() + "");
			logger.info(detailReturnVO.getFreight() + "");
		}
	}
	
	
	@Test
	public void testInsert() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = "123456";
		String encode = passwordEncoder.encode(password);
		memberOPMapper.insertSelective(new MemberOP(null, "Paul", encode, "БЃТо", "paul@qq.com", 2, 0, "Paul", null, 1));
		
		
	}
	
	
	private Logger logger = LoggerFactory.getLogger(CrowdTest.class);
	@Test
	public void testConnection() throws SQLException {
		Connection connection = dataSource.getConnection();
		logger.debug(connection.toString());
	}
	
}
