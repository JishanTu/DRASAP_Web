package tyk.drasap.genzu_irai;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.StringCheck;

/** 
 * ���}�ɍ�ƈ˗���Form
 */
public class RequestForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList iraiList = new ArrayList();//�˗����e�̃��X�g
	ArrayList list = new ArrayList();//�v�����^�̃��X�g
	ArrayList genzuNameList = new ArrayList();//���}���e�̃��X�g
	ArrayList syukusyouList = new ArrayList();//�k�����X�g
	ArrayList saizuList = new ArrayList();//�T�C�Y���X�g
	ArrayList syutuList = new ArrayList();//�o�͐惊�X�g
	
	String irai;//�˗����e
	String syutu;//�o�͐�
	String hiddenSyutu;
	String gouki1;//�����E���@
	String gouki2;
	String gouki3;
	String gouki4;
	String gouki5;
	String genzu1;//���}���e
	String genzu2;
	String genzu3;
	String genzu4;
	String genzu5;
	String kaisiNo1;//�J�n�ԍ�
	String kaisiNo2;
	String kaisiNo3;
	String kaisiNo4;
	String kaisiNo5;
	String syuuryouNo1;//�I���ԍ�
	String syuuryouNo2;
	String syuuryouNo3;
	String syuuryouNo4;
	String syuuryouNo5;
	String busuu1;//����
	String busuu2;
	String busuu3;
	String busuu4;
	String busuu5;
	String syukusyou1;//�k���敪
	String syukusyou2;
	String syukusyou3;
	String syukusyou4;
	String syukusyou5;
	String size1;//�k���T�C�Y
	String size2;
	String size3;
	String size4;
	String size5;
	String hiddenNo1;//�s�ԍ���hidden
	String hiddenNo2;
	String hiddenNo3;
	String hiddenNo4;
	String hiddenNo5;
	String zumenAll; //�W�J�����}�ʂ��S�ēo�^�ς̏ꍇ��"1"�ɂ���
	String action;//�A�N�V����
	String printer_flag;//���O�C���҂̌�������̗��p�O���[�v��p�v�����^�X�^���v�t���O
	String hani_s;//�͈͎w��(�J�n)
	String hani_e;//�͈͎w��(�I��)
	String errlog;//�J�n�܂��͏I���ԍ��ł̃G���[�p
	String errNumber;//�J�n�ƏI���ԍ��̃G���[�p
	String hani_t;//
	String hani_sitei;//�͈͎w��ŃG���[�p�Ɏg�p
	
	//---------------------------------------------------------- Methods
	/**
	 * 1�s�ڂɉ��炩�̓��͂������ true
	 * @return
	 */
	public boolean isInputedLine1(){
		// ���@
		if(! "".equals(gouki1)) return true;
		// ���}���e
		if(! "".equals(genzu1)) return true;
		// �J�n�}��
		if(! "".equals(kaisiNo1)) return true;
		// �I���}��
		if(! "".equals(syuuryouNo1)) return true;
		// ����
		if(! "".equals(busuu1)) return true;
		// �k��
		if("1".equals(syukusyou1)) return true;
		// �T�C�Y
		if(! "".equals(size1)) return true;
		
		return false;
	}
	/**
	 * 2�s�ڂɉ��炩�̓��͂������ true
	 * @return
	 */
	public boolean isInputedLine2(){
		// ���@
		if(! "".equals(gouki2)) return true;
		// ���}���e
		if(! "".equals(genzu2)) return true;
		// �J�n�}��
		if(! "".equals(kaisiNo2)) return true;
		// �I���}��
		if(! "".equals(syuuryouNo2)) return true;
		// ����
		if(! "".equals(busuu2)) return true;
		// �k��
		if("1".equals(syukusyou2)) return true;
		// �T�C�Y
		if(! "".equals(size2)) return true;
		
		return false;
	}
	/**
	 * 3�s�ڂɉ��炩�̓��͂������ true
	 * @return
	 */
	public boolean isInputedLine3(){
		// ���@
		if(! "".equals(gouki3)) return true;
		// ���}���e
		if(! "".equals(genzu3)) return true;
		// �J�n�}��
		if(! "".equals(kaisiNo3)) return true;
		// �I���}��
		if(! "".equals(syuuryouNo3)) return true;
		// ����
		if(! "".equals(busuu3)) return true;
		// �k��
		if("1".equals(syukusyou3)) return true;
		// �T�C�Y
		if(! "".equals(size3)) return true;
		
		return false;
	}
	/**
	 * 4�s�ڂɉ��炩�̓��͂������ true
	 * @return
	 */
	public boolean isInputedLine4(){
		// ���@
		if(! "".equals(gouki4)) return true;
		// ���}���e
		if(! "".equals(genzu4)) return true;
		// �J�n�}��
		if(! "".equals(kaisiNo4)) return true;
		// �I���}��
		if(! "".equals(syuuryouNo4)) return true;
		// ����
		if(! "".equals(busuu4)) return true;
		// �k��
		if("1".equals(syukusyou4)) return true;
		// �T�C�Y
		if(! "".equals(size4)) return true;
		
		return false;
	}
	/**
	 * 5�s�ڂɉ��炩�̓��͂������ true
	 * @return
	 */
	public boolean isInputedLine5(){
		// ���@
		if(! "".equals(gouki5)) return true;
		// ���}���e
		if(! "".equals(genzu5)) return true;
		// �J�n�}��
		if(! "".equals(kaisiNo5)) return true;
		// �I���}��
		if(! "".equals(syuuryouNo5)) return true;
		// ����
		if(! "".equals(busuu5)) return true;
		// �k��
		if("1".equals(syukusyou5)) return true;
		// �T�C�Y
		if(! "".equals(size5)) return true;
		
		return false;
	}
	/**
	 * ���͂��ꂽ�������A���p�̑啶���ɕύX����B
	 * �܂��}�Ԃɂ��ẮA�n�C�t�������ɐ��`����B�}�ʓo�^�˗��A�}�ʏo�͎w���̂Ƃ��̂݁B�ύX '04/07/22�B
	 */
	public void formatInpuedData(){
		// �����E���@
		gouki1 = StringCheck.changeDbToSbAscii(gouki1).toUpperCase();
		gouki2 = StringCheck.changeDbToSbAscii(gouki2).toUpperCase();
		gouki3 = StringCheck.changeDbToSbAscii(gouki3).toUpperCase();
		gouki4 = StringCheck.changeDbToSbAscii(gouki4).toUpperCase();
		gouki5 = StringCheck.changeDbToSbAscii(gouki5).toUpperCase();
		// �J�n�ԍ�
		kaisiNo1 = StringCheck.changeDbToSbAscii(kaisiNo1).toUpperCase();
		kaisiNo2 = StringCheck.changeDbToSbAscii(kaisiNo2).toUpperCase();
		kaisiNo3 = StringCheck.changeDbToSbAscii(kaisiNo3).toUpperCase();
		kaisiNo4 = StringCheck.changeDbToSbAscii(kaisiNo4).toUpperCase();
		kaisiNo5 = StringCheck.changeDbToSbAscii(kaisiNo5).toUpperCase();
		//�I���ԍ�
		syuuryouNo1 = StringCheck.changeDbToSbAscii(syuuryouNo1).toUpperCase();
		syuuryouNo2 = StringCheck.changeDbToSbAscii(syuuryouNo2).toUpperCase();
		syuuryouNo3 = StringCheck.changeDbToSbAscii(syuuryouNo3).toUpperCase();
		syuuryouNo4 = StringCheck.changeDbToSbAscii(syuuryouNo4).toUpperCase();
		syuuryouNo5 = StringCheck.changeDbToSbAscii(syuuryouNo5).toUpperCase();
		// �ԍ����n�C�t�������ɐ��`����̂́A�}�ʓo�^�˗��A�}�ʏo�͎w���̂Ƃ��̂݁B
		// �ύX '04/07/22 by Hirata.
		if("�}�ʓo�^�˗�".equals(irai) || "�}�ʏo�͎w��".equals(irai)){			
			// �J�n�ԍ�
			kaisiNo1 = changeZubanNoHyphen(kaisiNo1);
			kaisiNo2 = changeZubanNoHyphen(kaisiNo2);
			kaisiNo3 = changeZubanNoHyphen(kaisiNo3);
			kaisiNo4 = changeZubanNoHyphen(kaisiNo4);
			kaisiNo5 = changeZubanNoHyphen(kaisiNo5);
			// �I���ԍ�
			syuuryouNo1 = changeZubanNoHyphen(syuuryouNo1);
			syuuryouNo2 = changeZubanNoHyphen(syuuryouNo2);
			syuuryouNo3 = changeZubanNoHyphen(syuuryouNo3);
			syuuryouNo4 = changeZubanNoHyphen(syuuryouNo4);
			syuuryouNo5 = changeZubanNoHyphen(syuuryouNo5);
		}
		//����
		busuu1 = StringCheck.changeDbToSbAscii(busuu1).toUpperCase();
		busuu2 = StringCheck.changeDbToSbAscii(busuu2).toUpperCase();
		busuu3 = StringCheck.changeDbToSbAscii(busuu3).toUpperCase();
		busuu4 = StringCheck.changeDbToSbAscii(busuu4).toUpperCase();
		busuu5 = StringCheck.changeDbToSbAscii(busuu5).toUpperCase();
	}
	/**
	 * �w�肳�ꂽ�}�Ԃ��n�C�t�������ɂ��ĕԂ��B
	 * @param src
	 * @return
	 */
	private String changeZubanNoHyphen(String src){
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(src, "-");
		while(st.hasMoreElements()){
			sb.append(st.nextElement());
		}
		return sb.toString();
	}
	/**
	 * �`�F�b�N���[�`���p��1�s�ڃf�[�^��Ԃ�
	 * @return
	 */
	public RequestFormLineData getLineData1(){
		return new RequestFormLineData(gouki1, genzu1, kaisiNo1, syuuryouNo1,
								busuu1, syukusyou1, size1);
	}
	/**
	 * �`�F�b�N���[�`���p��2�s�ڃf�[�^��Ԃ�
	 * @return
	 */
	public RequestFormLineData getLineData2(){
		return new RequestFormLineData(gouki2, genzu2, kaisiNo2, syuuryouNo2,
								busuu2, syukusyou2, size2);
	}
	/**
	 * �`�F�b�N���[�`���p��3�s�ڃf�[�^��Ԃ�
	 * @return
	 */
	public RequestFormLineData getLineData3(){
		return new RequestFormLineData(gouki3, genzu3, kaisiNo3, syuuryouNo3,
								busuu3, syukusyou3, size3);
	}
	/**
	 * �`�F�b�N���[�`���p��4�s�ڃf�[�^��Ԃ�
	 * @return
	 */
	public RequestFormLineData getLineData4(){
		return new RequestFormLineData(gouki4, genzu4, kaisiNo4, syuuryouNo4,
								busuu4, syukusyou4, size4);
	}
	/**
	 * �`�F�b�N���[�`���p��5�s�ڃf�[�^��Ԃ�
	 * @return
	 */
	public RequestFormLineData getLineData5(){
		return new RequestFormLineData(gouki5, genzu5, kaisiNo5, syuuryouNo5,
								busuu5, syukusyou5, size5);
	}
	
	/**
	 * ActionForm#reset�̃I�[�o�[���C�h
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		gouki1="";
		gouki2="";
		gouki3="";
		gouki4="";
		gouki5="";
		kaisiNo1="";
		kaisiNo2="";
		kaisiNo3="";
		kaisiNo4="";
		kaisiNo5="";
		syuuryouNo1="";
		syuuryouNo2="";
		syuuryouNo3="";
		syuuryouNo4="";
		syuuryouNo5="";
		busuu1="";
		busuu2="";
		busuu3="";
		busuu4="";
		busuu5="";
		action = "";
		genzu1 = "";
		genzu2 = "";
		genzu3 = "";
		genzu4 = "";
		genzu5 = "";
		irai = "";
		hiddenNo1 = "";
		hiddenNo2 = "";
		hiddenNo3 = "";
		hiddenNo4 = "";
		hiddenNo5 = "";
		syukusyou1 = "";
		syukusyou2 = "";
		syukusyou3 = "";
		syukusyou4 = "";
		syukusyou5 = "";
		size1 = "";
		size2 = "";
		size3 = "";
		size4 = "";
		size5 = "";
	}

	//---------------------------------------------------------- getter,setter
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public ArrayList getGenzuNameList() {
		return genzuNameList;
	}
	public ArrayList getSyukusyouList() {
		return syukusyouList;
	}
	public ArrayList getSaizuList() {
		return saizuList;
	}
	public void setGenzuNameList(ArrayList genzuNameList) {
		this.genzuNameList = genzuNameList;
	}
	public ArrayList getSyutuList() {
		return syutuList;
	}
	public void setSyutuList(ArrayList syutuList) {
		this.syutuList = syutuList;
	}
	public void setSyukusyouList(ArrayList syukusyouList) {
		this.syukusyouList = syukusyouList;
	}
	public void setSaizuList(ArrayList saizuList) {
		this.saizuList = saizuList;
	}	
	public String getIrai(){
		return irai;
	}
	public void setIrai(String irai){
		this.irai = StringCheck.latinToUtf8(irai);
	}	
	public String getSyutu(){
		return syutu;
	}
	public void setSyutu(String syutu){
		this.syutu = StringCheck.latinToUtf8(syutu);
	}
	public String getHiddenSyutu(){
		return hiddenSyutu;
	}
	public void setHiddenSyutu(String hiddenSyutu){
		this.hiddenSyutu = StringCheck.latinToUtf8(hiddenSyutu);
	}
	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action = action;
	}
	public String getGenzu1(){
		return genzu1;
	}
	public void setGenzu1(String genzu1){
		this.genzu1 = StringCheck.latinToUtf8(genzu1);
	}
	public String getGenzu2(){
		return genzu2;
	}
	public void setGenzu2(String genzu2){
		this.genzu2 = StringCheck.latinToUtf8(genzu2);
	}
	public String getGenzu3(){
		return genzu3;
	}
	public void setGenzu3(String genzu3){
		this.genzu3 = StringCheck.latinToUtf8(genzu3);
	}	
	public String getGenzu4(){
		return genzu4;
	}
	public void setGenzu4(String genzu4){
		this.genzu4 = StringCheck.latinToUtf8(genzu4);
	}
	public String getGenzu5(){
		return genzu5;
	}
	public void setGenzu5(String genzu5){
		this.genzu5 = StringCheck.latinToUtf8(genzu5);
	}
	public String getGouki1(){
		return gouki1;
	}
	public void setGouki1(String gouki1){
		this.gouki1 = StringCheck.latinToUtf8(gouki1);
	}
	public String getGouki2(){
		return gouki2;
	}
	public void setGouki2(String gouki2){
		this.gouki2 = StringCheck.latinToUtf8(gouki2);
	}
	public String getGouki3(){
		return gouki3;
	}
	public void setGouki3(String gouki3){
		this.gouki3 = StringCheck.latinToUtf8(gouki3);
	}
	public String getGouki4(){
		return gouki4;
	}
	public void setGouki4(String gouki4){
		this.gouki4 = StringCheck.latinToUtf8(gouki4);
	}
	public String getGouki5(){
		return gouki5;
	}
	public void setGouki5(String gouki5){
		this.gouki5 = StringCheck.latinToUtf8(gouki5);
	}
	public String getKaisiNo1(){
		return kaisiNo1;
	}
	public void setKaisiNo1(String kaisiNo1){
		this.kaisiNo1 = StringCheck.latinToUtf8(kaisiNo1);
	}
	public String getKaisiNo2(){
		return kaisiNo2;
	}
	public void setKaisiNo2(String kaisiNo2){
		this.kaisiNo2 = StringCheck.latinToUtf8(kaisiNo2);
	}
	public String getKaisiNo3(){
		return kaisiNo3;
	}
	public void setKaisiNo3(String kaisiNo3){
		this.kaisiNo3 = StringCheck.latinToUtf8(kaisiNo3);
	}
	public String getKaisiNo4(){
		return kaisiNo4;
	}
	public void setKaisiNo4(String kaisiNo4){
		this.kaisiNo4 = StringCheck.latinToUtf8(kaisiNo4);
	}
	public String getKaisiNo5(){
		return kaisiNo5;
	}
	public void setKaisiNo5(String kaisiNo5){
		this.kaisiNo5 = StringCheck.latinToUtf8(kaisiNo5);
	}
	public String getSyuuryouNo1(){
		return syuuryouNo1;
	}
	public void setSyuuryouNo1(String syuuryouNo1){
		this.syuuryouNo1 = StringCheck.latinToUtf8(syuuryouNo1);
	}
	public String getSyuuryouNo2(){
		return syuuryouNo2;
	}
	public void setSyuuryouNo2(String syuuryouNo2){
		this.syuuryouNo2 = StringCheck.latinToUtf8(syuuryouNo2);
	}
	public String getSyuuryouNo3(){
		return syuuryouNo3;
	}
	public void setSyuuryouNo3(String syuuryouNo3){
		this.syuuryouNo3 = StringCheck.latinToUtf8(syuuryouNo3);
	}
	public String getSyuuryouNo4(){
		return syuuryouNo4;
	}
	public void setSyuuryouNo4(String syuuryouNo4){
		this.syuuryouNo4 = StringCheck.latinToUtf8(syuuryouNo4);
	}
	public String getSyuuryouNo5(){
		return syuuryouNo5;
	}
	public void setSyuuryouNo5(String syuuryouNo5){
		this.syuuryouNo5 = StringCheck.latinToUtf8(syuuryouNo5);
	}
	public String getBusuu1(){
		return busuu1;
	}
	public void setBusuu1(String busuu1){
		this.busuu1 = StringCheck.latinToUtf8(busuu1);
	}
	public String getBusuu2(){
		return busuu2;
	}
	public void setBusuu2(String busuu2){
		this.busuu2 = StringCheck.latinToUtf8(busuu2);
	}
	public String getBusuu3(){
		return busuu3;
	}
	public void setBusuu3(String busuu3){
		this.busuu3 = StringCheck.latinToUtf8(busuu3);
	}
	public String getBusuu4(){
		return busuu4;
	}
	public void setBusuu4(String busuu4){
		this.busuu4 = StringCheck.latinToUtf8(busuu4);
	}
	public String getBusuu5(){
		return busuu5;
	}
	public void setBusuu5(String busuu5){
		this.busuu5 = StringCheck.latinToUtf8(busuu5);
	}
	public String getSyukusyou1(){
		return syukusyou1;
	}
	public void setSyukusyou1(String syukusyou1){
		this.syukusyou1 = syukusyou1;
	}
	public String getSyukusyou2(){
		return syukusyou2;
	}
	public void setSyukusyou2(String syukusyou2){
		this.syukusyou2 = syukusyou2;
	}
	public String getSyukusyou3(){
		return syukusyou3;
	}
	public void setSyukusyou3(String syukusyou3){
		this.syukusyou3 = syukusyou3;
	}
	public String getSyukusyou4(){
		return syukusyou4;
	}
	public void setSyukusyou4(String syukusyou4){
		this.syukusyou4 = syukusyou4;
	}
	public String getSyukusyou5(){
		return syukusyou5;
	}
	public void setSyukusyou5(String syukusyou5){
		this.syukusyou5 = syukusyou5;
	}
	public String getSize1(){
		return size1;
	}
	public void setSize1(String size1){
		this.size1 = size1;
	}
	public String getSize2(){
		return size2;
	}
	public void setSize2(String size2){
		this.size2 = size2;
	}
	public String getSize3(){
		return size3;
	}
	public void setSize3(String size3){
		this.size3 = size3;
	}
	public String getSize4(){
		return size4;
	}
	public void setSize4(String size4){
		this.size4 = size4;
	}
	public String getSize5(){
		return size5;
	}
	public void setSize5(String size5){
		this.size5 = size5;
	}
	public String getHiddenNo1(){
		return hiddenNo1;
	}
	public void setHiddenNo1(String hiddenNo1){
		this.hiddenNo1 = StringCheck.latinToUtf8(hiddenNo1);
	}
	public String getHiddenNo2(){
		return hiddenNo2;
	}
	public void setHiddenNo2(String hiddenNo2){
		this.hiddenNo2 = StringCheck.latinToUtf8(hiddenNo2);
	}
	public String getHiddenNo3(){
		return hiddenNo3;
	}
	public void setHiddenNo3(String hiddenNo3){
		this.hiddenNo3 = StringCheck.latinToUtf8(hiddenNo3);
	}
	public String getHiddenNo4(){
		return hiddenNo4;
	}
	public void setHiddenNo4(String hiddenNo4){
		this.hiddenNo4 = StringCheck.latinToUtf8(hiddenNo4);
	}
	public String getHiddenNo5(){
		return hiddenNo5;
	}
	public void setHiddenNo5(String hiddenNo5){
		this.hiddenNo5 = StringCheck.latinToUtf8(hiddenNo5);
	}
	public String getZumenAll(){
		return zumenAll;
	}
	public void setZumenAll(String zumenAll){
		this.zumenAll = zumenAll;
	}
	public String getPrinter_flag(){
		return printer_flag;
	}
	public void setPrinter_flag(String printer_flag){
		this.printer_flag = printer_flag;
	}
	public String getHani_s(){
		return hani_s;
	}
	public void setHani_s(String hani_s){
		this.hani_s = hani_s;
	}
	public String getHani_e(){
		return hani_e;
	}
	public void setHani_e(String hani_e){
		this.hani_e = hani_e;
	}
	
	public String getErrlog(){
		return errlog;
	}
	public void setErrlog(String errlog){
		this.errlog = errlog;
	}
	public String getErrNumber(){
		return errNumber;
	}
	public void setErrNumber(String errNumber){
		this.errNumber = errNumber;
	}
	public String getHani_t(){
		return hani_t;
	}
	public void setHani_t(String hani_t){
		this.hani_t = hani_t;
	}
	public String getHani_sitei(){
		return hani_sitei;
	}
	public void setHani_sitei(String hani_sitei){
		this.hani_sitei = hani_sitei;
	}
	/**
	 * @return
	 */
	public ArrayList getIraiList() {
		return iraiList;
	}

}
