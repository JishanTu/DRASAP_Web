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
 * アクセスレベルマスターメンテナンス処理アクション
 * @author Y.eto
 * 作成日: 2006/07/10 
 */
public class AccessLevelMasterMaintenanceAction extends Action {
	private static DataSource ds;
//	private static Category category = Category.getInstance(AccessLevelMasterMaintenanceAction.class.getName());
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
		AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm = (AccessLevelMasterMaintenanceForm) form;
		if("init".equals(request.getParameter("act"))){
		    accessLevelMasterMaintenanceForm.setAct("init");
		}
	    accessLevelMasterMaintenanceForm.clearErrorMsg();
		
		//
		if ("init".equals(accessLevelMasterMaintenanceForm.getAct())){
		    accessLevelMasterMaintenanceForm.tableInfo = getAclLevelMasterAttrList(user, errors);
			ArrayList<AccessLevelMasterMaintenanceElement> groupMasterList = getAclMaster(user, errors);
			accessLevelMasterMaintenanceForm.setRecList(groupMasterList);
		    session.removeAttribute("accessLevelMasterMaintenanceForm");
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return mapping.findForward("success");
		} else if("update".equals(accessLevelMasterMaintenanceForm.getAct())){
		    if (inputCheck(accessLevelMasterMaintenanceForm)) {
				return mapping.findForward("update");
		    }
		    updateAccessLevelMasterMaintenanceForm(accessLevelMasterMaintenanceForm, user, errors);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			if(errors.isEmpty()){
				return mapping.findForward("update");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}
		} else if("delete".equals(accessLevelMasterMaintenanceForm.getAct())){
		    deleteAccessLevelMasterMaintenanceForm(accessLevelMasterMaintenanceForm, user, errors);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return mapping.findForward("success");
		} else if("addrecord".equals(accessLevelMasterMaintenanceForm.getAct())){
		    AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = addNewRecord();
		    accessLevelMasterMaintenanceForm.addRecList(accessLevelMasterMaintenanceElement);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return mapping.findForward("success");
		} else if ("search".equals(accessLevelMasterMaintenanceForm.getAct())){
			ArrayList<AccessLevelMasterMaintenanceElement> groupMasterList = getAclMaster(user, errors);
			accessLevelMasterMaintenanceForm.clearRecList();
			accessLevelMasterMaintenanceForm.setRecList(groupMasterList);
		    session.removeAttribute("accessLevelMasterMaintenanceForm");
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return mapping.findForward("search");
		}
		
