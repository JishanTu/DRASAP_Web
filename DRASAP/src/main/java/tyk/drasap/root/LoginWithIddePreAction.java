package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

/**
 * LoginWithIddeAction�ɂȂ����߂́A�O�����B
 * �ړI�̓u���E�U��URL�o�[�ɈÍ����\�������̂�h�����߁B
 * RequestParameter�� en_string,fn ���󂯂�B
 * en_string�E�E�E�Í������ꂽ���O�C��ID
 * fn�E�E�E�t�@���N�V����
 */
@Controller
public class LoginWithIddePreAction extends BaseAction {
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
	@PostMapping("/loginWithIddePre")
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// �p�����[�^�� en_string ���󂯎��
		String enString = request.getParameter("en_string");
		// �p�����[�^fn��ǉ� '04.May.13
		String fn = request.getParameter("fn");

		// en_string ��setAttribute����
		category.debug("enString = " + enString);
		if (enString == null) {
			return "timeout";
		}
		// ���_�C���N�g����ƁArequest��attribute�͏����Ă��܂��̂�
		// session�Ɋi�[����B
		request.getSession().setAttribute("en_string", enString);
		if (fn != null) {
			request.getSession().setAttribute("fn", fn);
		}

		// ���_�C���N�g����
		category.debug("redirect --> /loginWithIdde.do");
		return "success";
	}

}
