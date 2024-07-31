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
 * �A�N�Z�X���x���ꊇ�X�V�����A�N�V����
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
			// �A�N�Z�X���x���ꊇ�X�V�c�[���̎g�p�����Ȃ�
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "�A�N�Z�X���x���ꊇ�X�V���" }, null));
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
			//				// �G���[�m�F
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
			// �G���[�m�F
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
			// �G���[�m�F
			if (!Objects.isNull(errors.getAttribute("message"))) {
				session.setAttribute("errors", errors);
				//session.setAttribute("accessLevelBatchUpdate.erros", "1");
				session.setAttribute("accessLevelBatchUpdateErrors", "1");
				return "error";
			}

			// �������b�Z�[�W�ݒ�
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
			// Ajax���N�G�X�g�ׁ̈A�t�H���[�h�����Ȃ�
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
			// �G���[�m�F
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

	// ��ʕ\���f�[�^�擾
	/**
	 * �A�b�v���[�h�f�[�^���擾���AAccessLevelBatchUpdateForm�ɃZ�b�g����B
	 * �\������
	 * @param accessLevelBatchUpdateForm
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void setFormUploadData(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, DrasapInfo drasapInfo,
			User user, Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("�\���f�[�^�擾�����̊J�n");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();
			// �i�Ԑ�
			accessLevelBatchUpdateForm.setItemNoCount(
					AclUploadDB.getAclUploadCount(accessLevelBatchUpdateForm.getAclUpdateNo(), conn));
			// �A�b�v���[�h�f�[�^�擾
			accessLevelBatchUpdateForm.setUploadList(
					AclUploadDB.getAclUploadDispList(accessLevelBatchUpdateForm.getAclUpdateNo(), drasapInfo, conn));

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.init.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for �V�X�e���Ǘ���
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
			category.debug("�\���f�[�^�擾�����̏I��");
		}
	}

	// �A�b�v���[�h
	/**
	 * �A�b�v���[�h�t�@�C�����擾���AACL�A�b�v���[�h�f�[�^�e�[�u���ɓo�^����B
	 * @param accessLevelBatchUpdateForm
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doUpload(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, DrasapInfo drasapInfo,
			User user, Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("�A�b�v���[�h�����̊J�n");
		}
		//
		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		// �A�b�v���[�h�t�@�C���擾
		MultipartFile aclUploadFile = accessLevelBatchUpdateForm.getUploadFile();
		//String fileName = aclUploadFile.getName();
		String fileName = aclUploadFile.getOriginalFilename();
		String sheetName = drasapProperties.getProperty("tyk.upload.file.sheet.name"); // �V�[�g��

		int firstRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.start.row.num")); // �J�n�s
		int firstColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.start.column.num")); // �J�n��
		int skipCount = 0; // �X�L�b�v�s���J�E���^
		int skipRowsMax = Integer.parseInt(drasapProperties.getProperty("tyk.upload.file.skip.rows.max")); // �ő�X�L�b�v�s��

		String aclUpdateType = drasapProperties.getProperty("tyk.upload.aclupdateno.type.batchUpdate"); // ACL�X�V���

		String lfileName = fileName.toLowerCase();
		if (lfileName == null || lfileName.length() <= 0) {
			// �A�b�v���[�h�t�@�C�������͂���Ă��Ȃ��ꍇ
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.required.file", null, null));
			return;
		}
		if (!lfileName.endsWith("xls") && !lfileName.endsWith("xlsx")) {
			// �A�b�v���[�h�t�@�C����Excel�t�@�C���łȂ��ꍇ
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.file.format", null, null));
			return;
		}

		Connection conn = null;
		BufferedInputStream in = null;
		try {
			conn = ds.getConnection();

			// �Â����t�̃f�[�^���c���Ă���΍폜
			long count = AclUploadDB.deleteAclUpload(aclUpdateType, user, conn);
			if (category.isDebugEnabled()) {
				category.debug("ACL�A�b�v���[�h�f�[�^�e�[�u���폜:" + count + "�� (���f�[�^)");
			}
			// �O��A�b�v���[�h�f�[�^������΁A�폜
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
				count = AclUploadDB.deleteAclUpload(aclUpdateNo, conn);

				if (category.isDebugEnabled()) {
					category.debug("ACL�A�b�v���[�h�f�[�^�e�[�u���폜:" + count + "�� (�Ǘ�NO=" + aclUpdateNo + ")");
				}
			}

			// ACL�A�b�v���[�h�f�[�^�̃��X�g
			ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

			// �Ǘ�NO�̔� [A(�A�N�Z�X���x���ꊇ�X�V)|B(�A�N�Z�X���x���ύX) + yymmdd + 3���A��]
			aclUpdateNo = AclUpdateNoSequenceDB.getAclUpdateNo(aclUpdateType, conn);
			accessLevelBatchUpdateForm.setAclUpdateNo(aclUpdateNo);
			if (category.isDebugEnabled()) {
				category.debug("�Ǘ�NO�̔�:" + aclUpdateNo);
			}

			in = new BufferedInputStream(aclUploadFile.getInputStream());
			Workbook book = WorkbookFactory.create(in); // Book.
			Sheet sheet = book.getSheet(sheetName); // Sheet.
			Row row = null; // Row.
			FormulaEvaluator evaluator = book.getCreationHelper().createFormulaEvaluator();

			AclUpload aclUploadXls = null;

			// Excel�f�[�^�擾
			for (int i = firstRowNum; i <= sheet.getLastRowNum(); i++) {

				if (skipCount > skipRowsMax) {
					// �X�L�b�v�����ő�𒴂����ꍇ�A�I��
					break;
				}

				row = sheet.getRow(i); // row.
				if (row == null) {
					skipCount++;
					continue;
				}

				// �A�b�v���[�h�p�C���X�^���X����
				aclUploadXls = createAclUpload(row, firstColumnNum, evaluator,
						aclUpdateNo, drasapInfo, user, conn);

				// �X�L�b�v����
				if (aclUploadXls != null) {
					// ���͂���̏ꍇ�A�s�f�[�^��ǉ�
					aclUploadXls.setRecordNo(String.valueOf(aclUploadList.size() + 1)); // ���R�[�h�ԍ�
					aclUploadList.add(aclUploadXls);
					// �X�L�b�v�J�E���^�����Z�b�g
					skipCount = 0;
				} else {
					skipCount++;
				}
			}
			// �A�b�v���[�h
			long resultCount = 0;
			HashSet<String> itemNoShortSet = new HashSet<String>();
			for (AclUpload aclUpload : aclUploadList) {

				// �A�b�v���[�h�f�[�^�̃`�F�b�N���s
				checkUploadData(aclUpload, itemNoShortSet, conn);
				// ACL�A�b�v���[�h�f�[�^�o�^
				resultCount += AclUploadDB.insertAclUpload(aclUpload, conn);
				itemNoShortSet.add(aclUpload.getItemNoShort()); // �d���`�F�b�N�p�ɕi�Ԃ�ǉ�
			}
			// �A�b�v���[�h�f�[�^�̃`�F�b�N���s�i1��2�i�ԁj
			resultCount += checkUploadData(aclUpdateNo, conn);

			if (category.isDebugEnabled()) {
				category.debug("ACL�A�b�v���[�h�f�[�^�e�[�u���o�^:" + resultCount + "��");
				category.debug("�A�b�v���[�h�����̏I��");
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.upload.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.upload.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			// CLOSE����
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
	 * �A�b�v���[�h�f�[�^����ACL�A�b�v���[�h�̃C���X�^���X�𐶐�����B
	 * �Ώۃ��R�[�h����̏ꍇ�Anull��Ԃ��B
	 * @param row
	 * @param columnIndex
	 * @param evaluator Excel�֐��l�̎擾�p�I�u�W�F�N�g
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param user
	 * @param conn
	 */
	private AclUpload createAclUpload(Row row, int columnIndex, FormulaEvaluator evaluator,
			String aclUpdateNo, DrasapInfo drasapInfo, User user, Connection conn) throws Exception {

		// �A�b�v���[�h�p�C���X�^���X����
		AclUpload aclUpload = new AclUpload();
		aclUpload.setAclUpdateNo(aclUpdateNo); // �Ǘ�NO
		aclUpload.setUserId(user.getId()); // ���[�UID
		aclUpload.setUserName(user.getName()); // ����

		int columnCount = 0; // �J�����ݒ�ς݃J�E���^

		String value = getStringCellValue(row, columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineJp(StringCheck.trimWsp(value)); // ���u
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineNo(StringCheck.trimWsp(value)); // ���uNO
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setDrwgNo(StringCheck.trimWsp(value)); // ��z�}��
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setMachineCode(StringCheck.trimWsp(value)); // ���u�R�[�h
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setDetailNo(StringCheck.trimWsp(value)); // ���הԍ�
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setPages(StringCheck.trimWsp(value)); // ��
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setItemNo(StringCheck.trimWsp(value)); // �i��
			columnCount++;

			// trim�����B�n�C�t���u-�v�������B
			String itemNoShort = StringCheck.trimWsp(value).replace("-", "");
			// ���p�啶���ɕϊ�����
			itemNoShort = StringCheck.changeDbToSbAscii(itemNoShort).toUpperCase();
			aclUpload.setItemNoShort(itemNoShort); // �i�ԁi�󔒁A�n�C�t���u-�v�����������p�啶���j
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null
				&& StringCheck.trimWsp(value).length() > 0) {
			if (drasapInfo.getCorrespondingValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setCorrespondingFlag("1"); // �Y���}: �Y��
			}
			columnCount++;
		} else {
			aclUpload.setCorrespondingFlag("0"); // �Y���}: ��Y��
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null
				&& StringCheck.trimWsp(value).length() > 0) {
			if (drasapInfo.getConfidentialValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setConfidentialFlag("2"); // �@���Ǘ��}: ��
			} else if (drasapInfo.getStrictlyConfidentialValue().equals(StringCheck.trimWsp(value))) {
				aclUpload.setConfidentialFlag("3"); // �@���Ǘ��}: �ɔ�
			}
			columnCount++;
		} else {
			aclUpload.setConfidentialFlag("1"); // �@���Ǘ��}: �֌W�ҊO��
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setGrpCode(StringCheck.trimWsp(value)); // �O���[�v
			columnCount++;
		}
		value = getStringCellValue(row, ++columnIndex, evaluator);
		if (value != null) {
			aclUpload.setItemName(StringCheck.trimWsp(value)); // �i���i�K�i�^���j
			columnCount++;
		}

		// ���͂���̏ꍇ
		if (columnCount > 0) {
			HashMap<String, String> accessLevelMap = null;

			// �ύX�OACL���擾
			accessLevelMap = AclvMasterDB.getPreUpdateAcl(
					aclUpload.getItemNoShort(), conn);
			if (accessLevelMap.size() > 0) {
				aclUpload.setPreUpdateAcl(accessLevelMap.get("ACL_ID")); // �ύX�O�A�N�Z�X���x��
				aclUpload.setPreUpdateAclName(accessLevelMap.get("ACL_NAME")); // �ύX�O�A�N�Z�X���x����
			}
			// �ύX��ACL���擾
			accessLevelMap = AclvMasterDB.getPostUpdateAcl(
					aclUpload.getGrpCode(), aclUpload.getCorrespondingFlag(), aclUpload.getConfidentialFlag(), conn);
			if (accessLevelMap.size() > 0) {
				aclUpload.setPostUpdateAcl(accessLevelMap.get("ACL_ID")); // �ύX��A�N�Z�X���x��
				aclUpload.setPostUpdateAclName(accessLevelMap.get("ACL_NAME")); // �ύX��A�N�Z�X���x����
			}
			return aclUpload;
		}
		return null;
	}

	/**
	 * Excel�f�[�^�擾�B�Z���̒l�𕶎���ŕԂ��B
	 * ���l�̏ꍇ�͐����ň����B
	 * @param row
	 * @param columnIndex
	 * @param evaluator Excel�֐��l�̎擾�p�I�u�W�F�N�g
	 */
	private String getStringCellValue(Row row, int columnIndex, FormulaEvaluator evaluator) {

		String value = null;
		Cell cell = row.getCell(columnIndex); // cell.
		CellValue cellValue = null; // cell.(evaluated)

		if (cell == null) {
			return null;
		}

		// �Z���l�𕶎���ŕԂ�
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				// ���t�iyyyy/MM/dd�j
				value = new SimpleDateFormat("yyyy/MM/dd").format(cell
						.getDateCellValue());
			} else {
				// ���l
				value = String.valueOf((long) cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;

		case Cell.CELL_TYPE_FORMULA: {
			// �v�Z���̌��ʂ��擾
			cellValue = evaluator.evaluate(cell);

			// �֐��l�𕶎���ŕԂ�
			switch (cellValue.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cellValue.getBooleanValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// �֐��̃Z�������t�`����
				if (DateUtil.isCellDateFormatted(cell)) {
					// ���t�iyyyy/MM/dd�j
					value = new SimpleDateFormat("yyyy/MM/dd")
							.format(cellValue.getNumberValue());
				} else {
					// ���l
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
	 * �A�b�v���[�h�f�[�^�̃`�F�b�N�B
	 * �`�F�b�N�ŃG���[�ƂȂ������ڂ́AACL�A�b�v���[�h�̃��b�Z�[�W���ڂɃG���[����o�^���A��ʂɕ\������B
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
		// ���͒l�`�F�b�N

		value = aclUpload.getItemNo();
		// �K�{�i�i�ԁj
		if (value == null || value.length() <= 0) {
			aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�i�Ԃ������͂ł�" }, null));
			// �K�{�i�O���[�v�j
		} else if ((value = aclUpload.getGrpCode()) == null || value.length() <= 0) {
			aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�O���[�v�������͂ł�" }, null));
		}

		value = aclUpload.getMachineJp();
		// �����i���u�j
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_JP").getData_length())) {
			aclUpload.setMachineJp(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "���u�̓��̓f�[�^���������܂�" }, null));
			}
		}
		value = aclUpload.getMachineNo();
		// �����i���uNO�j
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_NO").getData_length())) {
			aclUpload.setMachineNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "���uNO�̓��̓f�[�^���������܂�" }, null));
			}
		}
		value = aclUpload.getDrwgNo();
		// �����i��z�}�ԁj
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("DRWG_NO").getData_length())) {
			aclUpload.setDrwgNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "��z�}�Ԃ̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i���u�R�[�h�j
		value = aclUpload.getMachineCode();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("MACHINE_CODE").getData_length())) {
			aclUpload.setMachineCode(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "���u�R�[�h�̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i���הԍ��j
		value = aclUpload.getDetailNo();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("DETAIL_NO").getData_length())) {
			aclUpload.setDetailNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "���הԍ��̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i�Łj
		value = aclUpload.getPages();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("PAGES").getData_length())) {
			aclUpload.setPages(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�ł̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i�i�ԁj
		value = aclUpload.getItemNo();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("ITEM_NO").getData_length())) {
			aclUpload.setItemNo(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�i�Ԃ̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i�O���[�v�j
		value = aclUpload.getGrpCode();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("GRP_CODE").getData_length())) {
			aclUpload.setGrpCode(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�O���[�v�̓��̓f�[�^���������܂�" }, null));
			}
		}
		// �����i�i��(�K�i�^��)�j
		value = aclUpload.getItemName();
		if (value != null
				&& value.getBytes(DEFAULT_CHARSET).length > (length = tableInfo.getColInfo("ITEM_NAME").getData_length())) {
			aclUpload.setItemName(new String(value.getBytes(DEFAULT_CHARSET), 0, length, DEFAULT_CHARSET));
			// ���b�Z�[�W
			if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�i��(�K�i�^��)�̓��̓f�[�^���������܂�" }, null));
			}
		}

		// ACL�ύX�`�F�b�N
		if (aclUpload.getPreUpdateAcl() != null && aclUpload.getPostUpdateAcl() != null) {
			if (aclUpload.getPreUpdateAcl().equals(aclUpload.getPostUpdateAcl())) {
				// ���b�Z�[�W
				if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
					aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.required.file.input", new Object[] { "�A�N�Z�X���x�����ύX����Ă��܂���" }, null));
				}
			}
		}

		if (aclUpload.getMessage() != null && aclUpload.getMessage().length() > 0) {
			// ���̓G���[������΁A�����I��
			return;
		}

		// �}�ԓo�^�ς݃`�F�b�N
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			if (getIndexDbCount(aclUpload.getItemNoShort(), conn) <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.drwgNo", null, null));
			}
		}
		// �ύX��ACL���݃`�F�b�N
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			value = aclUpload.getPostUpdateAcl();
			if (value == null || value.length() <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.acl", new Object[] { "�ύX��" }, null));
			}
		}
		// �d���`�F�b�N�i�f�[�^���̓��ꕡ���}�ԁj
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
		// �ύX�O��ACL���f�`�F�b�N�iACL�ύX�۔��f�}�X�^�Q�Ɓj
		if (aclUpload.getMessage() == null || aclUpload.getMessage().length() <= 0) {
			if (AclUpdatableConditionMasterDB.getAclUpdatableConditionCount(
					aclUpload.getPreUpdateAcl(), aclUpload.getPostUpdateAcl(), conn) <= 0) {
				aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.upload.nopermission.update.acl", null, null));
			}
		}
	}

	/**
	 * �A�b�v���[�h�f�[�^�̃`�F�b�N
	 * �`�F�b�N�ŃG���[�ƂȂ������ڂ́AACL�A�b�v���[�h�̃��b�Z�[�W�ɃG���[����o�^���A��ʂɕ\������B
	 * @param aclUpdateNo
	 * @param resources
	 * @param conn
	 */
	private long checkUploadData(String aclUpdateNo, Connection conn)
			throws Exception {

		long resultCount = 0;

		// 1��2�i�ԏ��擾
		ArrayList<HashMap<String, String>> drwgNoMapList = AclUploadDB.getDrwgNoMapList(aclUpdateNo, conn);

		String twinDrwgNo = null;
		String value = null;
		boolean createNew = false;
		for (HashMap<String, String> drwgNoMap : drwgNoMapList) {
			value = drwgNoMap.get("MESSAGE");
			// �}�ԃG���[�`�F�b�N
			if (value != null && value.length() > 0) {
				// �����I���B���̐}�Ԃ�
				continue;

				// 1��2�i�Ԑ������`�F�b�N
			}
			value = drwgNoMap.get("ID1_DRWG_NO");
			if (value == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID1_TWIN_DRWG_NO")) == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID2_DRWG_NO")) == null || value.length() <= 0 ||
					(value = drwgNoMap.get("ID2_TWIN_DRWG_NO")) == null || value.length() <= 0) {
				AclUploadDB.updateMessage(aclUpdateNo, messageSource.getMessage("system.aclBatchUpdate.upload.nodata.twinDrwgNo", null, null), conn, drwgNoMap.get("ITEM_NO_SHORT"));

				// 1��2�i��ACL�`�F�b�N
			} else {

				// �Ή��}�Ԃ��Ȃ��ꍇ�A1��2�i�ԑΉ��}�Ԃ�ACL�A�b�v���[�h�f�[�^�e�[�u���ɒǉ�
				twinDrwgNo = null;
				createNew = false;
				value = drwgNoMap.get("TWIN_DRWG_NO");
				if (value == null || value.length() <= 0) {

					value = drwgNoMap.get("ID1_TWIN_DRWG_NO");
					if (value != null && value.length() > 0) {
						// �������D�}�� �� 1��2�i�ԑΉ��}�Ԃ��擾���A�R�s�[�o�^
						twinDrwgNo = value;
					} else {
						// �������D1��2�i�ԑΉ��}�� �� �}�Ԃ��擾���A�R�s�[�o�^
						twinDrwgNo = drwgNoMap.get("ID2_DRWG_NO");
					}
					AclUploadDB.insertSelectTwinDrwgNo(aclUpdateNo, drwgNoMap.get("ITEM_NO_SHORT"), conn);
					createNew = true; // �Ή��}�Ԃ�ǉ��쐬
				} else {
					twinDrwgNo = drwgNoMap.get("TWIN_DRWG_NO");
				}

				// �ύX�O�A�N�Z�X���x����r
				if (!drwgNoMap.get("ID1_PRE_UPDATE_ACL").equals(drwgNoMap.get("ID2_PRE_UPDATE_ACL")) || !createNew && !drwgNoMap.get("POST_UPDATE_ACL").equals(drwgNoMap.get("TWIN_POST_UPDATE_ACL"))) {
					AclUploadDB.updateMessage(aclUpdateNo, messageSource.getMessage("system.aclBatchUpdate.upload.notequal.twin.drwgNo.acl", null, null), conn, drwgNoMap.get("ITEM_NO_SHORT"), twinDrwgNo);

					// �ύX��A�N�Z�X���x����r�i���ǉ��쐬���̓R�s�[�o�^�Ȃ̂Ń`�F�b�N���Ȃ��j
				}
			}
		}
		return resultCount;
	}

	/**
	 * �}�ԓo�^�ς݃`�F�b�N�p�ɁA�������e�[�u����������̐}�Ԃ̌�����Ԃ��B
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
				// ACL�A�b�v���[�h�f�[�^�����擾
				count = rs.getLong("COUNT");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// CLOSE����
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

	// �X�V
	/**
	 * ACL�A�b�v���[�h�f�[�^�e�[�u���̓��e�ŁA�������e�[�u���̐}�ʃA�N�Z�X���x�����X�V����B
	 * @param accessLevelBatchUpdateForm
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doUpdate(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, User user,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("�X�V�����̊J�n");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();

			if (Objects.isNull(accessLevelBatchUpdateForm)) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.nodata", null, null));
				return;
			}
			// ACL�A�b�v���[�h�f�[�^�擾
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			ArrayList<AclUpload> aclUploadList = AclUploadDB.getDistinctAclUploadList(aclUpdateNo, conn);

			if (aclUploadList.size() <= 0) {
				// �X�V�\�ȃA�b�v���[�h�f�[�^��0���̏ꍇ
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.nodata", null, null));
				return;
			}

			// �������e�[�u�����X�V
			long count = updateIndexDb(aclUploadList, user, errors, conn);
			if (category.isDebugEnabled()) {
				category.debug("�������e�[�u���X�V:" + count + "��");
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
			// for �V�X�e���Ǘ���
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
			category.debug("�X�V�����̏I��");
		}
	}

	/**
	 * �A�b�v���[�h�f�[�^�̓��e�ŁA�������e�[�u���̐}�ʃA�N�Z�X���x�����X�V����B
	 * ACL�ꊇ�X�V�̓A�b�v���[�h�f�[�^�̎��O�`�F�b�N�ŃG���[�ƂȂ��Ă��Ȃ����̂��ΏۂƂȂ�B
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
			// �X�V���`�F�b�N�pSQL
			String sql1 = "select count(DRWG_NO) as COUNT from INDEX_DB where DRWG_NO = ? and ACL_ID = ?";
			pstmt1 = conn.prepareStatement(sql1);
			// �X�V�pSQL
			String sql2 = "update INDEX_DB set ACL_ID = ?, ACL_UPDATE = ?, ACL_EMPNO = ?, ACL_NAME = ? " +
					"where DRWG_NO = ?";
			pstmt2 = conn.prepareStatement(sql2);
			// ACL�A�b�v���[�h�f�[�^�e�[�u���X�V�pSQL
			String sql3 = "update ACL_UPLOAD_TABLE set ACL_UPDATE = ? where " +
					"ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and MESSAGE is null";
			pstmt3 = conn.prepareStatement(sql3);

			for (AclUpload aclUpload : aclUploadList) {
				try {
					conn.setAutoCommit(false);

					// �X�V���`�F�b�N�i�r���j
					pstmt1.setString(1, aclUpload.getItemNoShort());
					pstmt1.setString(2, aclUpload.getPreUpdateAcl());
					rs = pstmt1.executeQuery();

					if (rs.next()) {
						if (aclUpload.getMessage() != null && aclUpload.getMessage().length() > 0) {
							// ACL�A�b�v���[�h�f�[�^�e�[�u���ɃG���[���b�Z�[�W���ݒ肳��Ă���ꍇ�A�X�V���������Ȃ���log�֏����o��
							ChangeAclLogger.logging(user, aclUpload); // ACL�ύX���O
							// ���[���o�b�N
							conn.rollback();
							continue;
						}
						if (rs.getLong("COUNT") <= 0) {
							// �ʃg�����U�N�V�����ɂ���Đ}��ACL���X�V�ς݂̏ꍇ�A�G���[�B
							aclUpload.setMessage(messageSource.getMessage("system.aclBatchUpdate.update.excusive.acl", null, null));
							// �X�V���������Ȃ���log�֏����o��
							ChangeAclLogger.logging(user, aclUpload); // ACL�ύX���O
							// ���[���o�b�N
							conn.rollback();
							continue;
						}
					}
					// �A�N�Z�X���x���ύX�������擾
					aclUpload.setAclUpdate(new Date());

					// �}��ACL�X�V
					pstmt2.setString(1, aclUpload.getPostUpdateAcl());
					pstmt2.setTimestamp(2, new Timestamp(aclUpload.getAclUpdate().getTime()));
					pstmt2.setString(3, aclUpload.getUserId());
					pstmt2.setString(4, aclUpload.getUserName());
					pstmt2.setString(5, aclUpload.getItemNoShort());
					count += pstmt2.executeUpdate();

					// ACL�A�b�v���[�h�f�[�^�e�[�u�����X�V�i�X�V�����j
					pstmt3.setTimestamp(1, new Timestamp(aclUpload.getAclUpdate().getTime()));
					pstmt3.setString(2, aclUpload.getAclUpdateNo());
					pstmt3.setString(3, aclUpload.getItemNoShort());
					pstmt3.executeUpdate();

					// �R�~�b�g
					conn.commit();

					// �X�V���ʂ����O�֏����o��
					ChangeAclLogger.logging(user, aclUpload); // ACL�ύX���O

				} catch (Exception e) {
					try {
						// ���[���o�b�N
						conn.rollback();
					} catch (Exception e2) {
					}

					// for ���[�U�[
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
					// for �V�X�e���Ǘ���
					ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
					// for MUR
					if (category.isInfoEnabled()) {
						category.error(messageSource.getMessage("system.aclBatchUpdate.update.aclv", new Object[] { ErrorUtility.error2String(e) }, null));
					}

					aclUpload.setMessage(e.getMessage()); // ACL�ύX���O
					// �X�V���������Ȃ���log�֏����o��
					ChangeAclLogger.logging(user, aclUpload);
				}
			}
		} catch (Exception e) {
			// ���[���o�b�N
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			// CLOSE����
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
	 * ��ʃN���[�Y���ɁA����ACL�A�b�v���[�h�f�[�^������΃N���A����B
	 * @param accessLevelBatchUpdateForm
	 * @param user
	 */
	private void doClose(AccessLevelBatchUpdateForm accessLevelBatchUpdateForm, User user) {

		if (category.isDebugEnabled()) {
			category.debug("�N���[�Y�����̊J�n");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();

			// �O��A�b�v���[�h�f�[�^������΁A�폜
			String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();
			if (aclUpdateNo != null && aclUpdateNo.length() > 0) {
				long count = AclUploadDB.deleteAclUpload(aclUpdateNo, conn);
				if (category.isDebugEnabled()) {
					category.debug("ACL�A�b�v���[�h�f�[�^�e�[�u���폜:" + count + "��");
				}
			}
		} catch (Exception e) {
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error("ACL�A�b�v���[�h�f�[�^�e�[�u���̍폜�Ɏ��s(" + ErrorUtility.error2String(e) + ")");
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		if (category.isDebugEnabled()) {
			category.debug("�N���[�Y�����̏I��");
		}
	}
}
