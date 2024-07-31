package tyk.drasap.system;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import tyk.drasap.change_acllog.ChangeAclLogger;
import tyk.drasap.common.AclUpdatableConditionMasterDB;
import tyk.drasap.common.AclUpdateNoSequenceDB;
import tyk.drasap.common.AclUpload;
import tyk.drasap.common.AclUploadDB;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.StringCheck;
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * アクセスレベル一括更新処理アクション
 *
 * @author 2013/07/03 yamagishi
 */
@Controller

public class AccessLevelBatchUpdateAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	private final String DEFAULT_CHARSET = "Windows-31J";

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
	@PostMapping("/accessLevelBatchUpdate")
	public Object execute(
			AccessLevelBatchUpdateForm form,
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
		session.removeAttribute("aclBatchUpdateFlag");
		session.removeAttribute("accessLevelBatchUpdateErrors");
		session.setAttribute("errors", errors);

		//ActionMessages errors = new ActionMessages();
		//MessageResources resources = getResources(request);

		if (user.getAclBatchUpdateFlag() == null || user.getAclBatchUpdateFlag().length() <= 0) {
			// アクセスレベル一括更新ツールの使用権限なし
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "アクセスレベル一括更新画面" }, null));
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "noPermission";
		}

		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		AccessLevelBatchUpdateForm accessLevelBatchUpdateForm = form;
		if ("init".equals(request.getParameter("act"))) {
			accessLevelBatchUpdateForm.setAct("init");
		}
		accessLevelBatchUpdateForm.clearErrorMsg();

		if ("init".equals(accessLevelBatchUpdateForm.getAct())) {
			//			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			//			if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
			//				setFormUploadData(accessLevelBatchUpdateForm, drasapInfo, user, errors);
			//				// エラー確認
			//				if (!Objects.isNull(errors.getAttribute("message"))) {
			//					session.setAttribute("errors", errors);
			//					//saveErrors(session, errors);
			//					session.setAttribute("accessLevelBatchUpdate.erros", "1");
			//					return "error";
			//				}
			//			}
			return "init";

		}
		if ("upload".equals(accessLevelBatchUpdateForm.getAct())) {
			doUpload(accessLevelBatchUpdateForm, drasapInfo, user, errors);
			// エラー確認
			if (!Objects.isNull(errors.getAttribute("message"))) {
				session.setAttribute("errors", errors);
				//session.setAttribute("accessLevelBatchUpdate.erros", "1");
				session.setAttribute("accessLevelBatchUpdateErrors", "1");
				return "error";
			}
			session.setAttribute("aclBatchUpdateFlag", "true");
			session.setAttribute("accessLevelBatchUpdateForm", accessLevelBatchUpdateForm);
			return "upload";

		}
		if ("update".equals(accessLevelBatchUpdateForm.getAct())) {
			accessLevelBatchUpdateForm = (AccessLevelBatchUpdateForm) session.getAttribute("accessLevelBatchUpdateForm");
			doUpdate(accessLevelBatchUpdateForm, user, errors);
			// エラー確認
			if (!Objects.isNull(errors.getAttribute("message"))) {
				session.setAttribute("errors", errors);
				//session.setAttribute("accessLevelBatchUpdate.erros", "1");
				session.setAttribute("accessLevelBatchUpdateErrors", "1");
				return "error";
			}

			// 完了メッセージ設定
			//ActionMessages info = new ActionMessages();
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.success", null, null));
			//saveMessages(session, info);
			session.setAttribute("info", errors);
			session.setAttribute("accessLevelBatchUpdate.info", "1");

			return "update";

		}
		if ("close".equals(accessLevelBatchUpdateForm.getAct())) {
			doClose(accessLevelBatchUpdateForm, user);
			session.removeAttribute("accessLevelBatchUpdateForm");
			//			MessageSourceUtil.addAttribute(errors, "message", null);
			session.removeAttribute("errors");
			MessageSourceUtil.addAttribute(errors, "message", null);
			// Ajaxリクエストの為、フォワードさせない
			return new ResponseEntity<>(HttpStatus.OK);
		}

		if (category.isDebugEnabled()) {
			category.debug("end");
		}
		//		session.setAttribute("accessLevelBatchUpdateForm", accessLevelBatchUpdateForm);
		//		return "init";
		accessLevelBatchUpdateForm = (AccessLevelBatchUpdateForm) session.getAttribute("accessLevelBatchUpdateForm");
		String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
		if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
			setFormUploadData(accessLevelBatchUpdateForm, drasapInfo, user, errors);
			// エラー確認
			if (!Objects.isNull(errors.getAttribute("message"))) {
				session.setAttribute("errors", errors);
				//saveErrors(session, errors);
				//session.setAttribute("accessLevelBatchUpdate.erros", "1");
				session.setAttribute("accessLevelBatchUpdateErrors", "1");
				return "error";
			}
		}
		return "update";
	}

	// 画面表示データ取得
	/**
	 * アップロードデータを取得し、AccessLevelBatchUpdateFormにセットする。
	 * 表示項目
	 * @param accessLevelBatchUpdateForm
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void setFormUploadData(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, DrasapInfo drasapInfo,
			User user, Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("表示データ取得処理の開始");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();
			// 品番数
			accessLevelBatchUpdateForm.setItemNoCount(
					AclUploadDB.getAclUploadCount(accessLevelBatchUpdateForm.getAclUpdateNo(), conn));
			// アップロードデータ取得
			accessLevelBatchUpdateForm.setUploadList(
					AclUploadDB.getAclUploadDispList(accessLevelBatchUpdateForm.getAclUpdateNo(), drasapInfo, conn));

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.init.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.init.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (category.isDebugEnabled()) {
			category.debug("表示データ取得処理の終了");
		}
	}

	// アップロード
	/**
	 * アップロードファイルを取得し、ACLアップロードデータテーブルに登録する。
	 * @param accessLevelBatchUpdateForm
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doUpload(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, DrasapInfo drasapInfo,
			User user, Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("アップロード処理の開始");
		}
		//
		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		// アップロードファイル取得
		MultipartFile aclUploadFile = accessLevelBatchUpdateForm.getUploadFile();
		//String fileName = aclUploadFile.getName();
		String fileName = aclUploadFile.getOriginalFilename();
		String sheetName = drasapProperties.getProperty("tyk.upload.file.sheet.name"); // シート名

		int firstRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.start.row.num")); // 開始行
		int firstColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.start.column.num")); // 開始列
		int skipCount = 0; // スキップ行数カウンタ
		int skipRowsMax = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.skip.rows.max")); // 最大スキップ行数

		String aclUpdateType = drasapProperties.getProperty("tyk.upload.aclupdateno.type.batchUpdate"); // ACL更新種別

		String lfileName = fileName.toLowerCase();
		if (lfileName == null || lfileName.length() <= 0) {
			// アップロードファイルが入力されていない場合
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.required.file", null, null));
			return;
		}
		if (!lfileName.endsWith("xls") && !lfileName.endsWith("xlsx")) {
			// アップロードファイルがExcelファイルでない場合
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.file.format", null, null));
			return;
		}

		Connection conn = null;
		BufferedInputStream in = null;
		try {
			conn = ds.getConnection();

			// 古い日付のデータが残っていれば削除
			long count = AclUploadDB.deleteAclUpload(aclUpdateType, user, conn);
			if (category.isDebugEnabled()) {
				category.debug("ACLアップロードデータテーブル削除:" + count + "件 (旧データ)");
			}
			// 前回アップロードデータがあれば、削除
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
				count = AclUploadDB.deleteAclUpload(aclUpdateNo, conn);

				if (category.isDebugEnabled()) {
					category.debug("ACLアップロードデータテーブル削除:" + count + "件 (管理NO=" + aclUpdateNo + ")");
				}
			}

			// ACLアップロードデータのリスト
			ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

			// 管理NO採番 [A(アクセスレベル一括更新)|B(アクセスレベル変更) + yymmdd + 3桁連番]
			aclUpdateNo = AclUpdateNoSequenceDB.getAclUpdateNo(aclUpdateType, conn);
			accessLevelBatchUpdateForm.setAclUpdateNo(aclUpdateNo);
			if (category.isDebugEnabled()) {
				category.debug("管理NO採番:" + aclUpdateNo);
			}

			in = new BufferedInputStream(aclUploadFile.getInputStream());
			Workbook book = WorkbookFactory.create(in); // Book.
			Sheet sheet = book.getSheet(sheetName); // Sheet.
			Row row = null; // Row.
			FormulaEvaluator evaluator = book.getCreationHelper().createFormulaEvaluator();

			AclUpload aclUploadXls = null;

			// Excelデータ取得
			for (int i = firstRowNum; i <= sheet.getLastRowNum(); i++) {

				if (skipCount > skipRowsMax) {
					// スキップ数が最大を超えた場合、終了
					break;
				}

				row = sheet.getRow(i); // row.
				if (row == null) {
					skipCount++;
					continue;
				}

				// アップロード用インスタンス生成
				aclUploadXls = createAclUpload(row, firstColumnNum, evaluator,
						aclUpdateNo, drasapInfo, user, conn);

				// スキップ判定
				if (aclUploadXls != null) {
					// 入力ありの場合、行データを追加
					aclUploadXls.setRecordNo(String.valueOf(aclUploadList.size() + 1)); // レコード番号
					aclUploadList.add(aclUploadXls);
					// スキップカウンタをリセット
					skipCount = 0;
				} else {
					skipCount++;
				}
			}
			// アップロード
			long resultCount = 0;
			HashSet<String> itemNoShortSet = new HashSet<String>();
			for (AclUpload aclUpload : aclUploadList) {

				// アップロードデータのチェック実行
				checkUploadData(aclUpload, itemNoShortSet, conn);
				// ACLアップロードデータ登録
				resultCount += AclUploadDB.insertAclUpload(aclUpload, conn);
				itemNoShortSet.add(aclUpload.getItemNoShort()); // 重複チェック用に品番を追加
			}
			// アップロードデータのチェック実行（1物2品番）
			resultCount += checkUploadData(aclUpdateNo, conn);

			if (category.isDebugEnabled()) {
				category.debug("ACLアップロードデータテーブル登録:" + resultCount + "件");
				category.debug("アップロード処理の終了");
			}
		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.upload.failed", new Object[] { ErrorUtility.error2String(e) }, null));
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
		}
	}

	/**
	 * アップロードデータからACLアップロードのインスタンスを生成する。
	 * 対象レコードが空の場合、nullを返す。
	 * @param row
	 * @param columnIndex
	 * @param evaluator Excel関数値の取得用オブジェクト
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param user
	 * @param conn
	 */
	private AclUpload createAclUpload(Row row, int columnIndex, FormulaEvaluator evaluator,
			String aclUpdateNo, DrasapInfo drasapInfo, User user, Connection conn) throws Exception {

		// アップロード用インスタンス生成
		AclUpload aclUpload = new AclUpload();
		aclUpload.setAclUpdateNo(aclUpdateNo); // 管理NO
		aclUpload.setUserId(user.getId()); // ユーザID
		aclUpload.setUserName(user.getName()); // 氏名

		int columnCount = 0; // カラム設定済みカウンタ

		String value = getStringCellValue(row, columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineJp(StringCheck.trimWsp(value)); // 装置
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineNo(StringCheck.trimWsp(value)); // 装置NO
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setDrwgNo(StringCheck.trimWsp(value)); // 手配図番
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineCode(StringCheck.trimWsp(value)); // 装置コード
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setDetailNo(StringCheck.trimWsp(value)); // 明細番号
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setPages(StringCheck.trimWsp(value)); // 頁
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setItemNo(StringCheck.trimWsp(value)); // 品番
			columnCount++;

			// trim処理。ハイフン「-」を除く。
			String itemNoShort = StringCheck.trimWsp(value).replace("-", "");
			// 半角大文字に変換する
			itemNoShort = StringCheck.changeDbToSbAscii(itemNoShort).toUpperCase();
			aclUpload.setItemNoShort(itemNoShort); // 品番（空白、ハイフン「-」を除いた半角大文字）
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null
				&& StringCheck.trimWsp(value).length() > 0) {
			if (drasapInfo.getCorrespondingValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setCorrespondingFlag("1"); // 該当図: 該当
			}
			columnCount++;
		} else {
			aclUpload.setCorrespondingFlag("0"); // 該当図: 非該当
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null
				&& StringCheck.trimWsp(value).length() > 0) {
			if (drasapInfo.getConfidentialValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setConfidentialFlag("2"); // 機密管理図: 秘
			} else if (drasapInfo.getStrictlyConfidentialValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setConfidentialFlag("3"); // 機密管理図: 極秘
			}
			columnCount++;
		} else {
			aclUpload.setConfidentialFlag("1"); // 機密管理図: 関係者外秘
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setGrpCode(StringCheck.trimWsp(value)); // グループ
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setItemName(StringCheck.trimWsp(value)); // 品名（規格型式）
			columnCount++;
		}

		// 入力ありの場合
		if (columnCount > 0) {
			HashMap<String, String> accessLevelMap = null;

			// 変更前ACL情報取得
			accessLevelMap = AclvMasterDB.getPreUpdateAcl(
					aclUpload.getItemNoShort(), conn);
			if (accessLevelMap.size() > 0) {
				aclUpload.setPreUpdateAcl(accessLevelMap.get("ACL_ID")); // 変更前アクセスレベル
				aclUpload.setPreUpdateAclName(accessLevelMap.get("ACL_NAME")); // 変更前アクセスレベル名
			}
			// 変更後ACL情報取得
			accessLevelMap = AclvMasterDB.getPostUpdateAcl(
					aclUpload.getGrpCode(), aclUpload.getCorrespondingFlag(), aclUpload.getConfidentialFlag(), conn);
			if (accessLevelMap.size() > 0) {
				aclUpload.setPostUpdateAcl(accessLevelMap.get("ACL_ID")); // 変更後アクセスレベル
				aclUpload.setPostUpdateAclName(accessLevelMap.get("ACL_NAME")); // 変更後アクセスレベル名
			}
			return aclUpload;
		}
		return null;
	}

	/**
	 * Excelデータ取得。セルの値を文字列で返す。
	 * 数値の場合は整数で扱う。
	 * @param row
	 * @param columnIndex
	 * @param evaluator Excel関数値の取得用オブジェクト
	 */
	private String getStringCellValue(Row row, int columnIndex, FormulaEvaluator evaluator) {

		String value = null;
		Cell cell = row.getCell(columnIndex); // cell.
		CellValue cellValue = null; // cell.(evaluated)

		if (cell == null) {
			return null;
		}

		// セル値を文字列で返す
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				// 日付（yyyy/MM/dd）
				value = new SimpleDateFormat("yyyy/MM/dd").format(cell
						.getDateCellValue());
			} else {
				// 数値
				value = String.valueOf((long) cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;

		case Cell.CELL_TYPE_FORMULA: {
			// 計算式の結果を取得
			cellValue = evaluator.evaluate(cell);

			// 関数値を文字列で返す
			switch (cellValue.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cellValue.getBooleanValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// 関数のセルが日付形式か
				if (DateUtil.isCellDateFormatted(cell)) {
					// 日付（yyyy/MM/dd）
					value = new SimpleDateFormat("yyyy/MM/dd")
							.format(cellValue.getNumberValue());
				} else {
					// 数値
					value = String.valueOf((long) cellValue.getNumberValue());
				}
				break;
			case Cell.CELL_TYPE_STRING:
				value = cellValue.getStringValue();
				break;
			default:
				break;
			}
			break;
		}
		case Cell.CELL_TYPE_BLANK:
		case Cell.CELL_TYPE_ERROR:
		default:
			break;
		}
		return value;
	}

	/**
	 * アップロードデータのチェック。
	 * チェックでエラーとなった項目は、ACLアップロードのメッセージ項目にエラー情報を登録し、画面に表示する。
	 * @param aclUpload
	 * @param itemNoShortSet
	 * @param resources
	 * @param conn
	 */
	private void checkUploadData(AclUpload aclUpload, HashSet<String> itemNoShortSet,
			Connection conn) throws Exception {

		TableInfo tableInfo = TableInfoDB.getTableInfoArray("ACL_UPLOAD_TABLE", conn);

		String value = null;
		int length = 0;
		// 入力値チェック

		value = aclUpload.getItemNo();
		// 必須（品番）
		if (value == null || value.length() <= 0) {
			aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "品番が未入力です" }, null));
			// 必須（グループ）
		} else if ((value = aclUpload.getGrpCode()) == null || value.length() <= 0) {
			aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "グループが未入力です" }, null));
		}

		value = aclUpload.getMachineJp();
		// 桁数（装置）
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_JP").getData_length())) {
			aclUpload.setMachineJp(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "装置の入力データが長すきます" }, null));
			}
		}
		value = aclUpload.getMachineNo();
		// 桁数（装置NO）
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_NO").getData_length())) {
			aclUpload.setMachineNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "装置NOの入力データが長すきます" }, null));
			}
		}
		value = aclUpload.getDrwgNo();
		// 桁数（手配図番）
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("DRWG_NO").getData_length())) {
			aclUpload.setDrwgNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "手配図番の入力データが長すきます" }, null));
			}
		}
		// 桁数（装置コード）
		value = aclUpload.getMachineCode();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_CODE").getData_length())) {
			aclUpload.setMachineCode(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "装置コードの入力データが長すきます" }, null));
			}
		}
		// 桁数（明細番号）
		value = aclUpload.getDetailNo();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("DETAIL_NO").getData_length())) {
			aclUpload.setDetailNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "明細番号の入力データが長すきます" }, null));
			}
		}
		// 桁数（頁）
		value = aclUpload.getPages();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("PAGES").getData_length())) {
			aclUpload.setPages(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "頁の入力データが長すきます" }, null));
			}
		}
		// 桁数（品番）
		value = aclUpload.getItemNo();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("ITEM_NO").getData_length())) {
			aclUpload.setItemNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "品番の入力データが長すきます" }, null));
			}
		}
		// 桁数（グループ）
		value = aclUpload.getGrpCode();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("GRP_CODE").getData_length())) {
			aclUpload.setGrpCode(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "グループの入力データが長すきます" }, null));
			}
		}
		// 桁数（品名(規格型式)）
		value = aclUpload.getItemName();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("ITEM_NAME").getData_length())) {
			aclUpload.setItemName(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// メッセージ
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "品名(規格型式)の入力データが長すきます" }, null));
			}
		}

		// ACL変更チェック
		if (aclUpload.getPreUpdateAcl() != null && aclUpload.getPostUpdateAcl() != null) {
			if (aclUpload.getPreUpdateAcl().equals(aclUpload.getPostUpdateAcl())) {
				// メッセージ
				if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
					aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "アクセスレベルが変更されていません" }, null));
				}
			}
		}

		if (aclUpload.getMessage() != null && aclUpload.getMessage().length() > 0) {
			// 入力エラーがあれば、処理終了
			return;
		}

		// 図番登録済みチェック
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			if (getIndexDbCount(aclUpload.getItemNoShort(), conn) <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.drwgNo", null, null));
			}
		}
		// 変更後ACL存在チェック
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			value = aclUpload.getPostUpdateAcl();
			if (value == null || value.length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.acl", new Object[] { "変更後" }, null));
			}
		}
		// 重複チェック（データ内の同一複数図番）
		if (itemNoShortSet.contains(aclUpload.getItemNoShort())) {
			long count = AclUploadDB.getItemNoAclCount(
					aclUpload.getAclUpdateNo(), aclUpload.getItemNoShort(), aclUpload.getPreUpdateAcl(), aclUpload.getPostUpdateAcl(), conn);
			if (count <= 0) {
				if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
					aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.notequal.overlapped.drwgNo.acl", null, null));
				}
				AclUploadDB.updateMessage(aclUpload.getAclUpdateNo(), messageSource.getMessage("system.aclBatchUpdate.upload.notequal.overlapped.drwgNo.acl", null, null), conn, aclUpload.getItemNoShort());
			}
		}
		// 変更前後ACL判断チェック（ACL変更可否判断マスタ参照）
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			if (AclUpdatableConditionMasterDB.getAclUpdatableConditionCount(
					aclUpload.getPreUpdateAcl(), aclUpload.getPostUpdateAcl(), conn) <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nopermission.update.acl", null, null));
			}
		}
	}

	/**
	 * アップロードデータのチェック
	 * チェックでエラーとなった項目は、ACLアップロードのメッセージにエラー情報を登録し、画面に表示する。
	 * @param aclUpdateNo
	 * @param resources
	 * @param conn
	 */
	private long checkUploadData(String aclUpdateNo, Connection conn)
			throws Exception {

		long resultCount = 0;

		// 1物2品番情報取得
		ArrayList<HashMap<String, String>> drwgNoMapList = AclUploadDB.getDrwgNoMapList(aclUpdateNo, conn);

		String twinDrwgNo = null;
		String value = null;
		boolean createNew = false;
		for (HashMap<String, String> drwgNoMap : drwgNoMapList) {
			value = drwgNoMap.get("MESSAGE");
			// 図番エラーチェック
			if (value != null && value.length() > 0) {
				// 処理終了。次の図番へ
				continue;

				// 1物2品番整合性チェック
			}
			value = drwgNoMap.get("ID1_DRWG_NO");
			if (value == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID1_TWIN_DRWG_NO")) == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID2_DRWG_NO")) == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID2_TWIN_DRWG_NO")) == null || value.length() <= 0) {
				AclUploadDB.updateMessage(aclUpdateNo, messageSource.getMessage("system.aclBatchUpdate.upload.nodata.twinDrwgNo", null, null), conn, drwgNoMap.get("ITEM_NO_SHORT"));

				// 1物2品番ACLチェック
			} else {

				// 対応図番がない場合、1物2品番対応図番をACLアップロードデータテーブルに追加
				twinDrwgNo = null;
				createNew = false;
				value = drwgNoMap.get("TWIN_DRWG_NO");
				if (value == null || value.length() <= 0) {

					value = drwgNoMap.get("ID1_TWIN_DRWG_NO");
					if (value != null && value.length() > 0) {
						// 属性情報．図番 ⇒ 1物2品番対応図番を取得し、コピー登録
						twinDrwgNo = value;
					} else {
						// 属性情報．1物2品番対応図番 ⇒ 図番を取得し、コピー登録
						twinDrwgNo = drwgNoMap.get("ID2_DRWG_NO");
					}
					AclUploadDB.insertSelectTwinDrwgNo(aclUpdateNo, drwgNoMap.get("ITEM_NO_SHORT"), conn);
					createNew = true; // 対応図番を追加作成
				} else {
					twinDrwgNo = drwgNoMap.get("TWIN_DRWG_NO");
				}

				// 変更前アクセスレベル比較
				if (!drwgNoMap.get("ID1_PRE_UPDATE_ACL").equals(drwgNoMap.get("ID2_PRE_UPDATE_ACL")) || !createNew && !drwgNoMap.get("POST_UPDATE_ACL").equals(drwgNoMap.get("TWIN_POST_UPDATE_ACL"))) {
					AclUploadDB.updateMessage(aclUpdateNo, messageSource.getMessage("system.aclBatchUpdate.upload.notequal.twin.drwgNo.acl", null, null), conn, drwgNoMap.get("ITEM_NO_SHORT"), twinDrwgNo);

					// 変更後アクセスレベル比較（※追加作成時はコピー登録なのでチェックしない）
				}
			}
		}
		return resultCount;
	}

	/**
	 * 図番登録済みチェック用に、属性情報テーブルから引数の図番の件数を返す。
	 * @param drwgNo
	 * @param conn
	 */
	private long getIndexDbCount(String drwgNo, Connection conn) throws Exception {
		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(DRWG_NO) as COUNT from INDEX_DB where DRWG_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, drwgNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// ACLアップロードデータ件数取得
				count = rs.getLong("COUNT");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// CLOSE処理
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return count;
	}

	// 更新
	/**
	 * ACLアップロードデータテーブルの内容で、属性情報テーブルの図面アクセスレベルを更新する。
	 * @param accessLevelBatchUpdateForm
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doUpdate(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, User user,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("更新処理の開始");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();

			if (Objects.isNull(accessLevelBatchUpdateForm)) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.nodata", null, null));
				return;
			}
			// ACLアップロードデータ取得
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			ArrayList<AclUpload> aclUploadList = AclUploadDB.getDistinctAclUploadList(aclUpdateNo, conn);

			if (aclUploadList.size() <= 0) {
				// 更新可能なアップロードデータが0件の場合
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.nodata", null, null));
				return;
			}

			// 属性情報テーブルを更新
			long count = updateIndexDb(aclUploadList, user, errors, conn);
			if (category.isDebugEnabled()) {
				category.debug("属性情報テーブル更新:" + count + "件");
			}

		} catch (Exception e) {
			// for ユーザー
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (category.isDebugEnabled()) {
			category.debug("更新処理の終了");
		}
	}

	/**
	 * アップロードデータの内容で、属性情報テーブルの図面アクセスレベルを更新する。
	 * ACL一括更新はアップロードデータの事前チェックでエラーとなっていないものが対象となる。
	 * @return aclUploadList
	 * @param user
	 * @param erros
	 * @param resources
	 * @param conn
	 */
	private long updateIndexDb(ArrayList<AclUpload> aclUploadList, User user,
			Model errors, Connection conn) throws Exception {

		long count = 0;

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;
		try {
			// 更新時チェック用SQL
			String sql1 = "select count(DRWG_NO) as COUNT from INDEX_DB where DRWG_NO = ? and ACL_ID = ?";
			pstmt1 = conn.prepareStatement(sql1);
			// 更新用SQL
			String sql2 = "update INDEX_DB set ACL_ID = ?, ACL_UPDATE = ?, ACL_EMPNO = ?, ACL_NAME = ? " +
					"where DRWG_NO = ?";
			pstmt2 = conn.prepareStatement(sql2);
			// ACLアップロードデータテーブル更新用SQL
			String sql3 = "update ACL_UPLOAD_TABLE set ACL_UPDATE = ? where " +
					"ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and MESSAGE is null";
			pstmt3 = conn.prepareStatement(sql3);

			for (AclUpload aclUpload : aclUploadList) {
				try {
					conn.setAutoCommit(false);

					// 更新時チェック（排他）
					pstmt1.setString(1, aclUpload.getItemNoShort());
					pstmt1.setString(2, aclUpload.getPreUpdateAcl());
					rs = pstmt1.executeQuery();

					if (rs.next()) {
						if (aclUpload.getMessage() != null && aclUpload.getMessage().length() > 0) {
							// ACLアップロードデータテーブルにエラーメッセージが設定されている場合、更新処理をしないでlogへ書き出し
							ChangeAclLogger.logging(user, aclUpload); // ACL変更ログ
							// ロールバック
							conn.rollback();
							continue;
						}
						if (rs.getLong("COUNT") <= 0) {
							// 別トランザクションによって図番ACLが更新済みの場合、エラー。
							aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.update.excusive.acl", null, null));
							// 更新処理をしないでlogへ書き出し
							ChangeAclLogger.logging(user, aclUpload); // ACL変更ログ
							// ロールバック
							conn.rollback();
							continue;
						}
					}
					// アクセスレベル変更日時を取得
					aclUpload.setAclUpdate(new Date());

					// 図番ACL更新
					pstmt2.setString(1, aclUpload.getPostUpdateAcl());
					pstmt2.setTimestamp(2, new Timestamp(aclUpload.getAclUpdate().getTime()));
					pstmt2.setString(3, aclUpload.getUserId());
					pstmt2.setString(4, aclUpload.getUserName());
					pstmt2.setString(5, aclUpload.getItemNoShort());
					count += pstmt2.executeUpdate();

					// ACLアップロードデータテーブルを更新（更新日時）
					pstmt3.setTimestamp(1, new Timestamp(aclUpload.getAclUpdate().getTime()));
					pstmt3.setString(2, aclUpload.getAclUpdateNo());
					pstmt3.setString(3, aclUpload.getItemNoShort());
					pstmt3.executeUpdate();

					// コミット
					conn.commit();

					// 更新結果をログへ書き出し
					ChangeAclLogger.logging(user, aclUpload); // ACL変更ログ

				} catch (Exception e) {
					try {
						// ロールバック
						conn.rollback();
					} catch (Exception e2) {
					}

					// for ユーザー
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
					// for システム管理者
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
					// for MUR
					if (category.isInfoEnabled()) {
						category.error(messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
					}

					aclUpload.setMessage(e.getMessage()); // ACL変更ログ
					// 更新処理をしないでlogへ書き出し
					ChangeAclLogger.logging(user, aclUpload);
				}
			}
		} catch (Exception e) {
			// ロールバック
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			// CLOSE処理
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			try {
				pstmt2.close();
			} catch (Exception e) {
			}
			try {
				pstmt3.close();
			} catch (Exception e) {
			}
		}
		return count;
	}

	// Close
	/**
	 * 画面クローズ時に、既存ACLアップロードデータがあればクリアする。
	 * @param accessLevelBatchUpdateForm
	 * @param user
	 */
	private void doClose(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, User user) {

		if (category.isDebugEnabled()) {
			category.debug("クローズ処理の開始");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();

			// 前回アップロードデータがあれば、削除
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
				long count = AclUploadDB.deleteAclUpload(aclUpdateNo, conn);
				if (category.isDebugEnabled()) {
					category.debug("ACLアップロードデータテーブル削除:" + count + "件");
				}
			}
		} catch (Exception e) {
			// for システム管理者
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error("ACLアップロードデータテーブルの削除に失敗(" + ErrorUtility.error2String(e) + ")");
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (category.isDebugEnabled()) {
			category.debug("クローズ処理の終了");
		}
	}
}
