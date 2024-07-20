package tyk.drasap.search;

import java.util.HashMap;

/**
 * 各テーブルの内容を保持する。
 * テーブルメンテナンス用。
 */
public class DeleteDwgFileInfo {
	String fileId = "";
	String fileName = "";
	String filePath = "";
	HashMap<String, String> linkDwgParmMap = new HashMap<String, String>();// リンクタグで使用するパラメータを格納するMap
	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}
	/**
	 * @param list
	 */
	public void setFileId(String val) {
	    fileId = val;
	}
	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param list
	 */
	public void setFileName(String val) {
	    fileName = val;
	}
	/**
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param list
	 */
	public void setFilePath(String val) {
	    filePath = val;
	}
	/**
	 * @return
	 */
	public HashMap<String, String> getLinkDwgParmMap() {
		return linkDwgParmMap;
	}
}
