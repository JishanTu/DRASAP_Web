package tyk.drasap.search;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.common.User;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * LoginForm.java created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 12-10-2003
 *
 * XDoclet definition:
 * @struts:form name="LoginForm"
 */
@Component
public class Delete_LoginForm extends BaseForm {
	/**
	 *
	 */
	private String passwd;

	// --------------------------------------------------------- Methods

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {

		User user = (User) request.getSession().getAttribute("user");

		// 入力チェック・・・Password
		if (passwd == null || passwd.length() == 0) {
			MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("search.required." + user.getLanKey(), new Object[] { "Password" }, null));
		}
		return errors;
	}

	// --------------------------------------------------------- getter,setter
	/**
	 * Returns the passwd.
	 * @return String
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * Set the passwd.
	 * @param passwd The passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
