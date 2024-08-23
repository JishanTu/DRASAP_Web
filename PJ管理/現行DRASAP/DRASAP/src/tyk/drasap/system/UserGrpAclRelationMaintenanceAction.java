package tyk.drasap.system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/** 
 * 利用者グループアクセスレベル関連メンテナンス画面のAction。
 */
public class UserGrpAclRelationMaintenanceAction extends Action {
	private static DataSource ds;
//	private static Category category = Category.getInstance(UserGrpAclRelationMaintenanceAction.class.getName());
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
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm = (UserGrpAclRelationMaintenanceForm) form;
		if("init".equals(request.getParameter("act"))){
		    userGrpAclRelationMaintenanceForm.setAct("init");
		}
		userGrpAclRelationMaintenanceForm.clearErrorMsg();
		
		//
		if ("init".equals(userGrpAclRelationMaintenanceForm.getAct())){
			addStatusList(userGrpAclRelationMaintenanceForm);
		    addGroupCodeList(userGrpAclRelationMaintenanceForm, user, errors);
		    addAclList(userGrpAclRelationMaintenanceForm, user, errors);
		    userGrpAclRelationMaintenanceForm.tableInfo = getUserGrpAclRelationMasterAttrList(user, errors);
			ArrayList<UserGrpAclRelationMaintenanceElement> userGrpAclRelationMasterList = getUserGrpAclRelationMaster(userGrpAclRelationMaintenanceForm, "aclId", user, errors);
			userGrpAclRelationMaintenanceForm.setRecList(userGrpAclRelationMasterList);
		    session.removeAttribute("userGrpAclRelationMaintenanceForm");
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			return mapping.findForward("success");
		} else if("update".equals(userGrpAclRelationMaintenanceForm.getAct())){
		    updateUserGrpAclRelationMaintenanceForm(userGrpAclRelationMaintenanceForm, user, errors);
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			if(errors.isEmpty()){
				return mapping.findForward("update");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}
		} else if ("search".equals(userGrpAclRelationMaintenanceForm.getAct())){
			ArrayList<UserGrpAclRelationMaintenanceElement> userGrpAclRelationMasterList = getUserGrpAclRelationMaster(userGrpAclRelationMaintenanceForm, userGrpAclRelationMaintenanceForm.getOrderBy(), user, errors);
			userGrpAclRelationMaintenanceForm.clearRecList();
			userGrpAclRelationMaintenanceForm.setRecList(userGrpAclRelationMasterList);
		    session.removeAttribute("userGrpAclRelationMaintenanceForm");
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			return mapping.findForward("search");
		}
		
