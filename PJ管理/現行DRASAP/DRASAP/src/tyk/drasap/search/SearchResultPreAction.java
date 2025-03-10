package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.StringCheck;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * 検索条件に従って、実際に検索を行い、検索データをSearchResultFormに格納するAction。
 *
 * @version 2013/09/13 yamagishi
 */
@SuppressWarnings("deprecation")
public class SearchResultPreAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(SearchResultPreAction.class.getName());
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}

		SearchResultForm searchResultForm = null;


		// requsetパラメータを確認して、処理を振り分ける
		if("init".equals(request.getParameter("task"))){
			// クッキーから言語設定を取得
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
			// task=initなら・・・メニューから呼び出された場合
			// 初期化処理を行う
			searchResultForm = new SearchResultForm();
			setFormInitData(searchResultForm, user, request, errors);

		} else if("search".equals(request.getParameter("task"))){
			// task=searchなら・・・検索条件入力画面から呼び出された場合
			searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
			searchResultForm.setLanguage(user.getLanguage());
			// 検索の表示件数を、検索条件の入力フォームから取得
			SearchConditionForm searchConditionForm = (SearchConditionForm) session.getAttribute("searchConditionForm");
			// 検索結果を取得し、searchResultFormにセットする
			searchResultForm.searchResultList = search(user, null, request, errors, searchConditionForm);

			searchResultForm.dispNumberPerPage = searchConditionForm.getDisplayCount();// 1ページ当たりの表示件数
			searchResultForm.dispNumberOffest = "0";// 検索結果を表示するときのoffset値。最初のページはゼロ。
			searchResultForm.dispAttr1 = searchConditionForm.getDispAttr1();// 表示属性をコピー
			searchResultForm.dispAttr2 = searchConditionForm.getDispAttr2();
			searchResultForm.dispAttr3 = searchConditionForm.getDispAttr3();
			searchResultForm.dispAttr4 = searchConditionForm.getDispAttr4();
			searchResultForm.dispAttr5 = searchConditionForm.getDispAttr5();
			searchResultForm.dispAttr6 = searchConditionForm.getDispAttr6();
			// アクセスログを
			AccessLoger.loging(user, AccessLoger.FID_SEARCH, user.getSys_id());
		} else if("language".equals(request.getParameter("task"))){
			// task=languageなら
			searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
			searchResultForm.setLanguage(user.getLanguage());
			setFormInitData(searchResultForm, user, request, errors);
			// 検索結果を取得し、searchResultFormにセットする
//			searchResultForm.searchResultList = search(user, request, errors);
//			// 検索の表示件数を、検索条件の入力フォームから取得
//			SearchConditionForm searchConditionForm = (SearchConditionForm) session.getAttribute("searchConditionForm");
//			searchResultForm.dispNumberPerPage = searchConditionForm.getDisplayCount();// 1ページ当たりの表示件数
//			searchResultForm.dispNumberOffest = "0";// 検索結果を表示するときのoffset値。最初のページはゼロ。
//			searchResultForm.dispAttr1 = searchConditionForm.getDispAttr1();// 表示属性をコピー
//			searchResultForm.dispAttr2 = searchConditionForm.getDispAttr2();
//			searchResultForm.dispAttr3 = searchConditionForm.getDispAttr3();
//			searchResultForm.dispAttr4 = searchConditionForm.getDispAttr4();
//			searchResultForm.dispAttr5 = searchConditionForm.getDispAttr5();
//			searchResultForm.dispAttr6 = searchConditionForm.getDispAttr6();
//			// アクセスログを
		} else if("multipreview".equals(request.getParameter("task"))){
			String sys_id = user.getSys_id();
			// クッキーから言語設定を取得
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
			// task=initなら・・・メニューから呼び出された場合
			// 初期化処理を行う
			searchResultForm = new SearchResultForm();
			setFormInitData(searchResultForm, user, request, errors);

			//
			searchResultForm.setAct("init");
			searchResultForm.setLanguage(user.getLanguage());
			// 検索結果を取得し、searchResultFormにセットする
			searchResultForm.searchResultList = search(user, sys_id, request, errors, null);
			// アクセスログを
			AccessLoger.loging(user, AccessLoger.FID_SEARCH, sys_id);
		}
		// sessionにフォームを
		if(searchResultForm != null){
			// 画面表示文字列取得
			getScreenItemStrList(searchResultForm, user);
			session.setAttribute("searchResultForm", searchResultForm);
		}

		if(errors.isEmpty()){
			category.debug("--> success");
			return mapping.findForward("success");
		} else {
			saveErrors(request, errors);
			category.debug("--> error");
			return mapping.findForward("error");
		}

	}

	/**
	 * SearchResultFormに初期データをセットする。
	 * 表示項目、出力プロッタ
	 * @param searchResultForm
	 * @param user
	 */
	private void setFormInitData(SearchResultForm searchResultForm, User user,
				HttpServletRequest request, ActionMessages errors){
		// 表示属性のプルダウンに項目をセットする。
		// ユーザーに設定された権限により、異なる。
		searchResultForm.dispKeyList = new ArrayList<String>();
		searchResultForm.dispNameList = new ArrayList<String>();
		searchResultForm.dispKeyList.add("");
		searchResultForm.dispNameList.add("");

		// このユーザーが使用可能な属性のキーと名称をセット
		// '04.Apr.16 リファクタリング by Hirata
		SearchUtil sUtil = new SearchUtil();
		searchResultForm.dispKeyList.addAll(sUtil.createEnabledAttrList(user, true));
		searchResultForm.dispNameList.addAll(sUtil.createEnabledAttrList(user, false));


		// ユーザーの前回の表示項目をセットする
		if (((user.getViewSelCol1() == null) || (user.getViewSelCol1().length() == 0)) &&
		    ((user.getViewSelCol2() == null) || (user.getViewSelCol2().length() == 0)) &&
		    ((user.getViewSelCol3() == null) || (user.getViewSelCol3().length() == 0)) &&
		    ((user.getViewSelCol4() == null) || (user.getViewSelCol4().length() == 0)) &&
		    ((user.getViewSelCol5() == null) || (user.getViewSelCol5().length() == 0)) &&
		    ((user.getViewSelCol6() == null) || (user.getViewSelCol6().length() == 0))) {
			searchResultForm.dispAttr1 = "DRWG_SIZE";
			searchResultForm.dispAttr2 = "DRWG_TYPE";
			if (user.getLanguage().equals("Japanese")) {
				searchResultForm.dispAttr3 = "MACHINE_JP";
			} else {
				searchResultForm.dispAttr3 = "MACHINE_EN";
			}
			searchResultForm.dispAttr4 = "PROCUREMENT";
			if (user.getLanguage().equals("Japanese")) {
				searchResultForm.dispAttr5 = "SUPPLYER_JP";
			} else {
				searchResultForm.dispAttr5 = "SUPPLYER_EN";
			}
			searchResultForm.dispAttr6 = "CREATE_DATE";

		} else {
			searchResultForm.dispAttr1 = user.getViewSelCol1();
			searchResultForm.dispAttr2 = user.getViewSelCol2();
			searchResultForm.dispAttr3 = user.getViewSelCol3();
			searchResultForm.dispAttr4 = user.getViewSelCol4();
			searchResultForm.dispAttr5 = user.getViewSelCol5();
			searchResultForm.dispAttr6 = user.getViewSelCol6();
		}
		// 出力プロッタをセットする
		searchResultForm.printerKeyList = new ArrayList<String>();
		searchResultForm.printerNameList = new ArrayList<String>();
		searchResultForm.printerKeyList.add("");
		searchResultForm.printerNameList.add("");
		for(int i = 0; i < user.getEnablePrinters().size(); i++){
			Printer printer = (Printer) user.getEnablePrinters().get(i);
			searchResultForm.printerKeyList.add(printer.getId());
			searchResultForm.printerNameList.add(printer.getDisplayName());
		}
		// 1ページ当たりの表示件数を
		searchResultForm.dispNumberPerPage = user.getDisplayCount();
		if(searchResultForm.dispNumberPerPage == null || searchResultForm.dispNumberPerPage.equals("")){
			searchResultForm.dispNumberPerPage = "50";
		}

	}

	/**
	 * 検索条件を用いて、検索する。検索結果をSearchResultElementオブジェクトとして、ArrayListに格納して返す。
	 * @param user
	 * @param request
	 * @param errors
	 * @return
	 */
	private ArrayList<SearchResultElement> search(
			User user, String sys_id, HttpServletRequest request, ActionMessages errors, SearchConditionForm srchCondForm){

		ArrayList<SearchResultElement> searchResultList = new ArrayList<SearchResultElement>();
		// 一時的に検索結果を格納
		HashMap<String, SearchResultElement> srchRetMap = new HashMap<String, SearchResultElement>();

// 2013.09.13 yamagishi modified. start
		// requestに"SQL_WHERE"が格納されているか確認する。
		// なければ、検索せずに空のリストを返す
		String sqlWhere = (String) request.getAttribute("SQL_WHERE");
		String sqlOrder = (String) request.getAttribute("SQL_ORDER");
		if ("multipreview".equals(request.getParameter("task"))) {
			sqlWhere = (String)request.getSession().getAttribute("SQL_WHERE");
			sqlOrder = (String)request.getSession().getAttribute("SQL_ORDER");
		}
		if (sqlWhere == null || sqlOrder == null) {
			return searchResultList;
		}
// 2013.09.13 yamagishi modified. end
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		int rs1Count = 0; // 2013.06.26 yamagishi add.
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			HashMap<String, String> aclMap = user.getAclMap(conn); // 2013.09.13 yamagishi add.
			StringBuffer sbSql1 = new StringBuffer("select DRWG_NO, TO_CHAR(CREATE_DATE,'YY/MM/DD HH24:MI:SS') CRT_DATE,");
			sbSql1.append("CREATE_USER,MACHINE_JP,MACHINE_EN,USED_FOR,MATERIAL,TREATMENT,");
			sbSql1.append("PROCUREMENT,SUPPLYER_JP,SUPPLYER_EN,SUPPLYER_TYPE,");
			sbSql1.append("ATTACH01,ATTACH02,ATTACH03,ATTACH04,ATTACH05,");
			sbSql1.append("ATTACH06,ATTACH07,ATTACH08,ATTACH09,ATTACH10,");
			sbSql1.append("MACHINE_NO,MACHINE_NAME,");
			sbSql1.append("MACHINE_SPEC1,MACHINE_SPEC2,MACHINE_SPEC3,MACHINE_SPEC4,MACHINE_SPEC5,");
			sbSql1.append("DRWG_TYPE,DRWG_SIZE,ISSUE,SUPPLY,CAD_TYPE,ENGINEER,");
			sbSql1.append("PROHIBIT, TO_CHAR(PROHIBIT_DATE,'YY/MM/DD HH24:MI:SS') PRHB_DATE,");
			sbSql1.append("PROHIBIT_EMPNO,PROHIBIT_NAME,PAGES,");
			sbSql1.append("ACL_ID, TO_CHAR(ACL_UPDATE,'YY/MM/DD HH24:MI:SS') ACL_DATE,");
			sbSql1.append("ACL_EMPNO,ACL_NAME,ATTACH_MAX,LATEST_FLAG,REPLACE_FLAG,CREATE_DIV,MEDIA_ID,TWIN_DRWG_NO,");
			sbSql1.append("FILE_NAME,FILE_TYPE,PATH_NAME");
			sbSql1.append(",ACL_BALLOON");		// 2013.07.11 yamagishi add.
			// ACLのSelect case部分以下を作成し付加する
			sbSql1.append(" from INDEX_FILE_VIEW");
			sbSql1.append(" left outer join (select ACL_ID as ID,ACL_BALLOON from ACCESS_LEVEL_MASTER) tmp on tmp.ID = INDEX_FILE_VIEW.ACL_ID");	// 2013.07.11 yamagishi add.
			// Where部分以下を作成し付加する
			sbSql1.append(" ");
			sbSql1.append(sqlWhere);
			// Order部分以下を作成し付加する
			sbSql1.append(" ");
			sbSql1.append(sqlOrder);
			//
			stmt1 = conn.createStatement();
			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
// 2013.07.11 yamagishi modified. start
				SearchResultElement resultElement = new SearchResultElement(rs1.getString("DRWG_NO"),
						rs1.getString("FILE_NAME"),rs1.getString("FILE_TYPE"),rs1.getString("PATH_NAME"), rs1.getString("ACL_BALLOON"));
// 2013.07.11 yamagishi modified. end
				resultElement.addAttr("CREATE_DATE", rs1.getString("CRT_DATE"));
				resultElement.addAttr("CREATE_USER", rs1.getString("CREATE_USER"));
				resultElement.addAttr("MACHINE_JP", rs1.getString("MACHINE_JP"));
				resultElement.addAttr("MACHINE_EN", rs1.getString("MACHINE_EN"));
				resultElement.addAttr("USED_FOR", rs1.getString("USED_FOR"));
				resultElement.addAttr("MATERIAL", rs1.getString("MATERIAL"));
				resultElement.addAttr("TREATMENT", rs1.getString("TREATMENT"));
				resultElement.addAttr("PROCUREMENT", rs1.getString("PROCUREMENT"));
				resultElement.addAttr("SUPPLYER_JP", rs1.getString("SUPPLYER_JP"));
				resultElement.addAttr("SUPPLYER_EN", rs1.getString("SUPPLYER_EN"));
				resultElement.addAttr("SUPPLYER_TYPE", rs1.getString("SUPPLYER_TYPE"));
				resultElement.addAttr("ATTACH01", rs1.getString("ATTACH01"));
				resultElement.addAttr("ATTACH02", rs1.getString("ATTACH02"));
				resultElement.addAttr("ATTACH03", rs1.getString("ATTACH03"));
				resultElement.addAttr("ATTACH04", rs1.getString("ATTACH04"));
				resultElement.addAttr("ATTACH05", rs1.getString("ATTACH05"));
				resultElement.addAttr("ATTACH06", rs1.getString("ATTACH06"));
				resultElement.addAttr("ATTACH07", rs1.getString("ATTACH07"));
				resultElement.addAttr("ATTACH08", rs1.getString("ATTACH08"));
				resultElement.addAttr("ATTACH09", rs1.getString("ATTACH09"));
				resultElement.addAttr("ATTACH10", rs1.getString("ATTACH10"));
				resultElement.addAttr("MACHINE_NO", rs1.getString("MACHINE_NO"));
				resultElement.addAttr("MACHINE_NAME", rs1.getString("MACHINE_NAME"));
				resultElement.addAttr("MACHINE_SPEC1", rs1.getString("MACHINE_SPEC1"));
				resultElement.addAttr("MACHINE_SPEC2", rs1.getString("MACHINE_SPEC2"));
				resultElement.addAttr("MACHINE_SPEC3", rs1.getString("MACHINE_SPEC3"));
				resultElement.addAttr("MACHINE_SPEC4", rs1.getString("MACHINE_SPEC4"));
				resultElement.addAttr("MACHINE_SPEC5", rs1.getString("MACHINE_SPEC5"));
				resultElement.addAttr("DRWG_TYPE", rs1.getString("DRWG_TYPE"));
				String drwgSize = rs1.getString("DRWG_SIZE");// 図面サイズ
				resultElement.addAttr("DRWG_SIZE", drwgSize);
				resultElement.setPrintSize(drwgSize);// 印刷サイズにセットする
				resultElement.addAttr("ISSUE", rs1.getString("ISSUE"));
				resultElement.addAttr("SUPPLY", rs1.getString("SUPPLY"));
				resultElement.addAttr("CAD_TYPE", rs1.getString("CAD_TYPE"));
				resultElement.addAttr("ENGINEER", rs1.getString("ENGINEER"));
				resultElement.addAttr("PROHIBIT", rs1.getString("PROHIBIT"));
				resultElement.addAttr("PROHIBIT_DATE", rs1.getString("PRHB_DATE"));
				resultElement.addAttr("PROHIBIT_EMPNO", rs1.getString("PROHIBIT_EMPNO"));
				resultElement.addAttr("PROHIBIT_NAME", rs1.getString("PROHIBIT_NAME"));
				resultElement.addAttr("PAGES", rs1.getString("PAGES"));
				String aclId = rs1.getString("ACL_ID");// この図番のアクセスレベルID
				resultElement.addAttr("ACL_ID", aclId);
				resultElement.addAttr("ACL_UPDATE", rs1.getString("ACL_DATE"));
				resultElement.addAttr("ACL_EMPNO", rs1.getString("ACL_EMPNO"));
				resultElement.addAttr("ACL_NAME", rs1.getString("ACL_NAME"));
				resultElement.addAttr("ATTACH_MAX", rs1.getString("ATTACH_MAX"));
				resultElement.addAttr("LATEST_FLAG", rs1.getString("LATEST_FLAG"));
				resultElement.addAttr("REPLACE_FLAG", rs1.getString("REPLACE_FLAG"));
				resultElement.addAttr("CREATE_DIV", rs1.getString("CREATE_DIV"));
				resultElement.addAttr("MEDIA_ID", rs1.getString("MEDIA_ID"));
				resultElement.addAttr("TWIN_DRWG_NO", rs1.getString("TWIN_DRWG_NO"));
// 2013.09.13 yamagishi add. start
				resultElement.setAclFlag("0".equals(aclMap.get(aclId)) ? "0" : "1");
// 2013.09.13 yamagishi add. end
				// リンクパラメータのMap
				resultElement.linkParmMap.put("DRWG_NO", resultElement.getDrwgNo());// 図番
				resultElement.linkParmMap.put("FILE_NAME", resultElement.getFileName());// ファイル名
				resultElement.linkParmMap.put("PATH_NAME", resultElement.getPathName());// ディレクトリのフルパス
				resultElement.linkParmMap.put("DRWG_SIZE", drwgSize);// 図面サイズ
// 2013.06.26 yamagishi modified. start
				resultElement.linkParmMap.put("PDF", user.getViewPrintDoc(aclId, conn, (rs1Count > 0)));// PDF変換する?
// 2013.06.26 yamagishi modified. end
// 2020.03.11 yamamoto modified. start
				if (srchCondForm != null && srchCondForm.isOrderDrwgNo()) {
					// 図番指定順が有効の場合
					srchRetMap.put(resultElement.getDrwgNo(), resultElement);
					category.debug("DrwgNo: " + resultElement.getDrwgNo());
				} else {
					searchResultList.add(resultElement);
				}
// 2020.03.11 yamamoto modified. end
				rs1Count++; // 2013.06.26 yamagishi add.
			}

