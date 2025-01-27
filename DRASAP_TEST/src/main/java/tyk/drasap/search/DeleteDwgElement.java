package tyk.drasap.search;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 各テーブルの内容を保持する。
 * テーブルメンテナンス用。
 */
public class DeleteDwgElement {
	//ArrayList<String> valList = new ArrayList<String>();//
	String drwgNo;
	String drwgType;
	ArrayList<String> valList = new ArrayList<String>();//
	HashMap<String, String> linkDwgParmMap = new HashMap<String, String>();// リンクタグで使用するパラメータを格納するMap
	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	public String getDrwgNo() {
		return drwgNo;
	}
	public void setDrwgNo(String drwgNo) {
		this.drwgNo = drwgNo;
	}
	public String getDrwgType() {
		return drwgType;
	}
	public void setDrwgType(String drwgType) {
		this.drwgType = drwgType;
	}
	/**
	 * @return
	 */
	public ArrayList<String> getValList() {
		return valList;
	}
	/**
	 * @return
	 */
	public String getVal(int idx) {
		return valList.get(idx);
	}
	/**
	 * @param list
	 */
	public void setValList(ArrayList<String> list) {
	    valList = list;
	}
	/**
	 * @param list
	 */
	public void addValList(String value) {
	    valList.add(value);
	}
	/**
	 * @return
	 */
	public HashMap<String, String> getLinkDwgParmMap() {
		return linkDwgParmMap;
	}
}
