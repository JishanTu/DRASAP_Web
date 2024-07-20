package tyk.drasap.system;

import tyk.drasap.common.StringCheck;

/**
 * 管理者設定情報を表す。
 * 
 */
public class AdminSettingListElement {
    String settingId = "";	// 設定項目ＩＤ
    String itemName = "";	// 設定項目日本語名
	String val = "";		// 設定値
	String status = "";		// 状態
	String modifiedDate;	// 更新日
	boolean updateFlg = false;
	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getSettingId() {
		return settingId;
	}
	/**
	 * @return
	 */
	public void setSettingId(String val) {
	    settingId = val;
		return;
	}
	/**
	 * @return
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @return
	 */
	public void setItemName(String val) {
	    itemName = val;
		return;
	}

	/**
	 * @return
	 */
	public String getVal() {
		return val;
	}
	/**
	 * @return
	 */
	public void setVal(String val) {
	    this.val = StringCheck.latinToUtf8(val);
		return;
	}

	/**
	 * @return
	 */
	public String getStatus() {
        return status;
	}
	/**
	 * @return
	 */
	public void setStatus(String val) {
	    status = val;
		return;
	}
	/**
	 * @return
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param 
	 */
	public void setModifiedDate(String val) {
	    modifiedDate = val;
	}
	/**
	 * @return
	 */
	public boolean isUpdate() {
	    return updateFlg;
	}
	/**
	 * @param 
	 */
	public void setUpdate(boolean flg) {
	    this.updateFlg = flg;
	}
}
