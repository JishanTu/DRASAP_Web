package tyk.drasap.common;

import java.util.ArrayList;

/**
 * ���[�U�[�e�[�u���̃L�[�J������\���B
 * @author Y.eto
 * �쐬��: 2006/05/10
 */
public class UserKeyColInfo {
	String tabl_name = "";// �e�[�u����
	ArrayList<String> keyCol = new ArrayList<String>();

	// ------------------------------------------------------- �R���X�g���N�^
	/**
	 * �R���X�g���N�^
	 */
	public UserKeyColInfo() {
	}

	// -------------------------------------------------------
	/**
	 * @return
	 */
	public void setTableName(String value) {
		this.tabl_name = value;
	}

	/**
	 * @return
	 */
	public String getTableName() {
		return this.tabl_name;
	}

	/**
	 * @return
	 */
	public int getNoKeyCol() {
		if (this.keyCol.isEmpty()) {
			return 0;
		}
		return this.keyCol.size();
	}

	public String getkeyCol(int idx) {
		return this.keyCol.get(idx);
	}

	public void addKeyCol(String keyColName) {
		this.keyCol.add(keyColName);
	}
}
