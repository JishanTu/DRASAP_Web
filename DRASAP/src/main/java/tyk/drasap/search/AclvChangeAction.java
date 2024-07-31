package tyk.drasap.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import tyk.drasap.change_acllog.ChangeAclLogger;
import tyk.drasap.common.AclUpdateNoSequenceDB;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.MessageManager;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;

/**
 * �A�N�Z�X���x���A�g�p�֎~��ύX����Action�B
 * '04.Nov.23 �}�Ԃ����M���O����悤�ɕύX
 * @author fumi
 * �쐬��: 2004/01/20
 * @version 2013/09/14 yamagishi
 */
@Controller
@SessionAttributes("aclvChangeForm")
public class AclvChangeAction extends BaseAction {
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
	@PostMapping("/aclvChange")
	public Object execute(
			@ModelAttribute("aclvChangeForm") AclvChangeForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		category.debug("start");
		AclvChangeForm aclvChangeForm = form;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		// session�^�C���A�E�g�̊m�F
		if (user == null) {
			return "timeout";
		}
		// AclvChangeForm�̏�������
		aclvChangeForm.errorMessages = new ArrayList<>();// �G���[���b�Z�[�W�̃N���A
		// act�����ɂ�鏈���̐؂蕪��
		if ("CHECK_ON".equals(aclvChangeForm.getAct())) {
			// �S�ĂɃ`�F�b�N
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeElement(i).setSelected(true);
			}
			category.debug("--> input");
			return "input";
		}

