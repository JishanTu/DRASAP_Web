package tyk.drasap.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletContext;
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
import org.apache.struts.upload.FormFile;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.common.UserKeyColDB;
import tyk.drasap.common.UserKeyColInfo;
import tyk.drasap.errlog.ErrorLoger;

/** 
 * テーブルメンテナンス画面のAction。
 */
public class TableMaintenanceAction extends Action {
	private static DataSource ds;
//	private static Category category = Category.getInstance(TableMaintenanceAction.class.getName());
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
		TableMaintenanceForm tableMaintenanceForm = (TableMaintenanceForm) form;
		if("init".equals(request.getParameter("act"))){
		    tableMaintenanceForm.setAct("init");
		}
		
		tableMaintenanceForm.clearErrorMsg();
		//
		if ("init".equals(tableMaintenanceForm.getAct())){
			ArrayList<String> tableList = getTableList(user, errors);
			Connection conn = null;
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
			UserKeyColDB UserKeyColDB = new UserKeyColDB(user, conn);
			try{ conn.close(); } catch(Exception e) {}
			tableMaintenanceForm = new TableMaintenanceForm();
			tableMaintenanceForm.setTableList(tableList);
			tableMaintenanceForm.UserKeyColDB = UserKeyColDB;
			String initTable = tableMaintenanceForm.getTableList().get(0).toString();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(initTable, UserKeyColDB, user, errors);
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(initTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("success");
		} else if("update".equals(tableMaintenanceForm.getAct())){
		    if (inputCheck(tableMaintenanceForm)) {
				return mapping.findForward("update");
		    }
		    updateTableMaintenanceForm(tableMaintenanceForm, user, errors);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			if(errors.isEmpty()){
				return mapping.findForward("update");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}
		} else if("delete".equals(tableMaintenanceForm.getAct())){
		    deleteTableMaintenanceForm(tableMaintenanceForm, user, errors);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			if(errors.isEmpty()){
				return mapping.findForward("success");
			} else {
				saveErrors(request, errors);
				return  mapping.findForward("error");
			}
		} else if("addrecord".equals(tableMaintenanceForm.getAct())){
		    TableMaintenanceRec tableMaintenanceRec = addNewRecord(tableMaintenanceForm);
			tableMaintenanceForm.addRecList(tableMaintenanceRec);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("success");
		} else if ("search".equals(tableMaintenanceForm.getAct())){
		    tableMaintenanceForm.clearWhereStr();
		    String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(selectTable, tableMaintenanceForm.UserKeyColDB, user, errors);
		    tableMaintenanceForm.setFromRecNo(0);
		    tableMaintenanceForm.setToRecNo(tableMaintenanceForm.getRecNoPerPage());
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("search");
		} else if ("wheresearch".equals(tableMaintenanceForm.getAct())){
		    String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(selectTable, tableMaintenanceForm.UserKeyColDB, user, errors);
		    tableMaintenanceForm.setFromRecNo(0);
		    tableMaintenanceForm.setToRecNo(tableMaintenanceForm.getRecNoPerPage());
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("search");
		} else if("prevpage".equals(tableMaintenanceForm.getAct())){
		    if (tableMaintenanceForm.getFromRecNo() <= 0) {
				return mapping.findForward("success");
		    }
		    long prevStartRecNo = tableMaintenanceForm.getFromRecNo() - tableMaintenanceForm.getRecNoPerPage();
		    long prevEndNo = tableMaintenanceForm.getFromRecNo();
		    
		    tableMaintenanceForm.setFromRecNo(prevStartRecNo);
		    tableMaintenanceForm.setToRecNo(prevEndNo);
		    String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("success");
		} else if("nextpage".equals(tableMaintenanceForm.getAct())){
		    if (tableMaintenanceForm.getToRecNo() >= tableMaintenanceForm.getRecCount()) {
				return mapping.findForward("success");
		    }
		    long nextStartRecNo = tableMaintenanceForm.getToRecNo();
		    long nextEndNo = tableMaintenanceForm.getToRecNo() + tableMaintenanceForm.getRecNoPerPage();
		    if (nextEndNo >= tableMaintenanceForm.getRecCount()) nextEndNo = tableMaintenanceForm.getRecCount();
		    
		    tableMaintenanceForm.setFromRecNo(nextStartRecNo);
		    tableMaintenanceForm.setToRecNo(nextEndNo);
		    String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("success");
		} else if("directpage".equals(tableMaintenanceForm.getAct())){
		    long startRec = Long.parseLong(tableMaintenanceForm.getSelectPage());
		    long endRec = startRec + tableMaintenanceForm.getRecNoPerPage();
		    if (endRec >= tableMaintenanceForm.getRecCount()) endRec = tableMaintenanceForm.getRecCount();
		    
		    tableMaintenanceForm.setFromRecNo(startRec);
		    tableMaintenanceForm.setToRecNo(endRec);
		    String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
		    session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return mapping.findForward("success");
		} else if("export".equals(tableMaintenanceForm.getAct())){
		    String selectTable = tableMaintenanceForm.getSelectTable();
		    createCsv(request, response, selectTable, user, tableMaintenanceForm, errors);
			return null;
		} else if("inport".equals(tableMaintenanceForm.getAct())){
		    String selectTable = tableMaintenanceForm.getSelectTable();
		    uploadCsv(request, response, selectTable, user, tableMaintenanceForm, errors);
			return mapping.findForward("success");
		}
		
		return mapping.findForward("success");
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateTableMaintenanceForm(TableMaintenanceForm tableMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
//			String selectedTable = tableMaintenanceForm.getSelectTable();
 			// チェックしているものだけkoushinn
//            ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();
            for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
                TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
                if (tableMaintenanceRec.isNew()) {
                    if (duplicateChk(i, tableMaintenanceForm, user, errors) > 0) {
                        tableMaintenanceForm.addErrorMsg("※このレコードはすでに存在しています。");
            			continue;
                    }
                    insertRecord(i, tableMaintenanceForm, user, conn, errors);
                } else if (tableMaintenanceRec.isCheck()) {
                	updateRecord(i, tableMaintenanceForm, user, conn, errors);
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
			try{ conn.close(); } catch(Exception e) {}
		}
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void deleteTableMaintenanceForm(TableMaintenanceForm tableMaintenanceForm, User user, ActionMessages errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
			String selectedTable = tableMaintenanceForm.getSelectTable();
 			// チェックしているものだけkoushinn
//            ArrayList attrInfo = tableMaintenanceForm.getAttrList();
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
			    tableMaintenanceForm.addErrorMsg("このテーブル[" + selectedTable + "]はキーが無いためメンテナンスできません。");
			    return;
			}
            for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
                TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
                if (tableMaintenanceRec.isCheck()) {
                    StringBuffer sbSql1 = new StringBuffer("delete ");
        			sbSql1.append(selectedTable);
        			if (userKeyColInfo.getNoKeyCol() > 0) sbSql1.append(" where ");
        			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
        			    if (j != 0) sbSql1.append("and ");
        				sbSql1.append(userKeyColInfo.getkeyCol(j));
        				sbSql1.append("='");
        			    int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
        				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
        				sbSql1.append(tableMaintenanceVal.getVal());
        				sbSql1.append("' ");
        			}

           			stmt1 = conn.createStatement();
        			stmt1.executeUpdate(sbSql1.toString());
        			
        			tableMaintenanceForm.recList.remove(i);
                }
            }
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
		    try{conn.commit();} catch(Exception e2){}
		    try{conn.setAutoCommit(true);} catch(Exception e2){}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void updateRecord(int recno, TableMaintenanceForm tableMaintenanceForm, User user, Connection conn, ActionMessages errors) throws IOException {
		PreparedStatement pstmt1 = null;
        TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try{
			String selectedTable = tableMaintenanceForm.getSelectTable();
            ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();

            StringBuffer sbSql1 = new StringBuffer("update ");
			sbSql1.append(selectedTable);
			sbSql1.append(" set ");
			for (int j = 0; j < attrInfo.size(); j++) {
			    if (j != 0) sbSql1.append(",");
	            TableMaintenanceElement tableMaintenanceElement = (TableMaintenanceElement)attrInfo.get(j);
	            sbSql1.append(tableMaintenanceElement.getColumn_name());
	            if (tableMaintenanceElement.getColumn_name().equals("MODIFIED_DATE")) {
		            sbSql1.append("=sysdate ");
	            } else if (tableMaintenanceElement.getData_type().equals("DATE")) {
	                sbSql1.append("=TO_DATE(?,'YY/MM/DD HH24:MI:SS')");
	            } else {
		            sbSql1.append("=? ");
	            }
			}
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
			    tableMaintenanceForm.addErrorMsg("このテーブル[" + selectedTable + "]はキーが無いためメンテナンスできません。");
			    return;
			}
			sbSql1.append("where ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
			    if (j != 0) sbSql1.append("and ");
				sbSql1.append(userKeyColInfo.getkeyCol(j));
				sbSql1.append("='");
			    int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
				sbSql1.append(tableMaintenanceVal.getVal());
				sbSql1.append("' ");
			}
	
			pstmt1 = conn.prepareStatement(sbSql1.toString());
			int idx=1;
			for (int j = 0; j < tableMaintenanceRec.getValList().size(); j++) {
	            TableMaintenanceElement tableMaintenanceElement = (TableMaintenanceElement)attrInfo.get(j);
	            if (tableMaintenanceElement.getColumn_name().equals("MODIFIED_DATE")) continue;
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				pstmt1.setString(idx++, tableMaintenanceVal.getVal());
			}
//			category.debug(sbSql1.toString());
			pstmt1.executeUpdate();
			try{ pstmt1.close(); } catch(Exception e) {}
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ pstmt1.close(); } catch(Exception e) {}
            tableMaintenanceRec.setNew(false);
            tableMaintenanceRec.setCheck(false);
		}
	}
	/**
	 * 入力チェックを行う。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private boolean inputCheck(TableMaintenanceForm tableMaintenanceForm) {
	    boolean sts = false;
        ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();
        for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
            TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
            for (int j = 0; j < attrInfo.size(); j++) {
	            TableMaintenanceElement tableMaintenanceElement = (TableMaintenanceElement)attrInfo.get(j);
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				tableMaintenanceVal.setDispStyle("");
	            if (tableMaintenanceElement.isKey()) {
		            if (tableMaintenanceVal.getVal() == null || tableMaintenanceVal.getVal().length() == 0) {
		                tableMaintenanceVal.setDispStyle("background-color:#FF0000;");	// color:#FF0000
		                tableMaintenanceForm.addErrorMsg("※" + tableMaintenanceElement.getColumn_name() + "を入力してください。");
	
		                sts = true;
		            } else if (tableMaintenanceVal.getVal().length() > tableMaintenanceForm.getAttrList(j).getData_length()) {
		                tableMaintenanceVal.setDispStyle("color:#FF0000;");	// 
		                tableMaintenanceForm.addErrorMsg("※入力した" + tableMaintenanceElement.getColumn_name() + "の文字数が多すぎます。");
	
		                sts = true;
		            }
	            } else if (tableMaintenanceElement.getNullable().equals("N")) {	// not null
		            if (tableMaintenanceVal.getVal() == null || tableMaintenanceVal.getVal().length() == 0) {
		                tableMaintenanceVal.setDispStyle("background-color:#FF0000;");	// color:#FF0000
		                tableMaintenanceForm.addErrorMsg("※" + tableMaintenanceElement.getColumn_name() + "を入力してください。");
	
		                sts = true;
		            }
	            }
			}
        }
        
        return sts;
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void insertRecord(int recno, TableMaintenanceForm tableMaintenanceForm, User user, Connection conn, ActionMessages errors) throws IOException {
		Statement stmt1 = null;
        TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try{
			String selectedTable = tableMaintenanceForm.getSelectTable();
            ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();

            StringBuffer sbSql1 = new StringBuffer("insert into ");
			sbSql1.append(selectedTable);
			sbSql1.append(" (");
			for (int j = 0; j < attrInfo.size(); j++) {
			    if (j != 0) sbSql1.append(",");
	            TableMaintenanceElement tableMaintenanceElement = (TableMaintenanceElement)attrInfo.get(j);
	            sbSql1.append(tableMaintenanceElement.getColumn_name());
			}
			sbSql1.append(")");
			sbSql1.append(" values(");
			for (int j = 0; j < tableMaintenanceRec.getValList().size(); j++) {
			    if (j != 0) sbSql1.append(",");
	            TableMaintenanceElement tableMaintenanceElement = (TableMaintenanceElement)attrInfo.get(j);
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				if (tableMaintenanceElement.getData_type().equals("DATE")) {
					sbSql1.append("TO_DATE('");
					sbSql1.append(tableMaintenanceVal.getVal());
					sbSql1.append("','YY/MM/DD HH24:MI:SS')");
				} else {
					sbSql1.append("'");
					sbSql1.append(tableMaintenanceVal.getVal());
					sbSql1.append("'");
				}
			}
			sbSql1.append(")");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			stmt1.executeUpdate(sbSql1.toString());
			try{ stmt1.close(); } catch(Exception e) {}
		} catch(Exception e){
			try{ conn.rollback(); } catch(Exception e2){}
		    
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ stmt1.close(); } catch(Exception e) {}
            tableMaintenanceRec.setNew(false);
            tableMaintenanceRec.setCheck(false);
		}
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private TableMaintenanceRec addNewRecord(TableMaintenanceForm tableMaintenanceForm) throws IOException {
	    ArrayList<TableMaintenanceElement> attrList = new ArrayList<TableMaintenanceElement>();

		attrList = tableMaintenanceForm.getAttrList();
	    ArrayList<TableMaintenanceVal> recValue = new ArrayList<TableMaintenanceVal>();
	    TableMaintenanceRec tableMaintenanceRec = new TableMaintenanceRec();
	    for (int i = 0; i < attrList.size(); i++) {
	        TableMaintenanceVal tableMaintenanceVal = new TableMaintenanceVal();
	        tableMaintenanceVal.val = null;
	        recValue.add(tableMaintenanceVal);
	    }
	    tableMaintenanceRec.setRec_no(Integer.toString(tableMaintenanceForm.getRecList().size()+1));
        tableMaintenanceRec.setValList(recValue);
        tableMaintenanceRec.setNew(true);
        tableMaintenanceRec.setCheck(true);
	    return tableMaintenanceRec;
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<TableMaintenanceElement> getTableAttrList(String selectTable, UserKeyColDB UserKeyColDB, User user, ActionMessages errors) throws IOException {
	    ArrayList<TableMaintenanceElement> attrList = new ArrayList<TableMaintenanceElement>();
	    Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			TableInfo tableInfo = TableInfoDB.getTableInfoArray(selectTable, conn);
			for (int i = 0; i < tableInfo.getNoCol(); i++) {
			    TableMaintenanceElement attrInfo = new TableMaintenanceElement();
			    attrInfo.setColumn_name(tableInfo.getColInfo(i).getColumn_name());
			    attrInfo.setData_type(tableInfo.getColInfo(i).getData_type());
			    attrInfo.setdata_length(tableInfo.getColInfo(i).getData_length());
			    attrInfo.setKey("0");
			    attrInfo.setNullable(tableInfo.getColInfo(i).getNullable());
			    for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
			        if (tableInfo.getColInfo(i).getColumn_name().equals(userKeyColInfo.getkeyCol(j))) {
					    attrInfo.setKey("1");
					    break;
			        }
			    }
			    attrList.add(attrInfo);
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("テーブル情報の取得にに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ conn.close(); } catch(Exception e) {}
		}
	    return attrList;
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<String> getTableList(User user, ActionMessages errors) throws IOException {
	    ArrayList<String> tableList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select table_name from ");
			sbSql1.append("user_tables");
			sbSql1.append(" where ");
//			sbSql1.append(" TABLESPACE_NAME like '%");
//			sbSql1.append(user.getSchema());
//			sbSql1.append("%' and");
			sbSql1.append(" dropped='NO' ");
//			sbSql1.append(" and TABLE_NAME like '%MASTER%'");
			sbSql1.append(" order by TABLE_NAME ");
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while(rs1.next()){
			    tableList.add(rs1.getString("TABLE_NAME"));
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return tableList;
	    
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private ArrayList<TableMaintenanceRec> getTableValue(String selectTable, ArrayList<TableMaintenanceElement> attrList, TableMaintenanceForm tableMaintenanceForm, User user, ActionMessages errors) throws IOException {
	    ArrayList<TableMaintenanceRec> tableValue = new ArrayList<TableMaintenanceRec>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
		    long recCount = getRecCount(tableMaintenanceForm.getWhereStr(), selectTable, user, errors);
		    tableMaintenanceForm.setRecCount(recCount);
//		    if (recCount > 20000) {
//                tableMaintenanceForm.addErrorMsg("※このテーブルはレコード数が多いため、対象にしません。");
//                return tableValue;
//		    }
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			TableMaintenanceElement tableMaintenanceElement = null;
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
			    tableMaintenanceForm.addErrorMsg("このテーブル[" + selectTable + "]はキーが無いためメンテナンスできません。");
			    return tableValue;
			}

			StringBuffer sbSql1 = new StringBuffer("select * from (");
		    sbSql1.append("select row_number() over (");
		    sbSql1.append(" order by ");
			if (userKeyColInfo.getNoKeyCol() > 0) {
				for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				    if (j != 0) sbSql1.append(", ");
					sbSql1.append(userKeyColInfo.getkeyCol(j));
				}
			} else {
			    tableMaintenanceElement = (TableMaintenanceElement)(attrList.get(0));
				sbSql1.append(tableMaintenanceElement.getColumn_name());
			}
		    sbSql1.append(") ct, ");
		    for (int i = 0; i < attrList.size(); i++) {
			    if (i > 0) sbSql1.append(", ");
		        tableMaintenanceElement = (TableMaintenanceElement)(attrList.get(i));
		        if (tableMaintenanceElement.getData_type().equals("DATE")) {
			        sbSql1.append("TO_CHAR (");
			        sbSql1.append(tableMaintenanceElement.getColumn_name());
					sbSql1.append(",'YY/MM/DD HH24:MI:SS') ");
			        sbSql1.append(tableMaintenanceElement.getColumn_name());
		        } else {
			        sbSql1.append(tableMaintenanceElement.getColumn_name());
		        }
		    }
		    sbSql1.append(" from ");
			sbSql1.append(selectTable);

			if ((tableMaintenanceForm.getWhereStr() != null) && (tableMaintenanceForm.getWhereStr().length() > 0)) {
			    sbSql1.append(" where " + tableMaintenanceForm.getWhereStr() );
			}
		    sbSql1.append(")");
		    sbSql1.append(" where ct >=");
		    sbSql1.append(tableMaintenanceForm.getFromRecNo());
		    sbSql1.append(" AND ct < ");
		    sbSql1.append(tableMaintenanceForm.getToRecNo());
			if (userKeyColInfo.getNoKeyCol() > 0) sbSql1.append(" order by ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
			    if (j != 0) sbSql1.append(", ");
				sbSql1.append(userKeyColInfo.getkeyCol(j));
			}
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			
			while(rs1.next()){
			    ArrayList<TableMaintenanceVal> recValue = new ArrayList<TableMaintenanceVal>();
			    TableMaintenanceRec tableMaintenanceRec = new TableMaintenanceRec();
			    for (int i = 0; i < attrList.size(); i++) {
			        TableMaintenanceVal tableMaintenanceVal = new TableMaintenanceVal();
			        tableMaintenanceElement = (TableMaintenanceElement)(attrList.get(i));
			        tableMaintenanceVal.val = rs1.getString(tableMaintenanceElement.getColumn_name());
			        recValue.add(tableMaintenanceVal);
			    }
			    tableMaintenanceRec.setRec_no(Integer.toString(rs1.getRow()));
		        tableMaintenanceRec.setValList(recValue);
		        tableValue.add(tableMaintenanceRec);
			}
			
			createPageList(tableMaintenanceForm);
			long pageNo = tableMaintenanceForm.getFromRecNo();
			tableMaintenanceForm.setSelectPage(Long.toString(pageNo));
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return tableValue;
	    
	}
	/**
	 * レコード数を取得する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private long getRecCount(String whereStr, String selectTable, User user, ActionMessages errors) throws IOException {
	    long recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select count(*) CNT ");
		    sbSql1.append("from ");
			sbSql1.append(selectTable);
			if ((whereStr != null) && (whereStr.length() > 0)) {
			    sbSql1.append(" where " + whereStr );
			}
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			
			if(rs1.next()){
			    recCount = rs1.getLong("CNT");
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return recCount;
	    
	}
	/**
	 * レコードがすでに登録されているかチェックする。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private int duplicateChk(int recno, TableMaintenanceForm tableMaintenanceForm, User user, ActionMessages errors) throws IOException {
	    int recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
        TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
 			// 新規レコードの重複チェック
			String selectedTable = tableMaintenanceForm.getSelectTable();

            StringBuffer sbSql1 = new StringBuffer("select count(*) CNT from ");
			sbSql1.append(selectedTable);

			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
			    tableMaintenanceForm.addErrorMsg("このテーブル[" + selectedTable + "]はキーが無いためメンテナンスできません。");
			    return recCount;
			}
			sbSql1.append(" where ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
			    if (j != 0) sbSql1.append("and ");
				sbSql1.append(userKeyColInfo.getkeyCol(j));
				sbSql1.append("='");
			    int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
				sbSql1.append(tableMaintenanceVal.getVal());
				sbSql1.append("' ");
			}
	
			stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			
			if (rs1.next()){
			    recCount = rs1.getInt("CNT");
			}

			if (recCount > 0) {
			    for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				    int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
					TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
		            tableMaintenanceVal.setDispStyle("color:#FF0000;");	// color:#FF0000
				}
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
	/**
	 * レコードがすでに登録されているかチェックする。例外が発生した場合、errorsにaddする。
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws 
	 */
	private void createPageList(TableMaintenanceForm tableMaintenanceForm) throws IOException {
	    ArrayList<String> pageList = new ArrayList<String>();
		ArrayList<String> pageNameList = new ArrayList<String>();
		String fromRec = "";
		String toRec = "";
		for (long i = 0; i < tableMaintenanceForm.getRecCount(); i = i + tableMaintenanceForm.getRecNoPerPage()) {
		    fromRec = Long.toString(i + 1);
		    toRec = Long.toString(Math.min(i + tableMaintenanceForm.getRecNoPerPage(),tableMaintenanceForm.getRecCount()));
		    pageNameList.add(fromRec + "～" + toRec);
		    pageList.add(Long.toString(i));
		}
		tableMaintenanceForm.setPageList(pageList);
		tableMaintenanceForm.setPageNameList(pageNameList);
	    
	}
	/**
	 * 指定したファイルテーブルをCSV形式で作成する。
	 * 作成に失敗した場合、errorsに書き出す。
	 * @param outFile 出力先のファイル
	 * @param searchResultForm
	 * @param allAttr 全属性ならtrue
	 * @param user
	 * @param errors
	 */
	private void createCsv(HttpServletRequest request, HttpServletResponse response, 
							String selectTable, User user, TableMaintenanceForm tableMaintenanceForm, ActionMessages errors){
		HttpSession session = request.getSession();
		ServletContext context = getServlet().getServletContext();
		String tempDirName = context.getRealPath("temp");// テンポラリのフォルダのフルパス
		String outFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
		File outFile = new File(outFileName);
		OutputStreamWriter out = null;
		try{
			// 出力ストリームの準備
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)),
											"Windows-31J");
			outputCsv(out, user, selectTable, tableMaintenanceForm, errors);
			try{ out.close(); } catch(Exception e){}
			//
			String streamFileName = selectTable + ".csv";// ヘッダにセットするファイル名
			response.setContentType("text/comma-separated-values");
			response.setHeader("Content-Disposition","attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"),"ISO8859_1"));
			//このままだと日本語が化ける
			//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			response.setContentLength((int)outFile.length());
	
			BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
			int c;
			while ((c = in.read()) != -1){
			    outStream.write(c);
			}
			in.close();
			outStream.flush();
			outStream.close();
			//
