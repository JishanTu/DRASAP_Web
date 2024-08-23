package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * ログインのAction。
 *
 * @version 2013/06/13 yamagishi
 */
public class Delete_LoginAction extends Action {
	private static Category category = Category.getInstance(Delete_LoginAction.class.getName());
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
		Delete_LoginForm loginForm = (Delete_LoginForm) form;
		HttpSession session = request.getSession();
		ActionMessages errors = new ActionMessages();

		User user = (User) session.getAttribute("user");

		// id、パスワードチェック
		chkPasswd(user, loginForm.getPasswd(), errors);
		if(errors.isEmpty()){
			// ユーザー情報が取得できたら sessionに格納する
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
	private void chkPasswd(User user, String passwd, ActionMessages errors){
		CsvItemStrList delDwgPs;
// 2013.06.13 yamagishi modified. start
//		String beaHome = System.getenv("BEA_HOME");
//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
//		String passwdFile = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		String passwdFile = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.delDwgPs.path");
// 2013.06.13 yamagishi modified. end
		try {
			delDwgPs = new CsvItemStrList(passwdFile);
			if (delDwgPs.searchLineData(user.getId()) != null) {
				if (!delDwgPs.searchLineData(user.getId()).get(1).equals(passwd)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delLogin.passMissmatch","Password"));
				}
			} else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delLogin.userNotFound",user.getName()));

			}

		} catch (FileNotFoundException e) {
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delLogin.FileNotFound",passwdFile));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
		} catch (IOException e) {
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			return;
		}
	}

}
