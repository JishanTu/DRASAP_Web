package tyk.drasap.search;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.User;
import tyk.drasap.springfw.form.BaseForm;

/**
 * �������ʂ�\�������ʂɑΉ�
 *
 * @version 2013/06/27 yamagishi
 */
public class SearchResultForm extends BaseForm {
	/**
	 *
	 */
	String act;// �����𕪂��邽�߂̑���
	ArrayList<String> dispAttrList = new ArrayList<>(); // �\������1-6
	boolean isVisible;
	ArrayList<String> dispNameList = new ArrayList<>();// �\�������v���_�E���̖��̃��X�g
	ArrayList<String> dispKeyList = new ArrayList<>();// �\�������v���_�E����Key���X�g
	String outputPrinter;// �o�̓v���b�^
	ArrayList<String> printerNameList = new ArrayList<>();// �o�̓v���b�^�̃v���_�E���̖���
	ArrayList<String> printerKeyList = new ArrayList<>();// �o�̓v���b�^�̃v���_�E����Key
	String dispNumberPerPage = "0";// 1�y�[�W������̕\������
	String dispNumberOffest = "0";// �������ʂ�\������Ƃ���Off�Z�b�g�l�Biterate�^�O�Ŏg�p�����B
	// 1���ڂ���\������Ƃ��́A0�ƂȂ�B
	ArrayList<SearchResultElement> searchResultList = new ArrayList<>();// �������ʂ��i�[����B
	String outCsvAll = "false";// �S�������t�@�C���o�͂���Ȃ� true

	// �\�����ډp�ꉻ�Ή�
	// Header ��
	String H_label1 = "��������";
	String H_label2 = "�S�ă`�F�b�N";
	String H_label3 = "�S�ĊO��";
	String H_label4 = "�ĕ\��";
	String H_label5 = "�o�̓v���b�^";
	String H_label6 = "�\������";
	String H_label7 = "���X�g�\��";
	String H_label8 = "�T���l�C���\��";
	String H_label9 = "�T���l�C���T�C�Y";
	String H_label10 = "�^�p�x��";
	String H_label11 = "����w�����";
	// Footer ��
	String F_label1 = "{0}����  {1}-{2}���\��";
	String F_label2 = "�O��{0}��";
	String F_label3 = "����{0}��";
	String F_label4 = "�o��";
	String F_label5 = "�S������";
	String F_label6 = "�t�@�C���o��";
	String F_label7 = "�A�N�Z�X���x���ύX";
	// 2019.11.28 yamamoto add. start
	String F_label8 = "�}���`PDF�o��";
	// 2019.11.28 yamamoto add. end
	// 2020.03.10 yamamoto add. start
	String F_label9 = "PDF�P��zip�o��";
	String F_label10 = "�}�ԍ폜";
	String F_label11 = "�߂�";
	String F_label12 = "�L�����Z��";
	// 2020.03.10 yamamoto add. end
	String language = "";
	private SearchUtil sUtil = new SearchUtil();
	// 2013.06.27 yamagishi add. start
	public final char H_LABEL1_LINE_NO = 9;
	public final char H_LABEL2_LINE_NO = 10;
	public final char H_LABEL3_LINE_NO = 11;
	public final char H_LABEL4_LINE_NO = 12;
	public final char H_LABEL5_LINE_NO = 13;
	public final char H_LABEL6_LINE_NO = 14;
	public final char H_LABEL7_LINE_NO = 34; // ���X�g�\��
	public final char H_LABEL8_LINE_NO = 35; // �T���l�C���\��
	public final char H_LABEL9_LINE_NO = 36; // �T���l�C���T�C�Y
	public final char H_LABEL10_LINE_NO = 37;
	public final char H_LABEL11_LINE_NO = 38;
	public final char F_LABEL1_LINE_NO = 15;
	public final char F_LABEL2_LINE_NO = 16;
	public final char F_LABEL3_LINE_NO = 17;
	public final char F_LABEL4_LINE_NO = 18;
	public final char F_LABEL5_LINE_NO = 19;
	public final char F_LABEL6_LINE_NO = 20;
	public final char F_LABEL7_LINE_NO = 21;
	// 2013.06.27 yamagishi add. end
	// 2019.11.28 yamamoto add. start
	public final char F_LABEL8_LINE_NO = 30; // �}���`PDF�o��
	// 2019.11.28 yamamoto add. end
	// 2020.03.10 yamamoto add. start
	public final char F_LABEL9_LINE_NO = 31; // PDF�P��zip�o��
	// 2020.03.10 yamamoto add. end
	public final char F_LABEL10_LINE_NO = 33; // �}�ԍ폜
	public final char F_LABEL11_LINE_NO = 39;
	public final char F_LABEL12_LINE_NO = 40;
	// ---------------------------------------------------- method
	/* (�� Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */

