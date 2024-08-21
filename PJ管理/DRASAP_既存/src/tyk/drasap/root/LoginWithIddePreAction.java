package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/**
 * LoginWithIddeActionにつなぐための、前処理。
 * 目的はブラウザのURLバーに暗号が表示されるのを防ぐため。
 * RequestParameterで en_string,fn を受ける。
 * en_string・・・暗号化されたログインID
 * fn・・・ファンクション
 */
public class LoginWithIddePreAction extends Action {
	private static Category category = Category.getInstance(LoginWithIddePreAction.class.getName());
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
		category.debug("start");
		// パラメータで en_string を受け取る
		String enString = request.getParameter("en_string");
		// パラメータfnを追加 '04.May.13
		String fn = request.getParameter("fn");

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if(enString != null){
			// リダイレクトすると、requestのattributeは消えてしまうので
			// sessionに格納する。
			request.getSession().setAttribute("en_string", enString);
		} else {
			return mapping.findForward("timeout");
		}
		if(fn != null){
			request.getSession().setAttribute("fn", fn);
		}

		// リダイレクトする
		category.debug("redirect --> /loginWithIdde.do");
//		return new ActionForward("/loginWithIdde.do", true);
		return mapping.findForward("success");
	}

}
