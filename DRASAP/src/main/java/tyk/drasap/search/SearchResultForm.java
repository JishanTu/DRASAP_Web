package tyk.drasap.search;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.DrasapUtil;

/**
 * �������ʂ�\�������ʂɑΉ�
 *
 * @version 2013/06/27 yamagishi
 */
public class SearchResultForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String act;// �����𕪂��邽�߂̑���
	String dispAttr1;// �\������1
	String dispAttr2;
	String dispAttr3;
	String dispAttr4;
	String dispAttr5;
	String dispAttr6;
	ArrayList<String> dispNameList = new ArrayList<String>();// �\�������v���_�E���̖��̃��X�g
	ArrayList<String> dispKeyList = new ArrayList<String>();// �\�������v���_�E����Key���X�g
	String outputPrinter;// �o�̓v���b�^
	ArrayList<String> printerNameList = new ArrayList<String>();// �o�̓v���b�^�̃v���_�E���̖���
	ArrayList<String> printerKeyList = new ArrayList<String>();// �o�̓v���b�^�̃v���_�E����Key
	String dispNumberPerPage = "0";// 1�y�[�W������̕\������
	String dispNumberOffest = "0";// �������ʂ�\������Ƃ���Off�Z�b�g�l�Biterate�^�O�Ŏg�p�����B
							// 1���ڂ���\������Ƃ��́A0�ƂȂ�B
	ArrayList<SearchResultElement> searchResultList = new ArrayList<SearchResultElement>();// �������ʂ��i�[����B
	String outCsvAll = "false";// �S�������t�@�C���o�͂���Ȃ� true

	// �\�����ډp�ꉻ�Ή�
	// Header ��
	String H_label1 = "��������";
	String H_label2 = "�S�ă`�F�b�N";
	String H_label3 = "�S�ĊO��";
	String H_label4 = "�ĕ\��";
	String H_label5 = "�o�̓v���b�^";
	String H_label6 = "�\������";
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
	// ---------------------------------------------------- method
	/* (�� Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// SELECT�̏�����
		dispAttr1 = "";// �\������1
		dispAttr2 = "";
		dispAttr3 = "";
		dispAttr4 = "";
		dispAttr5 = "";
		dispAttr6 = "";
		outputPrinter = "";// �o�͐�v���b�^
		//
		act = "";
		// ��ʂɕ\������Ă����͈͂ɂ���
		// searchResultList�̑I�����ʂ�false�ŏ���������
		// false�ŏ��������Ȃ��ƁA�`�F�b�N�{�b�N�X���O�������������f����Ȃ�
		// �܂��u��ʂɕ\������Ă����͈́v�݂̂ɂ��Ȃ��ƁA�\������Ă��Ȃ��͈͂ɂ��Ă��N���A����Ă��܂��B
		int tempIndex = Integer.parseInt(dispNumberOffest);
		int tempPerPages = Math.min(searchResultList.size(), tempIndex + Integer.parseInt(dispNumberPerPage));
		for(int i = tempIndex; i < tempPerPages; i++){
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
	 * dispAttr1�����ɂ��āASearchInfoMap������{�ꖼ�̂ɕϊ�����
	 * @return
	 */
	public String getDispAttr1Name() {
		if(dispAttr1 == null || dispAttr1.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr1, false);
		}
	}
	public String getDispAttr2Name() {
		if(dispAttr2 == null || dispAttr2.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr2, false);
		}
	}
	public String getDispAttr3Name() {
		if(dispAttr3 == null || dispAttr3.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr3, false);
		}
	}
	public String getDispAttr4Name() {
		if(dispAttr4 == null || dispAttr4.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr4, false);
		}
	}
	public String getDispAttr5Name() {
		if(dispAttr5 == null || dispAttr5.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr5, false);
		}
	}
	public String getDispAttr6Name() {
		if(dispAttr6 == null || dispAttr6.length() == 0){
			return "";
		} else {
			return sUtil.getSearchAttr(language, dispAttr6, false);
		}
	}
	/**
	 * iterate�^�O�ɑΉ������邽�߂̃��\�b�h
	 * @param index
	 * @return
	 */
	public SearchResultElement getSearchResultElement(int index){
		return this.searchResultList.get(index);
	}
	/**
	 * �ێ����Ă��錟�����ʂ����ɁACSV�t�@�C�����쐬����B
	 * @param out
	 * @param allAttr �S�����Ȃ� true
	 * @throws IOException
	 */
	public void writeAttrCsv(OutputStreamWriter out, boolean allAttr) throws IOException{
		// 1) ���ږ���
		out.write("\""+getDispDwgNoName()+"\"");
		if(allAttr){
			// 1�ڂ̓u�����N�Ȃ̂œǂݔ�΂�
			for(int i = 1; i < dispNameList.size(); i++){
				writeCsvColumn(out, (String) dispNameList.get(i));
			}
		} else {
			// �\������1
			writeCsvColumn(out, getDispAttr1Name());
			writeCsvColumn(out, getDispAttr2Name());
			writeCsvColumn(out, getDispAttr3Name());
			writeCsvColumn(out, getDispAttr4Name());
			writeCsvColumn(out, getDispAttr5Name());
			writeCsvColumn(out, getDispAttr6Name());
		}
		out.write("\r\n");
		// 2) ���ڂ̒l��
		for(int i = 0; i < searchResultList.size(); i++){
			SearchResultElement resultElement = getSearchResultElement(i);
			out.write('"');
			out.write(resultElement.getDrwgNoFormated());
			out.write('"');
			if(allAttr){
				// 1�ڂ̓u�����N�Ȃ̂œǂݔ�΂�
				for(int j = 1; j < dispKeyList.size(); j++){
					writeCsvColumn(out, resultElement.getAttr((String) dispKeyList.get(j)));
				}
			} else {
				// �\������1-6
				writeCsvColumn(out, resultElement.getAttr(dispAttr1));
				writeCsvColumn(out, resultElement.getAttr(dispAttr2));
				writeCsvColumn(out, resultElement.getAttr(dispAttr3));
				writeCsvColumn(out, resultElement.getAttr(dispAttr4));
				writeCsvColumn(out, resultElement.getAttr(dispAttr5));
				writeCsvColumn(out, resultElement.getAttr(dispAttr6));
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
	private void writeCsvColumn(OutputStreamWriter out, String value) throws IOException{
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
	 * @param string
	 */
	public void setOutCsvAll(String string) {
		outCsvAll = string;
	}
	public String getF_label1() {
		int cntStart = Integer.parseInt(dispNumberOffest) + 1;// offset��1�����Z
		if(searchResultList.size()==0){
			cntStart = 0;
		}
		int cntEnd = Math.min(Integer.parseInt(dispNumberOffest) + Integer.parseInt(dispNumberPerPage),	// offset��1�y�[�W�����茏�������Z
				searchResultList.size());	// ��������resultList�̃T�C�Y�̏�������
		return DrasapUtil.createMessage(F_label1,Integer.toString(searchResultList.size()),Integer.toString(cntStart),Integer.toString(cntEnd));
	}
	public String getF_label2() {
		return DrasapUtil.createMessage(F_label2,dispNumberPerPage);
	}
	public String getF_label3() {
		return DrasapUtil.createMessage(F_label3,dispNumberPerPage);
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
