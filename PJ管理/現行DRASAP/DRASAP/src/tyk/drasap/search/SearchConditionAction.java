package tyk.drasap.search;

import static tyk.drasap.common.DrasapPropertiesFactory.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import tyk.drasap.common.CookieManage;
import tyk.drasap.common.CsvItemStrList;
import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.DateCheck;
import tyk.drasap.common.DrasapInfo;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.ProfileString;
import tyk.drasap.common.StringCheck;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;

/**
 * �������s��Action�B
 *
 * @version 2013/06/14 yamagishi
 */
@SuppressWarnings("deprecation")
public class SearchConditionAction extends Action {
	private static DataSource ds;
	private static Category category = Category.getInstance(SearchConditionAction.class.getName());
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}

	// --------------------------------------------------------- Instance Variables

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
	public ActionForward execute(ActionMapping mapping,ActionForm form,
		HttpServletRequest request,HttpServletResponse response) throws Exception {
		category.debug("start");
		ActionMessages errors = new ActionMessages();
		//
		SearchConditionForm searchConditionForm = (SearchConditionForm) form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null){
			return mapping.findForward("timeout");
		}
		DrasapInfo drasapInfo = (DrasapInfo) session.getAttribute("drasapInfo");

		String lanKey = user.getLanKey();

		// �J�ڌ���session�ɕێ�
		session.setAttribute("parentPage", "Search");

		session.setAttribute("default_css", lanKey.equals("jp")?"default.css":"defaultEN.css");
		// act�����ŏ����𕪂���
		category.debug("act������" + searchConditionForm.act);
		if("search".equals(searchConditionForm.act)){
			System.currentTimeMillis();
			// ���[�U�[�̍ō��A�N�Z�X���x���l���A1��菬�����Ȃ猟���ł��Ȃ�
			if(user.getMaxAclValue().compareTo("1") < 0){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.no_authority.search." + user.getLanKey()));
				saveErrors(request, errors);
				return mapping.findForward("error");
			}
			// act��'search'���ݒ肳��Ă���Ƃ�
			// 1) ���������̊m�F
			if(! checkSearchCondition(searchConditionForm, drasapInfo, user, errors)){
				saveErrors(request, errors);
				return mapping.findForward("error");
			}
// 2013.06.27 yamagishi add. start
			String[] multipleDrwgNoArray = null;

// 2020.03.11 yamamoto modify. start
			// �����}�Ԃ̎w�肠��̏ꍇ
			if ((searchConditionForm.multipleDrwgNo != null
					&& searchConditionForm.multipleDrwgNo.length() > 0))
			{
				// AND���� �܂��� �}�Ԏw�菇���L���̏ꍇ
				if(("AND".equals(searchConditionForm.getEachCondition())
					|| searchConditionForm.isOrderDrwgNo()))
				{
					// �����}�Ԃ̎w�茏�����m�F
					multipleDrwgNoArray = multipleDrwgNoCount(searchConditionForm, drasapInfo);
					if (multipleDrwgNoArray.length > drasapInfo.getMultipleDrwgNoMax()) {
						// �����}�Ԏw�莞�̌����\�����𒴂����ꍇ
						request.setAttribute("hit", String.valueOf(multipleDrwgNoArray.length));
						category.debug("--> overLimitMultipleDrwgNo");
						return mapping.findForward("overLimitMultipleDrwgNo");
					}
					// 2) �����}�ԁA���������̊m�F
					if (!checkSearchConditionMultipleDrwgNo(multipleDrwgNoArray, searchConditionForm, drasapInfo, user, errors)) {
						saveErrors(request, errors);
						return mapping.findForward("error");
					}
				}
			}
// 2020.03.11 yamamoto modify. end
// 2013.06.27 yamagishi add. end

// 2013.06.28 yamagishi modified. start
//			// �܂�����������Hit�����������m�F
//			int hit = countHit(searchConditionForm, user, request, errors);
			// �܂�����������Hit�����������m�F
			int hit = countHit(searchConditionForm, user, request, errors, multipleDrwgNoArray);
// 2013.06.28 yamagishi modified. end
			if(hit == -1){
				// �G���[�����������ꍇ
				saveErrors(request, errors);
				return mapping.findForward("error");
			} else if(hit > drasapInfo.getSearchLimitCount()){
				// ���p�\�����𒴂����ꍇ
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overLimitHit");
				return mapping.findForward("overLimitHit");
			} else if(hit > drasapInfo.getSearchWarningCount()){
				// �x�������𒴂����ꍇ
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overHit");
				return mapping.findForward("overHit");
			} else {
				// �x�������ȉ��̏ꍇ
				category.debug("--> searchResult");
				return mapping.findForward("searchResult");
			}

		} else if("CHANGELANGUAGE".equals(searchConditionForm.act)){
			user.setLanguage(searchConditionForm.getLanguage());
			setFormData(searchConditionForm, user);
			getScreenItemStrList(searchConditionForm, user);
			session.setAttribute("user", user);
			session.setAttribute("searchConditionForm", searchConditionForm);

			// ����ݒ���N�b�L�[�ɕۑ�
			CookieManage langCookie = new CookieManage();
			langCookie.setCookie(response, user, "Language", searchConditionForm.getLanguage());

			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");

			// �\������ύX��
			category.debug("--> changeLanguage");
			return mapping.findForward("changeLanguage");
		} else if("multipreview".equals(searchConditionForm.act)){
			// �O���C���^�[�t�F�C�X����̃}���`�v���r���[

			// �N�b�L�[���猾��ݒ���擾
			CookieManage langCookie = new CookieManage();
			lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);
			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");

			// �C�j�V�����C�Y
			setFormData(searchConditionForm, user);
			searchConditionForm.setLanguage(user.getLanguage());
			// ��ʕ\��������擾
			getScreenItemStrList(searchConditionForm, user);

			// �}���`�v���r���[�p�ɓn���ꂽ�}�Ԃ����������ɐݒ肷��
			setMultiViewDrwgNos (searchConditionForm, request);
			session.setAttribute("searchConditionForm", searchConditionForm);

// 2013.06.28 yamagishi modified. start
//			// ����������Hit�����������m�F
//			int hit = countHit(searchConditionForm, user, request, errors);
			// ����������Hit�����������m�F
			int hit = countHit(searchConditionForm, user, request, errors, null);
