package tyk.drasap.search;

import java.sql.Connection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �������ʂ���A�o�͎w���������Ƃ��ɁATiff�ȊO��I�����Ă����Ƃ��ɁA
 * �m�F����A�N�V�����B
 * @author fumi
 * �쐬��: 2004/01/19
 */
@Controller
public class SearchWarningNotPrintableAction extends BaseAction {
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
	@PostMapping("/searchWarningNotPrintable")
	public String execute(
			SearchWarningNotPrintableForm form,
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

		SearchWarningNotPrintableForm notPrintableForm = form;
		// act�����ɂ�鏈���̐؂蕪��
		if ("continue".equals(notPrintableForm.getAct())) {
			// �p������
			SearchResultForm searchResultForm = (SearchResultForm) session.getAttribute("searchResultForm");
			int requestCount = requestPrint(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// �����������b�Z�[�W��
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.success.print.request." + user.getLanKey(),
						new Object[] { String.valueOf(requestCount) }, null));
			}
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> search_error");
			return "search_error";
		}
		// ���̂܂܌��̌������ʂɖ߂�
		category.debug("--> backResult");
		return "backResult";
	}

	/**
	 * �Q�l�}�̏o�͎w��������B�Q�l�}�o�͈˗��e�[�u���ɏ����o���B
	 * @param searchResultForm
	 * @param user
	 * @param errors
	 * @return �w����������
	 */
	private int requestPrint(SearchResultForm searchResultForm, User user, Model errors) {
		// �ŏ��ɑI�����ꂽ�v�����^�̃I�u�W�F�N�g���擾
		Printer selectedPrinter = null;
		for (int i = 0; i < user.getEnablePrinters().size(); i++) {
			Printer printer = user.getEnablePrinters().get(i);
			if (printer.getId().equals(searchResultForm.getOutputPrinter())) {
				selectedPrinter = printer;
				break;
			}
		}
		//
		int cnt = 0;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.print.request.jp", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�Q�l�}�̏o�͎w���Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return cnt;
	}

}
