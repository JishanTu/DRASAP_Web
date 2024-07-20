package tyk.drasap.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 原図庫作業依頼でのジョブIDを取得するクラス
 */
public class JobReqSeqDB {
	/**
	 * ジョブを指定して、ジョブIDを取得する。
	 * コネクションについては、このメソッドの中で分離レベルの設定などを行う。そのため新しく取得したばかりのコネクションを渡すこと。
	 * @param job A=図面登録依頼 B=図面出力指示 C=原図借用依頼 D=図面以外焼付
	 * @param conn このメソッドの中で分離レベルの設定などを行う。そのため新しく取得したばかりのコネクションを渡すこと。
	 * @return ジョブIDを返す。例) A040108-001
	 * @throws Exception
	 */
	public static String getJobId(String job, Connection conn) throws Exception {
		// jobの確認・・・A,B,C,Dのみ許す
		StringBuilder jobId = new StringBuilder();//
		String seqColumnName = null;// 取得対象のカラム名
		if ("A".equals(job)) {
			seqColumnName = "SEQ_A";
			jobId.append("A");
		} else if ("B".equals(job)) {
			seqColumnName = "SEQ_B";
			jobId.append("B");
		} else if ("C".equals(job)) {
			seqColumnName = "SEQ_C";
			jobId.append("C");
		} else if ("D".equals(job)) {
			seqColumnName = "SEQ_D";
			jobId.append("D");
		} else {
			throw new IllegalArgumentException("JOBの指定はA,B,C,Dの何れかのみ可能です。");
		}
		// 本日日付の取得
		String ymd = new SimpleDateFormat("yyMMdd").format(new Date());// YYMMDD形式の本日日付
		jobId.append(ymd);
		jobId.append('-');
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// トランザクションを設定する
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			// for updateで検索
			String strSql1 = "select * from JOB_REQUEST_SEQUENCE" +
					" where SEQ_DATE = '" + ymd + "'" +
					" for update";
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(strSql1);
			if (rs1.next()) {
				// すでに同じ日付キーが存在する場合
				String id = rs1.getString(seqColumnName);
				String newId = null;
				if (id == null) {
					// 同じJOBでシーケンスが未取得の場合
					newId = "001";
				} else {
					// 同じJOBでシーケンスを取得済みの場合
					int intId = Integer.parseInt(id);
					intId++;// インクリメントする
					if (intId > 999) {
						throw new RuntimeException("999まで使用しています。これ以上の番号を取得できません。");
					}
					newId = String.valueOf(intId);
					while (newId.length() < 3) {
						newId = "0" + newId;
					}
				}
				jobId.append(newId);
				// 更新する
				String strSql2 = "update JOB_REQUEST_SEQUENCE set " +
						seqColumnName + "='" + newId + "'" +
						" where SEQ_DATE = '" + ymd + "'";
				stmt1.executeUpdate(strSql2);
			} else {
				// 同じ日付キーが存在しない場合
				jobId.append("001");
				String strSql3 = "insert into JOB_REQUEST_SEQUENCE(SEQ_DATE, " + seqColumnName + ")" +
						" values('" + ymd + "', '001')";
				stmt1.executeUpdate(strSql3);
			}
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
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
		return jobId.toString();
	}

	/**
	 * テストのためのメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("JobReqSeqDBのテスト");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@orasrv:1521:SMP", "DRASAP", "DRASAP");
			System.out.println(JobReqSeqDB.getJobId("C", conn));
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("JobReqSeqDBのテストを終了");
		System.exit(0);
	}

}
