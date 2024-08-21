/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotPrintRequest.java
 * Name         : A-PLOT出図指示クラス
 * Description  : 画面から「出図」を行った際にA-PLOT出図指示を行うクラス.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Category;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.common.UserException;
import tyk.drasap.printlog.PrintLoger;
import tyk.drasap.search.SearchResultElement;


/**
 * 画面から「出図」を行った際にA-PLOT出図指示を行う.
 *
 * 呼び出し元からは下記のメソッドを呼び出す.
 *
 *  // A-PLOT出図指示.
 *  APlotPrintRequest.insertRequest(
 *             searchResultList,   -----  画面上表示されている図番の検索結果.
 *             printer,            -----  画面上選択されたプリンタ情報.
 *             user,               -----  出図指示を実行したユーザー情報.
 *             conn                -----  データベース接続情報.
 *             );
 *
 * 出図対象は、画面チェックボックスを選択した該当図出図対象のみとなる.
 *
 *
 * @author hideki_sugiyama
 *
 */
public class APlotPrintRequest {

	/** Logger（log4j） */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotPrintRequest.class.getName());

	/**
	 * プリンタマスタ取得.
	 * @param conn DBコネクション.
	 * @param printer 選択プリンタ情報（画面より）
	 * @return プリンタマスタ（配列）
	 * @throws UserException
	 * @throws SQLException
	 */
	private static APlotPrinterMasterDB[] askPrintMaster(Connection conn, Printer printer) throws UserException, SQLException {

		Statement stmt = conn.createStatement();

		// プリンタマスタを DRASAP.PRINTER_ASSIGN_MASTERから取得.
		APlotPrinterMasterDB[] printMaster =
				APlotPrinterMasterDB.getPrinterMaster(stmt,
							new String[] { printer.getId() } // プリンタId.
				);

		// この場合は1件のみヒットするはず.
		if ( printMaster == null ) {
			// A-PLOTなし
			throw new UserException("[" + printer.getDisplayName() + "] プリンタが定義されていません。");
		} else if ( printMaster.length > 1 ) {
			// A-PLOTなし
			throw new UserException("[" + printer.getDisplayName() + "] プリンタが複数定義されています。");
		}

		return printMaster;
	}

	/**
	 * 配布先マスタ取得.
	 * @param conn DBコネクション.
	 * @param printMaster プリンタマスタ情報
	 * @return 配布先マスタ（配列）
	 * @throws UserException
	 * @throws SQLException
	 */
	private static APlotRecipientMasterDB[] askRecipientMaster(Connection conn, APlotPrinterMasterDB printMaster) throws UserException, SQLException {

		Statement stmt = conn.createStatement();

		// 配布先マスタを 指定スキーマの RECIPIENT_MASTER から取得.
		APlotRecipientMasterDB[] recipieMaster =
				APlotRecipientMasterDB.getRecipientMaster(stmt,
						printMaster.getSchemaName(),   // スキーマ名.
						new String[] { printMaster.getPrinterId() }        // プリンタId.
				);

		// この場合は1件のみヒットするはず.
		if ( recipieMaster == null ) {
			// A-PLOTなし
			throw new UserException("[" + printMaster.getPrinterName() + "] プリンタが定義されていません。");
		}

		return recipieMaster;
	}


	/**
	 * サブミットジョブデータを作成する.
	 * @param conn DBコネクション.
	 * @param printMaster プリンタマスタ情報.
	 * @param user ユーザー情報.
	 * @return サブミットジョブデータ.
	 * @throws SQLException
	 */
	private static APlotSubmitJobDB createSubmitJob(Connection conn, APlotPrinterMasterDB printMaster , User user) throws SQLException {

		//
		String schema = printMaster.getSchemaName();

		// JOBIDを採番.
		String jobId = APlotSubmitJobDB.getNewJobID(conn,
				schema,                 // スキーマ名.
				printMaster.getSchemaNo(), // スキーマ番号.
				printMaster.getShortId());                // ショートID.

		// 参考図出図のジョブデータを作成.
		APlotSubmitJobDB jobData = new APlotSubmitJobDB(schema, jobId, "参考図出図", user.getId(), user.getName());
		// 属性設定.
		jobData.put("COVER_PAGE", "なし");  // 表紙（標準書式）作成指示
		jobData.put("PRIORITY", "中");      // 優先度
		String printId = printMaster.getPrinterId();
		printId = printId.trim();
		int printerIdLength = printId.length();
		int cutPos = printerIdLength - 3;
		if( cutPos < 0 ){
			cutPos = 0;
		}

		String queueId = printId.substring(cutPos);
		jobData.put("RECEIVE_QUEUE_ID", queueId);      // 受付キューID

		return jobData;
	}

	/**
	 * サブミット配布先情報を作成する.
	 * @param conn DBコネクション.
	 * @param job ジョブデータ.
	 * @param recipieMaster 配布先マスタ情報.
	 * @return サブミット配布先情報.
	 * @throws SQLException
	 */
	private static APlotSubmitRecipientDB createSubmitRecipient(Connection conn, APlotSubmitJobDB job, APlotRecipientMasterDB recipieMaster) throws SQLException {

		APlotSubmitRecipientDB recipe = new APlotSubmitRecipientDB(job.getSchemaName());
		recipe.put("JOB_ID", job.getJobID());
		recipe.put("RECIPIENT_ID", recipieMaster.get("RECIPIENT_ID")); // 配布先ID
		recipe.put("RECIPIENT_SUBID", "1");// 配布先ID内連番は1固定のはず.
		recipe.put("RECIPIENT_NAME", recipieMaster.get("RECIPIENT_NAME")); // 配布先名
		recipe.put("SEQUENCE_NO",  "1"); // シーケンス番号 1固定 ????
		recipe.put("PRINTER_ID", recipieMaster.get("PRINTER_ID"));
		recipe.put("DUE_MODE", "しない");    // 時刻指定モード
//			recipient.put("OUTPUT_STATUS", ""); 後で出図待ちに変える.
		recipe.put("OUTPUT_ORDER", recipieMaster.get("OUTPUT_ORDER")); // 排紙順
		recipe.put("DATA_FOLDER_PATH", recipieMaster.get("DATA_FOLDER_PATH")); // 転送先ディレクトリ

		return recipe;
	}

	/**
	 * サブミットドキュメントデータを作成する.
	 * @param conn DBコネクション.
	 * @param printMaster プリンタマスタ情報.
	 * @param recipieMaster 配布先マスタ情報.
	 * @param job ジョブデータ.
	 * @param idx 図番.
	 * @param element 図面情報（画面より）
	 * @param userStampText 参考図スタンプを押印するか
	 * @return サブミットドキュメントデータ.
	 * @throws SQLException
	 * @throws IOException
	 * @throws UserException
	 */
	private static APlotSubmitDocumentDB createSubmitDocument(Connection conn, APlotPrinterMasterDB printMaster, APlotRecipientMasterDB recipieMaster, APlotSubmitJobDB job, int idx, SearchResultElement element, String userStampText) throws SQLException, IOException, UserException {

		// スキーマ.
		String schemaName = job.getSchemaName();
		// スキーマ番号.
		int schemaNo = job.getSchemaNo();


		// ドキュメントIDを採番.
		String docId = APlotSubmitDocumentDB.getNewDocumentID(conn,
				schemaName,                   // スキーマ名.
				schemaNo,                     // スキーマ番号.
				printMaster.getShortId());    // ショートID.

		// ---------------------------------------------------------------------
		// ADMIN_SETTING_MASTERからシステム値を取得.
		// 図面ファイル格納フォルダパス（Valuts）.
		String valutsFolderPath = APlotSystemMasterDB.getInstance().getVaultsFolderPath();
		//
		// 図面ファイルスプール先フォルダパス.
		String spoolFolderPath = APlotSystemMasterDB.getInstance().getSpoolFolderPath(schemaNo);

		// ドキュメントフルパス.
		String documentFolderPath = APlotSystemMasterDB.getInstance().getDocumentFolderPath(schemaNo);

		// スプール先へファイルをコピー(失敗したら削除する).
		String fromFile = Paths.get(valutsFolderPath, element.getPathName(), element.getFileName()).normalize().toString();

		// コピー先ファイルパス
		String toFile =  Paths.get(spoolFolderPath, docId + "_" + element.getFileName()).normalize().toString();

		// ドキュメントフルパス.
		String docFile = Paths.get(documentFolderPath, docId + "_" + element.getFileName()).normalize().toString();

		// ファイルをコピー.
		copyFile(fromFile, toFile);

		// ドキュメントデータ作成.
		APlotSubmitDocumentDB doc = new APlotSubmitDocumentDB(job.getSchemaName());
		doc.put("JOB_ID", job.getJobID());							// ジョブID
		doc.put("RECIPIENT_ID", recipieMaster.get("RECIPIENT_ID"));	// 配布先ID
		doc.put("RECIPIENT_SUBID", "1");							// 配布先ID内連番
		doc.put("DOCUMENT_ID", docId);								// ドキュメントID
		doc.put("DOCUMENT_NAME", element.getDrwgNo());				// ドキュメント名(図番号)
		doc.put("DOCUMENT_KIND", "D");								// ドキュメント種別
		doc.put("SEQUENCE_NO", String.format("%05d", idx+1));		// シーケンス番号
		doc.put("FILE_PATH_NAME", docFile);							// ファイルフルパス名
		doc.put("DATA_FORMAT", "TIFF");								// データフォーマット
		doc.put("COPIES", element.getCopies());						// 部数
		doc.put("SIZE_MODE", "サイズ指定");							// 用紙サイズモード

		//原図のサイズ
		String dwgSize = element.getAttr("DRWG_SIZE");
		//画面指定の印刷サイズ
		String printSize = element.getPrintSize();
		//プリンタの最大出力サイズ
		String printerMaxSize = element.getPrinterMaxSize();

		doc.put("MEDIA_SIZE", APlotSubmitDocumentDB.toMediaSize(dwgSize, printSize, printerMaxSize) );								// 用紙サイズ指定

		// スケーリングモード.
		String zoomMode = APlotSubmitDocumentDB.toZoomMode(element.getPrintSize());
		doc.put("ZOOM_MODE",zoomMode ); // スケーリングモード
		doc.put("FIXED_ZOOM",
				APlotSubmitDocumentDB.toFixedZoom( zoomMode, dwgSize, printSize)); // スケーリングモード

		doc.put("PERCENTAGE_ZOOM",
				APlotSubmitDocumentDB.toPercentageZoom(element.getPrintSize())); // スケーリングモード
		doc.put("PERCENTAGE_ZOOM_X", "100");	// X方向パーセント倍率
		doc.put("PERCENTAGE_ZOOM_Y", "100");	// Y方向パーセント倍率
		doc.put("ORIENTATION", "指定なし");		// 印刷方向
		doc.put("ROTATION", "0");				// イメージの回転

		if ( "DEDICATED".equals(printMaster.get("OUTPUT_DEVICE").toString()) ) {
			// プリンタマスタ情報の出力装置が「DEDICATED」の場合のみ設定.
			doc.put("MEDIA_TYPE", recipieMaster.get("MEDIA_TYPE"));				// 用紙種類
			doc.put("FINISHING", recipieMaster.get("FINISHING"));				// 折り
			doc.put("OUTPUT_LOCATION", recipieMaster.get("OUTPUT_LOCATION"));	// 排紙先
			doc.put("CUT_TYPE", recipieMaster.get("CUT_TYPE"));				// カット方法
			doc.put("MEDIA_SOURCE", recipieMaster.get("MEDIA_SOURCE"));		// 給紙装置
			doc.put("LEADING_EDGE", recipieMaster.get("LEADING_EDGE"));		// 先端余白
			doc.put("TRAILING_EDGE", recipieMaster.get("TRAILING_EDGE"));	// 後端余白
			doc.put("BINDING_EDGE", recipieMaster.get("BINDING_EDGE"));		//綴じ代
		}
		doc.put("MIRROR", "off");				// イメージ反転

		// STAMP 1 （該当図）
		String dwgNo = element.getDrwgNo();
		boolean isCorrpd = false;

		try{
			isCorrpd = AclvMasterDB.isCorresponding(dwgNo, conn);
		}
		catch( Exception ex ){
			throw new UserException(ex.getMessage());
		}

		String stampKind = "None";

		if( isCorrpd ){
			stampKind = "IMAGE";
			APlotSystemMasterDB.APlotStampData stamp1 = APlotSystemMasterDB.getInstance().getStamp1Data();
			doc.put("STAMP1_KIND",stampKind);		// スタンプ 1 種別
			doc.put("STAMP1_POSITION_X", stamp1.positionX);	// スタンプ 1 刻印位置 X
			doc.put("STAMP1_POSITION_Y", stamp1.positionY);	// スタンプ 1 刻印位置 Y
			doc.put("STAMP1_ORIGIN", stamp1.origin);		// スタンプ 1原点
			doc.put("STAMP1_ID", stamp1.id);				// スタンプ 1 ID
			doc.put("STAMP1_ZOOM", stamp1.zoom);			// イメージスタンプ/バナー 1 サイズ
			doc.put("STAMP1_TEXT1",stamp1.stampText1);		// イメージスタンプ/バナー 1 挿入文字列 1
			doc.put("STAMP1_COLOR", stamp1.color);			// イメージスタンプ/バナー 1 文字濃淡
		}
		else{
			doc.put("STAMP1_KIND",stampKind);		// スタンプ 1 種別
		}

		// STAMP 2（参考図）
		if ( userStampText != null && !"".equals(userStampText) ) {
			APlotSystemMasterDB.APlotStampData stamp2 = APlotSystemMasterDB.getInstance().getStamp2Data();
			doc.put("STAMP2_KIND","IMAGE");		// スタンプ 1 種別
			doc.put("STAMP2_POSITION_X", stamp2.positionX);	// スタンプ 1 刻印位置 X
			doc.put("STAMP2_POSITION_Y", stamp2.positionY);	// スタンプ 1 刻印位置 Y
			doc.put("STAMP2_ORIGIN",  stamp2.origin);		// スタンプ 1原点
			doc.put("STAMP2_ID", stamp2.id);			// スタンプ 1 ID
			doc.put("STAMP2_ZOOM", stamp2.zoom);			// イメージスタンプ/バナー 1 サイズ
			doc.put("STAMP2_TEXT1",userStampText);	// イメージスタンプ/バナー 1 挿入文字列 1
			doc.put("STAMP2_COLOR", stamp2.color);	// イメージスタンプ/バナー 1 文字濃淡
		} else {
			doc.put("STAMP2_KIND","None");		// スタンプ 1 種別
		}

		// STAMP 3
		doc.put("STAMP3_KIND", "None");			// スタンプ種別3(JTEKTはスタンプ3は使用しない）

		//
		doc.put("ROTATE_EXTRA180", "off");		// イメージ180度回転
		doc.put("REINFORCE", "off");			// レインフォース
		doc.put("PUNCH", "off");				// パンチ穴
		doc.put("DATA_SIZE", element.getAttr("DRWG_SIZE") );			// データサイズ,INDEX_DB.DRWG_SIZEをセットする
		doc.put("ORG_FILE_NAME", element.getFileName());		// オリジナルファイル名
		doc.put("SHIFT_ALIGNMENT", "center");	// 印刷位置シフト位置合わせ
		doc.put("SHIFT_X", 0);	// 印刷位置シフトX方向
		doc.put("SHIFT_Y", 0);	// 印刷位置シフトY方向

		return doc;
	}


	/**
	 * 対応するスキーマの出図指示情報を登録する.
	 *
	 * @param conn
	 * @param job
	 * @param recipeList
	 * @param docList
	 * @return
	 * @throws SQLException
	 */
	private static int insertDatas(Connection conn, APlotSubmitJobDB job, ArrayList<APlotSubmitRecipientDB> recipeList, ArrayList<APlotSubmitDocumentDB> docList) throws SQLException {
		int cnt = 0; // 登録数.

		// トランザクションを設定する
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		try{
			// ジョブデータ登録.
			String sql = job.insertSql();
			category.debug(sql);
			stmt.executeUpdate(sql);
			category.info("A-PLOT出図 割当スキーマ:" + job.getSchemaName() + " JOB ID:" + job.getJobID() );

			// 配布先情報登録.
			for ( APlotSubmitRecipientDB recipie : recipeList ) {
				sql = recipie.insertSql();
				category.debug(sql);
				stmt.executeUpdate(sql);
				category.info("A-PLOT出図 配布先ID:" + recipie.get("RECIPIENT_ID").toString() + " 配布先:" + recipie.get("RECIPIENT_NAME").toString());
			}
			// ドキュメント情報登録.
			for ( APlotSubmitDocumentDB doc : docList ) {
				sql = doc.insertSql();
				category.debug(sql);
				int ret = stmt.executeUpdate(sql);
				category.info("A-PLOT出図 ドキュメントID:" + doc.get("DOCUMENT_ID").toString() + " ドキュメント名:" + doc.get("DOCUMENT_NAME").toString());
				cnt = ret + cnt;
			}

			// 最終的に配布先情報のステータスを出図待ちにする（意味あるかわからないけど）
			for ( APlotSubmitRecipientDB recipie : recipeList ) {
				sql = recipie.updateStatusSql( job.getJobID(), recipie.get("RECIPIENT_ID").toString() ,"出図待ち");
				category.debug(sql);
				stmt.executeUpdate(sql);
				category.info("A-PLOT出図 ステータス変更 配布先ID:" + recipie.get("RECIPIENT_ID").toString() + " ステータス:出図待ち");
			}

		} catch (SQLException ex) {
			category.fatal("A-PLOT出図 出図処理モジュールへ出図指示情報登録でSQLエラー", ex);
			throw ex;
		} finally {
		}
		category.info("A-PLOT出図 ドキュメント登録数:" + cnt);
		return cnt;
	}

	/**
	 * ファイルをコピーする.
	 * コピー先のフォルダがない場合は自動的に作成する.
	 * @param fromFilePath 元ファイルのフルパス.
	 * @param toDirPath コピー先フォルダパス.
	 * @throws IOException
	 */
	private static void copyFile(String fromFilePath, String toFilePath ) throws IOException {

		try {
			// 元ファイル.
			File fromFile = new File(fromFilePath);
			// コピー先ファイル.
			File toFile = new File(toFilePath);

			// コピー先フォルダ.
			File toDir = new File(toFile.getParent());

			if ( !fromFile.exists() || !fromFile.isFile() ) {
				// 指定ファイルが存在しない.
				throw new IOException("指定のファイルが存在しません. [" + fromFile.getPath() + "]");
			}

			// コピー先フォルダが存在しない場合は作成する.
			if ( !toDir.exists() ) {
				// フォルダ作成.
				if ( !toDir.mkdirs() ) {
					// フォルダの作成に失敗.
					throw new IOException("フォルダの作成ができませんでした. [" + toDir.getPath() + "]");
				}
			} else if ( !toDir.isDirectory() ) {
				// 指定のパスがフォルダではない.
				throw new IOException("指定のコピー先パスがフォルダではない. [" + toDir.getPath() + "]");
			}
			category.debug("copy " + fromFile.toPath() + " -> " + toFile.toPath());
			// New I/Oでファイルをコピー.
			Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING );

		} catch (IOException ex) {
			category.fatal("A-PLOT出図 図面ファイルスプール処理でエラー", ex);
			throw ex;
		} finally {

		}
	}


	/**
	 * SearchResultElementのリストを元に、APLOTテーブルに書き出す。
	 * このとき、SearchResultElementは、選択されていて、印刷可能な図面であるもののみ
	 *
	 * 出力方式をA-PLOTプリンタに変更.
	 * 指定のプリンターIDに該当するスキーマーをPRINTER_ASSIGN_MASTERテーブルから取得し、出力情報を下記JOBデータとして登録する.
	 *  submit_job （サブミットジョブ）
	 *  submit_recipient （サブミット配布先）
	 *  submit_document （サブミットドキュメント）
	 *
	 * @param searchResultList 画面上表示されている図番の検索結果.
	 * @param printer 画面上選択されたプリンタ情報.
	 * @param user 出図指示を実行したユーザー情報.
	 * @param conn データベース接続情報.
	 * @return 出力件数.
	 * @throws Exception
	 */
	public static int insertRequest(ArrayList<SearchResultElement> searchResultList, Printer printer, User user, Connection conn) throws Exception {

		// 戻り値：
		int cnt = 0;

		// 本日日付.
		Date outputDate = new Date();

		// エラー時のログ出力用図番.
		String logDrwgNo = null;
		try {
			// 自動コミットをしない.
			conn.setAutoCommit(false);

			// スタンプ2（参考図）用設定：日付形式.
			String stamp2outputDate = new SimpleDateFormat(APlotSystemMasterDB.getInstance().getStamp2Data().dateFormat).format(outputDate);

			// プリンタマスタを DRASAP.PRINTER_ASSIGN_MASTERから取得.
			APlotPrinterMasterDB[] printMaster = askPrintMaster(conn, printer);

			// 配布先マスタを 指定スキーマの RECIPIENT_MASTER から取得.
			APlotRecipientMasterDB[] recipieMaster = askRecipientMaster(conn, printMaster[0]);

			for(int drwIdx = 0; drwIdx < searchResultList.size(); drwIdx++ ) {
				// 選択している印刷可能（該当スタンプ押印対象）図番のみ対象.
				SearchResultElement element = (SearchResultElement) searchResultList.get(drwIdx);
				if (element.isSelected() && user.isPrintableByReq(element, conn, (drwIdx > 0))) {
					// ジョブの登録 >>>>>>>>>>>>>>>>>>>>>>>>、１Job１図番に変更する

					// 参考図出図のジョブデータを作成.
					APlotSubmitJobDB jobData = createSubmitJob(conn, printMaster[0], user);

					// 図番をロギングするため一時保管するリスト
					List<String> updatedDrwgNoList = new ArrayList<String>();

					// 配布先情報のデータを作成.
					// サブミット配布先(JTEKTの参考図出図はデータ上1つしかありえないはず）
					ArrayList<APlotSubmitRecipientDB> recipientData = new ArrayList<APlotSubmitRecipientDB>();
					recipientData.add(createSubmitRecipient(conn, jobData, recipieMaster[0]));

					// サブミットドキュメント 選択図面数分情報を登録.
					ArrayList<APlotSubmitDocumentDB> documentData = new ArrayList<APlotSubmitDocumentDB>();
	//			for(int drwIdx = 0; drwIdx < searchResultList.size(); drwIdx++ ) {
					//
//					SearchResultElement element = (SearchResultElement) searchResultList.get(drwIdx);

					// 選択している印刷可能（該当スタンプ押印対象）図番のみ対象.
	//余計なJob採番をしないためループ先頭へ移動				if (element.isSelected() && user.isPrintableByReq(element, conn, (drwIdx > 0))) {
						// エラーログ用に保持.
						logDrwgNo = element.getDrwgNo();

						// 参考図出図スタンプ. この変数が空の場合は参考図スタンプを押印しない.
						String userStampText = "";
						if( ( printer.isEucPrinter() && user.isEucStamp() ) || user.isPltrStamp() ){
							userStampText = String.format("%s%s  %s", APlotSystemMasterDB.getInstance().getStamp2Data().stampText1, stamp2outputDate , user.getName());
						}

						// ドキュメントデータ作成.
						documentData.add(createSubmitDocument(conn, printMaster[0], recipieMaster[0], jobData, drwIdx, element, userStampText ));

						// ログを出力.
						PrintLoger.info(PrintLoger.ACT_WRITE, user, element.getDrwgNo());

						// 後でロギングするためにリストに一時保管する
						updatedDrwgNoList.add(element.getDrwgNo());
	//			}

					// 印刷対象図面がある場合にデータを登録する. >>>>>>>>>>>>>>>>>>>>
					int insertedCnt = insertDatas(conn, jobData, recipientData, documentData);
					cnt += insertedCnt;

					// アクセスログをまとめて行う
					AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG,
							(String[])updatedDrwgNoList.toArray(new String[0]), user.getSys_id());
				}

				conn.commit();
			}
		} catch (SQLException | UserException | IOException ex) {
			// エラーのためログを確認.
			if ( logDrwgNo != null ) { PrintLoger.info(PrintLoger.FAILED_WRITE, user, logDrwgNo); }
			conn.rollback();
			throw ex;
		} finally {
		}
		return cnt;
	}

}