// 2013.06.28 yamagishi modified. end
			if(hit == -1){
				// �G���[�����������ꍇ
				saveErrors(request, errors);
				return mapping.findForward("error");
			} else if(hit > drasapInfo.getSearchLimitCount()){
				// ���p�\�����𒴂����ꍇ
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overLimitHit");
				return mapping.findForward("overLimitHit");
			} else if(hit > drasapInfo.getSearchWarningCount()){
				// �x�������𒴂����ꍇ
				request.setAttribute("hit", String.valueOf(hit));
				category.debug("--> overHit");
				return mapping.findForward("overHit");
			} else {
				// �x�������ȉ��̏ꍇ
				category.debug("--> multipreview");
				return new ActionForward(mapping.getInput());
			}
		} else {
			// �N�b�L�[���猾��ݒ���擾
			CookieManage langCookie = new CookieManage();
			lanKey = langCookie.getCookie (request, user, "Language");
			if (lanKey == null || lanKey.length() == 0) lanKey = "Japanese";
			user.setLanguage (lanKey);

			session.setAttribute("default_css", user.getLanKey().equals("jp")?"default.css":"defaultEN.css");
			// act�ɉ����ݒ肳��Ă��Ȃ��Ƃ�
			// ������ʂ�\������
			setFormData(searchConditionForm, user);
			searchConditionForm.setLanguage(user.getLanguage());
			// ��ʕ\��������擾
			getScreenItemStrList(searchConditionForm, user);

			session.setAttribute("searchConditionForm", searchConditionForm);
			return new ActionForward(mapping.getInput());
		}
	}

	/**
	 * SearchConditionForm�ɏ����l���Z�b�g����B
	 * @param searchConditionForm
	 * @param user
	 */
	private void setFormData(SearchConditionForm searchConditionForm, User user){
		// ���������̍��ڂ��Z�b�g���� ////////////////////////////////////////////
		// ����1) �L�[��
		searchConditionForm.conditionKeyList = new ArrayList<String>();
		searchConditionForm.conditionKeyList.add("");
		searchConditionForm.conditionKeyList.add("DRWG_NO");
		// ���̃��[�U�[���g�p�ł��鑮���S�Ă�
		// '04.Apr.16 �ύX by Hirata
		SearchUtil sUtil = new SearchUtil();
		searchConditionForm.conditionKeyList.addAll(sUtil.createEnabledAttrList(user, true));

		// ����2) ���̂�
		searchConditionForm.conditionNameList = new ArrayList<String>();
		searchConditionForm.conditionNameList.add("");
//		searchConditionForm.conditionNameList.add("�}��");
		searchConditionForm.conditionNameList.add(sUtil.getSearchAttr(user, "DRWG_NO", false));
		// ���̃��[�U�[���g�p�ł��鑮���S�Ă�
		// '04.Apr.16 �ύX by Hirata
		searchConditionForm.conditionNameList.addAll(sUtil.createEnabledAttrList(user, false));


		// �O��̌����������Z�b�g���� ////////////////////////////////////////////
		searchConditionForm.setCondition1(user.getSearchSelCol1());
		if(searchConditionForm.condition1 == null || searchConditionForm.condition1.equals("")){
			// ��������1�����ݒ�̏ꍇ�A�u�}�ԁv���Z�b�g����
			searchConditionForm.setCondition1("DRWG_NO");
		}
		searchConditionForm.setCondition2(user.getSearchSelCol2());
		searchConditionForm.setCondition3(user.getSearchSelCol3());
		searchConditionForm.setCondition4(user.getSearchSelCol4());
		searchConditionForm.setCondition5(user.getSearchSelCol5());
		// �O��̕\���������Z�b�g����
		searchConditionForm.setDisplayCount(user.getDisplayCount());
		if(searchConditionForm.displayCount == null || searchConditionForm.displayCount.equals("")){
			// �\�����������ݒ�̏ꍇ�A50�����Z�b�g����
			searchConditionForm.setDisplayCount("50");
		}
		// �\�[�g�����̃v���_�E�����Z�b�g����
		searchConditionForm.sortOrderKeyList = new ArrayList<String>();
		searchConditionForm.sortOrderKeyList.add("");
		searchConditionForm.sortOrderKeyList.add("1");
		searchConditionForm.sortOrderKeyList.add("2");
		searchConditionForm.sortOrderKeyList.add("3");
		searchConditionForm.sortOrderKeyList.add("4");
		searchConditionForm.sortOrderKeyList.add("5");
		searchConditionForm.sortOrderNameList = searchConditionForm.sortOrderKeyList;
		// �u�S�Ă̑����������v��AND���f�t�H���g�� '04.Feb.5�ύX
		searchConditionForm.eachCondition="AND";


		searchConditionForm.searchHelpMsg = getSearchHelpMsg(user.getLanguage());

		// 2019.09.25 yamamoto add.
		// �G���[���b�Z�[�W�Z�b�g
		searchConditionForm.setChangeLangErrMsg(getErrorMsg(user, "search.failed.change.lang"));
		searchConditionForm.setLogoutErrMsg(getErrorMsg(user, "search.failed.logout"));
		// 2020.03.17 yamamoto add.
		searchConditionForm.setlistOrderErrMsg(getErrorMsg(user, "search.failed.search.listOrder"));
	}
	/**
	 * �������������������͂���Ă��邩?
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @return ���������͂���Ă���� true
	 */
	private boolean checkSearchCondition(SearchConditionForm searchConditionForm, DrasapInfo drasapInfo, User user, ActionMessages errors){
		// �ŏ��ɁA1�ł����������͂���Ă��邩�m�F
		boolean inputed = false;// 1�ł����͂���Ă����� true
		if(isInputed(searchConditionForm.condition1, searchConditionForm.condition1Value)){
			inputed = true;
		} else if(isInputed(searchConditionForm.condition2, searchConditionForm.condition2Value)){
			inputed = true;
		} else if(isInputed(searchConditionForm.condition3, searchConditionForm.condition3Value)){
			inputed = true;
		} else if(isInputed(searchConditionForm.condition4, searchConditionForm.condition4Value)){
			inputed = true;
		} else if(isInputed(searchConditionForm.condition5, searchConditionForm.condition5Value)){
			inputed = true;
// 2013.06.27 yamagishi add. start
		} else if (searchConditionForm.eachCondition.equals("AND")) {
			String multiNotemp = searchConditionForm.multipleDrwgNo.replace(System.lineSeparator(), "");
				// ���s�R�[�h�����������͒l�Ŕ���
			if (isInputed("multipleDrwnNo", multiNotemp)) {
				inputed = true;
			}
// 2013.06.27 yamagishi add. end
// 2020.03.11 yamamoto modify. start
		} else if (searchConditionForm.isOrderDrwgNo()) {
			// �}�Ԏw�菇�Ƀ`�F�b�N�������Ă���ꍇ
			String multiNotemp = searchConditionForm.multipleDrwgNo.replace(System.lineSeparator(), "");
			// ���s�R�[�h�����������͒l�Ŕ���
			if (isInputed("multipleDrwnNo", multiNotemp)) {
				inputed = true;
			}
		}
// 2020.03.11 yamamoto modify. end
		if(!inputed){// ����������1�����͂���Ă��Ȃ��ꍇ
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.required.search.condition." + user.getLanKey()));
			return false;
		}
		// ���������Ƃ��Ďw�肳�ꂽ�l�����������߂ł��邩
		boolean isRight = true;
		if(! isRightCondition(searchConditionForm.condition1, searchConditionForm.condition1Value, drasapInfo, user, errors)){
			isRight = false;
		}
		if(! isRightCondition(searchConditionForm.condition2, searchConditionForm.condition2Value, drasapInfo, user, errors)){
			isRight = false;
		}
		if(! isRightCondition(searchConditionForm.condition3, searchConditionForm.condition3Value, drasapInfo, user, errors)){
			isRight = false;
		}
		if(! isRightCondition(searchConditionForm.condition4, searchConditionForm.condition4Value, drasapInfo, user, errors)){
			isRight = false;
		}
		if(! isRightCondition(searchConditionForm.condition5, searchConditionForm.condition5Value, drasapInfo, user, errors)){
			isRight = false;
		}
		return isRight;
	}