	public void reset(HttpServletRequest request) {
		// SELECT�̏�����
		for (int i = 0; i < getViewSelColNum(); i++) {
			dispAttrList.add(i, "");
		}
		outputPrinter = "";// �o�͐�v���b�^
		act = "";
		// ��ʂɕ\������Ă����͈͂ɂ���
		// searchResultList�̑I�����ʂ�false�ŏ���������
		// false�ŏ��������Ȃ��ƁA�`�F�b�N�{�b�N�X���O�������������f����Ȃ�
		// �܂��u��ʂɕ\������Ă����͈́v�݂̂ɂ��Ȃ��ƁA�\������Ă��Ȃ��͈͂ɂ��Ă��N���A����Ă��܂��B
		int tempIndex = Integer.parseInt(dispNumberOffest);
		int tempPerPages = Math.min(searchResultList.size(), tempIndex + Integer.parseInt(dispNumberPerPage));
		for (int i = tempIndex; i < tempPerPages; i++) {
			getSearchResultElement(i).setSelected(false);
		}
	}

	public String getDispDwgNoName() {
		return sUtil.getSearchAttr(language, "DRWG_NO", false);
	}

	public String getDispCopiesName() {
		return sUtil.getSearchAttr(language, "COPIES", false);
	}

	public String getDispOutputSizeName() {
		return sUtil.getSearchAttr(language, "OUTPUT_SIZE", false);
	}

	/**
	 * dispAttr1-6�����ɂ��āASearchInfoMap������{�ꖼ�̂ɕϊ�����
	 * @return
	 */
	public String getDispAttrName(int idx) {
		String attr = dispAttrList.get(idx);
		if (attr == null || attr.length() == 0) {
			return "";
		}
		return sUtil.getSearchAttr(language, attr, false);
	}

	/**
	 * iterate�^�O�ɑΉ������邽�߂̃��\�b�h
	 * @param index
	 * @return
	 */
	public SearchResultElement getSearchResultElement(int index) {
		return searchResultList.get(index);
	}

	/**
	 * �ێ����Ă��錟�����ʂ����ɁACSV�t�@�C�����쐬����B
	 * @param out
	 * @param allAttr �S�����Ȃ� true
	 * @throws IOException
	 */
	public void writeAttrCsv(OutputStreamWriter out, boolean allAttr) throws IOException {
		// 1) ���ږ���
		out.write("\"" + getDispDwgNoName() + "\"");
		if (allAttr) {
			// 1�ڂ̓u�����N�Ȃ̂œǂݔ�΂�
			for (int i = 1; i < dispNameList.size(); i++) {
				writeCsvColumn(out, dispNameList.get(i));
			}
		} else {
			// �\������1-6
			for (int i = 0; i < getViewSelColNum(); i++) {
				writeCsvColumn(out, getDispAttrName(i));
			}
		}
		out.write("\r\n");
		// 2) ���ڂ̒l��
		for (int i = 0; i < searchResultList.size(); i++) {
			SearchResultElement resultElement = getSearchResultElement(i);
			out.write('"');
			out.write(resultElement.getDrwgNoFormated());
			out.write('"');
			if (allAttr) {
				// 1�ڂ̓u�����N�Ȃ̂œǂݔ�΂�
				for (int j = 1; j < dispKeyList.size(); j++) {
					writeCsvColumn(out, resultElement.getAttr(dispKeyList.get(j)));
				}
			} else {
				// �\������1-6
				for (int j = 0; j < getViewSelColNum(); j++) {
					writeCsvColumn(out, resultElement.getAttr(dispAttrList.get(j)));
				}
			}
			out.write("\r\n");
		}

	}

	/**
	 * writeAttrCsv����g�p�����BCSV��1���ڂ������o��
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	private void writeCsvColumn(OutputStreamWriter out, String value) throws IOException {
		out.write(',');
		out.write('"');
		out.write(value);
		out.write('"');
	}

	// ---------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public ArrayList<String> getDispKeyList() {
		return dispKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getDispNameList() {
		return dispNameList;
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
	 * @param string
	 */
	public void setDispAttr(int idx, String string) {
		dispAttrList.add(idx, string);
	}

