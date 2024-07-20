package tyk.drasap.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �������ʂ��t�@�C���o�͂���
 * @author fumi
 * �쐬��: 2004/01/19
 */
@Controller
public class OutAttrCsvAction extends BaseAction {
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
	@PostMapping("/outAttrCsv")
	public Object execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors) throws Exception {
		category.debug("start");
		// �����i�K
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		//		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}

		// �e���|�����̃t�H���_�̃t���p�X
		String tempDirName = DrasapUtil.getRealTempPath(request);
		SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
		String outAllFlug = (String) request.getAttribute("OUT_CSV_ALL");// �S������?
		// �܂�CSV�t�@�C�����쐬����
		String outFileName = tempDirName + File.separator + session.getId() + "_" + new Date().getTime();
		File outFile = new File(outFileName);
		writeAttrCsv(outFile, searchResultForm,
				outAllFlug != null && "true".equals(outAllFlug), // null�łȂ��A"true"�̂Ƃ���
				user, errors);// CSV�쐬
		if (!Objects.isNull(errors.getAttribute("message"))) {
			// CSV�t�@�C���쐬�ŃG���[���������Ă�����
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> error");
			return "error";

		}
		// CSV�t�@�C���_�E�����[�h����
		String streamFileName = "SeachResult.csv";// �w�b�_�ɃZ�b�g����t�@�C����
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;" +
				" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
		//���̂܂܂��Ɠ��{�ꂪ������
		//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
		response.setContentLength((int) outFile.length());

		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
		out.flush();
		out.close();
		//
		category.debug("out.close()");
		// CSV�t�@�C�����폜����
		if (outFile.delete()) {
			category.debug(outFileName + " ���폜����");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * �w�肵���t�@�C���Ƃ��āA�������ʂ�CSV�`���ō쐬����B
	 * �쐬�Ɏ��s�����ꍇ�Aerrors�ɏ����o���B
	 * @param outFile �o�͐�̃t�@�C��
	 * @param searchResultForm
	 * @param allAttr �S�����Ȃ�true
	 * @param user
	 * @param errors
	 */
	private void writeAttrCsv(File outFile, SearchResultForm searchResultForm, boolean allAttr,
			User user, Model errors) {
		OutputStreamWriter out = null;
		try {
			// �o�̓X�g���[���̏���
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)), "Windows-31J");
			searchResultForm.writeAttrCsv(out, allAttr);
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.csv." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.csv"));
			// for MUR
			category.error("�������ʂ̃t�@�C���o�͂Ɏ��s\n" + ErrorUtility.error2String(e));

		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}

	}

}
