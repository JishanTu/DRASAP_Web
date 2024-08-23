package tyk.drasap.acslog;

import org.apache.log4j.Category;

import tyk.drasap.common.User;
/**
 * <PRE>
 * アクセスログを出力するクラス。
 * どこからでも共通に使用される。
 * '04.Nov.23 図番もログするのに対応
 * 2005-Mar-4 システムIDに対応
 * </PRE>
 * 最終変更 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class AccessLoger {
	private static Category category = Category.getInstance(AccessLoger.class.getName());
	// ロギングで使用するFunctionID
	public static String FID_SEARCH		= "01";// 検索
	public static String FID_DISP_DRWG		="02";// 図面表示
	public static String FID_OUT_DRWG		= "03";// 出力
	public static String FID_GENZ_REQ		= "04";// 原図庫作業依頼
	public static String FID_GENZ_RES		= "05";// 原図庫作業完了入力
	public static String FID_CHG_ACL		= "06";// アクセス権変更
	public static String FID_SAVE			= "07";// 保存		// 2013.07.11 yamagishi add.
	public static String FID_DEL_DRWG		= "99";// 図番削除

	// ------------------------------------------- Method
	/**
	 * DRASAPシステムとしてのアクセスログを出力する。
	 * @param user ユーザー
	 * @param functionId 機能ID
	 */
	public static void loging(User user, String functionId){
		// メッセージを作成し、
		// Log4jでは、infoとして出力する
		category.info(createLogMsg(user, functionId, null, null));
	}
	/**
	 * DRASAPシステムとしてのアクセスログを出力する。
	 * @param user ユーザー
	 * @param functionId 機能ID
	 * @param drwgNo 図番 ハイフン抜きで
	 */
	public static void loging(User user, String functionId, String drwgNo){
		// メッセージを作成し、
		// Log4jでは、infoとして出力する
		category.info(createLogMsg(user, functionId, drwgNo, null));
	}
	/**
	 * DRASAPシステムとしてのアクセスログを出力する。
	 * @param user ユーザー
	 * @param functionId 機能ID
	 * @param drwgNo 図番 ハイフン抜きで
	 * @param sysId システムID。null可。
	 */
	public static void loging(User user, String functionId, String drwgNo, String sysId){
		// メッセージを作成し、
		// Log4jでは、infoとして出力する
		category.info(createLogMsg(user, functionId, drwgNo, sysId));
	}
	/**
	 * DRASAPシステムとしてのアクセスログを出力する。
	 * 配列の長さだけロギングする。
	 * @param user ユーザー
	 * @param functionId 機能ID
	 * @param drwgNoArray 図番 ハイフン抜きで
	 */
	public static void loging(User user, String functionId, String[] drwgNoArray){
		// 配列の長さだけロギングする。
		for(int i = 0; i < drwgNoArray.length; i++){
			loging(user, functionId, drwgNoArray[i]);
		}
	}
	public static void loging(User user, String functionId, String[] drwgNoArray, String sysId){
		// 配列の長さだけロギングする。
		for(int i = 0; i < drwgNoArray.length; i++){
			loging(user, functionId, drwgNoArray[i], sysId);
		}
	}
	/**
	 * アクセスログのメッセージを作成する。
	 * 「YYMMDDhhmmss,」については
	 * "log4j.properties"にパターン登録されている。
	 * @param user ユーザー
	 * @param functionId 機能ID
	 * @param drwgNo 図番。null可。
	 * @param sysId システムID。null可。2005-Mar-4追加。他のシステムからの呼び出しに対応するため。
	 * @return ロギングのためのメッセージ
	 */
	private static String createLogMsg(User user, String functionId, String drwgNo, String sysId){
		// 「YYMMDDhhmmss,」については
		// "log4j.properties"にパターン登録されている
		StringBuffer buff = new StringBuffer();
		buff.append(user.getHost());// ホスト名
		buff.append(',');
		buff.append(user.getId());// ユーザーID
		buff.append(',');
		buff.append(user.getName());// 名前
		buff.append(',');
// 2013.06.24 yamagishi add. start
		buff.append(user.getDeptName());// 部署名
		buff.append(',');
// 2013.06.24 yamagishi add. end
		buff.append(functionId);// 機能ID
		// drwgNoがある場合は、最後につける
		if(drwgNo!=null){
			buff.append(',');
			buff.append(drwgNo);
			// sysIdがある場合は、さらに付加
			// 2005-Mar-4変更 by Hirata.
			if(sysId!=null){
				buff.append(',');
				buff.append(sysId);
			}
		}
		return buff.toString();
	}

}