// 2013.06.27 yamagishi add. start
	/**
	 * �������������������͂���Ă��邩?
	 * @param multipleDrwgNo
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @return ���������͂���Ă���� true
	 */
	private boolean checkSearchConditionMultipleDrwgNo(String[] multipleDrwgNo, SearchConditionForm searchConditionForm, DrasapInfo drasapInfo, User user, ActionMessages errors) {
		// ���������Ƃ��Ďw�肳�ꂽ�l�����������߂ł��邩
		boolean isRight = true;

		for (String drwgNo : multipleDrwgNo) {
			// �����}�Ԃ̑S�����`�F�b�N
			if (!isRightCondition("DRWG_NO", drwgNo, drasapInfo, user, errors)) {
				isRight = false;
				break;
			}
		}
		return isRight;
	}
// 2013.06.27 yamagishi add. end

	/**
	 * ���������̖��̂ƒl�̗��������͂���Ă��邩?
	 * @param conditionName ���������̖���
	 * @param conditionValue ���������̒l
	 * @return ���̂ƒl�̗��������͂���Ă���� true
	 */
	private boolean isInputed(String conditionName, String conditionValue){
		return (conditionName != null && conditionName.length() > 0 &&
		conditionValue != null && conditionValue.length() > 0);
	}
	/**
	 * ���������̒l���������w�肳��Ă��邩�m�F����B
	 * 1) �J���}�܂��̓A���o�T���h���������g�p����Ă��邩�E�E�E�ǂ̑���������
	 * 2) �E�E�E���t�^����
	 * @param conditionName ���������̖���
	 * @param conditionValue ���������̒l
	 * @param errors
	 * @return �������w�肳��Ă���� true
	 */
	private boolean isRightCondition(String conditionName, String conditionValue, DrasapInfo drasapInfo, User user, ActionMessages errors){
		boolean isRight = true;// ���������true
		if(conditionName != null && conditionName.length() > 0 &&
			conditionValue != null && conditionValue.length() > 0){
			// ���O�ƒl�̗��������͂���Ă���΃`�F�b�N����

			// �ǂ̑��������ʂ̃`�F�b�N /////////////////////////////////////////////////////////////
			// �J���}�܂��̓A���o�T���h���������g�p����Ă��邩?
			char[] c = conditionValue.toCharArray();
			int lastIndex = -1;// �J���}�܂��̓A���o�T���h�����ꂽindex
			for(int i = 0; i < c.length; i++){
				if(c[i] == ',' || c[i] == '&'){
					// �J���}�܂��̓A���o�T���h�����ꂽ
					if(i == (lastIndex+1) || i == 0 || i == c.length-1){
						// �J���}�܂��̓A���o�T���h�������Ă����NG
						// �J���}�܂��̓A���o�T���h�͍ŏ��A�Ō��NG
						isRight = false;
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.separate." + user.getLanKey(), conditionName));
						break;
					}
					// lastIndex��u��������
					lastIndex = i;
				}
			}
			// ���t�^�����̃`�F�b�N /////////////////////////////////////////////////////////////////
			if(conditionName.equals("CREATE_DATE")){
				// �J���}�܂��̓A���o�T���h�ŋ�؂�ꂽ�������
				StringTokenizer st = new StringTokenizer(conditionValue, ",&");
				while(st.hasMoreTokens()){
					String token = st.nextToken();
					token = token.replace('�@', ' ');// �S�p�X�y�[�X�𔼊p�X�y�[�X��
					token = token.trim();// trim���Ĕ��p�X�y�[�X������
					int sepIndex = token.indexOf('-');
					if(sepIndex == -1){
						// �u-�v���g�p�����͈͎w�肪�Ȃ��ꍇ
						int ymd = DateCheck.convertIntYMD(token);
						if(ymd == -1 || !DateCheck.isDate(ymd)){
							// ���t�Ƃ��ĉ��߂ł��Ȃ�
							isRight = false;
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.dateformat." + user.getLanKey(), conditionName));
							break;
						}
					} else {// �u-�v���g�p���Ĕ͈͎w�肳��Ă���ꍇ
						// �u-�v�𗘗p���ĊJ�n�A�I���̕�������擾
						String fromDate = null;// �J�n
						String toDate = null;// �I��
						if(sepIndex > 0){
							fromDate = token.substring(0, sepIndex);
						}
						if(sepIndex < token.length()-1){
							toDate = token.substring(sepIndex+1);
						}
						// ���t�Ƃ��ĉ��߂ł��邩
						// 1) �J�n��
						int fromYmd = 0;
						if(fromDate != null){
							fromYmd = DateCheck.convertIntYMD(fromDate);
							if(fromYmd == -1 || !DateCheck.isDate(fromYmd)){
								// ���t�Ƃ��ĉ��߂ł��Ȃ�
								isRight = false;
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.dateformat." + user.getLanKey(), conditionName));
								break;
							}
						}
						// 2) �I����
						int toYmd = 0;
						if(toDate != null){
							toYmd = DateCheck.convertIntYMD(toDate);
							if(toYmd == -1 || !DateCheck.isDate(toYmd)){
								// ���t�Ƃ��ĉ��߂ł��Ȃ�
								isRight = false;
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.dateformat." + user.getLanKey(), conditionName));
								break;
							}
						}
						// �J�n > �I�� �łȂ�?
						if(fromDate != null && toDate != null){
							if(fromYmd > toYmd){
								isRight = false;
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.date.fromto." + user.getLanKey(), conditionName));
								break;
							}
						}
					}
				}
			} else {
				// �����^�����̃`�F�b�N /////////////////////////////////////////////////////////
				// �J���}�܂��̓A���o�T���h�ŋ�؂�ꂽ�������
				StringTokenizer st = new StringTokenizer(conditionValue, ",&");
				while(st.hasMoreTokens()){
					String token = st.nextToken();
					token = token.replace('�@', ' ');// �S�p�X�y�[�X�𔼊p�X�y�[�X��
					token = token.trim();// trim���Ĕ��p�X�y�[�X������
					if(token.length() == 0){
						isRight = false;
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.separate." + user.getLanKey(), conditionName));
						break;
					} else {
						// �}�Ԃ̏ꍇ
						if(conditionName.equals("DRWG_NO")){
							// *���������������𐔂���
							char[] tempArray = token.toCharArray();
							int exceptLength = 0;
							for(int i = 0; i < tempArray.length; i++){
								if(tempArray[i] != '*'){
									exceptLength++;
								}
							}
							// �ݒ肳�ꂽ��������菭�Ȃ����
							if(exceptLength < drasapInfo.getMinimumIuputDrwgChar()){
								isRight = false;
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.miss.condition.drwgno.short." + user.getLanKey(),
														String.valueOf(drasapInfo.getMinimumIuputDrwgChar())));
								break;
							}
						}
					}
				}
			}// END�E�E�E�����^�����̃`�F�b�N
		}// END�E�E�E���O�ƒl�̗��������͂���Ă���΃`�F�b�N����
		return isRight;
	}

	/**
	 * ���������Ɉ�v�����������J�E���g���Ԃ��B
	 * �G���[�����������ꍇ�� -1 ��Ԃ��B
	 * @param searchConditionForm
	 * @param user
	 * @param errors
	 * @param multipleDrwgNo
	 * @return ���������Ɉ�v���������B
	 * �G���[�����������ꍇ�� -1�B
	 */
