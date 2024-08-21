package tyk.drasap.springfw.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURI = httpRequest.getServletPath();
		if ("/login".equals(requestURI) || "/timeout".equals(requestURI) || "/logout".equals(requestURI) || "/getip".equals(requestURI) ||
				requestURI.contains("/notfound.jsp") || requestURI.contains("/syserror.jsp") || requestURI.contains("/loginPre.jsp")) {
			chain.doFilter(request, response);
			return;
		}
		if (httpRequest.getSession(false) == null || httpRequest.getSession().getAttribute("user") == null) {
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/timeout");
			return;
		}
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
	}
}