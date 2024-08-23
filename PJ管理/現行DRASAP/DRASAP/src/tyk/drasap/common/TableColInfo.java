package tyk.drasap.common;

/**
 * �e�[�u���̃J��������\���B
 * 
 * @author Y.eto
 * �쐬��: 2006/05/10
 */
public class TableColInfo {
    String column_name = "";// ���ږ�
	String data_type = "";// ���ڃ^�C�v
	int data_length;// �T�C�Y
	String nullable="Y";
	// ------------------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^
	 */
	public TableColInfo(String newColumn_name, String newData_type, String newData_length, String newNullable) {
		this.column_name = newColumn_name;
		this.data_type = newData_type;
		this.data_length = Integer.valueOf(newData_length).intValue();
		this.nullable = newNullable;
	}
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
	public String getData_type() {
		return data_type;
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
	public String getNullable() {
		return nullable;
	}
}
