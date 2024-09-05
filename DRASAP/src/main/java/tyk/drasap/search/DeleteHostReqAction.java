package tyk.drasap.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * 通知票・図面の削除
 * @author Y.eto
 * 作成日: 2006/05/10
 */
@Controller
@SessionAttributes("deleteHostReqForm")
public class DeleteHostReqAction extends BaseAction {
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
	@PostMapping("/delHostReq")
	public Object execute(
			@ModelAttribute("deleteHostReqForm") DeleteHostReqForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws SQLException {
		category.debug("start");
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		DeleteHostReqForm deleteHostReqForm = form;
		session.setAttribute("deleteHostReqForm", deleteHostReqForm);
		//
		deleteHostReqForm.cleartMsgList();

		//		Properties searchProp = SearchPropertiesFactory.getSearchProperties(this);
		ProfileString prop = null;
		try {
			prop = new ProfileString(this, "search.properties");
		} catch (FileNotFoundException e) {
			deleteHostReqForm.addErrMsgList("", MessageManager.getInstance().getMessage("search.delHost.resources.FileNotFound", "resources/search.properties"));
			deleteHostReqForm.setDeleteOK(false);
			return "failed";
		} catch (IOException e) {
			deleteHostReqForm.addErrMsgList("", MessageManager.getInstance().getMessage("search.delHost.resources.FileIOError", "resources/search.properties"));
			deleteHostReqForm.setDeleteOK(false);
			return "failed";
		}

		//if ("init".equals(request.getParameter("task"))) {
		if ("init".equals(request.getAttribute("task"))) {
			ArrayList<String> conditions = deleteHostReqForm.getCondition();
			conditions.clear();
			for (int i = 0; i < 10; i++) {
				conditions.add("");
			}
			deleteHostReqForm.setDeleteOK(true);
			deleteHostReqForm.setCondition(conditions);
			request.removeAttribute("task");
			return "success";
		}
		if ("delete".equals(deleteHostReqForm.getAct())) {
			//
			// 生産出図サーバへの接続
			//
			String serverName[] = new String[2];
			int connect[] = { 0, 0 };
			MoveFilePrp[] filePop = null;
			try {
				filePop = chkServerKind(deleteHostReqForm.getCondition()); // 依頼番号から接続するサーバを判断
			} catch (FileNotFoundException e1) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.delHostReq.xml.FileNotFound", new Object[] { "" }, null));
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				deleteHostReqForm.setDeleteOK(false);
				return "failed";
			} catch (InvalidPropertiesFormatException e1) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.delHostReq.xml.InvalidPropertiesFormat", new Object[] { e1.getMessage() }, null));
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				deleteHostReqForm.setDeleteOK(false);
				return "failed";
			} catch (IOException e1) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.delHostReq.xml.IOException", new Object[] { e1.getMessage() }, null));
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				deleteHostReqForm.setDeleteOK(false);
				return "failed";
			}

			for (int i = 0; i < filePop.length; i++) {
				if (filePop[i] != null) {
					serverName[i] = filePop[i].serverPath;
					// サーバ接続処理
					connect[i] = netConnect(filePop[i], deleteHostReqForm, user, errors);
					if (connect[i] == -1) {
						//saveErrors(request, errors);
						request.setAttribute("errors", errors);
						deleteHostReqForm.setDeleteOK(false);
						return "failed";
					}

					// フォルダ存在チェック
					try {
						String seachKind = deleteHostReqForm.getSeachKind();
						for (int idx = 0; idx < filePop[i].getMoveFilePath(seachKind).size(); idx++) {
							folderChk(serverName[i] + filePop[i].getMoveFilePath(seachKind).get(idx), user, errors);
						}
						for (int idx = 0; idx < filePop[i].getMoveFolderPath(seachKind).size(); idx++) {
							folderChk(serverName[i] + filePop[i].getMoveFolderPath(seachKind).get(idx), user, errors);
						}
					} catch (FileNotFoundException e) {
						//saveErrors(request, errors);
						request.setAttribute("errors", errors);
						deleteHostReqForm.setDeleteOK(true);
						return "failed";
					}
				}
			}

