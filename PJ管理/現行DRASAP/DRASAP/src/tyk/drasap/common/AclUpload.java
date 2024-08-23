package tyk.drasap.common;

import java.util.Date;


/**
 * ACL一括更新画面用のアップロードデータを表すクラス。
 * ACLアップロードデータテーブル(ACL_UPLOAD_TABLE)に対応。
 *
 * @author 2013/07/09 yamagishi
 */
public class AclUpload {

	String aclUpdateNo = "";		// 管理NO
	String userId = "";				// ユーザID
	String userName = "";			// 氏名
	String recordNo = "";			// レコード番号
	String machineJp = "";			// 装置
	String machineNo = "";			// 装置NO
	String drwgNo = "";				// 手配図番
	String machineCode = "";		// 装置コード
	String detailNo = "";			// 明細番号
	String pages = "";				// 頁
	String itemNo = "";				// 品番
	String itemNoShort = "";		// 品番（空白、ハイフン「-」を除いた半角大文字）
	String grpCode = "";			// グループ
	String correspondingFlag = "";	// 該当図区分
	String correspondingValue = "";	// 該当図区分（表示）値
	String confidentialFlag = "";	// 機密管理図区分
	String confidentialValue = "";	// 機密管理図区分（表示）値
	String preUpdateAcl = "";		// 変更前アクセスレベル
	String preUpdateAclName = "";	// 変更前アクセスレベル名
	String postUpdateAcl = "";		// 変更後アクセスレベル
	String postUpdateAclName = "";	// 変更後アクセスレベル名
	String itemName = "";			// 品名（規格型式）
	String message = "";			// メッセージ
	Date aclUpdate = null;			// 更新日時

	// ------------------------------------------ constructor
	/**
	 * constructor
	 */
	public AclUpload() {
	}

	// ------------------------------------------ getter,setter
	/**
	 * aclUpdateNoを取得します。
	 * @return aclUpdateNo
	 */
	public String getAclUpdateNo() {
		return aclUpdateNo;
	}
	/**
	 * aclUpdateNoを設定します。
	 * @param aclUpdateNo aclUpdateNo
	 */
	public void setAclUpdateNo(String aclUpdateNo) {
		this.aclUpdateNo = aclUpdateNo;
	}

	/**
	 * userIdを取得します。
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * userIdを設定します。
	 * @param userId userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * userNameを取得します。
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * userNameを設定します。
	 * @param userName userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * recordNoを取得します。
	 * @return recordNo
	 */
	public String getRecordNo() {
		return recordNo;
	}
	/**
	 * recordNoを設定します。
	 * @param recordNo recordNo
	 */
	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	/**
	 * machineJpを取得します。
	 * @return machineJp
	 */
	public String getMachineJp() {
		return machineJp;
	}
	/**
	 * machineJpを設定します。
	 * @param machineJp machineJp
	 */
	public void setMachineJp(String machineJp) {
		this.machineJp = machineJp;
	}

	/**
	 * machineNoを取得します。
	 * @return machineNo
	 */
	public String getMachineNo() {
		return machineNo;
	}
	/**
	 * machineNoを設定します。
	 * @param machineNo machineNo
	 */
	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	/**
	 * drwgNoを取得します。
	 * @return drwgNo
	 */
	public String getDrwgNo() {
		return drwgNo;
	}
	/**
	 * drwgNoを設定します。
	 * @param drwgNo drwgNo
	 */
	public void setDrwgNo(String drwgNo) {
		this.drwgNo = drwgNo;
	}

	/**
	 * machineCodeを取得します。
	 * @return machineCode
	 */
	public String getMachineCode() {
		return machineCode;
	}
	/**
	 * machineCodeを設定します。
	 * @param machineCode machineCode
	 */
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	/**
	 * detailNoを取得します。
	 * @return detailNo
	 */
	public String getDetailNo() {
		return detailNo;
	}
	/**
	 * detailNoを設定します。
	 * @param detailNo detailNo
	 */
	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	/**
	 * pagesを取得します。
	 * @return pages
	 */
	public String getPages() {
		return pages;
	}
	/**
	 * pagesを設定します。
	 * @param pages pages
	 */
	public void setPages(String pages) {
		this.pages = pages;
	}

