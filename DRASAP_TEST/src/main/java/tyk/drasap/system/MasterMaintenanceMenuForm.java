package tyk.drasap.system;

import tyk.drasap.springfw.form.BaseForm;

/**
 * マスターメンテナンスメニュー画面に対応
 */
public class MasterMaintenanceMenuForm extends BaseForm {
	/**
	 *
	 */
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
