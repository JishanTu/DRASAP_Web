package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * ΄}ΙμΖΛπΜ½ίΜFormB
 * @author fumi
 */
public class RequestHistoryForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList historyList;
	private ArrayList errors;

	/* (ρ Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		historyList = new ArrayList();
		errors = new ArrayList ();
	}
	
	// -------------------------------------------getter,setter
	/**
	 * @return
	 */
	public ArrayList getErrors() {
		return errors;
	}

	/**
	 * @return
	 */
	public ArrayList getHistoryList() {
		return historyList;
	}

}
