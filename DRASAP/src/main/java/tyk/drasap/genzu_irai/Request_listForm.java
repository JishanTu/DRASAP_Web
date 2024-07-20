package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * 原図庫作業依頼リストのフォーム
 */
public class Request_listForm extends BaseForm {
	/**
	 *
	 */
	String action;//アクション
	String time;//(原図庫作業依頼リストで時間表示する)
	ArrayList<Request_listElement> iraiList = new ArrayList<Request_listElement>();//原図庫作業依頼リスト
	ArrayList<String> listErrors = new ArrayList<String>();//エラーリスト
	ArrayList<String> checkKeyList = new ArrayList<String>();//チェック項目のキー
	ArrayList<String> checkNameList = new ArrayList<String>();//チェック項目のリスト
	//ArrayList messageKeyList = new ArrayList();//メッセージ項目のキー
	ArrayList<String> messageNameList = new ArrayList<String>();//メッセージ項目のリスト
	ArrayList<RequestPriElement> printList = new ArrayList<RequestPriElement>();//印刷画面用のリスト

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * Returns the results.
	 * @return ArrayList
	 */
	public ArrayList<Request_listElement> getIraiList() {
		return iraiList;
	}

	/**
	 * Sets the results.
	 * @param results The results to set
	 */
	public void setIraiList(ArrayList<Request_listElement> iraiList) {
		this.iraiList = iraiList;
	}

	public ArrayList<String> getListErrors() {
		return listErrors;
	}

	public void setListErrors(ArrayList<String> listErrors) {
		this.listErrors = listErrors;
	}

	public ArrayList<String> getCheckKeyList() {
		return checkKeyList;
	}

	public void setCheckKeyList(ArrayList<String> checkKeyList) {
		this.checkKeyList = checkKeyList;
	}

	public ArrayList<String> getCheckNameList() {
		return checkNameList;
	}

	public void setCheckNameList(ArrayList<String> checkNameList) {
		this.checkNameList = checkNameList;
	}

	//    public ArrayList getMessageKeyList() {
	//        return messageKeyList;
	//    }
	//
	//    public void setMessageKeyList(ArrayList messageKeyList) {
	//        this.messageKeyList = messageKeyList;
	//    }

	public ArrayList<String> getMessageNameList() {
		return messageNameList;
	}

	public void setMessageNameList(ArrayList<String> messageNameList) {
		this.messageNameList = messageNameList;
	}

	public ArrayList<RequestPriElement> getPrintList() {
		return printList;
	}

	public void setPrintList(ArrayList<RequestPriElement> printList) {
		this.printList = printList;
	}

	/**
	 * Returns the item.
	 * @return EcPartsSearch
	 */
	public Request_listElement getItem(int index) {
		if (iraiList.isEmpty()) {
			// セッションタイムアウト時にException発生の回避策
			return new Request_listElement(
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		return iraiList.get(index);

	}

	public void reset(HttpServletRequest request) {
		action = "";
	}

}