// 2020.03.11 yamamoto add. start
			// 図番指定順が有効の場合、複数図番の入力順に並び替える
			if (srchCondForm != null && srchCondForm.isOrderDrwgNo()) {
				// 念のためクリア
				searchResultList.clear();
				// 複数図番情報を取得
				String[] multiDrwgNo = srchCondForm.getMultipleDrwgNo().split(System.lineSeparator());
				category.debug("multiDrwgNo.length: " + multiDrwgNo.length);

				// 重複図番がある場合は1番目の図番のみ有効とする
				Set<String> uniqDrwgNo = new HashSet<String>();
				for(String drwgNo : multiDrwgNo) {

					// trim処理。ハイフン「-」を除く。
					drwgNo = StringCheck.trimWsp(drwgNo).replace("-", "");
					// 半角大文字に変換する
					drwgNo = StringCheck.changeDbToSbAscii(drwgNo).toUpperCase();

					if (drwgNo.length() <= 0) {
						// 空文字（改行コード）をスキップ
						continue;
					}

					if(uniqDrwgNo.contains(drwgNo)) {
						// 重複があれば次へ
						continue;
					} else {
						// 重複しなければ追加
						uniqDrwgNo.add(drwgNo);

						SearchResultElement tmpRetElem = srchRetMap.get(drwgNo);
						if (tmpRetElem != null) {
							// 複数図番と検索結果の図番が一致した場合は検索結果リストに格納
							searchResultList.add(tmpRetElem);
						}

						category.debug("drwgNo: " + drwgNo + " ret: " + srchRetMap.get(drwgNo));
					}
				}
			}
