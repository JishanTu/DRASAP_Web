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
import tyk.drasap.common.USER_ID_CONVERSION_DB;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;

/**
 * <PRE>
 * 他のシステムから、図面ビューイング機能を直接使用するためのログイン。
 * ログインに成功すれば図面ビューイング機能へ遷移する。
 * パラメータ(en_string)を受け取り、暗号複号化ツール(IDDE)を使用してIDを取得する。
 * その他の必須パラメータとして、drwg_no、sys_idがある。
 * 
 * en_string・・・暗号化されたログインID
 * drwg_no・・・図番。DRASAPデータベースに登録された形式で。現行はハイフン抜きの形式。
 * sys_id・・・依頼したシステムのシステムID。ログに記録されます。
 * </PRE>
 * @author fumi
 * 作成日 2005/03/03
 * 変更日 $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 */
public class DirectLoginForPreviewAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(DirectLoginForPreviewAction.class.getName());
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
		
		ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		String id = "";
		String drwgNo = "";
		String sys_id = "";
		String user_id_col = "";
		try{
		
			// sessionからパラメータを取得し、複号する		
			String enString = (String) session.getAttribute("en_string");
			session.removeAttribute("en_string");// sessionからremoveする
			category.debug("en_string = "+ enString);		
			id = IDDE.decode(enString);// 複合化した結果
			category.debug("id = "+ id);
			// パラメーター sys_id
			sys_id = (String) session.getAttribute("sys_id");
			session.removeAttribute("sys_id");// sessionからremoveする
			category.debug("sys_id = "+ sys_id);
			user.setSys_id(sys_id);
			// パラメーター drwg_no
			drwgNo = (String)session.getAttribute("drwgNo");
			category.debug("drwg_no = "+ drwgNo);
			if (drwgNo == null || drwgNo.length() == 0) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.invalid.dragno." + user.getLanKey(),"DRWG_NO is null"));
				throw new UserException("drwgNo is null");
			}
			// パラメーター user_id_col
			user_id_col = (String) session.getAttribute("user_id_col");
			session.removeAttribute("user_id_col");// sessionからremoveする
			category.debug("user_id_col = "+ user_id_col);
	
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
	
			if(id.equals("-1")){
				// ログインID異常
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"ログインID異常"));
			} else if(id.equals("-2")){
				// SYSpass異常
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.decode." + user.getLanKey(),id,"SYSpass異常"));
			} else {
				// idを元にユーザー情報を取得し、userオブジェクトに付加する。
				addUserInfo(user, id, user_id_col, errors);
				// システム情報を管理者設定マスターから取得
				drasapInfo = getDrasapInfo(user, errors);
			}
		} catch (Exception e) {
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			category.error("他のシステムからのログインに失敗\n" + ErrorUtility.error2String(e));
		}
		
		if(errors.isEmpty()){
			// クッキーから言語設定を取得
			CookieManage langCookie = new CookieManage();
			String lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			// ユーザー情報が取得できたら sessionに格納する
			session.setAttribute("user", user);

			session.setAttribute("drasapInfo", drasapInfo);
			
			// DirectPreviewに必要なパラメータをrequestにsetAttributeする
			request.setAttribute("usr_id",id);// ユーザーID
			request.setAttribute("drwgNo",drwgNo);// 図番
			
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
	private void addUserInfo(User user, String id, String user_id_col, ActionMessages errors){
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			
			String drasapUserId = id;
			//　ユーザID変換テーブルのカラム名が指定されていたらDRASAPユーザＩＤに変換
			if (user_id_col != null && user_id_col.length() > 0)
				drasapUserId = USER_ID_CONVERSION_DB.getDrasapUserId(user_id_col, id, conn);
			
			if(UserDB.addUserInfo(user, drasapUserId.trim(), conn)){
				// idが一致
			} else {
				// 一致しない
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("directlogin.undefined.drasapuserid."+user.getLanKey(),"Id"));
				// for システム管理者
				ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
				// for MUR
				category.error("ユーザID変換テーブルにDRASAPユーザIDが登録されていません。user_id=" + drasapUserId);
			}
			
		} catch(UserException e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("directlogin.undefined.extuserid." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("ユーザID変換テーブルに外部システムのユーザIDが登録されていません\n" + ErrorUtility.error2String(e));
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.userinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
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
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("root.failed.get.drasapinfo." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("システム情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
		return drasapInfo;
		
	}
}