		return mapping.findForward("success");
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateUserGrpAclRelationMaintenanceForm(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		//PreparedStatement pstmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
 			// チェックしているものだけkoushinn
            for (int i = 0; i < userGrpAclRelationMaintenanceForm.getRecList().size(); i++) {
                UserGrpAclRelationMaintenanceElement userGrpAclRelationMaintenanceElement = userGrpAclRelationMaintenanceForm.getRecList(i);
                if (userGrpAclRelationMaintenanceElement.isUpdate()) {
                	updateRecord(i, userGrpAclRelationMaintenanceElement, user, conn, errors);
                }
            }
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターテーブルの更新に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターテーブルの更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
		    try{conn.commit();} catch(Exception e2){}
		    try{conn.setAutoCommit(true);} catch(Exception e2){}
			//try{ pstmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateRecord(int recno, UserGrpAclRelationMaintenanceElement userGrpAclRelationMasterMaintenanceElement, User user, Connection conn, ActionMessages errors) throws IOException {
		PreparedStatement pstmt1 = null;
		try{
            StringBuffer sbSql1 = new StringBuffer("update USER_GRP_ACL_RELATION");
			sbSql1.append(" set ");
			sbSql1.append("ACL_VALUE=?");
			sbSql1.append(" where ACL_ID = '");
			sbSql1.append(userGrpAclRelationMasterMaintenanceElement.getAclId());
			sbSql1.append("' ");
			sbSql1.append(" and USER_GRP_CODE = '");
			sbSql1.append(userGrpAclRelationMasterMaintenanceElement.getUserGrpCode());
			sbSql1.append("' ");
	
			pstmt1 = conn.prepareStatement(sbSql1.toString());
			
			pstmt1.setString(1, userGrpAclRelationMasterMaintenanceElement.getAclValue());
//			category.debug(sbSql1.toString());
			pstmt1.executeUpdate();
			try{ pstmt1.close(); } catch(Exception e) {}
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this, "マスターの更新に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの更新に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ pstmt1.close(); } catch(Exception e) {}
			userGrpAclRelationMasterMaintenanceElement.setUpdate(false);
		}
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<UserGrpAclRelationMaintenanceElement> getUserGrpAclRelationMaster(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, String orderBy, User user, ActionMessages errors) throws IOException {
	    ArrayList<UserGrpAclRelationMaintenanceElement> recList = new ArrayList<UserGrpAclRelationMaintenanceElement>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select USER_GRP_CODE, ACL_ID, ACL_VALUE");
			sbSql1.append(" from USER_GRP_ACL_RELATION");
			if (orderBy.equals("aclId")) {
				sbSql1.append(" order by ACL_ID, USER_GRP_CODE");
			} else {
				sbSql1.append(" order by USER_GRP_CODE, ACL_ID");
			}
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while(rs1.next()){
			    UserGrpAclRelationMaintenanceElement userGrpAclRelationMasterMaintenanceElement = new UserGrpAclRelationMaintenanceElement();

			    userGrpAclRelationMasterMaintenanceElement.aclId = rs1.getString("ACL_ID");
			    userGrpAclRelationMasterMaintenanceElement.aclName = userGrpAclRelationMaintenanceForm.getAclName(rs1.getString("ACL_ID"));
			    userGrpAclRelationMasterMaintenanceElement.aclValue = rs1.getString("ACL_VALUE");
			    userGrpAclRelationMasterMaintenanceElement.userGrpCode = rs1.getString("USER_GRP_CODE");
			    userGrpAclRelationMasterMaintenanceElement.userGrpName = userGrpAclRelationMaintenanceForm.getGroupName(rs1.getString("USER_GRP_CODE"));
		        recList.add(userGrpAclRelationMasterMaintenanceElement);
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの取得に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return recList;
	    
	}
	/**
	 * グループリストを取得する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void addGroupCodeList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			userGrpAclRelationMaintenanceForm.clearGroupCodeList();
			userGrpAclRelationMaintenanceForm.clearGroupNameList();
			StringBuffer sbSql1 = new StringBuffer("select USER_GRP_CODE, USER_GRP_NAME");
			sbSql1.append(" from USER_GROUP_MASTER order by USER_GRP_CODE");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while(rs1.next()){
			    userGrpAclRelationMaintenanceForm.addGroupCodeList(rs1.getString("USER_GRP_CODE"));
			    userGrpAclRelationMaintenanceForm.addGroupNameList(rs1.getString("USER_GRP_NAME"));
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの取得に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return;
	    
	}
	/**
	 * アクセスレヴェルリストを取得する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void addAclList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm,User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			userGrpAclRelationMaintenanceForm.clearAclIdList();
			userGrpAclRelationMaintenanceForm.clearAclNameList();
			StringBuffer sbSql1 = new StringBuffer("select ACL_ID, ACL_NAME");
			sbSql1.append(" from ACCESS_LEVEL_MASTER order by ACL_ID");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while(rs1.next()){
			    userGrpAclRelationMaintenanceForm.addAclIdList(rs1.getString("ACL_ID"));
			    userGrpAclRelationMaintenanceForm.addAclNameList(rs1.getString("ACL_NAME"));
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの取得に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return;
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private TableInfo getUserGrpAclRelationMasterAttrList(User user, ActionMessages errors) throws IOException {
	    TableInfo tableInfo = null;
	    Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			tableInfo = TableInfoDB.getTableInfoArray("USER_GRP_ACL_RELATION", conn);
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("テーブル情報の取得に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
	    return tableInfo;
	    
	}
	/**
	 * アクセスレベル値のリストを作成する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void addStatusList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm) {
		userGrpAclRelationMaintenanceForm.clearAclValueList();
		userGrpAclRelationMaintenanceForm.clearAclValueNameList();
		userGrpAclRelationMaintenanceForm.addAclValueList("1");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("検索・参照");
		userGrpAclRelationMaintenanceForm.addAclValueList("2");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("印刷");
		userGrpAclRelationMaintenanceForm.addAclValueList("3");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("変更");
	    return;
	    
	}
}
