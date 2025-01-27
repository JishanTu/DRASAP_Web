package tyk.drasap.system;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import tyk.drasap.common.AclUpload;
import tyk.drasap.springfw.form.BaseForm;

/**
 * アクセスレベル一括更新画面に対応
 *
 * @author 2013/07/03 yamagishi
 */
public class AccessLevelBatchUpdateForm extends BaseForm {
	/**
	 *
	 */
	String act; // 処理を分けるための属性
	ArrayList<String> errorMsg = new ArrayList<String>();
	String aclUpdateNo = null;
	String dlFileType = null;
	HashMap<String, String> linkParmMap = new HashMap<String, String>(); // リンクタグで使用するパラメータを格納するMap
	MultipartFile uploadFile = null;
	ArrayList<AclUpload> uploadList = new ArrayList<AclUpload>(); // アップロードデータを格納する。
	long itemNoCount = 0;

	// --------------------------------------------------------- constructor
	public AccessLevelBatchUpdateForm() {
		act = "";
		linkParmMap.put("dlFileType", "0");//リンクパラメータ設定
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
	 * aclUpdateNoを取得します。
	 * @return aclUpdateNo
	 */
	public String getAclUpdateNo() {
		return aclUpdateNo;
	}

	/**
	 * aclUpdateNoを設定します。
	 * @param aclUpdateNo aclUpdateNo
	 */
	public void setAclUpdateNo(String aclUpdateNo) {
		this.aclUpdateNo = aclUpdateNo;
	}

	/**
	 * dlFileTypeを取得します。
	 * @return dlFileType
	 */
	public String getDlFileType() {
		return dlFileType;
	}

	/**
	 * dlFileTypeを設定します。
	 * @param dlFileType dlFileType
	 */
	public void setDlFileType(String dlFileType) {
		this.dlFileType = dlFileType;
	}

	/**
	 * linkParmMapを取得します。
	 * @return linkParmMap
	 */
	public HashMap<String, String> getLinkParmMap() {
		return linkParmMap;
	}

	/**
	 * uploadFileを取得します。
	 * @return uploadFile
	 */
	public MultipartFile getUploadFile() {
		return uploadFile;
	}

	/**
	 * uploadFileを設定します。
	 * @param uploadFile uploadFile
	 */
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	/**
	 * uploadListを取得します。
	 * @return uploadList
	 */
	public ArrayList<AclUpload> getUploadList() {
		return uploadList;
	}

	/**
	 * uploadListを設定します。
	 * @param uploadList uploadList
	 */
	public void setUploadList(ArrayList<AclUpload> uploadList) {
		this.uploadList = uploadList;
	}

	/**
	 * itemNoCountを取得します。
	 * @return itemNoCount
	 */
	public long getItemNoCount() {
		return itemNoCount;
	}

	/**
	 * itemNoCountを設定します。
	 * @param itemNoCount itemNoCount
	 */
	public void setItemNoCount(long itemNoCount) {
		this.itemNoCount = itemNoCount;
	}
}