	/**
	 *
	 * @param lang
	 */
	public void setDefaulDispAttrs(String lang) {
		for (int i = 0; i < getViewSelColNum(); i++) {
			if (i == 0) {
				setDispAttr(0, "DRWG_SIZE");
			} else if (i == 1) {
				setDispAttr(1, "DRWG_TYPE");
			} else if (i == 2) {
				setDispAttr(2, "MACHINE_" + ("Japanese".equals(lang) ? "JP" : "EN"));
			} else if (i == 3) {
				setDispAttr(3, "PROCUREMENT");
			} else if (i == 4) {
				setDispAttr(4, "SUPPLYER_" + ("Japanese".equals(lang) ? "JP" : "EN"));
			} else if (i == 5) {
				setDispAttr(5, "CREATE_DATE");
			} else {
				setDispAttr(i, "");
			}
		}
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
	public void setDispAttrList(ArrayList<String> list) {
		dispAttrList = list;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getPrinterKeyList() {
		return printerKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getPrinterNameList() {
		return printerNameList;
	}

	/**
	 * @return
	 */
	public String getOutputPrinter() {
		return outputPrinter;
	}

	public String getOutputPrinterKey(int idx) {
		return printerKeyList.get(idx);
	}

	/**
	 * @param string
	 */
	public void setOutputPrinter(String string) {
		outputPrinter = string;
	}

	/**
	 * @return
	 */
	public String getDispNumberPerPage() {
		return dispNumberPerPage;
	}

	/**
	 * @return
	 */
	public ArrayList<SearchResultElement> getSearchResultList() {
		return searchResultList;
	}

	/**
	 * @return
	 */
	public String getDispNumberOffest() {
		return dispNumberOffest;
	}

	/**
	 * @param string
	 */
	public void setDispNumberOffest(String string) {
		dispNumberOffest = string;
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
	 * @param list
	 */
	public void setSearchResultList(ArrayList<SearchResultElement> list) {
		searchResultList = list;
	}

	/**
	 * @return
	 */
	public String getOutCsvAll() {
		return outCsvAll;
	}

	/**
	 * @return
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @return
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @param string
	 */
	public void setOutCsvAll(String string) {
		outCsvAll = string;
	}

	public String getF_label1() {
		int cntStart = Integer.parseInt(dispNumberOffest) + 1;// offset��1�����Z
		if (searchResultList.size() == 0) {
			cntStart = 0;
		}
		int cntEnd = Math.min(Integer.parseInt(dispNumberOffest) + Integer.parseInt(dispNumberPerPage), // offset��1�y�[�W�����茏�������Z
				searchResultList.size()); // ��������resultList�̃T�C�Y�̏�������
		return DrasapUtil.createMessage(F_label1, Integer.toString(searchResultList.size()), Integer.toString(cntStart), Integer.toString(cntEnd));
	}

	public String getF_label2() {
		return DrasapUtil.createMessage(F_label2, dispNumberPerPage);
	}

	public String getF_label3() {
		return DrasapUtil.createMessage(F_label3, dispNumberPerPage);
	}

	public String getF_label4() {
		return F_label4;
	}

	public String getF_label5() {
		return F_label5;
	}

	public String getF_label6() {
		return F_label6;
	}

	public String getF_label7() {
		return F_label7;
	}

	public String getF_label8() {
		return F_label8;
	}

	public String getF_label9() {
		return F_label9;
	}

	public String getF_label10() {
		return F_label10;
	}

	public String getF_label11() {
		return F_label11;
	}

	public String getF_label12() {
		return F_label12;
	}

	public String getH_label1() {
		return H_label1;
	}

	public String getH_label2() {
		return H_label2;
	}

	public String getH_label3() {
		return H_label3;
	}

	public String getH_label4() {
		return H_label4;
	}

	public String getH_label5() {
		return H_label5;
	}

	public String getH_label6() {
		return H_label6;
	}

	public String getH_label7() {
		return H_label7;
	}

	public String getH_label8() {
		return H_label8;
	}

	public String getH_label9() {
		return H_label9;
	}

	public String getH_label10() {
		return H_label10;
	}

	public String getH_label11() {
		return H_label11;
	}

	public void setF_label1(String f_label1) {
		F_label1 = f_label1;
	}

	public void setF_label2(String f_label2) {
		F_label2 = f_label2;
	}

	public void setF_label3(String f_label3) {
		F_label3 = f_label3;
	}

	public void setF_label4(String f_label4) {
		F_label4 = f_label4;
	}

	public void setF_label5(String f_label5) {
		F_label5 = f_label5;
	}

	public void setF_label6(String f_label6) {
		F_label6 = f_label6;
	}

	public void setF_label7(String f_label7) {
		F_label7 = f_label7;
	}

	public void setF_label8(String f_label8) {
		F_label8 = f_label8;
	}

	public void setF_label9(String f_label9) {
		F_label9 = f_label9;
	}

	public void setF_label10(String f_label10) {
		F_label10 = f_label10;
	}

	public void setF_label11(String f_label11) {
		F_label11 = f_label11;
	}

	public void setF_label12(String f_label12) {
		F_label12 = f_label12;
	}

	public void setH_label1(String h_label1) {
		H_label1 = h_label1;
	}

	public void setH_label2(String h_label2) {
		H_label2 = h_label2;
	}

	public void setH_label3(String h_label3) {
		H_label3 = h_label3;
	}

	public void setH_label4(String h_label4) {
		H_label4 = h_label4;
	}

	public void setH_label5(String h_label5) {
		H_label5 = h_label5;
	}

	public void setH_label6(String h_label6) {
		H_label6 = h_label6;
	}

	public void setH_label7(String h_label7) {
		H_label7 = h_label7;
	}

	public void setH_label8(String h_label8) {
		H_label8 = h_label8;
	}

	public void setH_label9(String h_label9) {
		H_label9 = h_label9;
	}

	public void setH_label10(String h_label10) {
		H_label10 = h_label10;
	}

	public void setH_label11(String h_label11) {
		H_label11 = h_label11;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