		if ("CHECK_OFF".equals(aclvChangeForm.getAct())) {
			// �S�Ẵ`�F�b�N�O��
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeElement(i).setSelected(false);
			}
			category.debug("--> input");
			return "input";
		}

		if ("NEXT".equals(aclvChangeForm.getAct())) {
			// 2013.07.24 yamagishi modified. start
			//MessageResources resources = getResources(request, "application"); // applicatin.properties�擾
			// ����ʂɐi�ޑO�̃`�F�b�N
			//			checkForNext(aclvChangeForm);
			aclvChangeForm.getErrorMessages().clear();
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				aclvChangeForm.getAclvChangeList().get(i).setSelected(Boolean.parseBoolean(request.getParameter("aclvChangeElement[" + i + "].selected")));
				aclvChangeForm.getAclvChangeList().get(i).setNewAclId(request.getParameter("aclvChangeElement[" + i + "].newAclId"));
				aclvChangeForm.getAclvChangeList().get(i).setNewProhibit(request.getParameter("aclvChangeElement[" + i + "].newProhibit"));
			}
			checkForNext(aclvChangeForm, user);
			// 2013.07.24 yamagishi modified. end
			if (aclvChangeForm.getErrorMessages().size() > 0) {
				// ���͉�ʂɖ߂�
				category.debug("--> input");
				return "input";
			}
			// �m�F��ʂɐi��
			category.debug("--> confirm");
			return "confirm";
		}

		if ("SEARCH".equals(aclvChangeForm.getAct())) {
			// ������ʂɖ߂�
			request.setAttribute("task", "continue");
			category.debug("--> search");
			return "search";
		}

		if ("BACK_INPUT".equals(aclvChangeForm.getAct())) {
			// ���͉�ʂɖ߂�
			category.debug("--> input");
			return "input";
		}

		if ("CONFIRMED".equals(aclvChangeForm.getAct())) {
			// 2013.07.24 yamagishi modified. start
			//MessageResources resources = getResources(request, "application");
			// �m�FOK�B�X�V���s���B
			//			updateAclv(aclvChangeForm, user);
			updateAclv(aclvChangeForm, user);
			// 2013.07.24 yamagishi modified. end
			if (aclvChangeForm.getErrorMessages().size() > 0) {
				// �X�V�Ɏ��s
				category.debug("--> input");
				return "input";
			}

			// �X�V�ɐ��������ꍇ�A
			// ������ʂɖ߂邪�A�������ʂ̂݃N���A����B�E�E�E�A�N�Z�X���x���ȂǕύX��������

			// �A�N�Z�X���O��
			// '04.Nov.23�ύX �}�Ԃ����M���O���邽�߁A�����ł̃��M���O�͍s��Ȃ�
			//AccessLoger.loging(user, AccessLoger.FID_CHG_ACL);
			//category.debug("�X�V���������� " + cnt);
			request.setAttribute("task", "clear_result");
			category.debug("--> search2");
			return "search2";
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * ����ʂɐi�ޑO�̃`�F�b�N�B
	 * - �I������Ă��邩
	 * - �ύX���ꂽ�f�[�^�����邩
	 * @param aclvChangeForm
	 * @param resources
	 */
	//	private void checkForNext(AclvChangeForm aclvChangeForm){	// 2013.07.24 yamagishi modified. start
	private void checkForNext(AclvChangeForm aclvChangeForm, User user) {
		HashMap<String, String> aclMap = aclvChangeForm.getAclMap();
		HashSet<String> drwgNoSet = new HashSet<String>(); // modifield end.
		// 1) �I������Ă��邩?
		//		�܂��ύX���ꂽ�f�[�^�����邩?
		boolean selected = false;// �I������Ă���� true
		boolean modified = false;// �ύX���ꂽ�f�[�^�����邩? ����� true
		for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
			AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);
			if (aclvChangeElement.isSelected()) {// �I������Ă���
				selected = true;
				if (aclvChangeElement.isModified()) {// �ύX����Ă���
					modified = true;
				}
			}
			drwgNoSet.add(aclvChangeElement.getDrwgNo()); // 1��2�i�ԗp�ɐ}�Ԃ�ۑ�		// 2013.07.24 yamagishi add.
		}
		if (!selected) {
			aclvChangeForm.getErrorMessages().add(MessageManager.getInstance().getMessage("msg.aclv.required.drwg"));
		} else if (!modified) {
			aclvChangeForm.getErrorMessages().add(MessageManager.getInstance().getMessage("msg.aclv.notmodified.aclv"));
			// 2013.09.14 yamagishi add. start
		} else {
			Connection conn = null;
			try {
				conn = ds.getConnection();

				// 1��2�i�ԗp�ǉ����X�g
				ArrayList<AclvChangeElement> addAclvChangeList = new ArrayList<AclvChangeElement>();
				HashMap<String, String> drwgNoMap = null;
				String twinDrwgNo = null;

				boolean twinDrwgNoValid = true;
				boolean twinAclValid = true;
				for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
					AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);

					if (!aclMap.containsKey(aclvChangeElement.getOldAclId()) || !aclMap.containsKey(aclvChangeElement.getNewAclId())) {
						// �A�N�Z�X���x������ۑ�
						aclMap.putAll(AclvMasterDB.getAclMap(conn, aclvChangeElement.getOldAclId(), aclvChangeElement.getNewAclId()));
					}
					drwgNoMap = AclvMasterDB.getDrwgNoMap(aclvChangeElement.getDrwgNo(), conn);
					if (drwgNoMap.isEmpty()) {
						// 1��2�i�ԈȊO�̏ꍇ�A�X�L�b�v
						continue;
					}

					// 1��2�i�Ԑ������`�F�b�N
					if (drwgNoMap.get("ID1_TWIN_DRWG_NO") == null || drwgNoMap.get("ID2_TWIN_DRWG_NO") == null
							|| !drwgNoMap.get("ID1_TWIN_DRWG_NO").equals(drwgNoMap.get("ID2_TWIN_DRWG_NO"))) {
						twinDrwgNoValid = false;

						// 1��2�i��ACL�`�F�b�N
					} else if (drwgNoMap.get("ACL_ID") == null || drwgNoMap.get("TWIN_ACL_ID") == null
							|| !drwgNoMap.get("ACL_ID").equals(drwgNoMap.get("TWIN_ACL_ID"))) {
						twinAclValid = false;
					}

					twinDrwgNo = drwgNoMap.get("ID1_TWIN_DRWG_NO") != null ? drwgNoMap.get("ID1_TWIN_DRWG_NO") : drwgNoMap.get("ID2_TWIN_DRWG_NO");
					if (!drwgNoSet.contains(twinDrwgNo)) {
						// 1��2�i�Ԃ��������X�g�ɂȂ���Βǉ�
						SearchResultElement searchResultElement = new SearchResultElement(twinDrwgNo, null, null, null, null);
						searchResultElement.addAttr("ACL_ID", drwgNoMap.get("TWIN_ACL_ID"));
						searchResultElement.addAttr("PROHIBIT", drwgNoMap.get("TWIN_PROHIBIT"));
						AclvChangeElement addAclvChangeElement = new AclvChangeElement(searchResultElement);
						addAclvChangeElement.setNewAclId(aclvChangeElement.getNewAclId());
						addAclvChangeElement.setNewProhibit(aclvChangeElement.getNewProhibit());
						addAclvChangeElement.setSelected(true);
						addAclvChangeList.add(addAclvChangeElement);

						if (!aclMap.containsKey(addAclvChangeElement.getOldAclId()) || !aclMap.containsKey(addAclvChangeElement.getNewAclId())) {
							// �ǉ����̃A�N�Z�X���x������ۑ�
							aclMap.putAll(AclvMasterDB.getAclMap(conn, addAclvChangeElement.getOldAclId(), addAclvChangeElement.getNewAclId()));
						}
					}
				}
				// ���X�g�̍Ō�ɒǉ�
				aclvChangeForm.getAclvChangeList().addAll(addAclvChangeList);

				if (!twinDrwgNoValid) {
					aclvChangeForm.getErrorMessages().add(messageSource.getMessage("system.aclBatchUpdate.upload.nodata.twinDrwgNo", null, null));
				} else if (!twinAclValid) {
					aclvChangeForm.getErrorMessages().add(messageSource.getMessage("system.aclBatchUpdate.upload.notequal.twin.drwgNo.acl", null, null));
				}
			} catch (Exception e) {
				// for ���[�U�[
				aclvChangeForm.getErrorMessages().add(
						MessageManager.getInstance().getMessage("msg.irai.unexpected", e.getMessage()));
				// for �V�X�e���Ǘ���
				ErrorLoger.error(user, this,
						DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.unexpected"));
				// for MUR
				category.error("�\�z�O�̗�O������\n" + ErrorUtility.error2String(e));
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
			// 2013.09.14 yamagishi add. end
		}
	}

	/**
	 * INDEX_DB�Ɏg�p�֎~�ƃA�N�Z�X���x�����X�V����B
	 * ��O�����������ꍇ�AAclvChangeForm.getErrorMessages()�ɒǉ�����B
	 * @param aclvChangeForm
	 * @param user
	 * @param resources
	 * @return �X�V���������B
	 */
	//	private int updateAclv(AclvChangeForm aclvChangeForm){	// 2013.07.24 yamagishi modified.
	private int updateAclv(AclvChangeForm aclvChangeForm, User user) {
		int cnt = 0;
		Connection conn = null;
		PreparedStatement pstmt1 = null;// �g�p�֎~�ƃA�N�Z�X���x����ύX
		PreparedStatement pstmt2 = null;// �g�p�֎~�݂̂�ύX
		PreparedStatement pstmt3 = null;// �A�N�Z�X���x���݂̂�ύX
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// �g�����U�N�V����
			// pstmt1�̏����E�E�E�g�p�֎~�ƃA�N�Z�X���x����ύX
			pstmt1 = conn.prepareStatement("update INDEX_DB set " +
					" PROHIBIT=?, PROHIBIT_DATE=sysdate, PROHIBIT_EMPNO=?, PROHIBIT_NAME=?," +
					" ACL_ID=?, ACL_UPDATE=sysdate, ACL_EMPNO=?, ACL_NAME=?" +
					" where DRWG_NO=?");
			pstmt1.setString(2, user.getId());// �E��
			pstmt1.setString(3, user.getName());// ���O
			pstmt1.setString(5, user.getId());// �E��
			pstmt1.setString(6, user.getName());// ���O
			// pstmt2�̏����E�E�E�g�p�֎~�݂̂�ύX
			pstmt2 = conn.prepareStatement("update INDEX_DB set " +
					" PROHIBIT=?, PROHIBIT_DATE=sysdate, PROHIBIT_EMPNO=?, PROHIBIT_NAME=?" +
					" where DRWG_NO=?");
			pstmt2.setString(2, user.getId());// �E��
			pstmt2.setString(3, user.getName());// ���O
			// pstmt3�̏����E�E�E�A�N�Z�X���x���݂̂�ύX
			pstmt3 = conn.prepareStatement("update INDEX_DB set " +
					" ACL_ID=?, ACL_UPDATE=sysdate, ACL_EMPNO=?, ACL_NAME=?" +
					" where DRWG_NO=?");
			pstmt3.setString(2, user.getId());// �E��
			pstmt3.setString(3, user.getName());// ���O
			// �}�Ԃ����M���O���邽�߂Ɉꎞ�i�[���邽�߂�List
			//			List updatedDrwgNoList = new ArrayList();
			List<AclvChangeElement> updatedDrwgNoList = new ArrayList<AclvChangeElement>(); // 2013.07.25 yamagishi modified.
			// INDEX_DB�ɍX�V������
			for (int i = 0; i < aclvChangeForm.getAclvChangeList().size(); i++) {
				AclvChangeElement aclvChangeElement = aclvChangeForm.getAclvChangeElement(i);
				if (aclvChangeElement.isSelected() && aclvChangeElement.isModified()) {
					// �I������āA���ύX����Ă���Ƃ��̂�
					if (!aclvChangeElement.oldProhibit.equals(aclvChangeElement.newProhibit)) {
						// ���Ȃ��Ƃ��g�p�֎~���ύX����Ă���
						if (!aclvChangeElement.oldAclId.equals(aclvChangeElement.newAclId)) {
							// �A�N�Z�X���x�����ύX����Ă���
							// �X�V���`�F�b�N���{
							checkExclusive(aclvChangeElement.getDrwgNo(), aclvChangeElement.getOldAclId(), conn); // 2013.07.24 yamagishi add.
							pstmt1.setString(1, aclvChangeElement.getNewProhibit());// �g�p�֎~
							pstmt1.setString(4, aclvChangeElement.getNewAclId());// �A�N�Z�X���x��
							pstmt1.setString(7, aclvChangeElement.getDrwgNo());// �}��
							cnt += pstmt1.executeUpdate();
						} else {
							// �g�p�֎~�̂ݕύX����Ă���
							pstmt2.setString(1, aclvChangeElement.getNewProhibit());// �g�p�֎~
							pstmt2.setString(4, aclvChangeElement.getDrwgNo());// �}��
							cnt += pstmt2.executeUpdate();
						}
					} else {
						// �A�N�Z�X���x���̂ݕύX����Ă���
						// �X�V���`�F�b�N���{
						checkExclusive(aclvChangeElement.getDrwgNo(), aclvChangeElement.getOldAclId(), conn); // 2013.07.24 yamagishi add.
						pstmt3.setString(1, aclvChangeElement.getNewAclId());// �A�N�Z�X���x��
						pstmt3.setString(4, aclvChangeElement.getDrwgNo());// �}��
						cnt += pstmt3.executeUpdate();
					}
					// �X�V�����}�Ԃ��ꎞ�i�[����
					//					updatedDrwgNoList.add(aclvChangeElement.getDrwgNo()); // 2013.07.25 yamagishi modified.
					updatedDrwgNoList.add(aclvChangeElement);
				}
			}
			// commit
			conn.commit();

			// 2013.07.25 yamagishi modified. start
			//			// '04.Nov.23 �}�Ԃ����M���O����悤�ɕύX
			//			// �A�N�Z�X���O���܂Ƃ߂čs��
			//			AccessLoger.loging(user, AccessLoger.FID_CHG_ACL,
			//					(String[])updatedDrwgNoList.toArray(new String[0]));
			// �Ǘ�NO�̔�
			String aclUpdateNo = AclUpdateNoSequenceDB.getAclUpdateNo(
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("tyk.upload.aclupdateno.type.changeAcl"), conn);
			HashMap<String, String> aclMap = aclvChangeForm.getAclMap();
			String drwgNo = null;
			String preUpdateAcl = null;
			String postUpdateAcl = null;
			for (AclvChangeElement aclvChangeElement : updatedDrwgNoList) {
				drwgNo = aclvChangeElement.getDrwgNo();
				preUpdateAcl = aclvChangeElement.getOldAclId();
				postUpdateAcl = aclvChangeElement.getNewAclId();
				ChangeAclLogger.logging(user, aclUpdateNo, drwgNo, preUpdateAcl, aclMap.get(preUpdateAcl), postUpdateAcl, aclMap.get(postUpdateAcl), null);
			}
			// 2013.07.25 yamagishi modified. end

		} catch (Exception e) {
			// rollback
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			// for ���[�U�[
			aclvChangeForm.getErrorMessages().add(
					MessageManager.getInstance().getMessage("msg.aclv.failed.update.aclv", e.getMessage()));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			category.error("�A�N�Z�X���x���̍X�V�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			try {
				pstmt2.close();
			} catch (Exception e) {
			}
			try {
				pstmt3.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		//
		return cnt;
	}

	// 2013.07.24 yamagishi add. start
	/**
	 * �������e�[�u���̐}�ԁEACL���X�V����Ă��Ȃ����`�F�b�N����
	 * @param drwgNo
	 * @param aclId
	 * @param resources
	 * @param conn
	 */
	private void checkExclusive(String drwgNo, String aclId, Connection conn)
			throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// �X�V���`�F�b�N�pSQL
			String sql = "select count(DRWG_NO) as COUNT from INDEX_DB where DRWG_NO = ? and ACL_ID = ?";
			pstmt = conn.prepareStatement(sql);

			// �X�V���`�F�b�N
			pstmt.setString(1, drwgNo);
			pstmt.setString(2, aclId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// �}��ACL�X�V����
				if (rs.getLong("COUNT") <= 0) {
					// �ʃg�����U�N�V�����ɂ���Đ}��ACL���X�V�ςׁ݂̈A�G���[
					throw new Exception("(" + messageSource.getMessage("system.aclBatchUpdate.update.excusive.acl", null, null) + ")");
				}
			}
		} finally {
			// CLOSE����
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
	}
	// 2013.07.24 yamagishi add. end
}
