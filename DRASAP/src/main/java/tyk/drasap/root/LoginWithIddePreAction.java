package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

/**
 * LoginWithIddeActionにつなぐための、前処理。
 * 目的はブラウザのURLバーに暗号が表示されるのを防ぐため。
 * RequestParameterで en_string,fn を受ける。
 * en_string・・・暗号化されたログインID
 * fn・・・ファンクション
 */
@Controller
public class LoginWithIddePreAction extends BaseAction {
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
	@PostMapping("/loginWithIddePre")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// パラメータで en_string を受け取る
		String enString = request.getParameter("en_string");
		// パラメータfnを追加 '04.May.13
		String fn = request.getParameter("fn");

		// en_string をsetAttributeする
		category.debug("enString = " + enString);
		if (enString == null) {
			return "timeout";
		}
		// リダイレクトすると、requestのattributeは消えてしまうので
		// sessionに格納する。
		request.getSession().setAttribute("en_string", enString);
		if (fn != null) {
			request.getSession().setAttribute("fn", fn);
		}

		// リダイレクトする
		category.debug("redirect --> /loginWithIdde.do");
		return "success";
	}

}
