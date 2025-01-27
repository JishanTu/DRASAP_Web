package tyk.drasap.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ログインのAction。
 *
 * @version 2013/06/13 yamagishi
 */
@Controller
public class Delete_LoginAction extends BaseAction {
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
	@PostMapping("/delete_Login")
	public String execute(
			Delete_LoginForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		Delete_LoginForm loginForm = form;
		HttpSession session = request.getSession();
		//ActionMessages errors = new ActionMessages();

		User user = (User) session.getAttribute("user");

		// id、パスワードチェック
		chkPasswd(user, loginForm.getPasswd(), errors);
		if (Objects.isNull(errors.getAttribute("message"))) {
			// ユーザー情報が取得できたら sessionに格納する
			category.debug("--> success");
			request.setAttribute("task", "init");
			return "success";

		}
		//saveErrors(request, errors);
		request.setAttribute("errors", errors);// エラーを登録
		category.debug("--> failed");
		return "failed";
	}

	/**
	 * id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void chkPasswd(User user, String passwd, Model errors) {
		CsvItemStrList delDwgPs;
		// 2013.06.13 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		//		String passwdFile = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
		// 2013.06.13 yamagishi modified. end
		String passwdFile = DrasapPropertiesFactory.getFullPath("tyk.csvdef.delDwgPs.path");
		try {
			delDwgPs = new CsvItemStrList(passwdFile);
			if (delDwgPs.searchLineData(user.getId()) != null) {
				if (!delDwgPs.searchLineData(user.getId()).get(1).equals(passwd)) {
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delLogin.passMissmatch", new Object[] { "Password" }, null));
				}
			} else {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delLogin.userNotFound", new Object[] { user.getName() }, null));

			}

		} catch (FileNotFoundException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delLogin.FileNotFound", new Object[] { passwdFile }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
		}
	}

}
