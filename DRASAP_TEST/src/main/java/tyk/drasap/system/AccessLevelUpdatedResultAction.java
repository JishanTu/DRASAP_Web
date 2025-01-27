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
 * �A�N�Z�X���x���X�V���ʏ����A�N�V����
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

		// �A�N�Z�X���x���ꊇ�X�V�c�[���̎g�p�����Ȃ��̏ꍇ
		if (user.getAclBatchUpdateFlag() == null || user.getAclBatchUpdateFlag().length() <= 0) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "�A�N�Z�X���x���X�V���ʉ��" }, null));
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclBatchUpdate.nopermission", new Object[] { "�A�N�Z�X���x���X�V���ʉ��" }, null));
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
			// �G���[�m�F
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
			// �G���[�m�F
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
	 * ��ʕ\���p�ɃA�N�Z�X���x���X�V���ʃ��O���擾���AAccessLevelUpdatedResultForm�ɃZ�b�g����B
	 * @param accessLevelUpdatedResultForm
	 */
	private void setFormLinkAclLogData(AccessLevelUpdatedResultForm accessLevelUpdatedResultForm) {

		if (category.isDebugEnabled()) {
			category.debug("�A�N�Z�X���x���X�V���ʎ擾�����̊J�n");
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

		HashMap<String, Long> sortMap = new HashMap<String, Long>(); // �\�[�g�p

		// ���O�o�̓t�H���_�̃t�@�C���ꗗ���擾
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			if (file.isFile()
					&& file.getName().startsWith(fileNameWithNoExtension) && file.getName().endsWith(fileExtension)) {
				// �t�@�C�����A�X�V����
				sortMap.put(file.getName(), file.lastModified());
			}
		}
		// �X�V�����̍~���Ń\�[�g
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
			linkParmMap.put("FILE_NAME", file.getName()); // �t�@�C����
			linkParmMap.put("act", "download");

			accessLevelUpdatedResultList.add(resultElement);
		}
		accessLevelUpdatedResultForm.setAccessLevelUpdatedResultList(accessLevelUpdatedResultList);
		accessLevelUpdatedResultForm.setFileCount(accessLevelUpdatedResultList.size()); // ACL���O�t�@�C������

		if (category.isDebugEnabled()) {
			category.debug("�A�N�Z�X���x���X�V���ʎ擾�����̏I��");
		}
	}

	/**
	 * �_�E�����[�h�����s����B
	 * @param response
	 * @param fileName
	 * @param user
	 * @param errors
	 * @param resources
	 */
	private void doDownload(HttpServletResponse response, String fileName, User user,
			Model errors) {

		if (category.isDebugEnabled()) {
			category.debug("�A�N�Z�X���x���X�V���ʃ_�E�����[�h���s�����̊J�n");
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

		// �t�@�C�������m�F
		if (fileName == null || fileName.length() <= 0) {
			// for ���[�U�[
			//ActionMessage error = messageSource.getMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���� " + fileName));
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "�t�@�C���� " + fileName }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "�t�@�C���� " + fileName }, null));
			}
			return;
		}

		String filePath = apServerHome + drasapProperties.getProperty("tyk.result.updated.log.path");
		String[] paths = filePath.split("\\" + File.separator);
		String orglFileName = paths[paths.length - 1];
		String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator + orglFileName));
		filePath = folderPath + File.separator + fileName;

		// ACL�X�V���ʃ��O�t�@�C�������邩�m�F����
		if (!new File(filePath).exists()) {
			// for ���[�U�[
			//ActionMessage error = messageSource.getMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���p�X " + filePath));
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "�t�@�C���p�X " + filePath }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { "�t�@�C���p�X " + filePath }, null));
			}
			return;
		}

		// �X�g���[���ɗ���
		File f = new File(filePath);
		String streamFileName = fileName;// �w�b�_�ɃZ�b�g����t�@�C����

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			response.setContentType("application/octet-stream");
			// ���������Ή�
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
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(messageSource.getMessage("system.aclUpdatedResult.download.failed", new Object[] { ErrorUtility.error2String(e) }, null));
			}

		} finally {
			// CLOSE����
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
			category.debug("�A�N�Z�X���x���X�V���ʃ_�E�����[�h���s�����̏I��");
		}
	}
}
