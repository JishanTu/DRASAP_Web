package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;



/**
 * 管理者設定変更画面に対応
 */
public class AdminSettingListForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7065770539289213930L;
	ArrayList<AdminSettingListElement> adminSettingList = new ArrayList<AdminSettingListElement>();// AdminSettingListElement
	String act;// 処理を分けるための属性
	String updateIndex;
	ArrayList<String> statusList = new ArrayList<String>();
	ArrayList<String> statsuNameList = new ArrayList<String>();
	// --------------------------------------------------------- Methods
	public AdminSettingListForm() {
	    act = "";
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	    for (int i = 0; i < adminSettingList.size(); i++) {
	        ((AdminSettingListElement)adminSettingList.get(i)).setUpdate(false);
	    }
	}
	// --------------------------------------------------------- getter,setter
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
	public ArrayList<AdminSettingListElement> getAdminSettingList() {
		return adminSettingList;
	}
	/**
	 * @return
	 */
	public AdminSettingListElement getAdminSettingListElement(int idx) {
		return (AdminSettingListElement)adminSettingList.get(idx);
	}
	/**
	 * @return
	 */
	public AdminSettingListElement getAdminSettingListElement(Integer idx) {
		return (AdminSettingListElement)adminSettingList.get(idx.intValue());
	}
	/**
	 * @param list
	 */
	public void setAdminSettingList(ArrayList<AdminSettingListElement> list) {
	    adminSettingList = list;
	}
	/**
	 * @param list
	 */
	public void setAdminSettingList(AdminSettingListElement elem) {
	    adminSettingList.add(elem);
	}
	/**
	 * @return
	 */
	public ArrayList<String> getStatusList() {
		return statusList;
	}
	/**
	 * @return
	 */
	public String getStatusList(int idx) {
		return (String)statusList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addStatusList(String string) {
	    statusList.add(string);
	}
	/**
	 * @param string
	 */
	public void setStatusList(ArrayList<String> list) {
	    statusList = list;
	}
	/**
	 * @return
	 */
	public ArrayList<String> getStatsuNameList() {
		return statsuNameList;
	}
	/**
	 * @return
	 */
	public String getStatsuNameList(int idx) {
		return (String)statsuNameList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addStatsuNameList(String string) {
	    statsuNameList.add(string);
	}
	/**
	 * @param string
	 */
	public void setStatsuNameList(ArrayList<String> list) {
	    statsuNameList = list;
	}
}
