package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;

/**
 * ユーザーマスターと対応したDBクラス。
 * @version 2013/09/17 yamagishi
 */
public class UserDB {
	private static Logger category = Logger.getLogger(UserDB.class.getName());

	/** パスワード最大桁数 */
	public static final int PASSWORD_MAX_LENGTH = 20;

	/**
	 * 取得したユーザー情報を、userに付加する。
	 * パスワード不要。ポータルからのSysPassチェックした場合に使用する。
	 * 成功すれば trueを返す。
	 * ID,パスワードの比較では大文字小文字を区別しない。
	 * @param user
	 * @param id
	 * @param conn
	 * @return true=成功。false=IDまたはパスワードが正しくない。
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, Connection conn)
			throws Exception {
		// パスワードチェックしない。
		// ダミーパスワードとして "" を渡す。
		return addUserInfo(user, id, "", false, conn);
	}

	/**
	 * 取得したユーザー情報を、userに付加する。
	 * 成功すれば trueを返す。
	 * ID,パスワードの比較では大文字小文字を区別しない。
	 * @param user
	 * @param id
	 * @param passwd
	 * @param conn
	 * @return true=成功。false=IDまたはパスワードが正しくない。
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, String passwd, Connection conn)
			throws Exception {
		// パスワードチェックする
		return addUserInfo(user, id, passwd, true, conn);
	}

	/**
	 * パスワードの有効期限チェック
	 * @param user
	 * @param errors
	 * @param conn
	 * @return　 0: 変更無 <br/> 1: 変更有(パスワードがユーザID) <br/> 2: 変更有(有効期限切れ)
	 * @throws Exception
	 */
	public static int checkPasswordExpiry(User user, Model errors, Connection conn) throws Exception {
		String userId = user.getId();
		String currentPass = UserDB.getPassword(userId, conn);

		if (StringUtils.isBlank(currentPass) || userId.equals(currentPass)) {
			// パスワード未設定 もしくは パスワードがユーザIDと同じ
			return 1;
		}

		/*
		 *  パスワード有効期限の確認
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar passwdUpCalendar = Calendar.getInstance();

		// パスワード定義ファイルから値取得
		UserDef userdef = new UserDef();
		HashMap<String, String> passwdDefMap = userdef.getPasswdDefinition(errors);

		// パスワード有効期限日数チェック
		int pwdLmtDay = Integer.parseInt(passwdDefMap.get(UserDef.PWD_LMT_DAY));

		// 現在日時の取得
		Calendar nowCal = Calendar.getInstance();

		// 時分秒をクリア
		nowCal.clear(Calendar.MINUTE);
		nowCal.clear(Calendar.SECOND);
		nowCal.clear(Calendar.MILLISECOND);
		nowCal.set(Calendar.HOUR_OF_DAY, 0);

		// パスワード設定日取得
		Date pwdUpDate = user.getPasswdUpdDate();

		if (pwdUpDate == null) {
			// パスワード設定日が未設定の場合は再設定対象
			return 2;
		}

		passwdUpCalendar.setTime(pwdUpDate); // DATE -> Calendar

		// パスワード設定日 + 有効期限日数
		passwdUpCalendar.add(Calendar.DATE, pwdLmtDay);

		Date passwdLimitDate = passwdUpCalendar.getTime(); // Calendar -> DATE
		Date nowDate = nowCal.getTime(); // Calendar -> DATE

		category.debug("Now Date=" + sdf.format(nowDate));
		category.debug("Password Limit Date=" + sdf.format(passwdLimitDate));

		// パスワード設定日 + 有効期限日数 < 現在日時の場合はパスワード変更
		if (nowDate.after(passwdLimitDate)) {
			return 2;
		}

		return 0;
	}

	/**
	 * 取得したユーザー情報を、userに付加する。
	 * 成功すれば trueを返す。
	 * IDの比較では大文字小文字を区別しない。
	 * 2019.12.06 yamamoto ログイン画面新設対応でパスワードは大文字小文字区別するように対応
	 * @param user
	 * @param id
	 * @param passwd
	 * @param checkPswd パスワードをチェックするなら、true。チェックしないならfalse。
	 * @param conn
	 * @return true=成功。false=IDまたはパスワードが正しくない。
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, String passwd, boolean checkPswd, Connection conn)
			throws Exception {
		// idを大文字に
		id = id.toUpperCase();
		//		passwd = passwd.toUpperCase();
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// idで検索を行う
			// そこでStatementに変更。 '04.Mar.2 Hirata
			// 発効、失効を見るように変更
			pstmt = conn.prepareStatement("select *" +
					" from USER_MASTER, DEPARTMENT_MASTER" +
					" where USER_MASTER.DEPT_CODE=DEPARTMENT_MASTER.DEPT_CODE(+)" +
					" and TRIM(USER_ID)=?" +
					// 発効日・・・nullまたは今日以前(今日を含む)
					" and (VALID_DATE is null or TRUNC(VALID_DATE) <= TRUNC(sysdate))" +
					// 失効日・・・nullまたは今日より後(今日は含まない)
					" and (EXPIRED_DATE is null or TRUNC(sysdate) < TRUNC(EXPIRED_DATE))");
			pstmt.setString(1, id.trim());
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				// ユーザーIDが一致しない
				return false;
			}
			String passwd_M = rs.getString("PASSWD");
			// パスワードチェックが不要、またはパスワードが一致したら
			// 変更 '04/04/13 by Hirata
			if (checkPswd && !passwd.equals(passwd_M)) {
				// パスワードが一致しない
				return false;
			}
			user.setId(id);
			user.setName(rs.getString("USER_NAME"));
			user.setNameE(rs.getString("ALPH_NAME"));
			user.setDept(rs.getString("DEPT_CODE"));
			user.setDeptName(rs.getString("DEPARTMENT"));
			// 検索条件カラムリストを設定
			for (int i = 1; i <= User.searchSelColNum; i++) {
				user.setSearchSelCol(i - 1, rs.getString("SEARCH_SELCOL" + i));
			}
			// 検索結果カラムリストを設定
			for (int j = 1; j <= User.viewSelColNum; j++) {
				user.setViewSelCol(j - 1, rs.getString("VIEW_SELCOL" + j));
			}
			user.setOnlyNewestFlag(rs.getString("LATEST_REV_DISP_FLAG"));
			user.setDisplayCount(rs.getString("DISPLAY_COUNT"));
			user.setAdminFlag(rs.getString("ADMIN_FLAG"));// 管理者フラグ
			user.setPosition(rs.getString("POSITION"));// 職位
			// 2013.07.24 yamagishi add. start
			user.setAclUpdateFlag(rs.getString("ACL_UPDATE_FLAG")); // アクセスレベル変更許可フラグ
			user.setAclBatchUpdateFlag(rs.getString("ACL_BATCH_UPDATE_FLAG")); // アクセスレベル一括更新ツール許可フラグ
			user.setDlManagerFlag(rs.getString("DL_MANAGER_FLAG")); // ＤＬマネージャ利用可能フラグ
			// 2013.07.24 yamagishi add. end
			// 2019.09.20 yamamoto add. start
			user.setPasswdUpdDate(rs.getDate("PASSWD_UPD_DATE")); // パスワード設定日
			user.setReproUserFlag(rs.getString("REPRO_USER_FLAG")); // 原図庫ユーザフラグ
			user.setDwgRegReqFlag(rs.getString("DWG_REG_REQ_FLAG")); // 図面登録依頼フラグ
			// 2019.09.20 yamamoto add. end
			// 2020.02.10 yamamoto add. start
			user.setMultiPdfFlag(rs.getString("MULTI_PDF_FLAG")); // マルチPDF出力許可フラグ
			// 2020.02.10 yamamoto add. end
			String defaultUserGroup = rs.getString("USER_GRP_CODE");// 原価部門の利用者グループ
			user.setDefaultUserGroup(defaultUserGroup);
			user.setThumbnailSize(rs.getString("THUMBNAIL_SIZE"));
			user.setResultDispMode(rs.getString("RESULT_DISP_MODE"));
			// 利用可能な全ての利用者グループを取得
			ArrayList<String> userGroupCodeArray = new ArrayList<String>();
			if (defaultUserGroup != null) {// 原価部門の利用者グループ
				userGroupCodeArray.add(defaultUserGroup);
			}
			if (rs.getString("USER_GRP_CODE01") != null) {// ユーザーマスタの利用者グループ01
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE01"));
			}
			if (rs.getString("USER_GRP_CODE02") != null) {// ユーザーマスタの利用者グループ02
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE02"));
			}
			if (rs.getString("USER_GRP_CODE03") != null) {// ユーザーマスタの利用者グループ03
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE03"));
			}
			if (rs.getString("USER_GRP_CODE04") != null) {// ユーザーマスタの利用者グループ04
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE04"));
			}
			if (rs.getString("USER_GRP_CODE05") != null) {// ユーザーマスタの利用者グループ05
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE05"));
			}
			if (rs.getString("USER_GRP_CODE06") != null) {// ユーザーマスタの利用者グループ06
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE06"));
			}
			if (rs.getString("USER_GRP_CODE07") != null) {// ユーザーマスタの利用者グループ07
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE07"));
			}
			if (rs.getString("USER_GRP_CODE08") != null) {// ユーザーマスタの利用者グループ08
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE08"));
			}
			if (rs.getString("USER_GRP_CODE09") != null) {// ユーザーマスタの利用者グループ09
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE09"));
			}
			if (rs.getString("USER_GRP_CODE10") != null) {// ユーザーマスタの利用者グループ10
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE10"));
			}
			// 2013.09.17 yamagishi add. start
			while (rs.next()) {
				// ユーザが複数の原価部門に所属している場合
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE"));// 原価部門の利用者グループ
			}
			// 2013.09.17 yamagishi add. end
			ArrayList<UserGroup> userGroupes = UserGroupDB.getUserGroupArray(userGroupCodeArray, conn);
			// 取得した利用者グループを、userにaddしていく
			for (int i = 0; i < userGroupes.size(); i++) {
				user.addUserGroup(userGroupes.get(i));
			}

			return true;
		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE処理
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * DBからパスワード取得する
	 *
	 * @param id
	 * @param conn
	 * @return 正常：パスワード文字列 <br>
	 *         異常：null/ユーザIDが無効 <br> 空文字/パスワード未設定
	 * @throws Exception
	 */
	public static String getPassword(String id, Connection conn) throws Exception {

		String passwd = null;

		// idを大文字に
		id = id.toUpperCase();
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// idで検索を行う
			// 発効、失効を見る
			pstmt = conn.prepareStatement("select *" +
					" from USER_MASTER, DEPARTMENT_MASTER" +
					" where USER_MASTER.DEPT_CODE=DEPARTMENT_MASTER.DEPT_CODE(+)" +
					" and TRIM(USER_ID)=?" +
					// 発効日・・・nullまたは今日以前(今日を含む)
					" and (VALID_DATE is null or TRUNC(VALID_DATE) <= TRUNC(sysdate))" +
					// 失効日・・・nullまたは今日より後(今日は含まない)
					" and (EXPIRED_DATE is null or TRUNC(sysdate) < TRUNC(EXPIRED_DATE))");
			pstmt.setString(1, id.trim());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				passwd = rs.getString("PASSWD");

				// パスワードが未設定の場合は空文字を設定する
				if (passwd == null) {
					passwd = "";
				}
			}

		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE処理
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}

