package tyk.drasap.root;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.toyodakouki.IDDE;
import tyk.drasap.common.AdminSettingDB;
import tyk.drasap.common.CookieManage;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �V���O���T�C���I���̂��߂̃��O�C���B�|�[�^������̃��O�C���ɑΉ����邽�߁B
 * �p�����[�^(en_string)���󂯎��A�Í��������c�[��(IDDE)���g�p����ID���擾����B
 * ���j���[������ɁA���ڃt�@���N�V�����ɑJ�ڂ��邽�߂ɁA�p�����[�^fn��ǉ��B'04.May.13
 * @version 2013/07/25 yamagishi
 */
@Controller
public class LoginWithIddeAction extends BaseAction {
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
	@PostMapping("/loginWithIdde")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// �܂�User���쐬����
		User user = new User(request.getRemoteAddr());
		HttpSession session = request.getSession();
		session.removeAttribute("user");// ���ł�session����폜����
		category.debug("session ID=" + session.getId());
		//ActionMessages errors = new ActionMessages();
		DrasapInfo drasapInfo = null;

		// session����p�����[�^���擾���A��������
		String enString = (String) session.getAttribute("en_string");
		session.removeAttribute("en_string");// session����remove����
		category.debug("en_string = " + enString);
		if (enString == null) {
			return "timeout";
		}
		String id = IDDE.decode(enString);// ��������������
		category.debug("id = " + id);
		// �p�����[�^ fn��ǉ�
		String fn = (String) session.getAttribute("fn");
		session.removeAttribute("fn");// session����remove����
		if (fn == null) {
			return "timeout";
		}
		category.debug("fn = " + fn);

		if ("-1".equals(id)) {
			// ���O�C��ID�ُ�
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "���O�C��ID�ُ�" }, null));
		} else if ("-2".equals(id)) {
			// SYSpass�ُ�
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.decode." + user.getLanKey(), new Object[] { id, "SYSpass�ُ�" }, null));
		} else {
			//			int serverPort = request.getServerPort();
			//			category.debug("serverPort="+Integer.toString(serverPort));
			//			try{
			//				ds = DataSourceFactory.getOracleDataSource(serverPort);
			//			} catch(Exception e){
			//				category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			//			}
			// id�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
			addUserInfo(user, id, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// �p�X���[�h�L�������`�F�b�N
				if (!isPasswordExpired(user, errors)) {
					// �V�X�e�������Ǘ��Ґݒ�}�X�^�[����擾
					drasapInfo = getDrasapInfo(user, errors);
				}
			}
		}

		if (!Objects.isNull(errors.getAttribute("message"))) {
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);// �G���[��o�^
			category.debug("--> failed");
			return "failed";
		}
		// �N�b�L�[���猾��ݒ���擾���Auser�I�u�W�F�N�g�ɕt������B
		CookieManage langCookie = new CookieManage();
		String lanKey = langCookie.getCookie(request, user, "Language");
		if (lanKey == null || lanKey.length() == 0) {
			lanKey = "Japanese";
		}
		user.setLanguage(lanKey);

		// ���[�U�[��񂪎擾�ł����� session�Ɋi�[����
		session.setAttribute("user", user);
		session.setAttribute("drasapInfo", drasapInfo);
		session.setAttribute("default_css", "jp".equals(user.getLanKey()) ? "default.css" : "defaultEN.css");
		// �p�����[�^fn�ɂ���ď����𕪂���B'04.May.13�ύX
		// fn = 1-4 �́A�O���[�o���t�H���[�h���g�p����B
		if ("1".equals(fn)) {
			// fn = 1 �Ȃ�@�}�ʌ���
			category.debug("--> search_search_Main");
			return "search_search_Main";

		}
		if ("2".equals(fn)) {
			// fn = 2 �Ȃ�@���}�ɍ�ƈ˗�
			category.debug("--> genzu_irai_request");
			return "genzu_irai_request";

		}
		if ("3".equals(fn)) {
			// fn = 3 �Ȃ�@���}�ɍ�ƈ˗��ڍ�
			category.debug("--> genzu_irai_request_ref");
			return "genzu_irai_request_ref";

		}
		if ("4".equals(fn)) {
			// fn = 4 �Ȃ�@���}�ɍ�ƈ˗����X�g
			category.debug("--> genzu_irai_request_list");
			return "genzu_irai_request_list";

			// 2013.07.25 yamagishi add. start
		} else if ("5".equals(fn)) {
			// fn = 5 �Ȃ�@�A�N�Z�X���x���ꊇ�X�V
			category.debug("--> acl_batch_update");
			return "acl_batch_update";
		} else if ("6".equals(fn)) {
			// fn = 6 �Ȃ�@�A�N�Z�X���x���X�V����
			category.debug("--> acl_updated_result");
			return "acl_updated_result";
			// 2013.07.25 yamagishi add. end

		} else {
			// ����ȊO�̏ꍇ�̓��j���[��
			category.debug("--> success");
			return "success";

		}
	}

	/**
	 * id�A�p�X���[�h�����Ƀ��[�U�[�����擾���Auser�I�u�W�F�N�g�ɕt������B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param id
	 * @param passwd
	 * @param errors
	 */
	private void addUserInfo(User user, String id, Model errors) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			if (UserDB.addUserInfo(user, id, conn)) {
				// id����v
			} else {
				// ��v���Ȃ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.missmatch." + user.getLanKey(), new Object[] { "Id" }, null));
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �V�X�e�������擾����B
	 * �G���[�������errors�ɃG���[�o�^����B
	 * @param user
	 * @param errors
	 */
	private DrasapInfo getDrasapInfo(User user, Model errors) {
		Connection conn = null;
		DrasapInfo drasapInfo = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			drasapInfo = AdminSettingDB.getDrasapInfo(conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.get.drasapinfo.jp", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�V�X�e�����̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return drasapInfo;
	}

	/**
	 * �p�X���[�h�L�������`�F�b�N
	 * @param user
	 * @param errors
	 * @return true:�߂��� false:�߂��Ă��Ȃ�
	 */
	private boolean isPasswordExpired(User user, Model errors) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			int ret = UserDB.checkPasswordExpiry(user, errors, conn);
			if (ret != 0) {
				String errMsg = ret == 1 ? "root.failed.password.notset." : "root.failed.password.expired.";
				// for ���[�U�[
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage(errMsg + user.getLanKey(), null, null));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
				// for MUR
				category.error("�p�X���[�h�L���������߂����B�߂�l=[" + ret + "]\n");
				result = true;
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("root.failed.check.password.expiry." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"), user.getSys_id());
			// for MUR
			category.error("�p�X���[�h�L�������`�F�b�N�Ɏ��s\n" + ErrorUtility.error2String(e));
			result = true;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

}
