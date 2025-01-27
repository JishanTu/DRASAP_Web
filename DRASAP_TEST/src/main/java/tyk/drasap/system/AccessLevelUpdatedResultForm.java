package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * アクセスレベル更新結果画面に対応
 *
 * @author 2013/07/23 yamagishi
 */
public class AccessLevelUpdatedResultForm extends BaseForm {
	/**
	 *
	 */
	String act; // 処理を分けるための属性
	ArrayList<String> errorMsg = new ArrayList<String>();
	ArrayList<AccessLevelUpdatedResultElement> accessLevelUpdatedResultList = new ArrayList<AccessLevelUpdatedResultElement>(); // 更新結果ログ情報を格納する
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
	 * accessLevelUpdatedResultListを取得します。
	 * @return accessLevelUpdatedResultList
	 */
	public ArrayList<AccessLevelUpdatedResultElement> getAccessLevelUpdatedResultList() {
		return accessLevelUpdatedResultList;
	}

	/**
	 * accessLevelUpdatedResultListを設定します。
	 * @param accessLevelUpdatedResultList
	 */
	public void setAccessLevelUpdatedResultList(ArrayList<AccessLevelUpdatedResultElement> accessLevelUpdatedResultList) {
		this.accessLevelUpdatedResultList = accessLevelUpdatedResultList;
	}

	/**
	 * fileCountを取得します。
	 * @return fileCount
	 */
	public long getFileCount() {
		return fileCount;
	}

	/**
	 * fileCountを設定します。
	 * @param fileCount fileCount
	 */
	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}
}
