package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * ŠÇ—Òİ’è•ÏX‰æ–Ê‚É‘Î‰
 */
public class AdminSettingListForm extends BaseForm {
	/**
	 *
	 */
	ArrayList<AdminSettingListElement> adminSettingList = new ArrayList<AdminSettingListElement>();// AdminSettingListElement
	String act;// ˆ—‚ğ•ª‚¯‚é‚½‚ß‚Ì‘®«
	String updateIndex;
	ArrayList<String> statusList = new ArrayList<String>();
	ArrayList<String> statsuNameList = new ArrayList<String>();

	// --------------------------------------------------------- Methods
	public AdminSettingListForm() {
		act = "";
	}

	public void reset(HttpServletRequest request) {
		for (int i = 0; i < adminSettingList.size(); i++) {
			adminSettingList.get(i).setUpdate(false);
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
		return adminSettingList.get(idx);
	}

	/**
	 * @return
	 */
	public AdminSettingListElement getAdminSettingListElement(Integer idx) {
		return adminSettingList.get(idx.intValue());
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
		return statusList.get(idx);
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
		return statsuNameList.get(idx);
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
