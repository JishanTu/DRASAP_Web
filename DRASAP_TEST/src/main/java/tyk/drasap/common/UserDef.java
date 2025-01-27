package tyk.drasap.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareConcurrentModel;

import tyk.drasap.springfw.utils.MessageSourceUtil;

@Component
public class UserDef {

	private static Logger category = Logger.getLogger(UserDef.class.getName());

	/** パスワード最小桁数 */
	public static final String PWD_MIN_LEN = "PWD_MIN_LEN";
	/** パスワード組合せ制約 */
	public static final String PWD_VAL_ROLE = "PWD_VAL_ROLE";
	/** パスワード有効期限日数 */
	public static final String PWD_LMT_DAY = "PWD_LMT_DAY";

	/** パスワード最小桁数(デフォルト) */
	private static final int DEFALUT_PWD_MIN_LEN = 4;
	/** パスワード組合せ制約(デフォルト) */
	private static final String DEFALUT_PWD_VAL_ROLE = "";
	/** パスワード有効期限日数(デフォルト) */
	private static final int DEFALUT_PWD_LMT_DAY = 120;

	public UserDef() {
	}

	/**
	 * メッセージファイルの読み込み
	 *
	 * @param Filepath
	 * @param charSet
	 * @return
	 */
	public String loadMessage(String Filepath, Charset charSet) {
		String str = "";
		List<String> lines = null;
		Path path = null;
		StringBuilder sb = null;

		try {
			String apServerHome = DrasapPropertiesFactory.getFullPath("");
			path = Paths.get(apServerHome + Filepath);
			lines = Files.readAllLines(path, charSet);

			// 行区切りでカンマが画面に表示されるため、除去する
			sb = new StringBuilder();
			for (String s : lines) {
				// 行の終端に改行コード追加
				sb.append(s + "&#010;");
			}
			str = sb.toString();

		} catch (NoSuchFileException e) {
			category.error(e);
		} catch (IOException e) {
			category.error(e);
		} catch (Exception e) {
			category.error(e);
		}

		return str;
	}

