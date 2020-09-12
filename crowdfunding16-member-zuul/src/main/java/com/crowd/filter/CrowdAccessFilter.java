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
		// 1.获取 RequestContext 对象
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 2.通过 RequestContext 对象获取当前请求对象（框架底层是借助 ThreadLocal 从当前线程上获取事先绑定的 Request 对象）
		HttpServletRequest request = requestContext.getRequest();
		// 3.获取 servletPath 值
		String contextPath = request.getServletPath();
		// 4.根据 servletPath 判断当前请求是否对应可以直接放行的特定功能
		boolean staticResources = AccessPassResources.PASS_RES_SET.contains(contextPath);
		if(staticResources) {
			// 5.如果当前请求是可以直接放行的特定功能请求则返回 false 放行
			return false;
		}
		
		   // 6.判断是否为放行的静态资源
		// 工具方法返回 true：说明当前请求是静态资源请求，取反为 false 表示放行不做登录检查
		// 工具方法返回 false：说明当前请求不是可以放行的特定请求也不是静态资源，取反为 true 表示需要做登录检查
		return !AccessPassResources.judgeStaticResources(contextPath);
	}

	@Override
	public Object run() throws ZuulException {
		// 1.获取 RequestContext 对象
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 2.通过 RequestContext 对象获取当前请求对象（框架底层是借助 ThreadLocal 从当前线程上获取事先绑定的 Request 对象）
		HttpServletRequest request = requestContext.getRequest();
		// 3.获取session
		HttpSession session = request.getSession();
		// 4.尝试从 Session 对象中获取已登录的用户
		Object member = session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		if(member == null) {
			// 5.把错误信息存放到session域
			session.setAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.ATTR_NAME_ACCESS_BIDDEN_EXCEPTION);
			// 6.通过 RequestContext,获取response
			HttpServletResponse response = requestContext.getResponse();
			// 7.重定向到登录页
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
		// 在目标微服务之前执行
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
