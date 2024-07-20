package tyk.drasap.search;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.StringCheck;

/**
 * @version 2013/06/26 yamagishi
 */
public class SearchConditionForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String act = "";// Actionクラスで処理を分けるための属性
	ArrayList<String> conditionNameList;// 検索項目プルダウンの名称リスト
	ArrayList<String> conditionKeyList;// 検索項目プルダウンのKeyリスト
	String condition1;// 検索の項目名
	String condition2;
	String condition3;
	String condition4;
	String condition5;
	String condition1Value;// 検索の指定値
	String condition2Value;
	String condition3Value;
	String condition4Value;
	String condition5Value;
	String sortWay1;// 昇順、降順
	String sortWay2;
	String sortWay3;
	String sortWay4;
	String sortWay5;
	String sortOrder1;// ソート優先順
	String sortOrder2;
	String sortOrder3;
	String sortOrder4;
	String sortOrder5;
	ArrayList<String> sortOrderNameList;// ソート優先順の名称リスト
	ArrayList<String> sortOrderKeyList;// ソート優先順のKeyリスト
	boolean onlyNewest = false;// 最新追番のみならtrue
	boolean orderDrwgNo = false; // 図番指定順ならtrue 2020.03.11 yamamoto add.
	String multipleDrwgNo;// 複数図番	2013.06.26 yamagishi add.
	String eachCondition;// 全ての検索条件をORまたはAND
	String displayCount;// 表示件数
	private String dispAttr1;// 表示属性1・・・検索結果画面からコピーさせる
	private String dispAttr2;
	private String dispAttr3;
	private String dispAttr4;
	private String dispAttr5;
	private String dispAttr6;
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
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// SELECTの初期化
		condition1="";
		condition2="";
		condition3="";
		condition4="";
		condition5="";
		sortOrder1="";
		sortOrder2="";
		sortOrder3="";
		sortOrder4="";
		sortOrder5="";
		displayCount="";
		// CHECKBOXの初期化
		onlyNewest=false;
		orderDrwgNo=false;
		// RADIOの初期化
		eachCondition="";
		//
		act="";
		condition1Value="";
		condition2Value="";
		condition3Value="";
		condition4Value="";
		condition5Value="";
		sortWay1="";
		sortWay2="";
		sortWay3="";
		sortWay4="";
		sortWay5="";
		multipleDrwgNo=""; // 2013.06.27 yamagishi add.
	}
	// --------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getCondition1() {
		return condition1;
	}

	/**
	 * @return
	 */
	public String getCondition1Value() {
		return condition1Value;
	}

	/**
	 * @return
	 */
	public String getCondition2() {
		return condition2;
	}

	/**
	 * @return
	 */
	public String getCondition2Value() {
		return condition2Value;
	}

	/**
	 * @return
	 */
	public String getCondition3() {
		return condition3;
	}

	/**
	 * @return
	 */
	public String getCondition3Value() {
		return condition3Value;
	}

	/**
	 * @return
	 */
	public String getCondition4() {
		return condition4;
	}

	/**
	 * @return
	 */
	public String getCondition4Value() {
		return condition4Value;
	}

	/**
	 * @return
	 */
	public String getCondition5() {
		return condition5;
	}

	/**
	 * @return
	 */
	public String getCondition5Value() {
		return condition5Value;
	}

	/**
	 * @param string
	 */
	public void setCondition1(String string) {
		condition1 = string;
	}

	/**
	 * @param string
	 */
	public void setCondition1Value(String string) {
		// UTF-8へ変換する
		condition1Value = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setCondition2(String string) {
		condition2 = string;
	}

	/**
	 * @param string
	 */
	public void setCondition2Value(String string) {
		// UTF-8へ変換する
		condition2Value = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setCondition3(String string) {
		condition3 = string;
	}

	/**
	 * @param string
	 */
	public void setCondition3Value(String string) {
		// UTF-8へ変換する
		condition3Value = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setCondition4(String string) {
		condition4 = string;
	}

	/**
	 * @param string
	 */
	public void setCondition4Value(String string) {
		// UTF-8へ変換する
		condition4Value = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setCondition5(String string) {
		condition5 = string;
	}

	/**
	 * @param string
	 */
	public void setCondition5Value(String string) {
		// UTF-8へ変換する
		condition5Value = StringCheck.latinToUtf8(string);
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
	public String getSortWay1() {
		return sortWay1;
	}

	/**
	 * @return
	 */
	public String getSortWay2() {
		return sortWay2;
	}

	/**
	 * @return
	 */
	public String getSortWay3() {
		return sortWay3;
	}

	/**
	 * @return
	 */
	public String getSortWay4() {
		return sortWay4;
	}

	/**
	 * @return
	 */
	public String getSortWay5() {
		return sortWay5;
	}

	/**
	 * @param string
	 */
	public void setSortWay1(String string) {
		// UTF-8へ変換する
		sortWay1 = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setSortWay2(String string) {
		// UTF-8へ変換する
		sortWay2 = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setSortWay3(String string) {
		// UTF-8へ変換する
		sortWay3 = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setSortWay4(String string) {
		// UTF-8へ変換する
		sortWay4 = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void setSortWay5(String string) {
		// UTF-8へ変換する
		sortWay5 = StringCheck.latinToUtf8(string);
	}

	/**
	 * @return
	 */
	public String getSortOrder1() {
		return sortOrder1;
	}

	/**
	 * @return
	 */
	public String getSortOrder2() {
		return sortOrder2;
	}

	/**
	 * @return
	 */
	public String getSortOrder3() {
		return sortOrder3;
	}

	/**
	 * @return
	 */
	public String getSortOrder4() {
		return sortOrder4;
	}

	/**
	 * @return
	 */
	public String getSortOrder5() {
		return sortOrder5;
	}

	/**
	 * @param string
	 */
	public void setSortOrder1(String string) {
		sortOrder1 = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder2(String string) {
		sortOrder2 = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder3(String string) {
		sortOrder3 = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder4(String string) {
		sortOrder4 = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder5(String string) {
		sortOrder5 = string;
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
	public String getDispAttr1() {
		return dispAttr1;
	}

	/**
	 * @return
	 */
	public String getDispAttr2() {
		return dispAttr2;
	}

	/**
	 * @return
	 */
	public String getDispAttr3() {
		return dispAttr3;
	}

	/**
	 * @return
	 */
	public String getDispAttr4() {
		return dispAttr4;
	}

	/**
	 * @return
	 */
	public String getDispAttr5() {
		return dispAttr5;
	}

	/**
	 * @return
	 */
	public String getDispAttr6() {
		return dispAttr6;
	}

	/**
	 * @param string
	 */
	public void setDispAttr1(String string) {
		dispAttr1 = string;
	}

	/**
	 * @param string
	 */
	public void setDispAttr2(String string) {
		dispAttr2 = string;
	}

	/**
	 * @param string
	 */
	public void setDispAttr3(String string) {
		dispAttr3 = string;
	}

	/**
	 * @param string
	 */
	public void setDispAttr4(String string) {
		dispAttr4 = string;
	}

	/**
	 * @param string
	 */
	public void setDispAttr5(String string) {
		dispAttr5 = string;
	}

	/**
	 * @param string
	 */
	public void setDispAttr6(String string) {
		dispAttr6 = string;
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
	    this.logoutErrMsg = StringCheck.latinToUtf8(str);
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
	    this.changeLangErrMsg = StringCheck.latinToUtf8(str);
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
	    this.listOrderErrMsg = StringCheck.latinToUtf8(str);
	}
// 2020.03.17 yamamoto add. end
}
