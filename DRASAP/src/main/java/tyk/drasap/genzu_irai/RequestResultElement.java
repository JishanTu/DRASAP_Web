/*
 * 作成日: 2004/01/13
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
public class RequestResultElement {
	
	String drwgNo;
	String flag;
	String touroku;

	public RequestResultElement(String newDrwgNo, String newFlag, String newTouroku){
		this.drwgNo = newDrwgNo;
		this.flag = newFlag;
		this.touroku = newTouroku;
	}
	
	/**
	 * Returns the id.
	 * @return String
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getFlag() {
		return flag;
	}
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getTouroku() {
		return touroku;
	}

}
