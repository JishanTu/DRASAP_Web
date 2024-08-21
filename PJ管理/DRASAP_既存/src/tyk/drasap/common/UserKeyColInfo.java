package tyk.drasap.common;

import java.util.ArrayList;

/**
 * ユーザーテーブルのキーカラムを表す。
 * @author Y.eto
 * 作成日: 2006/05/10
 */
public class UserKeyColInfo {
    String tabl_name = "";// テーブル名
    ArrayList<String> keyCol = new ArrayList<String>();
	// ------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
	public UserKeyColInfo() {
	}
	// ------------------------------------------------------- 
	/**
	 * @return
	 */
	public void setTableName(String value) {
	    tabl_name = value;
		return;
	}
	/**
	 * @return
	 */
	public String getTableName() {
		return tabl_name;
	}

	/**
	 * @return
	 */
	public int getNoKeyCol() {
	    if (keyCol.isEmpty()) {
	        return 0;
	    } else {
	        return keyCol.size();
	    }
	}
	public String getkeyCol(int idx) {
		return (String)keyCol.get(idx);
	}
	public void addKeyCol(String keyColName) {
		this.keyCol.add(keyColName);
	}
}
