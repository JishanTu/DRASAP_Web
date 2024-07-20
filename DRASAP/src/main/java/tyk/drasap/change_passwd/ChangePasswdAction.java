package tyk.drasap.change_passwd;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.User;
import tyk.drasap.common.UserDB;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

@Controller
public class ChangePasswdAction extends BaseAction {
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
	@PostMapping("/changePasswd")
	public String execute(
			ChangePasswdForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("ChangePassword start");

		ChangePasswdForm passwdForm = form;

		HttpSession session = request.getSession();

		// �Z�b�V��������user���擾
		User user = (User) session.getAttribute("user");

		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false); // �����R�~�b�g���Ȃ�

			//			category.debug("oldpass=" + passwdForm.getOldpass());
			//			category.debug("newpass=" + passwdForm.getNewpass());

			// �p�X���[�h�X�V
			UserDB.updatePassword(user.getId(), passwdForm.getNewpass(), conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message",
					messageSource.getMessage("root.failed.get.userinfo." + user.getLanKey(),
							new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		String page = (String) session.getAttribute("parentPage");
		String forward = "";
		if (Objects.isNull(errors.getAttribute("message"))) {
			if (StringUtils.isEmpty(page) || "Search".equals(page)) {
				// �J�ڌ����}�ʌ�����ʂ̏ꍇ
				category.debug("--> success");
				forward = "successFromSearch";
			} else if ("Login".equals(page)) {
				// �J�ڌ������O�C����ʂ̏ꍇ
				category.debug("--> success");
				forward = "successFromLogin";
			}
			// session����폜
			session.removeAttribute("samePasswdId");
			//			session.removeAttribute("parentPage");
		} else {
			category.debug("--> failed");
			// ���s�̏ꍇ�̓p�X���[�h�ύX��ʂɑJ��
			forward = "failed";
		}

		return forward;

	}
}
