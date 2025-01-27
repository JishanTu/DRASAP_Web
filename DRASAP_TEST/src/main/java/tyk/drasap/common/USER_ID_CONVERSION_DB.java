package tyk.drasap.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * プリンターリスト(PRINTER_LIST)と対応したクラス。
 */
public class USER_ID_CONVERSION_DB {
	/**
	 * 外部ユーザＩＤからDRASAPユーザＩＤを求める。
	 * @param user_id_col	ユーザID変換テーブルのカラム名
	 * @param ext_user_id	外部ユーザＩＤ
	 * @param conn
	 * @return				DRASAPユーザＩＤ
	 * @throws Exception
	 */
	public static String getDrasapUserId(String user_id_col, String ext_user_id, Connection conn)
			throws Exception {
		String drasapUserId = null;
		// チェック
		if (user_id_col == null || user_id_col.length() == 0) {
			throw new UserException("user_id_col is null");
		}
		if (ext_user_id == null || ext_user_id.length() == 0) {
			throw new UserException("ext_user_id is null");
		}
		ext_user_id = ext_user_id.trim();
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// プリンタIDで検索する
			StringBuilder sbSql1 = new StringBuilder("select user_id from USER_ID_CONVERSION");
			sbSql1.append(" where TRIM(" + user_id_col + ")='" + ext_user_id + "'");
			//
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if (!rs1.next()) {
				throw new UserException(user_id_col + "." + ext_user_id + " undefined");
			}
			drasapUserId = rs1.getString("user_id");

			if (drasapUserId == null || drasapUserId.length() == 0) {
				throw new UserException(user_id_col + "." + ext_user_id + " undefined");
			}

		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE処理
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
		//
		return drasapUserId;
	}

}
