package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.*;
import static tyk.drasap.common.DrasapUtil.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.AclUpload;
import tyk.drasap.common.AclUploadDB;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * アクセスレベル一括更新図面で、ダウンロード処理を実行するAction。
 * 入力パラメーターは requsetパラメーター で取得。
 * FILE_TYPE		ダウンロードファイルの種類
 * ACL_UPDATE_NO	管理NO
 *
 * @author 2013/07/08 yamagishi
 */
@Controller
public class AccessLevelDownloadAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	private Row cellStyles = null; // セルのスタイル設定取得用
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
	@PostMapping("/accessLevelDownload")
	public Object execute(
			AccessLevelBatchUpdateForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {

		if (category.isDebugEnabled()) {
			category.debug("start");
		}
		//
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// sessionタイムアウトの確認
		if (user == null) {
			return "timeout";
		}

		//ActionMessages errors = new ActionMessages();
		//MessageResources resources = getResources(request);

		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		AccessLevelBatchUpdateForm accessLevelBatchUpdateForm = form;

		// リクエストパラメータから取得する
		String fileType = request.getParameter("dlFileType"); // ファイルタイプ
		String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo(); // 管理NO

		// ダウンロード実行
		doDownload(response, fileType, aclUpdateNo, drasapInfo, user, errors);
		// エラー確認
		if (!Objects.isNull(errors.getAttribute("message"))) {
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "error";
		}

		if (category.isDebugEnabled()) {
			category.debug("end");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * ダウンロードを実行する。
	 * @param response
	 * @param fileType (0:雛形ファイル, 1:Excelデータ)
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doDownload(HttpServletResponse response, String fileType, String aclUpdateNo,
			DrasapInfo drasapInfo, User user, Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("ダウンロード実行処理の開始");
		}

		if ("0".equals(fileType)) {
			// 雛形ファイルのダウンロード
			createTemplate(response, user, errors);
		} else if ("1".equals(fileType)) {
			// 表示内容をExcelデータでダウンロード
			createExcelData(response, aclUpdateNo, user, drasapInfo, errors);
		} else {
			// ダウンロードするファイルが不明
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.download.failed.fileType", null, null));
			return;
		}

		if (category.isDebugEnabled()) {
			category.debug("ダウンロード実行処理の終了");
		}
	}

	/**
	 * 雛形ファイルのダウンロードを行う。
	 * @param response
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void createTemplate(HttpServletResponse response, User user,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("雛形ファイルダウンロード処理の開始");
		}
		//
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
		final String ORIGIN_FILE_NAME = apServerHome + drasapProperties.getProperty("tyk.download.template.path"); // テンプレートファイル

		// テンプレートファイルがあるか確認する
		if (!new File(ORIGIN_FILE_NAME).exists()) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.download.nofile", new Object[] { ORIGIN_FILE_NAME }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.download.nofile", new Object[] { ORIGIN_FILE_NAME }, null));
			}
			return;
		}

		String[] paths = ORIGIN_FILE_NAME.split("\\" + File.separator);
		String fileName = paths[paths.length - 1];
		String inFileName = ORIGIN_FILE_NAME; // ストリームに流す対象のファイル名

		// ストリームに流す
		File f = new File(inFileName);
		String streamFileName = fileName; // ヘッダにセットするファイル名

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
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.download.failed.template", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.download.failed.template", new Object[] { ErrorUtility.error2String(e) }, null));
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
			category.debug("雛形ファイルダウンロード処理の終了");
		}
	}

	/**
	 * 画面表示内容をExcelファイルでダウンロードを行う。
	 * @param response
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param errors
	 * @param resources
	 */
	private void createExcelData(HttpServletResponse response, String aclUpdateNo, User user, DrasapInfo drasapInfo,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("Excelファイルダウンロード処理の開始");
		}
		//
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
		final String ORIGIN_FILE_NAME = apServerHome + drasapProperties.getProperty("tyk.download.excel.format.path");// Excelフォーマット

		// テンプレートファイルがあるか確認する
		if (!new File(ORIGIN_FILE_NAME).exists()) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.download.nofile", new Object[] { ORIGIN_FILE_NAME }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.download.nofile", new Object[] { ORIGIN_FILE_NAME }, null));
			}
			return;
		}

		String[] paths = ORIGIN_FILE_NAME.split("\\" + File.separator);
		String sheetName = drasapProperties.getProperty("tyk.download.excel.sheet.name"); // シート名
		paths = ORIGIN_FILE_NAME.split("\\.");
		String extension = paths[paths.length - 1]; // 拡張子

		int headerRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.row.num")); // ヘッダ行
		int firstRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.start.row.num")); // 開始行
		int firstColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.start.column.num")); // 開始列
		int header1ColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.item1.column.num")); // ヘッダ項目1
		int header2ColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.item2.column.num")); // ヘッダ項目2

		Connection conn = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		ByteArrayOutputStream bytes = null;
		try {
			String fileName = null;
			if (aclUpdateNo.length() > 0) {
				fileName = "AclUpload_" + aclUpdateNo + "." + extension; // ファイル名
			} else {
				fileName = "AclUpload." + extension; // ファイル名
			}

			// レスポンスに書き出し
			response.setContentType("application/octet-stream;charset=Windows-31J");
			// 文字化け対応
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(fileName.getBytes("Windows-31J"), "ISO8859_1"));

			in = new BufferedInputStream(new FileInputStream(ORIGIN_FILE_NAME));
			out = new BufferedOutputStream(response.getOutputStream());

			// Excelフォーマット取得
			Workbook book = WorkbookFactory.create(in); // Book.
			Sheet sheet = book.getSheet(sheetName); // Sheet.
			Row row = null; // Row.

			// アップロードデータの取得
			conn = ds.getConnection();
			List<AclUpload> aclUploadList = AclUploadDB.getAclUploadDispList(aclUpdateNo, drasapInfo, conn);

			int columnIndex = 0;
			AclUpload aclUpload = null;
			for (int i = 0; i < aclUploadList.size(); i++) {
				columnIndex = firstColumnNum;
				aclUpload = aclUploadList.get(i);

				// レコードデータ作成
				row = getRow(sheet, firstRowNum + i);
				row.getCell(columnIndex).setCellValue(defaultString(aclUpload.getItemNo())); // 品番
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getItemName())); // 品名(規格型式)
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getGrpCode())); // グループ
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getCorrespondingValue())); // 該当図
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getConfidentialValue())); // 機密管理図
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getPreUpdateAclName())); // 変更前ACL
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getPostUpdateAclName())); // 変更後ACL
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getMessage())); // メッセージ

				if (i == 0) {
					if (cellStyles == null) {
						cellStyles = row;
					}
					// ヘッダデータ作成
					row = sheet.getRow(headerRowNum);
					row.getCell(header1ColumnNum).setCellValue(defaultString(aclUpdateNo)); // 管理NO
					row.getCell(header2ColumnNum).setCellValue(aclUploadList.size()); // 品番数
					// 印刷範囲設定
					book.setPrintArea(book.getSheetIndex(sheetName),
							firstColumnNum, columnIndex, headerRowNum,
							firstRowNum + aclUploadList.size() - 1);
				}

			}
			bytes = new ByteArrayOutputStream();
			book.write(bytes);
			response.setContentLength(bytes.size()); // コンテンツサイズ

			out.write(bytes.toByteArray());
			out.flush();

		} catch (Exception e) {
			try {
				response.reset();
			} catch (Exception e2) {
			}
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.download.failed.excel", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者.
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.download.failed.excel", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			// CLOSE処理
			try {
				conn.close();
			} catch (Exception e) {
			}
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
			try {
				if (bytes != null) {
					bytes.close();
				}
			} catch (Exception e) {
			}
		}

		if (category.isDebugEnabled()) {
			category.debug("Excelファイルダウンロード処理の終了");
		}
	}

	/**
	 * Excel行データが取得できなかった場合に、行追加を行う。
	 * スタイル設定は1行目の該当セルからコピーする。
	 * @param sheet
	 * @param rowIndex
	 */
	private Row getRow(Sheet sheet, int rowIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex); // 行追加
			Cell cell = null;
			for (int columnIndex = 0; columnIndex < cellStyles.getLastCellNum(); columnIndex++) {
				cell = row.createCell(columnIndex); // セル追加
				cell.setCellStyle(cellStyles.getCell(columnIndex).getCellStyle()); // スタイルコピー
			}
		}
		return row;
	}
}
