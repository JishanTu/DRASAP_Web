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
 * �}�ʂ��v���r���[����Action�B
 * ���̓p�����[�^�[�� requset�p�����[�^�[ �Ŏ擾�B
 * DRWG_NO		�}��
 * FILE_NAME	�t�@�C����
 * PATH_NAME	�f�B���N�g���̃t���p�X
 * DRWG_SIZE	�}�ԃT�C�Y
 * PDF			PDF�ɕϊ�����?
 * SYS_ID		�V�X�e��ID
 * '04.Nov.23�ύX �}�Ԃ����M���O����悤��
 * 2005-Mar-4�ύX ���V�X�e������̌Ăяo���ł̓V�X�e��ID�����M���O����B
 * 2008/6/30 �ύX Sun-Win2003�ڐA�ɔ����o�i�[�A�Ԉ��������ύX
 * </PRE>
 * �ŏI�ύX $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 *
 * @version 2013/06/14 yamagishi
 */
@Controller
public class PreviewAction extends BaseAction {
	// --------------------------------------------------------- Instance Variables
	protected String lock = "";

	/** �}�ʌ�����ʂ�act���� MULTI_PDF */
	private String ACT_MULTI_PDF = "MULTI_PDF";

	/** �}�ʌ�����ʂ�act���� PDF_ZIP */
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
		// �����i�K
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		// �e���|�����̃t�H���_�̃t���p�X
		String tempDirName = DrasapUtil.getRealTempPath(request);
		// 2019.10.17 yamamoto add start.
		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		//		session.removeAttribute("searchResultForm");// session����폜
		// 2019.10.17 yamamoto add end.

		// DL�}�l�[�W������̃��N�G�X�g���		// 2013.08.02 yamagishi add. start
		DLManagerInfo dlmInfo = null;
		if ("1".equals(request.getParameter("DLM_REQ"))) {
			dlmInfo = new DLManagerInfo(request.getParameter("act"), messageSource);
		} // end

		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			if (dlmInfo != null) { // 2013.08.01 yamagishi add. start
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "�����ȃ��O�C���ł��B������x���O�C�����s���Ă��������B");
				return null;
			} // end
			return "timeout";
		}
		if (dlmInfo != null && dlmInfo.isActError()) { // 2013.08.02 yamagishi add. start
			// DL�}�l�[�W���ŃG���[���������Ă���ꍇ
			doErrorLog(user, dlmInfo);
			return null;
		} // end

		// MultiTIFF�o��
		if (searchResultForm != null) {
			if (ACT_MULTI_PDF.equals(searchResultForm.getAct())
					|| ACT_PDF_ZIP.equals(searchResultForm.getAct())) {

				// act������ێ�
				String act = searchResultForm.getAct();

				searchResultForm.setAct("");// act�������N���A
				session.setAttribute("searchResultForm", searchResultForm);

				// MultiTIFF�쐬���PDF�ϊ����s��
				doMultiPDF(searchResultForm, drasapInfo, user, response, errors, request, act);

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// �G���[������
					//saveErrors(request, errors);
					request.setAttribute("errors", errors);
					category.error("==> doMultiPDF error");
					return "error";
				}

				return null;
			}
		}

		// ���N�G�X�g�p�����[�^����擾����
		String drwgNo = request.getParameter("DRWG_NO");// �}��
		String fileName = request.getParameter("FILE_NAME");// �t�@�C����
		String pathName = request.getParameter("PATH_NAME");// �f�B���N�g���̃t���p�X
		pathName = drasapInfo.getViewDBDrive() + pathName.replace("/", "\\");
		String drwgSize = request.getParameter("DRWG_SIZE");// �}�ԃT�C�Y
		String pdfFlug = request.getParameter("PDF");// PDF�ɕϊ�����?

		// ���V�X�e������̌Ăяo���ɑΉ����邽�߁A�p�����[�^��ǉ�
		// DRASAP��������̌Ăяo���ł́A���̒l�� null �ɂȂ�B
		// 2005-Mar-4 by Hirata.
		String sysId = user.getSys_id();// �V�X�e��ID

		final String ORIGIN_FILE_NAME = pathName + File.separator + fileName;// ���̌��}�t�@�C���E�E�E��Ώ�����

		// �{���t�H�[�}�b�g�t���O�`�F�b�N
		if (pdfFlug == null || pdfFlug.length() == 0 || "null".equals(pdfFlug)) {
			// �{���t�H�[�}�b�g�t���O�ݒ�G���[
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

		// �O�̂��ߌ��̌��}�����邩�m�F���� '04.Mar.2 Hirata
		if (!new File(ORIGIN_FILE_NAME).exists()) {
			// ���̌��}�����݂��Ȃ�
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

		String outFileName = ORIGIN_FILE_NAME;// �X�g���[���ɗ����Ώۂ̃t�@�C����

		// 2013.07.01 yamagishi add. start
		// �o�i�[�����ǉ�
		Connection conn = null;
		conn = ds.getConnection();

		// �o�i�[�������i�Y���}�j
		if (AclvMasterDB.isCorresponding(drwgNo, conn)) {
			// �ꎞ�I�ȃt�@�C������ύX����B
			String newOutFileName = tempDirName + File.separator + user.getId()
					+ "_" + drwgNo + "_" + new Date().getTime() + ".tif";

			doCorrespondingBanner(outFileName, newOutFileName,
					drasapInfo.getCorrespondingStampStr(),
					drasapInfo.getCorrespondingStampW(), drasapInfo.getCorrespondingStampL(),
					drasapInfo.getViewStampDeep(),
					drasapInfo.isDispDrwgNoWithView() ? drwgNo : null, // �}�Ԃ��󎚂��Ȃ��ꍇ�Anull��n��
					user, errors, dlmInfo);

			// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " ���폜����");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// �o�i�[�����ŃG���[���������Ă�����
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
			outFileName = newOutFileName;// �o�i�[������̃t�@�C�����ɕύX����
		}
		// 2013.07.01 yamagishi add. end

		// �����菇��ύX '04.Apr.8 by MUR/Hirata
		// �V) �X�^���v���� --> �Ԉ�������
		// ��) �Ԉ������� --> �X�^���v����
		// ���R�E�E�E�X�^���v��������� 100dpi��400dpi�ɂȂ��Ă��܂�����

		// �o�i�[������
		if (user.isViewStamp()) {// �r���[�Ńo�i�[����Ȃ�
			//String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogic���Ƃ₽��ɃZ�b�V����ID���������߁A�ꎞ�I�ȃt�@�C������ύX����B
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".tif";

			doBanner(outFileName, newOutFileName,
					drasapInfo.getViewStampW(), drasapInfo.getViewStampL(), drasapInfo.getViewStampDeep(),
					drasapInfo.getViewStampDateFormat(),
					// �}�Ԃ��󎚂��Ȃ��ꍇ�Anull��n��
					drasapInfo.isDispDrwgNoWithView() ? drwgNo : null,
					//					user, errors);	// 2013.08.02 yamagishi modified.
					user, errors, dlmInfo);
			// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " ���폜����");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// �o�i�[�����ŃG���[���������Ă�����
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
			outFileName = newOutFileName;// �o�i�[������̃t�@�C�����ɕύX����

		}

		// �Ԉ������K�v�����肷��
		// �Ԉ����T�C�Y�̖��ݒ�ɂ��Ή�����B'04.Jul.19�ύX by Hirata�B
		String mabikiDpi = null;// �Ԉ�����dpi
		if (drasapInfo.getMabiki100dpiSize() != null &&
				DrasapUtil.compareDrwgSize(drwgSize, drasapInfo.getMabiki100dpiSize()) >= 0) {
			// �}�ʃT�C�Y >= 100dpi�̃T�C�Y�A�Ȃ�
			mabikiDpi = "100";
		} else if (drasapInfo.getMabiki200dpiSize() != null &&
				DrasapUtil.compareDrwgSize(drwgSize, drasapInfo.getMabiki200dpiSize()) >= 0) {
			// �}�ʃT�C�Y >= 200dpi�̃T�C�Y�A�Ȃ�
			mabikiDpi = "200";
		}
		if (mabikiDpi != null) {
			// �Ԉ����ΏۂȂ�A�擾�����u�Ԉ�����dpi�v�ŊԈ���
			// �Ԉ��������t�@�C����
			// String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogic���Ƃ₽��ɃZ�b�V����ID���������߁A�ꎞ�I�ȃt�@�C������ύX����B
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".tif";

			//			doMabiki(outFileName, newOutFileName, mabikiDpi, user, errors, drwgNo);	// 2013.08.02 yamagishi modified.
			doMabiki(outFileName, newOutFileName, mabikiDpi, user, errors, drwgNo, dlmInfo);
			// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " ���폜����");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// �Ԉ��������ŃG���[���������Ă�����
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
			outFileName = newOutFileName;// �Ԉ��������t�@�C�����ɕύX����
		}

		// PDF�ɕϊ�����
		if ("printablePdf".equalsIgnoreCase(convertPdf) || "noprintPdf".equalsIgnoreCase(convertPdf)) {
			// PDF�ϊ���̃t�@�C����
			// String newOutFileName = tempDirName + File.separator + session.getId() + "_" + (new Date().getTime());
			// WebLogic���Ƃ₽��ɃZ�b�V����ID���������߁A�ꎞ�I�ȃt�@�C������ύX����B
			String newOutFileName = tempDirName + File.separator + user.getId() + "_"
					+ drwgNo + "_" + new Date().getTime() + ".pdf";
			//			tifToPdf(outFileName, newOutFileName, user, errors, drwgNo, convertPdf.equalsIgnoreCase("printablePdf")?true:false);	// 2013.08.02 yamagishi modified.
			tifToPdf(outFileName, newOutFileName, user, errors, drwgNo, "printablePdf".equalsIgnoreCase(convertPdf) == true, dlmInfo, 1, false);
			// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (new File(outFileName).delete()) {
					category.debug(outFileName + " ���폜����");
				}
			}
			if (!Objects.isNull(errors.getAttribute("message"))) {
				// PDF�����ŃG���[���������Ă�����
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
			outFileName = newOutFileName;// PDF�ϊ����������t�@�C�����ɕύX����
		}

		// �X�g���[���ɗ���
		File f = new File(outFileName);
		String streamFileName = fileName;// �w�b�_�ɃZ�b�g����t�@�C����
		if ("printablePdf".equalsIgnoreCase(convertPdf) || "noprintPdf".equalsIgnoreCase(convertPdf)) {
			// PDF�ϊ������ꍇ
			int lastIndex = streamFileName.lastIndexOf('.');
			if (lastIndex == -1) {
				// �t�@�C������'.'���Ȃ��ꍇ�E�E�E����'.pdf'��
				streamFileName += ".pdf";
			} else {
				// .tif��.pdf�ɕύX����
				streamFileName = streamFileName.substring(0, lastIndex) + ".pdf";
			}
		}
		// ���[�U�[�����̏����̍Œ��ɁA�ʂ̃��N�G�X�g�𓊂����ꍇ�i�����������̂Ŏ~�߂��A�w�肵���}�Ԃ��ԈႦ��)
		// response�����łɕ����Ă���ꍇ������B
		// ���̏ꍇ�ł��A�I���W�i���̃t�@�C���ȊO���폜�������̂ŁAtry,catch����
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			//���̂܂܂��Ɠ��{�ꂪ������
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
			// CLOSE����
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
				category.debug("out.close()");
			} catch (Exception e) {
			}

			// �I���W�i���̃t�@�C���łȂ���΁A�폜����
			if (!ORIGIN_FILE_NAME.equals(outFileName)) {
				if (f.delete()) {
					category.debug(outFileName + " ���폜����");
				}
			}
		}
		// �A�N�Z�X���O��
		// '04.Nov.23 �}�Ԃ����M���O����悤��
		// 2005-Mar-4�ύX ���V�X�e������̌Ăяo���ł̓V�X�e��ID�����M���O����B
		//		AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, sysId);		// 2013.08.02 yamagishi modified.
		if (dlmInfo != null && dlmInfo.isActSave()) {
			AccessLoger.loging(user, AccessLoger.FID_SAVE, drwgNo, sysId);
		} else {
			AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, sysId);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * �Ԉ����������s���B��O�����������ꍇ�Aerrors��add����B
	 * @param inFile �Ԉ����Ώۂ̃t�@�C��(��΃p�X)
	 * @param outFile �o�̓t�@�C����(��΃p�X)
	 * @param dpi �𑜓x
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doMabiki(String inFile, String outFile, String dpi, User user, Model errors,
			//							String drwgNo){		// 2013.08.02 yamagishi modified.
			String drwgNo, DLManagerInfo dlmInfo) {
		category.debug("�Ԉ��������̊J�n");
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�Ԉ��������̊J�n");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "�Ԉ����Ώۂ����݂��܂���B" + inFile);
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
			sbCmd.append(inFile);// �Ԉ����Ώۃt�@�C��
			sbCmd.append(' ');
			sbCmd.append(outFile);// �Ԉ�����̏o�̓t�@�C��
			sbCmd.append(' ');
			sbCmd.append(dpi);// �𑜓x
			synchronized (lock) {
				process = Runtime.getRuntime().exec(sbCmd.toString());
				// �v���Z�X����̕W���o�͂��̂Ă�
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				int exitCode = process.waitFor();
				while (br.ready()) {
					//�v���Z�X����̕W���o�͂��̂Ă�
					//�������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
					br.readLine();
				}

				if (exitCode != 0) {
					// 2013.08.02 yamagishi modified. start
					//					// for ���[�U�[
					//					MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(),"�I���R�[�h="+exitCode));
					//					// for �V�X�e���Ǘ���
					//					ErrorLoger.error(user, this,
					//								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					if (dlmInfo != null) {
						// for DL�}�l�[�W��
						dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.mabiki." + user.getLanKey(), "�I���R�[�h=" + exitCode));
						// for �V�X�e���Ǘ���
						ErrorLoger.error(user, dlmInfo,
								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					} else {
						// for ���[�U�[
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(), new Object[] { "�I���R�[�h=" + exitCode }, null));
						// for �V�X�e���Ǘ���
						ErrorLoger.error(user, this,
								DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
					}
					// 2013.08.02 yamagishi modified. end
					// �r���[��p�̃��O
					String errMsg = "VIEW�̂��߂̊Ԉ��������Ɏ��s�B�I���R�[�h=" + exitCode;
					ViewLoger.error(drwgNo, errMsg);
					// for MUR
					category.error(errMsg);
				}
			}

		} catch (Exception e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ���[�U�[
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(),e.getMessage()));
			//			// for �V�X�e���Ǘ���
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			if (dlmInfo != null) {
				// for DL�}�l�[�W��
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.mabiki." + user.getLanKey(), e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			} else {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.mabiki." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.mabiki"));
			}
			// 2013.08.02 yamagishi modified. end
			// �r���[��p�̃��O
			String errMsg = "VIEW�̂��߂̊Ԉ��������Ɏ��s\n" + ErrorUtility.error2String(e);
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

		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�Ԉ��������̏I��");
	}

	/**
	 * �o�i�[�������s�Ȃ��B��O�����������ꍇ�Aerrors��add����B
	 * @param inFile �o�i�[�Ώۂ̃t�@�C��(��΃p�X)
	 * @param outFile �o�̓t�@�C����(��΃p�X)
	 * @param pixW �E�����_����̉������̋���(�s�N�Z���P��)
	 * @param pixL �E�����_����̏c�����̋���(�s�N�Z���P��)
	 * @param deep �X�^���v�̔Z�W(0����100�܂�)
	 * @param dateFormat ���t�`���BSimpleDateFormat�̌`���ŁB
	 * @param drwgNo �}�ԁBnull�̏ꍇ�A�}�Ԃ͈󎚂���Ȃ��B
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doBanner(String inFile, String outFile, String pixW, String pixL, String deep,
			//						String dateFormat, String drwgNo, User user, Model errors){
			String dateFormat, String drwgNo, User user, Model errors, DLManagerInfo dlmInfo) { // 2013.08.02 yamagishi modified.
		category.debug("�o�i�[�����̊J�n");
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�o�i�[�����̊J�n");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "�o�i�[�����̑Ώۂ����݂��܂���B" + inFile);
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
			//			// for ���[�U�[
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.banner." + user.getLanKey(),e.getMessage()));
			//			// for �V�X�e���Ǘ���
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			if (dlmInfo != null) {
				// for DL�}�l�[�W��
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.banner." + user.getLanKey(), e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			} else {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			}
			// 2013.08.02 yamagishi modified. end
			// �r���[��p�̃��O
			String errMsg = "VIEW�̂��߂̃o�i�[�����Ɏ��s\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�o�i�[�����̏I��");
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
				//�v���Z�X����̕W���o�͂��̂Ă�
				//�������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
				br.readLine();
			}
			if (exitCode != 0) {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// �r���[��p�̃��O
				String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(�e�L�X�g�}�[�W�̎��s " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(�e�L�X�g�}�[�W�̎��s " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(�e�L�X�g�}�[�W�̎��s " + e.getMessage() + ")";
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
		sbCmd.append(inFile);// �o�i�[�g�s�h�e�e�t�@�C��
		sbCmd.append(' ');
		sbCmd.append(outFile);// �e�L�X�g�}�[�W��̂s�h�e�e�t�@�C��
		sbCmd.append(' ');
		try {
			process = Runtime.getRuntime().exec(sbCmd.toString());
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int exitCode;
			exitCode = process.waitFor();
			while (br.ready()) {
				//�v���Z�X����̕W���o�͂��̂Ă�
				//�������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
				br.readLine();
			}
			if (exitCode != 0) {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// �r���[��p�̃��O
				errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(�X�^���v�}�[�W�̎��s " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(�X�^���v�}�[�W�̎��s " + e.getMessage() + ")";
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
	 * PDF�ϊ��������s���B��O�����������ꍇ�Aerrors��add����B
	 * @param inFile PDF�ϊ��Ώۂ̃t�@�C��(��΃p�X)
	 * @param outFile �o�̓t�@�C����(��΃p�X)
	 * @param dpi �𑜓x
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 * @param totalPage Tiff�̍��v�y�[�W��
	 */
	@SuppressWarnings("deprecation")
	private void tifToPdf(String inFile, String outFile, User user, Model errors,
			//							String drwgNo, boolean printable){	// 2013.08.02 yamagishi modified.
			String drwgNo, boolean printable, DLManagerInfo dlmInfo, int totalPage,
			boolean multiPDF) {
		// Win�ł̏ꍇ�Apdf_java.dll���p�X���ʂ����Ƃ���ɒu�����ƁB
		// �p�X���ʂ��Ă��Ȃ��ꍇ�A
		// Cannot load the PDFlib shared library/DLL for Java.
		// Make sure to properly install the native PDFlib library.
		// �Ƃ��������b�Z�[�W���o�͂����B

		// �t�@�C�����ȂǂŃT�|�[�g����Ă���̂�ascii�̂�(2004.Jan.14 F.Hirata/MUR)

		category.debug("PDF�ϊ������̊J�n");
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "PDF�ϊ������̊J�n");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "PDF�ϊ������̑Ώۂ����݂��܂���B" + inFile);
		}

		int image;
		pdflib p = null;
		/* This is where font/image/PDF input files live. Adjust as necessary.*/

		//String searchpath = ".";

		try {

			p = new pdflib();
			/* open new PDF file */

			// �A�N�Z�X���̍폜 �iModified Oce Japan Corporation 2004/01/09�j
			// 2020.02.14 yamamoto mod Multi PDF�̏ꍇ�̓p�X���[�h�͐ݒ肵�Ȃ�
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

			// ���̃p�����[�^�������������s��
			// �Ƃ肠�����폜���Ă����삷�邱�Ƃ��m�F(2004.Jan.14 F.Hirata/MUR)
			//p.set_parameter("SearchPath", searchpath);

			// ���̕����͕s�v �iModified Oce Japan Corporation 2004/01/09�j
			//  p.set_info("Creator", "image.java");
			//  p.set_info("Author", "Thomas Merz");
			//  p.set_info("Title", "image sample (Java)");

			// Tiff�̃y�[�W�����ϊ�
			// 1�y�[�W����ϊ��J�n
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
			//			// for ���[�U�[
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(),
			//					"[" + e.get_errnum() + "] " + e.get_apiname() +	": " + e.getMessage()));
			//			// for �V�X�e���Ǘ���
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			if (dlmInfo != null) {
				// for DL�}�l�[�W��
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.pdf." + user.getLanKey(),
						"[" + e.get_errnum() + "] " + e.get_apiname() + ": " + e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			} else {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(),
						new Object[] { "[" + e.get_errnum() + "] " + e.get_apiname() + ": " + e.getMessage() }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			}
			// 2013.08.02 yamagishi modified. end
			// �r���[��p�̃��O
			String errMsg = "VIEW�̂��߂�PDF�ϊ������Ɏ��s\n" +
					"[" + e.get_errnum() + "] " + e.get_apiname() + "\n" +
					ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);

		} catch (Exception e) {
			// 2013.08.02 yamagishi modified. start
			//			// for ���[�U�[
			//			MessageSourceUtil.addAttribute(errors, "message",messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(), e.getMessage()));
			//			// for �V�X�e���Ǘ���
			//			ErrorLoger.error(user, this,
			//						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			if (dlmInfo != null) {
				// for DL�}�l�[�W��
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.pdf." + user.getLanKey(), e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			} else {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdf." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			}
			// 2013.08.02 yamagishi modified. end
			// �r���[��p�̃��O
			String errMsg = "VIEW�̂��߂�PDF�ϊ������Ɏ��s\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);

		} finally {
			if (p != null) {
				p.delete(); /* delete the PDFlib object */
			}
		}
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "PDF�ϊ������̏I��");
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
		String ymd = new SimpleDateFormat(dateFormat).format(new Date());// YYYY/MM/DD�`���̖{�����t
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
				//�v���Z�X����̕W���o�͂��̂Ă�
				//�������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
				br.readLine();
			}
			if (exitCode != 0) {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// �r���[��p�̃��O
				String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setBannerWidth�̎��s " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setBannerWidth�̎��s " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setBannerWidth�̎��s " + e.getMessage() + ")";
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
	 * �o�i�[�����i�Y���}�j���s�Ȃ��B��O�����������ꍇ�Aerrors��add����B
	 * @param correspondingStampStr �Y���}�p�X�^���v����
	 * @param inFile �o�i�[�Ώۂ̃t�@�C��(��΃p�X)
	 * @param outFile �o�̓t�@�C����(��΃p�X)
	 * @param pixW �E�����_����̉������̋���(�s�N�Z���P��)
	 * @param pixL �E�����_����̏c�����̋���(�s�N�Z���P��)
	 * @param deep �X�^���v�̔Z�W(0����100�܂�)
	 * @param drwgNo �}�ԁBnull�̏ꍇ�A�}�Ԃ͈󎚂���Ȃ��B
	 * @param user
	 * @param errors
	 * @param dlmInfo
	 */
	private void doCorrespondingBanner(String inFile, String outFile, String correspondingStampStr, String pixW, String pixL,
			String deep, String drwgNo, User user, Model errors, DLManagerInfo dlmInfo) {

		category.debug("�o�i�[�����i�Y���}�j�̊J�n");
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�o�i�[�����i�Y���}�j�̊J�n");
		if (!new File(inFile).exists()) {
			ViewLoger.error(drwgNo, "�o�i�[�����i�Y���}�j�̑Ώۂ����݂��܂���B" + inFile);
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
				// for DL�}�l�[�W��
				dlmInfo.setLabelMessage(dlmInfo.getMessage("search.failed.view.banner." + user.getLanKey(), e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, dlmInfo,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			} else {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			}
			// �r���[��p�̃��O
			String errMsg = "VIEW�̂��߂̃o�i�[�����i�Y���}�j�Ɏ��s\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�o�i�[�����i�Y���}�j�̏I��");
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
				//�v���Z�X����̕W���o�͂��̂Ă�
				//�������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
				br.readLine();
			}
			if (exitCode != 0) {
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { sbCmd }, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
				// �r���[��p�̃��O
				String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setCorrespondingBannerWidth�̎��s " + sbCmd + ")";
				ViewLoger.error("", errMsg);
				// for MUR
				category.error(errMsg);

				return false;
			}
		} catch (InterruptedException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setBannerWidth�̎��s " + e.getMessage() + ")";
			ViewLoger.error("", errMsg);
			// for MUR
			category.error(errMsg);
			throw e;
		} catch (IOException e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.banner." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.stamp"));
			// �r���[��p�̃��O
			String errMsg = "�r���[�C���O�p�̃f�[�^�쐬�Ɏ��s���܂����B(setBannerWidth�̎��s " + e.getMessage() + ")";
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
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.pdf"));
			// �r���[��p�̃��O
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
	 * DL�}�l�[�W���Ŕ��������G���[���O���o�͂���B
	 * @param user
	 * @param dlmInfo
	 */
	private void doErrorLog(User user, DLManagerInfo dlmInfo) {
		// for DL�}�l�[�W��
		ErrorLoger.error(user, dlmInfo,
				DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
	}
	// 2013.08.02 yamagishi add. end

	/**
	 * �}���`PDF�� �܂��� PDF��ZIP�����s��
	 * @param searchResultForm
	 * @param drasapInfo
	 * @param user
	 * @param response
	 * @param errors
	 * @param action
	 *
	 * modify 2020.03.10 yamamoto PDF��ZIP�������ǉ�
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

		// �e���|�����̃t�H���_�̃t���p�X
		String tempDirName = DrasapUtil.getRealTempPath(request);

		//		category.debug("searchResultForm����擾����");

		for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
			SearchResultElement resultElem = searchResultForm.getSearchResultList().get(i);
			if (resultElem.isSelected()) {
				// �������ʂ̃`�F�b�N�{�b�N�X��
				// �`�F�b�N�������Ă���΁A���X�g�ɕۑ�
				PreviewElement pe = new PreviewElement();
				pe.setDrwgNo(resultElem.getDrwgNo()); // �}��
				pe.setDrwgSize(resultElem.getAttr("DRWG_SIZE")); // �}�ԃT�C�Y
				pe.setFileName(resultElem.getFileName()); // �t�@�C����
				pe.setPathName(resultElem.getPathName()); // �f�B���N�g���̃t���p�X
				selectedList.add(pe);
			}
		}
		// PDF�����Z�L�����e�B�����̂��߁A�Œ�Œl��ݒ肷��
		pdfFlug = "printablePdf"; // printablePdf�Œ�
		convertPdf = pdfFlug.toLowerCase(); // "tiff", "printablePdf", "noprintPdf"

		Connection conn = null;
		String tmpOutFileName = "";

		try {

			conn = ds.getConnection();

			/*
			 * �I��}�ʂɂ��Ĉȉ����{
			 * �E�o�i�[������
			 * �E�X�^���v����
			 * �EPDF�ϊ�(PDF�P�� Zip�o�͂̏ꍇ�̂�
			 */
			for (int i = 0; i < selectedList.size(); i++) {

				// �I��}�ʂ̏��擾
				String drwgNo = selectedList.get(i).getdrwgNo();// �}��
				String fileName = selectedList.get(i).getFileName();// �t�@�C����
				String pathName = drasapInfo.getViewDBDrive() + File.separator +
						selectedList.get(i).getPathName(); // �f�B���N�g���̃t���p�X

				final String ORIGIN_FILE_NAME = pathName + File.separator + fileName;// ���̌��}�t�@�C���E�E�E��Ώ�����

				// ���̌��}�����邩�m�F
				if (!new File(ORIGIN_FILE_NAME).exists()) {
					// ���̌��}�����݂��Ȃ�
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.nofile." + user.getLanKey(), new Object[] { ORIGIN_FILE_NAME }, null));
					return;
				}

				String tmpJoinFileName = ORIGIN_FILE_NAME; // �����t�@�C����

				// �o�i�[�������i�Y���}�j
				// �Y���}�ʂɃA�N�Z�X�������邩�H
				if (AclvMasterDB.isCorresponding(drwgNo, conn)) {

					// �ϊ���̃t�@�C�����Ɉꎞ�I�ȃt�@�C������t����
					String newOutFileName = tempDirName + File.separator + user.getId()
							+ "_" + drwgNo + "_" + new Date().getTime() + ".tif";

					// �o�i�[�����i�Y���}�j
					doCorrespondingBanner(tmpJoinFileName, newOutFileName,
							drasapInfo.getCorrespondingStampStr(),
							drasapInfo.getCorrespondingStampW(), drasapInfo.getCorrespondingStampL(),
							drasapInfo.getViewStampDeep(),
							drasapInfo.isDispDrwgNoWithView() ? drwgNo : null, // �}�Ԃ��󎚂��Ȃ��ꍇ�Anull��n��
							user, errors, null); // DL�}�l�[�W���[����Ăяo����Ȃ����߁Anull��n��

					// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " ���폜����");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// �G���[������
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName; // �o�i�[������̃t�@�C�����ɕύX����
				}

				// �X�^���v����
				// �{�����̃X�^���v���������H
				if (user.isViewStamp()) {

					// �ϊ���̃t�@�C�����Ɉꎞ�I�ȃt�@�C������t����
					String newOutFileName = tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".tif";

					// �X�^���v������
					doBanner(tmpJoinFileName, newOutFileName,
							drasapInfo.getViewStampW(), drasapInfo.getViewStampL(), drasapInfo.getViewStampDeep(),
							drasapInfo.getViewStampDateFormat(),
							// �}�Ԃ��󎚂��Ȃ��ꍇ�Anull��n��
							drasapInfo.isDispDrwgNoWithView() ? drwgNo : null,
							user, errors, null); // DL�}�l�[�W���[����Ăяo����Ȃ����߁Anull��n��

					// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " ���폜����");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// �G���[������
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName;// �o�i�[������̃t�@�C�����ɕύX����
				}

				// �}���`PDF�͊Ԉ����͕s�v

				// PDF�ɕϊ� (PDF�P�� Zip�o�͂̏ꍇ�̂�)
				if (ACT_PDF_ZIP.equals(action)) {

					// �ϊ���̃t�@�C�����Ɉꎞ�I�ȃt�@�C������t����
					String newOutFileName = tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".pdf";
					// PDF �ϊ�
					tifToPdf(tmpJoinFileName, newOutFileName, user, errors, "",
							"printablePdf".equalsIgnoreCase(convertPdf) == true,
							null, 1, true);

					// �ϊ��O�̃t�@�C�����I���W�i���łȂ���΁A�폜����
					if (!ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {
						if (new File(tmpJoinFileName).delete()) {
							category.debug(tmpJoinFileName + " ���폜����");
						}
					}

					if (!Objects.isNull(errors.getAttribute("message"))) {
						// �G���[������
						throw new UserException();
					}
					tmpJoinFileName = newOutFileName;// PDF�ϊ���̃t�@�C�����ɕύX����
				}

				// ���ϊ�(�I���W�i���t�@�C��)�̏ꍇ�̓t�@�C�����R�s�[����
				if (ORIGIN_FILE_NAME.equals(tmpJoinFileName)) {

					// ���̃t�@�C��
					File targetFile = new File(tmpJoinFileName);
					// �R�s�[��̃t�@�C��
					File newOutFile = new File(tempDirName + File.separator + user.getId() + "_"
							+ drwgNo + "_" + new Date().getTime() + ".tif");

					// �t�@�C���R�s�[
					copyFile(targetFile, newOutFile);
					category.debug("ORIGIN_FILE:" + targetFile + " newOutFile:" + newOutFile);
				}

				if (ACT_MULTI_PDF.equals(action)) {
					// �����Ώۃt�@�C���ǉ� (�}���`PDF)
					joinFileNameList.add(tmpJoinFileName);
				} else {
					// �P��PDF���t�@�C���ǉ�
					singlePdfFileMap.put(drwgNo, tmpJoinFileName);
				}

				// 2020/02/27 �s�ǉ�
				// �A�N�Z�X���O�����M���O����
				AccessLoger.loging(user, AccessLoger.FID_DISP_DRWG, drwgNo, user.getSys_id());

			}

			/*
			 * �}���`PDF�o�͂̏ꍇ
			 * ����Tiff�t�@�C����1�t�@�C���̃}���`Tiff�ɕϊ���APDF�ɕϊ�����
			 */
			if (ACT_MULTI_PDF.equals(action)) {
				// �ꎞ�i�[�t�H���_��
				String newOutPathDir = tempDirName + File.separator + user.getId() + "_" + new Date().getTime();
				// Tiff������̃t�@�C����(��΃p�X)
				String multiTiffFileName = newOutPathDir + "_join.tif";

				// TIFF�t�@�C������
				doTiffJoint("", newOutPathDir, joinFileNameList, multiTiffFileName, user, errors);

				// �����O��TIFF�t�@�C���폜
				for (int i = 0; i < joinFileNameList.size(); i++) {
					File targetFile = new File(joinFileNameList.get(i));
					targetFile.delete();
				}

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// �G���[������
					throw new UserException();
				}

				// �ϊ����PDF�t�@�C�����́udrawings_yyyymmddhhmmss.pdf�v
				String newOutFileName = tempDirName + File.separator
						+ "drawings_"
						+ DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
						+ ".pdf";
				// PDF �ϊ�
				tifToPdf(multiTiffFileName, newOutFileName, user, errors, "",
						"printablePdf".equalsIgnoreCase(convertPdf) == true,
						null, joinFileNameList.size(), true);

				// �}���`Tiff�t�@�C���폜
				if (new File(multiTiffFileName).delete()) {
					category.debug(multiTiffFileName + " ���폜����");
				}
				if (!Objects.isNull(errors.getAttribute("message"))) {
					// �G���[������
					throw new UserException();
				}
				// PDF�ϊ���̃t�@�C�����ɕύX����
				tmpOutFileName = newOutFileName;
			}
			/*
			 *  PDF�P�� Zip�o�͂̏ꍇ
			 *  �ϊ�����PDF��Zip������
			 */
			else {

				// zip���Ώۃt�H���_
				String zipTargetDir = tempDirName + File.separator
						+ "drawings_" + new Date().getTime();
				// zip�t�@�C�� (�t���p�X)
				String zipFile = tempDirName + File.separator
						+ "drawings_"
						+ DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
						+ ".zip";

				// ZIP��
				doPdfToZip(selectedList, singlePdfFileMap, zipTargetDir, zipFile, user, errors);

				// PDF�t�@�C���폜
				for (String key : singlePdfFileMap.keySet()) {
					File file = new File(singlePdfFileMap.get(key));
					if (DrasapUtil.deleteFile(file)) {
						category.debug(file.toString() + " ���폜����");
					} else {
						category.error(file.toString() + " ���폜���s");
					}
				}

				if (!Objects.isNull(errors.getAttribute("message"))) {
					// �G���[������
					throw new UserException();
				}

				// zip����̃t�@�C�����ɕύX����
				tmpOutFileName = zipFile;
			}
		} catch (UserException e) {
			// �Ăяo����ŃG���[���b�Z�[�W��ǉ����Ă��邽�߁Areturn�̂�
			return;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}

		// �ϊ������t�@�C�����X�g���[���ɗ���
		File outFile = new File(tmpOutFileName);
		// �w�b�_�ɃZ�b�g����t�@�C���� (���X�g��1�ԍŏ��ɑI�������t�@�C������ݒ�)
		String streamFileName = outFile.getName();

		// ���[�U�[�����̏����̍Œ��ɁA�ʂ̃��N�G�X�g�𓊂����ꍇ�i�����������̂Ŏ~�߂��A�w�肵���}�Ԃ��ԈႦ��)
		// response�����łɕ����Ă���ꍇ������B
		// ���̏ꍇ�ł��A�I���W�i���̃t�@�C���ȊO���폜�������̂ŁAtry,catch����
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			//���̂܂܂��Ɠ��{�ꂪ������
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
			String errMsg = "�}���`PDF�����Ŏ��s\n" + ErrorUtility.error2String(e);
			category.error(errMsg);
		} finally {
			// CLOSE����
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}

			// �ϊ������t�@�C�����폜����
			if (outFile.delete()) {
				category.debug(tmpOutFileName + " ���폜����");
			}
		}
	}

	/**
	 * MULTI TIFF�t�@�C�����������s���B��O�����������ꍇ�Aerrors��add����B
	 * @param drwgNo
	 * @param inPath TIFF�}�ʊi�[�p�X(��΃p�X)
	 * @param FileList
	 * @param outFile ������TIFF�t�@�C����(��΃p�X)
	 * @param user
	 * @param errors
	 * @return outFileName
	 */
	private void doTiffJoint(String drwgNo, String inPath, ArrayList<String> FileList, String outFile, User user, Model errors) {
		category.debug("�s�h�e�e���������̊J�n");
		//		String outFileName = "";

		// �r���[��p�̃��O
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

			// �ꎞ�i�[�t�H���_���쐬��A�����Ώۂ�Tiff���R�s�[
			File f_inPath = new File(inPath);
			boolean success = f_inPath.mkdir();
			if (!success) {
				// �t�H���_�쐬���s
				throw new UserException("�t�H���_�쐬���s���܂���[" + inPath + "]");
			}
			for (int i = 0; i < FileList.size(); i++) {
				File targetFile = new File(FileList.get(i));
				String fileId = targetFile.getName();
				// �A��(4��)_���[�UID_�}��_�^�C���X�^���v.tif
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
			sbCmd.append(inPath);// TIFF�}�ʊi�[�p�X
			sbCmd.append(' ');
			sbCmd.append(outFile);// ������TIFF�t�@�C����
			sbCmd.append(' ');

			category.debug("cmd:" + sbCmd.toString());

			synchronized (lock) {
				process = Runtime.getRuntime().exec(sbCmd.toString());
				// �v���Z�X����̕W���o�͂��̂Ă�
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String str = null;
				StringBuilder errStr = new StringBuilder();
				while ((str = br.readLine()) != null) {
					// �v���Z�X����̕W���o�͂��̂Ă�
					// �������Ȃ��ƃv���Z�X���X�g�[������ꍇ������B
					errStr.append(str);
				}

				int exitCode = process.waitFor();
				if (exitCode == 0) {
					// for ���[�U�[
					MessageSourceUtil.addAttribute(errors, "message",
							messageSource.getMessage("search.failed.view.multiTiff." + user.getLanKey(), new Object[] { "drawNo=" + drwgNo + ",ExitCode=" + exitCode }, null));
					// for �V�X�e���Ǘ���
					ErrorLoger.error(user, this, "�s�h�e�e�̌��������Ɏ��s�B�I���R�[�h=" + exitCode + "," + errStr.toString());
					// �r���[��p�̃��O
					String errMsg = "�s�h�e�e�̌��������Ɏ��s�B�I���R�[�h=" + exitCode;
					ViewLoger.error(drwgNo, errMsg);
					// for MUR
					category.error(errMsg);
				}
			}

			// �t�H���_����уt�H���_�z���̑S�t�@�C���폜
			if (!DrasapUtil.deleteFile(f_inPath)) {
				category.error("�t�H���_�폜���s���܂���[" + inPath + "]");
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.multiTiff." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�s�h�e�e�̌��������Ɏ��s" + ErrorUtility.error2String(e));
			// �r���[��p�̃��O
			String errMsg = "�s�h�e�e�̌����Ɏ��s\n" + ErrorUtility.error2String(e);
			ViewLoger.error(drwgNo, errMsg);
			// for MUR
			category.error(errMsg);
		}
		// �r���[��p�̃��O
		ViewLoger.info(drwgNo, "�s�h�e�e�̌��������̏I��");

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
	 * ����PDF�t�@�C��Zip�����s���B��O�����������ꍇ�Aerrors��add����B
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

		category.debug("PDF Zip���k�����̊J�n");
		// �r���[��p�̃��O
		ViewLoger.info("", "PDF Zip���k�����̊J�n", true);

		byte[] buf = new byte[1024];
		// �Ώۃt�H���_
		File f_zipDir = new File(dir);
		// Zip�t�@�C��
		File zipFile = new File(outFile);

		ZipOutputStream zos = null;
		InputStream is = null;

		try {

			// zip�o�̓I�u�W�F�N�g�擾
			zos = new ZipOutputStream(new FileOutputStream(zipFile), Charset.forName("MS932"));

			// zip�t�H���_���쐬���Azip�t�H���_�Ƀt�@�C���R�s�[
			boolean success = f_zipDir.mkdir();
			if (!success) {
				// �t�H���_�쐬���s
				throw new UserException("Zip�t�H���_�쐬���s���܂���[" + f_zipDir.toString() + "]");
			}
			for (int i = 0; i < elem.size(); i++) {

				// �}��
				String _drwgNo = elem.get(i).getdrwgNo();

				// �R�s�[���t�@�C��
				File targetFile = new File(pdfFileMap.get(_drwgNo));

				// ���̃t�@�C�����擾
				String pdfFileName = elem.get(i).getFileName();
				// �O�̂��ߊg���q���`�F�b�N
				int lastIndex = pdfFileName.lastIndexOf('.');
				if (lastIndex == -1) {
					// �t�@�C������'.'���Ȃ��ꍇ�A����'.pdf'������
					pdfFileName += ".pdf";
				} else {
					// .tif��.pdf�ɕύX����
					pdfFileName = pdfFileName.substring(0, lastIndex) + ".pdf";
				}

				// �R�s�[��t�@�C��(zip�t�H���_\�}�ԃt�@�C����.pdf)
				File newFile = new File(dir + File.separator + pdfFileName);

				// �t�@�C���R�s�[
				copyFile(targetFile, newFile);
				//				category.debug("targetFile:" + targetFile + " newFile:" + newFile);
			}

			// zip�Ώۃt�@�C�����X�g�擾
			File[] fileList = f_zipDir.listFiles();

			// Zip�ǉ�
			for (File file : fileList) {

				// Zip�G���g���ǉ� (�t�@�C���t���p�X)
				zos.putNextEntry(new ZipEntry(file.getName()));

				// ZIP�t�@�C���ɏ�����������
				is = new BufferedInputStream(new FileInputStream(file));

				int len = 0;
				while ((len = is.read(buf)) != -1) {
					// Zip��������
					zos.write(buf, 0, len);
				}

				// �X�g���[�������
				zos.closeEntry();
				is.close();
			}

			// �t�H���_����уt�H���_�z���̑S�t�@�C���폜
			if (!DrasapUtil.deleteFile(f_zipDir)) {
				category.error("�t�H���_�폜���s���܂���[" + f_zipDir.toString() + "]");
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.view.pdfzip." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "PDF��Zip���k�����Ɏ��s" + ErrorUtility.error2String(e));
			// �r���[��p�̃��O
			String errMsg = "PDF��Zip���k�Ɏ��s\n" + ErrorUtility.error2String(e);
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

		category.debug("PDF Zip���k�����̏I��");

		// �r���[��p�̃��O
		ViewLoger.info("", "PDF Zip���k�����̏I��", true);
	}

}
