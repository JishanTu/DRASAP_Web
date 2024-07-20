package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * �A�N�Z�X���x���X�V���ʉ�ʂɑΉ�
 *
 * @author 2013/07/23 yamagishi
 */
public class AccessLevelUpdatedResultForm extends BaseForm {
	/**
	 *
	 */
	String act; // �����𕪂��邽�߂̑���
	ArrayList<String> errorMsg = new ArrayList<String>();
	ArrayList<AccessLevelUpdatedResultElement> accessLevelUpdatedResultList = new ArrayList<AccessLevelUpdatedResultElement>(); // �X�V���ʃ��O�����i�[����
	long fileCount = 0;

	// --------------------------------------------------------- constructor
	public AccessLevelUpdatedResultForm() {
		act = "";
	}

	// --------------------------------------------------------- Methods
	public void reset(HttpServletRequest request) {
	}

	// --------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getAct() {
		return act;
	}

	/**
	 * @param string
	 */
	public void setAct(String string) {
		act = string;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param string
	 */
	public void addErrorMsg(String string) {
		errorMsg.add(string);
	}

	/**
	 * @param string
	 */
	public void clearErrorMsg() {
		errorMsg.clear();
	}

	/**
	 * accessLevelUpdatedResultList���擾���܂��B
	 * @return accessLevelUpdatedResultList
	 */
	public ArrayList<AccessLevelUpdatedResultElement> getAccessLevelUpdatedResultList() {
		return accessLevelUpdatedResultList;
	}

	/**
	 * accessLevelUpdatedResultList��ݒ肵�܂��B
	 * @param accessLevelUpdatedResultList
	 */
	public void setAccessLevelUpdatedResultList(ArrayList<AccessLevelUpdatedResultElement> accessLevelUpdatedResultList) {
		this.accessLevelUpdatedResultList = accessLevelUpdatedResultList;
	}

	/**
	 * fileCount���擾���܂��B
	 * @return fileCount
	 */
	public long getFileCount() {
		return fileCount;
	}

	/**
	 * fileCount��ݒ肵�܂��B
	 * @param fileCount fileCount
	 */
	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}
}
