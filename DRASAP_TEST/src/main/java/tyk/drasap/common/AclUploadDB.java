package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ACLアップロードデータテーブルと対応したDBクラス。
 *
 * @author 2013/07/09 yamagishi
 */
public class AclUploadDB {
	/**
	 * ユーザーの持つACL情報リストの件数を返す。
	 * @param aclUpdateNo 管理NO
	 * @param conn
	 * @return count ACLアップロードデータリスト件数
	 * @throws Exception
	 */
	public static long getAclUploadCount(String aclUpdateNo, Connection conn) throws Exception {
		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(RECORD_NO) as COUNT from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// ACLアップロードデータ件数取得
				count = rs.getLong("COUNT");
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
		return count;
	}

	/**
	 * ユーザーの持つACL情報を、リストで返す。
	 * @param aclUpdateNo 管理NO
	 * @param conn
	 * @return aclUploadList ACLアップロードデータリスト
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getAclUploadList(String aclUpdateNo, Connection conn) throws Exception {
		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select * from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"order by RECORD_NO asc";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			while (rs.next()) {
				// ACLアップロードデータ
				aclUpload = new AclUpload();
				aclUpload.setAclUpdateNo(rs.getString("ACL_UPDATE_NO"));
				aclUpload.setUserId(rs.getString("USER_ID"));
				aclUpload.setUserName(rs.getString("USER_NAME"));
				aclUpload.setRecordNo(rs.getString("RECORD_NO"));
				aclUpload.setMachineJp(rs.getString("MACHINE_JP"));
				aclUpload.setMachineNo(rs.getString("MACHINE_NO"));
				aclUpload.setDrwgNo(rs.getString("DRWG_NO"));
				aclUpload.setMachineCode(rs.getString("MACHINE_CODE"));
				aclUpload.setDetailNo(rs.getString("DETAIL_NO"));
				aclUpload.setPages(rs.getString("PAGES"));
				aclUpload.setItemNo(rs.getString("ITEM_NO"));
				aclUpload.setItemNoShort(rs.getString("ITEM_NO_SHORT"));
				aclUpload.setGrpCode(rs.getString("GRP_CODE"));
				aclUpload.setCorrespondingFlag(rs.getString("CORRESPONDING_FLAG"));
				aclUpload.setConfidentialFlag(rs.getString("CONFIDENTIAL_FLAG"));
				aclUpload.setPreUpdateAcl(rs.getString("PRE_UPDATE_ACL"));
				aclUpload.setPreUpdateAcl(rs.getString("PRE_UPDATE_ACL_NAME"));
				aclUpload.setPostUpdateAcl(rs.getString("POST_UPDATE_ACL"));
				aclUpload.setPostUpdateAcl(rs.getString("POST_UPDATE_ACL_NAME"));
				aclUpload.setItemName(rs.getString("ITEM_NAME"));
				aclUpload.setMessage(rs.getString("MESSAGE"));
				aclUpload.setAclUpdate(rs.getDate("ACL_UPDATE"));

				// リスト追加
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * ユーザーの持つACL情報を、表示用リストで返す。
	 * @param aclUpdateNo 管理NO
	 * @param drasapInfo 管理者設定情報
	 * @param conn
	 * @return aclUploadList ACLアップロードデータリスト
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getAclUploadDispList(String aclUpdateNo, DrasapInfo drasapInfo,
			Connection conn) throws Exception {

		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ITEM_NO, ITEM_NAME, CORRESPONDING_FLAG, CONFIDENTIAL_FLAG, GRP_CODE, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL_NAME, MESSAGE " +
					"from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"order by RECORD_NO asc";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			while (rs.next()) {
				// ACLアップロードデータ
				aclUpload = new AclUpload();
				aclUpload.setItemNo(rs.getString("ITEM_NO"));
				aclUpload.setItemName(rs.getString("ITEM_NAME"));
				aclUpload.setCorrespondingFlag(rs.getString("CORRESPONDING_FLAG"));
				if ("1".equals(aclUpload.getCorrespondingFlag())) {
					// 該当
					aclUpload.setCorrespondingValue(drasapInfo.getCorrespondingValue());
				}
				aclUpload.setConfidentialFlag(rs.getString("CONFIDENTIAL_FLAG"));
				if ("2".equals(aclUpload.getConfidentialFlag()) && drasapInfo.getConfidentialValue() != null) {
					// 秘
					aclUpload.setConfidentialValue(drasapInfo.getConfidentialValue());
				} else if ("3".equals(aclUpload.getConfidentialFlag())) {
					// 極秘
					aclUpload.setConfidentialValue(drasapInfo.getStrictlyConfidentialValue());
				}
				aclUpload.setGrpCode(rs.getString("GRP_CODE"));
				aclUpload.setPreUpdateAclName(rs.getString("PRE_UPDATE_ACL_NAME"));
				aclUpload.setPostUpdateAclName(rs.getString("POST_UPDATE_ACL_NAME"));
				aclUpload.setMessage(rs.getString("MESSAGE"));

				// リスト追加
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * アップロードファイルを、ACLアップロードデータテーブルに登録する。
	 * @param aclUpload
	 * @param conn
	 * @return resultCount 登録件数
	 * @throws Exception
	 */
	public static long insertAclUpload(AclUpload aclUpload, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "insert into ACL_UPLOAD_TABLE (" +
					"ACL_UPDATE_NO, USER_ID, USER_NAME, RECORD_NO, MACHINE_JP, " +
					"MACHINE_NO, DRWG_NO, MACHINE_CODE, DETAIL_NO, PAGES, ITEM_NO, ITEM_NO_SHORT, " +
					"CORRESPONDING_FLAG, CONFIDENTIAL_FLAG, GRP_CODE, PRE_UPDATE_ACL, " +
					"PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, ITEM_NAME, MESSAGE, ACL_UPDATE) " +
					"values (?, ?, ?, lpad(?,8,'0'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)";
			pstmt = conn.prepareStatement(sql);

			int paramIndex = 0;
			pstmt.setString(++paramIndex, aclUpload.getAclUpdateNo()); // 管理NO
			pstmt.setString(++paramIndex, aclUpload.getUserId()); // ユーザID
			pstmt.setString(++paramIndex, aclUpload.getUserName()); // 氏名
			pstmt.setString(++paramIndex, aclUpload.getRecordNo()); // レコード番号
			pstmt.setString(++paramIndex, aclUpload.getMachineJp()); // 装置
			pstmt.setString(++paramIndex, aclUpload.getMachineNo()); // 装置NO
			pstmt.setString(++paramIndex, aclUpload.getDrwgNo()); // 手配図番
			pstmt.setString(++paramIndex, aclUpload.getMachineCode()); // 装置コード
			pstmt.setString(++paramIndex, aclUpload.getDetailNo()); // 明細番号
			pstmt.setString(++paramIndex, aclUpload.getPages()); // 頁
			pstmt.setString(++paramIndex, aclUpload.getItemNo()); // 品番
			pstmt.setString(++paramIndex, aclUpload.getItemNoShort()); // 品番（空白、ハイフン「-」を除いた半角大文字）
			pstmt.setString(++paramIndex, aclUpload.getCorrespondingFlag()); // 該当図区分
			pstmt.setString(++paramIndex, aclUpload.getConfidentialFlag()); // 機密管理図区分
			pstmt.setString(++paramIndex, aclUpload.getGrpCode()); // グループ
			pstmt.setString(++paramIndex, aclUpload.getPreUpdateAcl()); // 変更前アクセスレベル
			pstmt.setString(++paramIndex, aclUpload.getPreUpdateAclName()); // 変更前アクセスレベル名
			pstmt.setString(++paramIndex, aclUpload.getPostUpdateAcl()); // 変更後アクセスレベル
			pstmt.setString(++paramIndex, aclUpload.getPostUpdateAclName()); // 変更後アクセスレベル名
			pstmt.setString(++paramIndex, aclUpload.getItemName()); // 品名（規格型式）
			pstmt.setString(++paramIndex, aclUpload.getMessage()); // メッセージ
			resultCount += pstmt.executeUpdate();

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
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * ACLアップロードデータテーブルのデータを削除する。
	 * @param aclUpdateType (A:アクセスレベル一括更新, B:アクセスレベル変更)
	 * @param user
	 * @param conn
	 * @return count 削除件数
	 * @throws Exception
	 */
	public static long deleteAclUpload(String aclUpdateType, User user, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO < (? || to_char(sysdate, 'YYMMDD') || '000') " +
					"and USER_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateType);
			pstmt.setString(2, user.getId());
			resultCount = pstmt.executeUpdate();

			// コミット
			conn.commit();

		} catch (Exception e) {
			// ロールバック
			conn.rollback();
			throw e;
		} finally {
			// CLOSE処理
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * ACLアップロードデータテーブルのデータを削除する。
	 * @param aclUpdateNo
	 * @param conn
	 * @return count 削除件数
	 * @throws Exception
	 */
	public static long deleteAclUpload(String aclUpdateNo, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from ACL_UPLOAD_TABLE where ACL_UPDATE_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			resultCount = pstmt.executeUpdate();

			// コミット
			conn.commit();

		} catch (Exception e) {
			// ロールバック
			conn.rollback();
			throw e;
		} finally {
			// CLOSE処理
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * 属性情報更新用に、重複を除いてACLアップロードデータのリストで返す。
	 * @param aclUpdateNo 管理NO
	 * @param conn
	 * @return aclUploadList ACLアップロードデータリスト
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getDistinctAclUploadList(String aclUpdateNo, Connection conn)
			throws Exception {

		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ACL_UPDATE_NO, USER_ID, USER_NAME, ITEM_NO_SHORT, PRE_UPDATE_ACL, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, MESSAGE " +
					"from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"group by ACL_UPDATE_NO, USER_ID, USER_NAME, ITEM_NO_SHORT,PRE_UPDATE_ACL, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, MESSAGE " +
					"order by min(RECORD_NO)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			String value = null;
			String itemNoShort = null;
			while (rs.next()) {
				// ACLアップロードデータ
				aclUpload = new AclUpload();
				aclUpload.setAclUpdateNo(rs.getString("ACL_UPDATE_NO"));
				aclUpload.setUserId(rs.getString("USER_ID"));
				aclUpload.setUserName(rs.getString("USER_NAME"));

				// 属性情報テーブル更新用にフォーマット
				value = rs.getString("ITEM_NO_SHORT");
				if (value != null && value.length() > 0) {
					aclUpload.setItemNo(value);
					// trim処理。ハイフン「-」を除く。
					itemNoShort = StringCheck.trimWsp(value).replace("-", "");
					// 半角大文字に変換する
					itemNoShort = StringCheck.changeDbToSbAscii(itemNoShort).toUpperCase();
					aclUpload.setItemNoShort(itemNoShort);
				}
				if ((value = rs.getString("PRE_UPDATE_ACL")) != null && value.length() > 0) {
					aclUpload.setPreUpdateAcl(value);
				}
				if ((value = rs.getString("PRE_UPDATE_ACL_NAME")) != null && value.length() > 0) {
					aclUpload.setPreUpdateAclName(value);
				}
				if ((value = rs.getString("POST_UPDATE_ACL")) != null && value.length() > 0) {
					aclUpload.setPostUpdateAcl(value);
				}
				if ((value = rs.getString("POST_UPDATE_ACL_NAME")) != null && value.length() > 0) {
					aclUpload.setPostUpdateAclName(value);
				}
				if ((value = rs.getString("MESSAGE")) != null && value.length() > 0) {
					aclUpload.setMessage(value);
				}
				// リスト追加
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * ACLアップロードデータテーブルのメッセージを更新する。
	 * @param aclUpdateNo
	 * @param message
	 * @param conn
	 * @param itemNosShorts[]
	 * @return resultCount 更新件数
	 * @throws Exception
	 */
	public static long updateMessage(String aclUpdateNo, String message, Connection conn, String... itemNoShorts)
			throws Exception {

		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "update ACL_UPLOAD_TABLE set MESSAGE = ? " +
					"where ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and MESSAGE is null";
			pstmt = conn.prepareStatement(sql);
			for (String itemNoShort : itemNoShorts) {
				pstmt.setString(1, message);
				pstmt.setString(2, aclUpdateNo);
				pstmt.setString(3, itemNoShort);
				resultCount = pstmt.executeUpdate();

				// コミット
				conn.commit();
			}
		} catch (Exception e) {
			// ロールバック
			conn.rollback();
			throw e;
		} finally {
			// CLOSE処理
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * 品番および変更前後のアクセスレベルが一致する品番の件数を返す。
	 * @param aclUpdateNo 管理NO
	 * @param itemNoShort
	 * @param preUpdateAcl
	 * @param postUpdateAcl
	 * @param conn
	 * @return count 条件と一致するACLアップロードデータテーブル件数
	 * @throws Exception
	 */
	public static long getItemNoAclCount(String aclUpdateNo, String itemNoShort, String preUpdateAcl, String postUpdateAcl,
			Connection conn) throws Exception {

		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(ITEM_NO) as COUNT from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and PRE_UPDATE_ACL = ? and POST_UPDATE_ACL = ? and MESSAGE is null";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			pstmt.setString(2, itemNoShort);
			pstmt.setString(3, preUpdateAcl);
			pstmt.setString(4, postUpdateAcl);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// ACLアップロードデータ件数取得
				count = rs.getLong("COUNT");
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
		return count;
	}

	/**
	 * ACLアップロードデータテーブルの1物2品番情報をMapのリストで返す。
	 * @param aclUpdateNo 管理NO
	 * @param conn
	 * @return <Map>List 1物2品番情報のリスト
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getDrwgNoMapList(String aclUpdateNo, Connection conn)
			throws Exception {

		ArrayList<HashMap<String, String>> drwgNoMapList = new ArrayList<HashMap<String, String>>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		try {
			String sql = "select org.ITEM_NO_SHORT    as ITEM_NO_SHORT, " +
					"twin.ITEM_NO_SHORT   as TWIN_DRWG_NO, " +
					"org.POST_UPDATE_ACL  as POST_UPDATE_ACL, " +
					"twin.POST_UPDATE_ACL as TWIN_POST_UPDATE_ACL, " +
					"org.MESSAGE          as MESSAGE, " +
					"id1.DRWG_NO          as ID1_DRWG_NO, " +
					"id1.TWIN_DRWG_NO     as ID1_TWIN_DRWG_NO, " +
					"id1.ACL_ID           as ID1_PRE_UPDATE_ACL, " +
					"id2.DRWG_NO          as ID2_DRWG_NO, " +
					"id2.TWIN_DRWG_NO     as ID2_TWIN_DRWG_NO, " +
					"id2.ACL_ID           as ID2_PRE_UPDATE_ACL " +
					"from ACL_UPLOAD_TABLE org " +
					"left outer join INDEX_DB id1 on id1.DRWG_NO = org.ITEM_NO_SHORT and id1.TWIN_DRWG_NO is not null " + // アップロードデータ．図番で検索
					"left outer join INDEX_DB id2 on id2.TWIN_DRWG_NO = org.ITEM_NO_SHORT " + // アップロードデータ．1物2品番図番で検索
					"left outer join ACL_UPLOAD_TABLE twin on twin.ACL_UPDATE_NO = org.ACL_UPDATE_NO and twin.ITEM_NO_SHORT = nvl(id1.TWIN_DRWG_NO, ID2.DRWG_NO) " + // アップロードデータ内で1物2品番を検索
					"where org.ACL_UPDATE_NO = ? " +
					"and (id1.DRWG_NO is not null or id1.TWIN_DRWG_NO is not null or " +
					" id2.DRWG_NO is not null or id2.TWIN_DRWG_NO is not null)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();
			metaData = rs.getMetaData();

			HashMap<String, String> orgTwinDrwgNoMap = null;
			String columnName = null;
			while (rs.next()) {
				orgTwinDrwgNoMap = new HashMap<String, String>();

				// ACLアップロードデータ（1物2品番情報）
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnLabel(i);
					orgTwinDrwgNoMap.put(columnName, rs.getString(columnName));
				}
				// リスト追加
				drwgNoMapList.add(orgTwinDrwgNoMap);
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
		return drwgNoMapList;
	}

	/**
	 * 1物2品番対応図番を、ACLアップロードデータテーブルに登録する。
	 * @param aclUpdateNo
	 * @param itemNoShort 1物2品番の元図番
	 * @param conn
	 * @return 登録件数
	 * @throws Exception
	 */
	public static long insertSelectTwinDrwgNo(String aclUpdateNo, String itemNoShort,
			Connection conn) throws Exception {

		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "insert into ACL_UPLOAD_TABLE " +
					"select  aut.ACL_UPDATE_NO, " + // ACL_UPDATE_NO
					"aut.USER_ID, " + // USER_ID
					"aut.USER_NAME, " + // USER_NAME
					"lpad(tmp.NEW_RECORD_NO,8,'0'), " + // RECORD_NO
					"id.MACHINE_JP, " + // MACHINE_JP
					"id.MACHINE_NO, " + // MACHINE_NO
					"aut.DRWG_NO, " + // DRWG_NO
					"aut.MACHINE_CODE, " + // MACHINE_CODE
					"aut.DETAIL_NO, " + // DETAIL_NO
					"aut.PAGES, " + // PAGES
					"id.DRWG_NO, " + // ITEM_NO
					"id.DRWG_NO, " + // ITEM_NO_SHORT
					"aut.CORRESPONDING_FLAG, " + // CORRESPONDING_FLAG
					"aut.CONFIDENTIAL_FLAG, " + // CONFIDENTIAL_FLAG
					"aut.GRP_CODE, " + // GRP_CODE
					"id.ACL_ID, " + // PRE_UPDATE_ACL
					"alm.ACL_NAME , " + // PRE_UPDATE_ACL_NAME
					"aut.POST_UPDATE_ACL, " + // POST_UPDATE_ACL
					"aut.POST_UPDATE_ACL_NAME, " + // POST_UPDATE_ACL_NAME
					"id.MACHINE_JP, " + // ITEM_NAME
					"null, " + // MESSAGE
					"null " + // ACL_UPDATE
					"from INDEX_DB id " +
					"inner join ACL_UPLOAD_TABLE aut on aut.ITEM_NO_SHORT = id.TWIN_DRWG_NO " +
					"left outer join ACCESS_LEVEL_MASTER alm on alm.ACL_ID = id.ACL_ID, " +
					"(select to_number(max(RECORD_NO))+1 as NEW_RECORD_NO from ACL_UPLOAD_TABLE where ACL_UPDATE_NO = ?) tmp " +
					"where aut.ACL_UPDATE_NO = ? and aut.ITEM_NO_SHORT = ? and rownum <= 1 " +
					"and not exists (" +
					"select * from ACL_UPLOAD_TABLE ine where ine.ACL_UPDATE_NO = aut.ACL_UPDATE_NO and ine.ITEM_NO_SHORT = id.DRWG_NO" +
					")";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			pstmt.setString(2, aclUpdateNo);
			pstmt.setString(3, itemNoShort);
			resultCount = pstmt.executeUpdate();

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
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}
}
