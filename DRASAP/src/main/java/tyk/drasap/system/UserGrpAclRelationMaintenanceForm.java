package tyk.drasap.system;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.common.TableInfo;
import tyk.drasap.springfw.form.BaseForm;

/**
 * 部門管理マスターの内容を保持する。
 */
public class UserGrpAclRelationMaintenanceForm extends BaseForm {
	/**
	 *
	 */
	String act;// 処理を分けるための属性
	ArrayList<UserGrpAclRelationMaintenanceElement> recList = new ArrayList<UserGrpAclRelationMaintenanceElement>();// UserGrpAclRelationMasterMaintenanceElement
	ArrayList<String> errorMsg = new ArrayList<String>();
	String userGrpCoce = "";
	TableInfo tableInfo;
	ArrayList<String> aclIdList = new ArrayList<String>();
	ArrayList<String> aclNameList = new ArrayList<String>();
	ArrayList<String> groupCodeList = new ArrayList<String>();
	ArrayList<String> groupNameList = new ArrayList<String>();
	ArrayList<String> aclValueList = new ArrayList<String>();
	ArrayList<String> aclValueNameList = new ArrayList<String>();
	String orderBy = "aclId";

	// --------------------------------------------------------- Methods
	public void reset(HttpServletRequest request) {
		for (int i = 0; i < recList.size(); i++) {
			recList.get(i).setUpdate(false);
		}
	}

	// --------------------------------------------------------- getter,setter
	public UserGrpAclRelationMaintenanceForm() {
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
	public UserGrpAclRelationMaintenanceElement getUserGrpAclRelationMasterMaintenanceElement(int index) {
		return recList.get(index);
	}

	/**
	 * @return
	 */
	public ArrayList<UserGrpAclRelationMaintenanceElement> getRecList() {
		return recList;
	}

	/**
	 * @return
	 */
	public UserGrpAclRelationMaintenanceElement getRecList(int idx) {
		return recList.get(idx);
	}

	/**
	 * @param list
	 */
	public void setRecList(ArrayList<UserGrpAclRelationMaintenanceElement> list) {
		recList = list;
	}

	/**
	 * @param list
	 */
	public void setUserGrpAclRelationMasterMaintenanceElement(int idx, UserGrpAclRelationMaintenanceElement list) {
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
	public void addRecList(UserGrpAclRelationMaintenanceElement list) {
		recList.add(list);
	}

	/**
	 * @param list
	 */
	public void deleteRecList(String aclId) {
		for (int i = 0; i < recList.size(); i++) {
			UserGrpAclRelationMaintenanceElement userGrpAclRelationMasterMaintenanceElement = recList.get(i);
			if (userGrpAclRelationMasterMaintenanceElement.getAclId().equals(aclId)) {
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

	/**
	 * @return
	 */
	public void clearGroupCodeList() {
		groupCodeList.clear();
	}

	/**
	 * @return
	 */
	public ArrayList<String> getGroupCodeList() {
		return groupCodeList;
	}

	/**
	 * @return
	 */
	public String getGroupCodeList(int idx) {
		return groupCodeList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addGroupCodeList(String string) {
		groupCodeList.add(string);
	}

	/**
	 * @param string
	 */
	public void setGroupCodeList(ArrayList<String> list) {
		groupCodeList = list;
	}

	/**
	 * @return
	 */
	public void clearGroupNameList() {
		groupNameList.clear();
	}

	/**
	 * @return
	 */
	public ArrayList<String> getGroupNameList() {
		return groupNameList;
	}

	/**
	 * @return
	 */
	public String getGroupNameList(int idx) {
		return groupNameList.get(idx);
	}

	/**
	 * @return
	 */
	public String getGroupName(String groupCode) {
		for (int i = 0; i < groupCodeList.size(); i++) {
			if (groupCodeList.get(i).toString().equals(groupCode)) {
				return groupNameList.get(i).toString();
			}
		}
		return "";
	}

	/**
	 * @param string
	 */
	public void addGroupNameList(String string) {
		groupNameList.add(string);
	}

	/**
	 * @param string
	 */
	public void setGroupNameList(ArrayList<String> list) {
		groupNameList = list;
	}

	/**
	 * @return
	 */
	public void clearAclIdList() {
		aclIdList.clear();
	}

	/**
	 * @return
	 */
	public ArrayList<String> getAclIdList() {
		return aclIdList;
	}

	/**
	 * @return
	 */
	public String getAclIdList(int idx) {
		return aclIdList.get(idx);
	}

	/**
	 * @param string
	 */
	public void addAclIdList(String string) {
		aclIdList.add(string);
	}

	/**
	 * @param string
	 */
	public void setAclIdList(ArrayList<String> list) {
		aclIdList = list;
	}

	/**
	 * @return
	 */
	public void clearAclNameList() {
		aclNameList.clear();
	}

	/**
	 * @return
	 */
	public ArrayList<String> getAclNameList() {
		return aclNameList;
	}

	/**
	 * @return
	 */
	public String getAclNameList(int idx) {
		return aclNameList.get(idx);
	}

	/**
	 * @return
	 */
	public String getAclName(String aclId) {
		for (int i = 0; i < aclIdList.size(); i++) {
			if (aclIdList.get(i).toString().equals(aclId)) {
				return aclNameList.get(i).toString();
			}
		}
		return "";
	}

	/**
	 * @param string
	 */
	public void addAclNameList(String string) {
		aclNameList.add(string);
	}

	/**
	 * @param string
	 */
	public void setAclNameList(ArrayList<String> list) {
		aclNameList = list;
	}

	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @return
	 */
	public void setOrderBy(String val) {
		orderBy = val;
	}

	public void clearAclValueList() {
		aclValueList.clear();
	}

	public ArrayList<String> getAclValueList() {
		return aclValueList;
	}

	public void addAclValueList(String val) {
		aclValueList.add(val);
	}

	public void setAclValueList(ArrayList<String> aclValueList) {
		this.aclValueList = aclValueList;
	}

	public void clearAclValueNameList() {
		aclValueNameList.clear();
	}

	public ArrayList<String> getAclValueNameList() {
		return aclValueNameList;
	}

	public void addAclValueNameList(String val) {
		aclValueNameList.add(val);
	}

	public void setAclValueNameList(ArrayList<String> aclValueNameList) {
		this.aclValueNameList = aclValueNameList;
	}
}
