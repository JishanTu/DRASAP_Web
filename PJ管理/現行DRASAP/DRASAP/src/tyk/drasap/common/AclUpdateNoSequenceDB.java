package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ACL管理NO採番テーブルと対応したDBクラス。
 *
 * @author 2013/07/18 yamagishi
 */
public class AclUpdateNoSequenceDB {

	/**
	 * ACL管理NOを採番して返す。採番ロジックで排他制御を実施。
	 * @param aclUpdateType ACL更新種別（A: アクセスレベル一括更新, B: アクセスレベル変更）
	 * @param conn
	 * @return aclUpdateNo 管理NO
	 */
	public static synchronized String getAclUpdateNo(String aclUpdateType, Connection conn) throws Exception {
		long count = 0;
		String aclSeqNoOld = null; // 現在値
		String aclUpdateDateOld = null; // 現在値
		String aclSeqNo = null;
		String aclUpdateDate = null;
		String aclUpdateNo = null;

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);

			// 年月日(yymmdd)取得
			aclUpdateDate = new SimpleDateFormat("yyMMdd").format(new Date());

			String sql1 = "select ACL_SEQ_NO, ACL_UPDATE_DATE from ACL_UPDATE_NO_SEQUENCE " +
					"where ACL_UPDATE_TYPE = ? " +
					"for update";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, aclUpdateType);
			rs = pstmt1.executeQuery();

			if (rs.next()) {
				// 現在のシーケンス番号
				aclSeqNoOld = rs.getString("ACL_SEQ_NO");
				// 現在の年月日
				aclUpdateDateOld = rs.getString("ACL_UPDATE_DATE");
			} else {
				// 初回のみ実行
				String sql = "insert into ACL_UPDATE_NO_SEQUENCE " +
						"(ACL_UPDATE_TYPE, ACL_UPDATE_DATE, ACL_SEQ_NO) " +
						"values (?, ?, '000')";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, aclUpdateType);
				pstmt.setString(2, aclUpdateDate);
				// 初期データを作成
				count = pstmt.executeUpdate();
				if (count <= 0) {
					throw new Exception("初期データ作成失敗。");
				}
			}

			// シーケンス番号採番
			if (aclUpdateDateOld != null && aclUpdateDate.equals(aclUpdateDateOld)) {

				// 日付単位で連番（前ゼロ埋め3桁）
				aclSeqNo = String.format("%1$03d", Integer.parseInt(aclSeqNoOld) + 1);

				if (aclSeqNo.length() > 3) {
					// 万一、3桁を超えてしまった場合、初期化
					aclSeqNo = "001";
				}
			} else {
				// 日付が異なる為、初期化
				aclSeqNo = "001";
			}

			String sql2 = "update ACL_UPDATE_NO_SEQUENCE " +
					"set ACL_UPDATE_DATE = ?, ACL_SEQ_NO = ? " +
					"where ACL_UPDATE_TYPE = ?";
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, aclUpdateDate);
			pstmt2.setString(2, aclSeqNo);
			pstmt2.setString(3, aclUpdateType);
			count = pstmt2.executeUpdate();
			// コミット
			conn.commit();

			if (count > 0) {
				// 管理NO [A|B + yymmdd + 3桁連番]
				aclUpdateNo = aclUpdateType + aclUpdateDate + aclSeqNo;
			}

		} catch (Exception e) {
			// ロールバック
			try { conn.rollback(); } catch (Exception e2) {}
			throw e;
		} finally {
			// CLOSE処理
			try { rs.close(); } catch (Exception e) {}
			try { pstmt1.close(); } catch (Exception e) {}
			try { pstmt2.close(); } catch (Exception e) {}
		}
		return aclUpdateNo;
	}
}
