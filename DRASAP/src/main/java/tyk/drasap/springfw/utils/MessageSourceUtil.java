package tyk.drasap.springfw.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import tyk.drasap.springfw.config.ContextProvider;

public class MessageSourceUtil {
	public static MessageSource getMessageSource() {
		ApplicationContext context = ContextProvider.getApplicationContext();
		if (context != null) {
			return context.getBean(MessageSource.class);
		}
		throw new IllegalStateException("ApplicationContext is not initialized yet");
	}

	@SuppressWarnings("unchecked")
	public static void addAttribute(Model model, String key, String message) {
		if (model.containsAttribute(key)) {
			Object msgs = model.getAttribute(key);
			if (msgs instanceof List) {
				((List<String>) msgs).add(message);
				model.addAttribute(key, msgs);
			}
		} else {
			List<String> list = new ArrayList<>();
			list.add(message);
			model.addAttribute(key, list);
		}
	}
}
