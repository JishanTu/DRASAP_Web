package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

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
@Controller
public class DirectLoginForPreviewPreAction extends BaseAction {
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
	@PostMapping("/directLoginForPreviewPre")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// パラメータで en_string を受け取る
		String enString = request.getParameter("en_string");
		// パラメータで drwg_no を受け取る
		String sys_id = request.getParameter("sys_id");
		// パラメータで drwg_no を受け取る
		String drwgNo = request.getParameter("drwg_no");
		// パラメータで user_id_col を受け取る
		String user_id_col = request.getParameter("user_id_col");

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if (enString != null) {
			// リダイレクトすると、requestのattributeは消えてしまうので
			// sessionに格納する。
			request.getSession().setAttribute("en_string", enString);
		}
		request.getSession().setAttribute("drwgNo", drwgNo);
		if (sys_id != null) {
			request.getSession().setAttribute("sys_id", sys_id);
		}
		if (user_id_col != null) {
			request.getSession().setAttribute("user_id_col", user_id_col);
		}

		// リダイレクトする
		category.debug("redirect --> /directLoginForPreview");
		return "/directLoginForPreview.do";
	}

}
