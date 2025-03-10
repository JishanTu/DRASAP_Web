package tyk.drasap.system;

/**
 * 各テーブルの内容を保持する。
 * テーブルメンテナンス用。
 */
public class TableMaintenanceElement {
    String column_name = "";// 項目名
	String data_type = "";// 項目タイプ
	int data_length;// サイズ
	boolean keyFlg = false;
	String nullable = "Y";
	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getColumn_name() {
		return column_name;
	}
	/**
	 * @return
	 */
	public void setColumn_name(String val) {
	    column_name = val;
		return;
	}

	/**
	 * @return
	 */
	public String getData_type() {
		return data_type;
	}
	/**
	 * @return
	 */
	public void setData_type(String val) {
	    data_type = val;
		return;
	}

	/**
	 * @return
	 */
	public int getData_length() {
		return data_length;
	}
	/**
	 * @return
	 */
	public void setdata_length(int val) {
	    data_length = val;
		return;
	}
	/**
	 * @return
	 */
	public String getKey() {
	    if (keyFlg) {
	        return "1";
	    } else {
	        return "0";
	    }
	}
	/**
	 * @return
	 */
	public boolean isKey() {
		return keyFlg;
	}
	/**
	 * @return
	 */
	public void setKey(boolean flg) {
	    keyFlg = flg;
		return;
	}
	/**
	 * @return
	 */
	public void setKey(String val) {
	    if (val.equals("1")) {
	        keyFlg = true;
	    } else {
	        keyFlg = false;
	    }
		return;
	}
	/**
	 * @return
	 */
	public String getNullable() {
		return nullable;
	}
	/**
	 * @return
	 */
	public void setNullable(String val) {
	    nullable = val;
		return;
	}
}
