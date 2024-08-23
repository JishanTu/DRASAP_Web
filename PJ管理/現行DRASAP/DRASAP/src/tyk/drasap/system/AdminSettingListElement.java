package tyk.drasap.system;

import tyk.drasap.common.StringCheck;

/**
 * �Ǘ��Ґݒ����\���B
 * 
 */
public class AdminSettingListElement {
    String settingId = "";	// �ݒ荀�ڂh�c
    String itemName = "";	// �ݒ荀�ړ��{�ꖼ
	String val = "";		// �ݒ�l
	String status = "";		// ���
	String modifiedDate;	// �X�V��
	boolean updateFlg = false;
	// ------------------------------------------------------- �R���X�g���N�^
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