// 2013.06.28 yamagishi modified. start
//	private int countHit(SearchConditionForm searchConditionForm, User user,
//						HttpServletRequest request, ActionMessages errors){
	private int countHit(SearchConditionForm searchConditionForm, User user,
						HttpServletRequest request, ActionMessages errors, String[] multipleDrwgNo){
// 2013.06.28 yamagishi modified. end

		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt2 = null;
		int hit = -1;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			// �����𐔂���Ώۂ�INDEX_FILE_VIEW�ɕύX '04.Mar.2
			StringBuffer sbSql1 = new StringBuffer("select count(*) CNT from INDEX_FILE_VIEW");
			sbSql1.append(" ");
// 2013.09.13 yamagishi modified. start
			// Where�����ȉ����쐬���t������
			String sqlWhere = createSqlWhere(searchConditionForm, user, conn, multipleDrwgNo);
			sbSql1.append(sqlWhere);
			request.setAttribute("SQL_WHERE", sqlWhere);// ����SearchResultPreAction�Ŏg�p���邽�߂�request�ɃZ�b�g����
			request.setAttribute("SQL_ORDER", createSqlOrder(searchConditionForm));// ����SearchResultPreAction�Ŏg�p���邽�߂�request�ɃZ�b�g����
			if ("multipreview".equals(searchConditionForm.act)) {
				request.getSession().setAttribute("SQL_WHERE", sqlWhere);
				request.getSession().setAttribute("SQL_ORDER", createSqlOrder(searchConditionForm));
			}
// 2013.09.13 yamagishi modified. end
			//
			stmt1 = conn.createStatement();
			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if(rs1.next()){
				hit = rs1.getInt("CNT");
				category.debug("������ " + hit);
			}
// 2020.03.11 yamamoto modified. start
			if (searchConditionForm.isOrderDrwgNo()) {
				// ���[�U�[�Ǘ��}�X�^�[�Ɍ����J�����A�\���������Z�b�g����B
				// �}�Ԏw�菇�̃`�F�b�N�n�m�̏ꍇ�͌������������Ȃ̂ŁA
				// �O��̌���������ێ������܂܂Ƃ���B
				String strSql2 = "update USER_MASTER set DISPLAY_COUNT=?," +
								" VIEW_SELCOL1=?, VIEW_SELCOL2=?, VIEW_SELCOL3=?, VIEW_SELCOL4=?, VIEW_SELCOL5=?, VIEW_SELCOL6=?" +
								" where USER_ID=?";
				pstmt2 = conn.prepareStatement(strSql2);
				pstmt2.setString(1, searchConditionForm.getDisplayCount());
				pstmt2.setString(2, searchConditionForm.getDispAttr1());
				pstmt2.setString(3, searchConditionForm.getDispAttr2());
				pstmt2.setString(4, searchConditionForm.getDispAttr3());
				pstmt2.setString(5, searchConditionForm.getDispAttr4());
				pstmt2.setString(6, searchConditionForm.getDispAttr5());
				pstmt2.setString(7, searchConditionForm.getDispAttr6());
				pstmt2.setString(8, user.getId());
				pstmt2.executeUpdate();
				// ���[�U�[Object�ɂ��Z�b�g����
				user.setDisplayCount(searchConditionForm.getDisplayCount());
				user.setViewSelCol1(searchConditionForm.getDispAttr1());
				user.setViewSelCol2(searchConditionForm.getDispAttr2());
				user.setViewSelCol3(searchConditionForm.getDispAttr3());
				user.setViewSelCol4(searchConditionForm.getDispAttr4());
				user.setViewSelCol5(searchConditionForm.getDispAttr5());
				user.setViewSelCol6(searchConditionForm.getDispAttr6());
			} else 	if(!"multipreview".equals(searchConditionForm.act)){
				// ���������́A���[�U�[�Ǘ��}�X�^�[�Ɍ����J�����A�\���������Z�b�g����B
				String strSql2 = "update USER_MASTER set DISPLAY_COUNT=?," +
								" SEARCH_SELCOL1=?, SEARCH_SELCOL2=?, SEARCH_SELCOL3=?, SEARCH_SELCOL4=?, SEARCH_SELCOL5=?," +
								" VIEW_SELCOL1=?, VIEW_SELCOL2=?, VIEW_SELCOL3=?, VIEW_SELCOL4=?, VIEW_SELCOL5=?, VIEW_SELCOL6=?" +
								" where USER_ID=?";
				pstmt2 = conn.prepareStatement(strSql2);
				pstmt2.setString(1, searchConditionForm.getDisplayCount());
				pstmt2.setString(2, searchConditionForm.getCondition1());
				pstmt2.setString(3, searchConditionForm.getCondition2());
				pstmt2.setString(4, searchConditionForm.getCondition3());
				pstmt2.setString(5, searchConditionForm.getCondition4());
				pstmt2.setString(6, searchConditionForm.getCondition5());
				pstmt2.setString(7, searchConditionForm.getDispAttr1());
				pstmt2.setString(8, searchConditionForm.getDispAttr2());
				pstmt2.setString(9, searchConditionForm.getDispAttr3());
				pstmt2.setString(10, searchConditionForm.getDispAttr4());
				pstmt2.setString(11, searchConditionForm.getDispAttr5());
				pstmt2.setString(12, searchConditionForm.getDispAttr6());
				pstmt2.setString(13, user.getId());
				pstmt2.executeUpdate();
				// ���[�U�[Object�ɂ��Z�b�g����
				user.setDisplayCount(searchConditionForm.getDisplayCount());
				user.setSearchSelCol1(searchConditionForm.getCondition1());
				user.setSearchSelCol2(searchConditionForm.getCondition2());
				user.setSearchSelCol3(searchConditionForm.getCondition3());
				user.setSearchSelCol4(searchConditionForm.getCondition4());
				user.setSearchSelCol5(searchConditionForm.getCondition5());
				user.setViewSelCol1(searchConditionForm.getDispAttr1());
				user.setViewSelCol2(searchConditionForm.getDispAttr2());
				user.setViewSelCol3(searchConditionForm.getDispAttr3());
				user.setViewSelCol4(searchConditionForm.getDispAttr4());
				user.setViewSelCol5(searchConditionForm.getDispAttr5());
				user.setViewSelCol6(searchConditionForm.getDispAttr6());
			}
// 2020.03.11 yamamoto modified. end
		} catch(Exception e){
			// for ���[�U�[
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("search.failed.search.list." + user.getLanKey(),e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try{ rs1.close(); } catch(Exception e) {}
			try{ stmt1.close(); } catch(Exception e) {}
			try{ pstmt2.close(); } catch(Exception e) {}
			try{ conn.close(); } catch(Exception e) {}
		}

		return hit;
	}

