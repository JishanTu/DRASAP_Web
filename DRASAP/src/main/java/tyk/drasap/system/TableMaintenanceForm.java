package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import tyk.drasap.common.StringCheck;
import tyk.drasap.common.UserKeyColDB;
import tyk.drasap.springfw.form.BaseForm;

/**
 * メニュー画面に対応
 */
public class TableMaintenanceForm extends BaseForm {
	/**
	 *
	 */
	String act;// 処理を分けるための属性
	String updateIndex;
	String selectTable;// 選択されたテーブル
	String whereStr;// 検索条件
	ArrayList<String> tableList = new ArrayList<String>(); // user table name list
	ArrayList<TableMaintenanceElement> attrList = new ArrayList<TableMaintenanceElement>();// TableMaintenanceElement
	ArrayList<TableMaintenanceRec> recList = new ArrayList<TableMaintenanceRec>();// TableMaintenanceRec
	UserKeyColDB UserKeyColDB = null;
	ArrayList<String> errorMsg = new ArrayList<String>();
	ArrayList<String> pageList = new ArrayList<String>();
	ArrayList<String> pageNameList = new ArrayList<String>();
	long recCount = 0;
	long recNoPerPage = 100;
	long fromRecNo = 0;
	long toRecNo = recNoPerPage;
	String selectPage = "0";
	private MultipartFile fileUp;

	// --------------------------------------------------------- Methods
	public void reset(HttpServletRequest request) {
		for (int i = 0; i < recList.size(); i++) {
			recList.get(i).setCheck(false);
			recList.get(i).setNew(false);
		}
	}

	// --------------------------------------------------------- getter,setter
	public TableMaintenanceForm() {
		act = "";
	}

	public TableMaintenanceForm(ArrayList<TableMaintenanceElement> attr, ArrayList<TableMaintenanceRec> value, ArrayList<String> tableList) {
		attrList = attr;
		recList = value;
		this.tableList = tableList;
	}

	/**
	 * @return
	 */
	public String getAct() {
		return act;
	}

	/**
	 * @param string
	 */
	public void setAct(String string) {
		act = string;
	}

	/**
	 * @return
	 */
	public String getSelectTable() {
		return selectTable;
	}

	/**
	 * @param string
	 */
	public void setSelectTable(String string) {
		selectTable = string;
	}

	/**
	 * @return
	 */
	public String getWhereStr() {
		return whereStr;
	}

	/**
	 * @param string
	 */
	public void setWhereStr(String string) {
		whereStr = StringCheck.latinToUtf8(string);
	}

	/**
	 * @param string
	 */
	public void clearWhereStr() {
		whereStr = null;
	}

	/**
	 * @return
	 */
	public TableMaintenanceElement getAttrList(Integer index) {
		return attrList.get(index.intValue());
	}

	/**
	 * @return
	 */
	public TableMaintenanceElement getAttrList(int index) {
		return attrList.get(index);
	}

	/**
	 * @return
	 */
	public ArrayList<TableMaintenanceElement> getAttrList() {
		return attrList;
	}

	/**
	 * @return
	 */
	public int getColNo(String colName) {
		for (int i = 0; i < attrList.size(); i++) {
			TableMaintenanceElement TableMaintenanceElement = attrList.get(i);
			if (TableMaintenanceElement.getColumn_name().equals(colName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param list
	 */
	public void setAttrList(ArrayList<TableMaintenanceElement> list) {
		attrList = list;
	}

	/**
	 * @return
	 */
	public TableMaintenanceRec getRecList(int index) {
		return recList.get(index);
	}

	/**
	 * @return
	 */
	public TableMaintenanceRec getRecList(Integer index) {
		return recList.get(index.intValue());
	}

	/**
	 * @return
	 */
	public ArrayList<TableMaintenanceRec> getRecList() {
		return recList;
	}

	/**
	 * @param list
	 */
	public void setRecList(ArrayList<TableMaintenanceRec> list) {
		recList = list;
	}

	/**
	 * @param list
	 */
	public void addRecList(TableMaintenanceRec newObj) {
		recList.add(newObj);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getTableList() {
		return tableList;
	}

	/**
	 * @param list
	 */
	public void setTableList(ArrayList<String> list) {
		tableList = list;
	}

	/**
	 * @return
	 */
	public String getUpdateIndex() {
		return updateIndex;
	}

	/**
	 * @param string
	 */
	public void setUpdateIndex(String string) {
		updateIndex = string;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param string
	 */
	public void addErrorMsg(String string) {
		errorMsg.add(string);
	}

	/**
	 * @param string
	 */
	public void clearErrorMsg() {
		errorMsg.clear();
	}

	/**
	 * @return
	 */
	public long getRecCount() {
		return recCount;
	}

	/**
	 * @param string
	 */
	public void setRecCount(long count) {
		recCount = count;
	}

	/**
	 * @return
	 */
	public long getRecNoPerPage() {
		return recNoPerPage;
	}

	/**
	 * @param string
	 */
	public void setRecNoPerPage(long count) {
		recNoPerPage = count;
	}

	/**
	 * @return
	 */
	public long getFromRecNo() {
		return fromRecNo;
	}

	/**
	 * @param string
	 */
	public void setFromRecNo(long count) {
		fromRecNo = count;
	}

	/**
	 * @return
	 */
	public long getDispFromRecNo() {
		return fromRecNo + 1;
	}

	/**
	 * @return
	 */
	public long getToRecNo() {
		return toRecNo;
	}

	/**
	 * @param string
	 */
	public void setToRecNo(long count) {
		toRecNo = count;
	}

	/**
	 * @return
	 */
	public long getDispToRecNo() {
		return Math.min(toRecNo, recCount);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getPageList() {
		return pageList;
	}

	/**
	 * @return
	 */
	public String getPageList(int idx) {
		return pageList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addPageList(String string) {
		pageList.add(string);
	}

	/**
	 * @param string
	 */
	public void setPageList(ArrayList<String> list) {
		pageList = list;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getPageNameList() {
		return pageNameList;
	}

	/**
	 * @return
	 */
	public String getPageNameList(int idx) {
		return pageNameList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addPageNameList(String string) {
		pageNameList.add(string);
	}

	/**
	 * @param string
	 */
	public void setPageNameList(ArrayList<String> list) {
		pageNameList = list;
	}

	/**
	 * @return
	 */
	public String getSelectPage() {
		return selectPage;
	}

	/**
	 * @param string
	 */
	public void setSelectPage(String val) {
		selectPage = val;
	}

	public MultipartFile getFileUp() {
		return fileUp;
	}

	public void setFileUp(MultipartFile fileUp) {
		this.fileUp = fileUp;
	}
}
