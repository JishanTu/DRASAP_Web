package tyk.drasap.system;

import tyk.drasap.common.StringCheck;


/**
 * �e�e�[�u���̓��e��ێ�����B
 * �A�N�Z�X���x���}�X�^�[�����e�i���X�p�B
 */
public class AccessLevelMasterMaintenanceElement {
    String aclId = "";
    String aclIdStyle = "";
    String aclName = "";
    String aclNameStyle = "";
	boolean updateFlg = false;
	boolean newFlg = false;
	// ------------------------------------------------------- �R���X�g���N�^
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
		return;
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
		return;
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
	    this.aclName = StringCheck.latinToUtf8(val);
		return;
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
		return;
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
	/**
	 * @return
	 */
	public boolean isNew() {
	    return newFlg;
	}
	/**
	 * @param 
	 */
	public void setNew(boolean flg) {
	    this.newFlg = flg;
	}

}
