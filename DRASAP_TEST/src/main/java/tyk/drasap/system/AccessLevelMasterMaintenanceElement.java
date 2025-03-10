package tyk.drasap.system;

import tyk.drasap.common.StringCheck;

/**
 * 各テーブルの内容を保持する。
 * アクセスレベルマスターメンテナンス用。
 */
public class AccessLevelMasterMaintenanceElement {
	String aclId = "";
	String aclIdStyle = "";
	String aclName = "";
	String aclNameStyle = "";
	boolean updateFlg = false;
	boolean newFlg = false;

	// ------------------------------------------------------- コンストラクタ
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getAclId() {
		return aclId;
	}

	/**
	 * @return
	 */
	public void setAclId(String val) {
		aclId = val;
	}

	/**
	 * @return
	 */
	public String getAclIdStyle() {
		return aclIdStyle;
	}

	/**
	 * @return
	 */
	public void setAclIdStyle(String val) {
		aclIdStyle = val;
	}

	/**
	 * @return
	 */
	public String getAclName() {
		return aclName;
	}

	/**
	 * @return
	 */
	public void setAclName(String val) {
		aclName = StringCheck.latinToUtf8(val);
	}

	/**
	 * @return
	 */
	public String getAclNameStyle() {
		return aclNameStyle;
	}

	/**
	 * @return
	 */
	public void setAclNameStyle(String val) {
		aclNameStyle = val;
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
		updateFlg = flg;
	}

	/**
	 * @return
	 */
	public boolean isNew() {
		return newFlg;
	}

	/**
	 * @param
	 */
	public Boolean getNewFlg() {
		return newFlg;
	}

	/**
	 * @param
	 */
	public void setNew(boolean flg) {
		newFlg = flg;
	}

}