// 2013.06.27 yamagishi add. start
	/**
	 * �w�肳�ꂽ�����}�Ԃ�z��ŕԂ��B
	 * �i������������𒴂��Ă���ꍇ�A����{1���Ƃ���j
	 * @param searchConditionForm
	 * @return �����}�Ԃ̔z��B
	 */
	private String[] multipleDrwgNoCount(SearchConditionForm searchConditionForm, DrasapInfo drasapInfo) {
		String multipleDrwgNo = searchConditionForm.getMultipleDrwgNo();
		return multipleDrwgNo.split(System.lineSeparator(), drasapInfo.getMultipleDrwgNoMax() + 1);
	}
// 2013.06.27 yamagishi add. end

// 2013.09.13 yamagishi modified. start
	/**
	 * SQL���̃A�N�Z�X���x��CASE�������Where�������܂Ƃ߂č쐬����B
	 * �����ł̖߂�l�̗�)
	 * where ACL_ID in ('01','10','13','11','15','14','12') and (((TRUNC(CREATE_DATE)>=TO_DATE('20030101','YYYYMMDD'))) )
	 * @param searchConditionForm
	 * @param user
	 * @param conn
	 * @param multipleDrwgNo
	 * @return
	 */
	private String createSqlWhere(SearchConditionForm searchConditionForm, User user, Connection conn, String[] multipleDrwgNo) throws Exception {
		StringBuffer sbSql1 = new StringBuffer();
		sbSql1.append("where");
		// �A�N�Z�X���x���ɂ�鐧����
		sbSql1.append(" ACL_ID in (");

		HashMap<String, String> aclMap = user.getAclMap(conn);
		Iterator<String> itr = aclMap.keySet().iterator();
		int aclCount = 0; // �ۗLACL��
		while (itr.hasNext()) {
			String aclId = itr.next();// �A�N�Z�X���x��
			int aclValue = Integer.parseInt(aclMap.get(aclId));
			if (aclValue >= 0) {
				// ���̃A�N�Z�X���x�����u�Q�ƕs�v�ȏ�Ȃ�A�����Ώۂ̃A�N�Z�X���x���Ƃ��Ēǉ�����
				sbSql1.append("'");
				sbSql1.append(aclId);
				sbSql1.append("',");
				aclCount++;
			}
		}
		if (aclCount > 0) {
			sbSql1.deleteCharAt(sbSql1.length() - 1);// �Ō�̃J���}������
		} else {
			sbSql1.append("''");// ���IN���ǉ�
		}
		sbSql1.append(")");

		ArrayList<String> tempSqlList = new ArrayList<String>();// ���������ɂ��SQL�������ꎞ�ۊǂ���

// 2020.03.11 yamamoto modified. start
		if (searchConditionForm.isOrderDrwgNo()) {
			// �}�Ԏw�菇�Ƀ`�F�b�N�������Ă���ꍇ�͒ʏ�̌��������͖�������
			sbSql1.append(" and (");
		} else {
			// �ŐV�ǔԂ݂̂Ȃ�
			if (searchConditionForm.onlyNewest) {
				sbSql1.append(" and LATEST_FLAG='1'");
			}
			// ���������ɂ�鐧����
			sbSql1.append(" and (");
			String condition1Sql = createSqlWhereByRow(searchConditionForm.condition1, searchConditionForm.condition1Value);
			if (condition1Sql != null) {
				tempSqlList.add(condition1Sql);
			}
			String condition2Sql = createSqlWhereByRow(searchConditionForm.condition2, searchConditionForm.condition2Value);
			if (condition2Sql != null) {
				tempSqlList.add(condition2Sql);
			}
			String condition3Sql = createSqlWhereByRow(searchConditionForm.condition3, searchConditionForm.condition3Value);
			if (condition3Sql != null) {
				tempSqlList.add(condition3Sql);
			}
			String condition4Sql = createSqlWhereByRow(searchConditionForm.condition4, searchConditionForm.condition4Value);
			if (condition4Sql != null) {
				tempSqlList.add(condition4Sql);
			}
			String condition5Sql = createSqlWhereByRow(searchConditionForm.condition5, searchConditionForm.condition5Value);
			if (condition5Sql != null) {
				tempSqlList.add(condition5Sql);
			}
			for (int i = 0; i < tempSqlList.size(); i++) {
				if (i > 0) {
					// ���������ɂ�邻�ꂼ��̐�����OR,AND���邩��
					// searchConditionForm.eachCondition�ɂ��
					sbSql1.append(' ');
					sbSql1.append(searchConditionForm.eachCondition);
					sbSql1.append(' ');
				}
				sbSql1.append(tempSqlList.get(i));
			}
		}
// 2020.03.11 yamamoto modified. end

		// �����}�Ԍ�����
		if (multipleDrwgNo != null) {
			sbSql1.append(createSqlWhereByMultipleDrwgNo(multipleDrwgNo, (tempSqlList.size() > 0)));
		}
		sbSql1.append(" )");
		//
		return sbSql1.toString();
	}
