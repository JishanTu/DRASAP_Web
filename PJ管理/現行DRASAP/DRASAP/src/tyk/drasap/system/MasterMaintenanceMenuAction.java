package tyk.drasap.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.User;

/** 
 * マスターメンテナンスメニューのAction。
 */
public class MasterMaintenanceMenuAction extends Action {
//	private static Category category = Category.getInstance(MasterMaintenanceMenuAction.class.getName());

	// --------------------------------------------------------- Methods

	/** 
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,	HttpServletResponse response) throws Exception {
//		category.debug("start");
		MasterMaintenanceMenuForm masterMaintenanceMenuForm = (MasterMaintenanceMenuForm) form;
		//
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		if("adminSettingList".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("adminSettingList");
		} else if("userGroupMaster".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("userGroupMaster");
		} else if("deptMaster".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("deptMaster");
		} else if("userMaster".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("userMaster");
		} else if("accessLevelMaster".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("accessLevelMaster");
		} else if("userGroupAclRelation".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("userGroupAclRelation");
		} else if("tableMaintenance".equals(masterMaintenanceMenuForm.getAct())){
			return mapping.findForward("tableMaintenance");
		} else if ("logout".equals(masterMaintenanceMenuForm.getAct())){
		    session.invalidate();
			return mapping.findForward("logout");
		}
		
		return mapping.findForward("success");
	}
//	private void removeAttribute(HttpSession session) {
//		session.removeAttribute("accessLevelMasterMaintenanceForm");
//		session.removeAttribute("adminSettingListForm");
//		session.removeAttribute("tableMaintenanceForm");
//	}
}