//			category.debug("outStream.close()");
			// CSVファイルを削除する
			if(outFile.delete()){
//				category.debug(outFileName + " を削除した");				
			}
			return;
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.csv", e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,"検索結果のファイル出力に失敗" + e.getMessage());

			// for MUR
//			category.error("検索結果のファイル出力に失敗\n" + ErrorUtility.error2String(e));
			
		} finally {
		}
		
	}
	/**
	 * 保持している検索結果を元に、CSVファイルを作成する。
	 * @param out
	 * @param allAttr 全属性なら true
	 * @throws IOException
	 */
	public void outputCsv(OutputStreamWriter out, User user, String selectTable, TableMaintenanceForm tableMaintenanceForm, ActionMessages errors) throws IOException{
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			TableMaintenanceElement tableMaintenanceElement = null;
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
			    tableMaintenanceForm.addErrorMsg("このテーブル[" + selectTable + "]はキーが無いためメンテナンスできません。");
			    return;
			}

			StringBuffer sbSql1 = new StringBuffer("select * from ");
			sbSql1.append(selectTable);

			if (userKeyColInfo.getNoKeyCol() > 0) sbSql1.append(" order by ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
			    if (j != 0) sbSql1.append(", ");
				sbSql1.append(userKeyColInfo.getkeyCol(j));
			}

			// ヘッダー部作成
			ArrayList<TableMaintenanceElement> attrList = tableMaintenanceForm.getAttrList();
		    for (int i = 0; i < attrList.size(); i++) {
		        tableMaintenanceElement = attrList.get(i);
				if (i > 0) out.write(',');
				out.write(tableMaintenanceElement.getColumn_name());
		    }
			out.write("\r\n");

		    stmt1 = conn.createStatement();
