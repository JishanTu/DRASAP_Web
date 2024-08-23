package tyk.drasap.search;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;

/**
 * @author fumi
 * 作成日: 2004/01/20
 * @version 2013/09/14 yamagishi
 */
@SuppressWarnings("deprecation")
public class AclvChangePreAction extends Action {
	private static Category category = Category.getInstance(AclvChangePreAction.class.getName());
// 2013.06.26 yamagishi add. start
		private static DataSource ds;
		static{
			try{
				ds = DataSourceFactory.getOracleDataSource();
			} catch(Exception e){
				category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
			}
		}
// 2013.06.26 yamagishi add. end
	// --------------------------------------------------------- Method
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}
		// 前画面でのSearchResultFormを元に、次画面で使用するAclvChangeFormのデータを準備する。
		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		AclvChangeForm aclvChangeForm = new AclvChangeForm();
		setAclvChangeData(aclvChangeForm, searchResultForm, user);
		session.setAttribute("aclvChangeForm", aclvChangeForm);// sessionに格納
		//
		category.debug("--> success");
		return mapping.findForward("success");
	}
// 2013.09.14 yamagishi modified. start
	/**
	 * 前画面でのSearchResultFormを元に、次画面で使用するAclvChangeFormのデータを準備する。
	 * @param aclvChangeForm
	 * @param searchResultForm
	 * @param user
	 */
//	private void setAclvChangeData(AclvChangeForm aclvChangeForm,
//			SearchResultForm searchResultForm, User user) {
	private void setAclvChangeData(AclvChangeForm aclvChangeForm,
			SearchResultForm searchResultForm, User user) throws Exception {

		// 初期化
		aclvChangeForm.aclvChangeList = new ArrayList();
		aclvChangeForm.aclvNotChangeList = new ArrayList();
		aclvChangeForm.errorMessages = new ArrayList();

		Connection conn = null;
		conn = ds.getConnection();
		conn.setAutoCommit(false);// 非トランザクション

		try {

			// SearchResultFormから選択された図面を取得する
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				SearchResultElement resultElement = searchResultForm.getSearchResultElement(i);
				if (resultElement.isSelected()) {// 選択されているもののみ対象
//					if(user.isChangableAclv(resultElement)){
					if (user.isChangableAclv(resultElement, conn, (i > 0))) {
						// このユーザーが変更可能な図面なら
						aclvChangeForm.aclvChangeList.add(new AclvChangeElement(resultElement));

				} else {
					// 変更権がなければ
					// フォーマットした図番のみをセットする
					aclvChangeForm.aclvNotChangeList.add(resultElement.getDrwgNoFormated());
				}
			}
			}
			if (aclvChangeForm.aclvChangeList.size() == 0) {
				aclvChangeForm.errorMessages.add(MessageManager.getInstance().getMessage("msg.aclv.noselected.drwg"));
			}
			// 変更可能なアクセスレベルをセットする ////////////////////////////
			// 1) 初期化
			aclvChangeForm.aclvKeyList = new ArrayList();
			aclvChangeForm.aclvNameList = new ArrayList();
			// 2) ユーザーが持つ、アクセス権限HashMapから、アクセスレベルIDのListを作成
//			user.getAclMap().keySet();
//			ArrayList aclIdList = new ArrayList(user.getAclMap().keySet());
			HashMap<String, String> aclMap = null;
			ArrayList aclIdList = null;
			if (user.isAdmin()) {
				// 管理者はすべてのACLに変更できる
				aclIdList = AclvMasterDB.getAclvIdList(conn);
			} else {
				aclMap = user.getAclMap(conn);
				aclIdList = new ArrayList(aclMap.keySet());
			}
			Collections.sort(aclIdList);// ソートする
			for (int i = 0; i < aclIdList.size(); i++) {
				String aclId = (String) aclIdList.get(i);
//				if(Integer.parseInt((String) user.getAclMap().get(aclId)) >= 1){
				if (user.isAdmin() || Integer.parseInt(aclMap.get(aclId)) >= 1) {
					// 3) 参照権限を持つなら、そのアクセスIDに変更することができる
					aclvChangeForm.aclvKeyList.add(aclId);
					aclvChangeForm.aclvNameList.add(aclId);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try { conn.close(); } catch (Exception e) {}
		}
	}
// 2013.09.14 yamagishi modified. end

}
