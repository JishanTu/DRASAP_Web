package tyk.drasap.search;

import tyk.drasap.springfw.form.BaseForm;

/**
 * 削除画面に対応
 */
public class DirectPreviewForm extends BaseForm {
	String drwgNo[];

	public String[] getDrwgNo() {
		return this.drwgNo;
	}

	public void setDrwgNo(String[] drwgNo) {
		this.drwgNo = drwgNo;
	}
}
