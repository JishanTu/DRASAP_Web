package tyk.drasap.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * アクセスレベル変更ログを表すエレメント。
 * AccessLevelUpdatedResultFormのプロパティ
 *
 * @version 2013/08/20 yamagishi
 */
public class AccessLevelUpdatedResultElement {
	String fileName; // ファイル名
	String fileTypeDescription; // ファイル種類説明
	Date lastModified; // 更新日時
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd H:mm"); // 更新日時フォーマット

	HashMap<String, String> linkParmMap = new HashMap<String, String>(); // リンクタグで使用するパラメータを格納するMap

	// ---------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
	public AccessLevelUpdatedResultElement(String fileName, String fileTypeDescription, Date lastModified) {
		this.fileName = fileName;
		this.fileTypeDescription = fileTypeDescription;
		this.lastModified = lastModified;
	}

	// ---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public String getFileTypeDescription() {
		return fileTypeDescription != null ? fileTypeDescription : "";
	}

	/**
	 * @return
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @return
	 */
	public String getLastModifiedFormatted() {
		return lastModified != null ? sdf.format(lastModified) : "";
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getLinkParmMap() {
		return linkParmMap;
	}
}
