package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;


/**
 * ログインのAction。
 *
 * @version 2013/06/14 yamagishi
 */
public class SystemMaintenanceLoginAction extends Action {
	private static DataSource ds;
//	private static Category category = Category.getInstance(SystemMaintenanceLoginAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
//			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
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
//		category.debug("start");
		SystemMaintenanceLoginForm loginForm = (SystemMaintenanceLoginForm) form;
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ついでにsessionから削除する
		ActionMessages errors = new ActionMessages();

		// id、パスワードチェック
		chkPasswd(loginForm.getId(), loginForm.getPasswd(), errors);
		// id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// システム情報を管理者設定マスターから取得
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

		if(errors.isEmpty()){
			// ユーザー情報が取得できたら sessionに格納する
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
//			category.debug("--> success");
			return mapping.findForward("success");

		} else {
			saveErrors(request, errors);// エラーを登録
//			category.debug("--> failed");
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
	private void chkPasswd(String id, String passwd, ActionMessages errors){
		CsvItemStrList delDwgPs;
// 2013.06.14 yamagishi modified. start
//		String beaHome = System.getenv("BEA_HOME");
//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
//		String passwdFile = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		String passwdFile = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
// 2013.06.14 yamagishi modified. end
		try {
			delDwgPs = new CsvItemStrList(passwdFile);
			if (delDwgPs.searchLineData(id) != null) {
				if (!delDwgPs.searchLineData(id).get(1).equals(passwd)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.maintenanceLogin.passMissmatch","Password"));
				}
			} else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.maintenanceLogin.userNotFound",id));

			}

		} catch (FileNotFoundException e) {
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.maintenanceLogin.FileNotFound",passwdFile));
			// for MUR
//			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
		} catch (IOException e) {
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo.jp",e.getMessage()));
			// for MUR
//			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
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
	private void addUserInfo(User user, String id, String passwd, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			if(UserDB.addUserInfo(user, id, conn)){
				// id,パスワードが一致
			} else {
				// 一致しない
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.missmatch."+user.getLanKey(),"IdまたはPassword"));
			}

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
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
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.drasapinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("システム情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		return drasapInfo;

	}

}
