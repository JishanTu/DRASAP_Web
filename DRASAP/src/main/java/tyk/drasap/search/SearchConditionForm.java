package tyk.drasap.search;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import tyk.drasap.common.StringCheck;
import tyk.drasap.common.User;
import tyk.drasap.springfw.form.BaseForm;

/**
 * @version 2013/06/26 yamagishi
 */
public class SearchConditionForm extends BaseForm {
	/**
	 *
	 */
	String act = "";// Actionクラスで処理を分けるための属性
	ArrayList<String> conditionNameList;// 検索項目プルダウンの名称リスト
	ArrayList<String> conditionKeyList;// 検索項目プルダウンのKeyリスト

	ArrayList<String> conditionList = new ArrayList<>();// 検索の項目名
	ArrayList<String> conditionValueList = new ArrayList<>();// 検索の指定値
	ArrayList<String> sortWayList = new ArrayList<>();// 昇順、降順
	ArrayList<String> sortOrderList = new ArrayList<>();// ソート優先順

	ArrayList<String> sortOrderNameList;// ソート優先順の名称リスト
	ArrayList<String> sortOrderKeyList;// ソート優先順のKeyリスト
	boolean onlyNewest = false;// 最新追番のみならtrue
	boolean orderDrwgNo = false; // 図番指定順ならtrue 2020.03.11 yamamoto add.
	String multipleDrwgNo;// 複数図番	2013.06.26 yamagishi add.
	String eachCondition;// 全ての検索条件をORまたはAND
	String displayCount;// 表示件数
	private ArrayList<String> dispAttrList = new ArrayList<>(); // 表示属性1・・・検索結果画面からコピーさせる
	// 表示項目英語化対応
	String language = "Japanese";
	// Condition 部
	String C_label1 = "最新追番のみ表示";
	String C_label2 = "全ての属性条件を";
	String C_label3 = "検索開始";
	String C_label4 = "検索結果表示数";
	String C_label5 = "図面検索";
	String C_label6 = "職番";
	String C_label7 = "名前";
	String C_label8 = "部署名";
	String C_label9 = "複数図番"; // 2013.06.27 yamagishi add.
	// 2019.09.25 yamamoto add. start
	String C_label10 = "パスワード変更";
	String C_label11 = "ログアウト";
	String C_label12 = "原図庫作業依頼";
	String C_label13 = "原図庫作業依頼詳細";
	String C_label14 = "原図庫作業リスト";
	String C_label15 = "アクセスレベル一括更新";
	String C_label16 = "アクセスレベル更新結果";
	// 2019.09.25 yamamoto add. end
	// 2020.03.10 yamamoto add. start
	String C_label17 = "図番指定順";
	// 2020.03.10 yamamoto add. end
	// 2013.06.27 yamagishi add. start
	public final char C_LABEL1_LINE_NO = 1;
	public final char C_LABEL2_LINE_NO = 2;
	public final char C_LABEL3_LINE_NO = 3;
	public final char C_LABEL4_LINE_NO = 4;
	public final char C_LABEL5_LINE_NO = 5;
	public final char C_LABEL6_LINE_NO = 6;
	public final char C_LABEL7_LINE_NO = 7;
	public final char C_LABEL8_LINE_NO = 8;
	public final char C_LABEL9_LINE_NO = 22;
	// 2013.06.27 yamagishi add. end
	// 2019.09.25 yamamoto add. start
	public final char C_LABEL10_LINE_NO = 23;
	public final char C_LABEL11_LINE_NO = 24;
	public final char C_LABEL12_LINE_NO = 25;
	public final char C_LABEL13_LINE_NO = 26;
	public final char C_LABEL14_LINE_NO = 27;
	public final char C_LABEL15_LINE_NO = 28;
	public final char C_LABEL16_LINE_NO = 29;
	public final char C_LABEL17_LINE_NO = 32; // 図番指定順
	// 2019.09.25 yamamoto add. end
	// help message
	String searchHelpMsg = "";
	// 2019.09.25 yamamoto add. start
	// Error message
	String logoutErrMsg = "";
	String changeLangErrMsg = "";
	// 2019.09.25 yamamoto add. end
	// 2020.03.17 yamamoto add. start
	String listOrderErrMsg = "";

