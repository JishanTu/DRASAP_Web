/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitRecipientDB.java
 * Name         : A-PLOT出図 出図指示情報[配布先レベル] クラス
 * Description  : A-PLOT用の 出図処理モジュールへ出図指示情報[配布先レベル]を管理するクラス.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

/**
 * A-PLOT用の 出図処理モジュールへ出図指示情報[配布先レベル]を管理するクラス.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitRecipientDB extends AbstractAPlotSchemaBase {

	/**
	 * コンストラクター.
	 * @param schema 対応スキーマ名.
	 */
	public APlotSubmitRecipientDB(String schema) {
		super(schema);
	}


	/**
	 * 本データ挿入SQLを返す.
	 * @return SQL文.
	 */
	public String insertSql() {
		String[][] ATTRS = {
				{ "JOB_ID", "char" },
				{ "RECIPIENT_ID", "char" },
				{ "RECIPIENT_SUBID", "char" },
				{ "RECIPIENT_NAME", "varchar" },
				{ "SEQUENCE_NO", "numeric" },
				{ "PRINTER_ID", "char" },
				{ "NOTE", "varchar" },
				{ "DUE_MODE", "varchar" },
				{ "DUE_YEAR", "varchar" },
				{ "DUE_MONTH", "varchar" },
				{ "DUE_DAY", "varchar" },
				{ "DUE_HOUR", "varchar" },
				{ "DUE_MINUTES", "varchar" },
				{ "OUTPUT_STATUS", "varchar" },
				{ "OUTPUT_DETAIL_STATUS", "varchar" },
				{ "OUTPUT_DATE", "timestamp" },
				{ "LAST_JOBTICKET_ID", "varchar" },
				{ "OUTPUT_ORDER", "char" },
				{ "DATA_FOLDER_PATH", "varchar" },
				{ "OUTPUT_WARN_MSG", "varchar" },
				{ "DISTRIBUTIONED", "char" },
		};
		StringBuilder insertAttrs = new StringBuilder("");
		StringBuilder insertValues = new StringBuilder("");
		for ( String[] attrName : ATTRS ) {
			if ( insertAttrs.length() > 0 ) {
				// 次の属性以降はカンマ.
				insertAttrs.append(",");
				insertValues.append(",");
			}

			if ( this.containsKey(attrName[0]) && this.get(attrName[0]) != null ) {
				// 属性.
				insertAttrs.append(attrName[0]);
				// 値.
				insertValues.append(quart(get(attrName[0]).toString(), attrName[1]));
			} else {
				// 属性.
				insertAttrs.append(attrName[0]);
				// NULLを設定.
				insertValues.append("NULL");
			}
		}
		return String.format("INSERT INTO %s.SUBMIT_RECIPIENT ( %s ) VALUES ( %s )", this.getSchemaName(), insertAttrs.toString(), insertValues.toString());
	}

	/**
	 * 更新ロックをかけるセレクト文を返す.
	 * @param jobId ジョブId.
	 * @return SQL文.
	 */
	public String forUpdateSql(String jobId) {
		return String.format(
				"SELECT * FROM %s.SUBMIT_RECIPIENT WHERE JOB_ID = '%s' for update", this.getSchemaName(), jobId);
	}

	/**
	 * ステータス更新用SQLを返す.
	 * @param jobId ジョブId.
	 * @param status ステータス文字列
	 * @return SQL文.
	 */
	public String updateStatusSql(String jobId, String recipientId, String status) {
		return String.format(
				"UPDATE %s.SUBMIT_RECIPIENT SET OUTPUT_STATUS = '%s' WHERE JOB_ID = '%s' AND RECIPIENT_ID = '%s'", this.getSchemaName(), status, jobId, recipientId);
	}



}
