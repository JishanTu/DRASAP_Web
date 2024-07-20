/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : AbstractAPlotSchemaBase.java
 * Name         : A-PLOT出図 ID採番クラス.
 * Description  : A-PLOT出図のJOB ID、ドキュメントIDの採番を行うクラス.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * A-PLOTプリンタ出力用ID採番クラス.
 * 本クラスはスタティック使用を前提としている.
 * スキーマ側のテーブル OJ-ID連番管理[OJ_SEQUENCE_X]を使用して連番を管理する.
 *
 * 【使用方法】
 *
 *    int seqNo = APlotOJSequenceXDB.getNewSeq(
 *                  conn,   -----  データベースコネクションを指定.
 *                  "OJ1",  -----  スキーマ名を指定.
 *                  "R",    -----  ID種別（大分類）を指定. JTEKTでは「R」（固定）。
 *                  "B",    -----  ID種別（中分類）を指定. JTEKTではプリンタショートID.
 *                  "J",    -----  ID種別（小分類）を指定. ジョブの場合は「J」、ドキュメントの場合は「D」。
 *                  "1802"  -----  ID発番時のシステム日時の年月（YYMM形式）.
 *                  );
 *    ※採番メソッド内でトランザクションは閉じる.欠番管理はしない.
 *
 * @author hideki_sugiyama
 *
 */
public class APlotOJSequenceXDB {

	/** ジョブID取得時のID種別（大分類）の文字.  */
	public static final String JOB_ID_KIND1 = "R";
	/** ジョブID取得時のID種別（小分類）ジョブID.  */
	public static final String JOB_ID_KIND3_JOB = "J";
	/** ジョブID取得時のID種別（小分類）ドキュメント.  */
	public static final String JOB_ID_KIND3_DOC = "D";

	/** Logger（log4j） */
	private static Logger category = Logger.getLogger(APlotOJSequenceXDB.class.getName());

	/** コンストラクタ. */
	private APlotOJSequenceXDB() {
	}

	/**
	 * OJ-ID連番管理テーブル(OJ_SEQUENCE_X)からJOBIDの連番を取得（ロックも兼ねる）.
	 * @param schemaName スキーマ名.
	 * @param kd1 ID種別（大分類）を指定. JTEKTでは「R」（固定）。
	 * @param kd2 ID種別（中分類）を指定. JTEKTではプリンタショートID.
	 * @param kd3 ID種別（小分類）を指定. ジョブの場合は「J」、ドキュメントの場合は「D」。
	 * @param yymm ID発番時のシステム日時の年月（YYMM形式）.
	 * @return SQL文字列を返す.
	 */
	private static String createSQL_selectSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm) {

		// 連番を検索するSQLを返す（for update).
		return String.format(
				"SELECT SEQVAL FROM %s.OJ_SEQUENCE_X" +
						" WHERE ID_KIND1 = '%s' AND ID_KIND2 = '%s' AND ID_KIND3 = '%s' AND YYMM = '%s' for update",
				schemaName, kd1, kd2, kd3, yymm);
	}

	/**
	 * OJ-ID連番管理テーブル(OJ_SEQUENCE_X)の連番レコードを作成(初期採番時).
	 * @param schemaName スキーマ名.
	 * @param kd1 ID種別（大分類）を指定. JTEKTでは「R」（固定）。
	 * @param kd2 ID種別（中分類）を指定. JTEKTではプリンタショートID.
	 * @param kd3 ID種別（小分類）を指定. ジョブの場合は「J」、ドキュメントの場合は「D」。
	 * @param yymm ID発番時のシステム日時の年月（YYMM形式）.
	 * @param seqNo 初期番号.
	 * @return SQL文字列を返す.
	 */
	private static String createSQL_insertSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm, int seqNo) {

		// 連番を作成するSQLを返す.
		return String.format(
				"INSERT INTO %s.OJ_SEQUENCE_X(ID_KIND1, ID_KIND2, ID_KIND3, YYMM, SEQVAL) values (" +
						" '%s', '%s', '%s', '%s', %d)",
				schemaName, kd1, kd2, kd3, yymm, seqNo);
	}

	/**
	 * OJ-ID連番管理テーブル(OJ_SEQUENCE_X)の連番を更新.
	 * @param schemaName スキーマ名.
	 * @param kd1 ID種別（大分類）を指定. JTEKTでは「R」（固定）。
	 * @param kd2 ID種別（中分類）を指定. JTEKTではプリンタショートID.
	 * @param kd3 ID種別（小分類）を指定. ジョブの場合は「J」、ドキュメントの場合は「D」。
	 * @param yymm ID発番時のシステム日時の年月（YYMM形式）.
	 * @param seqNo 次の番号.
	 * @return SQL文字列を返す.
	 */
	private static String createSQL_updateSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm, int seqNo) {

		// 連番を更新するSQLを返す.
		return String.format(
				"UPDATE %s.OJ_SEQUENCE_X SET SEQVAL = %d " +
						" WHERE ID_KIND1 = '%s' AND ID_KIND2 = '%s' AND ID_KIND3 = '%s' AND YYMM = '%s'",
				schemaName, seqNo, kd1, kd2, kd3, yymm);
	}

	/**
	 * ジョブIDの割り振り.
	 * OJ-ID連番管理[OJ_SEQUENCE_X]テーブルで採番を行う.
	 * 本メソッド内でトランザクションを閉じる.
	 *
	 * @param kd1 大分類(「R」固定).
	 * @param kd2 中分類(プリンタのショートID).
	 * @param kd3 小分類(ジョブIDの場合は[J]、ドキュメントIDの場合は[D]).
	 * @param yymm 年月(YYMM形式).
	 * @return 新しい連番.
	 * @throws SQLException
	 */
	public static int getNewSeq(Connection conn, String schemaName, String kd1, String kd2, String kd3, String yymm) throws SQLException {

		int seqNo = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// トランザクションを設定する
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(createSQL_selectSeqNo(schemaName, kd1, kd2, kd3, yymm));
			if (rs.next()) {
				seqNo = rs.getInt("SEQVAL");
				// インクリメント.
				seqNo++;
			}
			if (seqNo <= 0) {
				// 未登録シーケンスNoなので初期値を1とする.
				seqNo = 1;
				// データを作成.
				stmt.executeUpdate(createSQL_insertSeqNo(schemaName, kd1, kd2, kd3, yymm, seqNo));
			} else {
				// データを更新.
				stmt.executeUpdate(createSQL_updateSeqNo(schemaName, kd1, kd2, kd3, yymm, seqNo));
			}
			// コミット
			conn.commit();
		} catch (SQLException ex) {
			category.fatal("A-PLOT出図採番でSQLエラー", ex);
			conn.rollback();
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return seqNo;
	}

}
