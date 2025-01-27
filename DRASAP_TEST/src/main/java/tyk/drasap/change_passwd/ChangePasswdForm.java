package tyk.drasap.change_passwd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserDef;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * @author tetsuya_yamamoto
 *
 */
@Component
public class ChangePasswdForm extends BaseForm {
	/**
	 *
	 */
	private static Logger category = Logger.getLogger(ChangePasswdAction.class.getName());
	private static DataSource ds;
	static {
		try {
			ds = DataSourceFactory.getOracleDataSource();
		} catch (Exception e) {
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}

	private String oldpass;
	private String newpass;
	private String newPassConfirm;

	/**
	 *
	 */
	public ChangePasswdForm() {
	}

	/**
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {
		//ActionErrors errors = new ActionErrors();

		// セッションからuser情報取得
		User user = (User) request.getSession().getAttribute("user");

		// sessionタイムアウトの確認
		if (user == null) {
			// エラーメッセージは空で返す。
			// JSPの方でエラーメッセージ表示前に
			// セッションタイムアウトページに転送するため。
			MessageSourceUtil.addAttribute(errors, "message", "");
			return errors;
		}

		try {
			// properties取得
			ProfileString prop = null;
			try {
				prop = new ProfileString(this, "application.properties");
			} catch (FileNotFoundException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException();

			} catch (IOException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException();
			}

			// フォームはすべて入力済みか？
			if (isInputPassword(errors, prop, messageSource) == false) {
				// 未入力項目がある場合はチェック終了
				throw new UserException();
			}

			// 2バイト文字または入力不可の記号はエラー
			char[] passChars = newpass.toCharArray();
			char[] symbols = prop.getValue("chgpasswd.input.allowedSymbols").toCharArray();
			for (char pass : passChars) {
				if ('\u0041' <= pass && pass <= '\u005a' // 大文字 A - Z
						|| '\u0061' <= pass && pass <= '\u007a' // 小文字 a - z
						|| '\u0030' <= pass && pass <= '\u0039') { // 数字 0 - 9
					// OK 次の文字へ
					continue;
				}
				// 記号チェック
				boolean chkFlg = false;
				for (char s : symbols) {
					if (s == pass) {
						// 入力可能な記号
						chkFlg = true;
						break;
					}
				}
				if (!chkFlg) {
					// 入力不可の記号
					MessageSourceUtil.addAttribute(errors, "illegalString",
							messageSource.getMessage("chgpasswd.failed.Illegal.passwd", null, null));
					break;
				}
			}

			// 現在のパスワードチェック
			checkCurrentPassword(user, prop, errors, messageSource);

			// 新しいパスワードは現在のパスワードとは異なるか？
			if (oldpass.equals(newpass)) {
				// 一致
				MessageSourceUtil.addAttribute(errors, "matcholdpassword",
						messageSource.getMessage("chgpasswd.failed.match.oldpassword", null, null));
			}

			// 新しいパスワードと確認パスワードは同じか？
			if (!newpass.equals(newPassConfirm)) {
				// 不一致
				MessageSourceUtil.addAttribute(errors, "disagreement",
						messageSource.getMessage("chgpasswd.failed.missmatch.newpasswd", null, null));
			}

			// IDとPasswordが同じ場合はエラー
			if (newpass.equals(user.getId())) {
				// IDと一致
				MessageSourceUtil.addAttribute(errors, "matchUserID",
						messageSource.getMessage("chgpasswd.failed.match.userId", null, null));
			}

			// パスワード整合性チェック
			checkPasswordIntegrity(errors, prop, messageSource);

		} catch (UserException e) {
			category.error(errors.toString());
		}

		return errors;
	}

	/**
	 * フォームはすべて入力済みか？
	 *
	 * @param errors
	 * @param prop
	 * @return
	 */
	private boolean isInputPassword(Model errors, ProfileString prop, MessageSource messageSource) {

		boolean inputFlag = true;

		// 入力チェック oldpass
		if (oldpass == null || oldpass.length() == 0) {
			// 未入力
			MessageSourceUtil.addAttribute(errors, "passwd", messageSource.getMessage("chgpasswd.required",
					new Object[] { "" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
							"" + prop.getValue("chgpasswd.name.oldpasswd.en") },
					null));
			inputFlag = false;
		}
		// 入力チェック newpass
		if (newpass == null || newpass.length() == 0) {
			// 未入力
			MessageSourceUtil.addAttribute(errors, "newPass", messageSource.getMessage("chgpasswd.required",
					new Object[] { "" + prop.getValue("chgpasswd.name.newpasswd.jp"),
							"" + prop.getValue("chgpasswd.name.newpasswd.en") },
					null));
			inputFlag = false;

		}
		// 入力チェック・・・Re-enter
		if (newPassConfirm == null || newPassConfirm.length() == 0) {
			// 未入力
			MessageSourceUtil.addAttribute(errors, "Re-enter",
					messageSource.getMessage("chgpasswd.required.re-enter", null, null));
			inputFlag = false;
		}

		return inputFlag;
	}

	/**
	 * 現在のパスワードチェック
	 *
	 * @param user
	 * @param prop
	 * @param errors
	 * @throws UserException
	 */
	private void checkCurrentPassword(User user, ProfileString prop, Model errors, MessageSource messageSource) throws UserException {
		Connection conn = null;
		String currentPassDB = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			currentPassDB = UserDB.getPassword(user.getId(), conn);

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (!oldpass.equals(currentPassDB)) {
			// 現在のパスワード不一致
			MessageSourceUtil.addAttribute(errors, "currentPasswordMissmatch",
					messageSource.getMessage("chgpasswd.missmatch",
							new Object[] { "" + prop.getValue("chgpasswd.name.oldpasswd.jp"),
									"" + prop.getValue("chgpasswd.name.oldpasswd.en") },
							null));
			throw new UserException(errors.toString());
		}
	}

	/**
	 * パスワード整合性チェック
	 *
	 * @param errors
	 * @param prop
	 * @throws UserException
	 */
	private void checkPasswordIntegrity(Model errors, ProfileString prop, MessageSource messageSource) throws UserException {

		HashMap<String, String> passwdDefMap = null;

		try {
			// パスワード定義ファイルから値取得
			UserDef userdef = new UserDef();
			passwdDefMap = userdef.getPasswdDefinition(errors);

		} catch (FileNotFoundException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for MUR
			category.error("パスワード定義ファイル取得に失敗\n" + ErrorUtility.error2String(e));
			throw new UserException(e);

		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo", new Object[] { e.getMessage() }, null));
			// for MUR
			category.error("パスワード定義ファイル取得に失敗\n" + ErrorUtility.error2String(e));
			throw new UserException(e);
		}

		// パスワード最小桁数チェック
		int pwdMinLen = Integer.parseInt(passwdDefMap.get(UserDef.PWD_MIN_LEN));
		category.debug("pwdMinLen=" + pwdMinLen);

		if (newpass.length() < pwdMinLen) {
			// パスワード最小桁数不足
			MessageSourceUtil.addAttribute(errors, "MissingDigits",
					messageSource.getMessage("chgpasswd.failed.missingdigits.passwd", new Object[] { "" + pwdMinLen },
							null));
		}

		List<String> strList = new ArrayList<>();

		// パスワード組合せ制約チェック
		String pwdValRole = passwdDefMap.get(UserDef.PWD_VAL_ROLE);
		category.debug("pwdValRole=" + pwdValRole);

		boolean isPasswdComb = true;

		char[] roleChars = pwdValRole.toCharArray();
		for (int i = 0; i < roleChars.length; i++) {
			char c = roleChars[i];

			// A: 大文字英字
			if (c == '\u0041') {
				strList.add(prop.getValue("chgpasswd.constraints.Uppercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0041' <= pass && pass <= '\u005a') { // 大文字 A - Z
						// チェックOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// チェックNG
					isPasswdComb = false;
				}
			}
			// a: 小文字英字
			else if (c == '\u0061') {
				strList.add(prop.getValue("chgpasswd.constraints.Lowercase"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0061' <= pass && pass <= '\u007a') { // 小文字 a - z
						// チェックOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// チェックNG
					isPasswdComb = false;
				}
			}
			// 1: 数字
			else if (c == '\u0031') {
				strList.add(prop.getValue("chgpasswd.constraints.Number"));

				char[] newPassChars = newpass.toCharArray();
				int j = 0;
				for (j = 0; j < newPassChars.length; j++) {
					char pass = newPassChars[j];
					if ('\u0030' <= pass && pass <= '\u0039') { // 数字 0 - 9
						// チェックOK
						break;
					}
				}

				if (j >= newPassChars.length) {
					// チェックNG
					isPasswdComb = false;
				}
			}
			// K: 記号
			else if (c == '\u004b') {
				String symbols = prop.getValue("chgpasswd.input.allowedSymbols");
				String cons = prop.getValue("chgpasswd.constraints.Symbol");
				strList.add(MessageFormat.format(cons, symbols));

				boolean chkFlg = false;
				// 2バイト文字または入力可能な記号以外は入力禁止
				char[] passChars = newpass.toCharArray();
				char[] symbolChars = symbols.toCharArray();

				for (char pass : passChars) {
					if ('\u0041' <= pass && pass <= '\u005a' // 大文字 A - Z
							|| '\u0061' <= pass && pass <= '\u007a' // 小文字 a - z
							|| '\u0030' <= pass && pass <= '\u0039') { // 数字 0 - 9
						// 次の文字へ
						continue;
					}
					// 記号チェック
					for (char s : symbolChars) {
						if (s == pass) {
							// 入力可能な記号
							chkFlg = true;
							break;
						}
					}
				}

				if (!chkFlg) {
					// 記号無し
					// チェックNG
					isPasswdComb = false;
				}
			}
		}

		if (isPasswdComb == false) {
			// パスワード組合せ制約違反
			MessageSourceUtil.addAttribute(errors, "FailedPasswordIntegrity",
					messageSource.getMessage("chgpasswd.failed.combination.passwd", new Object[] { strList.toString() },
							null));
			throw new UserException(strList.toString());
		}
	}

	// --------------------------------------------------------- getter,setter
	/**
	 * Returns the oldpasswd.
	 * @return String
	 */
	public String getOldpass() {
		return oldpass;
	}

	/**
	 * Set the oldpasswd.
	 * @param oldpasswd The passwd to set
	 */
	public void setOldpass(String oldpasswd) {
		oldpass = oldpasswd;
	}

	/**
	 * Returns the newpass.
	 * @return String
	 */
	public String getNewpass() {
		return newpass;
	}

	/**
	 * Set the newpass.
	 * @param newpass The passwd to set
	 */
	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}

	/**
	 * Returns the newpass.
	 * @return String
	 */
	public String getNewPassConfirm() {
		return newPassConfirm;
	}

	/**
	 * Set the newpass.
	 * @param newpass The passwd to set
	 */
	public void setNewPassConfirm(String newPassConfirm) {
		this.newPassConfirm = newPassConfirm;
	}
}
