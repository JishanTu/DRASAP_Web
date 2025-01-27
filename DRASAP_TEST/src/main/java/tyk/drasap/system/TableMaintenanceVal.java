package tyk.drasap.system;

import tyk.drasap.common.StringCheck;

/**
 * １レコードの内容を保持する。
 * テーブルメンテナンス用。
 */
public class TableMaintenanceVal {
	String val = "";
	String dispStyle = "";

	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param list
	 */
	public void setVal(String val) {
		this.val = StringCheck.latinToUtf8(val);
	}

	/**
	 * @return
	 */
	public String getDispStyle() {
		return dispStyle;
	}

	/**
	 * @return
	 */
	public void setDispStyle(String val) {
		dispStyle = val;
	}
}
