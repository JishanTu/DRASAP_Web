package tyk.drasap.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * setCharacterEncodingするためのFilter。
 * しかしSolalisのWebLogicでは、web.xmlにFilterタグがあるとデプロイできなかったので、
 * 使用しないように変更する。 '04.Mar.8 by Hirata
 * @version 2013/07/17 yamagishi Windowsサーバへの移行に伴い、再使用する。
 * @version 2013/09/04 yamagishi デフォルト文字コードを保持する変数を追加。
 */
public class SetCharacterEncodingFilter implements Filter {
	static String DEFAULT_ENCODING = null;
	protected String encoding = null;
	protected boolean ignore = true;

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.encoding = filterConfig.getInitParameter("encoding");
		// 2013.09.04 yamagishi add. start
		if (DEFAULT_ENCODING == null) {
			DEFAULT_ENCODING = this.encoding;
		}
		// 2013.09.04 yamagishi add. end
		String value = filterConfig.getInitParameter("ignore");
		if ((value == null) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) {
			this.ignore = true;
		} else {
			this.ignore = false;
		}
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		// Conditionally select and set the character encoding to be used
		if (this.ignore || req.getCharacterEncoding() == null) {
			if (this.encoding != null) {
				req.setCharacterEncoding(this.encoding);
				//System.out.println("setCharacterEncoding("+this.encoding+")した");
			}
		}

		// Pass control on to the next filter
		chain.doFilter(req, res);

	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		this.encoding = null;

	}

}