	/**
	 * パスワード定義ファイルから値取得
	 * <br>戻り値は変換可能な値を返す
	 *
	 * @param  errors
	 * @return HashMap
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public HashMap<String, String> getPasswdDefinition(Model errors) throws FileNotFoundException, IOException {
		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		String passwdDefFile = DrasapPropertiesFactory.getFullPath("tyk.passwddef.passwd.path");
		int pwdMinLen = DEFALUT_PWD_MIN_LEN; // パスワード最小桁数
		String pwdValRole = DEFALUT_PWD_VAL_ROLE; // パスワード組合せ制約
		int pwdLmtDay = DEFALUT_PWD_LMT_DAY; // パスワード有効期限日数

		HashMap<String, String> passwdDefMap = new HashMap<>();
		BufferedReader reader = null;
		String lineData = null;
		String tmpValue = null;

		try {
			File file = new File(passwdDefFile);
			if (!file.exists()) {
				// ファイルが存在しない場合は空ファイル作成
				file.createNewFile();
			}

			// ファイル読み込み
			reader = new BufferedReader(new FileReader(passwdDefFile));

			while ((lineData = reader.readLine()) != null) {
				if (lineData.startsWith("#") || StringUtils.isBlank(lineData)) {
					continue;
				}
				// パスワード最小桁数
				if (lineData.indexOf(PWD_MIN_LEN + "=") == 0) {
					tmpValue = lineData.replace(PWD_MIN_LEN + "=", "");

					// 未設定または10進数の整数(自然数と0)以外の場合は初期値設定
					if (StringUtils.isNotEmpty(tmpValue) && NumberUtils.isDigits(tmpValue)) {
						try {
							pwdMinLen = Integer.parseInt(tmpValue);
							// パスワード最小桁数が最大桁数を超えないようにする
							if (pwdMinLen > UserDB.PASSWORD_MAX_LENGTH) {
								pwdMinLen = UserDB.PASSWORD_MAX_LENGTH;
							}
						} catch (NumberFormatException e) {
						}
					}
					continue;

				} // パスワード組合せ制約
				if (lineData.indexOf(PWD_VAL_ROLE + "=") == 0) {
					tmpValue = lineData.replace(PWD_VAL_ROLE + "=", "");

					// 未設定の場合は初期値設定
					if (StringUtils.isNotEmpty(tmpValue)) {
						pwdValRole = tmpValue;
					}
					continue;

				} // パスワード有効期限日数
				if (lineData.indexOf(PWD_LMT_DAY + "=") == 0) {
					tmpValue = lineData.replace(PWD_LMT_DAY + "=", "");

					// 未設定または10進数の整数(自然数と0)以外の場合は初期値設定
					if (StringUtils.isNotEmpty(tmpValue) && NumberUtils.isDigits(tmpValue)) {
						try {
							pwdLmtDay = Integer.parseInt(tmpValue);
						} catch (NumberFormatException e) {
						}
					}
					continue;
				}
			}

			passwdDefMap.put(PWD_MIN_LEN, Integer.toString(pwdMinLen));
			passwdDefMap.put(PWD_VAL_ROLE, pwdValRole);
			passwdDefMap.put(PWD_LMT_DAY, Integer.toString(pwdLmtDay));

		} catch (FileNotFoundException e) {
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() }, null));
			category.error(errors.toString());
			throw e;

		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource
					.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			category.error(errors.toString());
			throw e;

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
			}
		}

		return passwdDefMap;
	}

	/**
	 * パスワード制約メッセージの取得
	 *
	 * @return msg
	 */
	public String getPasswordConstraintMessage() {

		String msg = "";

		MessageSource messageSource = MessageSourceUtil.getMessageSource();
		BindingAwareConcurrentModel errors = new BindingAwareConcurrentModel();
		HashMap<String, String> passwdDefMap = null;

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
				throw new UserException(e);

			} catch (IOException e) {
				MessageSourceUtil.addAttribute(errors, "message",
						messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() },
								null));
				category.error(errors.toString());
				throw new UserException(e);
			}

			try {
				// パスワード定義ファイルから値取得
				UserDef userdef = new UserDef();
				passwdDefMap = userdef.getPasswdDefinition(errors);

			} catch (FileNotFoundException e) {
				// for MUR
				category.error("パスワード定義ファイル取得に失敗\n" + ErrorUtility.error2String(e));
				throw new UserException(e);

			} catch (IOException e) {
				// for MUR
				category.error("パスワード定義ファイル取得に失敗\n" + ErrorUtility.error2String(e));
				throw new UserException(e);
			}

			// パスワード組合せ制約チェック
			String pwdValRole = passwdDefMap.get(UserDef.PWD_VAL_ROLE);
			category.debug("pwdValRole=" + pwdValRole);
			category.error("pwdValRole=" + pwdValRole);

			List<String> strList = new ArrayList<>();
			char[] roleChars = pwdValRole.toCharArray();
			for (int i = 0; i < roleChars.length; i++) {
				char c = roleChars[i];

				// A: 大文字英字
				if (c == '\u0041') {
					strList.add(prop.getValue("chgpasswd.constraints.Uppercase"));
				}
				// a: 小文字英字
				else if (c == '\u0061') {
					strList.add(prop.getValue("chgpasswd.constraints.Lowercase"));
				}
				// 1: 数字
				else if (c == '\u0031') {
					strList.add(prop.getValue("chgpasswd.constraints.Number"));
				}
				// K: 記号
				else if (c == '\u004b') {
					String symbols = prop.getValue("chgpasswd.input.allowedSymbols");
					String cons = prop.getValue("chgpasswd.constraints.Symbol");
					strList.add(MessageFormat.format(cons, symbols));
				}
			}

			if (strList.size() != 0) {
				// パスワード組合せ制約が設定されている場合のみメッセージを返す
				String msgText = prop.getValue("chgpasswd.failed.combination.passwd");
				msg = MessageFormat.format(msgText, strList.toString());
			}
		} catch (UserException e) {
			// エラーログ出力済みのため何もしない
		}

		return msg;
	}
}
