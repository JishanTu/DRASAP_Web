package tyk.drasap.search;

import tyk.drasap.aplot.APlotPrintRequest;

/**
 *
 * 2018/02/21 変更 参考図出図をA-PLOT出図に変更.
 * APlotPrintRequestクラスを継承してそちらに機能を移行した.
 * @author hideki_sugiyama
 *
 * ※以下旧履歴
 * 参考図出力用テーブルを取り扱うDBクラス。
 * '04.Nov.23 図番をロギングするように変更
 * 2005-Mar-4 PrintLogerでロギングを追加。
 * @author FUMI
 * 作成日: 2004/01/16
 * 変更日 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class PrintRequestDB extends APlotPrintRequest {
	//
	// 新規のAPlotPrintRequestクラスを作成して継承した為、
	// 下記のメソッドは使用しなくなった.
	//
	//	/**
	//	 * SearchResultElementのリストを元に、参考図出力用テーブルに書き出す。
	//	 * このとき、SearchResultElementは、選択されていて、印刷可能な図面であるもののみ
	//	 * @param searchResultList
	//	 * @param printer
	//	 * @param user
	//	 * @param conn
	//	 * @return
	//	 * @throws Exception
	//	 */
	//	public static int insertRequest(ArrayList<SearchResultElement> searchResultList, Printer printer, User user, Connection conn) throws Exception{
	//
	//		PreparedStatement pstmt1 = null;
	//		int cnt = 0;
	//		String logDrwgNo = null;// ロギングのための図番。catch節で使用するために追加。2005-Mar-4 by Hirata.
	//		try{
	//			// autoCommitをtureに
	//			conn.setAutoCommit(false);
	//			StringBuffer sbSql1 = new StringBuffer("insert into PRINT_REQUEST_TABLE");
	//			sbSql1.append("(JOB_ID,JOB_NAME,PRINTER_ID,DRWG_NO,OUTPUT_SIZE,COPIES,");
	//			sbSql1.append("STAMP_FLAG,USER_ID,USER_NAME,TRANSIT_STAT,REQUEST_DATE)");
	//			sbSql1.append(" values");
	//			// 参考図出力用テーブルのJOB_IDはシーケンスを利用する
	//			sbSql1.append("(TRIM(TO_CHAR(PRINT_REQUEST_SEQ.NEXTVAL,'0000000000000000')),");
	//			sbSql1.append(" '参考図出力', ?, ?, ?, ?,");
	//			sbSql1.append(" ?, ?, ?, 'SET', sysdate)");
	//			//
	//			pstmt1 = conn.prepareStatement(sbSql1.toString());
	//			// ユーザー情報をセットする
	//			pstmt1.setString(6, user.getId());// ユーザーID
	//			pstmt1.setString(7, user.getName());// ユーザー名
	//			// 図番をロギングするため一時保管するリスト
	//			List<String> updatedDrwgNoList = new ArrayList<String>();
	//			for(int i = 0; i < searchResultList.size(); i++){
	//				SearchResultElement searchResultElement = (SearchResultElement) searchResultList.get(i);
	//				// 選択されているもので、かつユーザーが印刷可能の図面のみ、insertする
	//// 2013.06.26 yamagishi modified. start
	////				if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
	//				if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn, (i > 0))) {
	//// 2013.06.26 yamagishi modified. end
	//					pstmt1.setString(1, printer.getId());// プリンタID
	//					pstmt1.setString(2, searchResultElement.getDrwgNo());// 図番
	//					logDrwgNo = searchResultElement.getDrwgNo();// ロギングのために図番をコピーしておく
	//					pstmt1.setString(3, searchResultElement.getDecidePrintSize());// 出力サイズ・・・decidePrintSizeを使用する
	//					pstmt1.setString(4, searchResultElement.getCopies());// 部数
	//					if(printer.isEucPrinter()){
	//						// EUCプリンタなら
	//						pstmt1.setString(5, user.isEucStamp()?"1":"0");// スタンプ有無。スタンプするなら1。
	//					} else {
	//						// 専用プリンタなら
	//						pstmt1.setString(5, user.isPltrStamp()?"1":"0");// スタンプ有無。スタンプするなら1。
	//					}
	//// 2013.06.24 yamagishi modified. start
	//					// 重複リクエストの調査のためのログ。2005-Mar-4 by Hirata.
	////					PrintLoger.info(PrintLoger.ACT_WRITE, user.getId(), searchResultElement.getDrwgNo());
	//					PrintLoger.info(PrintLoger.ACT_WRITE, user, searchResultElement.getDrwgNo());
	//// 2013.06.24 yamagishi modified. end
	//					// ここでOracleに書き出す
	//					cnt += pstmt1.executeUpdate();// insert
	//					// 後でロギングするためにリストに一時保管する
	//					updatedDrwgNoList.add(searchResultElement.getDrwgNo());
	//				}
	//			}
	//			// コミット
	//			conn.commit();
	//			// '04.Nov.23 図番もロギングするように変更
	//			// アクセスログをまとめて行う
	//			AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG,
	//					(String[])updatedDrwgNoList.toArray(new String[0]), user.getSys_id());
	//
	//			return cnt;
	//
	//		} catch(Exception e){
	//// 2013.06.24 yamagishi modified. start
	//			// 重複リクエストの調査のためのログ。2005-Mar-4 by Hirata.
	////			PrintLoger.info(PrintLoger.FAILED_WRITE, user.getId(), logDrwgNo);
	//			PrintLoger.info(PrintLoger.FAILED_WRITE, user, logDrwgNo);
	//// 2013.06.24 yamagishi modified. end
	//			// rollbackする
	//			try{ conn.rollback(); } catch(Exception e2){}
	//			// 例外を呼び出し元に投げる
	//			throw e;
	//
	//		} finally {
	//			// close処理
	//			try{ pstmt1.close(); } catch(Exception e){}
	//		}
	//	}

}
