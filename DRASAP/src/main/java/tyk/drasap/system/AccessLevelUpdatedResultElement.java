package tyk.drasap.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * �A�N�Z�X���x���ύX���O��\���G�������g�B
 * AccessLevelUpdatedResultForm�̃v���p�e�B
 *
 * @version 2013/08/20 yamagishi
 */
public class AccessLevelUpdatedResultElement {
	String fileName; // �t�@�C����
	String fileTypeDescription; // �t�@�C����ސ���
	Date lastModified; // �X�V����
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd H:mm"); // �X�V�����t�H�[�}�b�g

	HashMap<String, String> linkParmMap = new HashMap<String, String>(); // �����N�^�O�Ŏg�p����p�����[�^���i�[����Map

	// ---------------------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^
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
