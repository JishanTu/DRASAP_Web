package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * 通知票・図面の削除
 * @author Y.eto
 * 作成日: 2006/05/10
 * @version 2013/06/13 yamagishi
 */
public class DeleteDwgAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(DeleteDwgAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}
	private final int DELETE_FILE_SUCCESS = 0;
	private final int DELETE_FILE_NOT_FOUND = 1;
	private final int DELETE_FILE_COPY_FAILED = -1;

	// --------------------------------------------------------- Methods

	/**
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws SQLException
	 * @throws Exception
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,	HttpServletResponse response) throws SQLException {
		category.debug("start");
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		DeleteDwgForm deleteDwgForm = (DeleteDwgForm) form;
		//
		deleteDwgForm.setMsg1("");
		deleteDwgForm.setMsg2("");
		if ("init".equals(request.getParameter("task"))){
		    SearchResultForm searchResultForm = (SearchResultForm)session.getAttribute("searchResultForm");
		    deleteDwgForm = new DeleteDwgForm();
			deleteDwgForm.setAct("init");
		    session.removeAttribute("deleteDwgForm");
			session.setAttribute("deleteDwgForm", deleteDwgForm);
		    //if (deleteDwgForm == null) deleteDwgForm = new DeleteDwgForm();
			Connection conn = null;
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
			try{ conn.close(); } catch(Exception e) {}
			ArrayList<String> delKeys = new ArrayList<String>();
			for(int i = 0; i < searchResultForm.getSearchResultList().size(); i++){
			    SearchResultElement searchResultElement = searchResultForm.getSearchResultList().get(i);
			    if (searchResultElement.isSelected()) {
			    	delKeys.add(searchResultElement.getDrwgNo());
		        }
			}

			if (delKeys.size() == 0) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.warn.noSelect"));	// 削除する図面を選択してください
				saveErrors(request, errors);// エラーを登録
				deleteDwgForm.setDeleteOK(false);
			    session.removeAttribute("deleteDwgForm");
				session.setAttribute("deleteDwgForm", deleteDwgForm);
				return mapping.findForward("failed");
			}

			ArrayList<String> attrList = createColNameList(user, errors);
			ArrayList<DeleteDwgElement> recValue = createDwgValue(delKeys, attrList, deleteDwgForm, user, session, errors);
			deleteDwgForm.setColNameList(attrList);
			deleteDwgForm.setColNameJPList(createColNameJPList(user,attrList));
			deleteDwgForm.setRecList(recValue);
			if(errors.isEmpty()){

				deleteDwgForm.setMsg1("上記のデータを削除し保管領域へ移動します。削除したデータは元に戻せません。");
				deleteDwgForm.setMsg2("よろしければ削除ボタンで削除してください。");
				deleteDwgForm.setDeleteOK(true);

				session.removeAttribute("deleteDwgForm");
				session.setAttribute("deleteDwgForm", deleteDwgForm);
				return mapping.findForward("success");
			} else {
				saveErrors(request, errors);// エラーを登録
				deleteDwgForm.setDeleteOK(false);
			    session.removeAttribute("deleteDwgForm");
				session.setAttribute("deleteDwgForm", deleteDwgForm);
				return mapping.findForward("failed");
			}
		} else if("delete".equals(deleteDwgForm.getAct())){
			//
			// 図番削除
			//
		    deleteDwg(deleteDwgForm, user, errors, session);

		    session.setAttribute("deleteDwgForm", deleteDwgForm);
			if(errors.isEmpty()){
				deleteDwgForm.setDeleteOK(false);
				deleteDwgForm.setMsg1("上記のデータが削除されました。");
				deleteDwgForm.setMsg2("");
				return mapping.findForward("deleteComplete");
			} else {
				deleteDwgForm.setDeleteOK(false);
				saveErrors(request, errors);
				return  mapping.findForward("failed");
			}
		} else if("preview".equals(deleteDwgForm.getAct())){
//		    PreviewForm previewForm = createPreviewForm(deleteDwgForm, user);
//			session.setAttribute("previewForm", previewForm);
			return mapping.findForward("preview");
		} else if ("logout".equals(deleteDwgForm.getAct())){
		    session.removeAttribute("deleteDwgForm");
			return mapping.findForward("logout");
		} else if ("close".equals(deleteDwgForm.getAct())){
		    session.removeAttribute("deleteDwgForm");
			return null;
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
	private void deleteDwg(DeleteDwgForm deleteDwgForm, User user, ActionMessages errors, HttpSession session) {
		Connection conn = null;
		Statement stmt1 = null;
		StringBuffer sbSql1 = null;
	    String keyVal = null;

	    // 保存領域のチェック
// 2013.06.13 yamagishi modified. start
//		String beaHome = System.getenv("BEA_HOME");
//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
//    	String backupPathBase = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.delDwg.Backup.path");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
    	String backupPathBase = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.delDwg.Backup.path");
// 2013.06.13 yamagishi modified. end
		File DWG_Backup = new File(backupPathBase);
		if(! DWG_Backup.exists()){
			// 保管領域がない
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.BackupFolderNotFound", backupPathBase));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("保存領域[" + backupPathBase + "]がありません。");
		    return;
		}

		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション
 			// チェックしているものだけ削除
            for (int i = 0; i < deleteDwgForm.getRecList().size(); i++) {
                // キー値
                DeleteDwgElement deleteDwgElement = deleteDwgForm.getRecList(i);
                keyVal = deleteDwgElement.getDrwgNo().trim();


                // 当該図面を削除
                sbSql1 = new StringBuffer("delete INDEX_DB");
    			sbSql1.append(" where DRWG_NO='");
    			sbSql1.append(keyVal);
    			sbSql1.append("'");

    			category.debug(sbSql1.toString());

       			stmt1 = conn.createStatement();
    			int cnt = stmt1.executeUpdate(sbSql1.toString());
    			if (cnt == 0) {

    			}

    			updateLatestFlag(keyVal, deleteDwgElement.getDrwgType(), conn);
    			// FILE_DBの削除
    			deleteFileDb(deleteDwgElement, user, conn, errors);

    			// ファイルの削除
    			int delSts = deleteFile(keyVal, deleteDwgElement.linkDwgParmMap.get("PATH_NAME"), deleteDwgElement.linkDwgParmMap.get("FILE_NAME"), user, errors, session);
    			if (delSts == this.DELETE_FILE_COPY_FAILED) {
    				try {conn.rollback();} catch (Exception e2) {}
    			} else if (delSts == this.DELETE_FILE_NOT_FOUND){
    				try {conn.rollback();} catch (Exception e2) {}
//        			try{conn.commit();} catch(Exception e2){}
    			} else if (delSts == this.DELETE_FILE_SUCCESS){
        			try{conn.commit();} catch(Exception e2){}
        			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.success",keyVal));
        			category.info("図番["+ keyVal +"]を削除しました。");
    				AccessLoger.loging(user, AccessLoger.FID_DEL_DRWG, keyVal);
    			}

           }
		} catch(SQLException e){
			try{ conn.rollback(); } catch(Exception e2){}

			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.delIndexDB",keyVal, e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("INDEX_DBの削除に失敗\n" + ErrorUtility.error2String(e));
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
	private void deleteFileDb(DeleteDwgElement deleteDwgElement,
			User user, Connection conn, ActionMessages errors) {
		Statement stmt1 = null;
		StringBuffer sbSql1 = null;
		//
		sbSql1 = new StringBuffer("delete FILE_DB ");
		sbSql1.append(" where DRWG_NO='");
		sbSql1.append(deleteDwgElement.linkDwgParmMap.get("DRWG_NO"));
		sbSql1.append("'");

		try {
			stmt1 = conn.createStatement();
			stmt1.executeUpdate(sbSql1.toString());

		} catch (SQLException e) {
			try {conn.rollback();} catch (Exception e2) {}

			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.delFileDB", deleteDwgElement.linkDwgParmMap.get("DRWG_NO"), e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("FILE_DBの削除に失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try {stmt1.close();} catch (Exception e) {}
		}

	}
	/**
	 * 図面ファイルの削除を行う。
	 * @param drwgNo	図番
	 * @param pathName	ファイルパス
	 * @param fileName	ファイル名
	 * @param user		ユーザ
	 * @param errors	エラー
	 * @param session
	 * @return			=0 : success
	 * 					=1 : file not found
	 * 					=-1: error
	 */
	private int deleteFile(String drwgNo, String pathName, String fileName, User user, ActionMessages errors, HttpSession session) {

		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");

		String delPathName = drasapInfo.getViewDBDrive() + File.separator + pathName;
		String delFileName = delPathName + File.separator + fileName;
// 2013.06.13 yamagishi modified. start
//		String beaHome = System.getenv("BEA_HOME");
//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
// 2013.06.13 yamagishi modified. end
    	String backupPathBase = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.delDwg.Backup.path");


    	// 念のため元の原図があるか確認する '04.Mar.2 Hirata
		File moveFile = new File(delFileName);
		if(! moveFile.exists()){
			// 元の原図が存在しない
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.FileNotFound",drwgNo, delFileName));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("[" + delFileName + "]が存在しないか、またはアクセスできません。");
		    return this.DELETE_FILE_NOT_FOUND;
		}

		String ymd = (new SimpleDateFormat("yyyyMMdd")).format(new Date());// YYMMDD形式の本日日付
		String targetFolderName = backupPathBase + File.separator + ymd;
		File targetFolder = new File(targetFolderName);
		if (!targetFolder.exists()) targetFolder.mkdir();

		try {
			copyFile (moveFile.getPath(), targetFolder + File.separator + fileName);
		} catch (IOException e) {
			// ファイルの保存に失敗。
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.fileBackup",drwgNo, delFileName));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("[削除ファイル[" + delFileName + "]の保存に失敗しました。");
		    return this.DELETE_FILE_COPY_FAILED;
		} finally {
			moveFile.delete();
		}

    	category.debug(delFileName + " を移動しました");
	    return this.DELETE_FILE_SUCCESS;
	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<String> createColNameList(User user, ActionMessages errors) {
	    ArrayList<String> attrList = new ArrayList<String>();
	    Connection conn = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			TableInfo tableInfo = TableInfoDB.getTableInfoArray("INDEX_DB", conn);
			for (int i = 0; i < tableInfo.getNoCol(); i++) {
			    attrList.add(tableInfo.getColInfo(i).getColumn_name());
			}
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.delDwg.failed.colinfo",e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("テーブル情報の取得に失敗\n" + ErrorUtility.error2String(e));
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
	private ArrayList<String> createColNameJPList(User user, ArrayList<String> attrList) {
	    ArrayList<String> attrJPList = new ArrayList<String>();
		SearchUtil sUtil = new SearchUtil();
		for (int i = 0; i < attrList.size(); i++) {
		    attrJPList.add(sUtil.getSearchAttr(user.getLanguage(), attrList.get(i).toString(), false));
		}
	    return attrJPList;

	}
	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<DeleteDwgElement> createDwgValue(ArrayList<String> delKeys, ArrayList<String> attrList, DeleteDwgForm deleteDwgForm, User user, HttpSession session, ActionMessages errors) {
	    ArrayList<DeleteDwgElement> tableValue = new ArrayList<DeleteDwgElement>();
	    //ArrayList<String> fileIdList = new ArrayList<String>();
	    //ArrayList<String> mediaIdList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション

			StringBuffer sbSql1 = new StringBuffer("select ");
		    for (int i = 0; i < attrList.size(); i++) {
		        if (i > 0) sbSql1.append(",");
		        if (attrList.get(i).toString().equals("CREATE_DATE")) {
			        sbSql1.append("TO_CHAR(CREATE_DATE,'YY/MM/DD HH24:MI:SS') CREATE_DATE");
		        } else if (attrList.get(i).toString().equals("PROHIBIT_DATE")) {
			        sbSql1.append("TO_CHAR(PROHIBIT_DATE,'YY/MM/DD HH24:MI:SS') PROHIBIT_DATE");
		        } else if (attrList.get(i).toString().equals("ACL_UPDATE")) {
			        sbSql1.append("TO_CHAR(ACL_UPDATE,'YY/MM/DD HH24:MI:SS') ACL_UPDATE");
		        } else {
			        sbSql1.append(attrList.get(i).toString());
		        }
		    }
		    sbSql1.append(" from INDEX_DB");
		    sbSql1.append(" where DRWG_NO =ANY (");
		    for (int i = 0; i < delKeys.size(); i++) {
		    	if (i > 0) sbSql1.append(",");
			    sbSql1.append("'"+delKeys.get(i)+"'");
		    }
		    sbSql1.append(")");
			stmt1 = conn.createStatement();
			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			int rs1Count = 0; // 2013.06.26 yamagishi add.
			while(rs1.next()){
			    DeleteDwgElement deleteDwgElement = new DeleteDwgElement();
			    for (int i = 0; i < attrList.size(); i++) {
			        String value = rs1.getString(attrList.get(i));

			        deleteDwgElement.addValList(value);

			        if (attrList.get(i).toString().equals("DRWG_NO")) {
			        	deleteDwgElement.setDrwgNo(value);
			            //fileIdList.add(value);
			        }
			        if (attrList.get(i).toString().equals("DRWG_TYPE")) {
			        	deleteDwgElement.setDrwgType(value);
			            //mediaIdList.add(value);
			        }
			    }
//			    deleteDwgElement.linkDwgParmMap = createLinkDwgParmMap(rs1, user, errors);
			    deleteDwgElement.linkDwgParmMap = createLinkDwgParmMap(rs1, rs1Count, user, errors);
				tableValue.add(deleteDwgElement);

//		        deleteDwgForm.addFileList(createFileInfo(rs1, user, errors));
				rs1Count++; // 2013.06.26 yamagishi add.
			}

		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.lis." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return tableValue;

	}
// 2013.06.26 yamagishi modified. start
//	private HashMap<String, String> createLinkDwgParmMap(ResultSet rsRec, User user, ActionMessages errors) throws IOException {
	private HashMap<String, String> createLinkDwgParmMap(ResultSet rsRec, int rsCount, User user, ActionMessages errors) throws IOException {
// 2013.06.26 yamagishi modified. end
	    HashMap<String, String> linkDwgParmMap = new HashMap<String, String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// 非トランザクション
		    String fileId = rsRec.getString("DRWG_NO");
		    String mediaId = rsRec.getString("MEDIA_ID");
		    String fileName = null;
		    String pathName = null;

			StringBuffer sbSql1 = new StringBuffer("select FILE_NAME from ");
			sbSql1.append("FILE_DB");

		    sbSql1.append(" where DRWG_NO = '");
		    sbSql1.append(fileId);
		    sbSql1.append("'");

		    stmt1 = conn.createStatement();
			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			if (rs1.next()){
			    fileName = rs1.getString("FILE_NAME");
			}

			StringBuffer sbSql2 = new StringBuffer("select PATH_NAME from ");
			sbSql2.append("MEDIA_DB");

			sbSql2.append(" where TRIM(MEDIA_ID) = '");
		    sbSql2.append(mediaId.trim());
		    sbSql2.append("'");

			stmt2 = conn.createStatement();
			category.debug(sbSql2.toString());
			rs2 = stmt2.executeQuery(sbSql2.toString());
			if (rs2.next()){
			    pathName = rs2.getString("PATH_NAME");
			}
		    linkDwgParmMap.put("DRWG_NO", rsRec.getString("DRWG_NO"));// 図番
		    linkDwgParmMap.put("FILE_NAME", fileName);// ファイル名
		    linkDwgParmMap.put("PATH_NAME", pathName);// ディレクトリのフルパス
		    linkDwgParmMap.put("DRWG_SIZE", rsRec.getString("DRWG_SIZE"));// 図面サイズ
			// このユーザーが持つ、このアクセスレベルIDに対応するアクセスレベル値
			// null または 1 なら PDFに変換する
			String aclId = rsRec.getString("ACL_ID");// この図番のアクセスレベルID
// 2013.06.26 yamagishi modified. start
//			linkDwgParmMap.put("PDF",user.getViewPrintDoc(aclId));// PDF変換する?
			linkDwgParmMap.put("PDF", user.getViewPrintDoc(aclId, conn, (rsCount > 0)));// PDF変換する?
// 2013.06.26 yamagishi modified. end
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list." + user.getLanKey(),e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
			        DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("検索条件に一致した件数のカウントに失敗\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}
	    return linkDwgParmMap;

	}
	/**
	 * 最新の図番を検索し、最新図番区分をセット
	 *
	 * @param aclvChangeForm
	 * @param user
	 * @return 更新した件数。
	 */
	/**
	 * 最新の図番を検索し、最新図番区分をセット
	 *
	 * @param drwgNo
	 * @param drwgType
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private boolean updateLatestFlag(String drwgNo, String drwgType, Connection conn) throws SQLException {
		int cnt = 0;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt1 = null;// 使用禁止とアクセスレベルを変更
		try{
			StringBuffer sbSql1 = new StringBuffer();
			String drwgNo_head = drwgNo.substring(0, 10);	// 親図番
			int colLen = drwgNo.length();

			if (drwgType.equalsIgnoreCase("V") && (colLen == 14 || colLen == 15)) {			// メーカ承認図
				//
				// メーカ承認図 図番体系（図面種類："V"、桁数14 or 15）
				//
				//           親図番追番
				//            ↓  メーカー承認図追番：メーカー承認図のレビジョン
				//            ↓  ↓   メーカー承認図連番：メーカー承認図をユニークにするための番号（xx：2桁の番号）
				//   親図番         ↓  ↓   ↓
				// 9999999999 A n+a xx
				//

				String drwgNo_seq = drwgNo.substring(colLen - 2, colLen);	// メーカー承認図連番

				sbSql1.append("select DRWG_NO, LATEST_FLAG from INDEX_DB where NLS_UPPER(DRWG_TYPE) = 'V'");
				sbSql1.append(" and DRWG_NO like '" + drwgNo_head + "%'");
				sbSql1.append(" and LENGTH(DRWG_NO) = " + Integer.toString(colLen));
				sbSql1.append(" and substr(DRWG_NO,-2,2) = '" + drwgNo_seq + "'");
				sbSql1.append(" order by substr(DRWG_NO, 1,"+ Integer.toString(colLen - 2) + ") desc");

			} else if (!drwgType.equalsIgnoreCase("V") && (colLen == 11 || colLen == 12)) {	// 一般図
				//
				// 一般図番体系（図面種類："V"以外、桁数11 or 12）
				//
				//           追番（xx：1 or 2桁の番号）
				//   親図番         ↓
				// 9999999999 xx
				//

				sbSql1.append("select DRWG_NO, LATEST_FLAG from INDEX_DB where NLS_UPPER(DRWG_TYPE) != 'V'");
				sbSql1.append(" and DRWG_NO like '" + drwgNo_head + "%'");
				sbSql1.append(" and LENGTH(DRWG_NO) = " + Integer.toString(colLen));
				sbSql1.append(" order by DRWG_NO desc");
			} else if ((colLen == 14 || colLen == 15)) {	// 図面種別がメーカー承認図ではないがメーカー承認図と同じ体系のもの

				String drwgNo_seq = drwgNo.substring(colLen - 2, colLen);	//
				sbSql1.append("select DRWG_NO, LATEST_FLAG from INDEX_DB where NLS_UPPER(DRWG_TYPE) = 'V'");
				sbSql1.append(" and DRWG_NO like '" + drwgNo_head + "%'");
				sbSql1.append(" and LENGTH(DRWG_NO) = " + Integer.toString(colLen));
				sbSql1.append(" and substr(DRWG_NO,-2,2) = '" + drwgNo_seq + "'");
				sbSql1.append(" order by substr(DRWG_NO, 1,"+ Integer.toString(colLen - 2) + ") desc");
			} else if ((colLen != 11 && colLen != 12)) {	//

				sbSql1.append("select DRWG_NO, LATEST_FLAG from INDEX_DB where NLS_UPPER(DRWG_TYPE) != 'V'");
				sbSql1.append(" and DRWG_NO like '" + drwgNo_head + "%'");
				sbSql1.append(" and LENGTH(DRWG_NO) = " + Integer.toString(colLen));
				sbSql1.append(" order by DRWG_NO desc");
			} else {	// その他
				return true;
			}

			//
			String delDrwgNo = null;
			String latestFlag = null;
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if (rs1.next()){
				delDrwgNo = rs1.getString("DRWG_NO");
				latestFlag = rs1.getString("LATEST_FLAG");

				if (!latestFlag.equals("1")) {	// 最新図番区分がセットされていなかったらセットする
					pstmt1 = conn.prepareStatement("update INDEX_DB set LATEST_FLAG='1' where DRWG_NO=?");
					pstmt1.setString(1, delDrwgNo);//
					cnt += pstmt1.executeUpdate();
				}
			}

		} catch (SQLException e) {
			// rollback
			try{ conn.rollback(); } catch(Exception e2){}

			throw e;
		} finally {
			try{ rs1.close(); } catch (Exception e) {}
			try{ stmt1.close(); } catch (Exception e) {}
			try{ pstmt1.close(); } catch(Exception e) {}
		}
		//
		return true;
	}
	private int copyFile(String from, String to) throws FileNotFoundException, IOException {
		int data;
		// ファイル入力ストリームを取得
		BufferedInputStream fr = new BufferedInputStream(new FileInputStream(from));
		// ファイル出力ストリームを取得
		BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(to));
		while ((data = fr.read()) != -1) {
			fw.write(data);
		}
		fw.close();
		fr.close();
		return 1;
	}
}
