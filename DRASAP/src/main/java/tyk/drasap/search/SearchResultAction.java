package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.printlog.PrintLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * <PRE>
 * �������ʂ����X�g�\���������Ƃ�Action�B
 * 2005-Mar-4 PrintLoger�ɂ�郍�O��ǉ��B
 * </PRE>
 * �ύX�� $Date: 2005/03/18 04:33:22 $ $Author: fumi $
 * @version 2013/06/24 yamagishi
 */
@Controller
public class SearchResultAction extends BaseAction {
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
	@PostMapping("/result")
	public String execute(
			SearchResultForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		SearchResultForm searchResultForm = form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");

		//
		int offset = Integer.parseInt(searchResultForm.getDispNumberOffest());// ����offset�l
		int dispNumberPerPage = Integer.parseInt(searchResultForm.getDispNumberPerPage());// 1�y�[�W������̕\������
		// act�����ɂ�鏈���̐؂蕪��
		if ("PREV".equals(searchResultForm.getAct())) {
			// �O�ւȂ�
			// �I�t�Z�b�g�l��O�ɂ��炷
			offset = offset - dispNumberPerPage;
			if (offset < 0) {
				offset = 0;
			}
			searchResultForm.setDispNumberOffest(String.valueOf(offset));//�I�t�Z�b�g�l��ύX����
			searchResultForm.setAct("");// act�������N���A
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("NEXT".equals(searchResultForm.getAct())) {
			// ���ւȂ�
			// �I�t�Z�b�g�l�ƌ������ʐ����r���āA�\�Ȃ�I�t�Z�b�g�l��ύX����
			if (searchResultForm.getSearchResultList().size() > offset + dispNumberPerPage) {
				searchResultForm.setDispNumberOffest(String.valueOf(offset + dispNumberPerPage));
			}
			//
			searchResultForm.setAct("");// act�������N���A
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		}
		if ("REFRESH".equals(searchResultForm.getAct())
				|| "CHANGELANGUAGE".equals(searchResultForm.getAct())) {
			// �ĕ\���Ȃ�
			// �I�t�Z�b�g�l���[����
			searchResultForm.setDispNumberOffest("0");//�I�t�Z�b�g�l��ύX����
			searchResultForm.setAct("");// act�������N���A
			session.setAttribute("searchResultForm", searchResultForm);
			// ���[�U�[�}�X�^�[�ɑI�������\�����ڂ��Z�b�g����
			updateUserInfo(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				return "result";
			} else {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}

		}
		if ("CHECK_ON".equals(searchResultForm.getAct())) {
			// �S�ĂɃ`�F�b�N
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				searchResultForm.getSearchResultList().get(i).setSelected(true);
			}
			searchResultForm.setAct("");// act�������N���A
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		} else if ("CHECK_OFF".equals(searchResultForm.getAct())) {
			// �S�Ẵ`�F�b�N���O��
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				searchResultForm.getSearchResultList().get(i).setSelected(false);
			}
			searchResultForm.setAct("");// act�������N���A
			session.setAttribute("searchResultForm", searchResultForm);
			return "result";

		} else if ("PRINT".equals(searchResultForm.getAct())) {
			// ����̎w��������
			// 1) ���̃`�F�b�N���s��
			// - �w�肵�������̃`�F�b�N
			// - �o�̓v���b�^�͑I������Ă���?
			// - �������o�̓T�C�Y���w�肵�Ă���?
			checkForPrint(searchResultForm, user, drasapInfo, errors);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				category.debug("--> search_error");
				return "search_error";
			}
			// 2) ����\�łȂ��}�Ԃ��w�����Ă��Ȃ���?
			if (hasNotPrintable(searchResultForm, user)) {
				category.debug("--> notPrintable");
				return "notPrintable";
			}
			// 2013.06.24 yamagishi modified. start
			// 3) �Q�l�}�o�͗p�e�[�u���ɏo�͂���
			// �d�����N�G�X�g�̒����̂��߂̃��O�B2005-Mar-4 by Hirata.
			//			PrintLoger.info(PrintLoger.ACT_RECEIVE, user.getId());
			PrintLoger.info(PrintLoger.ACT_RECEIVE, user);
			// 2013.06.24 yamagishi modified. end

