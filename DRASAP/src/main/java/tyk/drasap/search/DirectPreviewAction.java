package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserException;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * ���̃V�X�e������A�}�ʃr���[�C���O�@�\�𒼐ڎg�p����Ƃ��Ɏg�p����Action�B
 * ���ۂ�Preview�@�\�́ADRASAP��������̂Ƃ��Ɠ��l��PreviewAction���g�p����B
 * ���̂��߂ɕK�v�ȏ��̎��W�Ȃǂ�����Action�ōs���B
 * DirectLoginForPreviewAction����J�ڂ��Ă���B
 * </PRE>
 * @author fumi
 * �쐬�� 2005/03/03
 * �ύX�� $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/26 yamagishi
 */
@Controller
public class DirectPreviewAction extends BaseAction {
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
	@PostMapping("/directPreview")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}
		// �N�b�L�[���猾��ݒ���擾
		CookieManage langCookie = new CookieManage();
		String lanKey = langCookie.getCookie(request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) {
			lanKey = "Japanese";
		}
		user.setLanguage(lanKey);

		// request����K�v�ȏ����擾���܂��B
		//		String usr_id = (String) request.getAttribute("usr_id");
		//		String drwg_no = (String) request.getAttribute("drwg_no");
		String drwgNo = (String) request.getAttribute("drwgNo");
		//String sys_id = user.getSys_id();

		session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");
		// drwg_no�Ō�������A����ʂɕK�v�ȏ����擾����
		Map<String, String> linkParmMap = null;
		try {
			linkParmMap = search(drwgNo, user, errors);
		} catch (Exception e) {
			// �G���[����������
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);// �G���[��o�^
			request.setAttribute("hasError", "true");// jsp�ŃG���[�\���Ɏg�p����

			return "error";
		}

		request.setAttribute("drwgNo", drwgNo);
		String path = "/preview.do";
		path = path + "?DRWG_NO=" + linkParmMap.get("DRWG_NO");
		path = path + "&FILE_NAME=" + linkParmMap.get("FILE_NAME");
		path = path + "&PATH_NAME=" + linkParmMap.get("PATH_NAME").replace("\\", "/");
		path = path + "&DRWG_SIZE=" + linkParmMap.get("DRWG_SIZE");
		path = path + "&PDF=" + linkParmMap.get("PDF");
		category.info("redirect to preview.do");

		return path;
	}

	/**
	 * �w�肵���}�ԂŌ�������B
	 * ����Ȃ�A�p�����[�^�[Map��Ԃ��B�i�[����̂�PreviewAction�ɕK�v�ȃp�����[�^�B
	 * �G���[������΁A�S�� errors �Ɋi�[����B
	 * �`�F�b�N���e�́A
	 * 1) �}�Ԃ��o�^����Ă��邩�H
	 * 2) �A�N�Z�X���������邩�H
	 * @param drwg_no �}��
	 * @param user
	 * @param sys_id �Ăяo�����̃V�X�e��ID
	 * @param errors
	 * @return PreviewAction�ɕK�v�ȃp�����[�^���i�[����Map
	 * @throws Exception
	 */
	private Map<String, String> search(String drwgNo, User user, Model errors) throws Exception {
		Map<String, String> linkParmMap = new HashMap<String, String>();
		Connection conn = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			StringBuilder sbSql1 = new StringBuilder("select DRWG_NO, FILE_NAME, PATH_NAME, DRWG_SIZE, ACL_ID");
			sbSql1.append(" from INDEX_FILE_VIEW");
			sbSql1.append(" where DRWG_NO='");
			sbSql1.append(drwgNo);
			sbSql1.append("'");

			pstmt1 = conn.prepareStatement(sbSql1.toString());

			rs1 = pstmt1.executeQuery();
			if (!rs1.next()) {
				// �w��}�ԂȂ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.not.registered." + user.getLanKey(), new Object[] { drwgNo }, null));
				// for MUR
				category.error("�w��}�ԂȂ�[" + drwgNo + "]");
				throw new UserException("�w��}�ԂȂ�[" + drwgNo + "]");
			}
			String aclId = rs1.getString("ACL_ID");// �A�N�Z�X���x��ID
			// 2013.06.26 yamagishi modified. start
			// String aclValue = (String)user.getAclMap().get(aclId);// �A�N�Z�X���x���l
			String aclValue = user.getAclMap(conn).get(aclId);// �A�N�Z�X���x���l
			// 2013.06.26 yamagishi modified. end
			if (aclValue == null || "0".equals(aclValue)) {
				// ���̃��[�U�[���瓱�����A�N�Z�X���x���l�� null �܂��� 0 �Ȃ�
				// �}�ʂ��Q�Ƃ��錠�����Ȃ��B�܂�\���ł��Ȃ��B
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.no.authority.reading." + user.getLanKey(), new Object[] { drwgNo }, null));
				// for MUR
				category.error("�����Ȃ�[" + drwgNo + "]");
				throw new UserException("�����Ȃ�[" + drwgNo + "]");
			}
			// Preview�ɕK�v�ȏ��� linkParmMap �� �i�[����B
			linkParmMap.put("DRWG_NO", drwgNo);// �}��
			linkParmMap.put("FILE_NAME", rs1.getString("FILE_NAME"));// �t�@�C����
			linkParmMap.put("PATH_NAME", rs1.getString("PATH_NAME"));// �f�B���N�g���̃t���p�X
			linkParmMap.put("DRWG_SIZE", rs1.getString("DRWG_SIZE"));// �}�ʃT�C�Y
			// 2013.06.26 yamagishi modified. start
			// linkParmMap.put("PDF", user.getViewPrintDoc(aclId));// PDF�ϊ�����?
			linkParmMap.put("PDF", user.getViewPrintDoc(aclId, conn));// PDF�ϊ�����?
			// 2013.06.26 yamagishi modified. end
		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�����Ɏ��s\n" + ErrorUtility.error2String(e));
			throw e;
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (Exception e) {
			}
			try {
				if (pstmt1 != null) {
					pstmt1.close();
				}
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		return linkParmMap;
	}
	//	private class DrwgNoInfo {
	//		String drwgNo = "";
	//		String info = "";
	//		int status = 0;
	//	}
}
