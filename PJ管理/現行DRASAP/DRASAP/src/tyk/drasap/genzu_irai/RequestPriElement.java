/*
 * �쐬��: 2004/01/14
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package tyk.drasap.genzu_irai;

/**
 * @author KAWAI
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class RequestPriElement {

	String job_id;//�˗�ID
	String job_Name;//�˗����e
	String gouki;//�����E���@
	String genzu;//���}���e
	String irai;//�˗���
	String busyo;//������
	String number;//�ԍ�
	
	public RequestPriElement(String newJob_id, String newJob_Name, String newGouki, String newGenzu,
									String newIrai, String newBusyo, String newNumber){
		this.job_id = newJob_id;
		this.job_Name = newJob_Name;	
		this.gouki = newGouki;
		this.genzu = newGenzu;
		this.irai = newIrai;
		this.busyo = newBusyo;
		this.number = newNumber;									
	
	}	
	
	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}							
	
	public String getJob_Name() {
		return job_Name;
	}

	public void setJob_Name(String job_Name) {
		this.job_Name = job_Name;
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
	
	public String getIrai() {
		return irai;
	}

	public void setIrai(String irai) {
		this.irai = irai;
	}
	
	public String getBusyo() {
		return busyo;
	}

	public void setBusyo(String busyo) {
		this.busyo = busyo;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}	
	

}