			int requestCount = requestPrint(searchResultForm, user, errors);
			if (Objects.isNull(errors.getAttribute("message"))) {
				// �����������b�Z�[�W��
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.success.print.request." + user.getLanKey(),
						new Object[] { String.valueOf(requestCount) }, null));
				// �S�Ẵ`�F�b�N���O�� '04.Feb.5
				for (int i = 0; i < searchResultForm.searchResultList.size(); i++) {
					searchResultForm.getSearchResultElement(i).setSelected(false);
				}
				// �A�N�Z�X���O��
				// '04.Nov.23 PrintRequestDB�Ő}�Ԃ��ƂɃ��M���O����悤�ɕύX�����̂�
				// �R�����g�A�E�g����B
				// AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG);

			}

			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			category.debug("--> search_error");
			return "search_error";

		} else if ("OUT_CSV".equals(searchResultForm.getAct())) {
			// �S�������ǂ������Arequest#setAttribute����
			request.setAttribute("OUT_CSV_ALL", searchResultForm.getOutCsvAll());
			// �������ʂ��t�@�C���o�͂���
			category.debug("--> out_csv");
			return "out_csv";

		} else if ("ACLV_CHG".equals(searchResultForm.getAct())) {
			// �A�N�Z�X���x���̕ύX��ʂ�
			category.debug("--> aclv_change");
			return "aclv_change";
		} else if ("DELETEDWG".equals(searchResultForm.getAct())) {
			category.debug("--> DELETEDWG");
			session.setAttribute("searchResultForm", searchResultForm);
			return "deletedwg";
			// 2019.10.17 yamamoto add. start
		} else if ("MULTI_PDF".equals(searchResultForm.getAct())) {
			session.setAttribute("searchResultForm", searchResultForm);
			// �I�������}�ʂ�1�t�@�C��PDF�ɂ��ă_�E�����[�h����
			category.debug("--> MULTI_PDF");
			return "multi_pdf";
			// 2019.10.17 yamamoto add. end
			// 2020.03.10 yamamoto add. start
		} else if ("PDF_ZIP".equals(searchResultForm.getAct())) {
			session.setAttribute("searchResultForm", searchResultForm);
			// �I�������}�ʂ�zip�Ń_�E�����[�h����
			category.debug("--> PDF_ZIP");
			return "multi_pdf";
			// 2020.03.10 yamamoto add. end
		}
		return null;
	}

	/**
	 * �ĕ\���ŕ\�����ڂ�ύX�����Ƃ��ɁA���̕\�����ڂ����[�U�[�}�X�^�ɓo�^����B
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 */
	private void updateUserInfo(SearchResultForm searchResultForm, User user, Model errors) {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			String strSql1 = "update USER_MASTER set " +
					" VIEW_SELCOL1=?, VIEW_SELCOL2=?, VIEW_SELCOL3=?, VIEW_SELCOL4=?, VIEW_SELCOL5=?, VIEW_SELCOL6=?" +
					" where USER_ID=?";
			pstmt1 = conn.prepareStatement(strSql1);
			pstmt1.setString(1, searchResultForm.getDispAttr1());
			pstmt1.setString(2, searchResultForm.getDispAttr2());
			pstmt1.setString(3, searchResultForm.getDispAttr3());
			pstmt1.setString(4, searchResultForm.getDispAttr4());
			pstmt1.setString(5, searchResultForm.getDispAttr5());
			pstmt1.setString(6, searchResultForm.getDispAttr6());
			pstmt1.setString(7, user.getId());
			pstmt1.executeUpdate();
			// ���[�U�[Object�ɂ��Z�b�g����
			user.setViewSelCol1(searchResultForm.getDispAttr1());
			user.setViewSelCol2(searchResultForm.getDispAttr2());
			user.setViewSelCol3(searchResultForm.getDispAttr3());
			user.setViewSelCol4(searchResultForm.getDispAttr4());
			user.setViewSelCol5(searchResultForm.getDispAttr5());
			user.setViewSelCol6(searchResultForm.getDispAttr6());

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.update.user." + user.getLanKey(), new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���[�U�[���̍X�V�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	// 2013.06.26 yamagishi modified. start
	/**
	 * ����s�\�Ȑ}�ʂ��`�F�b�N����Ă����� true ��Ԃ��B
	 * Tiff�ȊO�ł���B�܂��͈���������Ȃ��B
	 * @param searchResultForm
	 * @return Tiff�ȊO�̐}�ʂ��`�F�b�N����Ă����� true
	 */
	//	private boolean hasNotPrintable(SearchResultForm searchResultForm, User user){
	private boolean hasNotPrintable(SearchResultForm searchResultForm, User user) throws Exception {
		Connection conn = null;

		try {
			conn = ds.getConnection();

			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// �I������Ă��āA�����[�U�[������ł��Ȃ���΁E�E�E�x���Ώ�
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
				//				if(searchResultElement.isSelected() && ! user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && !user.isPrintableByReq(searchResultElement, conn, i > 0)) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
	// 2013.06.26 yamagishi modified. end

	// 2013.06.26 yamagishi modified. start
	/**
	 * ����w���܂��̃`�F�b�N���s���B
	 * �G���[������΁Aerrors�ɉ�����B
	 * @param searchResultForm
	 * @param drasapInfo
	 * @param errors
	 */
	//	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, Model errors){
	private void checkForPrint(SearchResultForm searchResultForm, User user, DrasapInfo drasapInfo, Model errors) throws Exception {
		Connection conn = null;

		try {
			conn = ds.getConnection();

			// 1) �w�肵�������̃`�F�b�N
			int selectedDrwgCount = 0;
			for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
				// �I������Ă��āA�����[�U�[������\�Ȃ�
				SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
				//				if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
				if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn, i > 0)) {
					selectedDrwgCount++;// �C���N�������g

				}
			}
			if (selectedDrwgCount == 0) {
				// �P���I�����Ă��Ȃ�
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.no.selected." + user.getLanKey(), null, null));

			} else if (selectedDrwgCount > drasapInfo.getPrintRequestMax()) {
				// ����𒴂����������w��
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.print.request.max." + user.getLanKey(),
						new Object[] { String.valueOf(drasapInfo.getPrintRequestMax()), String.valueOf(selectedDrwgCount) }, null));
			}
			// 2) �o�̓v���b�^�͑I������Ă���?
			if (searchResultForm.getOutputPrinter() == null || "".equals(searchResultForm.getOutputPrinter())) {
				String requiredStr;
				if ("jp".equals(user.getLanKey())) {
					requiredStr = "�o�̓v���b�^";
				} else {
					requiredStr = "PLOTTER";
				}
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.required." + user.getLanKey(), new Object[] { requiredStr }, null));
			} else {
				// �o�͐悪�w�肳��Ă���΁A�o�̓T�C�Y�̎w����`�F�b�N����
				Printer selectedPrinter = user.getPrinter(searchResultForm.getOutputPrinter());
				for (int i = 0; i < searchResultForm.getSearchResultList().size(); i++) {
					// �I������Ă��āA�����[�U�[������\�Ȃ�
					SearchResultElement searchResultElement = searchResultForm.getSearchResultElement(i);
					//					if(searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement)){
					if (searchResultElement.isSelected() && user.isPrintableByReq(searchResultElement, conn)) {
						// ���}�̃T�C�Y�A�w�肵���T�C�Y�A����������v�����^�̑g�ݍ��킹����A
						// �Q�l�}�o�͗p�e�[�u���̏o�̓T�C�Y�ɃZ�b�g����T�C�Y�����߂�
						// ���̒l��searchResultElement�ɂ����Ƃ���
						searchResultElement.setDecidePrintSize(
								DrasapUtil.decidePrintSizeForRequest(
										searchResultElement.getAttr("DRWG_SIZE"), // ���}�̃T�C�Y
										searchResultElement.getPrintSize(), // �w��T�C�Y
										selectedPrinter));// �v�����^�[
						if (searchResultElement.getDecidePrintSize() == null) {
							// �w��T�C�Y���G���[
							MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.print.nomatch.size." + user.getLanKey(),
									new Object[] { searchResultElement.getDrwgNoFormated() }, null));
						} else {
							//�v�����^�̍ő����T�C�Y���擾����
							searchResultElement.setPrinterMaxSize(selectedPrinter.getMaxSize());
						}
					}
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	// 2013.06.26 yamagishi modified. end
	/**
	 * �Q�l�}�̏o�͎w��������B�Q�l�}�o�͈˗��e�[�u���ɏ����o���B
	 * @param searchResultForm
	 * @param user
	 * @param errors
	 * @return �w����������
	 */
	private int requestPrint(SearchResultForm searchResultForm, User user, Model errors) {
		// �I�����ꂽ�v�����^�̃I�u�W�F�N�g���擾
		Printer selectedPrinter = user.getPrinter(searchResultForm.getOutputPrinter());
		//
		int cnt = 0;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			cnt = PrintRequestDB.insertRequest(searchResultForm.getSearchResultList(), selectedPrinter, user, conn);

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.print.request." + user.getLanKey(), new Object[] { e.getMessage() }, null));
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
