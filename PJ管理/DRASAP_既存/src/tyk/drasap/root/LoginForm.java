package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * LoginForm.java created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 12-10-2003
 *
 * XDoclet definition:
 * @struts:form name="LoginForm"
 */
public class LoginForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String passwd;
	private String id;

	// --------------------------------------------------------- Methods

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		// �N�b�L�[���猾��ݒ���擾
//		CookieManage langCookie = new CookieManage();
//		String lanKey = langCookie.getCookie (request, null, "Language");
//		if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";

		// ���̓`�F�b�N�E�E�EId
		if(id == null || id.length() == 0){
			// ���͂���Ă��Ȃ����
			errors.add("loginId", new ActionMessage("root.required","ID"));
//			if (lanKey.equals("Japanese")) {
//				errors.add("loginId", new ActionMessage("root.required.jp","Id"));
//			} else {
//				errors.add("loginId", new ActionMessage("root.required.en","Id"));
//			}
		}
		// ���̓`�F�b�N�E�E�EPassword
		if(passwd == null || passwd.length() == 0){
			errors.add("passwd", new ActionMessage("root.required","Password"));
//			if (lanKey.equals("Japanese")) {
//				errors.add("passwd", new ActionMessage("root.required.jp","Password"));
//			} else {
//				errors.add("passwd", new ActionMessage("root.required.en","Password"));
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
