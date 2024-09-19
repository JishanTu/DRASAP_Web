package tyk.drasap.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.common.CookieManage;
import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DateCheck;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.StringCheck;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 検索を行うAction。
 *
 * @version 2013/06/14 yamagishi
 */
@Controller
@SessionAttributes("searchConditionForm")
public class SearchConditionAction extends BaseAction {
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
	@PostMapping("/searchCondition")
	public String execute(
			@ModelAttribute("searchConditionForm") SearchConditionForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		//ActionMessages errors = new ActionMessages();

		SearchConditionForm searchConditionForm = form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");

		String lanKey = user.getLanKey();

		// リクエストから情報を取得
		searchConditionForm.setSearchCondition(request);

		// 遷移元をsessionに保持
		session.setAttribute("parentPage", "Search");

		session.setAttribute("default_css", "jp".equals(lanKey) ? "default.css" : "defaultEN.css");

		// act属性で処理を分ける
		String actVal = request.getParameter("act");
		category.debug("act属性は" + searchConditionForm.act + " / " + actVal);

		if (StringUtils.isEmpty(searchConditionForm.act)) {
			if (StringUtils.isNotEmpty(actVal)) {
				searchConditionForm.act = actVal;
			}
		}

		if ("search".equals(searchConditionForm.act)) {
			System.currentTimeMillis();
			// ユーザーの最高アクセスレベル値が、1より小さいなら検索できない
			if (user.getMaxAclValue().compareTo("1") < 0) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.no_authority.search." + user.getLanKey(), null, null));
				//				saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}

			// actに'search'が設定されているとき
			// 1) 検索条件の確認
			if (!checkSearchCondition(searchConditionForm, drasapInfo, user, errors)) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
			// 2013.06.27 yamagishi add. start
			String[] multipleDrwgNoArray = null;

			// 2020.03.11 yamamoto modify. start
			// 複数図番の指定ありの場合
			if (searchConditionForm.multipleDrwgNo != null
					&& searchConditionForm.multipleDrwgNo.length() > 0) {
				// AND検索 または 図番指定順が有効の場合
				if ("AND".equals(searchConditionForm.getEachCondition())
						|| searchConditionForm.isOrderDrwgNo()) {
					// 複数図番の指定件数を確認
					multipleDrwgNoArray = multipleDrwgNoCount(searchConditionForm, drasapInfo);
					if (multipleDrwgNoArray.length > drasapInfo.getMultipleDrwgNoMax()) {
						// 複数図番指定時の検索可能件数を超えた場合
						request.setAttribute("hit", String.valueOf(multipleDrwgNoArray.length));
						category.debug("--> overLimitMultipleDrwgNo");
						return "overLimitMultipleDrwgNo";
					}
					// 2) 複数図番、検索条件の確認
					if (!checkSearchConditionMultipleDrwgNo(multipleDrwgNoArray, searchConditionForm, drasapInfo, user, errors)) {
						//saveErrors(request, errors);
						request.setAttribute("errors", errors);
						return "error";
					}
				}
			}
			// 2020.03.11 yamamoto modify. end
			// 2013.06.27 yamagishi add. end

			// 2013.06.28 yamagishi modified. start
			//			// まず検索条件にHitした件数を確認
			//			int hit = countHit(searchConditionForm, user, request, errors);
			// まず検索条件にHitした件数を確認
			int hit = countHit(searchConditionForm, user, request, errors, multipleDrwgNoArray);
			// 2013.06.28 yamagishi modified. end
			if (hit == -1) {
				// エラーが発生した場合
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
			if (hit > drasapInfo.getSearchLimitCount()) {
				// 利用可能件数を超えた場合
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overLimitHit");
				return "overLimitHit";
			}
			if (hit > drasapInfo.getSearchWarningCount()) {
				// 警告件数を超えた場合
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overHit");
				return "overHit";
			}
			session.setAttribute("searchConditionForm", searchConditionForm);
			// 警告件数以下の場合
			category.debug("--> searchResult");
			return "searchResult";

		}
		if ("CHANGELANGUAGE".equals(searchConditionForm.act)) {
			user.setLanguage(searchConditionForm.getLanguage());
			setFormData(searchConditionForm, user);
			getScreenItemStrList(searchConditionForm, user);
			session.setAttribute("user", user);
			session.setAttribute("searchConditionForm", searchConditionForm);

			// 言語設定をクッキーに保存
			CookieManage langCookie = new CookieManage();
			langCookie.setCookie(response, user, "Language", searchConditionForm.getLanguage());

			session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");
			request.setAttribute("task", "changeLanguage");
			// 表示言語変更へ
			category.debug("--> changeLanguage");
			return "changeLanguage";
		}
		if (!"multipreview".equals(searchConditionForm.act)) {
			// クッキーから言語設定を取得
			CookieManage langCookie = new CookieManage();
			lanKey = langCookie.getCookie(request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) {
				lanKey = "Japanese";
			}
			user.setLanguage(lanKey);

			session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");
			// actに何も設定されていないとき
			// 検索画面を表示する
			setFormData(searchConditionForm, user);
			searchConditionForm.setLanguage(user.getLanguage());
			// 画面表示文字列取得
			getScreenItemStrList(searchConditionForm, user);

			SearchConditionForm searchForm = (SearchConditionForm) session.getAttribute("searchConditionForm");
			if (searchForm != null) {
				ArrayList<String> ConditionValueList = searchConditionForm.getConditionValueList();
				if (ConditionValueList.isEmpty() || ConditionValueList.stream().allMatch(String::isEmpty)) {
					ArrayList<String> valueList = searchForm.getConditionValueList();
					if (!valueList.isEmpty() && !valueList.stream().allMatch(String::isEmpty)) {
						searchConditionForm.setConditionValueList(valueList);
					}
				}
			}

			session.setAttribute("searchConditionForm", searchConditionForm);
			return "input";
		}
		// クッキーから言語設定を取得
		CookieManage langCookie = new CookieManage();
		lanKey = langCookie.getCookie(request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) {
			lanKey = "Japanese";
		}
		user.setLanguage(lanKey);
		session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");

		// イニシャライズ
		setFormData(searchConditionForm, user);
		searchConditionForm.setLanguage(user.getLanguage());
		// 画面表示文字列取得
		getScreenItemStrList(searchConditionForm, user);

		// マルチプレビュー用に渡された図番を検索条件に設定する
		setMultiViewDrwgNos(searchConditionForm, request);
		session.setAttribute("searchConditionForm", searchConditionForm);

		// 2013.06.28 yamagishi modified. start
		//			// 検索条件にHitした件数を確認
		//			int hit = countHit(searchConditionForm, user, request, errors);
		// 検索条件にHitした件数を確認
		int hit = countHit(searchConditionForm, user, request, errors, null);
		// 2013.06.28 yamagishi modified. end
		if (hit == -1) {
			// エラーが発生した場合
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "error";
		}
		if (hit > drasapInfo.getSearchLimitCount()) {
			// 利用可能件数を超えた場合
			request.setAttribute("hit", String.valueOf(hit));
			category.debug("--> overLimitHit");
			return "overLimitHit";
		}
		if (hit > drasapInfo.getSearchWarningCount()) {
			// 警告件数を超えた場合
			request.setAttribute("hit", String.valueOf(hit));
			category.debug("--> overHit");
			return "overHit";
		}
		// 警告件数以下の場合
		category.debug("--> multipreview");
		request.setAttribute("task", "continue");
		return "multipreview";
	}

	/**
	 * SearchConditionFormに初期値をセットする。
	 * @param searchConditionForm
	 * @param user
	 */
	private void setFormData(SearchConditionForm searchConditionForm, User user) {
		// 検索条件の項目をセットする ////////////////////////////////////////////
		// その1) キーを
		searchConditionForm.conditionKeyList = new ArrayList<>();
		searchConditionForm.conditionKeyList.add("");
		searchConditionForm.conditionKeyList.add("DRWG_NO");
		// このユーザーが使用できる属性全てを
		// '04.Apr.16 変更 by Hirata
		SearchUtil sUtil = new SearchUtil();
		searchConditionForm.conditionKeyList.addAll(sUtil.createEnabledAttrList(user, true));

		// その2) 名称を
		searchConditionForm.conditionNameList = new ArrayList<>();
		searchConditionForm.conditionNameList.add("");
		//		searchConditionForm.conditionNameList.add("図番");
		searchConditionForm.conditionNameList.add(sUtil.getSearchAttr(user, "DRWG_NO", false));
		// このユーザーが使用できる属性全てを
		// '04.Apr.16 変更 by Hirata
		searchConditionForm.conditionNameList.addAll(sUtil.createEnabledAttrList(user, false));

		// 前回の検索条件をセットする ////////////////////////////////////////////
		searchConditionForm.setConditionList(user.getSearchSelColList());
		// 検索条件1が未設定の場合、「図番」をセットする
		if (StringUtils.isEmpty(searchConditionForm.getCondition(0))) {
			searchConditionForm.setCondition(0, "DRWG_NO");
		}
		// 前回の表示件数をセットする
		searchConditionForm.setDisplayCount(user.getDisplayCount());
		if (searchConditionForm.displayCount == null || "".equals(searchConditionForm.displayCount)) {
			// 表示件数が未設定の場合、50件をセットする
			searchConditionForm.setDisplayCount("50");
		}
		// 前回の最新追番のみをセットする
		searchConditionForm.setOnlyNewest(user.isOnlyNewest());
		// ソート順序のプルダウンをセットする
		searchConditionForm.sortOrderKeyList = new ArrayList<>();
		searchConditionForm.sortOrderKeyList.add("");
		searchConditionForm.sortOrderKeyList.add("1");
		searchConditionForm.sortOrderKeyList.add("2");
		searchConditionForm.sortOrderKeyList.add("3");
		searchConditionForm.sortOrderKeyList.add("4");
		searchConditionForm.sortOrderKeyList.add("5");
		searchConditionForm.sortOrderKeyList.add("6");
		searchConditionForm.sortOrderKeyList.add("7");
		searchConditionForm.sortOrderKeyList.add("8");
		searchConditionForm.sortOrderKeyList.add("9");
		searchConditionForm.sortOrderKeyList.add("10");
		searchConditionForm.sortOrderNameList = searchConditionForm.sortOrderKeyList;
		// 「全ての属性条件を」はANDをデフォルトに '04.Feb.5変更
		searchConditionForm.eachCondition = "AND";

		searchConditionForm.searchHelpMsg = getSearchHelpMsg(user.getLanguage());

		// 2019.09.25 yamamoto add.
		// エラーメッセージセット
		searchConditionForm.setChangeLangErrMsg(getErrorMsg(user, "search.failed.change.lang"));
		searchConditionForm.setLogoutErrMsg(getErrorMsg(user, "search.failed.logout"));
		// 2020.03.17 yamamoto add.
		searchConditionForm.setlistOrderErrMsg(getErrorMsg(user, "search.failed.search.listOrder"));
	}

	/**
	 * 検索条件が正しく入力されているか?
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @return 正しく入力されていれば true
	 */
	private boolean checkSearchCondition(SearchConditionForm searchConditionForm, DrasapInfo drasapInfo, User user, Model errors) {
		// 最初に、1つでも条件が入力されているか確認
		boolean inputed = false;// 1つでも入力されていたら true

		for (int i = 0; i < searchConditionForm.getSearchSelColNum(); i++) {
			if (isInputed(searchConditionForm.getCondition(i), searchConditionForm.getConditionValue(i))) {
				inputed = true;
				break;
			}
		}

		// 2013.06.27 yamagishi add. start
		if (!inputed && ("AND".equals(searchConditionForm.eachCondition) || searchConditionForm.isOrderDrwgNo())) {
			String multiNotemp = searchConditionForm.multipleDrwgNo.replace(System.lineSeparator(), "");
			// 改行コードを除いた入力値で判定
			if (isInputed("multipleDrwnNo", multiNotemp)) {
				inputed = true;
			}
			// 2013.06.27 yamagishi add. end
			// 2020.03.11 yamamoto modify. start
		}
		// 2020.03.11 yamamoto modify. end
		if (!inputed) {// 検索条件が1つも入力されていない場合
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.required.search.condition." + user.getLanKey(), null, null));
			return false;
		}
		// 検索条件として指定された値が正しく解釈できるか
		boolean isRight = true;
		for (int i = 0; i < searchConditionForm.getSearchSelColNum(); i++) {
			if (!isRightCondition(searchConditionForm.getCondition(i), searchConditionForm.getConditionValue(i), drasapInfo, user, errors)) {
				isRight = false;
			}
		}
		return isRight;
	}

	// 2013.06.27 yamagishi add. start
	/**
	 * 検索条件が正しく入力されているか?
	 * @param multipleDrwgNo
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @return 正しく入力されていれば true
	 */
	private boolean checkSearchConditionMultipleDrwgNo(String[] multipleDrwgNo, SearchConditionForm searchConditionForm, DrasapInfo drasapInfo, User user, Model errors) {
		// 検索条件として指定された値が正しく解釈できるか
		boolean isRight = true;

		for (String drwgNo : multipleDrwgNo) {
			// 複数図番の全件をチェック
			if (!isRightCondition("DRWG_NO", drwgNo, drasapInfo, user, errors)) {
				isRight = false;
				break;
			}
		}
		return isRight;
	}
	// 2013.06.27 yamagishi add. end

	/**
	 * 検索条件の名称と値の両方が入力されているか?
	 * @param conditionName 検索条件の名称
	 * @param conditionValue 検索条件の値
	 * @return 名称と値の両方が入力されていれば true
	 */
	private boolean isInputed(String conditionName, String conditionValue) {
		return conditionName != null && conditionName.length() > 0 &&
				conditionValue != null && conditionValue.length() > 0;
	}

	/**
	 * 検索条件の値が正しく指定されているか確認する。
	 * 1) カンマまたはアンバサンドが正しく使用されているか・・・どの属性も共通
	 * 2) ・・・日付型属性
	 * @param conditionName 検索条件の名称
	 * @param conditionValue 検索条件の値
	 * @param errors
	 * @return 正しく指定されていれば true
	 */
	private boolean isRightCondition(String conditionName, String conditionValue, DrasapInfo drasapInfo, User user, Model errors) {
		boolean isRight = true;// 正しければtrue
		if (conditionName != null && conditionName.length() > 0 &&
				conditionValue != null && conditionValue.length() > 0) {
			// 名前と値の両方が入力されていればチェックする

			// どの属性も共通のチェック /////////////////////////////////////////////////////////////
			// カンマまたはアンバサンドが正しく使用されているか?
			char[] c = conditionValue.toCharArray();
			int lastIndex = -1;// カンマまたはアンバサンドが現れたindex
			for (int i = 0; i < c.length; i++) {
				if (c[i] == ',' || c[i] == '&') {
					// カンマまたはアンバサンドが現れた
					if (i == lastIndex + 1 || i == 0 || i == c.length - 1) {
						// カンマまたはアンバサンドが続いているとNG
						// カンマまたはアンバサンドは最初、最後はNG
						isRight = false;
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.separate." + user.getLanKey(), new Object[] { conditionName }, null));
						break;
					}
					// lastIndexを置き換える
					lastIndex = i;
				}
			}
			// 日付型属性のチェック /////////////////////////////////////////////////////////////////
			if ("CREATE_DATE".equals(conditionName) || "PROHIBIT_DATE".equals(conditionName) || "ACL_UPDATE".equals(conditionName)) {
				// カンマまたはアンバサンドで区切られた文字列を
				StringTokenizer st = new StringTokenizer(conditionValue, ",&");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					token = token.replace('　', ' ');// 全角スペースを半角スペースに
					token = token.trim();// trimして半角スペースを除く
					int sepIndex = token.indexOf('-');
					if (sepIndex == -1) {
						// 「-」を使用した範囲指定がない場合
						int ymd = DateCheck.convertIntYMD(token);
						if (ymd == -1 || !DateCheck.isDate(ymd)) {
							// 日付として解釈できない
							isRight = false;
							MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.dateformat." + user.getLanKey(), new Object[] { conditionName }, null));
							break;
						}
					} else {// 「-」を使用して範囲指定されている場合
						// 「-」を利用して開始、終了の文字列を取得
						String fromDate = null;// 開始
						String toDate = null;// 終了
						if (sepIndex > 0) {
							fromDate = token.substring(0, sepIndex);
						}
						if (sepIndex < token.length() - 1) {
							toDate = token.substring(sepIndex + 1);
						}
						// 日付として解釈できるか
						// 1) 開始日
						int fromYmd = 0;
						if (fromDate != null) {
							fromYmd = DateCheck.convertIntYMD(fromDate);
							if (fromYmd == -1 || !DateCheck.isDate(fromYmd)) {
								// 日付として解釈できない
								isRight = false;
								MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.dateformat." + user.getLanKey(), new Object[] { conditionName }, null));
								break;
							}
						}
						// 2) 終了日
						int toYmd = 0;
						if (toDate != null) {
							toYmd = DateCheck.convertIntYMD(toDate);
							if (toYmd == -1 || !DateCheck.isDate(toYmd)) {
								// 日付として解釈できない
								isRight = false;
								MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.dateformat." + user.getLanKey(), new Object[] { conditionName }, null));
								break;
							}
						}
						// 開始 > 終了 でない?
						if (fromDate != null && toDate != null) {
							if (fromYmd > toYmd) {
								isRight = false;
								MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.date.fromto." + user.getLanKey(), new Object[] { conditionName }, null));
								break;
							}
						}
					}
				}
			} else {
				// 文字型属性のチェック /////////////////////////////////////////////////////////
				// カンマまたはアンバサンドで区切られた文字列を
				StringTokenizer st = new StringTokenizer(conditionValue, ",&");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					token = token.replace('　', ' ');// 全角スペースを半角スペースに
					token = token.trim();// trimして半角スペースを除く
					if (token.length() == 0) {
						isRight = false;
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.separate." + user.getLanKey(), new Object[] { conditionName }, null));
						break;
					}
					if ("DRWG_NO".equals(conditionName)) {
						// *を除いた文字数を数える
						char[] tempArray = token.toCharArray();
						int exceptLength = 0;
						for (int i = 0; i < tempArray.length; i++) {
							if (tempArray[i] != '*') {
								exceptLength++;
							}
						}
						// 設定された文字数より少なければ
						if (exceptLength < drasapInfo.getMinimumIuputDrwgChar()) {
							isRight = false;
							MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.miss.condition.drwgno.short." + user.getLanKey(),
									new Object[] { String.valueOf(drasapInfo.getMinimumIuputDrwgChar()) }, null));
							break;
						}
					}
				}
			} // END・・・文字型属性のチェック
		} // END・・・名前と値の両方が入力されていればチェックする
		return isRight;
	}

	/**
	 * 検索条件に一致した件数をカウントし返す。
	 * エラーが発生した場合は -1 を返す。
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @param multipleDrwgNo
	 * @return 検索条件に一致した件数。
	 * エラーが発生した場合は -1。
	 */
	// 2013.06.28 yamagishi modified. start
	//	private int countHit(SearchConditionForm searchConditionForm, User user,
	//						HttpServletRequest request, Model errors){
	private int countHit(SearchConditionForm searchConditionForm, User user,
			HttpServletRequest request, Model errors, String[] multipleDrwgNo) {
		// 2013.06.28 yamagishi modified. end

		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		int hit = -1;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			// 件数を数える対象をINDEX_FILE_VIEWに変更 '04.Mar.2
			StringBuilder sbSql1 = new StringBuilder("select count(*) CNT from INDEX_FILE_VIEW");
			sbSql1.append(" ");
			// 2013.09.13 yamagishi modified. start
			// Where部分以下を作成し付加する
			String sqlWhere = createSqlWhere(searchConditionForm, user, conn, multipleDrwgNo);
			sbSql1.append(sqlWhere);
			request.setAttribute("SQL_WHERE", sqlWhere);// 次のSearchResultPreActionで使用するためにrequestにセットする
			request.setAttribute("SQL_ORDER", createSqlOrder(searchConditionForm));// 次のSearchResultPreActionで使用するためにrequestにセットする
			if ("multipreview".equals(searchConditionForm.act)) {
				request.getSession().setAttribute("SQL_WHERE", sqlWhere);
				request.getSession().setAttribute("SQL_ORDER", createSqlOrder(searchConditionForm));
			}
			// 2013.09.13 yamagishi modified. end
			//
			stmt1 = conn.createStatement();
			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if (rs1.next()) {
				hit = rs1.getInt("CNT");
				category.debug("件数は " + hit);
			}
			String onlyNewest = searchConditionForm.isOnlyNewest() ? "1" : "0";
			// 2020.03.11 yamamoto modified. start
			if (searchConditionForm.isOrderDrwgNo()) {
				// ユーザー管理マスターに検索カラム、表示件数をセットする。
				// 図番指定順のチェックＯＮの場合は検索条件無効なので、
				// 前回の検索条件を保持したままとする。
				String strSql2 = "update USER_MASTER set ";
				for (int i = 1; i <= searchConditionForm.getViewSelColNum(); i++) {
					String val = searchConditionForm.getDispAttr(i - 1);
					strSql2 += " VIEW_SELCOL" + i + "='" + val + "',";
					// ユーザーObjectにもセットする
					user.setViewSelCol(i - 1, val);
				}
				strSql2 += " ONLY_NEWEST='" + onlyNewest + "',";
				strSql2 += " DISPLAY_COUNT='" + searchConditionForm.getDisplayCount() + "'";
				strSql2 += " where USER_ID='" + user.getId() + "'";

				stmt2 = conn.createStatement();
				stmt2.executeUpdate(strSql2);

				// ユーザーObjectにもセットする
				user.setOnlyNewestFlag(onlyNewest);
				user.setDisplayCount(searchConditionForm.getDisplayCount());
			} else if (!"multipreview".equals(searchConditionForm.act)) {
				// ここから先は、ユーザー管理マスターに検索カラム、表示件数をセットする。
				String strSql2 = "update USER_MASTER set ";
				for (int i = 1; i <= searchConditionForm.getSearchSelColNum(); i++) {
					String val = searchConditionForm.getCondition(i - 1);
					strSql2 += " SEARCH_SELCOL" + i + "='" + val + "',";
					// ユーザーObjectにもセットする
					user.setSearchSelCol(i - 1, val);
				}

				for (int i = 1; i <= searchConditionForm.getViewSelColNum(); i++) {
					String val = searchConditionForm.getDispAttr(i - 1);
					strSql2 += " VIEW_SELCOL" + i + "='" + val + "',";
					// ユーザーObjectにもセットする
					user.setViewSelCol(i - 1, val);
				}
				strSql2 += " ONLY_NEWEST='" + onlyNewest + "',";
				strSql2 += " DISPLAY_COUNT='" + searchConditionForm.getDisplayCount() + "'";
				strSql2 += " where USER_ID='" + user.getId() + "'";

				stmt2 = conn.createStatement();
				stmt2.executeUpdate(strSql2);

				// ユーザーObjectにもセットする
				user.setOnlyNewestFlag(onlyNewest);
				user.setDisplayCount(searchConditionForm.getDisplayCount());
			}
			// 2020.03.11 yamamoto modified. end
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				stmt2.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		return hit;
	}

	// 2013.06.27 yamagishi add. start
	/**
	 * 指定された複数図番を配列で返す。
	 * （※件数が上限を超えている場合、上限＋1件とする）
	 * @param searchConditionForm
	 * @return 複数図番の配列。
	 */
	private String[] multipleDrwgNoCount(SearchConditionForm searchConditionForm, DrasapInfo drasapInfo) {
		String multipleDrwgNo = searchConditionForm.getMultipleDrwgNo();
		return multipleDrwgNo.split(System.lineSeparator(), drasapInfo.getMultipleDrwgNoMax() + 1);
	}
	// 2013.06.27 yamagishi add. end

	// 2013.09.13 yamagishi modified. start
	/**
	 * SQL文のアクセスレベルCASE式およびWhere部分をまとめて作成する。
	 * ここでの戻り値の例)
	 * where ACL_ID in ('01','10','13','11','15','14','12') and (((TRUNC(CREATE_DATE)>=TO_DATE('20030101','YYYYMMDD'))) )
	 * @param searchConditionForm
	 * @param user
	 * @param conn
	 * @param multipleDrwgNo
	 * @return
	 */
	private String createSqlWhere(SearchConditionForm searchConditionForm, User user, Connection conn, String[] multipleDrwgNo) throws Exception {
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("where");
		// アクセスレベルによる制限を
		sbSql1.append(" ACL_ID in (");

		HashMap<String, String> aclMap = user.getAclMap(conn);
		Iterator<String> itr = aclMap.keySet().iterator();
		int aclCount = 0; // 保有ACL数
		while (itr.hasNext()) {
			String aclId = itr.next();// アクセスレベル
			int aclValue = Integer.parseInt(aclMap.get(aclId));
			if (aclValue >= 0) {
				// このアクセスレベルが「参照不可」以上なら、検索対象のアクセスレベルとして追加する
				sbSql1.append("'");
				sbSql1.append(aclId);
				sbSql1.append("',");
				aclCount++;
			}
		}
		if (aclCount > 0) {
			sbSql1.deleteCharAt(sbSql1.length() - 1);// 最後のカンマを除く
		} else {
			sbSql1.append("''");// 空のIN句を追加
		}
		sbSql1.append(")");

		ArrayList<String> tempSqlList = new ArrayList<>();// 検索条件によるSQL部分を一時保管する

		// 2020.03.11 yamamoto modified. start
		if (searchConditionForm.isOrderDrwgNo()) {
			// 図番指定順にチェックが入っている場合は通常の検索条件は無視する
			sbSql1.append(" and (");
		} else {
			// 最新追番のみなら
			if (searchConditionForm.onlyNewest) {
				sbSql1.append(" and LATEST_FLAG='1'");
			}
			// 検索条件による制限を
			sbSql1.append(" and (");

			for (int i = 0; i < searchConditionForm.getSearchSelColNum(); i++) {
				String conditionSql = createSqlWhereByRow(searchConditionForm.getCondition(i), searchConditionForm.getConditionValue(i));
				if (conditionSql != null) {
					tempSqlList.add(conditionSql);
				}
			}

			for (int i = 0; i < tempSqlList.size(); i++) {
				if (i > 0) {
					// 検索条件によるそれぞれの制限をOR,ANDするかは
					// searchConditionForm.eachConditionによる
					sbSql1.append(' ');
					sbSql1.append(searchConditionForm.eachCondition);
					sbSql1.append(' ');
				}
				sbSql1.append(tempSqlList.get(i));
			}
		}
		// 2020.03.11 yamamoto modified. end

		// 複数図番検索部
		if (multipleDrwgNo != null) {
			sbSql1.append(createSqlWhereByMultipleDrwgNo(multipleDrwgNo, tempSqlList.size() > 0));
		}
		sbSql1.append(" )");
		//
		return sbSql1.toString();
	}
	// 2013.09.13 yamagishi modified. end

	// 2013.09.04 yamagishi add. start
	/**
	 * 画面で入力した複数図番でSQL文を部分的に構成する。
	 * ここでの戻り値の例)・・・前後に()が付く
	 * (DRWG_NO in ('3294500015','3294500016' or DRWG_NO LIKE '32941500%')
	 * @param multipleDrwgNo
	 * @param hasOtherCondition
	 * @return
	 */
	private String createSqlWhereByMultipleDrwgNo(String[] multipleDrwgNo, boolean otherConditionFlag) {
		// ループさせる前の初期準備
		StringBuilder sbSql1 = new StringBuilder();
		StringBuilder sbSql2 = new StringBuilder();
		// 最初のカッコ
		if (otherConditionFlag) {
			sbSql1.append(" and (DRWG_NO in (");
		} else {
			sbSql1.append(" (DRWG_NO in (");
		}

		boolean sqlInFlag = false;
		for (String drwgNo : multipleDrwgNo) {
			// trim処理。ハイフン「-」を除く。
			drwgNo = StringCheck.trimWsp(drwgNo).replace("-", "");
			// 半角大文字に変換する
			drwgNo = StringCheck.changeDbToSbAscii(drwgNo).toUpperCase();

			if (drwgNo.length() <= 0) {
				// 空文字（改行コード）をスキップ
				continue;
			}

			if (drwgNo.contains("*")) {
				// ワイルドカード(*)使用時は、LIKE文にして or条件で結合
				// 「*」を「%」に変換。
				sbSql2.append(" or DRWG_NO LIKE '" + drwgNo.replace("*", "%") + "'");
			} else if (drwgNo.contains("_")) {
				// ワイルドカード(_)使用時は、LIKE文にして or条件で結合
				sbSql2.append(" or DRWG_NO LIKE '" + drwgNo + "'");
			} else {
				// IN句に追加
				sbSql1.append("'" + drwgNo + "',");
				sqlInFlag = true;
			}
		}
		if (sqlInFlag) {
			// 最後の1文字(,)を削除
			sbSql1.deleteCharAt(sbSql1.length() - 1);
		} else {
			// IN句の要素がない場合、空文字を追加。
			sbSql1.append("''");
		}
		sbSql1.append(")"); // IN句
		sbSql1.append(sbSql2); // ワイルドカード
		sbSql1.append(")"); // 複数図番検索条件
		return sbSql1.toString();
	}

	// 2013.09.04 yamagishi add. end

	/**
	 * 画面で入力した1行の条件でSQL文を部分的に構成する。
	 * ここでの戻り値の例)・・・前後に()が付く
	 * (DRWG_NO LIKE '329450015%' OR DRWG_NO LIKE '3294%' AND DRWG_NO LIKE '32941500002')
	 * @param conditionName
	 * @param conditionValue
	 * @return
	 */
	private String createSqlWhereByRow(String conditionName, String conditionValue) {
		// 検索条件の項目名または条件が未入力なら nullを返す
		if (conditionName == null || conditionName.length() == 0 ||
				conditionValue == null || conditionValue.length() == 0) {
			return null;
		}
		// ループさせる前の初期準備
		StringBuilder sbSql = new StringBuilder();
		sbSql.append('(');// 最初のカッコ
		int lastIndex = -1;
		String lastOrAnd = "";// 最初はなし。次からは" AND "または" OR "を入れる。
		// ループする部分
		while (true) {
			// 1) 次のカンマまたはアンバサンドの位置の小さい方を求める /////////////////////////////////////
			int nextOrIndex = conditionValue.indexOf(',', lastIndex + 1);// 次のカンマの位置
			int nextAndIndex = conditionValue.indexOf('&', lastIndex + 1);// 次のアンバサンドの位置
			int nextOrAndIndex = -1;// 次のカンマまたはアンドの位置
			if (nextOrIndex == -1) {
				nextOrAndIndex = nextAndIndex;// 次のカンマがなければ
			} else if (nextAndIndex == -1) {
				nextOrAndIndex = nextOrIndex;// 次のアンバサンドがなければ
			} else {
				nextOrAndIndex = Math.min(nextOrIndex, nextAndIndex);// 次のカンマまたはアンバサンドの位置の小さい方
			}
			// 2) 次の値を取り出す ////////////////////////////////////////////////////////////
			String nextValue = null;
			if (nextOrAndIndex == -1) {
				// 次のカンマもアンバサンドもない
				nextValue = conditionValue.substring(lastIndex + 1, conditionValue.length());

			} else {
				// 次のカンマまたはアンバサンドの直前を
				nextValue = conditionValue.substring(lastIndex + 1, nextOrAndIndex);
			}
			// 3) SQL文を組み立てる ///////////////////////////////////////////////////
			sbSql.append(lastOrAnd);
			// 図番、作成日に関しては、半角大文字に変換する
			if ("CREATE_DATE".equals(conditionName) || "DRWG_NO".equals(conditionName) || "TWIN_DRWG_NO".equals(conditionName)) {
				nextValue = StringCheck.changeDbToSbAscii(nextValue).toUpperCase();
			}
			//
			if ("CREATE_DATE".equals(conditionName) || "PROHIBIT_DATE".equals(conditionName) || "ACL_UPDATE".equals(conditionName)) {
				// 日付型の場合
				nextValue = nextValue.replace('　', ' ');// 全角スペースを半角スペースに
				nextValue = nextValue.trim();// trimして半角スペースを除く
				int sepIndex = nextValue.indexOf('-');
				if (sepIndex == -1) {
					// 「-」を使用した範囲指定がない場合
					int ymd = DateCheck.convertIntYMD(nextValue);// 指定日を8ケタ数字(YYYYMMDD)へ
					// 検索スピード向上のため、TRUNCを使用しないように変更する。
					// 指定日の0時0分0秒 <= conditionName < 指定日の1日後の0時0分0秒
					// として取り出す
					// 変更 '04.Mar.19 by Hirata
					sbSql.append("TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("000000','YYYYMMDDHH24MISS')<=");
					sbSql.append(conditionName);// 検索条件の項目名
					sbSql.append(" and ");
					sbSql.append(conditionName);// 検索条件の項目名
					sbSql.append(" < TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("000000','YYYYMMDDHH24MISS')+1");

					/* 以下はTRUNC使用バージョン
					 * 検索スピードが問題となった
					sbSql.append("TRUNC(");
					sbSql.append(conditionName);// 検索条件の項目名
					sbSql.append(")=TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("','YYYYMMDD')");
					 */
				} else {// 「-」を使用して範囲指定されている場合
					sbSql.append("(");
					// 「-」を利用して開始、終了の文字列を取得
					String fromDate = null;// 開始
					String toDate = null;// 終了
					if (sepIndex > 0) {
						fromDate = nextValue.substring(0, sepIndex);
					}
					if (sepIndex < nextValue.length() - 1) {
						toDate = nextValue.substring(sepIndex + 1);
					}
					// 1) 開始日
					int fromYmd = 0;
					if (fromDate != null) {
						fromYmd = DateCheck.convertIntYMD(fromDate);// 開始日を8ケタ数字(YYYYMMDD)へ
						// 検索スピード向上のため、TRUNCを使用しないように変更する。
						// 開始日の0時0分0秒 <= conditionName
						// として取り出す
						// 変更 '04.Mar.19 by Hirata
						sbSql.append("TO_DATE('");
						sbSql.append(fromYmd);
						sbSql.append("000000','YYYYMMDDHH24MISS')<=");
						sbSql.append(conditionName);// 検索条件の項目名

						/* 以下はTRUNC使用バージョン
						 * 検索スピードが問題となった
						sbSql.append("TRUNC(");
						sbSql.append(conditionName);// 検索条件の項目名
						sbSql.append(")>=TO_DATE('");
						sbSql.append(fromYmd);
						sbSql.append("','YYYYMMDD')");
						 */
					}
					// 2) 終了日
					int toYmd = 0;
					if (toDate != null) {
						toYmd = DateCheck.convertIntYMD(toDate);// 終了日を8ケタ数字(YYYYMMDD)へ
						if (fromYmd != 0) {
							// 開始日が指定されていた場合
							sbSql.append(" AND ");
						}
						// 検索スピード向上のため、TRUNCを使用しないように変更する。
						// conditionName < 終了日の1日後の0時0分0秒
						// として取り出す
						// 変更 '04.Mar.19 by Hirata
						sbSql.append(conditionName);// 検索条件の項目名
						sbSql.append(" < TO_DATE('");
						sbSql.append(toYmd);
						sbSql.append("000000','YYYYMMDDHH24MISS')+1");

						/* 以下はTRUNC使用バージョン
						 * 検索スピードが問題となった
						sbSql.append("TRUNC(");
						sbSql.append(conditionName);// 検索条件の項目名
						sbSql.append(")<=TO_DATE('");
						sbSql.append(toYmd);
						sbSql.append("','YYYYMMDD')");
						 */
					}
					sbSql.append(")");
				}

			} else {
				// 文字型の場合
				sbSql.append(conditionName);// 検索条件の項目名
				sbSql.append(" LIKE '");
				// trim処理。「*」を「%」に変換。
				String tempNextValue = StringCheck.trimWsp(nextValue).replace('*', '%');
				// 図番の場合、-を除く処理
				if ("DRWG_NO".equals(conditionName) || "TWIN_DRWG_NO".equals(conditionName)) {
					StringTokenizer st = new StringTokenizer(tempNextValue, "-");
					StringBuilder sb = new StringBuilder();
					while (st.hasMoreTokens()) {
						sb.append(st.nextToken());
					}
					tempNextValue = sb.toString();
				} else if (!tempNextValue.endsWith("%")
						&& ("PAGES".equals(conditionName) // ページ数
								|| "ACL_ID".equals(conditionName) // アクセスレベル
								|| "ATTACH_MAX".equals(conditionName) // 添付図数
								|| "MEDIA_ID".equals(conditionName))) { // メディアID
					// DBの定義がCHARで、値はスペースで左詰めになっているため
					// 入力条件の桁が足りない場合、ヒットして来れない
					// DBの定義は下記となる
					// 　"PAGES" CHAR(4),
					// 　"ACL_ID" CHAR(2) NOT NULL ENABLE,
					// 　"ATTACH_MAX" CHAR(2),
					// 　"MEDIA_ID" CHAR(8) NOT NULL ENABLE,
					int len = "PAGES".equals(conditionName)
							? 4
							: "MEDIA_ID".equals(conditionName)
									? 8
									: 2;
					tempNextValue = String.format("%-" + len + "s", tempNextValue);
				}
				sbSql.append(tempNextValue);
				sbSql.append("'");
			}
			// 4) 次のループのための準備
			if (nextOrAndIndex == -1) {
				// 次のカンマもアンバサンドもない
				break;
			}
			if (conditionValue.charAt(nextOrAndIndex) == ',') {
				lastOrAnd = " OR ";//
			} else {
				lastOrAnd = " AND ";//
			}
			lastIndex = nextOrAndIndex;
		}
		//
		sbSql.append(')');// 閉じカッコ
		return sbSql.toString();
	}

	/**
	 * SQL文のOrder部分をまとめて作成する。
	 * ここでの戻り値の例)
	 * order by DRWG_NO, CREATE_DATE DESC
	 * @param searchConditionForm
	 * @return
	 */
	private String createSqlOrder(SearchConditionForm searchConditionForm) {
		SearchOrderSentenceMaker orderSentenceMaker = new SearchOrderSentenceMaker();
		// 条件1から5までを
		for (int i = 0; i < searchConditionForm.getSearchSelColNum(); i++) {
			orderSentenceMaker.addOrderCondition(searchConditionForm.getCondition(i),
					searchConditionForm.getSortWay(i), searchConditionForm.getSortOrder(i));
		}
		return orderSentenceMaker.getSqlOrder();
	}

	private void getScreenItemStrList(SearchConditionForm searchConditionForm, User user) {
		CsvItemStrList screenItemStrList;
		try {
			int langIdx = 0;

			if (!"Japanese".equals(user.getLanguage())) {
				langIdx = 1;
			}

			// 2013.06.14 yamagishi modified. start
			//			String beaHome = System.getenv("BEA_HOME");
			//			if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
			//			if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
			//			screenItemStrList = new CsvItemStrList(beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.screenItemStrList.path"));
			// 2013.06.14 yamagishi modified. end
			screenItemStrList = new CsvItemStrList(DrasapPropertiesFactory.getFullPath("tyk.csvdef.screenItemStrList.path"));
			// 2013.06.27 yamagishi modified. start
			//			searchConditionForm.setC_label1(screenItemStrList.getLineData(1)== null?"":screenItemStrList.getLineData(1).get(langIdx));
			//			searchConditionForm.setC_label2(screenItemStrList.getLineData(2)== null?"":screenItemStrList.getLineData(2).get(langIdx));
			//			searchConditionForm.setC_label3(screenItemStrList.getLineData(3)== null?"":screenItemStrList.getLineData(3).get(langIdx));
			//			searchConditionForm.setC_label4(screenItemStrList.getLineData(4)== null?"":screenItemStrList.getLineData(4).get(langIdx));
			//			searchConditionForm.setC_label5(screenItemStrList.getLineData(5)== null?"":screenItemStrList.getLineData(5).get(langIdx));
			//			searchConditionForm.setC_label6(screenItemStrList.getLineData(6)== null?"":screenItemStrList.getLineData(6).get(langIdx));
			//			searchConditionForm.setC_label7(screenItemStrList.getLineData(7)== null?"":screenItemStrList.getLineData(7).get(langIdx));
			//			searchConditionForm.setC_label8(screenItemStrList.getLineData(8)== null?"":screenItemStrList.getLineData(8).get(langIdx));
			ArrayList<String> lineData = null;
			searchConditionForm.setC_label1((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL1_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label2((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL2_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label3((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL3_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label4((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL4_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label5((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL5_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label6((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL6_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label7((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL7_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label8((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL8_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label9((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL9_LINE_NO)) == null ? "" : lineData.get(langIdx)); // 複数図番
			// 2013.06.27 yamagishi modified. end
			// 2019.09.25 yamamoto add. start
			searchConditionForm.setC_label10((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL10_LINE_NO)) == null ? "" : lineData.get(langIdx)); // パスワード変更
			searchConditionForm.setC_label11((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL11_LINE_NO)) == null ? "" : lineData.get(langIdx)); // ログアウト
			searchConditionForm.setC_label12((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL12_LINE_NO)) == null ? "" : lineData.get(langIdx)); // 原図庫作業依頼
			searchConditionForm.setC_label13((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL13_LINE_NO)) == null ? "" : lineData.get(langIdx)); // 原図庫作業依頼詳細
			searchConditionForm.setC_label14((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL14_LINE_NO)) == null ? "" : lineData.get(langIdx)); // 原図庫作業リスト
			searchConditionForm.setC_label15((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL15_LINE_NO)) == null ? "" : lineData.get(langIdx)); // アクセスレベル一括更新
			searchConditionForm.setC_label16((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL16_LINE_NO)) == null ? "" : lineData.get(langIdx)); // アクセスレベル更新結果
			// 2019.09.25 yamamoto add. end
			// 2020.03.10 yamamoto add. start
			searchConditionForm.setC_label17((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL17_LINE_NO)) == null ? "" : lineData.get(langIdx)); // 図番指定順
			// 2020.03.10 yamamoto add. end
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * お知らせメッセージを取得する。例外が発生した場合、errorsにaddする。
	 * @param infoFileName お知らせメッセージファイル名
	 * @param information メッセージ内容
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private String getSearchHelpMsg(String language) {
		String key;
		StringBuilder infoSb = null;
		if ("Japanese".equals(language)) {
			key = "tyk.csvdef.searchHelpMsg_J.path";
		} else {
			key = "tyk.csvdef.searchHelpMsg_E.path";
		}
		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		// 2013.06.14 yamagishi modified. end
		String infoFileName = DrasapPropertiesFactory.getFullPath(key);
		try {
			File infoFile = new File(infoFileName);
			if (!infoFile.exists()) {
				infoFile.createNewFile();
			}
			infoSb = new StringBuilder();
			BufferedReader inpBr = new BufferedReader(new FileReader(infoFileName), 128);
			while (inpBr.ready()) {
				infoSb.append(inpBr.readLine() + "\r");
			}
			inpBr.close();
		} catch (IOException e) {
		}
		return infoSb.toString();

	}

	// 2019.09.25 yamamoto add. start
	/**
	 * エラーメッセージを取得する
	 *
	 * @param user
	 * @param key
	 * @return
	 */
	private String getErrorMsg(User user, String key) {

		// properties取得
		String msg = "";
		ProfileString prop = null;
		try {
			prop = new ProfileString(this, "application.properties");
			msg = prop.getValue(key + "." + user.getLanKey());

		} catch (Exception e) {
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("application.propertiesの読み込み失敗" + ErrorUtility.error2String(e));
		}

		return msg;
	}
	// 2019.09.25 yamamoto add. end

	private boolean setMultiViewDrwgNos(SearchConditionForm form, HttpServletRequest request) {
		// 最初に、1つでも条件が入力されているか確認

		String[] drwgNoArray = (String[]) request.getSession().getAttribute("drwgNoArray");
		if (drwgNoArray == null || drwgNoArray.length == 0) {
			return false;
		}

		String conditionValue = "";
		for (int i = 0; i < drwgNoArray.length; i++) {
			if (i > 0) {
				conditionValue += ",";
			}
			conditionValue += drwgNoArray[i];
		}

		boolean setFlag = false;
		for (int i = 0; i < form.getSearchSelColNum(); i++) {
			if ("DRWG_NO".equals(form.getCondition(i))) {
				form.setConditionValue(i, conditionValue);
				setFlag = true;
				break;
			}
		}
		if (!setFlag) {
			form.setCondition(0, "DRWG_NO");
			form.setConditionValue(0, conditionValue);
		}
		return true;
	}
}