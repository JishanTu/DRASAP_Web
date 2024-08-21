package tyk.drasap.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.User;

/**
 * 検索した件数が警告件数を超えた場合のAction
 *
 * @version 2013/09/13 yamagishi
 */
@SuppressWarnings("deprecation")
public class SearchWarningOverHitAction extends Action {
	private static Category category = Category.getInstance(SearchWarningOverHitAction.class.getName());
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
		HttpServletRequest request,HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}

		SearchWarningOverHitForm overHitForm = (SearchWarningOverHitForm)form;
		category.debug("act=" + overHitForm.getAct());
		category.debug("SqlWhere=" + overHitForm.getSqlWhere());
		category.debug("SqlOrder=" + overHitForm.getSqlOrder());

		// 継続の場合
		if("continue".equals(overHitForm.getAct())){
			// 次のSearchResultPreActionで使用するためにrequestにセットする
			request.setAttribute("SQL_WHERE", overHitForm.getSqlWhere());
			request.setAttribute("SQL_ORDER", overHitForm.getSqlOrder());

			category.debug("--> continue");
			return mapping.findForward("continue");
		}
		// 中止の場合
		category.debug("--> cancel");
		return mapping.findForward("cancel");
	}

}
