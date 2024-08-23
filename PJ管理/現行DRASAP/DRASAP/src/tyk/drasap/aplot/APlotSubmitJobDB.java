/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitJobDB.java
 * Name         : A-PLOT出図 出図指示情報[ジョブレベル] クラス
 * Description  : A-PLOT用の 出図処理モジュールへ出図指示情報[ジョブレベル]を管理するクラス.
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A-PLOT用の 出図処理モジュールへ出図指示情報[ジョブレベル]を管理するクラス.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitJobDB extends AbstractAPlotSchemaBase {

	/**
	 * コンストラクター.
	 * @param schema 対応スキーマ名.
	 * @param jobId ジョブID.
	 * @param jobName ジョブ名.
	 * @param userId 出図ユーザーID.
	 * @param userName 出図ユーザー名.
	 */
	public APlotSubmitJobDB(String schema, String jobId, String jobName, String userId, String userName) {
		super(schema);

		// 基本データを設定.
		this.put("JOB_ID", jobId);
		this.put("JOB_NAME", jobName);
		this.put("SUBMITTER_ID", userId);
		this.put("SUBMITTER_NAME", userName);
	}

	/**
	 * JOBID取得
	 * @return JOBID
	 */
	public String getJobID() {
		return this.get("JOB_ID").toString();
	}

	/**
	 * 本データ挿入SQLを返す.
	 * @return SQL文.
	 */
	public String insertSql() {
		StringBuilder sb = new StringBuilder("");
		sb.append("INSERT INTO ").append(getSchemaName()).append(".SUBMIT_JOB (");
		sb.append("JOB_ID").append(", ");
		sb.append("JOB_NAME").append(", ");
		sb.append("SUBMITTER_ID").append(", ");
		sb.append("SUBMITTER_NAME").append(", ");
		sb.append("COVER_PAGE").append(", ");
		sb.append("PRIORITY").append(", ");
		sb.append("RECEIVE_QUEUE_ID").append(", ");
		sb.append("SUBMIT_DATE");
		sb.append(") VALUES (");
		sb.append("'").append(get("JOB_ID").toString()).append("',");
		sb.append("'").append(get("JOB_NAME").toString()).append("',");
		sb.append("'").append(get("SUBMITTER_ID").toString()).append("',");
		sb.append("'").append(get("SUBMITTER_NAME").toString()).append("',");
		sb.append("'").append(get("COVER_PAGE").toString()).append("',");
		sb.append("'").append(get("PRIORITY").toString()).append("',");
		sb.append("'").append(get("RECEIVE_QUEUE_ID").toString()).append("',");
		sb.append("SYSDATE");
		sb.append(")");
		return sb.toString();
	}


	/**
	 * JOBIDを採番.
	 * @param conn データベースコネクター
	 * @param shortId プリンタショートID
	 * @return JOBIDを返す.
	 * @throws SQLException
	 */
	public static String getNewJobID(Connection conn, String schemaName, int schemaNo, String shortId) throws SQLException {

		// 日付を取得.
		String yymm = new SimpleDateFormat("yyMM").format(new Date());

		// 指定のスキーマのID連番管理(OJ_SEQUENCE_X)から連番を取得.
		int seqNo = APlotOJSequenceXDB.getNewSeq(
				conn,
				schemaName, // スキーマ名.
				APlotOJSequenceXDB.JOB_ID_KIND1, // R固定
				shortId, // ショートID
				APlotOJSequenceXDB.JOB_ID_KIND3_JOB, //
				new SimpleDateFormat("yyMM").format(new Date()));

		// 「ID種別（大分類） + スキーマ番号 + ID種別（中分類） + ID種別（小分類） + 年月（YYMM形式） + '-'（固定） + 連番（00000000形式）」
		return String.format("%s%d%s%s%s-%07d", APlotOJSequenceXDB.JOB_ID_KIND1, schemaNo, shortId, APlotOJSequenceXDB.JOB_ID_KIND3_JOB, yymm, seqNo);
	}



}
