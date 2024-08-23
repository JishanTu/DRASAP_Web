package tyk.drasap.search;

import java.io.Serializable;

public class PreviewElement implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PreviewElement() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	private String drwgNo = "";			// 図番
	private String fileName = "";		// ファイル名
	private String pathName = "";		// ディレクトリのフルパス
	private String drwgSize = "";		// 図番サイズ

	/**
	 * getDrwgNo
	 * @return Returns the drwgNo.
	 */
	public String getdrwgNo() {
		return drwgNo;
	}
	/**
	 * setDrwgNo
	 * @param DrwgNo The DrwgNo to set.
	 */
	public void setDrwgNo(String drwgNo) {
		this.drwgNo = drwgNo;
	}

	/**
	 * getDrwgSize
	 * @return Returns the drwgSize.
	 */
	public String getDrwgSize() {
		return drwgSize;
	}
	/**
	 * setDrwgSize
	 * @param drwgSize The drwgSize to set.
	 */
	public void setDrwgSize(String drwgSize) {
		this.drwgSize = drwgSize;
	}

	/**
	 * getFileName
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * setFileName
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * getPathName
	 * @return Returns the pathName.
	 */
	public String getPathName() {
		return pathName;
	}
	/**
	 * setPathName
	 * @param pathName The pathName to set.
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
}
