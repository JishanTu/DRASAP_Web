package tyk.drasap.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.User;
import tyk.drasap.springfw.action.BaseAction;

/**
 * ���������������x�������𒴂����ꍇ��Action
 *
 * @version 2013/09/13 yamagishi
 */
@Controller
public class SearchWarningOverHitAction extends BaseAction {
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
	@PostMapping("/searchWarningOverHit")
	public String execute(
			SearchWarningOverHitForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}

		SearchWarningOverHitForm overHitForm = form;
		category.debug("act=" + overHitForm.getAct());
		category.debug("SqlWhere=" + overHitForm.getSqlWhere());
		category.debug("SqlOrder=" + overHitForm.getSqlOrder());

		// �p���̏ꍇ
		if ("continue".equals(overHitForm.getAct())) {
			// ����SearchResultPreAction�Ŏg�p���邽�߂�request�ɃZ�b�g����
			request.setAttribute("SQL_WHERE", overHitForm.getSqlWhere());
			request.setAttribute("SQL_ORDER", overHitForm.getSqlOrder());

			category.debug("--> continue");
			return "continue";
		}
		// ���~�̏ꍇ
		category.debug("--> cancel");
		return "cancel";
	}

}
