package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * ΄}ΙμΖΛπΜ½ίΜFormB
 * @author fumi
 */
public class RequestHistoryForm extends BaseForm {
	/**
	 *
	 */
	private ArrayList<RequestHistoryElement> historyList;
	private ArrayList<String> errors;

	/* (ρ Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(HttpServletRequest request) {
		historyList = new ArrayList<>();
		errors = new ArrayList<>();
	}

	// -------------------------------------------getter,setter
	/**
	 * @return
	 */
	public ArrayList<String> getErrors() {
		return errors;
	}

	/**
	 * @return
	 */
	public ArrayList<RequestHistoryElement> getHistoryList() {
		return historyList;
	}

}
