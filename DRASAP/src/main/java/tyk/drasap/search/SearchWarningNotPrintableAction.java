package tyk.drasap.search;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 検索結果から、出力指示をしたときに、Tiff以外を選択していたときに、
 * 確認するアクション。
 * @author fumi
 * 作成日: 2004/01/19
 */
@Controller
public class SearchWarningNotPrintableAction extends BaseAction {
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
	@PostMapping("/searchWarningNotPrintable")
	public String execute(
			SearchWarningNotPrintableForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}

		SearchWarningNotPrintableForm notPrintableForm = form;
		// act属性による処理の切り分け
		if ("continue".equals(notPrintableForm.getAct())) {
			// 継続する
			SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
			int requestCount = requestPrint(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// 成功したメッセージを
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.success.print.request." + user.getLanKey(),
						new Object[] { String.valueOf(requestCount) }, null));
			}
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> search_error");
			return "search_error";
		}
		// そのまま元の検索結果に戻る
		category.debug("--> backResult");
		return "backResult";
	}

	/**
	 * 参考図の出力指示をする。参考図出力依頼テーブルに書き出す。
	 * @param searchResultForm
	 * @param user
	 * @param errors
	 * @return 指示した件数
	 */
	private int requestPrint(SearchResultForm searchResultForm, User user, Model errors) {
		// 最初に選択されたプリンタのオブジェクトを取得
		Printer selectedPrinter = null;
		for (int i = 0; i < user.getEnablePrinters().size(); i++) {
			Printer printer = user.getEnablePrinters().get(i);
			if (printer.getId().equals(searchResultForm.getOutputPrinter())) {
				selectedPrinter = printer;
				break;
			}
		}
		//
		int cnt = 0;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.print.request.jp", new Object[] { e.getMessage() }, null));
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

}
