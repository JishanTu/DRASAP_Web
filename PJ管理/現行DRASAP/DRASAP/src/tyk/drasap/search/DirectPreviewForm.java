package tyk.drasap.search;

import org.apache.struts.action.ActionForm;


/**
 * 削除画面に対応
 */
public class DirectPreviewForm extends ActionForm {

	private static final long serialVersionUID = 4060745795707916301L;
	String drwgNo[];
	public String[] getDrwgNo() {
		return drwgNo;
	}
	public void setDrwgNo(String[] drwgNo) {
		this.drwgNo = drwgNo;
	}
}
