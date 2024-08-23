package tyk.drasap.system;

import static tyk.drasap.common.DrasapPropertiesFactory.BEA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.CATALINA_HOME;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_BASE;
import static tyk.drasap.common.DrasapPropertiesFactory.OCE_AP_SERVER_HOME;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * �A�N�Z�X���x���X�V���ʏ����A�N�V����
 *
 * @author 2013/07/23 yamagishi
 */
public class AccessLevelUpdatedResultAction extends Action {
	private static Category category = Category.getInstance(AccessLevelUpdatedResultAction.class.getName());

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

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return mapping.findForward("timeout");
		}

		ActionMessages errors = new ActionMessages();
		MessageResources resources = getResources(request);

		// �A�N�Z�X���x���ꊇ�X�V�c�[���̎g�p�����Ȃ��̏ꍇ
		if (user.getAclBatchUpdateFlag() == null || user.getAclBatchUpdateFlag().length() <= 0) {
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclBatchUpdate.nopermission", "�A�N�Z�X���x���X�V���ʉ��"));
			saveErrors(request, errors);
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclBatchUpdate.nopermission", "�A�N�Z�X���x���X�V���ʉ��"));
			}
			return mapping.findForward("noPermission");
		}

		AccessLevelUpdatedResultForm accessLevelUpdatedResultForm = (AccessLevelUpdatedResultForm) form;
		if ("init".equals(request.getParameter("act"))) {
			accessLevelUpdatedResultForm.setAct("init");
		}
		accessLevelUpdatedResultForm.clearErrorMsg();
		String fileName = request.getParameter("FILE_NAME");

		//
		if ("init".equals(accessLevelUpdatedResultForm.getAct())) {
			setFormLinkAclLogData(accessLevelUpdatedResultForm);
			// �G���[�m�F
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return mapping.findForward("error");
			}
			return mapping.findForward("init");

		} else if ("download".equals(accessLevelUpdatedResultForm.getAct())) {
			doDownload(response, fileName, user, errors, resources);
			// �G���[�m�F
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return mapping.findForward("error");
			}
			return null;
		}

		if (category.isDebugEnabled()) {
			category.debug("end");
		}
		return mapping.findForward("init");
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
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);
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
		ArrayList<Map.Entry<String, Long>> sortedFiles =
				new ArrayList<Map.Entry<String, Long>>(sortMap.entrySet());
		Collections.sort(sortedFiles, new Comparator<Map.Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
				return ((Long) entry2.getValue()).compareTo((Long) entry1.getValue());
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
			ActionMessages errors, MessageResources resources) {

		if (category.isDebugEnabled()) {
			category.debug("�A�N�Z�X���x���X�V���ʃ_�E�����[�h���s�����̊J�n");
		}

		Properties drasapProperties = DrasapPropertiesFactory.getDrasapProperties(this);

		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = drasapProperties.getProperty(OCE_AP_SERVER_BASE);

		// �t�@�C�������m�F
		if (fileName == null || fileName.length() <= 0) {
			// for ���[�U�[
			ActionMessage error = new ActionMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���� " + fileName));
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���� " + fileName)));
			}
			return;
		}

		String filePath = apServerHome + drasapProperties.getProperty("tyk.result.updated.log.path");
		String[] paths = filePath.split("\\" + File.separator);
		String orglFileName = paths[paths.length - 1];
		String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator + orglFileName));
		filePath = folderPath + File.separator + fileName;

		// ACL�X�V���ʃ��O�t�@�C�������邩�m�F����
		if (!(new File(filePath)).exists()) {
			// for ���[�U�[
			ActionMessage error = new ActionMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���p�X " + filePath));
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclUpdatedResult.download.failed", ("�t�@�C���p�X " + filePath)));
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
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("system.aclUpdatedResult.download.failed", ErrorUtility.error2String(e)));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"), user.getSys_id());
			// for MUR
			if (category.isInfoEnabled()) {
				category.error(resources.getMessage("system.aclUpdatedResult.download.failed", ErrorUtility.error2String(e)));
			}

		} finally {
			// CLOSE����
			try { if (in != null) in.close(); } catch (Exception e) {}
			try {
				if (out != null) out.close();
				if (category.isDebugEnabled()) {
					category.debug("out.close()");
				}
			} catch (Exception e) {}
		}
		if (category.isDebugEnabled()) {
			category.debug("�A�N�Z�X���x���X�V���ʃ_�E�����[�h���s�����̏I��");
		}
	}
}
