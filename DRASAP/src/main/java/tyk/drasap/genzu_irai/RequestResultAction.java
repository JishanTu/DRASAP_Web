package tyk.drasap.genzu_irai;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.User;
import tyk.drasap.springfw.action.BaseAction;

/**
 * @author KAWAI
 * 原図庫作業依頼リストで完了情報登録をする
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
@Controller
public class RequestResultAction extends BaseAction {
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
	@PostMapping("/req_result")
	public String execute(
			Request_listForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}

		return "success";
	}
}