		return mapping.findForward("success");
	}
	/**
	 * アクセスレベルマスターの更新。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateAccessLevelMasterMaintenanceForm(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		//PreparedStatement pstmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
 			// チェックしているものだけkoushinn
            for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
                AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
                if (accessLevelMasterMaintenanceElement.isNew()) {
                    if (aclIdDuplicateChk(accessLevelMasterMaintenanceElement.getAclId(), user, errors) > 0) {
                        accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;");	// 
                        accessLevelMasterMaintenanceForm.addErrorMsg("※アクセスレベルＩＤ「"+accessLevelMasterMaintenanceElement.getAclId() + "」はすでに使用しています。");
            			continue;
                    }
                    insertRecord(i, accessLevelMasterMaintenanceElement, user, conn, errors);
                } else if (accessLevelMasterMaintenanceElement.isUpdate()) {
                	updateRecord(i, accessLevelMasterMaintenanceElement, user, conn, errors);
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
	 * アクセスレベルマスターの削除
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void deleteAccessLevelMasterMaintenanceForm(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
            for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
                AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
                if (accessLevelMasterMaintenanceElement.isUpdate()) {
                    if (aclLevelUsedCheck(accessLevelMasterMaintenanceElement.getAclId(), user, errors)) {
            			try{ conn.rollback(); } catch(Exception e2){}
            			accessLevelMasterMaintenanceForm.addErrorMsg("※削除しようとしたアクセスレベル「"+accessLevelMasterMaintenanceElement.getAclName() + "」は図面属性情報で使用しているため削除できません。");
            			return;
                    }
                    StringBuffer sbSql1 = new StringBuffer("delete ACCESS_LEVEL_MASTER");
        			sbSql1.append(" where ");
       				sbSql1.append("ACL_ID='");
       				sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
       				sbSql1.append("' ");

           			stmt1 = conn.createStatement();
        			stmt1.executeUpdate(sbSql1.toString());
        			
        			deleteAclRelation(accessLevelMasterMaintenanceElement.getAclId(), user, conn, errors);
                }
            }
            for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
                AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
                if (accessLevelMasterMaintenanceElement.isUpdate()) {
        			accessLevelMasterMaintenanceForm.deleteRecList(accessLevelMasterMaintenanceElement.getAclId());
        			i--;
                }
            }
			
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの削除に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの削除に失敗\n" + ErrorUtility.error2String(e));
		} finally {
		    try{conn.commit();} catch(Exception e2){}
		    try{conn.setAutoCommit(true);} catch(Exception e2){}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    
	}
	/**
	 * アクセスレベルマスター更新
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateRecord(int recno, AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement, User user, Connection conn, ActionMessages errors) throws IOException {
		PreparedStatement pstmt1 = null;
		try{
            StringBuffer sbSql1 = new StringBuffer("update ACCESS_LEVEL_MASTER");
			sbSql1.append(" set ");
			sbSql1.append("ACL_ID=?,");
			sbSql1.append("ACL_NAME=?");
			sbSql1.append(" where ACL_ID = '");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
			sbSql1.append("' ");
	
			pstmt1 = conn.prepareStatement(sbSql1.toString());
			
			pstmt1.setString(1, accessLevelMasterMaintenanceElement.getAclId());
			pstmt1.setString(2, accessLevelMasterMaintenanceElement.getAclName());
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
			accessLevelMasterMaintenanceElement.setUpdate(false);
		}
	}
	/**
	 * アクセスレベルマスター追加
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void insertRecord(int recno, AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement, User user, Connection conn, ActionMessages errors) throws IOException {
		Statement stmt1 = null;
		try{
            StringBuffer sbSql1 = new StringBuffer("insert into ACCESS_LEVEL_MASTER");
			sbSql1.append(" (");
			sbSql1.append("ACL_ID,");
			sbSql1.append("ACL_NAME");
			sbSql1.append(")");
			sbSql1.append(" values(");
			sbSql1.append("'");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
			sbSql1.append("',");
			sbSql1.append("'");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclName());
			sbSql1.append("'");
			sbSql1.append(")");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			stmt1.executeUpdate(sbSql1.toString());
			try{ stmt1.close(); } catch(Exception e) {}
			
			insertAclRelation(accessLevelMasterMaintenanceElement.getAclId(), user, conn , errors);
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの挿入に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの挿入に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ stmt1.close(); } catch(Exception e) {}
			accessLevelMasterMaintenanceElement.setUpdate(false);
			accessLevelMasterMaintenanceElement.setNew(false);
		}
	}
	/**
	 * 検アクセスレベルマスターリスト取得
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<AccessLevelMasterMaintenanceElement> getAclMaster(User user, ActionMessages errors) throws IOException {
	    ArrayList<AccessLevelMasterMaintenanceElement> recList = new ArrayList<AccessLevelMasterMaintenanceElement>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select ACL_ID,ACL_NAME");
			sbSql1.append(" from ACCESS_LEVEL_MASTER order by ACL_ID");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			
			while(rs1.next()){
			    AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = new AccessLevelMasterMaintenanceElement();

			    accessLevelMasterMaintenanceElement.aclId = rs1.getString("ACL_ID");
			    accessLevelMasterMaintenanceElement.aclName = rs1.getString("ACL_NAME");
		        recList.add(accessLevelMasterMaintenanceElement);
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
	 * 「追加」ボタン押下時の処理。画面上で一行追加する
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private AccessLevelMasterMaintenanceElement addNewRecord() throws IOException {
	    AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = new AccessLevelMasterMaintenanceElement();
	    accessLevelMasterMaintenanceElement.setUpdate(true);
	    accessLevelMasterMaintenanceElement.setNew(true);
	    return accessLevelMasterMaintenanceElement;
	    
	}
	/**
	 * 利用者グループアクセス関連を挿入する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void insertAclRelation(String aclId, User user, Connection conn, ActionMessages errors) throws IOException {
		Statement stmt1 = null;
		try{
		    ArrayList<String> groupCodeList = getGroupCodeList(user, errors);
		    for (int i = 0; i < groupCodeList.size(); i++) {
	            StringBuffer sbSql1 = new StringBuffer("insert into USER_GRP_ACL_RELATION");
				sbSql1.append(" (");
				sbSql1.append("ACL_ID,");
				sbSql1.append("USER_GRP_CODE,");
				sbSql1.append("IMPORT_PRIV,");
				sbSql1.append("REFER_PRIV,");
				sbSql1.append("PRINT_PRIV,");
				sbSql1.append("CHANGE_PRIV,");
				sbSql1.append("MESSAGE_PRIV,");
				sbSql1.append("LATEST_REV_ONLY");
				sbSql1.append(")");
				sbSql1.append(" values(");
				sbSql1.append("'");
				sbSql1.append(aclId);
				sbSql1.append("','");
				sbSql1.append(groupCodeList.get(i).toString());
				sbSql1.append("','0','0','0','0','0','0')");
				stmt1 = conn.createStatement();
//				category.debug(sbSql1.toString());
				stmt1.executeUpdate(sbSql1.toString());
				try{ stmt1.close(); } catch(Exception e) {}
		    }
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの挿入に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの挿入に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ stmt1.close(); } catch(Exception e) {}
		}
	}
	/**
	 * 利用者グループアクセス関連を削除する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void deleteAclRelation(String aclId, User user, Connection conn, ActionMessages errors) throws IOException {
		Statement stmt1 = null;
		try{
            StringBuffer sbSql1 = new StringBuffer("delete USER_GRP_ACL_RELATION");
			sbSql1.append(" where ");
			sbSql1.append("ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("' ");

   			stmt1 = conn.createStatement();
			stmt1.executeUpdate(sbSql1.toString());
			
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"マスターの削除に失敗" + ErrorUtility.error2String(e));
			// for MUR
//			category.error("マスターの削除に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ stmt1.close(); } catch(Exception e) {}
		}
	    
	}
	/**
	 * グループコードリスト作成
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<String> getGroupCodeList(User user, ActionMessages errors) throws IOException {
	    ArrayList<String> groupCodeList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select USER_GRP_CODE");
			sbSql1.append(" from USER_GROUP_MASTER order by USER_GRP_CODE");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while(rs1.next()){
			    groupCodeList.add(rs1.getString("USER_GRP_CODE"));
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
	    return groupCodeList;
	    
	}
	/**
	 * 入力チェック
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private boolean inputCheck(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm) {
	    boolean sts = false;
	    String aclId = null;
	    String aclName = null;
        for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
            AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
            aclId = accessLevelMasterMaintenanceElement.getAclId();
            accessLevelMasterMaintenanceElement.setAclIdStyle("");
            if (aclId == null || aclId.length() == 0) {
                accessLevelMasterMaintenanceElement.setAclIdStyle("background-color:#FF0000;");	// color:#FF0000
		        accessLevelMasterMaintenanceForm.addErrorMsg("※アクセスレベルＩＤを入力してください。");

                sts = true;
            } else if (aclId.length() > accessLevelMasterMaintenanceForm.tableInfo.getColInfo("ACL_ID").getData_length()) {
                accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;");	// 
		        accessLevelMasterMaintenanceForm.addErrorMsg("※入力したアクセスレベルＩＤの文字数が多すぎます。");

                sts = true;
            }
            aclName = accessLevelMasterMaintenanceElement.getAclName();
            accessLevelMasterMaintenanceElement.setAclNameStyle("");
            if (aclName == null || aclName.length() == 0) {
            } else if (aclName.length() > accessLevelMasterMaintenanceForm.tableInfo.getColInfo("ACL_NAME").getData_length()) {
                accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;");	// 
		        accessLevelMasterMaintenanceForm.addErrorMsg("※入力したアクセスレベル名の文字数が多すぎます。");

                sts = true;
            }
        }
        
        return sts;
	}
	/**
	 * アクセスレベルマスターカラム情報取得
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private TableInfo getAclLevelMasterAttrList(User user, ActionMessages errors) throws IOException {
	    TableInfo tableInfo = null;
	    Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			tableInfo = TableInfoDB.getTableInfoArray("ACCESS_LEVEL_MASTER", conn);
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
	 * アクセスレベルが使用中かチェックする。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private boolean aclLevelUsedCheck(String aclId, User user, ActionMessages errors) throws IOException {
//	    ArrayList drwgTableList = TableInfoArray.getTableArry();
	    boolean sts = false;
	    Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		int hit = 0;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select  count(*) CNT from INDEX_DB");
			sbSql1.append(" where ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("'");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if(rs1.next()){
				hit = rs1.getInt("CNT");
				if (hit > 0) sts = true;
			}
			StringBuffer sbSql2 = new StringBuffer("select  count(*) CNT from NOTIFY_NO");
			sbSql2.append(" where ACL_ID='");
			sbSql2.append(aclId);
			sbSql2.append("'");
			stmt1 = conn.createStatement();
//			category.debug(sbSql2.toString());
			rs1 = stmt1.executeQuery(sbSql2.toString());
			if(rs1.next()){
				hit = rs1.getInt("CNT");
				if (hit > 0) sts = true;
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
	    return sts;
	    
	}
	/**
	 * ユーザーＩＤがすでに登録されているかを検索する。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private int aclIdDuplicateChk(String aclId, User user, ActionMessages errors) throws IOException {
	    int recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select count(*) CNT");
			sbSql1.append(" from ACCESS_LEVEL_MASTER ");
			sbSql1.append(" where ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("'");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			
			if (rs1.next()){
			    recCount = rs1.getInt("CNT");
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
	    return recCount;
	    
	}
}
