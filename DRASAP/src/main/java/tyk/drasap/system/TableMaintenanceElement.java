package tyk.drasap.system;

/**
 * �e�e�[�u���̓��e��ێ�����B
 * �e�[�u�������e�i���X�p�B
 */
public class TableMaintenanceElement {
	String column_name = "";// ���ږ�
	String data_type = "";// ���ڃ^�C�v
	int data_length;// �T�C�Y
	boolean keyFlg = false;
	String nullable = "Y";

	// ------------------------------------------------------- �R���X�g���N�^
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getColumn_name() {
		return column_name;
	}

	/**
	 * @return
	 */
	public void setColumn_name(String val) {
		column_name = val;
	}

	/**
	 * @return
	 */
	public String getData_type() {
		return data_type;
	}

	/**
	 * @return
	 */
	public void setData_type(String val) {
		data_type = val;
	}

	/**
	 * @return
	 */
	public int getData_length() {
		return data_length;
	}

	/**
	 * @return
	 */
	public void setdata_length(int val) {
		data_length = val;
	}

	/**
	 * @return
	 */
	public String getKey() {
		if (keyFlg) {
			return "1";
		}
		return "0";
	}

	/**
	 * @return
	 */
	public boolean isKey() {
		return keyFlg;
	}

	/**
	 * @return
	 */
	public void setKey(boolean flg) {
		keyFlg = flg;
	}

	/**
	 * @return
	 */
	public void setKey(String val) {
		if ("1".equals(val)) {
			keyFlg = true;
		} else {
			keyFlg = false;
		}
	}

	/**
	 * @return
	 */
	public String getNullable() {
		return nullable;
	}

	/**
	 * @return
	 */
	public void setNullable(String val) {
		nullable = val;
	}
}
