package tyk.drasap.search;

import java.io.Serializable;

public class PreviewElement implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PreviewElement() {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	private String drwgNo = "";			// �}��
	private String fileName = "";		// �t�@�C����
	private String pathName = "";		// �f�B���N�g���̃t���p�X
	private String drwgSize = "";		// �}�ԃT�C�Y

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
