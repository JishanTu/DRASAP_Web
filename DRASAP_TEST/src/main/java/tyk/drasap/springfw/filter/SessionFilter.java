package tyk.drasap.springfw.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter implements Filter {

	// 対象リスト
	List<String> targetList = List.of("result", "searchCondition");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String servletPath = httpRequest.getServletPath();

		String[] parts = servletPath.split("\\?");
		parts = parts[0].split("/");
		String target = parts[parts.length - 1];

		if (targetList.contains(target)) {
			// セッションにuser情報が存在しない場合、タイムアウト画面へ遷移
			if (httpRequest.getSession(false) == null || httpRequest.getSession().getAttribute("user") == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/timeout");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}