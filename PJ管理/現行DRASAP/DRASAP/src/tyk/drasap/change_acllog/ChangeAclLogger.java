package tyk.drasap.change_acllog;

import static tyk.drasap.common.DrasapUtil.defaultString;

import org.apache.log4j.Category;

import tyk.drasap.common.AclUpload;
import tyk.drasap.common.User;
/**
 * <PRE>
 * アクセスレベル変更ログを出力するクラス。
 * どこからでも共通に使用される。
 * </PRE>
 * @author 2013/07/11 yamagishi
 */
public class ChangeAclLogger {
	private static Category category = Category.getInstance(ChangeAclLogger.class.getName());

	// ------------------------------------------- Method
	/**
	 * DRASAPシステムとしてのアクセスレベル変更ログを出力する。
	 * @param user ユーザー
	 * @param aclUpdateNo 管理NO
	 * @param drwgNo 図番
	 * @param preUpdateAc 変更前アクセスレベル
	 * @param preUpdateAclName 変更前アクセスレベル名
	 * @param postUpdateAcl 変更後アクセスレベル
	 * @param postUpdateAclName 変更後アクセスレベル名
	 * @param message メッセージ
	 */
	public static void logging(User user, String aclUpdateNo, String drwgNo,
			String preUpdateAcl, String preUpdateAclName, String postUpdateAcl, String postUpdateAclName, String message) {
		// メッセージを作成し、
		// Log4jでは、infoとして出力する
		category.info(createLogMsg(user, aclUpdateNo, drwgNo, preUpdateAcl, preUpdateAclName, postUpdateAcl, postUpdateAclName, message));
	}

	/**
	 * DRASAPシステムとしてのアクセスレベル変更ログを出力する。
	 * @param user ユーザー
	 * @param aclUpload ACLアップロードデータテーブルのオブジェクト
	 */
	public static void logging(User user, AclUpload aclUpload) {
		// メッセージを作成し、
		// Log4jでは、infoとして出力する
		String aclUpdateNo = aclUpload.getAclUpdateNo();
		String drwgNo = aclUpload.getItemNoShort();
		String preUpdateAcl = aclUpload.getPreUpdateAcl();
		String preUpdateAclName = aclUpload.getPreUpdateAclName();
		String postUpdateAcl = aclUpload.getPostUpdateAcl();
		String postUpdateAclName = aclUpload.getPostUpdateAclName();
		String message = aclUpload.getMessage();
		category.info(createLogMsg(user, aclUpdateNo, drwgNo, preUpdateAcl, preUpdateAclName, postUpdateAcl, postUpdateAclName, message));
	}

	/**
	 * アクセスレベル変更ログのメッセージを作成する。
	 * 「YYMMDDhhmmss,」については
	 * "log4j.properties"にパターン登録されている。
	 * @param aclUpdateNo 管理NO
	 * @param user ユーザー
	 * @param drwgNo 図番
	 * @param preUpdateAcl 変更前アクセスレベル
	 * @param preUpdateAclName 変更前アクセスレベル名
	 * @param postUpdateAcl 変更後アクセスレベル
	 * @param postUpdateAclName 変更後アクセスレベル名
	 * @param message メッセージ
	 * @return ロギングのためのメッセージ
	 */
	private static String createLogMsg(User user, String aclUpdateNo, String drwgNo,
			String preUpdateAcl, String preUpdateAclName, String postUpdateAcl, String postUpdateAclName, String message) {

		// 「YYMMDDhhmmss,」については
		// "log4j.properties"にパターン登録されている
		StringBuffer buff = new StringBuffer();
		buff.append(aclUpdateNo);			// 管理No
		buff.append(',');
		buff.append(user.getId());			// ユーザーID
		buff.append(',');
		buff.append(user.getName());		// 氏名
		buff.append(',');
		buff.append(drwgNo);				// 図番
		buff.append(',');
		String procedure = (message != null && message.length() > 0) ? "無し" : "更新";
		buff.append(procedure);				// 処置
		buff.append(',');
		if (preUpdateAcl != null && preUpdateAcl.length() > 0) {
			buff.append(preUpdateAcl);		// 変更前アクセスレベル
		}
		if (preUpdateAclName != null && preUpdateAclName.length() > 0) {
			buff.append('（');
			buff.append(preUpdateAclName);	// 変更前アクセスレベル名
			buff.append('）');
		}
		buff.append(',');
		if (postUpdateAcl != null && postUpdateAcl.length() > 0) {
			buff.append(postUpdateAcl);		// 変更後アクセスレベル
		}
		if (postUpdateAclName != null && postUpdateAclName.length() > 0) {
			buff.append('（');
			buff.append(postUpdateAclName);	// 変更後アクセスレベル名
			buff.append('）');
		}
		buff.append(',');
		buff.append(defaultString(message)); // メッセージ
		return buff.toString();
	}
}
