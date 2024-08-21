/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitDocumentDB.java
 * Name         : A-PLOT出図 出図指示情報[ドキュメント（ファイル）] クラス
 * Description  : A-PLOT用の 出図処理モジュールへ出図指示情報[ドキュメント（ファイル）]を管理するクラス.
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
import java.util.HashMap;

import org.apache.log4j.Category;

import tyk.drasap.common.DrasapUtil;

/**
 * A-PLOT用の 出図処理モジュールへ出図指示情報[ドキュメント（ファイル）]を管理するクラス.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitDocumentDB extends AbstractAPlotSchemaBase {


	/** 印刷サイズのスケーリングモード変換用マップテーブル. */
	static final private HashMap<String, String> zoomModeMap = new HashMap<String, String>() { {
		put("ORG","サイズ指定");
		put("A0","サイズ指定");
		put("A1","サイズ指定");
		put("A2","サイズ指定");
		put("A3","サイズ指定");
		put("A4","サイズ指定");
		//put("A0","サイズ指定");
		//put("A0L","サイズ指定");
		//put("A1L","サイズ指定");
		//put("A2L","サイズ指定");
		//put("A3L","サイズ指定");
		//put("A4L","サイズ指定");
		put("70.7%","倍率指定");
		put("50%","倍率指定");
		put("35.4%","倍率指定");
		put("25%","倍率指定");
	}};

	/** 印刷サイズの倍率変換用マップテーブル. */
	static final private HashMap<String, String> fixedZoomMap = new HashMap() { {
		put("ORG","等倍");
		put("A0","A0");
		put("A1","A1");
		put("A2","A2");
		put("A3","A3");
		put("A4","A4");
		//put("A0","A0");
		//put("A0L","A0L");
		//put("A1L","A1L");
		//put("A2L","A2L");
		//put("A3L","A3L");
		//put("A4L","A4L");
	}};

	/** 印刷サイズのパーセント倍率マップテーブル. */
	static final private HashMap<String, String> PercentageZoomMap = new HashMap() { {
		put("70.7%","70.7");
		put("50%","50");
		put("35.4%","35.4");
		put("25%","25");
	}};

	//対象図面がA0Lの場合、設定変更必要の画面指定サイズのテーブル
	static final private HashMap<String,String> targetPaperSizeForA0L = new HashMap(){ {
		put("A0", "A0");
		put("A1", "A1");
		put("A2", "A2");
		put("A3", "A3");
		put("A4", "A4");
	}};

	/**
	 * コンストラクター.
	 * @param schema 対応スキーマ名.
	 */
	public APlotSubmitDocumentDB(String schema) {
		super(schema);
	}


	/**
	 * 印刷サイズからスケーリングモード文字列に変換.
	 *
	 * 【スケーリングモード】
	 *   画面で指定されたサイズが下記の場合は「サイズ指定」を返す.
	 *    「ORG」、「A0」、「A1」、「A2」、「A3」、「A4」、「A0L」、「A1L」、「A2L」、「A3L」、「A4L」
	 *   画面で指定されたサイズが下記の場合は「倍率指定」を返す.
	 *    「70.7%」、「50%」、「35.4%」、「25%」
	 * @param printSize 印刷サイズ.
	 * @return スケーリングモード.
	 */
	public static String toZoomMode(String printSize) {
		// マップテーブルに対応する文字列を返す.
		return zoomModeMap.containsKey(printSize) ? zoomModeMap.get(printSize) : "";
	}


	/**
	 * 印刷サイズから固定倍率文字列に変換.
	 * @param zoomMode スケーリングモード
	 * @param dwgSize 図面サイズ
	 * @param printSize 印刷サイズ.
	 * @return 固定倍率.パーセント倍率だった場合は空文字を返す.
	 */
	public static String toFixedZoom(String zoomMode, String dwgSize, String printSize) {

		String fixedMode = "";

		if( zoomMode.equals("サイズ指定")){
			// マップテーブルに対応する文字列を返す.
			fixedMode = fixedZoomMap.containsKey(printSize) ? fixedZoomMap.get(printSize) : "";

			//指定されたサイズが、実サイズより大きい場合は、実サイズ（"A0", "A1","A2"など）をセットし、同等か小さい場合は、指定された図面サイズをセット。
			if( printSize.equals("ORG") == false &&  DrasapUtil.compareDrwgSize(printSize, dwgSize) > 0 ){ //文字列で用紙サイズを比較すると、A0 < A4, A1 < A4
				fixedMode = dwgSize;
			}

			//出力対象の図面がA0Lの場合で、画面で指定したサイズが「A0」、「A1」、「A2」、「A3」、「A4」の場合は、「用紙にフィット」をセットする。
			if( (dwgSize.equalsIgnoreCase("A0L") || dwgSize.equalsIgnoreCase("A1L") || dwgSize.equalsIgnoreCase("A2L") ) && targetPaperSizeForA0L.containsKey(printSize)){
				fixedMode = "用紙にフィット";
			}
		}

		return fixedMode;
	}

	/**
	 * 印刷サイズからパーセント倍率文字列に変換.
	 * @param printSize 印刷サイズ.
	 * @return パーセント倍率.固定倍率だった場合は空文字を返す.
	 */
	public static String toPercentageZoom(String printSize) {
		// マップテーブルに対応する文字列を返す.
		return PercentageZoomMap.containsKey(printSize) ? PercentageZoomMap.get(printSize) : "";
	}

	/**
	 * //出力対象の図面がA0Lの場合で、画面で指定したサイズが「A0」、「A1」、「A2」、「A3」、「A4」の場合は、その値をセットする。
	 * @param dwgSize 出力対象の図面サイズ
	 * @param printSize　画面の指定サイズ
	 * @return
	 */
	public static String toMediaSize( String dwgSize, String printSize, String printerMasSize ){
		String mediaSize = "自動";

		if( (dwgSize.equalsIgnoreCase("A0L") || dwgSize.equalsIgnoreCase("A1L") || dwgSize.equalsIgnoreCase("A2L") )  && targetPaperSizeForA0L.containsKey(printSize)){
			//出力対象の図面がA0Lの場合で、画面で指定したサイズが「A0」、「A1」、「A2」、「A3」、「A4」の場合
			mediaSize = printSize;
			//最大印刷可能サイズより大きい場合は、最大印刷可能サイズをセットする
			if( DrasapUtil.compareDrwgSize(printSize, printerMasSize) > 0 ){
				mediaSize = printerMasSize;
			}
		}

		return mediaSize;
	}

	/**
	 * 本データ挿入SQLを返す.
	 * @return SQL文.
	 */
	public String insertSql() {

		String[][] ATTRS = {
				{ "JOB_ID", "CHAR" },
				{ "RECIPIENT_ID", "CHAR" },
				{ "RECIPIENT_SUBID", "CHAR" },
				{ "DOCUMENT_ID", "CHAR" },
				{ "DOCUMENT_NAME", "VARCHAR" },
				{ "DOCUMENT_KIND", "CHAR" },
				{ "SEQUENCE_NO", "NUMERIC" },
				{ "FILE_PATH_NAME", "VARCHAR" },
				{ "DATA_FORMAT", "VARCHAR" },
				{ "COPIES", "NUMERIC" },
				{ "SIZE_MODE", "VARCHAR" },
				{ "MEDIA_SIZE", "VARCHAR" },
				{ "MEDIA_Y", "REAL" },
				{ "MEDIA_X", "REAL" },
				{ "ZOOM_MODE", "VARCHAR" },
				{ "FIXED_ZOOM", "VARCHAR" },
				{ "PERCENTAGE_ZOOM", "VARCHAR" },
				{ "RANKDOWN_ZOOM", "VARCHAR" },
				{ "SIZEMAPA0_ZOOM", "VARCHAR" },
				{ "SIZEMAPA1_ZOOM", "VARCHAR" },
				{ "SIZEMAPA2_ZOOM", "VARCHAR" },
				{ "SIZEMAPA3_ZOOM", "VARCHAR" },
				{ "SIZEMAPA4_ZOOM", "VARCHAR" },
				{ "SIZEMAPA0L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA1L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA2L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA3L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA4L_ZOOM", "VARCHAR" },
				{ "PERCENTAGE_ZOOM_X", "VARCHAR" },
				{ "PERCENTAGE_ZOOM_Y", "VARCHAR" },
				{ "ORIENTATION", "VARCHAR" },
				{ "ROTATION", "VARCHAR" },
				{ "MEDIA_TYPE", "VARCHAR" },
				{ "FINISHING", "VARCHAR" },
				{ "OUTPUT_LOCATION", "VARCHAR" },
				{ "CUT_TYPE", "VARCHAR" },
				{ "MEDIA_SOURCE", "VARCHAR" },
				{ "MIRROR", "VARCHAR" },
				{ "TOP_MARGIN", "REAL" },
				{ "RIGHT_MARGIN", "REAL" },
				{ "LEFT_MARGIN", "REAL" },
				{ "BOTTOM_MARGIN", "REAL" },
				{ "LEADING_EDGE", "REAL" },
				{ "TRAILING_EDGE", "REAL" },
				{ "BINDING_TYPE", "VARCHAR" },
				{ "BINDING_EDGE", "REAL" },
				{ "STAMP1_KIND", "VARCHAR" },
				{ "STAMP1_POSITION_X", "REAL" },
				{ "STAMP1_POSITION_Y", "REAL" },
				{ "STAMP1_ORIGIN", "VARCHAR" },
				{ "STAMP1_ID", "VARCHAR" },
				{ "STAMP1_ZOOM", "VARCHAR" },
				{ "STAMP1_TEXT1", "VARCHAR" },
				{ "STAMP1_TEXT2", "VARCHAR" },
				{ "STAMP1_TEXT3", "VARCHAR" },
				{ "STAMP1_COLOR", "VARCHAR" },
				{ "TEXTSTAMP1_TEXT", "VARCHAR" },
				{ "TEXTSTAMP1_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP1_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP1_FONT", "VARCHAR" },
				{ "TEXTSTAMP1_SIZE", "VARCHAR" },
				{ "TEXTSTAMP1_COLOR", "VARCHAR" },
				{ "STAMP2_KIND", "VARCHAR" },
				{ "STAMP2_POSITION_X", "REAL" },
				{ "STAMP2_POSITION_Y", "REAL" },
				{ "STAMP2_ORIGIN", "VARCHAR" },
				{ "STAMP2_ID", "VARCHAR" },
				{ "STAMP2_ZOOM", "VARCHAR" },
				{ "STAMP2_TEXT1", "VARCHAR" },
				{ "STAMP2_TEXT2", "VARCHAR" },
				{ "STAMP2_TEXT3", "VARCHAR" },
				{ "STAMP2_COLOR", "VARCHAR" },
				{ "TEXTSTAMP2_TEXT", "VARCHAR" },
				{ "TEXTSTAMP2_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP2_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP2_FONT", "VARCHAR" },
				{ "TEXTSTAMP2_SIZE", "VARCHAR" },
				{ "TEXTSTAMP2_COLOR", "VARCHAR" },
				{ "STAMP3_KIND", "VARCHAR" },
				{ "STAMP3_POSITION_X", "REAL" },
				{ "STAMP3_POSITION_Y", "REAL" },
				{ "STAMP3_ORIGIN", "VARCHAR" },
				{ "STAMP3_ID", "VARCHAR" },
				{ "STAMP3_ZOOM", "VARCHAR" },
				{ "STAMP3_TEXT1", "VARCHAR" },
				{ "STAMP3_TEXT2", "VARCHAR" },
				{ "STAMP3_TEXT3", "VARCHAR" },
				{ "STAMP3_COLOR", "VARCHAR" },
				{ "TEXTSTAMP3_TEXT", "VARCHAR" },
				{ "TEXTSTAMP3_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP3_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP3_FONT", "VARCHAR" },
				{ "TEXTSTAMP3_SIZE", "VARCHAR" },
				{ "TEXTSTAMP3_COLOR", "VARCHAR" },
				{ "OUTPUT_STATUS", "VARCHAR" },
				{ "OUTPUT_DETAIL_STATUS", "VARCHAR" },
				{ "OUTPUT_DATE", "TIMESTAMP" },
				{ "JOBTICKET_ID", "VARCHAR" },
				{ "ROTATE_EXTRA180", "VARCHAR" },
				{ "REINFORCE", "VARCHAR" },
				{ "PUNCH", "VARCHAR" },
				{ "DATA_SIZE", "VARCHAR" },
				{ "ORG_FILE_NAME", "VARCHAR" },
				{ "SHIFT_ALIGNMENT", "VARCHAR" },
				{ "SHIFT_X", "REAL" },
				{ "SHIFT_Y", "REAL" },
				{ "OUTPUT_WARN_MSG", "VARCHAR" },
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

		return String.format("INSERT INTO %s.SUBMIT_DOCUMENT ( %s ) VALUES ( %s )", this.getSchemaName(), insertAttrs.toString(), insertValues.toString());
	}

	/**
	 * ドキュメントIDを採番.
	 *
	 * @param conn データベースコネクター
	 * @param shortId プリンタショートID
	 * @return JOBIDを返す.
	 * @throws SQLException
	 */
	public static String getNewDocumentID(Connection conn, String schemaName, int schemaNo, String shortId) throws SQLException {

		// 日付を取得.
		String yymm = new SimpleDateFormat("yyMM").format(new Date());

		// 指定のスキーマのID連番管理(OJ_SEQUENCE_X)から連番を取得.
		int seqNo = APlotOJSequenceXDB.getNewSeq(
				conn,
				schemaName, // スキーマ名.
				APlotOJSequenceXDB.JOB_ID_KIND1, // R固定
				shortId, // ショートID
				APlotOJSequenceXDB.JOB_ID_KIND3_DOC, //
				new SimpleDateFormat("yyMM").format(new Date()));

		// 「ID種別（大分類） + スキーマ番号 + ID種別（中分類） + ID種別（小分類） + 年月（YYMM形式） + '-'（固定） + 連番（00000000形式）」
		return String.format("%s%d%s%s%s-%07d", APlotOJSequenceXDB.JOB_ID_KIND1, schemaNo, shortId, APlotOJSequenceXDB.JOB_ID_KIND3_DOC, yymm, seqNo);
	}


}
