package tyk.drasap.root;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

/**
 * 通知票・図面の削除
 * @author Y.eto
 * 作成日: 2006/05/10
 */
@Controller
public class DirectSearchAction extends BaseAction {
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
	@PostMapping(value = { "/directSearch", "/directSearch.do" }) //　外部IFなので、元々アクセスURLをサポートしなければならないため、ここは.doを付けとく
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws SQLException {
		category.debug("start");
		// パラメータで en_string を受け取る
		String enString = request.getParameter("en_string");
		// パラメータで sys_id を受け取る
		String sys_id = request.getParameter("sys_id");
		//String[] attrArray = request.getParameterValues("DRWG_NO");
		//int len = attrArray.length;

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if (enString != null) {
			// リダイレクトすると、requestのattributeは消えてしまうので
			// sessionに格納する。
			request.getSession().setAttribute("en_string", enString);
		}
		if (sys_id != null) {
			request.getSession().setAttribute("sys_id", sys_id);
		}

		// リダイレクトする
		category.debug("redirect --> /directLoginForPreview.do");
		return "/directLoginForPreview.do";
	}
}
