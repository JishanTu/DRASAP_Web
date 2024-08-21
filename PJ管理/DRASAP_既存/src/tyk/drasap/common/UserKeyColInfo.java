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
	    tabl_name = value;
		return;
	}
	/**
	 * @return
	 */
	public String getTableName() {
		return tabl_name;
	}

	/**
	 * @return
	 */
	public int getNoKeyCol() {
	    if (keyCol.isEmpty()) {
	        return 0;
	    } else {
	        return keyCol.size();
	    }
	}
	public String getkeyCol(int idx) {
		return (String)keyCol.get(idx);
	}
	public void addKeyCol(String keyColName) {
		this.keyCol.add(keyColName);
	}
}
