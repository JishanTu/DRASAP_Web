package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.struts.action.ActionRedirect;

import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;

/**
 * <PRE>
 * 他のシステムから、図面ビューイング機能を直接使用するときに使用するAction。
 * 実際のPreview機能は、DRASAP内部からのときと同様にPreviewActionを使用する。
 * そのために必要な情報の収集などをこのActionで行う。
 * DirectLoginForPreviewActionから遷移してくる。
 * </PRE>
 * @author fumi
 * 作成日 2005/03/03
 * 変更日 $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/26 yamagishi
 */
public class DirectPreviewAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(DirectPreviewAction.class.getName());
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
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}
		// クッキーから言語設定を取得
		CookieManage langCookie = new CookieManage();
		String lanKey = langCookie.getCookie (request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
		user.setLanguage (lanKey);

		// requestから必要な情報を取得します。
//		String usr_id = (String) request.getAttribute("usr_id");
//		String drwg_no = (String) request.getAttribute("drwg_no");
		String drwgNo = (String)request.getAttribute("drwgNo");
		//String sys_id = user.getSys_id();

		session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
		// drwg_noで検索する、次画面に必要な情報を取得する
		Map<String, String> linkParmMap = null;
		try {
			linkParmMap = search(drwgNo, user, errors);
		} catch(Exception e){
			// エラーがあったら
			saveErrors(request, errors);// エラーを登録
			request.setAttribute("hasError","true");// jspでエラー表示に使用する

			return mapping.findForward("error");
		}

		request.setAttribute("drwgNo", drwgNo);
//		String path = request.getContextPath() + "/search/switch.do?prefix=/search&page=/preview.do";
		String path = "/preview.do";
		path = path + "?DRWG_NO=" + linkParmMap.get("DRWG_NO");
		path = path + "&FILE_NAME=" + linkParmMap.get("FILE_NAME");
		path = path + "&PATH_NAME=" + linkParmMap.get("PATH_NAME");
		path = path + "&DRWG_SIZE=" + linkParmMap.get("DRWG_SIZE");
		path = path + "&PDF=" + linkParmMap.get("PDF");
	    category.info("redirect to preview.do");
		return new ActionRedirect(path);
//		return mapping.findForward("success");
	}
	/**
	 * 指定した図番で検索する。
	 * 正常なら、パラメーターMapを返す。格納するのはPreviewActionに必要なパラメータ。
	 * エラーがあれば、全て errors に格納する。
	 * チェック内容は、
	 * 1) 図番が登録されているか？
	 * 2) アクセス権限があるか？
	 * @param drwg_no 図番
	 * @param user
	 * @param sys_id 呼び出し側のシステムID
	 * @param errors
	 * @return PreviewActionに必要なパラメータを格納したMap
	 * @throws Exception
	 */
	private Map<String, String> search(String drwgNo, User user, ActionMessages errors) throws Exception{
		Map<String, String> linkParmMap = new HashMap<String, String>();
		Connection conn = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
			StringBuffer sbSql1 = new StringBuffer("select DRWG_NO, FILE_NAME, PATH_NAME, DRWG_SIZE, ACL_ID");
			sbSql1.append(" from INDEX_FILE_VIEW");
			sbSql1.append(" where DRWG_NO='");
			sbSql1.append(drwgNo);
			sbSql1.append("'");

			pstmt1 = conn.prepareStatement(sbSql1.toString());

			rs1 = pstmt1.executeQuery();
			if (rs1.next()) {
				String aclId = rs1.getString("ACL_ID");// アクセスレベルID
// 2013.06.26 yamagishi modified. start
//				String aclValue = (String)user.getAclMap().get(aclId);// アクセスレベル値
				String aclValue = (String)user.getAclMap(conn).get(aclId);// アクセスレベル値
// 2013.06.26 yamagishi modified. end
				if (aclValue == null || aclValue.equals("0")) {
					// このユーザーから導かれるアクセスレベル値が null または 0 なら
					// 図面を参照する権限もない。つまり表示できない。
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.no.authority.reading." + user.getLanKey()));
					// for MUR
					category.error("権限なし[" + drwgNo + "]");
					throw new UserException("");
				} else {
					// Previewに必要な情報を linkParmMap に 格納する。
					linkParmMap.put("DRWG_NO", drwgNo);// 図番
					linkParmMap.put("FILE_NAME", rs1.getString("FILE_NAME"));// ファイル名
					linkParmMap.put("PATH_NAME", rs1.getString("PATH_NAME"));// ディレクトリのフルパス
					linkParmMap.put("DRWG_SIZE", rs1.getString("DRWG_SIZE"));// 図面サイズ
// 2013.06.26 yamagishi modified. start
//					linkParmMap.put("PDF", user.getViewPrintDoc(aclId));// PDF変換する?
					linkParmMap.put("PDF", user.getViewPrintDoc(aclId, conn));// PDF変換する?
// 2013.06.26 yamagishi modified. end
				}
			} else {
				// 指定図番なし
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.not.registered." + user.getLanKey(),drwgNo));
				// for MUR
				category.error("指定図番なし[" + drwgNo + "]");
				throw new UserException("");
			}
		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("検索に失敗\n" + ErrorUtility.error2String(e));
			throw e;

		} finally {
			try{ if (rs1 != null) rs1.close(); } catch(Exception e) {}
			try{ if (pstmt1 != null) pstmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}

		return linkParmMap;
	}
//	private class DrwgNoInfo {
//		String drwgNo = "";
//		String info = "";
//		int status = 0;
//	}
}
