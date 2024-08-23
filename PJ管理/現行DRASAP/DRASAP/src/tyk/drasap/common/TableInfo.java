package tyk.drasap.common;

import java.util.ArrayList;

/**
 * テーブル情報を表す。
 * 
 */
public class TableInfo {
    String tabl_name = "";// テーブル名
    ArrayList<TableColInfo> colInfos = new ArrayList<TableColInfo>();
	// ------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
	public TableInfo(String newTabl_name) {
		this.tabl_name = newTabl_name;
	}
	// ------------------------------------------------------- 
	/**
	 * @return
	 */
	public String getTableName() {
		return tabl_name;
	}

	/**
	 * @return
	 */
	public int getNoCol() {
	    if (colInfos.isEmpty()) {
	        return 0;
	    } else {
	        return colInfos.size();
	    }
	}
	public TableColInfo getColInfo(int idx) {
		return colInfos.get(idx);
	}
	public TableColInfo getColInfo(String colname) {
	    for (int i = 0; i < colInfos.size(); i++) {
	        if (colInfos.get(i).getColumn_name().equals(colname)) {
	            return colInfos.get(i);
	        }
	    }
		return new TableColInfo(colname, "", "0", "Y");
	}
	public void setTableColInfo(ArrayList<TableColInfo> newTableColInfo) {
		this.colInfos = newTableColInfo;
	}
}
