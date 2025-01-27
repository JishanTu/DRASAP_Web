package tyk.drasap.springfw.form;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

public interface Validatable {
	Model validate(HttpServletRequest request, Model errors, MessageSource messageSource);
}
