package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * 原図庫作業依頼リスト(原図庫担当者)画面から、印刷する画面に対応。
 */
public class RequestPriAction extends Action {
	private static DataSource ds;
	
	private static Category category = Category.getInstance(RequestPriAction.class.getName());
	private String classId = "";
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
				HttpServletResponse response)
				throws Exception {
					
		Request_listForm request_resultForm = (Request_listForm) form;
		category.debug("RequestPriAction スタート");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		try{
			// クラスID取得
			classId = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.drasap.genzu_irai.RequestPriAction");
		}catch(java.lang.Exception e){
				category.error("プロパティファイルの読込みに失敗しました\n" + ErrorUtility.error2String(e));
		}
		category.debug("action = " + request_resultForm.action);
		if("button_print".equals(request_resultForm.action)){
			
			java.sql.Connection conn = null;
			java.sql.ResultSet rs = null;							// リザルトセット
            java.sql.Statement stmt = null;
			request_resultForm.printList = new ArrayList();
			try{
				conn = ds.getConnection();
				stmt = conn.createStatement();	// データ存在チェック用コネクションステートメント
			  	String strSql = "select JOB_REQUEST_TABLE.JOB_ID,JOB_NAME,GOUKI_NO,CONTENT,USER_NAME,DEPT_NAME,EXPAND_DRWG_NO,JOB_REQUEST_TABLE.ROW_NO,SEQ_NO" +
			  					" from JOB_REQUEST_TABLE,JOB_REQUEST_EXPAND_TABLE" +
			  					// 結合条件にバグあり。'04.May.19修正 by Hirata
			  					" where JOB_REQUEST_TABLE.JOB_ID = JOB_REQUEST_EXPAND_TABLE.JOB_ID(+)" +
								" and JOB_REQUEST_TABLE.ROW_NO = JOB_REQUEST_EXPAND_TABLE.ROW_NO(+)" + // 結合条件に行番号必要
								" and JOB_REQUEST_EXPAND_TABLE.EXIST is null" + // 未着手のみを対象とする
			  					" order by JOB_ID, ROW_NO, SEQ_NO";
				category.debug("sql = " + strSql);				
				rs = stmt.executeQuery(strSql);
				while(rs.next()){
					String job_id = rs.getString("JOB_ID");//依頼ID
					String job_Name = rs.getString("JOB_NAME");//依頼内容
					String gouki = rs.getString("GOUKI_NO");//号口・号機
					String genzu = rs.getString("CONTENT");//原図内容
					String irai = rs.getString("USER_NAME");//依頼者
					String busyo = rs.getString("DEPT_NAME");//部署名
					String number = rs.getString("EXPAND_DRWG_NO");//番号
					if("図面登録依頼".equals(job_Name) || "図面出力指示".equals(job_Name)){
						// 整形するのは、図面登録依頼、図面出力指示のときのみ・・・変更 '04/07/22 by Hirata
						
						//番号が11桁の場合は「-」を付ける
						if(number != null){
							number = DrasapUtil.formatDrwgNo(number);	
						}
					}
	
					if("図面登録依頼".equals(job_Name) || "図面出力指示".equals(job_Name)){
						request_resultForm.printList.add(new RequestPriElement(job_id,job_Name,gouki,genzu,irai,busyo,number));
					}	
				}	
	
		  	}catch(Exception e){
				request_resultForm.listErrors.add("原図庫作業依頼リストの取得に失敗しました\n" + e.getMessage());
				//for システム管理者
				ErrorLoger.error(user, this,DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));	
			  	//errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(classId, e.getMessage()));
			  	category.error("[" + classId + "]:原図庫作業依頼リストの削除に失敗しました\n" + ErrorUtility.error2String(e));
				throw new Exception(e.getMessage());	
		  	}finally{
			  	try{
					if(stmt != null){
						stmt.close();stmt = null;
				  	}
			  	}catch(Exception e){}	
		  	}
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			String today = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime());// 本日
			request_resultForm.time = today;	
	
			//アクセスログを取得
			// 印刷用の表示では、アクセスログは残さない。 '04.May.17修正 by Hirata
			//AccessLoger.loging(user, AccessLoger.FID_GENZ_REQ);
			category.debug("--> printer");
			return(mapping.findForward("printer"));				
		}
		
					
		return(mapping.findForward("success"));						
	}

}
