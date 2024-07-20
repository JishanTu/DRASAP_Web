package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

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
public class LoginForm extends BaseForm {
	/**
	 *
	 */
	private String passwd;
	private String id;

	// --------------------------------------------------------- Methods

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {
		// �N�b�L�[���猾��ݒ���擾
		//		CookieManage langCookie = new CookieManage();
		//		String lanKey = langCookie.getCookie (request, null, "Language");
		//		if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";

		// ���̓`�F�b�N�E�E�EId
		if (id == null || id.length() == 0) {
			// ���͂���Ă��Ȃ����
			MessageSourceUtil.addAttribute(errors, "loginId", messageSource.getMessage("root.required", new Object[] { "ID" }, null));
			//			if (lanKey.equals("Japanese")) {
			//				MessageSourceUtil.addAttribute(errors, "loginId", messageSource.getMessage("root.required.jp","Id"));
			//			} else {
			//				MessageSourceUtil.addAttribute(errors, "loginId", messageSource.getMessage("root.required.en","Id"));
			//			}
		}
		// ���̓`�F�b�N�E�E�EPassword
		if (passwd == null || passwd.length() == 0) {
			MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("root.required", new Object[] { "Password" }, null));
			//			if (lanKey.equals("Japanese")) {
			//				MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("root.required.jp","Password"));
			//			} else {
			//				MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("root.required.en","Password"));
			//			}
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

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 * @param id The id to set
	 */
	public void setId(String id) {
		// ���O�C��ID������̖����̋󔒏��� 2021/01/15 K.Tanaka
		// this.id = id;
		this.id = id.trim();
	}

}
