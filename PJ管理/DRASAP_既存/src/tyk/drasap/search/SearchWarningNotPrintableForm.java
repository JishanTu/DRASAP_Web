package tyk.drasap.search;

import org.apache.struts.action.ActionForm;

/**
 * 検索結果から、出力指示をしたときに、Tiff以外を選択していたときに、
 * 確認するアクションに対応したフォーム
 * @author fumi
 * 作成日: 2004/01/19
 */
public class SearchWarningNotPrintableForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String act;
	// ----------------------------------------------- getter, setter
	/**
	 * @return
	 */
	public String getAct() {
		return act;
	}

	/**
	 * @param string
	 */
	public void setAct(String string) {
		act = string;
	}

}
