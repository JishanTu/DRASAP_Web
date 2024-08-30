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
import tyk.drasap.common.USER_ID_CONVERSION_DB;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * 他のシステムから、図面ビューイング機能を直接使用するためのログイン。
 * ログインに成功すれば図面ビューイング機能へ遷移する。
 * パラメータ(en_string)を受け取り、暗号複号化ツール(IDDE)を使用してIDを取得する。
 * その他の必須パラメータとして、drwg_no、sys_idがある。
 *
 * en_string・・・暗号化されたログインID
 * drwg_no・・・図番。DRASAPデータベースに登録された形式で。現行はハイフン抜きの形式。
 * sys_id・・・依頼したシステムのシステムID。ログに記録されます。
 * </PRE>
 * @author fumi
 * 作成日 2005/03/03
 * 変更日 $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 */
@Controller
public class DirectLoginForPreviewAction extends BaseAction {
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
	@PostMapping("/directLoginForPreview")
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

		//ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		String id = "";
		String drwgNo = "";
		String sys_id = "";
		String user_id_col = "";
		try {
			// sessionからパラメータを取得し、複号する
			String enString = (String) session.getAttribute("en_string");
			session.removeAttribute("en_string");// sessionからremoveする
			category.debug("en_string = " + enString);
			id = IDDE.decode(enString);// 複合化した結果
			category.debug("id = " + id);
			// パラメーター sys_id
			sys_id = (String) session.getAttribute("sys_id");
			session.removeAttribute("sys_id");// sessionからremoveする
			category.debug("sys_id = " + sys_id);
			user.setSys_id(sys_id);
			// パラメーター drwg_no
			drwgNo = (String) session.getAttribute("drwgNo");
			category.debug("drwg_no = " + drwgNo);
			if (drwgNo == null || drwgNo.length() == 0) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.invalid.dragno." + user.getLanKey(), new Object[] { "DRWG_NO is null" }, null));
				throw new UserException("drwgNo is null");
			}
			// パラメーター user_id_col
			user_id_col = (String) session.getAttribute("user_id_col");
			session.removeAttribute("user_id_col");// sessionからremoveする
			category.debug("user_id_col = " + user_id_col);

			session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");

			if ("-1".equals(id)) {
				// ログインID異常
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "ログインID異常" }, null));
			} else if ("-2".equals(id)) {
				// SYSpass異常
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "SYSpass異常" }, null));
			} else {
				// idを元にユーザー情報を取得し、userオブジェクトに付加する。
				addUserInfo(user, id, user_id_col, errors);
				if (Objects.isNull(errors.getAttribute("message"))) {
					// パスワード有効期限チェック
					if (!isPasswordExpired(user, errors)) {
						// システム情報を管理者設定マスターから取得
						drasapInfo = getDrasapInfo(user, errors);
					}
				}
			}
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.login.othersys." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			category.error("他のシステムからのログインに失敗\n" + ErrorUtility.error2String(e));
		}

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

		// DirectPreviewに必要なパラメータをrequestにsetAttributeする
		request.setAttribute("usr_id", id);// ユーザーID
		request.setAttribute("drwgNo", drwgNo);// 図番

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
	private void addUserInfo(User user, String id, String user_id_col, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			String drasapUserId = id;
			//　ユーザID変換テーブルのカラム名が指定されていたらDRASAPユーザＩＤに変換
			if (user_id_col != null && user_id_col.length() > 0) {
				drasapUserId = USER_ID_CONVERSION_DB.getDrasapUserId(user_id_col, id, conn);
			}

			if (UserDB.addUserInfo(user, drasapUserId.trim(), conn)) {
				// idが一致
			} else {
				// 一致しない
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("directlogin.undefined.drasapuserid." + user.getLanKey(), new Object[] { "Id" }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
				// for MUR
				category.error("ユーザID変換テーブルにDRASAPユーザIDが登録されていません。user_id=" + drasapUserId);
			}

		} catch (UserException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("directlogin.undefined.extuserid." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("ユーザID変換テーブルに外部システムのユーザIDが登録されていません\n" + ErrorUtility.error2String(e));
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
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
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.drasapinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
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
				if (ret == 1) {
					ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.password.notset"), user.getSys_id());
				} else {
					ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.password.expired"), user.getSys_id());
				}
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
