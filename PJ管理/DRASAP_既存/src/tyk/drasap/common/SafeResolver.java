package tyk.drasap.common;

import org.apache.commons.beanutils.expression.DefaultResolver;

/**
 * SafeResolver
 * (StrutsÆã«‘Î‰: CVE-2014-0094)
 *
 * @author 2014/04/28 yamagishi
 */
public class SafeResolver extends DefaultResolver {
	/**
	 * next
	 * @param expression
	 * @return property
	 */
	@Override
	public String next(String expression) {
		String property = super.next(expression);
		if ("class".equalsIgnoreCase(property)) {
			return "";
		}
		return property;
	}
}
