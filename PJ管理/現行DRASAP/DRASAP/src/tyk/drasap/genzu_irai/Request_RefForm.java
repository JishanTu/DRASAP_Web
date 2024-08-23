package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.StringCheck;

/**
 * @author KAWAI
 *　原図庫依頼詳細のForm
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class Request_RefForm extends ActionForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String action;//アクション
	String job;//ジョブID(メッセージありの場合リンクで使うデータ)
	String job_id;//ジョブID
	String job_name;//依頼内容
	String job_number;//番号
	String gouki;//号口・号機
	String genzu;//原図内容
	String message;//メッセージ
	String touroku;//登録有無
		
	ArrayList<String> listErrors = new ArrayList<String>();//エラー表示
	ArrayList iraiList = new ArrayList();//原図依頼詳細のデータ
	ArrayList<Request_RefElement> irai_List = new ArrayList<Request_RefElement>();//依頼中の内容を格納する
	ArrayList iraiend_List = new ArrayList();//完了した内容を格納する
	
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
		// UTF-8に変更
		this.job = StringCheck.latinToUtf8(job);
	}
	
	public String getJob_name(){
		return job_name;
	}
	public void setJob_name(String job_name){
		// UTF-8に変更
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
