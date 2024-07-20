package tyk.drasap.springfw.form;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

public class BaseForm implements Validatable {
	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {
		return null;
	}

}
