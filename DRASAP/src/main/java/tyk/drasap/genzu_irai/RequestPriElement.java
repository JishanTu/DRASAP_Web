/*
 * 作成日: 2004/01/14
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package tyk.drasap.genzu_irai;

/**
 * @author KAWAI
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class RequestPriElement {

	String job_id;//依頼ID
	String job_Name;//依頼内容
	String gouki;//号口・号機
	String genzu;//原図内容
	String irai;//依頼者
	String busyo;//部署名
	String number;//番号
	
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
