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
 * ���������������x�������𒴂����ꍇ��Action
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
		// session�^�C���A�E�g�̊m�F
		if(user == null){
			return mapping.findForward("timeout");
		}

		SearchWarningOverHitForm overHitForm = (SearchWarningOverHitForm)form;
		category.debug("act=" + overHitForm.getAct());
		category.debug("SqlWhere=" + overHitForm.getSqlWhere());
		category.debug("SqlOrder=" + overHitForm.getSqlOrder());

		// �p���̏ꍇ
		if("continue".equals(overHitForm.getAct())){
			// ����SearchResultPreAction�Ŏg�p���邽�߂�request�ɃZ�b�g����
			request.setAttribute("SQL_WHERE", overHitForm.getSqlWhere());
			request.setAttribute("SQL_ORDER", overHitForm.getSqlOrder());

			category.debug("--> continue");
			return mapping.findForward("continue");
		}
		// ���~�̏ꍇ
		category.debug("--> cancel");
		return mapping.findForward("cancel");
	}

}