// 2013.09.13 yamagishi modified. end

// 2013.09.04 yamagishi add. start
	/**
	 * ��ʂœ��͂��������}�Ԃ�SQL���𕔕��I�ɍ\������B
	 * �����ł̖߂�l�̗�)�E�E�E�O���()���t��
	 * (DRWG_NO in ('3294500015','3294500016' or DRWG_NO LIKE '32941500%')
	 * @param multipleDrwgNo
	 * @param hasOtherCondition
	 * @return
	 */
	private String createSqlWhereByMultipleDrwgNo(String[] multipleDrwgNo, boolean otherConditionFlag) {
		// ���[�v������O�̏�������
		StringBuffer sbSql1 = new StringBuffer();
		StringBuffer sbSql2 = new StringBuffer();
		// �ŏ��̃J�b�R
		if (otherConditionFlag) {
			sbSql1.append(" and (DRWG_NO in (");
		} else {
			sbSql1.append(" (DRWG_NO in (");
		}

		boolean sqlInFlag = false;
		for (String drwgNo : multipleDrwgNo) {
			// trim�����B�n�C�t���u-�v�������B
			drwgNo = StringCheck.trimWsp(drwgNo).replace("-", "");
			// ���p�啶���ɕϊ�����
			drwgNo = StringCheck.changeDbToSbAscii(drwgNo).toUpperCase();

			if (drwgNo.length() <= 0) {
				// �󕶎��i���s�R�[�h�j���X�L�b�v
				continue;
			}

			if (drwgNo.contains("*")) {
				// ���C���h�J�[�h(*)�g�p���́ALIKE���ɂ��� or�����Ō���
				// �u*�v���u%�v�ɕϊ��B
				sbSql2.append(" or DRWG_NO LIKE '" + drwgNo.replace("*", "%") + "'");
			} else if (drwgNo.contains("_")) {
				// ���C���h�J�[�h(_)�g�p���́ALIKE���ɂ��� or�����Ō���
				sbSql2.append(" or DRWG_NO LIKE '" + drwgNo + "'");
			} else {
				// IN��ɒǉ�
				sbSql1.append("'" + drwgNo + "',");
				sqlInFlag = true;
			}
		}
		if (sqlInFlag) {
			// �Ō��1����(,)���폜
			sbSql1.deleteCharAt(sbSql1.length() - 1);
		} else {
			// IN��̗v�f���Ȃ��ꍇ�A�󕶎���ǉ��B
			sbSql1.append("''");
		}
		sbSql1.append(")");		// IN��
		sbSql1.append(sbSql2);	// ���C���h�J�[�h
		sbSql1.append(")");		// �����}�Ԍ�������
		return sbSql1.toString();
	}

