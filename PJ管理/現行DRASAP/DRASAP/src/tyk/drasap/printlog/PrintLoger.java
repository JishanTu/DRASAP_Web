package tyk.drasap.printlog;

import org.apache.log4j.Category;

import tyk.drasap.common.User;

/**
 * <PRE>
 * 参考図出力に関してのロギングするクラス。
 * 過去に何度か重複して、PRINT_REQUEST_TABLEに書き込まれたことがあり、
 * それに対する原因追求するための情報収集を目的とする。
 * </PRE>
 * @author fumi
 * 作成日 2005/03/04
 * 変更日 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class PrintLoger {
	private static Category category = Category.getInstance(PrintLoger.class.getName());
	// メッセージの種類
	/** クライアントからのリクエストを受けたタイミング */
	public static String ACT_RECEIVE =	"Receive   ";
	/** Oracleのテーブルに書き出すタイミング */
	public static String ACT_WRITE =		"Write     ";
	/** Oracleのテーブルの書き出しに失敗したタイミング */
	public static String FAILED_WRITE =	"Failed    ";

// 2013.06.24 yamagishi modified. start
//	public static void info(String act, String userId){
//		info(act, userId, null);
//	}
//	/**
//	 * <PRE>
//	 * ロギングする。
//	 * Log4Jでは、infoレベルで出力する
//	 * </PRE>
//	 * @param act 何が起きたか?
//	 * @param userId ユーザーID
//	 * @param drwgNo 図番。nullも可
//	 */
//	public static void info(String act, String userId, String drwgNo){
//		// Log4Jでは、infoレベルで出力する
//		category.info(createMessage(act, userId, drwgNo));
//	}
//	/**
//	 * <PRE>
//	 * ロギングするメッセージを作成する。
//	 * YYMMDDHHMMSSについては、log4j.propertiesで定義する。
//	 * </PRE>
//	 * @param act 何が起きたか?
//	 * @param userId ユーザーID
//	 * @param drwgNo 図番。nullも可
//	 * @return ロギングするメッセージ
//	 */
//	private static String createMessage(String act, String userId, String drwgNo){
//		StringBuffer sbMsg = new StringBuffer(act);// 何が起きたか?
//		sbMsg.append(',');
//		sbMsg.append(userId);// ユーザーID
//		// 図番が指定されていたら、図番もロギングする
//		if(drwgNo!=null){
//			sbMsg.append(',');
//			sbMsg.append(drwgNo);// 図番
//		}
//
//		return sbMsg.toString();
//	}
	public static void info(String act, User user){
		info(act, user, null);
	}
	/**
	 * <PRE>
	 * ロギングする。
	 * Log4Jでは、infoレベルで出力する
	 * </PRE>
	 * @param act 何が起きたか?
	 * @param user ユーザー
	 * @param drwgNo 図番。nullも可
	 */
	public static void info(String act, User user, String drwgNo){
		// Log4Jでは、infoレベルで出力する
		category.info(createMessage(act, user, drwgNo));
	}
	/**
	 * <PRE>
	 * ロギングするメッセージを作成する。
	 * YYMMDDHHMMSSについては、log4j.propertiesで定義する。
	 * </PRE>
	 * @param act 何が起きたか?
	 * @param user ユーザー
	 * @param drwgNo 図番。nullも可
	 * @return ロギングするメッセージ
	 */
	private static String createMessage(String act, User user, String drwgNo){
		StringBuffer sbMsg = new StringBuffer(act);// 何が起きたか?
		sbMsg.append(',');
		sbMsg.append(user.getId());// ユーザーID
		sbMsg.append(',');
		sbMsg.append(user.getDept());// 部署コード
		sbMsg.append(',');
		sbMsg.append(user.getDeptName());// 部署名
		// 図番が指定されていたら、図番もロギングする
		if(drwgNo!=null){
			sbMsg.append(',');
			sbMsg.append(drwgNo);// 図番
		}

		return sbMsg.toString();
	}
// 2013.06.24 yamagishi modified. end

}
