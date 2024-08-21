package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * ユーザーマスターと対応したDBクラス。
 *
 * @author 2013/06/25 yamagishi
 */
public class UserGrpAclRelationDB {
	/**
	 * ユーザーの持つACL情報を、リストで返す。
	 * @param id ユーザーID
	 * @param conn
	 * @return userGrpAclList ACLリスト
	 * @throws Exception
	 */
	public static ArrayList<UserGrpAclRelation> getAclList(String id, Connection conn)
						throws Exception {
		ArrayList<UserGrpAclRelation> userGrpAclList = new ArrayList<UserGrpAclRelation>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select * from USER_GRP_ACL_RELATION " +
						 "where USER_GRP_CODE in (" +
							"with tmp as (" +
								"select DEPT_CODE,USER_GRP_CODE01,USER_GRP_CODE02,USER_GRP_CODE03,USER_GRP_CODE04,USER_GRP_CODE05,USER_GRP_CODE06,USER_GRP_CODE07,USER_GRP_CODE08,USER_GRP_CODE09,USER_GRP_CODE10 from USER_MASTER where USER_ID=?" +
							") " +
							"select USER_GRP_CODE   as USER_GRP_CODE from tmp inner join DEPARTMENT_MASTER d on d.DEPT_CODE = tmp.DEPT_CODE union all " +
							"select USER_GRP_CODE01 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE02 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE03 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE04 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE05 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE06 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE07 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE08 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE09 as USER_GRP_CODE from tmp union all " +
							"select USER_GRP_CODE10 as USER_GRP_CODE from tmp" +
						")";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id.trim());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 利用者グループ・アクセスレベル関連
				UserGrpAclRelation userGrpAcl = new UserGrpAclRelation();
				userGrpAcl.setUserGrpCode(rs.getString("USER_GRP_CODE"));
				userGrpAcl.setAclId(rs.getString("ACL_ID"));
				userGrpAcl.setAclValue(rs.getString("ACL_VALUE"));

				userGrpAclList.add(userGrpAcl);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			// CLOSE処理
			try{ rs.close(); } catch (Exception e) {}
			try{ pstmt.close(); } catch (Exception e) {}
		}
		return userGrpAclList;
	}
}
