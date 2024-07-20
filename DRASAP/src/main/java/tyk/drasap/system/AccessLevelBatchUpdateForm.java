package tyk.drasap.system;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import tyk.drasap.common.AclUpload;
import tyk.drasap.springfw.form.BaseForm;

/**
 * �A�N�Z�X���x���ꊇ�X�V��ʂɑΉ�
 *
 * @author 2013/07/03 yamagishi
 */
public class AccessLevelBatchUpdateForm extends BaseForm {
	/**
	 *
	 */
	String act; // �����𕪂��邽�߂̑���
	ArrayList<String> errorMsg = new ArrayList<String>();
	String aclUpdateNo = null;
	String dlFileType = null;
	HashMap<String, String> linkParmMap = new HashMap<String, String>(); // �����N�^�O�Ŏg�p����p�����[�^���i�[����Map
	MultipartFile uploadFile = null;
	ArrayList<AclUpload> uploadList = new ArrayList<AclUpload>(); // �A�b�v���[�h�f�[�^���i�[����B
	long itemNoCount = 0;

	// --------------------------------------------------------- constructor
	public AccessLevelBatchUpdateForm() {
		act = "";
		linkParmMap.put("dlFileType", "0");//�����N�p�����[�^�ݒ�
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
	 * aclUpdateNo���擾���܂��B
	 * @return aclUpdateNo
	 */
	public String getAclUpdateNo() {
		return aclUpdateNo;
	}

	/**
	 * aclUpdateNo��ݒ肵�܂��B
	 * @param aclUpdateNo aclUpdateNo
	 */
	public void setAclUpdateNo(String aclUpdateNo) {
		this.aclUpdateNo = aclUpdateNo;
	}

	/**
	 * dlFileType���擾���܂��B
	 * @return dlFileType
	 */
	public String getDlFileType() {
		return dlFileType;
	}

	/**
	 * dlFileType��ݒ肵�܂��B
	 * @param dlFileType dlFileType
	 */
	public void setDlFileType(String dlFileType) {
		this.dlFileType = dlFileType;
	}

	/**
	 * linkParmMap���擾���܂��B
	 * @return linkParmMap
	 */
	public HashMap<String, String> getLinkParmMap() {
		return linkParmMap;
	}

	/**
	 * uploadFile���擾���܂��B
	 * @return uploadFile
	 */
	public MultipartFile getUploadFile() {
		return uploadFile;
	}

	/**
	 * uploadFile��ݒ肵�܂��B
	 * @param uploadFile uploadFile
	 */
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	/**
	 * uploadList���擾���܂��B
	 * @return uploadList
	 */
	public ArrayList<AclUpload> getUploadList() {
		return uploadList;
	}

	/**
	 * uploadList��ݒ肵�܂��B
	 * @param uploadList uploadList
	 */
	public void setUploadList(ArrayList<AclUpload> uploadList) {
		this.uploadList = uploadList;
	}

	/**
	 * itemNoCount���擾���܂��B
	 * @return itemNoCount
	 */
	public long getItemNoCount() {
		return itemNoCount;
	}

	/**
	 * itemNoCount��ݒ肵�܂��B
	 * @param itemNoCount itemNoCount
	 */
	public void setItemNoCount(long itemNoCount) {
		this.itemNoCount = itemNoCount;
	}
}