//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			String val = "";
			while(rs1.next()){
			    for (int i = 0; i < attrList.size(); i++) {
			        tableMaintenanceElement = attrList.get(i);
					if (i > 0) out.write(',');
					val = rs1.getString(tableMaintenanceElement.getColumn_name());
					if (val != null && val.length() != 0) out.write(val);
			    }
				out.write("\r\n");
			}
			
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
//			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return;
	    
	}
	/**
	 * 指定したファイルテーブルをCSV形式で作成する。
	 * 作成に失敗した場合、errorsに書き出す。
	 * @param outFile 出力先のファイル
	 * @param searchResultForm
	 * @param allAttr 全属性ならtrue
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private void uploadCsv(HttpServletRequest request, HttpServletResponse response, 
							String selectTable, User user, TableMaintenanceForm tableMaintenanceForm, ActionMessages errors) throws IOException{
	    FormFile fileUp = tableMaintenanceForm.getFileUp();
	    BufferedInputStream inBuffer = null;
	    BufferedOutputStream outBuffer = null;

	    InputStream is;
        try {
            is = fileUp.getInputStream();
    	    inBuffer = new BufferedInputStream(is);
    		ServletContext context = getServlet().getServletContext();
    		String tempDirName = context.getRealPath("temp");// テンポラリのフォルダのフルパス
    	    FileOutputStream fos = new FileOutputStream(tempDirName + File.separator + fileUp.getFileName());

    	    outBuffer = new BufferedOutputStream(fos);

    	    int contents = 0;
			while ((contents = inBuffer.read()) != -1) {
			    outBuffer.write(contents);
			}

			outBuffer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
		} finally {
    	    inBuffer.close();
    	    outBuffer.close(); 

    	    fileUp.destroy();
        }
		
	}
}
