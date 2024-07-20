package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import tyk.drasap.common.StringCheck;
import tyk.drasap.common.UserKeyColDB;

/**
 * メニュー画面に対応
 */
public class TableMaintenanceForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3305409154536915815L;
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
	private FormFile fileUp;

	// --------------------------------------------------------- Methods
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	    for (int i = 0; i < recList.size(); i++) {
	        ((TableMaintenanceRec)recList.get(i)).setCheck(false);
	        ((TableMaintenanceRec)recList.get(i)).setNew(false);
	    }
	}
	// --------------------------------------------------------- getter,setter
	public TableMaintenanceForm() {
	    act = "";
	}
	public TableMaintenanceForm(ArrayList<TableMaintenanceElement> attr, ArrayList<TableMaintenanceRec> value, ArrayList<String> tableList) {
	    this.attrList = attr;
	    this.recList = value;
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
		return (TableMaintenanceElement)attrList.get(index.intValue());
	}
	/**
	 * @return
	 */
	public TableMaintenanceElement getAttrList(int index) {
		return (TableMaintenanceElement)attrList.get(index);
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
		    TableMaintenanceElement TableMaintenanceElement = (TableMaintenanceElement)attrList.get(i);
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
		return (TableMaintenanceRec)recList.get(index);
	}
	/**
	 * @return
	 */
	public TableMaintenanceRec getRecList(Integer index) {
		return (TableMaintenanceRec)recList.get(index.intValue());
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
		return Math.min(toRecNo,recCount);
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
		return (String)pageList.get(idx);
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
		return (String)pageNameList.get(idx);
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
	public FormFile getFileUp() { 
	    return fileUp;
	}
	public void setFileUp(FormFile fileUp) {
	    this.fileUp = fileUp;
	}
}
