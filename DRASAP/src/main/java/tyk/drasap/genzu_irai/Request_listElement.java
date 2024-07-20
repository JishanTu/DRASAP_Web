package tyk.drasap.genzu_irai;

import tyk.drasap.common.StringCheck;

/**
 * ���}�ɍ�ƈ˗����X�g�̕\�����e�B
 * @author KAWAI
 */
public class Request_listElement {
	
	String seq;//�V�[�P���X�ԍ�
	String job_id;//�˗�ID
	String rowNo;//�s�ԍ�
	String zuban;//�}��
	String touroku;//�o�^�L��
	String message;//���b�Z�[�W
	String zikan;//����
	String irai;//�˗����e
	String sagyou;//��ƃX�e�[�^�X
	String gouki;//�����E���@
	String genzu;//���}���e
	String kaisi;//�J�n�ԍ�
	String owari;//�I���ԍ�
	String busuu;//����
	String syukusyou;//�k��
	String size;//�T�C�Y
	String iraisya;
	String busyo;
	String key;
	String iraikubun;
	String stamp_flag;
	String printer_id;
	String zyoutai;
	String job_date;
	String output_stat;
	String output_date;
	String user_id;//���[�UID
	String user_name;//���[�U��
	String busyo_name;//������
	String hiddenMessage;//���b�Z�[�W��hidden
	String hiddenTouroku;//�o�^�L����hidden	
	
	
	public Request_listElement(String newSeq, String newJob_id, String newZikan, String newIrai, String newZuban,
								String newGouki, String newGenzu, String newBusuu, String newSyukusyou, String newSize,	 
									String newUser_name, String newBusyo_name, String newMessage, String newRowNo,
									String newTouroku, String newHiddenMessage, String newHiddenTouroku){
										
		this.seq = newSeq;	
		this.job_id = newJob_id;
		this.zikan = newZikan;
		this.irai = newIrai;									
		this.zuban = newZuban;
		this.gouki = newGouki;
		this.genzu = newGenzu;
		this.busuu = newBusuu;
		this.syukusyou = newSyukusyou;
		this.size = newSize;
		this.user_name = newUser_name;
		this.busyo_name = newBusyo_name;
		this.message = newMessage;
		this.rowNo = newRowNo;
		this.touroku = newTouroku;
		this.hiddenMessage = newHiddenMessage;
		this.hiddenTouroku = newHiddenTouroku;

	}									
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
		
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	
	public String getRowNo() {
		return rowNo;
	}

	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	
	public String getZuban() {
		return zuban;
	}

	public void setZuban(String zuban) {
		this.zuban = zuban;
	}
	
	public String getTouroku() {
		return touroku;
	}

	public void setTouroku(String touroku) {
		this.touroku = touroku;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		// UTF-8�ɕύX
		this.message = StringCheck.latinToUtf8(message);
	}
	
	public String getZyoutai() {
		return zyoutai;
	}

	public void setZyoutai(String zyoutai) {
		this.zyoutai = zyoutai;
	}
	
	public String getZikan() {
		return zikan;
	}

	public void setZikan(String zikan) {
		this.zikan = zikan;
	}
	
	public String getIrai() {
		return irai;
	}

	public void setIrai(String irai) {
		this.irai = irai;
	}
	
	public String getSagyou() {
		return sagyou;
	}

	public void setSagyou(String sagyou) {
		this.sagyou = sagyou;
	}
	
	public String getGouki() {
		return gouki;
	}

	public void setGouki(String gouki) {
		this.gouki = gouki;
	}
	
	public String getGenzu() {
		return genzu;
	}

	public void setGenzu(String genzu) {
		this.genzu = genzu;
	}
	
	public String getKaisi() {
		return kaisi;
	}

	public void setKaisi(String kaisi) {
		this.kaisi = kaisi;
	}
	
	public String getOwari() {
		return owari;
	}

	public void setOwari(String owari) {
		this.owari = owari;
	}
		
	public String getBusuu() {
		return busuu;
	}

	public void setBusuu(String busuu) {
		this.busuu = busuu;
	}
	
	public String getSyukusyou() {
		return syukusyou;
	}

	public void setSyukusyou(String syukusyou) {
		this.syukusyou = syukusyou;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getIraisya() {
		return iraisya;
	}

	public void setIraisya(String iraisya) {
		this.iraisya = iraisya;
	}
	
	public String getBusyo() {
		return busyo;
	}

	public void setBusyo(String busyo) {
		this.busyo = busyo;
	}
	
	public String getIraikubun() {
		return iraikubun;
	}

	public void setIraikubun(String iraikubun) {
		this.iraikubun = iraikubun;
	}
	
	public String getStamp_flag() {
		return stamp_flag;
	}

	public void setStamp_flag(String stamp_flag) {
		this.stamp_flag = stamp_flag;
	}
	
	public String getPrinter_id() {
		return printer_id;
	}

	public void setPrinter_id(String printer_id) {
		this.printer_id = printer_id;
	}
	
	public String getJob_date() {
		return job_date;
	}

	public void setJob_date(String job_date) {
		this.job_date = job_date;
	}
	
	public String getOutput_stat() {
		return output_stat;
	}

	public void setOutput_stat(String output_stat) {
		this.output_stat = output_stat;
	}
		
	public String getOutput_date() {
		return output_date;
	}

	public void setOutput_date(String output_date) {
		this.output_date = output_date;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getBusyo_name() {
		return busyo_name;
	}

	public void setBusyo_name(String busyo_name) {
		this.busyo_name = busyo_name;
	}
	
	public String getHiddenMessage() {
		return hiddenMessage;
	}

	public void setHiddenMessage(String hiddenMessage) {
		this.hiddenMessage = hiddenMessage;
	}
	
	public String getHiddenTouroku() {
		return hiddenTouroku;
	}

	public void setHiddenTouroku(String hiddenTouroku) {
		this.hiddenTouroku = hiddenTouroku;
	}
}
