package tyk.drasap.change_passwd;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

@Controller
public class ChangePasswdAction extends BaseAction {
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
	@PostMapping("/changePasswd")
	public String execute(
			ChangePasswdForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("ChangePassword start");

		ChangePasswdForm passwdForm = form;

		HttpSession session = request.getSession();

		// セッションからuser情報取得
		User user = (User) session.getAttribute("user");

		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false); // 自動コミットしない

			//			category.debug("oldpass=" + passwdForm.getOldpass());
			//			category.debug("newpass=" + passwdForm.getNewpass());

			// パスワード更新
			UserDB.updatePassword(user.getId(), passwdForm.getNewpass(), conn);

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(),
							new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		String page = (String) session.getAttribute("parentPage");
		String forward = "";
		if (Objects.isNull(errors.getAttribute("message"))) {
			if (StringUtils.isEmpty(page) || "Search".equals(page)) {
				// 遷移元が図面検索画面の場合
				category.debug("--> success");
				forward = "successFromSearch";
			} else if ("Login".equals(page)) {
				// 遷移元がログイン画面の場合
				category.debug("--> success");
				forward = "successFromLogin";
			}
			// sessionから削除
			session.removeAttribute("samePasswdId");
			//			session.removeAttribute("parentPage");
		} else {
			category.debug("--> failed");
			// 失敗の場合はパスワード変更画面に遷移
			forward = "failed";
		}

		return forward;

	}
}