	// 2020.03.17 yamamoto add. end
	// --------------------------------------- method
	/* (非 Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(HttpServletRequest request) {
		// SELECTの初期化
		for (int i = 0; i < getSearchSelColNum(); i++) {
			conditionList.add(i, "");
			conditionValueList.add(i, "");
			sortWayList.add(i, "");
			sortOrderList.add(i, "");
		}
		displayCount = "";
		// CHECKBOXの初期化
		onlyNewest = false;
		orderDrwgNo = false;
		// RADIOの初期化
		eachCondition = "AND";
		act = "";
		multipleDrwgNo = ""; // 2013.06.27 yamagishi add.
	}

	// --------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getCondition(int idx) {
		return conditionList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getConditionList() {
		return conditionList;
	}

	/**
	 * @return
	 */
	public String getConditionValue(int idx) {
		return conditionValueList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getConditionValueList() {
		return conditionValueList;
	}

	/**
	 * @param string
	 */
	public void setCondition(int idx, String string) {
		conditionList.add(idx, string);
	}

	/**
	 * @return
	 */
	public void setConditionList(ArrayList<String> list) {
		conditionList = list;
	}

	/**
	 * @param string
	 */
	public void setConditionValue(int idx, String string) {
		// UTF-8へ変換する
		conditionValueList.add(idx, StringCheck.latinToUtf8(string));
	}

	/**
	 * @return
	 */
	public void setConditionValueList(ArrayList<String> list) {
		conditionValueList = list;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getConditionKeyList() {
		return conditionKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getConditionNameList() {
		return conditionNameList;
	}

	/**
	 * @param list
	 */
	public void setConditionKeyList(ArrayList<String> list) {
		conditionKeyList = list;
	}

	/**
	 * @param list
	 */
	public void setConditionNameList(ArrayList<String> list) {
		conditionNameList = list;
	}

	/**
	 * @return
	 */
	public String getSortWay(int idx) {
		return sortWayList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSortWayList() {
		return sortWayList;
	}

	/**
	 * @param string
	 */
	public void setSortWay(int idx, String string) {
		// UTF-8へ変換する
		sortWayList.add(idx, StringCheck.latinToUtf8(string));
	}

	/**
	 * @param string
	 */
	public void setSortWayList(ArrayList<String> list) {
		// UTF-8へ変換する
		sortWayList = list;
	}

	/**
	 * @return
	 */
	public String getSortOrder(int idx) {
		return sortOrderList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSortOrderList() {
		return sortOrderList;
	}

	/**
	 * @param string
	 */
	public void setSortOrder(int idx, String string) {
		sortOrderList.add(idx, string);
	}

	/**
	 * @param string
	 */
	public void setSortOrderList(ArrayList<String> list) {
		sortOrderList = list;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSortOrderKeyList() {
		return sortOrderKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSortOrderNameList() {
		return sortOrderNameList;
	}

	/**
	 * @param list
	 */
	public void setSortOrderKeyList(ArrayList<String> list) {
		sortOrderKeyList = list;
	}

	/**
	 * @param list
	 */
	public void setSortOrderNameList(ArrayList<String> list) {
		sortOrderNameList = list;
	}

	/**
	 * @return
	 */
	public boolean isOnlyNewest() {
		return onlyNewest;
	}

	/**
	 * @param b
	 */
	public void setOnlyNewest(boolean b) {
		onlyNewest = b;
	}

	/**
	 * @return
	 */
	public boolean isOrderDrwgNo() {
		return orderDrwgNo;
	}

	/**
	 * @param b
	 */
	public void setOrderDrwgNo(boolean b) {
		orderDrwgNo = b;
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

	// 2013.06.26 yamagishi add. start
	/**
	 * multipleDrwgNoを取得します。
	 * @return multipleDrwgNo
	 */
	public String getMultipleDrwgNo() {
		return multipleDrwgNo;
	}

	/**
	 * multipleDrwgNoを設定します。
	 * @param multipleDrwgNo multipleDrwgNo
	 */
	public void setMultipleDrwgNo(String multipleDrwgNo) {
		// UTF-8へ変換する
		this.multipleDrwgNo = StringCheck.latinToUtf8(multipleDrwgNo);
	}
	// 2013.06.26 yamagishi add. end

	/**
	 * @return
	 */
	public String getEachCondition() {
		return eachCondition;
	}

	/**
	 * @param string
	 */
	public void setEachCondition(String string) {
		eachCondition = string;
	}

	/**
	 * @return
	 */
	public String getDisplayCount() {
		return displayCount;
	}

	/**
	 * @param string
	 */
	public void setDisplayCount(String string) {
		displayCount = string;
	}

	/**
	 * @return
	 */
	public int getSearchSelColNum() {
		return User.searchSelColNum;
	}

	/**
	 * @return
	 */
	public int getViewSelColNum() {
		return User.viewSelColNum;
	}

	/**
	 * @return
	 */
	public String getDispAttr(int idx) {
		return dispAttrList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getDispAttrList() {
		return dispAttrList;
	}

	/**
	 * @param string
	 */
	public void setDispAttr(int idx, String string) {
		dispAttrList.add(idx, string);
	}

	/**
	 * @param string
	 */
	public void setDispAttrList(ArrayList<String> list) {
		dispAttrList = list;
	}

	public void setSearchCondition(HttpServletRequest request) {
		boolean onlyNewest = "true".equals(request.getParameter("onlyNewest"));
		setOnlyNewest(onlyNewest);

		dispAttrList.clear();
		for (int i = 1; i <= getViewSelColNum(); i++) {
			String paramName = "dispAttr" + i;
			String dispAttr = request.getParameter(paramName);
			dispAttr = StringUtils.isEmpty(dispAttr) ? "" : dispAttr;
			setDispAttr(i - 1, dispAttr);
		}

		conditionList.clear();
		conditionValueList.clear();
		sortWayList.clear();
		sortOrderList.clear();
		for (int j = 1; j <= getSearchSelColNum(); j++) {
			String paramName = "condition" + j;
			String value = request.getParameter(paramName);
			value = StringUtils.isEmpty(value) ? "" : value;
			setCondition(j - 1, value);

			paramName = "conditionValue" + j;
			value = request.getParameter(paramName);
			value = StringUtils.isEmpty(value) ? "" : value;
			setConditionValue(j - 1, value);

			paramName = "sortWayButton" + j;
			value = request.getParameter(paramName);
			value = StringUtils.isEmpty(value) ? "" : value;
			setSortWay(j - 1, value);

			paramName = "sortOrder" + j;
			value = request.getParameter(paramName);
			value = StringUtils.isEmpty(value) ? "" : value;
			setSortOrder(j - 1, value);
		}
	}

	public String getC_label1() {
		return C_label1;
	}

	public String getC_label2() {
		return C_label2;
	}

	public String getC_label3() {
		return C_label3;
	}

	public String getC_label4() {
		return C_label4;
	}

	public void setC_label1(String c_label1) {
		C_label1 = c_label1;
	}

	public void setC_label2(String c_label2) {
		C_label2 = c_label2;
	}

	public void setC_label3(String c_label3) {
		C_label3 = c_label3;
	}

	public void setC_label4(String c_label4) {
		C_label4 = c_label4;
	}

	public String getC_label5() {
		return C_label5;
	}

	public void setC_label5(String c_label5) {
		C_label5 = c_label5;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns Help Message
	 * @return String
	 */
	public String getSearchHelpMsg() {
		return searchHelpMsg;
	}

	/**
	 * Set Help Message.
	 * @param searchHelpMsg Help Message to set
	 */
	public void setSearchHelpMsg(String searchHelpMsg) {
		this.searchHelpMsg = StringCheck.latinToUtf8(searchHelpMsg);
	}

	public String getC_label6() {
		return C_label6;
	}

	public void setC_label6(String c_label6) {
		C_label6 = c_label6;
	}

	public String getC_label7() {
		return C_label7;
	}

	public void setC_label7(String c_label7) {
		C_label7 = c_label7;
	}

	public String getC_label8() {
		return C_label8;
	}

	public void setC_label8(String c_label8) {
		C_label8 = c_label8;
	}

	// 2013.06.27 yamagishi add. start
	/**
	 * C_label9を取得します。
	 * @return C_label9
	 */
	public String getC_label9() {
		return C_label9;
	}

	/**
	 * C_label9を設定します。
	 * @param C_label9 C_label9
	 */
	public void setC_label9(String C_label9) {
		this.C_label9 = C_label9;
	}

	// 2013.06.27 yamagishi add. end
	// 2019.09.25 yamamoto add. start
	/**
	 * C_label10を設定します。
	 * @param C_label10 C_label10
	 */
	public void setC_label10(String C_label10) {
		this.C_label10 = C_label10;
	}

	/**
	 * C_label10を取得します。
	 * @return C_label10
	 */
	public String getC_label10() {
		return C_label10;
	}

	/**
	 * C_label11を取得します。
	 * @return C_label11
	 */
	public String getC_label11() {
		return C_label11;
	}

	/**
	 * C_label11を設定します。
	 * @param C_label11 C_label11
	 */
	public void setC_label11(String C_label11) {
		this.C_label11 = C_label11;
	}

	/**
	 * C_label12を取得します。
	 * @return C_label12
	 */
	public String getC_label12() {
		return C_label12;
	}

	/**
	 * C_label12を設定します。
	 * @param C_label12 C_label12
	 */
	public void setC_label12(String C_label12) {
		this.C_label12 = C_label12;
	}

	/**
	 * C_label13を取得します。
	 * @return C_label13
	 */
	public String getC_label13() {
		return C_label13;
	}

	/**
	 * C_label13を設定します。
	 * @param C_label13 C_label13
	 */
	public void setC_label13(String C_label13) {
		this.C_label13 = C_label13;
	}

	/**
	 * C_label14を取得します。
	 * @return C_label14
	 */
	public String getC_label14() {
		return C_label14;
	}

	/**
	 * C_label14を設定します。
	 * @param C_label14 C_label14
	 */
	public void setC_label14(String C_label14) {
		this.C_label14 = C_label14;
	}

	/**
	 * C_label15を取得します。
	 * @return C_label15
	 */
	public String getC_label15() {
		return C_label15;
	}

	/**
	 * C_label15を設定します。
	 * @param C_label15 C_label15
	 */
	public void setC_label15(String C_label15) {
		this.C_label15 = C_label15;
	}

	/**
	 * C_label16を取得します。
	 * @return C_label16
	 */
	public String getC_label16() {
		return C_label16;
	}

	/**
	 * C_label16を設定します。
	 * @param C_label16 C_label16
	 */
	public void setC_label16(String C_label16) {
		this.C_label16 = C_label16;
	}

	/**
	 * C_label17を取得します。
	 * @return C_label17
	 */
	public String getC_label17() {
		return C_label17;
	}

	/**
	 * C_label17を設定します。
	 * @param C_label17 C_label17
	 */
	public void setC_label17(String C_label17) {
		this.C_label17 = C_label17;
	}

	/**
	 * logoutErrMsgを取得します。
	 * @return logoutErrMsg
	 */
	public String getLogoutErrMsg() {
		return logoutErrMsg;
	}

	/**
	 * logoutErrMsgを設定します。
	 * @param str logoutErrMsg
	 */
	public void setLogoutErrMsg(String str) {
		logoutErrMsg = StringCheck.latinToUtf8(str);
	}

	/**
	 * changeLangErrMsgを取得します。
	 * @return changeLangErrMsg
	 */
	public String getChangeLangErrMsg() {
		return changeLangErrMsg;
	}

	/**
	 * changeLangErrMsgを設定します。
	 * @param str changeLangErrMsg
	 */
	public void setChangeLangErrMsg(String str) {
		changeLangErrMsg = StringCheck.latinToUtf8(str);
	}

	// 2019.09.25 yamamoto add. end
	// 2020.03.17 yamamoto add. end
	/**
	 * listOrderErrMsgを取得します。
	 * @return listOrderErrMsg
	 */
	public String getlistOrderErrMsg() {
		return listOrderErrMsg;
	}

	/**
	 * listOrderErrMsgを設定します。
	 * @param str listOrderErrMsg
	 */
	public void setlistOrderErrMsg(String str) {
		listOrderErrMsg = StringCheck.latinToUtf8(str);
	}
	// 2020.03.17 yamamoto add. end
}