		return passwd;
	}

	/**
	 * ユーザマスタ更新
	 *
	 * @param id
	 * @param column
	 * @param value
	 * @param conn
	 * @throws Exception
	 */
	public static void updateUserMaster(String id, String column, String value, Connection conn) throws Exception {
		// idを大文字に
		id = id.toUpperCase();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// ユーザマスタ更新
			stmt = conn.createStatement();

			// SQL文を組み立てる
			String sql = "update USER_MASTER" +
					" set " + column + "='" + value.trim() + "'";

			if ("PASSWD".equals(column)) {
				// パスワード更新する
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date nowDate = new Date();

				sql = sql + "     ,PASSWD_UPD_DATE=TO_DATE('" + sdf.format(nowDate) + "','YYYY/MM/DD')";
				category.debug("nowdate:" + sdf.format(nowDate));
			}
			sql = sql + " where USER_ID='" + id.trim() + "'";

			category.debug("SQL:" + stmt.toString());
			stmt.executeUpdate(sql);

			// コミット
			conn.commit();
		} catch (Exception e) {
			// ロールバック
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			// CLOSE処理
			try {
				if (Objects.nonNull(rs)) {
					rs.close();
				}
			} catch (Exception e) {
			}
			try {
				if (Objects.nonNull(stmt)) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
