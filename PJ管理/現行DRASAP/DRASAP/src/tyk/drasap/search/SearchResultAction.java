package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.printlog.PrintLoger;

/**
 * <PRE>
 * 検索結果をリスト表示したあとのAction。
 * 2005-Mar-4 PrintLogerによるログを追加。
 * </PRE>
 * 変更日 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
public class SearchResultAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(SearchResultAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}
	// --------------------------------------------------------- Methods

	/**
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
		HttpServletRequest request,	HttpServletResponse response) throws Exception {
		category.debug("start");
		ActionMessages errors = new ActionMessages();
		SearchResultForm searchResultForm = (SearchResultForm) form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");

		//
		int offset = Integer.parseInt(searchResultForm.getDispNumberOffest());// 今のoffset値
		int dispNumberPerPage = Integer.parseInt(searchResultForm.getDispNumberPerPage());// 1ページ当たりの表示件数
		// act属性による処理の切り分け
		if("PREV".equals(searchResultForm.getAct())){
			// 前へなら
			// オフセット値を前にずらす
			offset = offset - dispNumberPerPage;
			if(offset < 0){
				offset = 0;
			}
			searchResultForm.setDispNumberOffest(String.valueOf(offset));//オフセット値を変更する
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return  mapping.findForward("result");

		} else if("NEXT".equals(searchResultForm.getAct())){
			// 次へなら
			// オフセット値と検索結果数を比較して、可能ならオフセット値を変更する
			if(searchResultForm.getSearchResultList().size() > (offset + dispNumberPerPage)){
				searchResultForm.setDispNumberOffest(String.valueOf(offset + dispNumberPerPage));
			}
			//
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return  mapping.findForward("result");

		} else if("REFRESH".equals(searchResultForm.getAct())){
			// 再表示なら
			// オフセット値をゼロに
			searchResultForm.setDispNumberOffest("0");//オフセット値を変更する
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			// ユーザーマスターに選択した表示項目をセットする
			updateUserInfo(searchResultForm, user, errors);
			if(errors.isEmpty()){
				return  mapping.findForward("result");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}

		} else if("CHANGELANGUAGE".equals(searchResultForm.getAct())){
			// 再表示なら
			// オフセット値をゼロに
			searchResultForm.setDispNumberOffest("0");//オフセット値を変更する
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			// ユーザーマスターに選択した表示項目をセットする
			updateUserInfo(searchResultForm, user, errors);
			if(errors.isEmpty()){
				return  mapping.findForward("result");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}

		} else if("CHECK_ON".equals(searchResultForm.getAct())){
			// 全てにチェック
			for(int i = 0; i < searchResultForm.getSearchResultList().size(); i++){
				((SearchResultElement)searchResultForm.getSearchResultList().get(i)).setSelected(true);
			}
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return  mapping.findForward("result");

		} else if("CHECK_OFF".equals(searchResultForm.getAct())){
			// 全てのチェックを外す
			for(int i = 0; i < searchResultForm.getSearchResultList().size(); i++){
				((SearchResultElement)searchResultForm.getSearchResultList().get(i)).setSelected(false);
			}
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return  mapping.findForward("result");

		} else if("PRINT".equals(searchResultForm.getAct())){
			// 印刷の指示をする
			// 1) 次のチェックを行う
			// - 指定した枚数のチェック
			// - 出力プロッタは選択されている?
			// - 正しい出力サイズを指定している?
			checkForPrint(searchResultForm, user, drasapInfo, errors);
			if(!errors.isEmpty()){
				saveErrors(request, errors);
				category.debug("--> search_error");
				return  mapping.findForward("search_error");
			}
			// 2) 印刷可能でない図番を指示していないか?
			if(hasNotPrintable(searchResultForm, user)){
				category.debug("--> notPrintable");
				return  mapping.findForward("notPrintable");
			}
// 2013.06.24 yamagishi modified. start
			// 3) 参考図出力用テーブルに出力する
			// 重複リクエストの調査のためのログ。2005-Mar-4 by Hirata.
//			PrintLoger.info(PrintLoger.ACT_RECEIVE, user.getId());
			PrintLoger.info(PrintLoger.ACT_RECEIVE, user);
// 2013.06.24 yamagishi modified. end

			int requestCount = requestPrint(searchResultForm, user, errors);
			if(errors.isEmpty()){
				// 成功したメッセージを
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.success.print.request." + user.getLanKey(),
								String.valueOf(requestCount)));
				// 全てのチェックを外す '04.Feb.5
				for(int i = 0; i < searchResultForm.searchResultList.size(); i++){
					searchResultForm.getSearchResultElement(i).setSelected(false);
				}
				// アクセスログを
				// '04.Nov.23 PrintRequestDBで図番ごとにロギングするように変更したので
				// コメントアウトする。
				// AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG);

			}
			saveErrors(request, errors);
			category.debug("--> search_error");
			return  mapping.findForward("search_error");

		} else if("OUT_CSV".equals(searchResultForm.getAct())){
			// 全属性かどうかを、request#setAttributeする
			request.setAttribute("OUT_CSV_ALL", searchResultForm.getOutCsvAll());
			// 検索結果をファイル出力する
			category.debug("--> out_csv");
			return mapping.findForward("out_csv");

		} else if("ACLV_CHG".equals(searchResultForm.getAct())){
			// アクセスレベルの変更画面へ
			category.debug("--> aclv_change");
			return mapping.findForward("aclv_change");
		} else if("DELETEDWG".equals(searchResultForm.getAct())){
			category.debug("--> DELETEDWG");
			session.setAttribute("searchResultForm", searchResultForm);
			return mapping.findForward("deletedwg");
// 2019.10.17 yamamoto add. start
		} else if("MULTI_PDF".equals(searchResultForm.getAct())){
			session.setAttribute("searchResultForm", searchResultForm);
			// 選択した図面を1ファイルPDFにしてダウンロードする
			category.debug("--> MULTI_PDF");
			return mapping.findForward("multi_pdf");
// 2019.10.17 yamamoto add. end
// 2020.03.10 yamamoto add. start
		} else if("PDF_ZIP".equals(searchResultForm.getAct())){
			session.setAttribute("searchResultForm", searchResultForm);
			// 選択した図面をzipでダウンロードする
			category.debug("--> PDF_ZIP");
			return mapping.findForward("multi_pdf");
// 2020.03.10 yamamoto add. end
		}
		return null;
	}
	/**
	 * 再表示で表示項目を変更したときに、その表示項目をユーザーマスタに登録する。
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 */
	private void updateUserInfo(SearchResultForm searchResultForm, User user, ActionMessages errors){
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			String strSql1 = "update USER_MASTER set " +
						" VIEW_SELCOL1=?, VIEW_SELCOL2=?, VIEW_SELCOL3=?, VIEW_SELCOL4=?, VIEW_SELCOL5=?, VIEW_SELCOL6=?" +
						" where USER_ID=?";
			pstmt1 = conn.prepareStatement(strSql1);
			pstmt1.setString(1, searchResultForm.getDispAttr1());
			pstmt1.setString(2, searchResultForm.getDispAttr2());
			pstmt1.setString(3, searchResultForm.getDispAttr3());
			pstmt1.setString(4, searchResultForm.getDispAttr4());
			pstmt1.setString(5, searchResultForm.getDispAttr5());
			pstmt1.setString(6, searchResultForm.getDispAttr6());
			pstmt1.setString(7, user.getId());
			pstmt1.executeUpdate();
			// ユーザーObjectにもセットする
			user.setViewSelCol1(searchResultForm.getDispAttr1());
			user.setViewSelCol2(searchResultForm.getDispAttr2());
			user.setViewSelCol3(searchResultForm.getDispAttr3());
			user.setViewSelCol4(searchResultForm.getDispAttr4());
			user.setViewSelCol5(searchResultForm.getDispAttr5());
			user.setViewSelCol6(searchResultForm.getDispAttr6());

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.update.user." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ pstmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	}
// 2013.06.26 yamagishi modified. start
	/**
	 * 印刷不可能な図面がチェックされていたら true を返す。
	 * Tiff以外である。または印刷権限がない。
	 * @param searchResultForm
	 * @return Tiff以外の図面がチェックされていたら true
	 */
//	private boolean hasNotPrintable(SearchResultForm searchResultForm, User user){
	private boolean hasNotPrintable(SearchResultForm searchResultForm, User user) throws Exception {
		Connection conn = null;

		try{
			conn = ds.getConnection();

			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// 選択されていて、かつユーザーが印刷できなければ・・・警告対象
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
//				if(searchResultElement.isSelected() && ! user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && !user.isPrintableByReq(searchResultElement, conn, (i > 0))) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			throw e;
		} finally {
			try { conn.close(); } catch (Exception e) {}
		}
	}
// 2013.06.26 yamagishi modified. end

// 2013.06.26 yamagishi modified. start
	/**
	 * 印刷指示まえのチェックを行う。
	 * エラーがあれば、errorsに加える。
	 * @param searchResultForm
	 * @param drasapInfo
	 * @param errors
	 */
//	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, ActionMessages errors){
	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, ActionMessages errors) throws Exception {
		Connection conn = null;

		try{
			conn = ds.getConnection();

			// 1) 指定した枚数のチェック
			int selectedDrwgCount = 0;
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// 選択されていて、かつユーザーが印刷可能なら
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
//				if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn, (i > 0))) {
					selectedDrwgCount++;// インクリメント

				}
			}
			if (selectedDrwgCount == 0) {
				// １つも選択していない
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.no.selected." + user.getLanKey()));

			} else if (selectedDrwgCount > drasapInfo.getPrintRequestMax()) {
				// 上限を超えた件数を指定
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.print.request.max." + user.getLanKey(),
						String.valueOf(drasapInfo.getPrintRequestMax()), String.valueOf(selectedDrwgCount)));
			}
			// 2) 出力プロッタは選択されている?
			if(searchResultForm.getOutputPrinter() == null || searchResultForm.getOutputPrinter().equals("")){
				String requiredStr;
				if (user.getLanKey().equals("jp")) {
					requiredStr = "出力プロッタ";
				} else {
					requiredStr = "PLOTTER";
				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.required." + user.getLanKey(), requiredStr));
			} else {
			// 出力先が指定されていれば、出力サイズの指定をチェックする
				Printer selectedPrinter = user.getPrinter(searchResultForm.getOutputPrinter());
				for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
					// 選択されていて、かつユーザーが印刷可能なら
					SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
//					if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
					if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn)) {
						// 原図のサイズ、指定したサイズ、印刷したいプリンタの組み合わせから、
						// 参考図出力用テーブルの出力サイズにセットするサイズを求める
						// その値をsearchResultElementにせっとする
						searchResultElement.setDecidePrintSize(
													DrasapUtil.decidePrintSizeForRequest(
													searchResultElement.getAttr("DRWG_SIZE"),	// 原図のサイズ
													searchResultElement.getPrintSize(),			// 指定サイズ
													selectedPrinter));// プリンター
						if (searchResultElement.getDecidePrintSize() == null) {
							// 指定サイズがエラー
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.print.nomatch.size." + user.getLanKey(),
									searchResultElement.getDrwgNoFormated()));
						}
						else{
							//プリンタの最大印刷サイズを取得する
							searchResultElement.setPrinterMaxSize(selectedPrinter.getMaxSize());
						}
					}
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			try { conn.close(); } catch (Exception e) {}
		}
	}
// 2013.06.26 yamagishi modified. end
	/**
	 * 参考図の出力指示をする。参考図出力依頼テーブルに書き出す。
	 * @param searchResultForm
	 * @param user
	 * @param errors
	 * @return 指示した件数
	 */
	private int requestPrint(SearchResultForm searchResultForm, User user, ActionMessages errors){
		// 選択されたプリンタのオブジェクトを取得
		Printer selectedPrinter = user.getPrinter(searchResultForm.getOutputPrinter());
		//
		int cnt = 0;
		Connection conn = null;
		try{
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.print.request." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("参考図の出力指示に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		return cnt;
	}
}
