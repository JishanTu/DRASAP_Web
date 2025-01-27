package tyk.drasap.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.form.BaseForm;

/**
 * <PRE>
 * DirectLoginForPreviewAction�ɂȂ����߂́A�O�����B
 * �ړI�̓u���E�U��URL�o�[�ɈÍ����\�������̂�h�����߁B
 *
 * ����!! ���̂��߁Asession�Ɋi�[���邽�߁A������ʂ���Z���ԂɘA���ŌĂяo���ꂽ�ꍇ�A
 * ����ɓ��삵�Ȃ��ꍇ������B0.5�b�قǊԊu���󂯂��OK�B
 * komebom����Ăяo���ꍇ�́A���ۂ�0.5�b�󂯂��B
 *
 * RequestParameter�� en_string,fn ���󂯂�B
 * en_string�E�E�E�Í������ꂽ���O�C��ID
 * drwg_no�E�E�E�}�ԁBDRASAP�f�[�^�x�[�X�ɓo�^���ꂽ�`���ŁB���s�̓n�C�t�������̌`���B
 * sys_id�E�E�E�V�X�e��ID�B���O�ɋL�^����܂��B
 * </PRE>
 * @author fumi
 * �쐬�� 2005/03/03
 * �ύX�� $Date: 2005/03/18 04:33:21 $ $Author: fumi $
 */
@Controller
public class DirectLoginForMultiPreviewPreAction extends BaseAction {
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
	@PostMapping(value = { "/directLoginForMultiPreviewPre", "/directLoginForMultiPreviewPre.do" }) //�@�O��IF�Ȃ̂ŁA���X�A�N�Z�XURL���T�|�[�g���Ȃ���΂Ȃ�Ȃ����߁A������.do��t���Ƃ�
	public String execute(
			BaseForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		// �p�����[�^�� en_string ���󂯎��
		String enString = request.getParameter("en_string");
		// �p�����[�^�� drwg_no ���󂯎��
		String sys_id = request.getParameter("sys_id");
		// �p�����[�^�� user_id_col ���󂯎��
		String user_id_col = request.getParameter("user_id_col");
		// �t�H�[������ drwg_no ���󂯎��
		String[] drwgNoArray = request.getParameterValues("DRWG_NO");

		// en_string ��setAttribute����
		category.debug("enString = " + enString);
		if (enString != null) {
			// ���_�C���N�g����ƁArequest��attribute�͏����Ă��܂��̂�
			// session�Ɋi�[����B
			request.getSession().setAttribute("en_string", enString);
		}
		//		if(drwgNoArray.length > 0){
		//			request.getSession().setAttribute("drwgNoArray", drwgNoArray);
		//		}
		request.getSession().setAttribute("drwgNoArray", drwgNoArray);
		if (sys_id != null) {
			request.getSession().setAttribute("sys_id", sys_id);
		}
		if (user_id_col != null) {
			request.getSession().setAttribute("user_id_col", user_id_col);
		}

		// ���_�C���N�g����
		category.debug("redirect --> /directLoginForMultiPreview.do");
		return "/directLoginForMultiPreview.do";
	}

}
