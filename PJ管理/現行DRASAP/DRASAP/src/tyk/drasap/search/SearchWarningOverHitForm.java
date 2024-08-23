package tyk.drasap.search;

import org.apache.struts.action.ActionForm;

import tyk.drasap.common.StringCheck;

/**
 * ���������������x�������𒴂����Ƃ��ɁA�x��������
 *
 * @version 2013/09/13 yamagishi
 */
public class SearchWarningOverHitForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
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
