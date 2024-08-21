package tyk.drasap.search;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import tyk.drasap.common.User;

/** 
 * LoginForm.java created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 12-10-2003
 * 
 * XDoclet definition:
 * @struts:form name="LoginForm"
 */
public class Delete_LoginForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String passwd;

	// --------------------------------------------------------- Methods

	/** 
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		User user = (User)request.getSession().getAttribute("user");
		
		// 入力チェック・・・Password
		if(passwd == null || passwd.length()==0){
			errors.add("passwd", new ActionMessage("search.required." + user.getLanKey(),"Password"));
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
