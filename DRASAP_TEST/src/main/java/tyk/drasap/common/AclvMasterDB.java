package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * アクセスレベルマスターに対応したDBクラス
 * @author fumi
 * 作成日: 2004/01/20
 * @version 2013/09/13 yamagishi
 */
public class AclvMasterDB {
	// 2013.09.14 yamagishi modified. start
	/**
	 * アクセスレベルマスターからアクセスレベルIDのリストを取得
	 * @param printerIdArray
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getAclvIdList(Connection conn)
			throws Exception {
		ArrayList<String> aclvIdList = new ArrayList<String>();
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			//			StringBuffer sbSql1 = new StringBuffer("select * from ACCESS_LEVEL_MASTER where STATUS='1'");
			//			sbSql1.append(" order by ACL_ID");
			StringBuilder sbSql1 = new StringBuilder("select ACL_ID from ACCESS_LEVEL_MASTER order by ACL_ID");
			//
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				aclvIdList.add(rs1.getString("ACL_ID"));
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
		return aclvIdList;
	}
	// 2013.09.14 yamagishi modified. end

	// 2013.07.13 yamagishi add. start
	/**
	 * 図面のアクセスレベルから、該当図面であるかを判定して返す。
	 * @param drwgNo 図番
	 * @param conn
	 * @return 該当図面ならtrue。それ以外はfalse。
	 * @throws Exception
	 */
	public static boolean isCorresponding(String drwgNo, Connection conn) throws Exception {
		int count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select COUNT(id.DRWG_NO) as COUNT " +
					"from INDEX_DB id " +
					"inner join ACCESS_LEVEL_MASTER alm on alm.ACL_ID = id.ACL_ID " +
					"where id.DRWG_NO = ? and alm.CORRESPONDING_FLAG = '1' and alm.CONFIDENTIAL_FLAG in ('1', '2', '3')";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, drwgNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt("COUNT");
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
		return count > 0;
	}

	/**
	 * 変更前ACL情報を取得し、マップで返す。
	 * @param drwgNo 図番
	 * @param conn
	 * @return accessLevelMap
	 * @throws Exception
	 */
	public static HashMap<String, String> getPreUpdateAcl(String drwgNo, Connection conn) throws Exception {
		HashMap<String, String> accessLevelMap = new HashMap<String, String>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select alm.ACL_ID, alm.ACL_NAME " +
					"from ACCESS_LEVEL_MASTER alm " +
					"inner join INDEX_DB id on id.ACL_ID = alm.ACL_ID " +
					"where id.DRWG_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, drwgNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				accessLevelMap.put("ACL_ID", rs.getString("ACL_ID")); // アクセスレベル
				accessLevelMap.put("ACL_NAME", rs.getString("ACL_NAME")); // アクセスレベル名
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
		return accessLevelMap;
	}

	/**
	 * 変更後ACL情報を取得し、マップで返す。
	 * @param grpCode グループコード
	 * @param correspondingFlag 該当図区分
	 * @param confidentialFlag 機密管理図区分
	 * @param conn
	 * @return accessLevel
	 * @throws Exception
	 */
	public static HashMap<String, String> getPostUpdateAcl(String grpCode, String correspondingFlag, String confidentialFlag,
			Connection conn) throws Exception {

		HashMap<String, String> accessLevelMap = new HashMap<String, String>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ACL_ID, ACL_NAME from ACCESS_LEVEL_MASTER " +
					"where GRP_CODE = ? and CORRESPONDING_FLAG = ? and CONFIDENTIAL_FLAG = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, grpCode);
			pstmt.setString(2, correspondingFlag);
			pstmt.setString(3, confidentialFlag);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				accessLevelMap.put("ACL_ID", rs.getString("ACL_ID")); // アクセスレベル
				accessLevelMap.put("ACL_NAME", rs.getString("ACL_NAME")); // アクセスレベル名
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
		return accessLevelMap;
	}
	// 2013.07.13 yamagishi add. end

	// 2013.07.24 yamagishi add. start
	/**
	 * 属性情報テーブルのレコード（1物2品番情報）をMapで返す。
	 * @param drwgNo
	 * @param conn
	 * @return Map 属性情報レコード（1物2品番情報）のMap
	 * @throws Exception
	 */
	public static HashMap<String, String> getDrwgNoMap(String drwgNo, Connection conn) throws Exception {
		HashMap<String, String> drwgNoMap = new HashMap<String, String>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		try {
			String sql = "select id1.DRWG_NO      as DRWG_NO, " +
					"id1.TWIN_DRWG_NO as ID1_TWIN_DRWG_NO, " +
					"id2.DRWG_NO      as ID2_TWIN_DRWG_NO, " +
					"id1.ACL_ID       as ACL_ID, " +
					"id2.ACL_ID       as TWIN_ACL_ID, " +
					"id2.PROHIBIT     as TWIN_PROHIBIT " +
					"from INDEX_DB id1 " +
					"left outer join INDEX_DB id2 on id1.DRWG_NO = id2.TWIN_DRWG_NO " + // 1物2品番図番
					"where id1.DRWG_NO = ? " +
					"and (id1.TWIN_DRWG_NO is not null or id2.DRWG_NO is not null)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, drwgNo);
			rs = pstmt.executeQuery();
			metaData = rs.getMetaData();

			String columnName = null;
			if (rs.next()) {
				drwgNoMap = new HashMap<String, String>();

				// 属性情報（1物2品番情報）
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnLabel(i);
					drwgNoMap.put(columnName, rs.getString(columnName));
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
		return drwgNoMap;
	}

	/**
	 * 引数のIDの集合に紐づくアクセスレベル情報をMapで返す。
	 * @param conn
	 * @param aclIds
	 * @return Map アクセスレベル情報のMap
	 * @throws Exception
	 */
	public static HashMap<String, String> getAclMap(Connection conn, String... aclIds) throws Exception {
		HashMap<String, String> aclMap = new HashMap<String, String>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// sql
			StringBuilder sql = new StringBuilder("select ACL_ID, ACL_NAME " +
					"from ACCESS_LEVEL_MASTER " +
					"where ACL_ID in (");
			for (int i = 0; i < aclIds.length; i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.lastIndexOf(",")); // 末尾のカンマを削除
			sql.append(")");

			pstmt = conn.prepareStatement(sql.toString());
			int index = 0;
			for (String aclId : aclIds) {
				pstmt.setString(++index, aclId);
			}

			rs = pstmt.executeQuery();
			while (rs.next()) {
				// ACL情報
				aclMap.put(rs.getString("ACL_ID"), rs.getString("ACL_NAME"));
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
		return aclMap;
	}
	// 2013.07.24 yamagishi add. end
}
