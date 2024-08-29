package tyk.drasap.root;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.toyodakouki.IDDE;
import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * シングルサインオンのためのログイン。ポータルからのログインに対応するため。
 * パラメータ(en_string)を受け取り、暗号複号化ツール(IDDE)を使用してIDを取得する。
 * メニューを介さずに、直接ファンクションに遷移するために、パラメータfnを追加。'04.May.13
 * @version 2013/07/25 yamagishi
 */
@Controller
public class LoginWithIddeAction extends BaseAction {
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
	@PostMapping("/loginWithIdde")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// まずUserを作成する
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ついでにsessionから削除する
		category.debug("session ID=" + session.getId());
		//ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		// sessionからパラメータを取得し、複号する
		String enString = (String) session.getAttribute("en_string");
		session.removeAttribute("en_string");// sessionからremoveする
		category.debug("en_string = " + enString);
		if (enString == null) {
			return "timeout";
		}
		String id = IDDE.decode(enString);// 復号化した結果
		category.debug("id = " + id);
		// パラメータ fnを追加
		String fn = (String) session.getAttribute("fn");
		session.removeAttribute("fn");// sessionからremoveする
		if (fn == null) {
			return "timeout";
		}
		category.debug("fn = " + fn);

		if ("-1".equals(id)) {
			// ログインID異常
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "ログインID異常" }, null));
		} else if ("-2".equals(id)) {
			// SYSpass異常
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "SYSpass異常" }, null));
		} else {
			//			int serverPort = request.getServerPort();
			//			category.debug("serverPort="+Integer.toString(serverPort));
			//			try{
			//				ds = DataSourceFactory.getOracleDataSource(serverPort);
			//			} catch(Exception e){
			//				category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
			//			}
			// idを元にユーザー情報を取得し、userオブジェクトに付加する。
			addUserInfo(user, id, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// パスワード有効期限チェック
				if (!isPasswordExpired(user, errors)) {
					// システム情報を管理者設定マスターから取得
					drasapInfo = getDrasapInfo(user, errors);
				}
			}
		}

		if (!Objects.isNull(errors.getAttribute("message"))) {
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);// エラーを登録
			category.debug("--> failed");
			return "failed";
		}
		// クッキーから言語設定を取得し、userオブジェクトに付加する。
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
		// パラメータfnによって処理を分ける。'04.May.13変更
		// fn = 1-4 は、グローバルフォワードを使用する。
		if ("1".equals(fn)) {
			// fn = 1 なら　図面検索
			category.debug("--> search_search_Main");
			return "search_search_Main";

		}
		if ("2".equals(fn)) {
			// fn = 2 なら　原図庫作業依頼
			category.debug("--> genzu_irai_request");
			return "genzu_irai_request";

		}
		if ("3".equals(fn)) {
			// fn = 3 なら　原図庫作業依頼詳細
			category.debug("--> genzu_irai_request_ref");
			return "genzu_irai_request_ref";

		}
		if ("4".equals(fn)) {
			// fn = 4 なら　原図庫作業依頼リスト
			category.debug("--> genzu_irai_request_list");
			return "genzu_irai_request_list";

			// 2013.07.25 yamagishi add. start
		} else if ("5".equals(fn)) {
			// fn = 5 なら　アクセスレベル一括更新
			category.debug("--> acl_batch_update");
			return "acl_batch_update";
		} else if ("6".equals(fn)) {
			// fn = 6 なら　アクセスレベル更新結果
			category.debug("--> acl_updated_result");
			return "acl_updated_result";
			// 2013.07.25 yamagishi add. end

		} else {
			// それ以外の場合はメニューへ
			category.debug("--> success");
			return "success";

		}
	}

	/**
	 * id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			if (UserDB.addUserInfo(user, id, conn)) {
				// idが一致
			} else {
				// 一致しない
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.missmatch." + user.getLanKey(), new Object[] { "Id" }, null));
			}

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
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
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.drasapinfo.jp", new Object[] { e.getMessage() }, null));
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
	 * パスワード有効期限チェック
	 * @param user
	 * @param errors
	 * @return true:過ぎた false:過ぎていない
	 */
	private boolean isPasswordExpired(User user, Model errors) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			int ret = UserDB.checkPasswordExpiry(user, errors, conn);
			if (ret != 0) {
				String errMsg = ret == 1 ? "root.failed.password.notset." : "root.failed.password.expired.";
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage(errMsg + user.getLanKey(), null, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
				// for MUR
				category.error("パスワード有効期限を過ぎた。戻り値=[" + ret + "]\n");
				result = true;
			}
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.check.password.expiry." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("パスワード有効期限チェックに失敗\n" + ErrorUtility.error2String(e));
			result = true;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

}
