package tyk.drasap.search;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.printlog.PrintLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * 検索結果をリスト表示したあとのAction。
 * 2005-Mar-4 PrintLogerによるログを追加。
 * </PRE>
 * 変更日 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
@Controller
@SessionAttributes("searchResultForm")
public class SearchResultAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods

	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/result")
	public String execute(
			@ModelAttribute("searchResultForm") SearchResultForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		SearchResultForm searchResultForm = form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		if (user.getThumbnailSize() == null) {
			session.setAttribute("thumbnailSize", "M");
		} else {
			session.setAttribute("thumbnailSize", user.getThumbnailSize());
		}

		//
		int offset = Integer.parseInt(searchResultForm.getDispNumberOffest());// 今のoffset値
		int dispNumberPerPage = Integer.parseInt(searchResultForm.getDispNumberPerPage());// 1ページ当たりの表示件数
		// act属性による処理の切り分け
		if ("PREV".equals(searchResultForm.getAct())) {
			// 前へなら
			// オフセット値を前にずらす

			offset = offset - dispNumberPerPage;
			if (offset < 0) {
				offset = 0;
			}
			searchResultForm.setAct("");// act属性をクリア
			searchResultForm.setDispNumberOffest(String.valueOf(offset));//オフセット値を変更する
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("NEXT".equals(searchResultForm.getAct())) {
			// 次へなら
			if (searchResultForm.getSearchResultList().size() > offset + dispNumberPerPage) {
				searchResultForm.setDispNumberOffest(String.valueOf(offset + dispNumberPerPage));
			}
			//
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("REFRESH".equals(searchResultForm.getAct())
				|| "CHANGELANGUAGE".equals(searchResultForm.getAct())) {
			// 再表示なら
			// オフセット値をゼロに
			searchResultForm.setDispNumberOffest("0");//オフセット値を変更する
			searchResultForm.setAct("");// act属性をクリア
			for (int i = 1; i <= searchResultForm.getViewSelColNum(); i++) {
				String dispAttr = request.getParameter("dispAttr" + i);
				searchResultForm.getDispAttrList().set(i - 1, dispAttr);
			}
			session.setAttribute("searchResultForm", searchResultForm);
			// ユーザーマスターに選択した表示項目をセットする
			updateUserInfo(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				return "result";
			}
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "error";

		}
		if ("CHECK_ON".equals(searchResultForm.getAct())) {
			// 全てにチェック
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				searchResultForm.getSearchResultList().get(i).setSelected(true);
			}
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("CHECK_OFF".equals(searchResultForm.getAct())) {
			// 全てのチェックを外す
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				searchResultForm.getSearchResultList().get(i).setSelected(false);
			}
			searchResultForm.setAct("");// act属性をクリア
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("PRINT".equals(searchResultForm.getAct())) {
			// 印刷の指示をする
			// 1) 次のチェックを行う
			// - 指定した枚数のチェック
			// - 出力プロッタは選択されている?
			// - 正しい出力サイズを指定している?
			checkForPrint(searchResultForm, user, drasapInfo, errors);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.debug("--> search_error");
				return "search_error";
			}
			// 2) 印刷可能でない図番を指示していないか?
			if (hasNotPrintable(searchResultForm, user)) {
				category.debug("--> notPrintable");
				return "notPrintable";
			}
			// 2013.06.24 yamagishi modified. start
			// 3) 参考図出力用テーブルに出力する
			// 重複リクエストの調査のためのログ。2005-Mar-4 by Hirata.
			//			PrintLoger.info(PrintLoger.ACT_RECEIVE, user.getId());
			PrintLoger.info(PrintLoger.ACT_RECEIVE, user);
			// 2013.06.24 yamagishi modified. end

			int requestCount = requestPrint(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// 成功したメッセージを
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.success.print.request." + user.getLanKey(),
						new Object[] { String.valueOf(requestCount) }, null));
				// 全てのチェックを外す '04.Feb.5
				for (int i = 0; i < searchResultForm.searchResultList.size(); i++) {
					searchResultForm.getSearchResultElement(i).setSelected(false);
				}
				// アクセスログを
				// '04.Nov.23 PrintRequestDBで図番ごとにロギングするように変更したので
				// コメントアウトする。
				// AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG);

			}

			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> search_error");
			return "search_error";

		}
		if ("OUT_CSV".equals(searchResultForm.getAct())) {
			// 全属性かどうかを、request#setAttributeする
			session.setAttribute("OUT_CSV_ALL", searchResultForm.getOutCsvAll());
			// 検索結果をファイル出力する
			category.debug("--> out_csv");
			return "out_csv";

		}
		if ("ACLV_CHG".equals(searchResultForm.getAct())) {
			// アクセスレベルの変更画面へ
			category.debug("--> aclv_change");
			return "aclv_change";
		}
		if ("DELETEDWG".equals(searchResultForm.getAct())) {
			category.debug("--> DELETEDWG");
			return "deletedwg";
			// 2019.10.17 yamamoto add. start
		}
		if ("MULTI_PDF".equals(searchResultForm.getAct())) {
			session.setAttribute("searchResultForm", searchResultForm);
			// 選択した図面を1ファイルPDFにしてダウンロードする
			category.debug("--> MULTI_PDF");
			return "multi_pdf";
			// 2019.10.17 yamamoto add. end
			// 2020.03.10 yamamoto add. start
		}
		if ("PDF_ZIP".equals(searchResultForm.getAct())) {
			session.setAttribute("searchResultForm", searchResultForm);
			// 選択した図面をzipでダウンロードする
			category.debug("--> PDF_ZIP");
			return "multi_pdf";
			// 2020.03.10 yamamoto add. end
		}
		if ("LIST_VIEW".equals(searchResultForm.getAct())) {
			session.setAttribute("indication", "thumbnail_view");
			return "result";
		}
		if ("THUMBNAIL_VIEW".equals(searchResultForm.getAct())) {
			session.setAttribute("indication", "list_view");
			return "result";
		}
		if ("THUMBNAIL_SIZE".equals(searchResultForm.getAct())) {
			thumbnailSizeChange(user, request.getParameter("thumbnailSize"), errors);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				request.setAttribute("errors", errors);
				return "search_error";
			}
			String thumbnailSize = request.getParameter("thumbnailSize");
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				String newThumbnailName = searchResultForm.searchResultList.get(i).thumbnailName;
				if (!"1".equals(searchResultForm.searchResultList.get(i).aclFlag)) {
					if ("L".equals(thumbnailSize)) {
						newThumbnailName = "NotAccess_L_thumb.jpg";
					} else if ("S".equals(thumbnailSize)) {
						newThumbnailName = "NotAccess_S_thumb.jpg";
					} else {
						newThumbnailName = "NotAccess_M_thumb.jpg";
					}
					searchResultForm.searchResultList.get(i).addAttr("DRWG_SIZE", "A0");
				}
				if ("NotFound_L_thumb.jpg".equals(newThumbnailName) || "NotFound_S_thumb.jpg".equals(newThumbnailName) || "NotFound_M_thumb.jpg".equals(newThumbnailName)) {
					if ("L".equals(thumbnailSize)) {
						newThumbnailName = "NotFound_L_thumb.jpg";
					} else if ("S".equals(thumbnailSize)) {
						newThumbnailName = "NotFound_S_thumb.jpg";
					} else {
						newThumbnailName = "NotFound_M_thumb.jpg";
					}
					searchResultForm.searchResultList.get(i).addAttr("DRWG_SIZE", "A0");
				}
				searchResultForm.searchResultList.get(i).thumbnailName = newThumbnailName;
			}
			session.setAttribute("thumbnailSize", user.getThumbnailSize());
			session.setAttribute("indication", "thumbnail_view");
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";
		}
		if ("SEARCH_THUMBNAIL".equals(searchResultForm.getAct())) {
			// 印刷の指示をする
			// 1) 次のチェックを行う
			// - 指定した枚数のチェック
			// - 正しい出力サイズを指定している?
			checkForPrint(searchResultForm, user, drasapInfo, errors);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.debug("--> search_thumb");
				return "search_thumb";
			}
			// 2) 印刷可能でない図番を指示していないか?
			if (hasNotPrintable(searchResultForm, user)) {
				category.debug("--> thumbNotPrintable");
				return "thumbNotPrintable";
			}
			// 3) 参考図出力用テーブルに出力する
			// 重複リクエストの調査のためのログ。
			PrintLoger.info(PrintLoger.ACT_RECEIVE, user);

			request.setAttribute("errors", errors);
			category.debug("--> search_thumb");
			return "search_thumb";
		}
		if ("PRIENTER_THUMBNAIL".equals(searchResultForm.getAct())) {
			// 印刷の指示をする
			// 1) 次のチェックを行う
			// - 指定した枚数のチェック
			// - 出力プロッタは選択されている?
			// - 正しい出力サイズを指定している?
			checkForPrint(searchResultForm, user, drasapInfo, errors);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.debug("--> search_thumb");
				return "search_thumb";
			}
			// 2) 印刷可能でない図番を指示していないか?
			if (hasNotPrintable(searchResultForm, user)) {
				category.debug("--> thumbNotPrintable");
				return "thumbNotPrintable";
			}
			// 3) 参考図出力用テーブルに出力する
			// 重複リクエストの調査のためのログ。
			PrintLoger.info(PrintLoger.ACT_RECEIVE, user);

			int requestCount = requestPrint(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// 成功したメッセージを
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.success.print.request." + user.getLanKey(),
						new Object[] { String.valueOf(requestCount) }, null));
				// 全てのチェックを外す
				for (int i = 0; i < searchResultForm.searchResultList.size(); i++) {
					searchResultForm.getSearchResultElement(i).setSelected(false);
				}
			}
			request.setAttribute("errors", errors);
			category.debug("--> search_thumb");
			return "search_thumb";
		}
		if ("SEARCH".equals(searchResultForm.getAct())) {
			request.setAttribute("task", "continue");
			return "search";
		}
		return null;
	}

	/**
	 * 再表示で表示項目を変更したときに、その表示項目をユーザーマスタに登録する。
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 */
	private void updateUserInfo(SearchResultForm searchResultForm, User user, Model errors) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			String strSql = "update USER_MASTER set ";
			for (int i = 1; i <= searchResultForm.getViewSelColNum(); i++) {
				String val = searchResultForm.getDispAttr(i - 1);
				strSql += " VIEW_SELCOL" + i + "='" + val + "',";
				// ユーザーObjectにもセットする
				user.setViewSelCol(i - 1, val);
			}
			strSql = DrasapUtil.removeLastComma(strSql);// 最後のカンマを取り除く
			strSql += " where USER_ID='" + user.getId() + "'";

			stmt = conn.createStatement();
			stmt.executeUpdate(strSql);
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.update.user." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
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

		try {
			conn = ds.getConnection();

			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// 選択されていて、かつユーザーが印刷できなければ・・・警告対象
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
				//				if(searchResultElement.isSelected() && ! user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && !user.isPrintableByReq(searchResultElement, conn, i > 0)) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
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
	//	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, Model errors){
	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, Model errors) throws Exception {
		Connection conn = null;

		try {
			conn = ds.getConnection();

			// 1) 指定した枚数のチェック
			int selectedDrwgCount = 0;
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// 選択されていて、かつユーザーが印刷可能なら
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
				//				if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn, i > 0)) {
					selectedDrwgCount++;// インクリメント

				}
			}
			if (selectedDrwgCount == 0) {
				// １つも選択していない
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.no.selected." + user.getLanKey(), null, null));

			} else if (selectedDrwgCount > drasapInfo.getPrintRequestMax()) {
				// 上限を超えた件数を指定
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.print.request.max." + user.getLanKey(),
						new Object[] { String.valueOf(drasapInfo.getPrintRequestMax()), String.valueOf(selectedDrwgCount) }, null));
			}
			// 2) 出力プロッタは選択されている?
			if (searchResultForm.getOutputPrinter() == null || "".equals(searchResultForm.getOutputPrinter())) {
				if (!"SEARCH_THUMBNAIL".equals(searchResultForm.getAct())) {
					String requiredStr;
					if ("jp".equals(user.getLanKey())) {
						requiredStr = "出力プロッタ";
					} else {
						requiredStr = "PLOTTER";
					}
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.required." + user.getLanKey(), new Object[] { requiredStr }, null));
				}
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
										searchResultElement.getAttr("DRWG_SIZE"), // 原図のサイズ
										searchResultElement.getPrintSize(), // 指定サイズ
										selectedPrinter));// プリンター
						if (searchResultElement.getDecidePrintSize() == null) {
							// 指定サイズがエラー
							MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.print.nomatch.size." + user.getLanKey(),
									new Object[] { searchResultElement.getDrwgNoFormated() }, null));
						} else {
							//プリンタの最大印刷サイズを取得する
							searchResultElement.setPrinterMaxSize(selectedPrinter.getMaxSize());
						}
					}
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
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
	private int requestPrint(SearchResultForm searchResultForm, User user, Model errors) {
		// 選択されたプリンタのオブジェクトを取得
		Printer selectedPrinter = user.getPrinter(searchResultForm.getOutputPrinter());
		//
		int cnt = 0;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.print.request." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("参考図の出力指示に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return cnt;
	}

	/**
	 *
	 * @param user
	 * @param thumbnailSize
	 * @param errors
	 * @throws Exception
	 */
	private void thumbnailSizeChange(User user, String thumbnailSize, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false); // 自動コミットしない
			// サムネイルサイズ更新
			UserDB.updateThumbnailSize(user.getId(), thumbnailSize, conn);
			user.setThumbnailSize(thumbnailSize);
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
}
