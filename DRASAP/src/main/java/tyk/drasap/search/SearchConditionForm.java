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
	String act = "";// Action�N���X�ŏ����𕪂��邽�߂̑���
	ArrayList<String> conditionNameList;// �������ڃv���_�E���̖��̃��X�g
	ArrayList<String> conditionKeyList;// �������ڃv���_�E����Key���X�g

	ArrayList<String> conditionList = new ArrayList<>();// �����̍��ږ�
	ArrayList<String> conditionValueList = new ArrayList<>();// �����̎w��l
	ArrayList<String> sortWayList = new ArrayList<>();// �����A�~��
	ArrayList<String> sortOrderList = new ArrayList<>();// �\�[�g�D�揇

	ArrayList<String> sortOrderNameList;// �\�[�g�D�揇�̖��̃��X�g
	ArrayList<String> sortOrderKeyList;// �\�[�g�D�揇��Key���X�g
	boolean onlyNewest = false;// �ŐV�ǔԂ݂̂Ȃ�true
	boolean orderDrwgNo = false; // �}�Ԏw�菇�Ȃ�true 2020.03.11 yamamoto add.
	String multipleDrwgNo;// �����}��	2013.06.26 yamagishi add.
	String eachCondition;// �S�Ă̌���������OR�܂���AND
	String displayCount;// �\������
	private ArrayList<String> dispAttrList = new ArrayList<>(); // �\������1�E�E�E�������ʉ�ʂ���R�s�[������
	// �\�����ډp�ꉻ�Ή�
	String language = "Japanese";
	// Condition ��
	String C_label1 = "�ŐV�ǔԂ̂ݕ\��";
	String C_label2 = "�S�Ă̑���������";
	String C_label3 = "�����J�n";
	String C_label4 = "�������ʕ\����";
	String C_label5 = "�}�ʌ���";
	String C_label6 = "�E��";
	String C_label7 = "���O";
	String C_label8 = "������";
	String C_label9 = "�����}��"; // 2013.06.27 yamagishi add.
	// 2019.09.25 yamamoto add. start
	String C_label10 = "�p�X���[�h�ύX";
	String C_label11 = "���O�A�E�g";
	String C_label12 = "���}�ɍ�ƈ˗�";
	String C_label13 = "���}�ɍ�ƈ˗��ڍ�";
	String C_label14 = "���}�ɍ�ƃ��X�g";
	String C_label15 = "�A�N�Z�X���x���ꊇ�X�V";
	String C_label16 = "�A�N�Z�X���x���X�V����";
	// 2019.09.25 yamamoto add. end
	// 2020.03.10 yamamoto add. start
	String C_label17 = "�}�Ԏw�菇";
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
	public final char C_LABEL17_LINE_NO = 32; // �}�Ԏw�菇
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
	/* (�� Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(HttpServletRequest request) {
		// SELECT�̏�����
		for (int i = 0; i < getSearchSelColNum(); i++) {
			conditionList.add(i, "");
			conditionValueList.add(i, "");
			sortWayList.add(i, "");
			sortOrderList.add(i, "");
		}
		displayCount = "";
		// CHECKBOX�̏�����
		onlyNewest = false;
		orderDrwgNo = false;
		// RADIO�̏�����
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
		// UTF-8�֕ϊ�����
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
		// UTF-8�֕ϊ�����
		sortWayList.add(idx, StringCheck.latinToUtf8(string));
	}

	/**
	 * @param string
	 */
	public void setSortWayList(ArrayList<String> list) {
		// UTF-8�֕ϊ�����
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
	 * multipleDrwgNo���擾���܂��B
	 * @return multipleDrwgNo
	 */
	public String getMultipleDrwgNo() {
		return multipleDrwgNo;
	}

	/**
	 * multipleDrwgNo��ݒ肵�܂��B
	 * @param multipleDrwgNo multipleDrwgNo
	 */
	public void setMultipleDrwgNo(String multipleDrwgNo) {
		// UTF-8�֕ϊ�����
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
	 * C_label9���擾���܂��B
	 * @return C_label9
	 */
	public String getC_label9() {
		return C_label9;
	}

	/**
	 * C_label9��ݒ肵�܂��B
	 * @param C_label9 C_label9
	 */
	public void setC_label9(String C_label9) {
		this.C_label9 = C_label9;
	}

	// 2013.06.27 yamagishi add. end
	// 2019.09.25 yamamoto add. start
	/**
	 * C_label10��ݒ肵�܂��B
	 * @param C_label10 C_label10
	 */
	public void setC_label10(String C_label10) {
		this.C_label10 = C_label10;
	}

	/**
	 * C_label10���擾���܂��B
	 * @return C_label10
	 */
	public String getC_label10() {
		return C_label10;
	}

	/**
	 * C_label11���擾���܂��B
	 * @return C_label11
	 */
	public String getC_label11() {
		return C_label11;
	}

	/**
	 * C_label11��ݒ肵�܂��B
	 * @param C_label11 C_label11
	 */
	public void setC_label11(String C_label11) {
		this.C_label11 = C_label11;
	}

	/**
	 * C_label12���擾���܂��B
	 * @return C_label12
	 */
	public String getC_label12() {
		return C_label12;
	}

	/**
	 * C_label12��ݒ肵�܂��B
	 * @param C_label12 C_label12
	 */
	public void setC_label12(String C_label12) {
		this.C_label12 = C_label12;
	}

	/**
	 * C_label13���擾���܂��B
	 * @return C_label13
	 */
	public String getC_label13() {
		return C_label13;
	}

	/**
	 * C_label13��ݒ肵�܂��B
	 * @param C_label13 C_label13
	 */
	public void setC_label13(String C_label13) {
		this.C_label13 = C_label13;
	}

	/**
	 * C_label14���擾���܂��B
	 * @return C_label14
	 */
	public String getC_label14() {
		return C_label14;
	}

	/**
	 * C_label14��ݒ肵�܂��B
	 * @param C_label14 C_label14
	 */
	public void setC_label14(String C_label14) {
		this.C_label14 = C_label14;
	}

	/**
	 * C_label15���擾���܂��B
	 * @return C_label15
	 */
	public String getC_label15() {
		return C_label15;
	}

	/**
	 * C_label15��ݒ肵�܂��B
	 * @param C_label15 C_label15
	 */
	public void setC_label15(String C_label15) {
		this.C_label15 = C_label15;
	}

	/**
	 * C_label16���擾���܂��B
	 * @return C_label16
	 */
	public String getC_label16() {
		return C_label16;
	}

	/**
	 * C_label16��ݒ肵�܂��B
	 * @param C_label16 C_label16
	 */
	public void setC_label16(String C_label16) {
		this.C_label16 = C_label16;
	}

	/**
	 * C_label17���擾���܂��B
	 * @return C_label17
	 */
	public String getC_label17() {
		return C_label17;
	}

	/**
	 * C_label17��ݒ肵�܂��B
	 * @param C_label17 C_label17
	 */
	public void setC_label17(String C_label17) {
		this.C_label17 = C_label17;
	}

	/**
	 * logoutErrMsg���擾���܂��B
	 * @return logoutErrMsg
	 */
	public String getLogoutErrMsg() {
		return logoutErrMsg;
	}

	/**
	 * logoutErrMsg��ݒ肵�܂��B
	 * @param str logoutErrMsg
	 */
	public void setLogoutErrMsg(String str) {
		logoutErrMsg = StringCheck.latinToUtf8(str);
	}

	/**
	 * changeLangErrMsg���擾���܂��B
	 * @return changeLangErrMsg
	 */
	public String getChangeLangErrMsg() {
		return changeLangErrMsg;
	}

	/**
	 * changeLangErrMsg��ݒ肵�܂��B
	 * @param str changeLangErrMsg
	 */
	public void setChangeLangErrMsg(String str) {
		changeLangErrMsg = StringCheck.latinToUtf8(str);
	}

	// 2019.09.25 yamamoto add. end
	// 2020.03.17 yamamoto add. end
	/**
	 * listOrderErrMsg���擾���܂��B
	 * @return listOrderErrMsg
	 */
	public String getlistOrderErrMsg() {
		return listOrderErrMsg;
	}

	/**
	 * listOrderErrMsg��ݒ肵�܂��B
	 * @param str listOrderErrMsg
	 */
	public void setlistOrderErrMsg(String str) {
		listOrderErrMsg = StringCheck.latinToUtf8(str);
	}
	// 2020.03.17 yamamoto add. end
}
