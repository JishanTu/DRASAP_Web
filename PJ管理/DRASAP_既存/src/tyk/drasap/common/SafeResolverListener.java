package tyk.drasap.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * SafeResolverListener
 * (StrutsÆã«‘Î‰: CVE-2014-0094)
 *
 * @author 2014/04/28 yamagishi
 */
public class SafeResolverListener implements ServletContextListener {
	/**
	 * contextInitialized
	 * @param event
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		SafeResolver resolver = new SafeResolver();
		BeanUtilsBean.getInstance().getPropertyUtils().setResolver(resolver);
	}

	/**
	 * contextDestroyed
	 * @param event
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
