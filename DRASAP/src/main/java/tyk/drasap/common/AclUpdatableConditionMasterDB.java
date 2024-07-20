package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * ACL変更可否判断マスターと対応したDBクラス。
 *
 * @author 2013/07/22 yamagishi
 */
public class AclUpdatableConditionMasterDB {
	/**
	 * 引数と変更前後のアクセスレベルが一致する件数を返す。
	 * @param preUpdateAcl
	 * @param postUpdateAcl
	 * @param conn
	 * @return count ACL変更可否判断マスターの該当件数
	 * @throws Exception
	 */
	public static long getAclUpdatableConditionCount(String preUpdateAcl, String postUpdateAcl,
			Connection conn) throws Exception {
		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(*) as COUNT from ACL_UPDATABLE_CONDITION_MASTER " +
					"where PRE_UPDATE_ACL = ? and POST_UPDATE_ACL = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, preUpdateAcl);
			pstmt.setString(2, postUpdateAcl);
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
}
