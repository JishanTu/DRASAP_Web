package tyk.drasap.search;

import java.util.HashMap;

/**
 * �e�e�[�u���̓��e��ێ�����B
 * �e�[�u�������e�i���X�p�B
 */
public class DeleteDwgFileInfo {
	String fileId = "";
	String fileName = "";
	String filePath = "";
	HashMap<String, String> linkDwgParmMap = new HashMap<String, String>();// �����N�^�O�Ŏg�p����p�����[�^���i�[����Map
	// ------------------------------------------------------- �R���X�g���N�^
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
