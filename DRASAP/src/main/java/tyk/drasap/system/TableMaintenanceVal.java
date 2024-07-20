package tyk.drasap.system;

import tyk.drasap.common.StringCheck;

/**
 * �P���R�[�h�̓��e��ێ�����B
 * �e�[�u�������e�i���X�p�B
 */
public class TableMaintenanceVal {
	String val = "";
	String dispStyle = "";

	// ------------------------------------------------------- �R���X�g���N�^
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param list
	 */
	public void setVal(String val) {
		this.val = StringCheck.latinToUtf8(val);
	}

	/**
	 * @return
	 */
	public String getDispStyle() {
		return dispStyle;
	}

	/**
	 * @return
	 */
	public void setDispStyle(String val) {
		dispStyle = val;
	}
}
