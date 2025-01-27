package tyk.drasap.system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * ���p�҃O���[�v�A�N�Z�X���x���֘A�����e�i���X��ʂ�Action�B
 */
@Controller
public class UserGrpAclRelationMaintenanceAction extends BaseAction {
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
	@PostMapping("/userGrpAclRelationMaintenance")
	public String execute(
			UserGrpAclRelationMaintenanceForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		//		category.debug("start");
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm = form;
		if ("init".equals(session.getAttribute("act"))) {
			userGrpAclRelationMaintenanceForm.setAct("init");
			session.removeAttribute("act");
		}
		userGrpAclRelationMaintenanceForm.clearErrorMsg();

		//
		if ("init".equals(userGrpAclRelationMaintenanceForm.getAct())) {
			addStatusList(userGrpAclRelationMaintenanceForm);
			addGroupCodeList(userGrpAclRelationMaintenanceForm, user, errors);
			addAclList(userGrpAclRelationMaintenanceForm, user, errors);
			userGrpAclRelationMaintenanceForm.tableInfo = getUserGrpAclRelationMasterAttrList(user, errors);
			ArrayList<UserGrpAclRelationMaintenanceElement> userGrpAclRelationMasterList = getUserGrpAclRelationMaster(userGrpAclRelationMaintenanceForm, "aclId", user, errors);
			userGrpAclRelationMaintenanceForm.setRecList(userGrpAclRelationMasterList);
			session.removeAttribute("userGrpAclRelationMaintenanceForm");
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			return "success";
		}
		if ("update".equals(userGrpAclRelationMaintenanceForm.getAct())) {
			updateUserGrpAclRelationMaintenanceForm(userGrpAclRelationMaintenanceForm, user, errors);
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			if (Objects.isNull(errors.getAttribute("message"))) {
				return "update";
			}
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "error";
		}
		if ("search".equals(userGrpAclRelationMaintenanceForm.getAct())) {
			ArrayList<UserGrpAclRelationMaintenanceElement> userGrpAclRelationMasterList = getUserGrpAclRelationMaster(userGrpAclRelationMaintenanceForm, userGrpAclRelationMaintenanceForm.getOrderBy(), user, errors);
			userGrpAclRelationMaintenanceForm.clearRecList();
			userGrpAclRelationMaintenanceForm.setRecList(userGrpAclRelationMasterList);
			session.removeAttribute("userGrpAclRelationMaintenanceForm");
			session.setAttribute("userGrpAclRelationMaintenanceForm", userGrpAclRelationMaintenanceForm);
			return "search";
		}

		return "success";
	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateUserGrpAclRelationMaintenanceForm(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		//PreparedStatement pstmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			// �`�F�b�N���Ă�����̂���koushinn
			for (int i = 0; i < userGrpAclRelationMaintenanceForm.getRecList().size(); i++) {
				UserGrpAclRelationMaintenanceElement userGrpAclRelationMaintenanceElement = userGrpAclRelationMaintenanceForm.getRecList(i);
				if (userGrpAclRelationMaintenanceElement.isUpdate()) {
					updateRecord(userGrpAclRelationMaintenanceElement, user, conn, errors);
				}
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�e�[�u���̍X�V�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�e�[�u���̍X�V�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.commit();
			} catch (Exception e2) {
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
			}
			//try{ pstmt1.close(); } catch(Exception e) {}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateRecord(UserGrpAclRelationMaintenanceElement userGrpAclRelationMasterMaintenanceElement, User user, Connection conn, Model errors) throws IOException {
		PreparedStatement pstmt1 = null;
		try {
			StringBuilder sbSql1 = new StringBuilder("update USER_GRP_ACL_RELATION");
			sbSql1.append(" set ");
			sbSql1.append("ACL_VALUE=?");
			sbSql1.append(" where ACL_ID = '");
			sbSql1.append(userGrpAclRelationMasterMaintenanceElement.getAclId());
			sbSql1.append("' ");
			sbSql1.append(" and USER_GRP_CODE = '");
			sbSql1.append(userGrpAclRelationMasterMaintenanceElement.getUserGrpCode());
			sbSql1.append("' ");

			pstmt1 = conn.prepareStatement(sbSql1.toString());

			pstmt1.setString(1, userGrpAclRelationMasterMaintenanceElement.getAclValue());
			//			category.debug(sbSql1.toString());
			pstmt1.executeUpdate();
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̍X�V�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̍X�V�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			userGrpAclRelationMasterMaintenanceElement.setUpdate(false);
		}
	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<UserGrpAclRelationMaintenanceElement> getUserGrpAclRelationMaster(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, String orderBy, User user, Model errors) throws IOException {
		ArrayList<UserGrpAclRelationMaintenanceElement> recList = new ArrayList<UserGrpAclRelationMaintenanceElement>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select USER_GRP_CODE, ACL_ID, ACL_VALUE");
			sbSql1.append(" from USER_GRP_ACL_RELATION");
			if ("aclId".equals(orderBy)) {
				sbSql1.append(" order by ACL_ID, USER_GRP_CODE");
			} else {
				sbSql1.append(" order by USER_GRP_CODE, ACL_ID");
			}
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				UserGrpAclRelationMaintenanceElement userGrpAclRelationMasterMaintenanceElement = new UserGrpAclRelationMaintenanceElement();

				userGrpAclRelationMasterMaintenanceElement.aclId = rs1.getString("ACL_ID");
				userGrpAclRelationMasterMaintenanceElement.aclName = userGrpAclRelationMaintenanceForm.getAclName(rs1.getString("ACL_ID"));
				userGrpAclRelationMasterMaintenanceElement.aclValue = rs1.getString("ACL_VALUE");
				userGrpAclRelationMasterMaintenanceElement.userGrpCode = rs1.getString("USER_GRP_CODE");
				userGrpAclRelationMasterMaintenanceElement.userGrpName = userGrpAclRelationMaintenanceForm.getGroupName(rs1.getString("USER_GRP_CODE"));
				recList.add(userGrpAclRelationMasterMaintenanceElement);
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̎擾�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return recList;

	}

	/**
	 * �O���[�v���X�g���擾����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void addGroupCodeList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			userGrpAclRelationMaintenanceForm.clearGroupCodeList();
			userGrpAclRelationMaintenanceForm.clearGroupNameList();
			StringBuilder sbSql1 = new StringBuilder("select USER_GRP_CODE, USER_GRP_NAME");
			sbSql1.append(" from USER_GROUP_MASTER order by USER_GRP_CODE");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				userGrpAclRelationMaintenanceForm.addGroupCodeList(rs1.getString("USER_GRP_CODE"));
				userGrpAclRelationMaintenanceForm.addGroupNameList(rs1.getString("USER_GRP_NAME"));
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̎擾�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * �A�N�Z�X�����F�����X�g���擾����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void addAclList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			userGrpAclRelationMaintenanceForm.clearAclIdList();
			userGrpAclRelationMaintenanceForm.clearAclNameList();
			StringBuilder sbSql1 = new StringBuilder("select ACL_ID, ACL_NAME");
			sbSql1.append(" from ACCESS_LEVEL_MASTER order by ACL_ID");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				userGrpAclRelationMaintenanceForm.addAclIdList(rs1.getString("ACL_ID"));
				userGrpAclRelationMaintenanceForm.addAclNameList(rs1.getString("ACL_NAME"));
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̎擾�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private TableInfo getUserGrpAclRelationMasterAttrList(User user, Model errors) throws IOException {
		TableInfo tableInfo = null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			tableInfo = TableInfoDB.getTableInfoArray("USER_GRP_ACL_RELATION", conn);
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("�e�[�u�����̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return tableInfo;

	}

	/**
	 * �A�N�Z�X���x���l�̃��X�g���쐬����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void addStatusList(UserGrpAclRelationMaintenanceForm userGrpAclRelationMaintenanceForm) {
		userGrpAclRelationMaintenanceForm.clearAclValueList();
		userGrpAclRelationMaintenanceForm.clearAclValueNameList();
		userGrpAclRelationMaintenanceForm.addAclValueList("1");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("�����E�Q��");
		userGrpAclRelationMaintenanceForm.addAclValueList("2");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("���");
		userGrpAclRelationMaintenanceForm.addAclValueList("3");
		userGrpAclRelationMaintenanceForm.addAclValueNameList("�ύX");

	}
}
