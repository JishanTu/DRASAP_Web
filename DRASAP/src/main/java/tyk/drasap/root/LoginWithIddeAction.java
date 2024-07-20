package tyk.drasap.root;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import jp.co.toyodakouki.IDDE;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
/**
 * シングルサインオンのためのログイン。ポータルからのログインに対応するため。
 * パラメータ(en_string)を受け取り、暗号複号化ツール(IDDE)を使用してIDを取得する。
 * メニューを介さずに、直接ファンクションに遷移するために、パラメータfnを追加。'04.May.13
 * @version 2013/07/25 yamagishi
 */
public class LoginWithIddeAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(LoginWithIddeAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}
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
		// まずUserを作成する
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ついでにsessionから削除する
		category.debug("session ID="+session.getId());
		ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		// sessionからパラメータを取得し、複号する
		String enString = (String) session.getAttribute("en_string");
		session.removeAttribute("en_string");// sessionからremoveする
		category.debug("en_string = "+ enString);
		if (enString == null) {
			return mapping.findForward("timeout");
		}
		String id = IDDE.decode(enString);// 復号化した結果
		category.debug("id = "+ id);
		// パラメータ fnを追加
		String fn = (String) session.getAttribute("fn");
		session.removeAttribute("fn");// sessionからremoveする
		if (fn == null) {
			return mapping.findForward("timeout");
		}
		category.debug("fn = "+ fn);

		if(id.equals("-1")){
			// ログインID異常
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"ログインID異常"));
		} else if(id.equals("-2")){
			// SYSpass異常
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"SYSpass異常"));
		} else {
//			int serverPort = request.getServerPort();
//			category.debug("serverPort="+Integer.toString(serverPort));
//			try{
//				ds = DataSourceFactory.getOracleDataSource(serverPort);
//			} catch(Exception e){
//				category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
//			}
			// idを元にユーザー情報を取得し、userオブジェクトに付加する。
			addUserInfo(user, id, errors);

			// システム情報を管理者設定マスターから取得
			drasapInfo = getDrasapInfo(user, errors);
		}

		if(errors.isEmpty()){
			// クッキーから言語設定を取得し、userオブジェクトに付加する。
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			// ユーザー情報が取得できたら sessionに格納する
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
			// パラメータfnによって処理を分ける。'04.May.13変更
			// fn = 1-4 は、グローバルフォワードを使用する。
			if("1".equals(fn)){
				// fn = 1 なら　図面検索　
				category.debug("--> search_search_Main");
				return mapping.findForward("search_search_Main");

			} else if("2".equals(fn)){
				// fn = 2 なら　原図庫作業依頼　
				category.debug("--> genzu_irai_request");
				return mapping.findForward("genzu_irai_request");

			} else if("3".equals(fn)){
				// fn = 3 なら　原図庫作業依頼詳細　
				category.debug("--> genzu_irai_request_ref");
				return mapping.findForward("genzu_irai_request_ref");

			} else if("4".equals(fn)){
				// fn = 4 なら　原図庫作業依頼リスト
				category.debug("--> genzu_irai_request_list");
				return mapping.findForward("genzu_irai_request_list");

// 2013.07.25 yamagishi add. start
			} else if ("5".equals(fn)) {
				// fn = 5 なら　アクセスレベル一括更新
				category.debug("--> acl_batch_update");
				return mapping.findForward("acl_batch_update");
			} else if ("6".equals(fn)) {
				// fn = 6 なら　アクセスレベル更新結果
				category.debug("--> acl_updated_result");
				return mapping.findForward("acl_updated_result");
// 2013.07.25 yamagishi add. end

			} else{
				// それ以外の場合はメニューへ
				category.debug("--> success");
				return mapping.findForward("success");

			}

		} else {
			saveErrors(request, errors);// エラーを登録
			category.debug("--> failed");
			return mapping.findForward("failed");
		}
	}

	/**
	 * id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			if(UserDB.addUserInfo(user, id, conn)){
				// idが一致
			} else {
				// 一致しない
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.missmatch."+user.getLanKey(),"Id"));
			}

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
	}

	/**
	 * システム情報を取得する。
	 * エラーがあればerrorsにエラー登録する。
	 * @param user
	 * @param errors
	 */
	private DrasapInfo getDrasapInfo(User user, ActionMessages errors){
		Connection conn = null;
		DrasapInfo drasapInfo = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			drasapInfo = AdminSettingDB.getDrasapInfo(conn);

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.drasapinfo.jp",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("システム情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		return drasapInfo;

	}

}
