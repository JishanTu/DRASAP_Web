package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <PRE>
 * DirectLoginForPreviewActionにつなぐための、前処理。
 * 目的はブラウザのURLバーに暗号が表示されるのを防ぐため。
 * 
 * 注意!! このため、sessionに格納するため、同じ画面から短時間に連続で呼び出された場合、
 * 正常に動作しない場合がある。0.5秒ほど間隔を空ければOK。
 * komebomから呼び出す場合は、実際に0.5秒空けた。
 * 
 * RequestParameterで en_string,fn を受ける。
 * en_string・・・暗号化されたログインID
 * drwg_no・・・図番。DRASAPデータベースに登録された形式で。現行はハイフン抜きの形式。
 * sys_id・・・システムID。ログに記録されます。
 * </PRE>
 * @author fumi
 * 作成日 2005/03/03
 * 変更日 $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 */
public class DirectLoginForMultiPreviewPreAction extends Action {
	private static Category category = Category.getInstance(DirectLoginForMultiPreviewPreAction.class.getName());
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
		// パラメータで drwg_no を受け取る
		String sys_id = request.getParameter("sys_id");
		// パラメータで user_id_col を受け取る
		String user_id_col = request.getParameter("user_id_col");
		// フォームから drwg_no を受け取る
		String[] drwgNoArray = request.getParameterValues("DRWG_NO");

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if(enString != null){
			// リダイレクトすると、requestのattributeは消えてしまうので
			// sessionに格納する。
			request.getSession().setAttribute("en_string", enString);
		}
//		if(drwgNoArray.length > 0){
//			request.getSession().setAttribute("drwgNoArray", drwgNoArray);
//		}
		request.getSession().setAttribute("drwgNoArray", drwgNoArray);
		if(sys_id != null){
			request.getSession().setAttribute("sys_id", sys_id);
		}
		if(user_id_col != null){
			request.getSession().setAttribute("user_id_col", user_id_col);
		} 
		
		// リダイレクトする
		category.debug("redirect --> /directLoginForMultiPreview.do");
		return new ActionForward("/directLoginForMultiPreview.do", true);
	}

}
