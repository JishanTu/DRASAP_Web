package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.TableInfo;


/**
 * アクセスレベルマスターメンテナンス画面に対応
 */
public class AccessLevelMasterMaintenanceForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2446454148867321808L;
	String act;// 処理を分けるための属性
	ArrayList<AccessLevelMasterMaintenanceElement> recList = new ArrayList<AccessLevelMasterMaintenanceElement>();// AccessLevelMasterMaintenanceElement
	ArrayList<String> errorMsg = new ArrayList<String>();
	TableInfo tableInfo;
	// --------------------------------------------------------- Methods
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	    for (int i = 0; i < recList.size(); i++) {
	        ((AccessLevelMasterMaintenanceElement)recList.get(i)).setUpdate(false);
	        ((AccessLevelMasterMaintenanceElement)recList.get(i)).setNew(false);
	    }
	}
	// --------------------------------------------------------- getter,setter
	public AccessLevelMasterMaintenanceForm() {
	    act = "";
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
	public AccessLevelMasterMaintenanceElement getAccessLevelMasterMaintenanceElement(int index) {
		return (AccessLevelMasterMaintenanceElement)recList.get(index);
	}
	/**
	 * @return
	 */
	public ArrayList<AccessLevelMasterMaintenanceElement> getRecList() {
		return recList;
	}
	/**
	 * @return
	 */
	public AccessLevelMasterMaintenanceElement getRecList(int idx) {
		return (AccessLevelMasterMaintenanceElement)recList.get(idx);
	}
	/**
	 * @param list
	 */
	public void setRecList(ArrayList<AccessLevelMasterMaintenanceElement> list) {
	    recList = list;
	}
	/**
	 * @param list
	 */
	public void setAccessLevelMasterMaintenanceElement(int idx, AccessLevelMasterMaintenanceElement list) {
	    recList.set(idx, list);
	}
	/**
	 * @param list
	 */
	public void clearRecList() {
	    recList.clear();
	}
	/**
	 * @param list
	 */
	public void addRecList(AccessLevelMasterMaintenanceElement list) {
	    recList.add(list);
	}
	/**
	 * @param list
	 */
	public void deleteRecList(String aclId) {
	    for (int i = 0; i < recList.size(); i++) {
	        AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = (AccessLevelMasterMaintenanceElement)recList.get(i);
	        if (accessLevelMasterMaintenanceElement.getAclId().equals(aclId)) {
	            recList.remove(i);
	        }
	    }
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
}
