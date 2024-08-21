package tyk.drasap.genzu_irai;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.User;


/**
 * @author KAWAI
 * 原図庫作業依頼リストで完了情報登録をする
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class RequestResultAction extends Action {
	
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
	
		return(mapping.findForward("success"));			
	}			
}