// 2020.03.11 yamamoto add. end

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), sys_id);
			// for MUR
			category.error("検索に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		//
		return searchResultList;
	}
	private void getScreenItemStrList(SearchResultForm searchResultForm, User user) {
		CsvItemStrList screenItemStrList;
		try {
			int langIdx = 0;

			if (!user.getLanguage().equals("Japanese")) langIdx = 1;

// 2013.06.14 yamagishi modified. start
//			String beaHome = System.getenv("BEA_HOME");
//			if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//			if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
//			screenItemStrList = new CsvItemStrList(beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.screenItemStrList.path"));
			String apServerHome = System.getenv(BEA_HOME);
			if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
			if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
			if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			screenItemStrList = new CsvItemStrList(apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.screenItemStrList.path"));
// 2013.06.14 yamagishi modified. end
// 2013.06.27 yamagishi modified. start
//			searchResultForm.setH_label1(screenItemStrList.getLineData(9)==null?"":screenItemStrList.getLineData(9).get(langIdx));
//			searchResultForm.setH_label2(screenItemStrList.getLineData(10)==null?"":screenItemStrList.getLineData(10).get(langIdx));
//			searchResultForm.setH_label3(screenItemStrList.getLineData(11)==null?"":screenItemStrList.getLineData(11).get(langIdx));
//			searchResultForm.setH_label4(screenItemStrList.getLineData(12)==null?"":screenItemStrList.getLineData(12).get(langIdx));
//			searchResultForm.setH_label5(screenItemStrList.getLineData(13)==null?"":screenItemStrList.getLineData(13).get(langIdx));
//			searchResultForm.setH_label6(screenItemStrList.getLineData(14)==null?"":screenItemStrList.getLineData(14).get(langIdx));
//
//			searchResultForm.setF_label1(screenItemStrList.getLineData(15)==null?"":screenItemStrList.getLineData(15).get(langIdx));
//			searchResultForm.setF_label2(screenItemStrList.getLineData(16)==null?"":screenItemStrList.getLineData(16).get(langIdx));
//			searchResultForm.setF_label3(screenItemStrList.getLineData(17)==null?"":screenItemStrList.getLineData(17).get(langIdx));
//			searchResultForm.setF_label4(screenItemStrList.getLineData(18)==null?"":screenItemStrList.getLineData(18).get(langIdx));
//			searchResultForm.setF_label5(screenItemStrList.getLineData(19)==null?"":screenItemStrList.getLineData(19).get(langIdx));
//			searchResultForm.setF_label6(screenItemStrList.getLineData(20)==null?"":screenItemStrList.getLineData(20).get(langIdx));
//			searchResultForm.setF_label7(screenItemStrList.getLineData(21)==null?"":screenItemStrList.getLineData(21).get(langIdx));
			ArrayList<String> lineData = null;
			searchResultForm.setH_label1((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL1_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setH_label2((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL2_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setH_label3((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL3_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setH_label4((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL4_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setH_label5((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL5_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setH_label6((lineData = screenItemStrList.getLineData(searchResultForm.H_LABEL6_LINE_NO)) == null ? "" : lineData.get(langIdx));

			searchResultForm.setF_label1((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL1_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label2((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL2_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label3((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL3_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label4((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL4_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label5((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL5_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label6((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL6_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchResultForm.setF_label7((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL7_LINE_NO)) == null ? "" : lineData.get(langIdx));
// 2013.06.27 yamagishi modified. end
// 2019.11.28 yamamoto add. start
			searchResultForm.setF_label8((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL8_LINE_NO)) == null ? "" : lineData.get(langIdx));
// 2019.11.28 yamamoto add. end
// 2020.03.10 yamamoto add. start
			searchResultForm.setF_label9((lineData = screenItemStrList.getLineData(searchResultForm.F_LABEL9_LINE_NO)) == null ? "" : lineData.get(langIdx));
// 2020.03.10 yamamoto add. end
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
}
