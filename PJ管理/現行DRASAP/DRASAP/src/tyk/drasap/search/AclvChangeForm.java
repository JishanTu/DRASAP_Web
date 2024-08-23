package tyk.drasap.search;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author fumi
 * 作成日: 2004/01/20
 * @version 2013/07/25 yamagishi
 */
public class AclvChangeForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String act;// 処理を分けるための
	ArrayList aclvChangeList = new ArrayList();// アクセスレベルの変更対象を格納する。内部はAclvChangeElement。
	ArrayList aclvNotChangeList = new ArrayList();// 選択していたが、変更できない図面を格納する。
												//内部は図番(フォーマット済みの)
	ArrayList aclvNameList = new ArrayList();// アクセスレベルのプルダウンの名称リスト
	ArrayList aclvKeyList = new ArrayList();// アクセスレベルのプルダウンのKeyリスト
	ArrayList errorMessages = new ArrayList();// エラーメッセージの表示
// 2013.07.25 yamagishi add. start
	HashMap<String, String> aclMap = new HashMap<String, String>();
// 2013.07.25 yamagishi add. end

	// ------------------------------------------------------------------- method
	/* (非 Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// act属性が、BACK_INPUTまたはCONFIRMEDを指定されたとき
		// つまり・・・確認画面から戻るときは、
		// チェックボックスを初期化しない
		// 注意: resetが呼び出された時点では、まだactに設定されていないため、
		// 			requestからパラメータとして取得して、判定している。
		if(! "BACK_INPUT".equals(request.getParameter("act")) && ! "CONFIRMED".equals(request.getParameter("act"))){

			// チェックボックスの初期化
			for(int i = 0; i < aclvChangeList.size(); i++){
				getAclvChangeElement(i).setSelected(false);
			}
		}
	}
	/**
	 * iterateタグに対応させるためのメソッド
	 * @param index
	 * @return
	 */
	public AclvChangeElement getAclvChangeElement(int index){
		return (AclvChangeElement) this.aclvChangeList.get(index);
	}
	// ------------------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public ArrayList getAclvChangeList() {
		return aclvChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvNotChangeList() {
		return aclvNotChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvKeyList() {
		return aclvKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvNameList() {
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
	public ArrayList getErrorMessages() {
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
