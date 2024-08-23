package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;
import static tyk.drasap.common.DrasapUtil.defaultString;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import tyk.drasap.common.AclUpload;
import tyk.drasap.common.AclUploadDB;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * <PRE>
 * �A�N�Z�X���x���ꊇ�X�V�}�ʂŁA�_�E�����[�h���������s����Action�B
 * ���̓p�����[�^�[�� requset�p�����[�^�[ �Ŏ擾�B
 * FILE_TYPE		�_�E�����[�h�t�@�C���̎��
 * ACL_UPDATE_NO	�Ǘ�NO
 *
 * @author 2013/07/08 yamagishi
 */
public class AccessLevelDownloadAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(AccessLevelDownloadAction.class.getName());
	static {
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch (Exception e) {
			if (category.isInfoEnabled()) {
				category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			}
		}
	}
	private Row cellStyles = null; // �Z���̃X�^�C���ݒ�擾�p

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
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (category.isDebugEnabled()) {
			category.debug("start");
		}
		//
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return mapping.findForward("timeout");
		}

		ActionMessages errors = new ActionMessages();
		MessageResources resources = getResources(request);

		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		AccessLevelBatchUpdateForm accessLevelBatchUpdateForm = (AccessLevelBatchUpdateForm) form;

		// ���N�G�X�g�p�����[�^����擾����
		String fileType = request.getParameter("dlFileType");				// �t�@�C���^�C�v
		String aclUpdateNo = accessLevelBatchUpdateForm.getAclUpdateNo();	// �Ǘ�NO

		// �_�E�����[�h���s
		doDownload(response, fileType, aclUpdateNo, drasapInfo, user, errors, resources);
		// �G���[�m�F
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("error");
		}

		if (category.isDebugEnabled()) {
			category.debug("end");
		}
		return null;
	}

	/**
	 * �_�E�����[�h�����s����B
	 * @param response
	 * @param fileType (0:���`�t�@�C��, 1:Excel�f�[�^)
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doDownload(HttpServletResponse response, String fileType, String aclUpdateNo,
			DrasapInfo drasapInfo, User user, ActionMessages errors, MessageResources resources) {

		if (category.isDebugEnabled()) {
			category.debug("�_�E�����[�h���s�����̊J�n");
		}

		if ("0".equals(fileType)) {
			// ���`�t�@�C���̃_�E�����[�h
			createTemplate(response, user, errors, resources);
		} else if ("1".equals(fileType)) {
			// �\�����e��Excel�f�[�^�Ń_�E�����[�h
			createExcelData(response, aclUpdateNo, user, drasapInfo, errors, resources);
		} else {
			// �_�E�����[�h����t�@�C�����s��
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.download.failed.fileType"));
			return;
		}

		if (category.isDebugEnabled()) {
			category.debug("�_�E�����[�h���s�����̏I��");
		}
	}

	/**
	 * ���`�t�@�C���̃_�E�����[�h���s���B
	 * @param response
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void createTemplate(HttpServletResponse response, User user,
			ActionMessages errors, MessageResources resources) {

		if (category.isDebugEnabled()) {
			category.debug("���`�t�@�C���_�E�����[�h�����̊J�n");
		}
		//
		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);
		final String ORIGIN_FILE_NAME = apServerHome + drasapProperties.getProperty("tyk.download.template.path"); // �e���v���[�g�t�@�C��

		// �e���v���[�g�t�@�C�������邩�m�F����
		if (!(new File(ORIGIN_FILE_NAME)).exists()) {
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.download.nofile", ORIGIN_FILE_NAME));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclBatchUpdate.download.nofile", ORIGIN_FILE_NAME));
			}
			return;
		}

		String[] paths = ORIGIN_FILE_NAME.split("\\" + File.separator);
		String fileName = paths[paths.length - 1];
		String inFileName = ORIGIN_FILE_NAME; // �X�g���[���ɗ����Ώۂ̃t�@�C����

		// �X�g���[���ɗ���
		File f = new File(inFileName);
		String streamFileName = fileName; // �w�b�_�ɃZ�b�g����t�@�C����

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			response.setContentType("application/octet-stream");
			// ���������Ή�
			response.setHeader("Content-Disposition","attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"),"ISO8859_1"));
			response.setContentLength((int) f.length());

			in = new BufferedInputStream(new FileInputStream(f));
			out = new BufferedOutputStream(response.getOutputStream());
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			out.flush();

		} catch(Exception e) {
			try { response.reset(); } catch (Exception e2) {}
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.download.failed.template", ErrorUtility.error2String(e)));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
 			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclBatchUpdate.download.failed.template", ErrorUtility.error2String(e)));
			}

		} finally {
			// CLOSE����
			try { if (in != null) in.close(); } catch (Exception e) {}
			try {
				if (out != null) out.close();
				if (category.isDebugEnabled()) {
					category.debug("out.close()");
				}
			} catch (Exception e) {
			}
		}
		if (category.isDebugEnabled()) {
			category.debug("���`�t�@�C���_�E�����[�h�����̏I��");
		}
	}

	/**
	 * ��ʕ\�����e��Excel�t�@�C���Ń_�E�����[�h���s���B
	 * @param response
	 * @param aclUpdateNo
	 * @param drasapInfo
	 * @param errors
	 * @param resources
	 */
	private void createExcelData(HttpServletResponse response, String aclUpdateNo, User user, DrasapInfo drasapInfo,
			ActionMessages errors, MessageResources resources) {

		if (category.isDebugEnabled()) {
			category.debug("Excel�t�@�C���_�E�����[�h�����̊J�n");
		}
		//
		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);
		final String ORIGIN_FILE_NAME = apServerHome + drasapProperties.getProperty("tyk.download.excel.format.path");// Excel�t�H�[�}�b�g

		// �e���v���[�g�t�@�C�������邩�m�F����
		if (!(new File(ORIGIN_FILE_NAME)).exists()) {
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.download.nofile", ORIGIN_FILE_NAME));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclBatchUpdate.download.nofile", ORIGIN_FILE_NAME));
			}
			return;
		}

		String[] paths = ORIGIN_FILE_NAME.split("\\" + File.separator);
		String sheetName = drasapProperties.getProperty("tyk.download.excel.sheet.name"); // �V�[�g��
		paths = ORIGIN_FILE_NAME.split("\\.");
		String extension = paths[paths.length - 1]; // �g���q

		int headerRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.row.num"));					// �w�b�_�s
		int firstRowNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.start.row.num"));					// �J�n�s
		int firstColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.start.column.num"));				// �J�n��
		int header1ColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.item1.column.num"));	// �w�b�_����1
		int	header2ColumnNum = Integer.parseInt(drasapProperties.getProperty("tyk.download.excel.header.item2.column.num"));	// �w�b�_����2

		Connection conn = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		ByteArrayOutputStream bytes = null;
		try {
			String fileName = null;
			if (aclUpdateNo.length() > 0) {
				fileName = "AclUpload_" + aclUpdateNo + "." + extension; // �t�@�C����
			} else {
				fileName = "AclUpload." + extension; // �t�@�C����
			}

			// ���X�|���X�ɏ����o��
			response.setContentType("application/octet-stream;charset=Windows-31J");
			// ���������Ή�
			response.setHeader("Content-Disposition","attachment;" +
					" filename=" + new String(fileName.getBytes("Windows-31J"),"ISO8859_1"));

			in = new BufferedInputStream(new FileInputStream(ORIGIN_FILE_NAME));
			out = new BufferedOutputStream(response.getOutputStream());

			// Excel�t�H�[�}�b�g�擾
			Workbook book = WorkbookFactory.create(in);	// Book.
			Sheet sheet = book.getSheet(sheetName);	// Sheet.
			Row row = null;	// Row.

			// �A�b�v���[�h�f�[�^�̎擾
			conn = ds.getConnection();
			List<AclUpload> aclUploadList = AclUploadDB.getAclUploadDispList(aclUpdateNo, drasapInfo, conn);

			int columnIndex = 0;
			AclUpload aclUpload = null;
			for (int i = 0; i < aclUploadList.size(); i++) {
				columnIndex = firstColumnNum;
				aclUpload = aclUploadList.get(i);

				// ���R�[�h�f�[�^�쐬
				row = getRow(sheet, firstRowNum + i);
				row.getCell(columnIndex).setCellValue(defaultString(aclUpload.getItemNo()));				// �i��
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getItemName()));			// �i��(�K�i�^��)
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getGrpCode()));				// �O���[�v
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getCorrespondingValue()));	// �Y���}
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getConfidentialValue()));	// �@���Ǘ��}
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getPreUpdateAclName()));	// �ύX�OACL
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getPostUpdateAclName()));	// �ύX��ACL
				row.getCell(++columnIndex).setCellValue(defaultString(aclUpload.getMessage()));				// ���b�Z�[�W

				if (i == 0) {
					if (cellStyles == null) {
						cellStyles = row;
					}
					// �w�b�_�f�[�^�쐬
					row = sheet.getRow(headerRowNum);
					row.getCell(header1ColumnNum).setCellValue(defaultString(aclUpdateNo));	// �Ǘ�NO
					row.getCell(header2ColumnNum).setCellValue(aclUploadList.size());		// �i�Ԑ�
					// ����͈͐ݒ�
					book.setPrintArea(book.getSheetIndex(sheetName),
							firstColumnNum, columnIndex, headerRowNum,
							firstRowNum + aclUploadList.size() - 1);
				}

			}
			bytes = new ByteArrayOutputStream();
			book.write(bytes);
			response.setContentLength(bytes.size()); // �R���e���c�T�C�Y

			out.write(bytes.toByteArray());
			out.flush();

		} catch (Exception e) {
			try { response.reset(); } catch (Exception e2) {}
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.download.failed.excel", ErrorUtility.error2String(e)));
			// for �V�X�e���Ǘ���.
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclBatchUpdate.download.failed.excel", ErrorUtility.error2String(e)));
			}

		} finally {
			// CLOSE����
			try { conn.close(); } catch (Exception e) {}
			try { if (in != null) in.close(); } catch (Exception e) {}
			try {
				if (out != null) out.close();
				if (category.isDebugEnabled()) {
					category.debug("out.close()");
				}
			} catch (Exception e) {}
			try { if (bytes != null) bytes.close(); } catch (Exception e) {}
		}

		if (category.isDebugEnabled()) {
			category.debug("Excel�t�@�C���_�E�����[�h�����̏I��");
		}
	}

	/**
	 * Excel�s�f�[�^���擾�ł��Ȃ������ꍇ�ɁA�s�ǉ����s���B
	 * �X�^�C���ݒ��1�s�ڂ̊Y���Z������R�s�[����B
	 * @param sheet
	 * @param rowIndex
	 */
	private Row getRow(Sheet sheet, int rowIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex); // �s�ǉ�
			Cell cell = null;
			for (int columnIndex = 0; columnIndex < cellStyles.getLastCellNum(); columnIndex++) {
				cell = row.createCell(columnIndex); // �Z���ǉ�
				cell.setCellStyle(cellStyles.getCell(columnIndex).getCellStyle()); // �X�^�C���R�s�[
			}
		}
		return row;
	}
}
