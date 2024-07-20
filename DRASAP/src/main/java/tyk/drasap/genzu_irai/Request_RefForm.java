package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.StringCheck;

/**
 * @author KAWAI
 *�@���}�Ɉ˗��ڍׂ�Form
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class Request_RefForm extends ActionForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String action;//�A�N�V����
	String job;//�W���uID(���b�Z�[�W����̏ꍇ�����N�Ŏg���f�[�^)
	String job_id;//�W���uID
	String job_name;//�˗����e
	String job_number;//�ԍ�
	String gouki;//�����E���@
	String genzu;//���}���e
	String message;//���b�Z�[�W
	String touroku;//�o�^�L��
		
	ArrayList<String> listErrors = new ArrayList<String>();//�G���[�\��
	ArrayList iraiList = new ArrayList();//���}�˗��ڍׂ̃f�[�^
	ArrayList<Request_RefElement> irai_List = new ArrayList<Request_RefElement>();//�˗����̓��e���i�[����
	ArrayList iraiend_List = new ArrayList();//�����������e���i�[����
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		action = "";
		job_id = "";
		job_name = "";
		job_number = "";
		gouki = "";
		genzu = "";
		message = "";
		touroku = "";
	}
	
	public ArrayList getListErrors() {
		return listErrors;
	}

	public void setListErrors(ArrayList listErrors) {
		this.listErrors = listErrors;
	}
	public ArrayList getIraiList() {
		return iraiList;
	}

	public void setIraiList(ArrayList iraiList) {
		this.iraiList = iraiList;
	}
	
	public ArrayList getIrai_List() {
		return irai_List;
	}

	public void setIrai_List(ArrayList<Request_RefElement> irai_List) {
		this.irai_List = irai_List;
	}
	
	public ArrayList getIraiend_List() {
		return iraiend_List;
	}

	public void setIraiend_List(ArrayList iraiend_List) {
		this.iraiend_List = iraiend_List;
	}	

	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action = action;
	}
	
	public String getJob_id(){
		return job_id;
	}
	public void setJob_id(String job_id){
		this.job_id = job_id;
	}
	
	public String getJob(){
		return job;
	}
	public void setJob(String job){
		// UTF-8�ɕύX
		this.job = StringCheck.latinToUtf8(job);
	}
	
	public String getJob_name(){
		return job_name;
	}
	public void setJob_name(String job_name){
		// UTF-8�ɕύX
		this.job_name = StringCheck.latinToUtf8(job_name);
	}
	
	public String getJob_number(){
		return job_number;
	}
	public void setJob_number(String job_number){
		this.job_number = job_number;
	}
	
	public String getGouki(){
		return gouki;
	}
	public void setGouki(String gouki){
		this.gouki = gouki;
	}
	
	public String getGenzu(){
		return genzu;
	}
	public void setGenzu(String genzu){
		this.genzu = genzu;
	}
	
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getTouroku(){
		return touroku;
	}
	public void setTouroku(String touroku){
		this.touroku = touroku;
	}

}
