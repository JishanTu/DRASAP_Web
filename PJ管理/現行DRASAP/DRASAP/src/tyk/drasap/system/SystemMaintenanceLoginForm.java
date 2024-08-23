package tyk.drasap.system;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * EasyStruts created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 12-10-2003
 * 
 * XDoclet definition:
 * @struts:form name="LoginForm"
 */
public class SystemMaintenanceLoginForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String act;
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
		this.id = id;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

}
