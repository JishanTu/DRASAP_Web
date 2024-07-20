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
 * 検索した件数が警告件数を超えた場合のAction
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
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}

		SearchWarningOverHitForm overHitForm = form;
		category.debug("act=" + overHitForm.getAct());
		category.debug("SqlWhere=" + overHitForm.getSqlWhere());
		category.debug("SqlOrder=" + overHitForm.getSqlOrder());

		// 継続の場合
		if ("continue".equals(overHitForm.getAct())) {
			// 次のSearchResultPreActionで使用するためにrequestにセットする
			request.setAttribute("SQL_WHERE", overHitForm.getSqlWhere());
			request.setAttribute("SQL_ORDER", overHitForm.getSqlOrder());

			category.debug("--> continue");
			return "continue";
		}
		// 中止の場合
		category.debug("--> cancel");
		return "cancel";
	}

}
