package tyk.drasap.system;


import org.apache.struts.action.ActionForm;

/**
 * マスターメンテナンスメニュー画面に対応
 */
public class MasterMaintenanceMenuForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2248665929277142616L;
	String act;// 処理を分けるための属性

	// --------------------------------------------------------- Methods

	// --------------------------------------------------------- getter,setter
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
