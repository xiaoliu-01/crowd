package com.crowd.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.crowd.Constant.AccessPassResources;
import com.crowd.Constant.CrowdConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
@Component
public class CrowdAccessFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		// 1.��ȡ RequestContext ����
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 2.ͨ�� RequestContext �����ȡ��ǰ������󣨿�ܵײ��ǽ��� ThreadLocal �ӵ�ǰ�߳��ϻ�ȡ���Ȱ󶨵� Request ����
		HttpServletRequest request = requestContext.getRequest();
		// 3.��ȡ servletPath ֵ
		String contextPath = request.getServletPath();
		// 4.���� servletPath �жϵ�ǰ�����Ƿ��Ӧ����ֱ�ӷ��е��ض�����
		boolean staticResources = AccessPassResources.PASS_RES_SET.contains(contextPath);
		if(staticResources) {
			// 5.�����ǰ�����ǿ���ֱ�ӷ��е��ض����������򷵻� false ����
			return false;
		}
		
		   // 6.�ж��Ƿ�Ϊ���еľ�̬��Դ
		// ���߷������� true��˵����ǰ�����Ǿ�̬��Դ����ȡ��Ϊ false ��ʾ���в�����¼���
		// ���߷������� false��˵����ǰ�����ǿ��Է��е��ض�����Ҳ���Ǿ�̬��Դ��ȡ��Ϊ true ��ʾ��Ҫ����¼���
		return !AccessPassResources.judgeStaticResources(contextPath);
	}

	@Override
	public Object run() throws ZuulException {
		// 1.��ȡ RequestContext ����
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 2.ͨ�� RequestContext �����ȡ��ǰ������󣨿�ܵײ��ǽ��� ThreadLocal �ӵ�ǰ�߳��ϻ�ȡ���Ȱ󶨵� Request ����
		HttpServletRequest request = requestContext.getRequest();
		// 3.��ȡsession
		HttpSession session = request.getSession();
		// 4.���Դ� Session �����л�ȡ�ѵ�¼���û�
		Object member = session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		if(member == null) {
			// 5.�Ѵ�����Ϣ��ŵ�session��
			session.setAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.ATTR_NAME_ACCESS_BIDDEN_EXCEPTION);
			// 6.ͨ�� RequestContext,��ȡresponse
			HttpServletResponse response = requestContext.getResponse();
			// 7.�ض��򵽵�¼ҳ
			try {
				response.sendRedirect("/auth/member/to/login/page");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public String filterType() {
		// ��Ŀ��΢����֮ǰִ��
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
