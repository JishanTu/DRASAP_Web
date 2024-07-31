package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.filechooser.FileSystemView;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * アクセスレベル更新結果処理アクション
 *
 * @author 2013/07/23 yamagishi
 */
@Controller
@SessionAttributes("accessLevelUpdatedResultForm")
public class AccessLevelUpdatedResultAction extends BaseAction {
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
	@PostMapping("/accessLevelUpdatedResult")
	public Object execute(
			@ModelAttribute("accessLevelUpdatedResultForm") AccessLevelUpdatedResultForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {

		if (category.isDebugEnabled()) {
			category.debug("start");
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}

		//ActionMessages errors = new ActionMessages();
		//MessageResources resources = getResources(request);

		// アクセスレベル一括更新ツールの使用権限なしの場合
		if (user.getAclBatchUpdateFlag() == null || user.getAclBatchUpdateFlag().length() <= 0) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "アクセスレベル更新結果画面" }, null));
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "アクセスレベル更新結果画面" }, null));
			}
			return "noPermission";
		}

		AccessLevelUpdatedResultForm accessLevelUpdatedResultForm = form;
		if ("init".equals(request.getParameter("act"))) {
			accessLevelUpdatedResultForm.setAct("init");
		} else if ("download".equals(request.getParameter("act"))) {
			accessLevelUpdatedResultForm.setAct("download");
		}
		accessLevelUpdatedResultForm.clearErrorMsg();
		String fileName = request.getParameter("FILE_NAME");

		//
		if ("init".equals(accessLevelUpdatedResultForm.getAct())) {
			setFormLinkAclLogData(accessLevelUpdatedResultForm);
			// エラー確認
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
			session.removeAttribute("accessLevelUpdatedResultForm");
			session.setAttribute("accessLevelUpdatedResultForm", accessLevelUpdatedResultForm);
			return "init";
		}
		if ("download".equals(accessLevelUpdatedResultForm.getAct())) {
			doDownload(response, fileName, user, errors);
			// エラー確認
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}

		if (category.isDebugEnabled()) {
			category.debug("end");
		}
		return "init";
	}

	/**
	 * 画面表示用にアクセスレベル更新結果ログを取得し、AccessLevelUpdatedResultFormにセットする。
	 * @param accessLevelUpdatedResultForm
	 */
	private void setFormLinkAclLogData(AccessLevelUpdatedResultForm accessLevelUpdatedResultForm) {

		if (category.isDebugEnabled()) {
			category.debug("アクセスレベル更新結果取得処理の開始");
		}

		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);
		}
		String filePath = apServerHome + drasapProperties.getProperty("tyk.result.updated.log.path");
		String[] paths = filePath.split("\\" + File.separator);
		String fileName = paths[paths.length - 1];
		String fileNameWithNoExtension = fileName.split("\\.")[0];
		String fileExtension = fileName.split("\\.")[1];
		String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator + fileName));

		ArrayList<AccessLevelUpdatedResultElement> accessLevelUpdatedResultList = new ArrayList<AccessLevelUpdatedResultElement>();
		AccessLevelUpdatedResultElement resultElement = null;
		HashMap<String, String> linkParmMap = null;

		HashMap<String, Long> sortMap = new HashMap<String, Long>(); // ソート用

		// ログ出力フォルダのファイル一覧を取得
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			if (file.isFile()
					&& file.getName().startsWith(fileNameWithNoExtension) && file.getName().endsWith(fileExtension)) {
				// ファイル名、更新日時
				sortMap.put(file.getName(), file.lastModified());
			}
		}
		// 更新日時の降順でソート
		ArrayList<Entry<String, Long>> sortedFiles = new ArrayList<Entry<String, Long>>(sortMap.entrySet());
		Collections.sort(sortedFiles, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});

		FileSystemView view = FileSystemView.getFileSystemView();
		File file = null;
		for (Entry<String, Long> sortedFile : sortedFiles) {
			file = new File(folderPath + File.separator + sortedFile.getKey());

			resultElement = new AccessLevelUpdatedResultElement(
					file.getName(), view.getSystemTypeDescription(file), new Date(file.lastModified()));

			linkParmMap = resultElement.getLinkParmMap();
			linkParmMap.put("FILE_NAME", file.getName()); // ファイル名
			linkParmMap.put("act", "download");

			accessLevelUpdatedResultList.add(resultElement);
		}
		accessLevelUpdatedResultForm.setAccessLevelUpdatedResultList(accessLevelUpdatedResultList);
		accessLevelUpdatedResultForm.setFileCount(accessLevelUpdatedResultList.size()); // ACLログファイル件数

		if (category.isDebugEnabled()) {
			category.debug("アクセスレベル更新結果取得処理の終了");
		}
	}

	/**
	 * ダウンロードを実行する。
	 * @param response
	 * @param fileName
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doDownload(HttpServletResponse response, String fileName, User user,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("アクセスレベル更新結果ダウンロード実行処理の開始");
		}

		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);
		}

		// ファイル名を確認
		if (fileName == null || fileName.length() <= 0) {
			// for ユーザー
			//ActionMessage error = messageSource.getMessage("system.aclUpdatedResult.download.failed", ("ファイル名 " + fileName));
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "ファイル名 " + fileName }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "ファイル名 " + fileName }, null));
			}
			return;
		}

		String filePath = apServerHome + drasapProperties.getProperty("tyk.result.updated.log.path");
		String[] paths = filePath.split("\\" + File.separator);
		String orglFileName = paths[paths.length - 1];
		String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator + orglFileName));
		filePath = folderPath + File.separator + fileName;

		// ACL更新結果ログファイルがあるか確認する
		if (!new File(filePath).exists()) {
			// for ユーザー
			//ActionMessage error = messageSource.getMessage("system.aclUpdatedResult.download.failed", ("ファイルパス " + filePath));
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "ファイルパス " + filePath }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "ファイルパス " + filePath }, null));
			}
			return;
		}

		// ストリームに流す
		File f = new File(filePath);
		String streamFileName = fileName;// ヘッダにセットするファイル名

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			response.setContentType("application/octet-stream");
			// 文字化け対応
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			response.setContentLength((int) f.length());

			in = new BufferedInputStream(new FileInputStream(f));
			out = new BufferedOutputStream(response.getOutputStream());
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			out.flush();

		} catch (Exception e) {
			try {
				response.reset();
			} catch (Exception e2) {
			}
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			// CLOSE処理
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
			}
			try {
				if (out != null) {
					out.close();
				}
				if (category.isDebugEnabled()) {
					category.debug("out.close()");
				}
			} catch (Exception e) {
			}
		}
		if (category.isDebugEnabled()) {
			category.debug("アクセスレベル更新結果ダウンロード実行処理の終了");
		}
	}
}