	/**
	 * itemNoを取得します。
	 * @return itemNo
	 */
	public String getItemNo() {
		return itemNo;
	}
	/**
	 * itemNoを設定します。
	 * @param itemNo itemNo
	 */
	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	/**
	 * correspondingFlagを取得します。
	 * @return correspondingFlag
	 */
	public String getCorrespondingFlag() {
		return correspondingFlag;
	}
	/**
	 * correspondingFlagを設定します。
	 * @param correspondingFlag correspondingFlag
	 */
	public void setCorrespondingFlag(String correspondingFlag) {
		this.correspondingFlag = correspondingFlag;
	}

	/**
	 * correspondingValueを取得します。
	 * @return correspondingValue
	 */
	public String getCorrespondingValue() {
		return correspondingValue;
	}
	/**
	 * correspondingValueを設定します。
	 * @param correspondingValue correspondingValue
	 */
	public void setCorrespondingValue(String correspondingValue) {
		this.correspondingValue = correspondingValue;
	}

	/**
	 * confidentialFlagを取得します。
	 * @return confidentialFlag
	 */
	public String getConfidentialFlag() {
		return confidentialFlag;
	}
	/**
	 * confidentialFlagを設定します。
	 * @param confidentialFlag confidentialFlag
	 */
	public void setConfidentialFlag(String confidentialFlag) {
		this.confidentialFlag = confidentialFlag;
	}

	/**
	 * confidentialValueを取得します。
	 * @return confidentialValue
	 */
	public String getConfidentialValue() {
		return confidentialValue;
	}
	/**
	 * confidentialValueを設定します。
	 * @param confidentialValue confidentialValue
	 */
	public void setConfidentialValue(String confidentialValue) {
		this.confidentialValue = confidentialValue;
	}

	/**
	 * itemNoShortを取得します。
	 * @return itemNoShort
	 */
	public String getItemNoShort() {
		return itemNoShort;
	}
	/**
	 * itemNoShortを設定します。
	 * @param itemNoShort itemNoShort
	 */
	public void setItemNoShort(String itemNoShort) {
		this.itemNoShort = itemNoShort;
	}

	/**
	 * grpCodeを取得します。
	 * @return grpCode
	 */
	public String getGrpCode() {
		return grpCode;
	}
	/**
	 * grpCodeを設定します。
	 * @param grpCode grpCode
	 */
	public void setGrpCode(String grpCode) {
		this.grpCode = grpCode;
	}

	/**
	 * preUpdateAclを取得します。
	 * @return preUpdateAcl
	 */
	public String getPreUpdateAcl() {
		return preUpdateAcl;
	}
	/**
	 * preUpdateAclを設定します。
	 * @param preUpdateAcl preUpdateAcl
	 */
	public void setPreUpdateAcl(String preUpdateAcl) {
		this.preUpdateAcl = preUpdateAcl;
	}

	/**
	 * preUpdateAclNameを取得します。
	 * @return preUpdateAclName
	 */
	public String getPreUpdateAclName() {
		return preUpdateAclName;
	}
	/**
	 * preUpdateAclNameを設定します。
	 * @param preUpdateAclName preUpdateAclName
	 */
	public void setPreUpdateAclName(String preUpdateAclName) {
		this.preUpdateAclName = preUpdateAclName;
	}

	/**
	 * postUpdateAclを取得します。
	 * @return postUpdateAcl
	 */
	public String getPostUpdateAcl() {
		return postUpdateAcl;
	}
	/**
	 * postUpdateAclを設定します。
	 * @param postUpdateAcl postUpdateAcl
	 */
	public void setPostUpdateAcl(String postUpdateAcl) {
		this.postUpdateAcl = postUpdateAcl;
	}

	/**
	 * postUpdateAclNameを取得します。
	 * @return postUpdateAclName
	 */
	public String getPostUpdateAclName() {
		return postUpdateAclName;
	}
	/**
	 * postUpdateAclNameを設定します。
	 * @param postUpdateAclName postUpdateAclName
	 */
	public void setPostUpdateAclName(String postUpdateAclName) {
		this.postUpdateAclName = postUpdateAclName;
	}

	/**
	 * itemNameを取得します。
	 * @return itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * itemNameを設定します。
	 * @param itemName itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * messageを取得します。
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * messageを設定します。
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * aclUpdateを取得します。
	 * @return aclUpdate
	 */
	public Date getAclUpdate() {
		return aclUpdate;
	}
	/**
	 * aclUpdateを設定します。
	 * @param aclUpdate aclUpdate
	 */
	public void setAclUpdate(Date aclUpdate) {
		this.aclUpdate = aclUpdate;
	}
}
