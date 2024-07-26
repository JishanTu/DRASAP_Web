package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.pdflib.PDFlibException;
import com.pdflib.pdflib;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;
import tyk.drasap.viewlog.ViewLoger;

/**
 * <PRE>
 * 図面をプレビューするAction。
 * 入力パラメーターは requsetパラメーター で取得。
 * DRWG_NO		図番
 * FILE_NAME	ファイル名
 * PATH_NAME	ディレクトリのフルパス
 * DRWG_SIZE	図番サイズ
 * PDF			PDFに変換する?
 * SYS_ID		システムID
 * '04.Nov.23変更 図番をロギングするように
 * 2005-Mar-4変更 他システムからの呼び出しではシステムIDをロギングする。
 * 2008/6/30 変更 Sun-Win2003移植に伴いバナー、間引き処理変更
 * </PRE>
 * 最終変更 $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 *
 * @version 2013/06/14 yamagishi
 */
@Controller
public class PreviewAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	protected String lock = "";

	/** 図面検索画面のact属性 MULTI_PDF */
	private String ACT_MULTI_PDF = "MULTI_PDF";

	/** 図面検索画面のact属性 PDF_ZIP */
	private String ACT_PDF_ZIP = "PDF_ZIP";

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
	@PostMapping("/preview")
	public Object execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// 準備段階
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		// テンポラリのフォルダのフルパス
		String tempDirName = DrasapUtil.getRealTempPath(request);
		// 2019.10.17 yamamoto add start.
		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		//		session.removeAttribute("searchResultForm");// sessionから削除
		// 2019.10.17 yamamoto add end.

		// DLマネージャからのリクエスト情報		// 2013.08.02 yamagishi add. start
		DLManagerInfo dlmInfo = null;
		if ("1".equals(request.getParameter("DLM_REQ"))) {
			dlmInfo = new DLManagerInfo(request.getParameter("act"), messageSource);
		} // end

		// sessionタイムアウトの確認
		if (user == null) {
			if (dlmInfo != null) { // 2013.08.01 yamagishi add. start
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "無効なログインです。もう一度ログインを行ってください。");
				return null;
			} // end
			return "timeout";
		}
		if (dlmInfo != null && dlmInfo.isActError()) { // 2013.08.02 yamagishi add. start
			// DLマネージャでエラーが発生している場合
			doErrorLog(user, dlmInfo);
			return null;
		} // end

		// MultiTIFF出力
		if (searchResultForm != null) {
			if (ACT_MULTI_PDF.equals(searchResultForm.getAct())
					|| ACT_PDF_ZIP.equals(searchResultForm.getAct())) {

				// act属性を保持
				String act = searchResultForm.getAct();

				searchResultForm.setAct("");// act属性をクリア
				session.setAttribute("searchResultForm", searchResultForm);

				// MultiTIFF作成後にPDF変換を行う
				doMultiPDF(searchResultForm, drasapInfo, user, response, errors, request, act);

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// エラーが発生
					//saveErrors(request, errors);
					request.setAttribute("errors", errors);
					category.error("==> doMultiPDF error");
					return "error";
				}

				return null;
			}
		}

		// リクエストパラメータから取得する
		String drwgNo = request.getParameter("DRWG_NO");// 図番
		String fileName = request.getParameter("FILE_NAME");// ファイル名
		String pathName = request.getParameter("PATH_NAME");// ディレクトリのフルパス
		pathName = drasapInfo.getViewDBDrive() + pathName.replace("/", "\\");
		String drwgSize = request.getParameter("DRWG_SIZE");// 図番サイズ
		String pdfFlug = request.getParameter("PDF");// PDFに変換する?

		// 他システムからの呼び出しに対応するため、パラメータを追加
		// DRASAP内部からの呼び出しでは、この値は null になる。
		// 2005-Mar-4 by Hirata.
		String sysId = user.getSys_id();// システムID

		final String ORIGIN_FILE_NAME = pathName + File.separator + fileName;// 元の原図ファイル・・・絶対消すな

		// 閲覧フォーマットフラグチェック
		if (pdfFlug == null || pdfFlug.length() == 0 || "null".equals(pdfFlug)) {
			// 閲覧フォーマットフラグ設定エラー
			// 2013.08.02 yamagishi add. start
			if (dlmInfo != null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						dlmInfo.getMessage("search.failed.view.invalid.setting." + user.getLanKey(), ORIGIN_FILE_NAME));
				category.error("==> error");
				return null;
			}
			// 2013.08.02 yamagishi add. end
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.invalid.setting." + user.getLanKey(), new Object[] { ORIGIN_FILE_NAME }, null));
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.error("==> error");
			return "error";
		}
		String convertPdf = pdfFlug.toLowerCase(); // "tiff", "printablePdf", "noprintPdf"

		// 念のため元の原図があるか確認する '04.Mar.2 Hirata
		if (!new File(ORIGIN_FILE_NAME).exists()) {
			// 元の原図が存在しない
			// 2013.08.02 yamagishi add. start
			if (dlmInfo != null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						dlmInfo.getMessage("search.failed.view.nofile." + user.getLanKey(), ORIGIN_FILE_NAME));
				category.error("==> error");
				return null;
			}
			// 2013.08.02 yamagishi add. end
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.nofile." + user.getLanKey(), new Object[] { ORIGIN_FILE_NAME }, null));
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.error("==> error");
			return "error";
		}

		String outFileName = ORIGIN_FILE_NAME;// ストリームに流す対象のファイル名

		// 2013.07.01 yamagishi add. start
		// バナー処理追加
		Connection conn = null;
		conn = ds.getConnection();

		// バナーを押す（該当図）
		if (AclvMasterDB.isCorresponding(drwgNo, conn)) {
			// 一時的なファイル名を変更する。
			String newOutFileName = tempDirName + File.separator + user.getId()
					+ "_" + drwgNo + "_" + new Date().getTime() + ".tif";

			doCorrespondingBanner(outFileName, newOutFileName,
					drasapInfo.getCorrespondingStampStr(),
					drasapInfo.getCorrespondingStampW(), drasapInfo.getCorrespondingStampL(),
					drasapInfo.getViewStampDeep(),
					drasapInfo.isDispDrwgNoWithView() ? drwgNo : null, // 図番を印字しない場合、nullを渡す
					user, errors, dlmInfo);

			// 変換前のファイルがオリジナルでなければ、削除する
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " を削除した");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// バナー処理でエラーが発生していたら
				// 2013.08.02 yamagishi add. start
				if (dlmInfo != null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dlmInfo.getLabelMessage());
					category.error("==> error");
					return null;
				}
				// 2013.08.02 yamagishi add. end
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.error("==> error");
				return "error";
			}
			outFileName = newOutFileName;// バナー処理後のファイル名に変更する
		}
		// 2013.07.01 yamagishi add. end

		// 処理手順を変更 '04.Apr.8 by MUR/Hirata
		// 新) スタンプ合成 --> 間引き処理
		// 旧) 間引き処理 --> スタンプ合成
		// 理由・・・スタンプ合成すると 100dpiが400dpiになってしまうため

		// バナーを押す
		if (user.isViewStamp()) {// ビューでバナーするなら
			//String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogicだとやたらにセッションIDが長いため、一時的なファイル名を変更する。
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".tif";

			doBanner(outFileName, newOutFileName,
					drasapInfo.getViewStampW(), drasapInfo.getViewStampL(), drasapInfo.getViewStampDeep(),
					drasapInfo.getViewStampDateFormat(),
					// 図番を印字しない場合、nullを渡す
					drasapInfo.isDispDrwgNoWithView() ? drwgNo : null,
					//					user, errors);	// 2013.08.02 yamagishi modified.
					user, errors, dlmInfo);
			// 変換前のファイルがオリジナルでなければ、削除する
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " を削除した");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// バナー処理でエラーが発生していたら
				// 2013.08.02 yamagishi add. start
				if (dlmInfo != null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dlmInfo.getLabelMessage());
					category.error("==> error");
					return null;
				}
				// 2013.08.02 yamagishi add. end
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.error("==> error");
				return "error";
			}
			outFileName = newOutFileName;// バナー処理後のファイル名に変更する

		}

		// 間引きが必要か判定する
		// 間引きサイズの未設定にも対応する。'04.Jul.19変更 by Hirata。
		String mabikiDpi = null;// 間引きのdpi
		if (drasapInfo.getMabiki100dpiSize() != null &&
				DrasapUtil.compareDrwgSize(drwgSize, drasapInfo.getMabiki100dpiSize()) >= 0) {
			// 図面サイズ >= 100dpiのサイズ、なら
			mabikiDpi = "100";
		} else if (drasapInfo.getMabiki200dpiSize() != null &&
				DrasapUtil.compareDrwgSize(drwgSize, drasapInfo.getMabiki200dpiSize()) >= 0) {
			// 図面サイズ >= 200dpiのサイズ、なら
			mabikiDpi = "200";
		}
		if (mabikiDpi != null) {
			// 間引き対象なら、取得した「間引きのdpi」で間引き
			// 間引きしたファイル名
			// String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogicだとやたらにセッションIDが長いため、一時的なファイル名を変更する。
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".tif";

			//			doMabiki(outFileName, newOutFileName, mabikiDpi, user, errors, drwgNo);	// 2013.08.02 yamagishi modified.
			doMabiki(outFileName, newOutFileName, mabikiDpi, user, errors, drwgNo, dlmInfo);
			// 変換前のファイルがオリジナルでなければ、削除する
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " を削除した");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// 間引き処理でエラーが発生していたら
				// 2013.08.02 yamagishi add. start
				if (dlmInfo != null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dlmInfo.getLabelMessage());
					category.error("==> error");
					return null;
				}
				// 2013.08.02 yamagishi add. end
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.error("==> error");
				return "error";
			}
			outFileName = newOutFileName;// 間引きしたファイル名に変更する
		}

		// PDFに変換する
		if ("printablePdf".equalsIgnoreCase(convertPdf) || "noprintPdf".equalsIgnoreCase(convertPdf)) {
			// PDF変換後のファイル名
			// String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogicだとやたらにセッションIDが長いため、一時的なファイル名を変更する。
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".pdf";
			//			tifToPdf(outFileName, newOutFileName, user, errors, drwgNo, convertPdf.equalsIgnoreCase("printablePdf")?true:false);	// 2013.08.02 yamagishi modified.
			tifToPdf(outFileName, newOutFileName, user, errors, drwgNo, "printablePdf".equalsIgnoreCase(convertPdf) == true, dlmInfo, 1, false);
			// 変換前のファイルがオリジナルでなければ、削除する
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " を削除した");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// PDF処理でエラーが発生していたら
				// 2013.08.02 yamagishi modified. start
				if (dlmInfo != null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dlmInfo.getLabelMessage());
					category.error("==> error");
					return null;
				}
				// 2013.08.02 yamagishi modified. end
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.error("==> error");
				return "error";
			}
			outFileName = newOutFileName;// PDF変換したしたファイル名に変更する
		}

		// ストリームに流す
		File f = new File(outFileName);
		String streamFileName = fileName;// ヘッダにセットするファイル名
		if ("printablePdf".equalsIgnoreCase(convertPdf) || "noprintPdf".equalsIgnoreCase(convertPdf)) {
			// PDF変換した場合
			int lastIndex = streamFileName.lastIndexOf('.');
			if (lastIndex == -1) {
				// ファイル名に'.'がない場合・・・後ろに'.pdf'を
				streamFileName += ".pdf";
			} else {
				// .tifを.pdfに変更する
				streamFileName = streamFileName.substring(0, lastIndex) + ".pdf";
			}
		}
		// ユーザーがこの処理の最中に、別のリクエストを投げた場合（処理が長いので止めた、指定した図番を間違えた)
		// responseがすでに閉じられている場合がある。
		// その場合でも、オリジナルのファイル以外を削除したいので、try,catchする
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			//このままだと日本語が化ける
			//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			response.setContentLength((int) f.length());

			out = new BufferedOutputStream(response.getOutputStream());
			in = new BufferedInputStream(new FileInputStream(f));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}

			out.flush();

		} finally {
			// CLOSE処理
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
				category.debug("out.close()");
			} catch (Exception e) {
			}

			// オリジナルのファイルでなければ、削除する
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (f.delete()) {
					category.debug(outFileName + " を削除した");
				}
			}
		}
		// アクセスログを
		// '04.Nov.23 図番もロギングするように
		// 2005-Mar-4変更 他システムからの呼び出しではシステムIDをロギングする。
		//		AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, sysId);		// 2013.08.02 yamagishi modified.
		if (dlmInfo != null && dlmInfo.isActSave()) {
			AccessLoger.loging(user, AccessLoger.FID_SAVE, drwgNo, sysId);
		} else {
			AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, sysId);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 間引き処理を行う。例外が発生した場合、errorsにaddする。
	 * @param inFile 間引き対象のファイル(絶対パス)
	 * @param outFile 出力ファイル名(絶対パス)
	 * @param dpi 解像度
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doMabiki(String inFile, String outFile, String dpi, User user, Model errors,
			//							String drwgNo){		// 2013.08.02 yamagishi modified.
			String drwgNo, DLManagerInfo dlmInfo) {
		category.debug("間引き処理の開始");
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "間引き処理の開始");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "間引き対象が存在しません。" + inFile);
		}

		BufferedReader br = null;
		Process process = null;
		try {
			// 2013.06.14 yamagishi modified. start
			//			String beaHome = System.getenv("BEA_HOME");
			//			if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
			//			if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
			//			String mabikiPath = beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.mabiki.path");
			String apServerHome = System.getenv(BEA_HOME);
			if (apServerHome == null) {
				apServerHome = System.getenv(CATALINA_HOME);
			}
			if (apServerHome == null) {
				apServerHome = System.getenv(OCE_AP_SERVER_HOME);
			}
			if (apServerHome == null) {
				apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			}

			String mabikiPath = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.mabiki.path");
			// 2013.06.14 yamagishi modified. end
			StringBuilder sbCmd = new StringBuilder(mabikiPath);
			sbCmd.append(' ');
			sbCmd.append(inFile);// 間引き対象ファイル
			sbCmd.append(' ');
			sbCmd.append(outFile);// 間引き後の出力ファイル
			sbCmd.append(' ');
			sbCmd.append(dpi);// 解像度
			synchronized (lock) {
				process = Runtime.getRuntime().exec(sbCmd.toString());
				// プロセスからの標準出力を捨てる
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				int exitCode = process.waitFor();
				while (br.ready()) {
					//プロセスからの標準出力を捨てる
					//そうしないとプロセスがストールする場合がある。
					br.readLine();
				}

				if (exitCode != 0) {
					// 2013.08.02 yamagishi modified. start
					//					// for ユーザー
					//					MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(),"終了コード="+exitCode));
					//					// for システム管理者
					//					ErrorLoger.error(user, this,
					//								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					if (dlmInfo != null) {
						// for DLマネージャ
						dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.mabiki." + user.getLanKey(), "終了コード=" + exitCode));
						// for システム管理者
						ErrorLoger.error(user, dlmInfo,
								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					} else {
						// for ユーザー
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(), new Object[] { "終了コード=" + exitCode }, null));
						// for システム管理者
						ErrorLoger.error(user, this,
								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					}
					// 2013.08.02 yamagishi modified. end
					// ビュー専用のログ
					String errMsg = "VIEWのための間引き処理に失敗。終了コード=" + exitCode;
					ViewLoger.error(drwgNo, errMsg);
					// for MUR
					category.error(errMsg);
				}
			}

		} catch (Exception e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ユーザー
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(),e.getMessage()));
			//			// for システム管理者
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			if (dlmInfo != null) {
				// for DLマネージャ
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.mabiki." + user.getLanKey(), e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			} else {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			}
			// 2013.08.02 yamagishi modified. end
			// ビュー専用のログ
			String errMsg = "VIEWのための間引き処理に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (process != null) {
				process.destroy();
			}
		}

		// ビュー専用のログ
		ViewLoger.info(drwgNo, "間引き処理の終了");
	}

	/**
	 * バナー処理を行なう。例外が発生した場合、errorsにaddする。
	 * @param inFile バナー対象のファイル(絶対パス)
	 * @param outFile 出力ファイル名(絶対パス)
	 * @param pixW 右下原点からの横方向の距離(ピクセル単位)
	 * @param pixL 右下原点からの縦方向の距離(ピクセル単位)
	 * @param deep スタンプの濃淡(0から100まで)
	 * @param dateFormat 日付形式。SimpleDateFormatの形式で。
	 * @param drwgNo 図番。nullの場合、図番は印字されない。
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doBanner(String inFile, String outFile, String pixW, String pixL, String deep,
			//						String dateFormat, String drwgNo, User user, Model errors){
			String dateFormat, String drwgNo, User user, Model errors, DLManagerInfo dlmInfo) { // 2013.08.02 yamagishi modified.
		category.debug("バナー処理の開始");
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "バナー処理の開始");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "バナー処理の対象が存在しません。" + inFile);
		}
		try {
			synchronized (lock) {
				if (deep == null || deep.length() == 0) {
					deep = "0";
				}
				createTextMergeText(dateFormat, user.getName(), drwgNo, user, errors);
				if (!textMerge(user, errors)) {
					return;
				}
				createStampMergeText(deep, pixW, pixL, user, errors);
				if (!StampMerge(inFile, outFile, user, errors)) {
					return;
				}
			}
		} catch (Exception e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ユーザー
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.banner." + user.getLanKey(),e.getMessage()));
			//			// for システム管理者
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			if (dlmInfo != null) {
				// for DLマネージャ
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.banner." + user.getLanKey(), e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			} else {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			}
			// 2013.08.02 yamagishi modified. end
			// ビュー専用のログ
			String errMsg = "VIEWのためのバナー処理に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "バナー処理の終了");
	}

	private boolean textMerge(User user, Model errors) throws InterruptedException, IOException {

		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			// 2013.06.14 yamagishi modified. end
		}

		Process process = null;
		BufferedReader br = null;

		String textMergePath = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.textMerge.path");
		StringBuilder sbCmd = new StringBuilder(textMergePath);
		try {
			process = Runtime.getRuntime().exec(sbCmd.toString());
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int exitCode;
			exitCode = process.waitFor();
			while (br.ready()) {
				//プロセスからの標準出力を捨てる
				//そうしないとプロセスがストールする場合がある。
				br.readLine();
			}
			if (exitCode != 0) {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// ビュー専用のログ
				String errMsg = "ビューイング用のデータ作成に失敗しました。(テキストマージの失敗 " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(テキストマージの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(テキストマージの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (process != null) {
				process.destroy();
			}
		}

		return true;

	}

	private boolean StampMerge(String inFile, String outFile, User user, Model errors) throws InterruptedException, IOException {
		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		// 2013.06.14 yamagishi modified. end
		Process process = null;
		BufferedReader br = null;
		String errMsg = null;

		String stampMergePath = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.stampMerge.path");
		StringBuilder sbCmd = new StringBuilder(stampMergePath);
		sbCmd.append(' ');
		sbCmd.append(inFile);// バナー枠ＴＩＦＦファイル
		sbCmd.append(' ');
		sbCmd.append(outFile);// テキストマージ後のＴＩＦＦファイル
		sbCmd.append(' ');
		try {
			process = Runtime.getRuntime().exec(sbCmd.toString());
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int exitCode;
			exitCode = process.waitFor();
			while (br.ready()) {
				//プロセスからの標準出力を捨てる
				//そうしないとプロセスがストールする場合がある。
				br.readLine();
			}
			if (exitCode != 0) {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// ビュー専用のログ
				errMsg = "ビューイング用のデータ作成に失敗しました。(スタンプマージの失敗 " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			errMsg = "ビューイング用のデータ作成に失敗しました。(スタンプマージの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (process != null) {
				process.destroy();
			}
		}

		return true;

	}

	/**
	 * PDF変換処理を行う。例外が発生した場合、errorsにaddする。
	 * @param inFile PDF変換対象のファイル(絶対パス)
	 * @param outFile 出力ファイル名(絶対パス)
	 * @param dpi 解像度
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 * @param totalPage Tiffの合計ページ数
	 */
	@SuppressWarnings("deprecation")
	private void tifToPdf(String inFile, String outFile, User user, Model errors,
			//							String drwgNo, boolean printable){	// 2013.08.02 yamagishi modified.
			String drwgNo, boolean printable, DLManagerInfo dlmInfo, int totalPage,
			boolean multiPDF) {
		// Win版の場合、pdf_java.dllをパスが通ったところに置くこと。
		// パスが通っていない場合、
		// Cannot load the PDFlib shared library/DLL for Java.
		// Make sure to properly install the native PDFlib library.
		// といったメッセージが出力される。

		// ファイル名などでサポートされているのはasciiのみ(2004.Jan.14 F.Hirata/MUR)

		category.debug("PDF変換処理の開始");
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "PDF変換処理の開始");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "PDF変換処理の対象が存在しません。" + inFile);
		}

		int image;
		pdflib p = null;
		/* This is where font/image/PDF input files live. Adjust as necessary.*/

		//String searchpath = ".";

		try {

			p = new pdflib();
			/* open new PDF file */

			// アクセス権の削除 （Modified Oce Japan Corporation 2004/01/09）
			// 2020.02.14 yamamoto mod Multi PDFの場合はパスワードは設定しない
			if (!multiPDF) {
				p.set_parameter("masterpassword", "EnjoyGeorgia");
			}
			if (!printable) {
				p.set_parameter("permissions", "nomodify noaccessible noassemble noannots noforms nocopy noprint nohiresprint");
			}
			p.set_parameter("compatibility", "1.4");
			if (p.open_file(outFile) == -1) {
				throw new Exception("Error: " + p.get_errmsg());
			}

			// このパラメータが何を示すか不明
			// とりあえず削除しても動作することを確認(2004.Jan.14 F.Hirata/MUR)
			//p.set_parameter("SearchPath", searchpath);

			// この部分は不要 （Modified Oce Japan Corporation 2004/01/09）
			//  p.set_info("Creator", "image.java");
			//  p.set_info("Author", "Thomas Merz");
			//  p.set_info("Title", "image sample (Java)");

			// Tiffのページ数分変換
			// 1ページから変換開始
			for (int pageCnt = 1; pageCnt <= totalPage; pageCnt++) {
				image = p.load_image("auto", inFile, "page=" + pageCnt);
				if (image == -1) {
					throw new Exception("Error: " + p.get_errmsg());
				}

				/* dummy page size, will be adjusted by PDF_fit_image() */
				p.begin_page(10, 10);
				p.fit_image(image, (float) 0.0, (float) 0.0, "adjustpage");
				p.close_image(image);
				p.end_page(); /* close page           */
			}

			p.close(); /* close PDF document   */

		} catch (PDFlibException e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ユーザー
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(),
			//					"[" + e.get_errnum() + "] " + e.get_apiname() +	": " + e.getMessage()));
			//			// for システム管理者
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			if (dlmInfo != null) {
				// for DLマネージャ
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.pdf." + user.getLanKey(),
						"[" + e.get_errnum() + "] " + e.get_apiname() + ": " + e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			} else {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(),
						new Object[] { "[" + e.get_errnum() + "] " + e.get_apiname() + ": " + e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			}
			// 2013.08.02 yamagishi modified. end
			// ビュー専用のログ
			String errMsg = "VIEWのためのPDF変換処理に失敗\n" +
					"[" + e.get_errnum() + "] " + e.get_apiname() + "\n" +
					ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);

		} catch (Exception e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ユーザー
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(), e.getMessage()));
			//			// for システム管理者
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			if (dlmInfo != null) {
				// for DLマネージャ
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.pdf." + user.getLanKey(), e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			} else {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			}
			// 2013.08.02 yamagishi modified. end
			// ビュー専用のログ
			String errMsg = "VIEWのためのPDF変換処理に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);

		} finally {
			if (p != null) {
				p.delete(); /* delete the PDFlib object */
			}
		}
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "PDF変換処理の終了");
	}

	private void createTextMergeText(String dateFormat, String name, String drwgNo, User user, Model errors) throws FileNotFoundException, IOException, InterruptedException {
		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		// 2013.06.14 yamagishi modified. end
		String template = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.textMergeTemplate");
		String mergeText = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.textMergeTxt");

		BufferedReader reader = null;
		BufferedWriter writer = null;
		String lineData = null;
		String outLine = null;
		if (drwgNo == null || drwgNo.length() == 0) {
			drwgNo = "";
		} else {
			drwgNo = DrasapUtil.formatDrwgNo(drwgNo);
		}
		String ymd = new SimpleDateFormat(dateFormat).format(new Date());// YYYY/MM/DD形式の本日日付
		//		String bannerStr = title + " " + ymd + " " + name + " " + drwgNo;
		String bannerStr = ymd + " " + name + " " + drwgNo;

		String sMojiHeight = "3.6";
		String sMojiWidth = "3.6";
		//		double mojiHeight = 3.6; // 2013.07.02 yamagishi commented out.
		double mojiWidth = 3.6;

		try {
			reader = new BufferedReader(new FileReader(template));
			writer = new BufferedWriter(new FileWriter(mergeText));
			while ((lineData = reader.readLine()) != null) {
				if (lineData.indexOf("BANNER_TITLE=") == 0) {
					bannerStr = lineData.replace("BANNER_TITLE=", "") + " " + bannerStr;
					continue;
				}
				if (lineData.indexOf("MOJI_WIDTH=") == 0) {
					sMojiWidth = lineData.replace("MOJI_WIDTH=", "");
					if (DrasapUtil.isDigit(sMojiWidth)) {
						mojiWidth = Double.parseDouble(sMojiWidth);
					}
					outLine = lineData;
				} else if (lineData.indexOf("MOJI_HEIGHT=") == 0) {
					sMojiHeight = lineData.replace("MOJI_HEIGHT=", "");
					if (DrasapUtil.isDigit(sMojiHeight)) {
						// 2013.07.02 yamagishi modified. start
						//						mojiHeight = Double.parseDouble(sMojiHeight);
						Double.parseDouble(sMojiHeight);
						// 2013.07.02 yamagishi modified. end
					}
				} else if ("%%BANNER.TEXT".equals(lineData)) {
					outLine = "TEXT=" + bannerStr;
				} else {
					outLine = lineData;
				}
				writer.write(outLine + System.getProperty("line.separator"));
			}
			setBannerWidth(bannerStr, mojiWidth, user, errors);

		} catch (FileNotFoundException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "specified pathname does not exist.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
			}
			throw e;
		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(mergeText);
			try {
				reader.close();
			} catch (IOException e1) {
			}
			throw e;
		} catch (InterruptedException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(mergeText);
			try {
				reader.close();
			} catch (IOException e1) {
			}
			throw e;
		} finally {
			reader.close();
			writer.flush();
			writer.close();
		}
	}

	private boolean setBannerWidth(String bannerStr, double mojiWidth, User user, Model errors) throws InterruptedException, IOException {

		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			// 2013.06.14 yamagishi modified. end
		}

		Process process = null;
		BufferedReader br = null;

		String setBannerWidth = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.setBannerWidth.path");
		double dBannerWidth = (bannerStr.getBytes().length + 4) * mojiWidth * 100;
		long bannerWidth = (long) (dBannerWidth / 2);
		setBannerWidth = setBannerWidth + " " + bannerWidth;
		StringBuilder sbCmd = new StringBuilder(setBannerWidth);
		try {
			process = Runtime.getRuntime().exec(sbCmd.toString());
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int exitCode;
			exitCode = process.waitFor();
			while (br.ready()) {
				//プロセスからの標準出力を捨てる
				//そうしないとプロセスがストールする場合がある。
				br.readLine();
			}
			if (exitCode != 0) {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// ビュー専用のログ
				String errMsg = "ビューイング用のデータ作成に失敗しました。(setBannerWidthの失敗 " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(setBannerWidthの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(setBannerWidthの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (process != null) {
				process.destroy();
			}
		}

		return true;

	}

	private void createStampMergeText(String deep, String x, String y, User user, Model errors) throws IOException {
		// 2013.06.14 yamagishi modified. start
		//		String beaHome = System.getenv("BEA_HOME");
		//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
		//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		// 2013.06.14 yamagishi modified. end
		String tmpStamp = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.tempStamp.path");
		String mergeText = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.stampMergeTxt");

		BufferedWriter writer = null;
		String outLine = null;
		try {
			writer = new BufferedWriter(new FileWriter(mergeText));
			outLine = tmpStamp + " -ALPHA" + deep;
			outLine += " -R90 -NM0 -MOR -MORG2 -MREFRD -XM" + x + " -YM" + y;
			writer.write(outLine + System.getProperty("line.separator"));
		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(mergeText, errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			writer.flush();
			writer.close();
		}
	}

	// 2013.07.12 yamagishi add. start
	/**
	 * バナー処理（該当図）を行なう。例外が発生した場合、errorsにaddする。
	 * @param correspondingStampStr 該当図用スタンプ文字
	 * @param inFile バナー対象のファイル(絶対パス)
	 * @param outFile 出力ファイル名(絶対パス)
	 * @param pixW 右下原点からの横方向の距離(ピクセル単位)
	 * @param pixL 右下原点からの縦方向の距離(ピクセル単位)
	 * @param deep スタンプの濃淡(0から100まで)
	 * @param drwgNo 図番。nullの場合、図番は印字されない。
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doCorrespondingBanner(String inFile, String outFile, String correspondingStampStr, String pixW, String pixL,
			String deep, String drwgNo, User user, Model errors, DLManagerInfo dlmInfo) {

		category.debug("バナー処理（該当図）の開始");
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "バナー処理（該当図）の開始");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "バナー処理（該当図）の対象が存在しません。" + inFile);
		}
		try {
			synchronized (lock) {
				if (deep == null || deep.length() == 0) {
					deep = "0";
				}
				createCorrespondingTextMergeText(correspondingStampStr, drwgNo, user, errors);
				if (!textMerge(user, errors)) {
					return;
				}
				createCorrespondingStampMergeText(deep, pixW, pixL, user, errors);
				if (!StampMerge(inFile, outFile, user, errors)) {
					return;
				}
			}
		} catch (Exception e) {
			if (dlmInfo != null) {
				// for DLマネージャ
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.banner." + user.getLanKey(), e.getMessage()));
				// for システム管理者
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			} else {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			}
			// ビュー専用のログ
			String errMsg = "VIEWのためのバナー処理（該当図）に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "バナー処理（該当図）の終了");
	}

	private void createCorrespondingTextMergeText(String correspondingStampStr,
			String drwgNo, User user, Model errors) throws IOException, InterruptedException {

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		String template = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.textMergeTemplate");
		String mergeText = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.textMergeTxt");

		BufferedReader reader = null;
		BufferedWriter writer = null;
		String lineData = null;
		String outLine = null;
		String bannerStr = correspondingStampStr;

		String sMojiHeight = "3.6";
		String sMojiWidth = "3.6";
		double mojiWidth = 3.6;

		try {
			reader = new BufferedReader(new FileReader(template));
			writer = new BufferedWriter(new FileWriter(mergeText));
			while ((lineData = reader.readLine()) != null) {
				if (lineData.indexOf("BANNER_TITLE=") == 0) {
					lineData.replace("BANNER_TITLE=", "");
					continue;
				}
				if (lineData.indexOf("MOJI_WIDTH=") == 0) {
					sMojiWidth = lineData.replace("MOJI_WIDTH=", "");
					if (DrasapUtil.isDigit(sMojiWidth)) {
						mojiWidth = Double.parseDouble(sMojiWidth);
					}
					outLine = lineData;
				} else if (lineData.indexOf("MOJI_HEIGHT=") == 0) {
					sMojiHeight = lineData.replace("MOJI_HEIGHT=", "");
					if (DrasapUtil.isDigit(sMojiHeight)) {
						Double.parseDouble(sMojiHeight);
					}
				} else if ("%%BANNER.TEXT".equals(lineData)) {
					outLine = "TEXT=" + bannerStr;
				} else {
					outLine = lineData;
				}
				writer.write(outLine + System.getProperty("line.separator"));
			}
			setCorrespondingBannerWidth(bannerStr, mojiWidth, user, errors);

		} catch (FileNotFoundException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.notound", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "specified pathname does not exist.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
			}
			throw e;
		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(mergeText);
			try {
				reader.close();
			} catch (IOException e1) {
			}
			throw e;
		} catch (InterruptedException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(mergeText);
			try {
				reader.close();
			} catch (IOException e1) {
			}
			throw e;
		} finally {
			reader.close();
			writer.flush();
			writer.close();
		}
	}

	private boolean setCorrespondingBannerWidth(String bannerStr, double mojiWidth, User user, Model errors)
			throws InterruptedException, IOException {

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}

		Process process = null;
		BufferedReader br = null;

		String setCorrespondingBannerWidth = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.setCorrespondingBannerWidth.path");
		double dCorrespondingBannerWidth = (bannerStr.getBytes().length + 3) * mojiWidth * 100;
		long correspondingBannerWidth = (long) (dCorrespondingBannerWidth / 2);
		setCorrespondingBannerWidth = setCorrespondingBannerWidth + " " + correspondingBannerWidth;
		StringBuilder sbCmd = new StringBuilder(setCorrespondingBannerWidth);
		try {
			process = Runtime.getRuntime().exec(sbCmd.toString());
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int exitCode;
			exitCode = process.waitFor();
			while (br.ready()) {
				//プロセスからの標準出力を捨てる
				//そうしないとプロセスがストールする場合がある。
				br.readLine();
			}
			if (exitCode != 0) {
				// for ユーザー
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for システム管理者
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// ビュー専用のログ
				String errMsg = "ビューイング用のデータ作成に失敗しました。(setCorrespondingBannerWidthの失敗 " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(setBannerWidthの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// ビュー専用のログ
			String errMsg = "ビューイング用のデータ作成に失敗しました。(setBannerWidthの失敗 " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return true;
	}
	// 2013.07.12 yamagishi add. end

	// 2013.09.06 yamagishi add. start
	private void createCorrespondingStampMergeText(String deep, String x, String y, User user, Model errors) throws IOException {
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) {
			apServerHome = System.getenv(CATALINA_HOME);
		}
		if (apServerHome == null) {
			apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		}
		if (apServerHome == null) {
			apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
		}
		String tmpStamp = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.tempStamp.path");
		String mergeText = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.preview.stampMergeTxt");

		BufferedWriter writer = null;
		String outLine = null;
		try {
			writer = new BufferedWriter(new FileWriter(mergeText));
			outLine = tmpStamp + " -ALPHA" + deep;
			outLine += " -R90 -NM0 -MOR -MORG2 -MREFRD -XM" + x + " -YM" + y;
			writer.write(outLine + System.getProperty("line.separator"));
		} catch (IOException e) {
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.file.IOExceltion", new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// ビュー専用のログ
			String errMsg = "Signals that an I/O exception of some sort has occurred.\n" + ErrorUtility.error2String(e);
			ViewLoger.error(mergeText, errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} finally {
			writer.flush();
			writer.close();
		}
	}
	// 2013.09.06 yamagishi add. end

	// 2013.08.02 yamagishi add. start
	/**
	 * DLマネージャで発生したエラーログを出力する。
	 * @param user
	 * @param dlmInfo
	 */
	private void doErrorLog(User user, DLManagerInfo dlmInfo) {
		// for DLマネージャ
		ErrorLoger.error(user, dlmInfo,
				DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
	}
	// 2013.08.02 yamagishi add. end

	/**
	 * マルチPDF化 または PDFのZIP化を行う
	 * @param searchResultForm
	 * @param drasapInfo
	 * @param user
	 * @param response
	 * @param errors
	 * @param action
	 *
	 * modify 2020.03.10 yamamoto PDFのZIP化処理追加
	 */
	private void doMultiPDF(
			SearchResultForm searchResultForm,
			DrasapInfo drasapInfo,
			User user,
			HttpServletResponse response,
			Model errors,
			HttpServletRequest request,
			String action) throws Exception {

		ArrayList<PreviewElement> selectedList = new ArrayList<PreviewElement>();
		ArrayList<String> joinFileNameList = new ArrayList<String>();
		HashMap<String, String> singlePdfFileMap = new HashMap<String, String>();
		String convertPdf = "";
		String pdfFlug = "";

		// テンポラリのフォルダのフルパス
		String tempDirName = DrasapUtil.getRealTempPath(request);

		//		category.debug("searchResultFormから取得成功");

		for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
			SearchResultElement resultElem = searchResultForm.getSearchResultList().get(i);
			if (resultElem.isSelected()) {
				// 検索結果のチェックボックスに
				// チェックが入っていれば、リストに保存
				PreviewElement pe = new PreviewElement();
				pe.setDrwgNo(resultElem.getDrwgNo()); // 図番
				pe.setDrwgSize(resultElem.getAttr("DRWG_SIZE")); // 図番サイズ
				pe.setFileName(resultElem.getFileName()); // ファイル名
				pe.setPathName(resultElem.getPathName()); // ディレクトリのフルパス
				selectedList.add(pe);
			}
		}
		// PDF文書セキュリティ無しのため、固定で値を設定する
		pdfFlug = "printablePdf"; // printablePdf固定
		convertPdf = pdfFlug.toLowerCase(); // "tiff", "printablePdf", "noprintPdf"

		Connection conn = null;
		String tmpOutFileName = "";

		try {

			conn = ds.getConnection();

			/*
			 * 選択図面について以下実施
			 * ・バナーを押す
			 * ・スタンプ合成
			 * ・PDF変換(PDF単独 Zip出力の場合のみ
			 */
			for (int i = 0; i < selectedList.size(); i++) {

				// 選択図面の情報取得
				String drwgNo = selectedList.get(i).getdrwgNo();// 図番
				String fileName = selectedList.get(i).getFileName();// ファイル名
				String pathName = drasapInfo.getViewDBDrive() + File.separator +
						selectedList.get(i).getPathName(); // ディレクトリのフルパス

				final String ORIGIN_FILE_NAME = pathName + File.separator + fileName;// 元の原図ファイル・・・絶対消すな

				// 元の原図があるか確認
				if (!new File(ORIGIN_FILE_NAME).exists()) {
					// 元の原図が存在しない
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.nofile." + user.getLanKey(), new Object[] { ORIGIN_FILE_NAME }, null));
					return;
				}

				String tmpJoinFileName = ORIGIN_FILE_NAME; // 結合ファイル名

				// バナーを押す（該当図）
				// 該当図面にアクセス権があるか？
				if (AclvMasterDB.isCorresponding(drwgNo, conn)) {

					// 変換後のファイル名に一時的なファイル名を付ける
					String newOutFileName = tempDirName + File.separator + user.getId()
							+ "_" + drwgNo + "_" + new Date().getTime() + ".tif";

					// バナー処理（該当図）
					doCorrespondingBanner(tmpJoinFileName, newOutFileName,
							drasapInfo.getCorrespondingStampStr(),
							drasapInfo.getCorrespondingStampW(), drasapInfo.getCorrespondingStampL(),
							drasapInfo.getViewStampDeep(),
							drasapInfo.isDispDrwgNoWithView() ? drwgNo : null, // 図番を印字しない場合、nullを渡す
							user, errors, null); // DLマネージャーから呼び出されないため、nullを渡す

					// 変換前のファイルがオリジナルでなければ、削除する
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " を削除した");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// エラーが発生
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName; // バナー処理後のファイル名に変更する
				}

				// スタンプ合成
				// 閲覧時のスタンプを押すか？
				if (user.isViewStamp()) {

					// 変換後のファイル名に一時的なファイル名を付ける
					String newOutFileName = tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".tif";

					// スタンプを押す
					doBanner(tmpJoinFileName, newOutFileName,
							drasapInfo.getViewStampW(), drasapInfo.getViewStampL(), drasapInfo.getViewStampDeep(),
							drasapInfo.getViewStampDateFormat(),
							// 図番を印字しない場合、nullを渡す
							drasapInfo.isDispDrwgNoWithView() ? drwgNo : null,
							user, errors, null); // DLマネージャーから呼び出されないため、nullを渡す

					// 変換前のファイルがオリジナルでなければ、削除する
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " を削除した");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// エラーが発生
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName;// バナー処理後のファイル名に変更する
				}

				// マルチPDFは間引きは不要

				// PDFに変換 (PDF単独 Zip出力の場合のみ)
				if (ACT_PDF_ZIP.equals(action)) {

					// 変換後のファイル名に一時的なファイル名を付ける
					String newOutFileName = tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".pdf";
					// PDF 変換
					tifToPdf(tmpJoinFileName, newOutFileName, user, errors, "",
							"printablePdf".equalsIgnoreCase(convertPdf) == true,
							null, 1, true);

					// 変換前のファイルがオリジナルでなければ、削除する
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " を削除した");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// エラーが発生
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName;// PDF変換後のファイル名に変更する
				}

				// 未変換(オリジナルファイル)の場合はファイルをコピーする
				if (ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {

					// 元のファイル
					File targetFile = new File(tmpJoinFileName);
					// コピー後のファイル
					File newOutFile = new File(tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".tif");

					// ファイルコピー
					copyFile(targetFile, newOutFile);
					category.debug("ORIGIN_FILE:" + targetFile + " newOutFile:" + newOutFile);
				}

				if (ACT_MULTI_PDF.equals(action)) {
					// 結合対象ファイル追加 (マルチPDF)
					joinFileNameList.add(tmpJoinFileName);
				} else {
					// 単独PDF化ファイル追加
					singlePdfFileMap.put(drwgNo, tmpJoinFileName);
				}

				// 2020/02/27 行追加
				// アクセスログをロギングする
				AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, user.getSys_id());

			}

			/*
			 * マルチPDF出力の場合
			 * 複数Tiffファイルを1ファイルのマルチTiffに変換後、PDFに変換する
			 */
			if (ACT_MULTI_PDF.equals(action)) {
				// 一時格納フォルダ名
				String newOutPathDir = tempDirName + File.separator + user.getId() + "_" + new Date().getTime();
				// Tiff結合後のファイル名(絶対パス)
				String multiTiffFileName = newOutPathDir + "_join.tif";

				// TIFFファイル結合
				doTiffJoint("", newOutPathDir, joinFileNameList, multiTiffFileName, user, errors);

				// 結合前のTIFFファイル削除
				for (int i = 0; i < joinFileNameList.size(); i++) {
					File targetFile = new File(joinFileNameList.get(i));
					targetFile.delete();
				}

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// エラーが発生
					throw new UserException();
				}

				// 変換後のPDFファイル名は「drawings_yyyymmddhhmmss.pdf」
				String newOutFileName = tempDirName + File.separator
						+ "drawings_"
						+ DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
						+ ".pdf";
				// PDF 変換
				tifToPdf(multiTiffFileName, newOutFileName, user, errors, "",
						"printablePdf".equalsIgnoreCase(convertPdf) == true,
						null, joinFileNameList.size(), true);

				// マルチTiffファイル削除
				if (new File(multiTiffFileName).delete()) {
					category.debug(multiTiffFileName + " を削除した");
				}
				if (!Objects.isNull(errors.getAttribute("message"))) {
					// エラーが発生
					throw new UserException();
				}
				// PDF変換後のファイル名に変更する
				tmpOutFileName = newOutFileName;
			}
			/*
			 *  PDF単独 Zip出力の場合
			 *  変換したPDFをZip化する
			 */
			else {

				// zip化対象フォルダ
				String zipTargetDir = tempDirName + File.separator
						+ "drawings_" + new Date().getTime();
				// zipファイル (フルパス)
				String zipFile = tempDirName + File.separator
						+ "drawings_"
						+ DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
						+ ".zip";

				// ZIP化
				doPdfToZip(selectedList, singlePdfFileMap, zipTargetDir, zipFile, user, errors);

				// PDFファイル削除
				for (String key : singlePdfFileMap.keySet()) {
					File file = new File(singlePdfFileMap.get(key));
					if (DrasapUtil.deleteFile(file)) {
						category.debug(file.toString() + " を削除した");
					} else {
						category.error(file.toString() + " を削除失敗");
					}
				}

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// エラーが発生
					throw new UserException();
				}

				// zip化後のファイル名に変更する
				tmpOutFileName = zipFile;
			}
		} catch (UserException e) {
			// 呼び出し先でエラーメッセージを追加しているため、returnのみ
			return;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}

		// 変換したファイルをストリームに流す
		File outFile = new File(tmpOutFileName);
		// ヘッダにセットするファイル名 (リストで1番最初に選択したファイル名を設定)
		String streamFileName = outFile.getName();

		// ユーザーがこの処理の最中に、別のリクエストを投げた場合（処理が長いので止めた、指定した図番を間違えた)
		// responseがすでに閉じられている場合がある。
		// その場合でも、オリジナルのファイル以外を削除したいので、try,catchする
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			//このままだと日本語が化ける
			response.setContentLength((int) outFile.length());

			out = new BufferedOutputStream(response.getOutputStream());
			in = new BufferedInputStream(new FileInputStream(outFile));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}

			out.flush();

		} catch (Exception e) {
			// for MUR
			String errMsg = "マルチPDF処理で失敗\n" + ErrorUtility.error2String(e);
			category.error(errMsg);
		} finally {
			// CLOSE処理
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}

			// 変換したファイルを削除する
			if (outFile.delete()) {
				category.debug(tmpOutFileName + " を削除した");
			}
		}
	}

	/**
	 * MULTI TIFFファイルを結合を行う。例外が発生した場合、errorsにaddする。
	 * @param drwgNo
	 * @param inPath TIFF図面格納パス(絶対パス)
	 * @param FileList
	 * @param outFile 結合後TIFFファイル名(絶対パス)
	 * @param user
	 * @param errors
	 * @return outFileName
	 */
	private void doTiffJoint(String drwgNo, String inPath, ArrayList<String> FileList, String outFile, User user, Model errors) {
		category.debug("ＴＩＦＦ結合処理の開始");
		//		String outFileName = "";

		// ビュー専用のログ
		try {

			String apServerHome = System.getenv(BEA_HOME);
			if (apServerHome == null) {
				apServerHome = System.getenv(CATALINA_HOME);
			}
			if (apServerHome == null) {
				apServerHome = System.getenv(OCE_AP_SERVER_HOME);
			}
			if (apServerHome == null) {
				apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			}

			// 一時格納フォルダを作成後、結合対象のTiffをコピー
			File f_inPath = new File(inPath);
			boolean success = f_inPath.mkdir();
			if (!success) {
				// フォルダ作成失敗
				throw new UserException("フォルダ作成失敗しました[" + inPath + "]");
			}
			for (int i = 0; i < FileList.size(); i++) {
				File targetFile = new File(FileList.get(i));
				String fileId = targetFile.getName();
				// 連番(4桁)_ユーザID_図番_タイムスタンプ.tif
				File newFile = new File(inPath + File.separator +
						String.format("%04d", i) + "_" + fileId);

				copyFile(targetFile, newFile);

				category.debug("targetFile:" + targetFile + " newFile:" + newFile);
			}

			category.debug("path:" + inPath);
			category.debug("tiff_join_file:" + outFile);

			Process process;
			StringBuilder sbCmd = new StringBuilder(
					apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.tiffmulti.path"));
			sbCmd.append(' ');
			sbCmd.append(inPath);// TIFF図面格納パス
			sbCmd.append(' ');
			sbCmd.append(outFile);// 結合後TIFFファイル名
			sbCmd.append(' ');

			category.debug("cmd:" + sbCmd.toString());

			synchronized (lock) {
				process = Runtime.getRuntime().exec(sbCmd.toString());
				// プロセスからの標準出力を捨てる
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String str = null;
				StringBuilder errStr = new StringBuilder();
				while ((str = br.readLine()) != null) {
					// プロセスからの標準出力を捨てる
					// そうしないとプロセスがストールする場合がある。
					errStr.append(str);
				}

				int exitCode = process.waitFor();
				if (exitCode == 0) {
					// for ユーザー
					MessageSourceUtil.addAttribute(errors, "message",
							messageSource.getMessage("search.failed.view.multiTiff." + user.getLanKey(), new Object[] { "drawNo=" + drwgNo + ",ExitCode=" + exitCode }, null));
					// for システム管理者
					ErrorLoger.error(user, this, "ＴＩＦＦの結合処理に失敗。終了コード=" + exitCode + "," + errStr.toString());
					// ビュー専用のログ
					String errMsg = "ＴＩＦＦの結合処理に失敗。終了コード=" + exitCode;
					ViewLoger.error(drwgNo, errMsg);
					// for MUR
					category.error(errMsg);
				}
			}

			// フォルダおよびフォルダ配下の全ファイル削除
			if (!DrasapUtil.deleteFile(f_inPath)) {
				category.error("フォルダ削除失敗しました[" + inPath + "]");
			}

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.multiTiff." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, "ＴＩＦＦの結合処理に失敗" + ErrorUtility.error2String(e));
			// ビュー専用のログ
			String errMsg = "ＴＩＦＦの結合に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// ビュー専用のログ
		ViewLoger.info(drwgNo, "ＴＩＦＦの結合処理の終了");

		//		return outFileName;
	}

	/**
	 * copyFile
	 * @param fromFile
	 * @param toFile
	 */
	private void copyFile(File fromFile, File toFile) {
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(fromFile));
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(toFile));
			byte buf[] = new byte[256];
			int len;
			while ((len = input.read(buf)) != -1) {
				output.write(buf, 0, len);
			}
			output.flush();
			output.close();
			input.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 複数PDFファイルZip化を行う。例外が発生した場合、errorsにaddする。
	 *
	 * @param elem
	 * @param pdfFileMap
	 * @param dir
	 * @param outFile
	 * @param user
	 * @param errors
	 */
	private void doPdfToZip(
			ArrayList<PreviewElement> elem,
			HashMap<String, String> pdfFileMap,
			String dir, String outFile, User user, Model errors) {

		category.debug("PDF Zip圧縮処理の開始");
		// ビュー専用のログ
		ViewLoger.info("", "PDF Zip圧縮処理の開始", true);

		byte[] buf = new byte[1024];
		// 対象フォルダ
		File f_zipDir = new File(dir);
		// Zipファイル
		File zipFile = new File(outFile);

		ZipOutputStream zos = null;
		InputStream is = null;

		try {

			// zip出力オブジェクト取得
			zos = new ZipOutputStream(new FileOutputStream(zipFile), Charset.forName("MS932"));

			// zipフォルダを作成し、zipフォルダにファイルコピー
			boolean success = f_zipDir.mkdir();
			if (!success) {
				// フォルダ作成失敗
				throw new UserException("Zipフォルダ作成失敗しました[" + f_zipDir.toString() + "]");
			}
			for (int i = 0; i < elem.size(); i++) {

				// 図番
				String _drwgNo = elem.get(i).getdrwgNo();

				// コピー元ファイル
				File targetFile = new File(pdfFileMap.get(_drwgNo));

				// 元のファイル名取得
				String pdfFileName = elem.get(i).getFileName();
				// 念のため拡張子をチェック
				int lastIndex = pdfFileName.lastIndexOf('.');
				if (lastIndex == -1) {
					// ファイル名に'.'がない場合、後ろに'.pdf'をつける
					pdfFileName += ".pdf";
				} else {
					// .tifを.pdfに変更する
					pdfFileName = pdfFileName.substring(0, lastIndex) + ".pdf";
				}

				// コピー先ファイル(zipフォルダ\図番ファイル名.pdf)
				File newFile = new File(dir + File.separator + pdfFileName);

				// ファイルコピー
				copyFile(targetFile, newFile);
				//				category.debug("targetFile:" + targetFile + " newFile:" + newFile);
			}

			// zip対象ファイルリスト取得
			File[] fileList = f_zipDir.listFiles();

			// Zip追加
			for (File file : fileList) {

				// Zipエントリ追加 (ファイルフルパス)
				zos.putNextEntry(new ZipEntry(file.getName()));

				// ZIPファイルに情報を書き込む
				is = new BufferedInputStream(new FileInputStream(file));

				int len = 0;
				while ((len = is.read(buf)) != -1) {
					// Zip書き込み
					zos.write(buf, 0, len);
				}

				// ストリームを閉じる
				zos.closeEntry();
				is.close();
			}

			// フォルダおよびフォルダ配下の全ファイル削除
			if (!DrasapUtil.deleteFile(f_zipDir)) {
				category.error("フォルダ削除失敗しました[" + f_zipDir.toString() + "]");
			}

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdfzip." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for システム管理者
			ErrorLoger.error(user, this, "PDFのZip圧縮処理に失敗" + ErrorUtility.error2String(e));
			// ビュー専用のログ
			String errMsg = "PDFのZip圧縮に失敗\n" + ErrorUtility.error2String(e);
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
			}
		}

		category.debug("PDF Zip圧縮処理の終了");

		// ビュー専用のログ
		ViewLoger.info("", "PDF Zip圧縮処理の終了", true);
	}

}