			//
			// 依頼削除処理
			//
			try {
				deleteProc(deleteHostReqForm, filePop, prop, user, errors);
			} catch (Exception e) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				deleteHostReqForm.setDeleteOK(false);
				return "failed";
			}

			//
			// サーバ切断処理
			//
			// 刈谷生産出図サーバを切断
			if (connect[0] == 1) {
				netDisConnect(serverName[0]);
			}
			// 東刈谷生産出図サーバを切断
			if (connect[1] == 1) {
				netDisConnect(serverName[1]);
			}

			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			deleteHostReqForm.setDeleteOK(true);
			return "deleteComplete";
		}
		if ("close".equals(deleteHostReqForm.getAct())) {
			session.removeAttribute("deleteHostReqForm");
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return "success";
	}

	/**
	 * HOST生産出図依頼削除
	 * @param deleteHostReqForm
	 * @param honshaServerName
	 * @param higashiServerName
	 * @param user
	 * @param errors
	 * @throws FileNotFoundException
	 */
	private void deleteProc(DeleteHostReqForm deleteHostReqForm, MoveFilePrp[] moveFilePop, ProfileString prop, User user, Model errors) {
		int cnt = 0;
		ArrayList<String> conditions = deleteHostReqForm.getCondition();
		for (int i = 0; i < conditions.size(); i++) {
			String condition = conditions.get(i);
			if (condition == null || condition.length() == 0) {
				continue;
			}
			try {
				cnt = 0;
				if ("delSeisan".equals(deleteHostReqForm.getSeachKind())) {
					// DBの削除
					cnt = deleteIraiDb(condition, user, errors);
				}

				MoveFilePrp filePop = null;
				if ("A".equals(condition.substring(8, 9)) ||
						"B".equals(condition.substring(8, 9))) { // 刈谷
					filePop = moveFilePop[0];
				} else if ("C".equals(condition.substring(8, 9)) ||
						"D".equals(condition.substring(8, 9))) { // 東刈谷
					filePop = moveFilePop[1];
				}
				if (filePop == null) {
					continue;
				}
				// ファイルのコピー
				cnt += copySeisan(deleteHostReqForm.getSeachKind(), filePop, condition);
				// ファイルの削除
				deleteSeisan(deleteHostReqForm.getSeachKind(), filePop, condition);

				if (cnt > 0) {
					deleteHostReqForm.addMsgList(condition, prop.getValue("search.delHost.success", condition));
					category.debug("依頼[" + condition + "]を削除しました。");
				} else {
					if (deleteHostReqForm.getMsg(condition) == null) {
						deleteHostReqForm.addErrMsgList(condition, prop.getValue("search.delHost.target.notFound", condition));
					}
					category.debug("依頼[" + condition + "]は見つかりませんでした。");
				}
			} catch (SQLException e) {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.failed.delSeisanDB", new Object[] { condition, e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
				// for MUR
				category.error("SEISAN_SHUTSUZU_TABLEの削除に失敗\n" + ErrorUtility.error2String(e));
			} catch (FileDeleteFailedException e) {
				deleteHostReqForm.addErrMsgList(condition, prop.getValue("search.delHost.failed", condition, "ファイルの削除に失敗", e.getMessage()));
			} catch (FileNotFoundException e) {
				deleteHostReqForm.addErrMsgList(condition, prop.getValue("search.delHost.failed", condition, "ファイルが見つからない", e.getMessage()));
			} catch (IOException e) {
				deleteHostReqForm.addErrMsgList(condition, prop.getValue("search.delHost.failed", condition, "ファイルI/Oエラー", e.getMessage()));
			}

		}

	}

	/**
	 * 検索表示項目を検索する。例外が発生した場合、errorsにaddする。
	 * @param userGroupCode ユーザーグループコード
	 * @param user
	 * @param errors
	 * @throws SQLException
	 * @throws IOException
	 * @throws
	 */
	private int deleteIraiDb(String condition, User user, Model errors) throws SQLException {
		int cnt = 0;
		Connection conn = null;
		Statement stmt1 = null;
		StringBuilder sbSql1 = null;

		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// 非トランザクション

			// 当該依頼を削除
			sbSql1 = new StringBuilder("delete SEISAN_SHUTSUZU_TABLE");
			sbSql1.append(" where HOST_IRAI_ID='");
			sbSql1.append(condition);
			sbSql1.append("'");

			category.debug(sbSql1.toString());

			stmt1 = conn.createStatement();
			cnt = stmt1.executeUpdate(sbSql1.toString());
			if (cnt == 0) {

			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			throw new SQLException(e.getMessage());
		} finally {
			try {
				conn.commit();
			} catch (Exception e2) {
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return cnt;
	}

	private int copySeisan(String SeachKind, MoveFilePrp filePop, String condition) throws FileDeleteFailedException, FileNotFoundException, IOException {
		int cnt = 0;
		cnt = copyDelFile(SeachKind, filePop, condition);
		cnt += copyDelFolder(SeachKind, filePop, condition);

		return cnt;
	}

	private void deleteSeisan(String SeachKind, MoveFilePrp filePop, String condition) throws FileDeleteFailedException, FileNotFoundException, IOException {
		deleteDelFile(SeachKind, filePop, condition);
		deleteDelFolder(SeachKind, filePop, condition);

	}

	private int copyDelFile(String seachKind, MoveFilePrp filePop, String condition) throws FileNotFoundException, IOException {
		int cnt = 0;
		for (int i = 0; i < filePop.getMoveFilePath(seachKind).size(); i++) {
			File[] fromFiles = new File(filePop.serverPath + filePop.getMoveFilePath(seachKind).get(i)).listFiles(new moveFileFilter(condition));
			File toDir = new File(filePop.serverPath + filePop.getDestFolder(seachKind) + File.separator + condition);
			for (int idx = 0; idx < fromFiles.length; idx++) {
				if (!toDir.exists() || !toDir.isDirectory()) {
					toDir.mkdir();
				}
				cnt += copyFile(fromFiles[idx].getPath(), toDir.getPath() + File.separator + fromFiles[idx].getName());
			}
		}
		return cnt == 0 ? 0 : 1;
	}

	private int copyDelFolder(String SeachKind, MoveFilePrp filePop, String condition) throws FileDeleteFailedException, FileNotFoundException, IOException {
		int cnt = 0;
		for (int i = 0; i < filePop.getMoveFolderPath(SeachKind).size(); i++) {
			File folder = new File(filePop.serverPath + filePop.getMoveFolderPath(SeachKind).get(i) + File.separator + condition);
			if (folder.exists()) {
				cnt += copyFolder(folder.getPath(), filePop.serverPath + filePop.getDestFolder(SeachKind) + File.separator + condition);
			}
		}
		return cnt;
	}

	private boolean deleteDelFile(String seachKind, MoveFilePrp filePop, String condition) throws FileNotFoundException, IOException {
		for (int i = 0; i < filePop.getMoveFilePath(seachKind).size(); i++) {
			File[] fromFiles = new File(filePop.serverPath + filePop.getMoveFilePath(seachKind).get(i)).listFiles(new moveFileFilter(condition));
			for (int idx = 0; idx < fromFiles.length; idx++) {
				deleteFile(fromFiles[idx].getPath());
			}
		}
		return true;
	}

	private int deleteDelFolder(String seachKind, MoveFilePrp filePop, String condition) throws FileNotFoundException, IOException {
		int cnt = 0;
		for (int i = 0; i < filePop.getMoveFolderPath(seachKind).size(); i++) {
			File folder = new File(filePop.serverPath + filePop.getMoveFolderPath(seachKind).get(i) + File.separator + condition);
			if (folder.exists()) {
				if (!deleteFolder(folder.getPath())) {
					throw new FileDeleteFailedException(folder.getPath());
				}
			}
		}
		return cnt;
	}

	/**
	 * 生産出図サーバへの接続を行う
	 *
	 * @param serverName
	 * @param deleteHostReqForm
	 * @param user
	 * @param errors
	 * @return
	 * 			= 1:新規に接続した
	 * 			= 0:すでに接続していた
	 * 			= -1:エラー発生
	 */
	private int netConnect(MoveFilePrp filePop, DeleteHostReqForm deleteHostReqForm, User user, Model errors) {
		int connectSts = -1;
		int exitCode;
		Process netUseChk;
		Process netConnect;

		try {
			netUseChk = Runtime.getRuntime().exec("NET USE");
			BufferedReader br = new BufferedReader(new InputStreamReader(netUseChk.getInputStream()));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (str.toUpperCase().indexOf(filePop.serverPath.toUpperCase()) >= 0) {
					connectSts = 0; // すでに接続されている
				}
			}
			exitCode = netUseChk.waitFor();
			if (exitCode != 0) {
			}
			br.close();
			netUseChk.destroy();

			if (connectSts != 0) { // 接続されていない
				// 接続
				netConnect = Runtime.getRuntime().exec("NET USE " + filePop.serverPath + " /USER:" + filePop.user + " " + filePop.passwd);
				br = new BufferedReader(new InputStreamReader(netConnect.getInputStream()));
				str = null;
				while ((str = br.readLine()) != null) {
					str = str.toLowerCase();
				}
				exitCode = netConnect.waitFor();
				if (exitCode == 0) {
					connectSts = 1; // 新規に接続した
				} else {
					// for ユーザー
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.failed.connect", new Object[] { filePop.serverPath }, null));
					// for システム管理者
					ErrorLoger.error(user, this,
							DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
					// for MUR
					category.error("生産出図サーバ[" + filePop.serverPath + "]への接続に失敗しました。\n");
				}
				br.close();
				netConnect.destroy();

			}
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.failed.connect", new Object[] { filePop.serverPath }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("生産出図サーバ[" + filePop.serverPath + "]への接続に失敗しました。\n" + ErrorUtility.error2String(e));
		} catch (InterruptedException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.failed.connect", new Object[] { filePop.serverPath }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("生産出図サーバ[" + filePop.serverPath + "]への接続に失敗しました。\n" + ErrorUtility.error2String(e));
		}
		return connectSts;
	}

	private void netDisConnect(String serverName) {
		Process netDisConnect;
		// 切断
		try {
			netDisConnect = Runtime.getRuntime().exec("NET USE " + serverName + " /DELETE");
			BufferedReader br = new BufferedReader(new InputStreamReader(netDisConnect.getInputStream()));
			String str = null;
			while ((str = br.readLine()) != null) {
				str = str.toLowerCase();
			}
			netDisConnect.waitFor();
			br.close();
			netDisConnect.destroy();
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 依頼番号より対象サーバを取得
	 * @param condition
	 * @return
	 * 			[0]=true:刈谷生産用サーバ接続
	 * 			[1]=true:東刈谷生産用サーバ接続
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidPropertiesFormatException
	 */
	private MoveFilePrp[] chkServerKind(ArrayList<String> condition) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		MoveFilePrp[] filePop = { null, null };
		for (int i = 0; i < condition.size(); i++) {
			if (condition.get(i) == null || condition.get(i).length() == 0) {
				continue;
			}
			if ("A".equals(condition.get(i).substring(8, 9))) {
				if (filePop[0] == null) {
					filePop[0] = getFilePop("honsha");
				}
			}
			if ("C".equals(condition.get(i).substring(8, 9))) {
				if (filePop[1] == null) {
					filePop[1] = getFilePop("higashi");
				}
			}
			if ("B".equals(condition.get(i).substring(8, 9))) {
				if (filePop[0] == null) {
					filePop[0] = getFilePop("honsha");
				}
			}
			if ("D".equals(condition.get(i).substring(8, 9))) {
				if (filePop[1] == null) {
					filePop[1] = getFilePop("higashi");
				}
			}
		}
		return filePop;
	}

	/**
	 * フォルダの存在チェック
	 * @param dir
	 * @param user
	 * @param errors
	 * @return
	 * @throws FileNotFoundException
	 */
	private boolean folderChk(String dir, User user, Model errors) throws FileNotFoundException {
		if (!new File(dir).exists()) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.dir.notFound", new Object[] { dir }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("フォルダ[" + dir + "]が見つかりません。\n");

			throw new FileNotFoundException(dir + " not found.");
		}
		return true;
	}

	private MoveFilePrp getFilePop(String node) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		MoveFilePrp moveFilePrp = new MoveFilePrp();
		Properties delhostPop = new Properties();
		// ファイルからInputStreamを作成
		String delHostReqXml = DrasapPropertiesFactory.getFullPath("tyk.xmldef.delHostReq.xml.path");
		InputStream is = Files.newInputStream(Paths.get(delHostReqXml));
		if (is == null) {
			throw new FileNotFoundException("delHostReq.xml");
		}
		delhostPop.loadFromXML(is);
		moveFilePrp.serverPath = delhostPop.getProperty("tyk.delHostReqPs." + node + ".seisan.server");
		moveFilePrp.user = delhostPop.getProperty("tyk.delHostReqPs." + node + ".seisan.server.user");
		moveFilePrp.passwd = delhostPop.getProperty("tyk.delHostReqPs." + node + ".seisan.server.pass");
		moveFilePrp.delSeisanDestFolder = delhostPop.getProperty("tyk.delHostReqPs." + node + ".delSeisan.destFolder.path");
		moveFilePrp.delPrtDestFolder = delhostPop.getProperty("tyk.delHostReqPs." + node + ".delPrt.destFolder.path");
		Enumeration<?> entryList = delhostPop.propertyNames();

		while (entryList.hasMoreElements()) {
			String key = (String) entryList.nextElement();
			if (key.indexOf("tyk.delHostReqPs." + node + ".delSeisan.moveFile.path") == 0) {
				moveFilePrp.delSeisanMoveFilePath.add(delhostPop.getProperty(key));
			} else if (key.indexOf("tyk.delHostReqPs." + node + ".delSeisan.moveFolder.path") == 0) {
				moveFilePrp.delSeisanMoveFolderPath.add(delhostPop.getProperty(key));
			} else if (key.indexOf("tyk.delHostReqPs." + node + ".delPrt.moveFile.path") == 0) {
				moveFilePrp.delPrtMoveFilePath.add(delhostPop.getProperty(key));
			} else if (key.indexOf("tyk.delHostReqPs." + node + ".delPrt.moveFolder.path") == 0) {
				moveFilePrp.delPrtMoveFolderPath.add(delhostPop.getProperty(key));
			}
		}
		is.close();
		return moveFilePrp;
	}

	private int copyFolder(String fromFolder, String toFolder) throws FileDeleteFailedException, FileNotFoundException, IOException {
		int cnt = 0;

		File fromDir = new File(fromFolder);
		File toDir = new File(toFolder);
		if (fromDir.exists() && fromDir.isDirectory()) {
			if (!toDir.exists() || !toDir.isDirectory()) {
				toDir.mkdir();
			}

			File[] Files = fromDir.listFiles();
			for (int i = 0; i < Files.length; i++) {
				if (Files[i].isFile()) {
					cnt += copyFile(Files[i].getPath(), toDir.getPath() + File.separator + Files[i].getName());
				} else if (Files[i].isDirectory()) {
					cnt += copyFolder(Files[i].getPath(), toFolder + File.separator + Files[i].getName());
				}
			}
		}
		return cnt;
	}

	private int copyFile(String from, String to) throws FileDeleteFailedException, FileNotFoundException, IOException {
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

	private boolean deleteFolder(String folderName) throws FileNotFoundException {
		File folder = new File(folderName);
		if (folder.exists() && folder.isDirectory()) {
			File[] Files = folder.listFiles();
			for (int i = 0; i < Files.length; i++) {
				if (Files[i].isFile()) {
					if (!deleteFile(Files[i].getPath())) {
						return false;
					}
				} else if (Files[i].isDirectory()) {
					if (!deleteFolder(Files[i].getPath())) {
						return false;
					}
				}
			}
		}

		return folder.delete();
	}

	private boolean deleteFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("FileNotFound");
		}

		if (!file.canWrite()) {
			throw new FileNotFoundException("fileCanNotWrite");
		}
		return file.delete();
	}

	private class MoveFilePrp {
		String serverPath = "";
		String user = "";
		String passwd = "";
		private ArrayList<String> delSeisanMoveFolderPath = new ArrayList<String>();
		private ArrayList<String> delSeisanMoveFilePath = new ArrayList<String>();
		private String delSeisanDestFolder = "";
		private ArrayList<String> delPrtMoveFolderPath = new ArrayList<String>();
		private ArrayList<String> delPrtMoveFilePath = new ArrayList<String>();
		private String delPrtDestFolder = "";

		public ArrayList<String> getMoveFilePath(String seachKind) {
			if ("delSeisan".equals(seachKind)) {
				return delSeisanMoveFilePath;
			}
			return delPrtMoveFilePath;
		}

		public ArrayList<String> getMoveFolderPath(String seachKind) {
			if ("delSeisan".equals(seachKind)) {
				return delSeisanMoveFolderPath;
			}
			return delPrtMoveFolderPath;
		}

		public String getDestFolder(String seachKind) {
			if ("delSeisan".equals(seachKind)) {
				return delSeisanDestFolder;
			}
			return delPrtDestFolder;
		}
	}

	private class moveFileFilter implements FilenameFilter {
		String conditionName = "";

		public moveFileFilter(String conditionName) {
			super();
			this.conditionName = conditionName;
		}

		@Override
		public boolean accept(File dir, String name) {
			return new File(dir.getPath() + File.separator + name).isFile() && name.startsWith(conditionName);
		}
	}

	//	private class moveFolderFilter implements FileFilter {
	//
	//		public boolean accept(File pathname) {
	//			return pathname.isDirectory();
	//		}
	//	}
	private class FileDeleteFailedException extends FileNotFoundException {
		private static final long serialVersionUID = 1L;

		//		public FileDeleteFailedException() {
		//			super();
		//		}

		public FileDeleteFailedException(String s) {
			super(s);
		}

	}
}
