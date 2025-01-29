package tyk.drasap.errlog;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;

/**
 * エラーログを出力するクラス。
 * どこからでも共通に使用される。
 */
public class ErrorLoger {
	private static Logger category = Logger.getLogger(ErrorLoger.class.getName());

	// ------------------------------------------- Method
	/**
	 * DRASAPシステムとしてのエラーログを出力する。
	 */
	public static void error(User user, Object screenObj, String errorId) {
		error(user, screenObj, errorId, null);
	}

	/**
	 * DRASAPシステムとしてのエラーログを出力する。
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId) {
		error(user, screenObj, errorId, sysId, null);
	}

	/**
	 * DRASAPシステムとしてのエラーログを出力する。
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId, String extuserid) {
		String userId = user.getId();
		if (StringUtils.isEmpty(userId)) {
			userId = StringUtils.isEmpty(extuserid) ? "" : extuserid;
		}
		// 「YYMMDDhhmmss,」については
		// "log4j.properties"にパターン登録されている
		StringBuilder buff = new StringBuilder();
		buff.append(user.getHost());// ホスト名
		buff.append(',');
		buff.append(userId);// ユーザーID
		buff.append(',');
		buff.append(user.getName());// 名前
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// 画面ID
		buff.append(',');
		buff.append(errorId);// エラーID
		if (sysId != null) {
			buff.append(',');
			buff.append(sysId);
		}
		// Log4jでは、infoとして出力する
		category.info(buff.toString());
	}

}