// 2013.09.04 yamagishi add. end

	/**
	 * ��ʂœ��͂���1�s�̏�����SQL���𕔕��I�ɍ\������B
	 * �����ł̖߂�l�̗�)�E�E�E�O���()���t��
	 * (DRWG_NO LIKE '329450015%' OR DRWG_NO LIKE '3294%' AND DRWG_NO LIKE '32941500002')
	 * @param conditionName
	 * @param conditionValue
	 * @return
	 */
	private String createSqlWhereByRow(String conditionName, String conditionValue){
		// ���������̍��ږ��܂��͏����������͂Ȃ� null��Ԃ�
		if(conditionName == null || conditionName.length() == 0 ||
		conditionValue == null || conditionValue.length() == 0){
			return null;
		}
		// ���[�v������O�̏�������
		StringBuffer sbSql = new StringBuffer();
		sbSql.append('(');// �ŏ��̃J�b�R
		int lastIndex = -1;
		String lastOrAnd = "";// �ŏ��͂Ȃ��B�������" AND "�܂���" OR "������B
		// ���[�v���镔��
		while(true){
			// 1) ���̃J���}�܂��̓A���o�T���h�̈ʒu�̏������������߂� /////////////////////////////////////
			int nextOrIndex = conditionValue.indexOf(',', lastIndex + 1);// ���̃J���}�̈ʒu
			int nextAndIndex = conditionValue.indexOf('&', lastIndex + 1);// ���̃A���o�T���h�̈ʒu
			int nextOrAndIndex = -1;// ���̃J���}�܂��̓A���h�̈ʒu
			if(nextOrIndex == -1){
				nextOrAndIndex = nextAndIndex;// ���̃J���}���Ȃ����
			} else if(nextAndIndex == -1){
				nextOrAndIndex = nextOrIndex;// ���̃A���o�T���h���Ȃ����
			} else {
				nextOrAndIndex = Math.min(nextOrIndex, nextAndIndex);// ���̃J���}�܂��̓A���o�T���h�̈ʒu�̏�������
			}
			// 2) ���̒l�����o�� ////////////////////////////////////////////////////////////
			String nextValue = null;
			if(nextOrAndIndex == -1){
				// ���̃J���}���A���o�T���h���Ȃ�
				nextValue = conditionValue.substring(lastIndex + 1, conditionValue.length());

			} else{
				// ���̃J���}�܂��̓A���o�T���h�̒��O��
				nextValue = conditionValue.substring(lastIndex + 1, nextOrAndIndex);
			}
			// 3) SQL����g�ݗ��Ă� ///////////////////////////////////////////////////
			sbSql.append(lastOrAnd);
			// �}�ԁA�쐬���Ɋւ��ẮA���p�啶���ɕϊ�����
			if(conditionName.equals("CREATE_DATE") || conditionName.equals("DRWG_NO") || conditionName.equals("TWIN_DRWG_NO")){
				nextValue = StringCheck.changeDbToSbAscii(nextValue).toUpperCase();
			}
			//
			if(conditionName.equals("CREATE_DATE")){
				// ���t�^�̏ꍇ
				nextValue = nextValue.replace('�@', ' ');// �S�p�X�y�[�X�𔼊p�X�y�[�X��
				nextValue = nextValue.trim();// trim���Ĕ��p�X�y�[�X������
				int sepIndex = nextValue.indexOf('-');
				if(sepIndex == -1){
					// �u-�v���g�p�����͈͎w�肪�Ȃ��ꍇ
					int ymd = DateCheck.convertIntYMD(nextValue);// �w�����8�P�^����(YYYYMMDD)��
					// �����X�s�[�h����̂��߁ATRUNC���g�p���Ȃ��悤�ɕύX����B
					// �w�����0��0��0�b <= conditionName < �w�����1�����0��0��0�b
					// �Ƃ��Ď��o��
					// �ύX '04.Mar.19 by Hirata
					sbSql.append("TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("000000','YYYYMMDDHH24MISS')<=");
					sbSql.append(conditionName);// ���������̍��ږ�
					sbSql.append(" and ");
					sbSql.append(conditionName);// ���������̍��ږ�
					sbSql.append(" < TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("000000','YYYYMMDDHH24MISS')+1");

					/* �ȉ���TRUNC�g�p�o�[�W����
					 * �����X�s�[�h�����ƂȂ���

					sbSql.append("TRUNC(");
					sbSql.append(conditionName);// ���������̍��ږ�
					sbSql.append(")=TO_DATE('");
					sbSql.append(ymd);
					sbSql.append("','YYYYMMDD')");
					 */
				} else {// �u-�v���g�p���Ĕ͈͎w�肳��Ă���ꍇ
					sbSql.append("(");
					// �u-�v�𗘗p���ĊJ�n�A�I���̕�������擾
					String fromDate = null;// �J�n
					String toDate = null;// �I��
					if(sepIndex > 0){
						fromDate = nextValue.substring(0, sepIndex);
					}
					if(sepIndex < nextValue.length()-1){
						toDate = nextValue.substring(sepIndex+1);
					}
					// 1) �J�n��
					int fromYmd = 0;
					if(fromDate != null){
						fromYmd = DateCheck.convertIntYMD(fromDate);// �J�n����8�P�^����(YYYYMMDD)��
						// �����X�s�[�h����̂��߁ATRUNC���g�p���Ȃ��悤�ɕύX����B
						// �J�n����0��0��0�b <= conditionName
						// �Ƃ��Ď��o��
						// �ύX '04.Mar.19 by Hirata
						sbSql.append("TO_DATE('");
						sbSql.append(fromYmd);
						sbSql.append("000000','YYYYMMDDHH24MISS')<=");
						sbSql.append(conditionName);// ���������̍��ږ�

						/* �ȉ���TRUNC�g�p�o�[�W����
						 * �����X�s�[�h�����ƂȂ���

						sbSql.append("TRUNC(");
						sbSql.append(conditionName);// ���������̍��ږ�
						sbSql.append(")>=TO_DATE('");
						sbSql.append(fromYmd);
						sbSql.append("','YYYYMMDD')");
						 */
					}
					// 2) �I����
					int toYmd = 0;
					if(toDate != null){
						toYmd = DateCheck.convertIntYMD(toDate);// �I������8�P�^����(YYYYMMDD)��
						if(fromYmd != 0){
							// �J�n�����w�肳��Ă����ꍇ
							sbSql.append(" AND ");
						}
						// �����X�s�[�h����̂��߁ATRUNC���g�p���Ȃ��悤�ɕύX����B
						// conditionName < �I������1�����0��0��0�b
						// �Ƃ��Ď��o��
						// �ύX '04.Mar.19 by Hirata
						sbSql.append(conditionName);// ���������̍��ږ�
						sbSql.append(" < TO_DATE('");
						sbSql.append(toYmd);
						sbSql.append("000000','YYYYMMDDHH24MISS')+1");

						/* �ȉ���TRUNC�g�p�o�[�W����
						 * �����X�s�[�h�����ƂȂ���

						sbSql.append("TRUNC(");
						sbSql.append(conditionName);// ���������̍��ږ�
						sbSql.append(")<=TO_DATE('");
						sbSql.append(toYmd);
						sbSql.append("','YYYYMMDD')");
						 */
					}
					sbSql.append(")");
				}

			} else {
				// �����^�̏ꍇ
				sbSql.append(conditionName);// ���������̍��ږ�
				sbSql.append(" LIKE '");
				// trim�����B�u*�v���u%�v�ɕϊ��B
				String tempNextValue = StringCheck.trimWsp(nextValue).replace('*','%');
				// �}�Ԃ̏ꍇ�A-����������
				if(conditionName.equals("DRWG_NO") || conditionName.equals("TWIN_DRWG_NO")){
					StringTokenizer st = new StringTokenizer(tempNextValue, "-");
					StringBuffer sb = new StringBuffer();
					while(st.hasMoreTokens()){
						sb.append(st.nextToken());
					}
					tempNextValue = sb.toString();
				}
				sbSql.append(tempNextValue);
				sbSql.append("'");

			}
			// 4) ���̃��[�v�̂��߂̏���
			if(nextOrAndIndex == -1){
				// ���̃J���}���A���o�T���h���Ȃ�
				break;
			} else if(conditionValue.charAt(nextOrAndIndex) == ','){
				lastOrAnd = " OR ";//
			} else {
				lastOrAnd = " AND ";//
			}
			lastIndex = nextOrAndIndex;
		}
		//
		sbSql.append(')');// ���J�b�R
		return sbSql.toString();
	}
	/**
	 * SQL����Order�������܂Ƃ߂č쐬����B
	 * �����ł̖߂�l�̗�)
	 * order by DRWG_NO, CREATE_DATE DESC
	 * @param searchConditionForm
	 * @return
	 */
	private String createSqlOrder(SearchConditionForm searchConditionForm){
		SearchOrderSentenceMaker orderSentenceMaker = new SearchOrderSentenceMaker();
		// ����1����5�܂ł�
		orderSentenceMaker.addOrderCondition(searchConditionForm.condition1,
					searchConditionForm.sortWay1, searchConditionForm.sortOrder1);
		orderSentenceMaker.addOrderCondition(searchConditionForm.condition2,
					searchConditionForm.sortWay2, searchConditionForm.sortOrder2);
		orderSentenceMaker.addOrderCondition(searchConditionForm.condition3,
					searchConditionForm.sortWay3, searchConditionForm.sortOrder3);
		orderSentenceMaker.addOrderCondition(searchConditionForm.condition4,
					searchConditionForm.sortWay4, searchConditionForm.sortOrder4);
		orderSentenceMaker.addOrderCondition(searchConditionForm.condition5,
					searchConditionForm.sortWay5, searchConditionForm.sortOrder5);

		return orderSentenceMaker.getSqlOrder();
	}
	private void getScreenItemStrList(SearchConditionForm searchConditionForm, User user) {
		CsvItemStrList screenItemStrList;
		try {
			int langIdx = 0;

			if (!user.getLanguage().equals("Japanese")) langIdx = 1;

// 2013.06.14 yamagishi modified. start
//			String beaHome = System.getenv("BEA_HOME");
//			if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//			if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
//			screenItemStrList = new CsvItemStrList(beaHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.screenItemStrList.path"));
			String apServerHome = System.getenv(BEA_HOME);
			if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
			if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
			if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			screenItemStrList = new CsvItemStrList(apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.csvdef.screenItemStrList.path"));
// 2013.06.14 yamagishi modified. end
// 2013.06.27 yamagishi modified. start
//			searchConditionForm.setC_label1(screenItemStrList.getLineData(1)== null?"":screenItemStrList.getLineData(1).get(langIdx));
//			searchConditionForm.setC_label2(screenItemStrList.getLineData(2)== null?"":screenItemStrList.getLineData(2).get(langIdx));
//			searchConditionForm.setC_label3(screenItemStrList.getLineData(3)== null?"":screenItemStrList.getLineData(3).get(langIdx));
//			searchConditionForm.setC_label4(screenItemStrList.getLineData(4)== null?"":screenItemStrList.getLineData(4).get(langIdx));
//			searchConditionForm.setC_label5(screenItemStrList.getLineData(5)== null?"":screenItemStrList.getLineData(5).get(langIdx));
//			searchConditionForm.setC_label6(screenItemStrList.getLineData(6)== null?"":screenItemStrList.getLineData(6).get(langIdx));
//			searchConditionForm.setC_label7(screenItemStrList.getLineData(7)== null?"":screenItemStrList.getLineData(7).get(langIdx));
//			searchConditionForm.setC_label8(screenItemStrList.getLineData(8)== null?"":screenItemStrList.getLineData(8).get(langIdx));
			ArrayList<String> lineData = null;
			searchConditionForm.setC_label1((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL1_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label2((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL2_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label3((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL3_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label4((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL4_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label5((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL5_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label6((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL6_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label7((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL7_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label8((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL8_LINE_NO)) == null ? "" : lineData.get(langIdx));
			searchConditionForm.setC_label9((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL9_LINE_NO)) == null ? "" : lineData.get(langIdx));	// �����}��
// 2013.06.27 yamagishi modified. end
// 2019.09.25 yamamoto add. start
			searchConditionForm.setC_label10((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL10_LINE_NO)) == null ? "" : lineData.get(langIdx));	// �p�X���[�h�ύX
			searchConditionForm.setC_label11((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL11_LINE_NO)) == null ? "" : lineData.get(langIdx));	// ���O�A�E�g
			searchConditionForm.setC_label12((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL12_LINE_NO)) == null ? "" : lineData.get(langIdx));	// ���}�ɍ�ƈ˗�
			searchConditionForm.setC_label13((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL13_LINE_NO)) == null ? "" : lineData.get(langIdx));	// ���}�ɍ�ƈ˗��ڍ�
			searchConditionForm.setC_label14((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL14_LINE_NO)) == null ? "" : lineData.get(langIdx));	// ���}�ɍ�ƃ��X�g
			searchConditionForm.setC_label15((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL15_LINE_NO)) == null ? "" : lineData.get(langIdx));	// �A�N�Z�X���x���ꊇ�X�V
			searchConditionForm.setC_label16((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL16_LINE_NO)) == null ? "" : lineData.get(langIdx));	// �A�N�Z�X���x���X�V����
// 2019.09.25 yamamoto add. end
// 2020.03.10 yamamoto add. start
			searchConditionForm.setC_label17((lineData = screenItemStrList.getLineData(searchConditionForm.C_LABEL17_LINE_NO)) == null ? "" : lineData.get(langIdx));	// �}�Ԏw�菇
// 2020.03.10 yamamoto add. end
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	/**
	 * ���m�点���b�Z�[�W���擾����B��O�����������ꍇ�Aerrors��add����B
	 * @param infoFileName ���m�点���b�Z�[�W�t�@�C����
	 * @param information ���b�Z�[�W���e
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private String getSearchHelpMsg(String language){
	    String key;
	    StringBuffer infoSb = null;
	    if (language.equals("Japanese")) {
	    	key = "tyk.csvdef.searchHelpMsg_J.path";
	    } else {
	    	key = "tyk.csvdef.searchHelpMsg_E.path";
	    }
// 2013.06.14 yamagishi modified. start
//		String beaHome = System.getenv("BEA_HOME");
//		if (beaHome == null) beaHome = System.getenv("OCE_BEA_HOME");
//		if (beaHome == null) beaHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
		String apServerHome = System.getenv(BEA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(CATALINA_HOME);
		if (apServerHome == null) apServerHome = System.getenv(OCE_AP_SERVER_HOME);
		if (apServerHome == null) apServerHome = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
// 2013.06.14 yamagishi modified. end
	    String infoFileName = apServerHome + DrasapPropertiesFactory.getDrasapProperties(this).getProperty(key);
		try {
			File infoFile = new File(infoFileName);
			if(!infoFile.exists()){
				infoFile.createNewFile();
			}
			infoSb = new StringBuffer();
			BufferedReader inpBr = new BufferedReader(new FileReader(infoFileName), 128);
			while (inpBr.ready()) {
				infoSb.append(inpBr.readLine() + "\r");
			}
			inpBr.close();
		} catch (IOException e) {
		}
        return infoSb.toString();

    }
// 2019.09.25 yamamoto add. start
	/**
	 * �G���[���b�Z�[�W���擾����
	 *
	 * @param user
	 * @param key
	 * @return
	 */
	private String getErrorMsg(User user, String key){

		// properties�擾
		String msg = "";
		ProfileString prop = null;
		try {
			prop = new ProfileString(this, "resources/application.properties");
			msg = prop.getValue(key + "." + user.getLanKey());

		} catch (Exception e) {
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
			// for MUR
			category.error("application.properties�̓ǂݍ��ݎ��s" + ErrorUtility.error2String(e));
		}

        return msg;
    }
// 2019.09.25 yamamoto add. end

	private boolean setMultiViewDrwgNos(SearchConditionForm form, HttpServletRequest request){
		// �ŏ��ɁA1�ł����������͂���Ă��邩�m�F

		String[] drwgNoArray = (String[])request.getSession().getAttribute("drwgNoArray");
		if (drwgNoArray == null || drwgNoArray.length == 0) return false;

		String conditionValue = "";
		for (int i = 0; i < drwgNoArray.length; i++) {
			if (i > 0) conditionValue += ",";
			conditionValue += drwgNoArray[i];
		}
		if (form.condition1.equals("DRWG_NO")) {
			form.condition1Value = conditionValue;
		} else if (form.condition2.equals("DRWG_NO")) {
			form.condition2Value = conditionValue;
		} else if (form.condition3.equals("DRWG_NO")) {
			form.condition3Value = conditionValue;
		} else if (form.condition4.equals("DRWG_NO")) {
			form.condition4Value = conditionValue;
		} else if (form.condition5.equals("DRWG_NO")) {
			form.condition5Value = conditionValue;
		} else {
			form.condition1 = "DRWG_NO";
			form.condition1Value = conditionValue;
		}
		return true;
	}
}