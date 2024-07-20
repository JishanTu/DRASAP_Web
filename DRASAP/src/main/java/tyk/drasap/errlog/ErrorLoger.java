package tyk.drasap.errlog;

import org.apache.log4j.Category;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;
/**
 * エラーログを出力するクラス。
 * どこからでも共通に使用される。
 */
public class ErrorLoger {
	private static Category category = Category.getInstance(ErrorLoger.class.getName());
	
	// ------------------------------------------- Method
	/**
	 * DRASAPシステムとしてのエラーログを出力する。
	 */
	public static void error(User user, Object screenObj, String errorId){
		// 「YYMMDDhhmmss,」については
		// "log4j.properties"にパターン登録されている
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// ホスト名
		buff.append(',');
		buff.append(user.getId());// ユーザーID
		buff.append(',');
		buff.append(user.getName());// 名前
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// 画面ID
		buff.append(',');
		buff.append(errorId);// エラーID
		// Log4jでは、infoとして出力する
		category.info(buff.toString());
	}
	/**
	 * DRASAPシステムとしてのエラーログを出力する。
	 */
	public static void error(User user, Object screenObj, String errorId, String sysId){
		// 「YYMMDDhhmmss,」については
		// "log4j.properties"にパターン登録されている
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// ホスト名
		buff.append(',');
		buff.append(user.getId());// ユーザーID
		buff.append(',');
		buff.append(user.getName());// 名前
		buff.append(',');
		buff.append(DrasapPropertiesFactory.getScreenId(screenObj));// 画面ID
		buff.append(',');
		buff.append(errorId);// エラーID
		if(sysId!=null){
			buff.append(',');
			buff.append(sysId);
		}
		// Log4jでは、infoとして出力する
		category.info(buff.toString());
	}

}
