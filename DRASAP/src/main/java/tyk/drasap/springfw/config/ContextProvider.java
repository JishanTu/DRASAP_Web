package tyk.drasap.springfw.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	public ContextProvider() {
		System.out.println("ContextProvider start ......");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}
}