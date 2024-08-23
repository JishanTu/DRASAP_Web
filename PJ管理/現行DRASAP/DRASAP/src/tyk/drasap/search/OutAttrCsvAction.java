package tyk.drasap.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.servlet.ServletContext;
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

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * 検索結果をファイル出力する
 * @author fumi
 * 作成日: 2004/01/19
 */
public class OutAttrCsvAction extends Action {
	private static Category category = Category.getInstance(OutAttrCsvAction.class.getName());
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
		HttpServletRequest request,HttpServletResponse response) throws Exception {
		category.debug("start");
		// 準備段階
		ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
//		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		ServletContext context = getServlet().getServletContext();
		String tempDirName = context.getRealPath("temp");// テンポラリのフォルダのフルパス
		// sessionタイムアウトの確認
		if(user == null){
			return mapping.findForward("timeout");
		}

		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		String outAllFlug = (String)request.getAttribute("OUT_CSV_ALL");// 全属性か?
		// まずCSVファイルを作成する
		String outFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
		File outFile = new File(outFileName);
		writeAttrCsv(outFile, searchResultForm,
					(outAllFlug!= null && outAllFlug.equals("true")), // nullでなく、"true"のときに
					 user, errors);// CSV作成
		if(!errors.isEmpty()){
			// CSVファイル作成でエラーが発生していたら
			saveErrors(request, errors);
			category.debug("--> error");
			return mapping.findForward("error");
			
		} else {
			String streamFileName = "SeachResult.csv";// ヘッダにセットするファイル名
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"),"ISO8859_1"));
			//このままだと日本語が化ける
			//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			response.setContentLength((int)outFile.length());
	
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
			int c;
			while ((c = in.read()) != -1){
				out.write(c);
			}
			in.close();
			out.flush();
			out.close();
			//
			category.debug("out.close()");
			// CSVファイルを削除する
			if(outFile.delete()){
				category.debug(outFileName + " を削除した");				
			}
			return null;
		}
	}
	/**
	 * 指定したファイルとして、検索結果をCSV形式で作成する。
	 * 作成に失敗した場合、errorsに書き出す。
	 * @param outFile 出力先のファイル
	 * @param searchResultForm
	 * @param allAttr 全属性ならtrue
	 * @param user
	 * @param errors
	 */
	private void writeAttrCsv(File outFile, SearchResultForm searchResultForm, boolean allAttr,
							User user, ActionMessages errors){
		OutputStreamWriter out = null;
		try{
			// 出力ストリームの準備
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)),
											"Windows-31J");
//											"UTF-8");
			searchResultForm.writeAttrCsv(out, allAttr);
			//
		} catch(Exception e){
			// for ユーザー
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.csv." + user.getLanKey(), e.getMessage()));
			// for システム管理者
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("検索結果のファイル出力に失敗\n" + ErrorUtility.error2String(e));
			
		} finally {
			try{ out.close(); } catch(Exception e){}
		}
		
	}
	

}
