package tyk.drasap.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 検索結果をファイル出力する
 * @author fumi
 * 作成日: 2004/01/19
 */
@Controller
public class OutAttrCsvAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	// --------------------------------------------------------- Methods

	/**
	 * Method execute
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/outAttrCsv")
	public Object execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors) throws Exception {
		category.debug("start");
		// 準備段階
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		//		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}

		// テンポラリのフォルダのフルパス
		String tempDirName = DrasapUtil.getRealTempPath(request);
		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		String outAllFlug = (String) request.getAttribute("OUT_CSV_ALL");// 全属性か?
		// まずCSVファイルを作成する
		String outFileName = tempDirName + File.separator + session.getId() + "_" + new Date().getTime();
		File outFile = new File(outFileName);
		writeAttrCsv(outFile, searchResultForm,
				outAllFlug != null && "true".equals(outAllFlug), // nullでなく、"true"のときに
				user, errors);// CSV作成
		if (!Objects.isNull(errors.getAttribute("message"))) {
			// CSVファイル作成でエラーが発生していたら
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> error");
			return "error";

		}
		// CSVファイルダウンロード処理
		String streamFileName = "SeachResult.csv";// ヘッダにセットするファイル名
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
		//このままだと日本語が化ける
		//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
		response.setContentLength((int) outFile.length());

		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
		out.flush();
		out.close();
		//
		category.debug("out.close()");
		// CSVファイルを削除する
		if (outFile.delete()) {
			category.debug(outFileName + " を削除した");
		}
		return new ResponseEntity<>(HttpStatus.OK);
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
			User user, Model errors) {
		OutputStreamWriter out = null;
		try {
			// 出力ストリームの準備
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)), "Windows-31J");
			searchResultForm.writeAttrCsv(out, allAttr);
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.csv." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("検索結果のファイル出力に失敗\n" + ErrorUtility.error2String(e));

		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}

	}

}
