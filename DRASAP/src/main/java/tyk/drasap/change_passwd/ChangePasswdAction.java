package tyk.drasap.change_passwd;

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

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

public class ChangePasswdAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(ChangePasswdAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		category.debug("ChangePassword start");

		ChangePasswdForm passwdForm = (ChangePasswdForm) form;
		ActionMessages errors = new ActionMessages();

		HttpSession session = request.getSession();

		// セッションからuser情報取得
		User user = (User)session.getAttribute("user");

		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}

		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false); // 自動コミットしない

//			category.debug("oldpass=" + passwdForm.getOldpass());
//			category.debug("newpass=" + passwdForm.getNewpass());

			// パスワード更新
			UserDB.updatePassword(user.getId(), passwdForm.getNewpass(), conn);

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("ユーザー情報の取得に失敗\n" + ErrorUtility.error2String(e));
			throw e;
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}

		String page = (String)session.getAttribute("parentPage");
		String forward = "";
		if(errors.isEmpty()){
			if(StringUtils.isEmpty(page) || "Search".equals(page)) {
				// 遷移元が図面検索画面の場合
				category.debug("--> success");
				forward = "successFromSearch";
			} else if ("Login".equals(page)) {
				// 遷移元がログイン画面の場合
				category.debug("--> success");
				forward = "successFromLogin";
			}
			// sessionから削除
			session.removeAttribute("samePasswdId");
//			session.removeAttribute("parentPage");
		} else {
			category.debug("--> failed");
			// 失敗の場合はパスワード変更画面に遷移
			forward = "failed";
		}

		return mapping.findForward(forward);

	}
}
