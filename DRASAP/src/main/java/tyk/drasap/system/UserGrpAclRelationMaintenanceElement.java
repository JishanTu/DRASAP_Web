package tyk.drasap.system;

/**
 * ����Ǘ��}�X�^�[�̓��e��ێ�����B
 * �e�[�u�������e�i���X�p�B
 */
public class UserGrpAclRelationMaintenanceElement {
	String aclId = "";
	String aclName = "";
	String userGrpCode = "";
	String userGrpName = "";
	String aclValue = "";
	boolean updateFlg = false;

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
		aclName = val;
	}

	/**
	 * @return
	 */
	public String getUserGrpCode() {
		return userGrpCode;
	}

	/**
	 * @return
	 */
	public void setUserGrpCode(String val) {
		userGrpCode = val;
	}

	/**
	 * @return
	 */
	public String getuserGrpName() {
		return userGrpName;
	}

	/**
	 * @return
	 */
	public void setUuserGrpName(String val) {
		userGrpName = val;
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

	public String getAclValue() {
		return aclValue;
	}

	public void setAclValue(String aclValue) {
		this.aclValue = aclValue;
	}

	public boolean isUpdateFlg() {
		return updateFlg;
	}

	public void setUpdateFlg(boolean updateFlg) {
		this.updateFlg = updateFlg;
	}

	public String getUserGrpName() {
		return userGrpName;
	}

	public void setUserGrpName(String userGrpName) {
		this.userGrpName = userGrpName;
	}

}
