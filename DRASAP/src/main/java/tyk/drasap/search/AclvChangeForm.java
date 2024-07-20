package tyk.drasap.search;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * @author fumi
 * 作成日: 2004/01/20
 * @version 2013/07/25 yamagishi
 */
public class AclvChangeForm extends BaseForm {
	/**
	 *
	 */
	String act;// 処理を分けるための
	ArrayList<AclvChangeElement> aclvChangeList = new ArrayList<>();// アクセスレベルの変更対象を格納する。内部はAclvChangeElement。
	ArrayList<String> aclvNotChangeList = new ArrayList<>();// 選択していたが、変更できない図面を格納する。
	//内部は図番(フォーマット済みの)
	ArrayList<String> aclvNameList = new ArrayList<>();// アクセスレベルのプルダウンの名称リスト
	ArrayList<String> aclvKeyList = new ArrayList<>();// アクセスレベルのプルダウンのKeyリスト
	ArrayList<String> errorMessages = new ArrayList<>();// エラーメッセージの表示
	// 2013.07.25 yamagishi add. start
	HashMap<String, String> aclMap = new HashMap<String, String>();
	// 2013.07.25 yamagishi add. end

	// ------------------------------------------------------------------- method
	/* (非 Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(HttpServletRequest request) {
		// act属性が、BACK_INPUTまたはCONFIRMEDを指定されたとき
		// つまり・・・確認画面から戻るときは、
		// チェックボックスを初期化しない
		// 注意: resetが呼び出された時点では、まだactに設定されていないため、
		// 			requestからパラメータとして取得して、判定している。
		if (!"BACK_INPUT".equals(request.getParameter("act")) && !"CONFIRMED".equals(request.getParameter("act"))) {

			// チェックボックスの初期化
			for (int i = 0; i < aclvChangeList.size(); i++) {
				getAclvChangeElement(i).setSelected(false);
			}
		}
	}

	/**
	 * iterateタグに対応させるためのメソッド
	 * @param index
	 * @return
	 */
	public AclvChangeElement getAclvChangeElement(int index) {
		return aclvChangeList.get(index);
	}

	// ------------------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public ArrayList<AclvChangeElement> getAclvChangeList() {
		return aclvChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getAclvNotChangeList() {
		return aclvNotChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getAclvKeyList() {
		return aclvKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getAclvNameList() {
		return aclvNameList;
	}

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
	public ArrayList<String> getErrorMessages() {
		return errorMessages;
	}

	// 2013.07.25 yamagishi add. start
	/**
	 * @return aclMap
	 */
	public HashMap<String, String> getAclMap() {
		return aclMap;
	}
	// 2013.07.25 yamagishi add. end
}
