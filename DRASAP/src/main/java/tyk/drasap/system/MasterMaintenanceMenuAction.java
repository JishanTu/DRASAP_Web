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
		} else if ("accessLevelMaster".equals(masterMaintenanceMenuForm.getAct())) {
			return "accessLevelMaster";
		} else if ("userGroupAclRelation".equals(masterMaintenanceMenuForm.getAct())) {
			return "userGroupAclRelation";
		} else if ("tableMaintenance".equals(masterMaintenanceMenuForm.getAct())) {
			return "tableMaintenance";
		} else if ("logout".equals(masterMaintenanceMenuForm.getAct())) {
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