package tyk.drasap.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.User;
import tyk.drasap.springfw.action.BaseAction;

/**
 * マスターメンテナンスメニューのAction。
 */
@Controller
public class MasterMaintenanceMenuAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods
	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/masterMaintenanceMenu")
	public String execute(
			MasterMaintenanceMenuForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		//		category.debug("start");
		MasterMaintenanceMenuForm masterMaintenanceMenuForm = form;
		//
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		if ("adminSettingList".equals(masterMaintenanceMenuForm.getAct())) {
			session.setAttribute("act", "init");
			return "adminSettingList";
		}
		if ("userGroupMaster".equals(masterMaintenanceMenuForm.getAct())) {
			return "userGroupMaster";
		}
		if ("deptMaster".equals(masterMaintenanceMenuForm.getAct())) {
			return "deptMaster";
		}
		if ("userMaster".equals(masterMaintenanceMenuForm.getAct())) {
			return "userMaster";
		}
		if ("accessLevelMaster".equals(masterMaintenanceMenuForm.getAct())) {
			session.setAttribute("act", "init");
			return "accessLevelMaster";
		}
		if ("userGroupAclRelation".equals(masterMaintenanceMenuForm.getAct())) {
			session.setAttribute("act", "init");
			return "userGroupAclRelation";
		}
		if ("tableMaintenance".equals(masterMaintenanceMenuForm.getAct())) {
			session.setAttribute("act", "init");
			return "tableMaintenance";
		}
		if ("logout".equals(masterMaintenanceMenuForm.getAct())) {
			session.invalidate();
			return "logout";
		}

		return "success";
	}
	//	private void removeAttribute(HttpSession session) {
	//		session.removeAttribute("accessLevelMasterMaintenanceForm");
	//		session.removeAttribute("adminSettingListForm");
	//		session.removeAttribute("tableMaintenanceForm");
	//	}
}