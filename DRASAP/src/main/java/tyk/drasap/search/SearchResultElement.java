package tyk.drasap.search;

import java.util.HashMap;

import tyk.drasap.common.DrasapUtil;

/**
 * 検索結果を表すエレメント。INDEX_DBに対応する。
 * SearchResultFormの
 *
 * @version 2013/06/24 yamagishi
 */
public class SearchResultElement {
	String drwgNo;// 図番
	String drwgNoFormated;// フォーマットされた図番・・・コンストラクタで同時に作成される
	HashMap<String, String> attrMap = new HashMap<String, String>();// 属性を格納したMAP
	String printSize;// 印刷サイズ。デフォルトは図面サイズと同じ・・・画面上で表示される
	String decidePrintSize;// 出力プリンタ、原図サイズなどを複合的に考えて、決定される。
							// この値が、参考図出力用テーブルに書き込まれる。
							// 出力前のチェックで、セットされる。SearchResultAction#checkForPrint
	String copies;// 部数。デフォルトは1。
	String fileName;// ファイル名。00-59410060-1.tifなど。
	String fileType;// FILE_DB.FILE_TYPに対応。1=TIFF、2=PDF、3=JPG
	String pathName;// パス名。ファイルが格納されているディレクトリ名。
	boolean selected = false;// 選択チェックボックスに対応
	HashMap<String, String> linkParmMap = new HashMap<String, String>();// リンクタグで使用するパラメータを格納するMap
	// 2013.06.24 yamagishi add. start
	String aclBalloon;// 品番バルーン表示内容
	String aclFlag;
	// 2013.06.24 yamagishi add. end

	String printerMaxSize = ""; //プリンタの最大印刷サイズ
	// ---------------------------------------------------------- コンストラクタ

	public SearchResultElement() {

	}

	/**
	 * コンストラクタ
	 */
	// 2013.07.11 yamagishi modified. start
	//	public SearchResultElement(String newDrwgNo, String newFileName, String newFileType, String newPathName) {
	public SearchResultElement(String newDrwgNo, String newFileName, String newFileType, String newPathName, String newAclBalloon) {
		// 2013.07.11 yamagishi modified. end
		drwgNo = newDrwgNo;
		drwgNoFormated = DrasapUtil.formatDrwgNo(drwgNo);// フォーマットした図番
		copies = "1";
		fileName = newFileName;
		fileType = newFileType;
		pathName = newPathName;
		// 2013.07.11 yamagishi add. start
		aclBalloon = newAclBalloon != null ? newAclBalloon : "";
		// 2013.07.11 yamagishi add. end
	}

	// ---------------------------------------------------------- Method
	/**
	 * 属性Mapに属性を追加する。valueがnullなら、""に変換して追加する
	 * @param key
	 * @param value
	 */
	public void addAttr(String key, String value) {
		if (value == null) {
			attrMap.put(key, "");
		} else {
			attrMap.put(key, value);
		}
	}

	/**
	 * 属性Mapから属性を取得して返す。このとき、keyがnull(または長さ0)なら、""を返す
	 * @param key
	 * @return
	 */
	public String getAttr(String key) {
		if (key == null || key.length() == 0) {
			return "";
		}
		return attrMap.get(key);
	}

	// ---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public HashMap<String, String> getAttrMap() {
		return attrMap;
	}

	public String getPrinterMaxSize() {
		return printerMaxSize;
	}

	public void setPrinterMaxSize(String maxSize) {
		printerMaxSize = maxSize;
	}

	/**
	 * @return
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * @return
	 */
	public String getPrintSize() {
		return printSize;
	}

	/**
	 * @param string
	 */
	public void setPrintSize(String string) {
		printSize = string;
	}

	/**
	 * @return
	 */
	public String getCopies() {
		return copies;
	}

	/**
	 * @param string
	 */
	public void setCopies(String string) {
		copies = string;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @return
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	public boolean getSelected() {
		return selected;
	}

	/**
	 * @param b
	 */
	public void setSelected(boolean b) {
		selected = b;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getLinkParmMap() {
		return linkParmMap;
	}

	/**
	 * @return
	 */
	public String getDrwgNoFormated() {
		return drwgNoFormated;
	}

	/**
	 * @return
	 */
	public String getDecidePrintSize() {
		return decidePrintSize;
	}

	/**
	 * @param string
	 */
	public void setDecidePrintSize(String string) {
		decidePrintSize = string;
	}

	// 2013.06.24 yamagishi add. start
	/**
	 * aclBalloonを取得します。
	 * @return aclBalloon
	 */
	public String getAclBalloon() {
		return aclBalloon;
	}

	/**
	 * aclBalloonを設定します。
	 * @param aclBalloon aclBalloon
	 */
	public void setAclBalloon(String aclBalloon) {
		this.aclBalloon = aclBalloon == null ? "" : aclBalloon;
	}

	/**
	 * aclFlagを取得します。
	 * @return aclFlag
	 */
	public String getAclFlag() {
		return aclFlag;
	}

	/**
	 * aclFlagを設定します。
	 * @param aclFlag aclFlag
	 */
	public void setAclFlag(String aclFlag) {
		this.aclFlag = aclFlag;
	}
	// 2013.06.24 yamagishi add. end
}
