package tyk.drasap.genzu_irai;

/**
 * 原図庫作業依頼履歴で表示する1件のデータに対応
 * @author fumi
 */
public class RequestHistoryElement implements Comparable {
	private String completeDate;// 作業完了日時
	private String completeUser;// 作業完了者名
	private String requestDate;// 依頼日時
	private String jobName;// ジョブ名
	private String drwgNo;// 図番
	private String goukiNo;// 号機
	private String genzuContent;// 原図内容
	private String copies;// 部数
	private String scaleMode;// 縮小区分
	private String scaleSize;// 縮小サイズ
	private String message;// メッセージ
	private String requestUser;// 依頼者名
	private String deptName;// 依頼部署
	
	/**
	 * コンストラクター
	 */
	public RequestHistoryElement(String newCompleteDate, String newCompleteUser,
					String newRequestDate, String newJobName, String newDrwgNo, String newGoukiNo,
					String newGenzuContent, String newCopies, String newScaleMode, String newScaleSize,
					String newMessage, String newRequestUser, String newDeptName) {
		this.completeDate = newCompleteDate;// 作業完了日時
		if(completeDate == null) completeDate="";
		this.completeUser = newCompleteUser;// 作業完了者名
		if(completeUser == null) completeUser="";
		this.requestDate = newRequestDate;// 依頼日時
		this.jobName = newJobName;// ジョブ名
		if(jobName == null) jobName="";
		this.drwgNo = newDrwgNo;// 図番
		this.goukiNo = newGoukiNo;// 号機
		if(goukiNo == null) goukiNo="";
		this.genzuContent = newGenzuContent;// 原図内容
		if(genzuContent == null) genzuContent="";
		this.copies = newCopies;// 部数
		if(copies == null) copies="";
		this.scaleMode = newScaleMode;// 縮小区分
		if(scaleMode == null) scaleMode="";
		this.scaleSize = newScaleSize;// 縮小サイズ
		if(scaleSize == null) scaleSize="";
		this.message = newMessage;// メッセージ
		if(message == null) message="";
		this.requestUser = newRequestUser;// 依頼者名
		this.deptName = newDeptName;// 依頼部署
	}
	
	/**
	 * Collections#sort()を使用するために、実装する。
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(! (o instanceof RequestHistoryElement)){
			// 比較しようとした対象が RequestHistoryElement でない。
			throw new ClassCastException("RequestHistoryElementにキャストできません。");
		}
		RequestHistoryElement target = (RequestHistoryElement) o;
		// 比較は「作業完了日時」で
		return this.completeDate.compareTo(target.getCompleteDate());
	}

	// -------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getCompleteDate() {
		return completeDate;
	}

	/**
	 * @return
	 */
	public String getCompleteUser() {
		return completeUser;
	}

	/**
	 * @return
	 */
	public String getCopies() {
		return copies;
	}

	/**
	 * @return
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @return
	 */
	public String getDrwgNo() {
		return drwgNo;
	}

	/**
	 * @return
	 */
	public String getGenzuContent() {
		return genzuContent;
	}

	/**
	 * @return
	 */
	public String getGoukiNo() {
		return goukiNo;
	}

	/**
	 * @return
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return
	 */
	public String getRequestDate() {
		return requestDate;
	}

	/**
	 * @return
	 */
	public String getRequestUser() {
		return requestUser;
	}

	/**
	 * @return
	 */
	public String getScaleMode() {
		return scaleMode;
	}

	/**
	 * @return
	 */
	public String getScaleSize() {
		return scaleSize;
	}

}
