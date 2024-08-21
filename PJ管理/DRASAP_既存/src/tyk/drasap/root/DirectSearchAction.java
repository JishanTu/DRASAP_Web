package tyk.drasap.root;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** 
 * 通知票・図面の削除
 * @author Y.eto
 * 作成日: 2006/05/10
 */
public class DirectSearchAction extends Action {
	private static Category category = Category.getInstance(DirectSearchAction.class.getName());
	// --------------------------------------------------------- Methods

	/** 
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws SQLException 
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,	HttpServletResponse response) throws SQLException {
		category.debug("start");
		// パラメータで en_string を受け取る
		String enString = request.getParameter("en_string");
		// パラメータで drwg_no を受け取る
		String sys_id = request.getParameter("sys_id");
		//String[] attrArray = request.getParameterValues("DRWG_NO");
		//int len = attrArray.length;

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if(enString != null){
			// リダイレクトすると、requestのattributeは消えてしまうので
			// sessionに格納する。
			request.getSession().setAttribute("en_string", enString);
		}
		if(sys_id != null){
			request.getSession().setAttribute("sys_id", sys_id);
		}
		
		// リダイレクトする
		category.debug("redirect --> /directLoginForPreview.do");
		return new ActionForward("/directLoginForPreview.do", true);
	}
}
