package tyk.drasap.root;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

/**
 * �ʒm�[�E�}�ʂ̍폜
 * @author Y.eto
 * �쐬��: 2006/05/10
 */
@Controller
public class DirectSearchAction extends BaseAction {
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
	@PostMapping(value = { "/directSearch", "/directSearch.do" }) //�@�O��IF�Ȃ̂ŁA���X�A�N�Z�XURL���T�|�[�g���Ȃ���΂Ȃ�Ȃ����߁A������.do��t���Ƃ�
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws SQLException {
		category.debug("start");
		// �p�����[�^�� en_string ���󂯎��
		String enString = request.getParameter("en_string");
		// �p�����[�^�� sys_id ���󂯎��
		String sys_id = request.getParameter("sys_id");
		//String[] attrArray = request.getParameterValues("DRWG_NO");
		//int len = attrArray.length;

		// en_string ��setAttribute����
		category.debug("enString = " + enString);
		if (enString != null) {
			// ���_�C���N�g����ƁArequest��attribute�͏����Ă��܂��̂�
			// session�Ɋi�[����B
			request.getSession().setAttribute("en_string", enString);
		}
		if (sys_id != null) {
			request.getSession().setAttribute("sys_id", sys_id);
		}

		// ���_�C���N�g����
		category.debug("redirect --> /directLoginForPreview.do");
		return "/directLoginForPreview.do";
	}
}
