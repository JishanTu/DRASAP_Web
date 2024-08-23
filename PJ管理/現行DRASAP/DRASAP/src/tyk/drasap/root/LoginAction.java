package tyk.drasap.root;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
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
import tyk.drasap.common.UserDef;
import tyk.drasap.errlog.ErrorLoger;

/**
 * ログインのAction。
 */
public class LoginAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(LoginAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}
	// --------------------------------------------------------- Methods

	public LoginAction () {
		category.debug("start");
		category.debug("end");
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
		LoginForm loginForm = (LoginForm) form;
		// まずUserを作成する
		User user = new User(request.getRemoteAddr());

		HttpSession session = request.getSession();
		session.removeAttribute("user");// ついでにsessionから削除する

		// パスワード変更無しでパスワード変更画面を閉じた場合に
		// sessionに残るためここで削除
		session.removeAttribute("samePasswdId");

//		int localPort = request.getLocalPort();
//		int serverPort = request.getServerPort();
//        String container = "weblogic";
//        category.debug("container="+System.getenv("container"));
//        if (System.getenv("container") != null) {
//            container = "tomcat";
//        }
//		Properties appProp = DrasapPropertiesFactory.getDrasapProperties(
//				new DataSourceFactory());
//		try{
//			ds = DataSourceFactory.getOracleDataSource(serverPort);
//		} catch(Exception e){
//			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
//		}
//		Properties pop = System.getProperties();
//		category.debug("pop="+pop.toString());

		ActionMessages errors = new ActionMessages();
		// id、パスワードを元にユーザー情報を取得し、userオブジェクトに付加する。
		addUserInfo(user, loginForm.getId(), loginForm.getPasswd(), errors);
		// システム情報を管理者設定マスターから取得
		DrasapInfo drasapInfo = getDrasapInfo(user, errors);

		//
		if(errors.isEmpty()){
			// クッキーから言語設定を取得
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);
			// ユーザー情報が取得できたら sessionに格納する
			session.setAttribute("user", user);
			session.setAttribute("drasapInfo", drasapInfo);
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");

			// パスワード設定日 + 有効期限日数 < 現在日時の場合はパスワード変更
			int ret = isChangePassword(user, errors);
			if(0 != ret ) {
				if (1 == ret) {
					// パスワードがユーザIDと同じ場合はsessionに保持
					session.setAttribute("samePasswdId", "true");
				}
				// 遷移元をsessionに保持
				session.setAttribute("parentPage", "Login");
				category.debug("--> chgpasswd");
				return mapping.findForward("chgpasswd");
			}

			category.debug("--> success");
			return mapping.findForward("success");

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
	private void addUserInfo(User user, String id, String passwd, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			if(UserDB.addUserInfo(user, id, passwd, conn)){
				// id,パスワードが一致
			} else {
				// 一致しない
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("root.missmatch", "IDまたはPassword"));
			}

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo", e.getMessage()));
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
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.drasapinfo", e.getMessage()));
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

	/**
	 * パスワード変更有無チェック
	 *
	 * @param user
	 * @param errors
	 * @return 0: 変更無 <br/> 1: 変更有(パスワードがユーザID) <br/> 2: 変更有(有効期限切れ)
	 * @throws Exception
	 */
	private int isChangePassword(User user, ActionMessages errors) throws Exception {

		/*
		 * 現在のパスワードチェック
		 */
	    Connection conn = null;
		conn = ds.getConnection();
		conn.setAutoCommit(true);// 非トランザクション

		String userId = user.getId();
	    String currentPass = UserDB.getPassword(userId, conn);

	    if(StringUtils.isEmpty(currentPass) || userId.equals(currentPass)) {
	    	// パスワード未設定 もしくは パスワードがユーザIDと同じ
	    	return 1;
	    }

		/*
		 *  パスワード有効期限の確認
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar passwdUpCalendar = Calendar.getInstance();

		// パスワード定義ファイルから値取得
		UserDef userdef = new UserDef();
		HashMap<String, String> passwdDefMap = userdef.getPasswdDefinition(errors);

		// パスワード有効期限日数チェック
		int pwdLmtDay = Integer.parseInt(passwdDefMap.get(UserDef.PWD_LMT_DAY));

		// 現在日時の取得
		Calendar nowCal = Calendar.getInstance();

		// 時分秒をクリア
	    nowCal.clear(Calendar.MINUTE);
	    nowCal.clear(Calendar.SECOND);
	    nowCal.clear(Calendar.MILLISECOND);
	    nowCal.set(Calendar.HOUR_OF_DAY, 0);

	    // パスワード設定日取得
	    Date pwdUpDate = user.getPasswdUpdDate();

	    if(pwdUpDate == null ) {
	    	// パスワード設定日が未設定の場合は再設定対象
	    	return 2;
	    }

		passwdUpCalendar.setTime(pwdUpDate); // DATE -> Calendar

		// パスワード設定日 + 有効期限日数
		passwdUpCalendar.add(Calendar.DATE, pwdLmtDay);

		Date passwdLimitDate = passwdUpCalendar.getTime(); // Calendar -> DATE
		Date nowDate = nowCal.getTime(); // Calendar -> DATE

		category.debug("Now Date=" + sdf.format(nowDate));
		category.debug("Password Limit Date=" + sdf.format(passwdLimitDate));

		// パスワード設定日 + 有効期限日数 < 現在日時の場合はパスワード変更
		if(nowDate.after(passwdLimitDate)) {
			return 2;
		}

		return 0;
	}

}
