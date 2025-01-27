package tyk.drasap.root;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ログインのAction。
 */
@Controller
public class LoginAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods

	public LoginAction() {
		category.debug("start");
		category.debug("end");
	}

	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/login")
	public String execute(
			LoginForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		LoginForm loginForm = form;
		// まずUserを作成する
		User user = new User(request.getRemoteAddr());

		HttpSession session = request.getSession();
		session.removeAttribute("user");// ついでにsessionから削除する

		// パスワード変更無しでパスワード変更画面を閉じた場合に
		// sessionに残るためここで削除
		session.removeAttribute("samePasswdId");

		//		int localPort = request.getLocalPort();
		//		int serverPort = request.getServerPort();
		//        String container = "weblogic";
		//        category.debug("container="+System.getenv("container"));
		//        if (System.getenv("container") != null) {
		//            container = "tomcat";
		//        }
		//		Properties appProp = DrasapPropertiesFactory.getDrasapProperties(
		//				new DataSourceFactory());
		//		try{
		//			ds = DataSourceFactory.getOracleDataSource(serverPort);
		//		} catch(Exception e){
		//			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		//		}
		//		Properties pop = System.getProperties();
		//		category.debug("pop="+pop.toString());

		//		ActionMessages errors = new ActionMessages();
		// id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// システム情報を管理者設定マスターから取得
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

		if (!Objects.isNull(errors.getAttribute("message"))) {
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);// エラーを登録
			category.debug("--> failed");
			return "failed";
		}
		// クッキーから言語設定を取得
		CookieManage langCookie = new CookieManage();
		String lanKey = langCookie.getCookie(request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) {
			lanKey = "Japanese";
		}
		user.setLanguage(lanKey);
		// ユーザー情報が取得できたら sessionに格納する
		session.setAttribute("user", user);
		session.setAttribute("drasapInfo", drasapInfo);
		session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");

		// パスワード設定日 + 有効期限日数 < 現在日時の場合はパスワード変更
		int ret = isChangePassword(user, errors);
		if (0 != ret) {
			if (1 == ret) {
				// パスワードがユーザIDと同じ場合はsessionに保持
				session.setAttribute("samePasswdId", "true");
			}
			// 遷移元をsessionに保持
			session.setAttribute("parentPage", "Login");
			category.debug("--> chgpasswd");
			return "chgpasswd";
		}
		category.debug("--> success");
		return "success";
	}

	/**
	 * id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, String passwd, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			if (UserDB.addUserInfo(user, id, passwd, conn)) {
				// id,パスワードが一致
			} else {
				// 一致しない
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("root.missmatch", new Object[] { "IDまたはPassword" }, null));
			}

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * システム情報を取得する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param errors
	 */
	private DrasapInfo getDrasapInfo(User user, Model errors) {
		Connection conn = null;
		DrasapInfo drasapInfo = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			drasapInfo = AdminSettingDB.getDrasapInfo(conn);

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.drasapinfo", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("システム情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return drasapInfo;

	}

	/**
	 * パスワード変更有無チェック
	 *
	 * @param user
	 * @param errors
	 * @return 0: 変更無 <br/> 1: 変更有(パスワードがユーザID) <br/> 2: 変更有(有効期限切れ)
	 * @throws Exception
	 */
	private int isChangePassword(User user, Model errors) throws Exception {

		/*
		 * 現在のパスワードチェック
		 */
		Connection conn = null;
		conn = ds.getConnection();
		conn.setAutoCommit(true);// 非トランザクション

		// パスワード有効期限チェック
		return UserDB.checkPasswordExpiry(user, errors, conn);
	}

}
