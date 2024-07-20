package tyk.drasap.genzu_irai;


/**
 * @author KAWAI
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class Request_RefElement {
	
	String job_id;//�˗�ID
	String job_stat;//��ƃX�e�[�^�X
	String job_name;//�˗����e
	String gouki;//�����E���@
	String genzu;//���}���e
	String start;//�J�n�ԍ�
	String end;//�I���ԍ�
	String busuu;//����
	String syuku;//�k��
	String size;//�T�C�Y
	String printer;//�o�͐�
	String messege;//���b�Z�[�W(�}�ʓo�^�˗��A�}�ʏo�͎w��)
	String messege1;//���b�Z�[�W(���}�ɍ�ƈ˗��A�}�ʈȊO�ĕt�˗�)
	String exist;//�o�^�L��
	String seq;//�V�[�P���X�ԍ�
	String rowNo;//�s�ԍ�
	
	public Request_RefElement(String newJob_id, String newJob_stat, String newJob_name, String newGouki, String newGenzu, 
								String newStart, String newEnd, String newBusuu, String newSyuku, String newSize, String newPrinter,
								String newMessege, String newMessege1, String newExist, String newSeq, String newRowNo){
											
		this.job_id = newJob_id;
		this.job_stat = newJob_stat;
		this.job_name = newJob_name;
		this.gouki = newGouki;
		this.genzu = newGenzu;
		this.start = newStart;
		this.end = newEnd;
		this.busuu = newBusuu;
		this.syuku = newSyuku;
		this.size = newSize;
		this.printer = newPrinter;
		this.messege = newMessege;
		this.messege1 = newMessege1;
		this.exist = newExist;
		this.seq = newSeq;
		this.rowNo = newRowNo;
	}
	
	public Request_RefElement(String newJob_name, String newGouki, String newGenzu, String newStart, String newMessage, String newExist){
		this.job_name = newJob_name;
		this.gouki = newGouki;
		this.genzu = newGenzu;
		this.start = newStart;
		this.messege = newMessage;
		this.exist = newExist;
		
	}
	
	public String getJob_id() {
		return job_id;
	}
	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}									
	public String getJob_stat() {
		return job_stat;
	}
	public void setJob_stat(String job_stat) {
		this.job_stat = job_stat;
	}
	public String getJob_name() {
		return job_name;
	}
	public void setJob_name(String job_name) {
		this.job_name = job_name;
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
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getBusuu() {
		return busuu;
	}
	public void setBusuu(String busuu) {
		this.busuu = busuu;
	}
	public String getSyuku() {
		return syuku;
	}
	public void setSyuku(String syuku) {
		this.syuku = syuku;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getPrinter() {
		return printer;
	}
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	public String getMessege() {
		return messege;
	}
	public void setMessege(String messege) {
		this.messege = messege;
	}
	public String getMessege1() {
		return messege1;
	}
	public void setMessege1(String messege1) {
		this.messege1 = messege1;
	}
	public String getExist() {
		return exist;
	}
	public void setExist(String exist) {
		this.exist = exist;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getRowNo() {
		return rowNo;
	}
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	

}
