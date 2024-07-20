package tyk.drasap.search;

import tyk.drasap.common.StringCheck;
import tyk.drasap.springfw.form.BaseForm;

/**
 * ŒŸõŒ”‚ªŒŸõŒxŒ”‚ğ’´‚¦‚½‚Æ‚«‚ÉAŒx‚·‚é‰æ–Ê
 *
 * @version 2013/09/13 yamagishi
 */
public class SearchWarningOverHitForm extends BaseForm {
	/**
	 *
	 */
	String sqlWhere;
	String sqlOrder;
	String act;
	// ----------------------------------------------------- getter,setter

	/**
	 * @return
	 */
	public String getSqlOrder() {
		return sqlOrder;
	}

	/**
	 * @return
	 */
	public String getSqlWhere() {
		return sqlWhere;
	}

	/**
	 * @param string
	 */
	public void setSqlOrder(String string) {
		sqlOrder = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setSqlWhere(String string) {
		sqlWhere = StringCheck.latinToUtf8(string);
	}

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
