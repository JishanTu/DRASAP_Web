package tyk.drasap.system;

import java.util.ArrayList;

/**
 * �P���R�[�h�̓��e��ێ�����B
 * �e�[�u�������e�i���X�p�B
 */
public class TableMaintenanceRec {
	String rec_no = "0";//
	ArrayList<TableMaintenanceVal> valList = new ArrayList<TableMaintenanceVal>();// TableMaintenanceVal
	boolean checkFlg = false;
	boolean newFlg = false;

	// ------------------------------------------------------- �R���X�g���N�^
	// ------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getRec_no() {
		return rec_no;
	}

	/**
	 * @return
	 */
	public void setRec_no(String val) {
		rec_no = val;
	}

	/**
	 * @return
	 */
	public TableMaintenanceVal getValList(int index) {
		return valList.get(index);
	}

	/**
	 * @return
	 */
	public ArrayList<TableMaintenanceVal> getValList() {
		return valList;
	}

	/**
	 * @param list
	 */
	public void setValList(ArrayList<TableMaintenanceVal> list) {
		valList = list;
	}

	/**
	 * @return
	 */
	public boolean isCheck() {
		return checkFlg;
	}

	/**
	 * @param
	 */
	public void setCheck(boolean flg) {
		checkFlg = flg;
	}

	/**
	 * @return
	 */
	public String getNew() {
		if (newFlg) {
			return "1";
		}
		return "0";
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
		newFlg = flg;
	}
}
