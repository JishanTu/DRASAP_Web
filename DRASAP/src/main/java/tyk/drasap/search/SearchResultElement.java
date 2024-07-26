package tyk.drasap.search;

import java.util.HashMap;

import tyk.drasap.common.DrasapUtil;

/**
 * �������ʂ�\���G�������g�BINDEX_DB�ɑΉ�����B
 * SearchResultForm��
 *
 * @version 2013/06/24 yamagishi
 */
public class SearchResultElement {
	String drwgNo;// �}��
	String drwgNoFormated;// �t�H�[�}�b�g���ꂽ�}�ԁE�E�E�R���X�g���N�^�œ����ɍ쐬�����
	HashMap<String, String> attrMap = new HashMap<String, String>();// �������i�[����MAP
	String printSize;// ����T�C�Y�B�f�t�H���g�͐}�ʃT�C�Y�Ɠ����E�E�E��ʏ�ŕ\�������
	String decidePrintSize;// �o�̓v�����^�A���}�T�C�Y�Ȃǂ𕡍��I�ɍl���āA���肳���B
							// ���̒l���A�Q�l�}�o�͗p�e�[�u���ɏ������܂��B
							// �o�͑O�̃`�F�b�N�ŁA�Z�b�g�����BSearchResultAction#checkForPrint
	String copies;// �����B�f�t�H���g��1�B
	String fileName;// �t�@�C�����B00-59410060-1.tif�ȂǁB
	String fileType;// FILE_DB.FILE_TYP�ɑΉ��B1=TIFF�A2=PDF�A3=JPG
	String pathName;// �p�X���B�t�@�C�����i�[����Ă���f�B���N�g�����B
	boolean selected = false;// �I���`�F�b�N�{�b�N�X�ɑΉ�
	HashMap<String, String> linkParmMap = new HashMap<String, String>();// �����N�^�O�Ŏg�p����p�����[�^���i�[����Map
	// 2013.06.24 yamagishi add. start
	String aclBalloon;// �i�ԃo���[���\�����e
	String aclFlag;
	// 2013.06.24 yamagishi add. end

	String printerMaxSize = ""; //�v�����^�̍ő����T�C�Y
	// ---------------------------------------------------------- �R���X�g���N�^

	public SearchResultElement() {

	}

	/**
	 * �R���X�g���N�^
	 */
	// 2013.07.11 yamagishi modified. start
	//	public SearchResultElement(String newDrwgNo, String newFileName, String newFileType, String newPathName) {
	public SearchResultElement(String newDrwgNo, String newFileName, String newFileType, String newPathName, String newAclBalloon) {
		// 2013.07.11 yamagishi modified. end
		drwgNo = newDrwgNo;
		drwgNoFormated = DrasapUtil.formatDrwgNo(drwgNo);// �t�H�[�}�b�g�����}��
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
	 * ����Map�ɑ�����ǉ�����Bvalue��null�Ȃ�A""�ɕϊ����Ēǉ�����
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
	 * ����Map���瑮�����擾���ĕԂ��B���̂Ƃ��Akey��null(�܂��͒���0)�Ȃ�A""��Ԃ�
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
	 * aclBalloon���擾���܂��B
	 * @return aclBalloon
	 */
	public String getAclBalloon() {
		return aclBalloon;
	}

	/**
	 * aclBalloon��ݒ肵�܂��B
	 * @param aclBalloon aclBalloon
	 */
	public void setAclBalloon(String aclBalloon) {
		this.aclBalloon = aclBalloon == null ? "" : aclBalloon;
	}

	/**
	 * aclFlag���擾���܂��B
	 * @return aclFlag
	 */
	public String getAclFlag() {
		return aclFlag;
	}

	/**
	 * aclFlag��ݒ肵�܂��B
	 * @param aclFlag aclFlag
	 */
	public void setAclFlag(String aclFlag) {
		this.aclFlag = aclFlag;
	}
	// 2013.06.24 yamagishi add. end
}
