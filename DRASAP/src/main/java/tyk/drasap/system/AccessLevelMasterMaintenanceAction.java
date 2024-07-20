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
 * �A�N�Z�X���x���}�X�^�[�����e�i���X�����A�N�V����
 * @author Y.eto
 * �쐬��: 2006/07/10
 */
@Controller
public class AccessLevelMasterMaintenanceAction extends BaseAction {
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
	@PostMapping("/accessLevelMasterMaintenance")
	public String execute(
			AccessLevelMasterMaintenanceForm form,
			HttpServletRequest request,
			HttpServletResponse response,
			Model errors)
			throws Exception {
		//category.debug("start");
		//ActionMessages errors = new ActionMessages();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "timeout";
		}
		AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm = form;
		if ("init".equals(request.getParameter("act"))) {
			accessLevelMasterMaintenanceForm.setAct("init");
		}
		accessLevelMasterMaintenanceForm.clearErrorMsg();

		//
		if ("init".equals(accessLevelMasterMaintenanceForm.getAct())) {
			accessLevelMasterMaintenanceForm.tableInfo = getAclLevelMasterAttrList(user, errors);
			ArrayList<AccessLevelMasterMaintenanceElement> groupMasterList = getAclMaster(user, errors);
			accessLevelMasterMaintenanceForm.setRecList(groupMasterList);
			session.removeAttribute("accessLevelMasterMaintenanceForm");
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return "success";
		}
		if ("update".equals(accessLevelMasterMaintenanceForm.getAct())) {
			if (inputCheck(accessLevelMasterMaintenanceForm)) {
				return "update";
			}
			updateAccessLevelMasterMaintenanceForm(accessLevelMasterMaintenanceForm, user, errors);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			if (Objects.isNull(errors.getAttribute("message"))) {
				return "update";
			} else {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
		}
		if ("delete".equals(accessLevelMasterMaintenanceForm.getAct())) {
			deleteAccessLevelMasterMaintenanceForm(accessLevelMasterMaintenanceForm, user, errors);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
		} else if ("addrecord".equals(accessLevelMasterMaintenanceForm.getAct())) {
			AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = addNewRecord();
			accessLevelMasterMaintenanceForm.addRecList(accessLevelMasterMaintenanceElement);
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
		} else if ("search".equals(accessLevelMasterMaintenanceForm.getAct())) {
			ArrayList<AccessLevelMasterMaintenanceElement> groupMasterList = getAclMaster(user, errors);
			accessLevelMasterMaintenanceForm.clearRecList();
			accessLevelMasterMaintenanceForm.setRecList(groupMasterList);
			session.removeAttribute("accessLevelMasterMaintenanceForm");
			session.setAttribute("accessLevelMasterMaintenanceForm", accessLevelMasterMaintenanceForm);
			return "search";
		}

		return "success";
	}

	/**
	 * �A�N�Z�X���x���}�X�^�[�̍X�V�B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateAccessLevelMasterMaintenanceForm(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		//PreparedStatement pstmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			// �`�F�b�N���Ă�����̂���koushinn
			for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
				AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
				if (accessLevelMasterMaintenanceElement.isNew()) {
					if (aclIdDuplicateChk(accessLevelMasterMaintenanceElement.getAclId(), user, errors) > 0) {
						accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;"); //
						accessLevelMasterMaintenanceForm.addErrorMsg("���A�N�Z�X���x���h�c�u" + accessLevelMasterMaintenanceElement.getAclId() + "�v�͂��łɎg�p���Ă��܂��B");
						continue;
					}
					insertRecord(accessLevelMasterMaintenanceElement, user, conn, errors);
				} else if (accessLevelMasterMaintenanceElement.isUpdate()) {
					updateRecord(accessLevelMasterMaintenanceElement, user, conn, errors);
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
	 * �A�N�Z�X���x���}�X�^�[�̍폜
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void deleteAccessLevelMasterMaintenanceForm(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
				AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
				if (accessLevelMasterMaintenanceElement.isUpdate()) {
					if (aclLevelUsedCheck(accessLevelMasterMaintenanceElement.getAclId(), user, errors)) {
						try {
							conn.rollback();
						} catch (Exception e2) {
						}
						accessLevelMasterMaintenanceForm.addErrorMsg("���폜���悤�Ƃ����A�N�Z�X���x���u" + accessLevelMasterMaintenanceElement.getAclName() + "�v�͐}�ʑ������Ŏg�p���Ă��邽�ߍ폜�ł��܂���B");
						return;
					}
					StringBuilder sbSql1 = new StringBuilder("delete ACCESS_LEVEL_MASTER");
					sbSql1.append(" where ");
					sbSql1.append("ACL_ID='");
					sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
					sbSql1.append("' ");

					stmt1 = conn.createStatement();
					stmt1.executeUpdate(sbSql1.toString());

					deleteAclRelation(accessLevelMasterMaintenanceElement.getAclId(), user, conn, errors);
				}
			}
			for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
				AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
				if (accessLevelMasterMaintenanceElement.isUpdate()) {
					accessLevelMasterMaintenanceForm.deleteRecList(accessLevelMasterMaintenanceElement.getAclId());
					i--;
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
			ErrorLoger.error(user, this, "�}�X�^�[�̍폜�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̍폜�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.commit();
			} catch (Exception e2) {
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
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
	 * �A�N�Z�X���x���}�X�^�[�X�V
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateRecord(AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement, User user, Connection conn, Model errors) throws IOException {
		PreparedStatement pstmt1 = null;
		try {
			StringBuilder sbSql1 = new StringBuilder("update ACCESS_LEVEL_MASTER");
			sbSql1.append(" set ");
			sbSql1.append("ACL_ID=?,");
			sbSql1.append("ACL_NAME=?");
			sbSql1.append(" where ACL_ID = '");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
			sbSql1.append("' ");

			pstmt1 = conn.prepareStatement(sbSql1.toString());

			pstmt1.setString(1, accessLevelMasterMaintenanceElement.getAclId());
			pstmt1.setString(2, accessLevelMasterMaintenanceElement.getAclName());
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
			accessLevelMasterMaintenanceElement.setUpdate(false);
		}
	}

	/**
	 * �A�N�Z�X���x���}�X�^�[�ǉ�
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void insertRecord(AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement, User user, Connection conn, Model errors) throws IOException {
		Statement stmt1 = null;
		try {
			StringBuilder sbSql1 = new StringBuilder("insert into ACCESS_LEVEL_MASTER");
			sbSql1.append(" (");
			sbSql1.append("ACL_ID,");
			sbSql1.append("ACL_NAME");
			sbSql1.append(")");
			sbSql1.append(" values(");
			sbSql1.append("'");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclId());
			sbSql1.append("',");
			sbSql1.append("'");
			sbSql1.append(accessLevelMasterMaintenanceElement.getAclName());
			sbSql1.append("'");
			sbSql1.append(")");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			stmt1.executeUpdate(sbSql1.toString());
			try {
				stmt1.close();
			} catch (Exception e) {
			}

			insertAclRelation(accessLevelMasterMaintenanceElement.getAclId(), user, conn, errors);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̑}���Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̑}���Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			accessLevelMasterMaintenanceElement.setUpdate(false);
			accessLevelMasterMaintenanceElement.setNew(false);
		}
	}

	/**
	 * ���A�N�Z�X���x���}�X�^�[���X�g�擾
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<AccessLevelMasterMaintenanceElement> getAclMaster(User user, Model errors) throws IOException {
		ArrayList<AccessLevelMasterMaintenanceElement> recList = new ArrayList<AccessLevelMasterMaintenanceElement>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select ACL_ID,ACL_NAME");
			sbSql1.append(" from ACCESS_LEVEL_MASTER order by ACL_ID");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			while (rs1.next()) {
				AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = new AccessLevelMasterMaintenanceElement();

				accessLevelMasterMaintenanceElement.aclId = rs1.getString("ACL_ID");
				accessLevelMasterMaintenanceElement.aclName = rs1.getString("ACL_NAME");
				recList.add(accessLevelMasterMaintenanceElement);
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
	 * �u�ǉ��v�{�^���������̏����B��ʏ�ň�s�ǉ�����
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private AccessLevelMasterMaintenanceElement addNewRecord() throws IOException {
		AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = new AccessLevelMasterMaintenanceElement();
		accessLevelMasterMaintenanceElement.setUpdate(true);
		accessLevelMasterMaintenanceElement.setNew(true);
		return accessLevelMasterMaintenanceElement;

	}

	/**
	 * ���p�҃O���[�v�A�N�Z�X�֘A��}������B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void insertAclRelation(String aclId, User user, Connection conn, Model errors) throws IOException {
		Statement stmt1 = null;
		try {
			ArrayList<String> groupCodeList = getGroupCodeList(user, errors);
			for (int i = 0; i < groupCodeList.size(); i++) {
				StringBuilder sbSql1 = new StringBuilder("insert into USER_GRP_ACL_RELATION");
				sbSql1.append(" (");
				sbSql1.append("ACL_ID,");
				sbSql1.append("USER_GRP_CODE,");
				sbSql1.append("IMPORT_PRIV,");
				sbSql1.append("REFER_PRIV,");
				sbSql1.append("PRINT_PRIV,");
				sbSql1.append("CHANGE_PRIV,");
				sbSql1.append("MESSAGE_PRIV,");
				sbSql1.append("LATEST_REV_ONLY");
				sbSql1.append(")");
				sbSql1.append(" values(");
				sbSql1.append("'");
				sbSql1.append(aclId);
				sbSql1.append("','");
				sbSql1.append(groupCodeList.get(i).toString());
				sbSql1.append("','0','0','0','0','0','0')");
				stmt1 = conn.createStatement();
				//				category.debug(sbSql1.toString());
				stmt1.executeUpdate(sbSql1.toString());
				try {
					stmt1.close();
				} catch (Exception e) {
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
			ErrorLoger.error(user, this, "�}�X�^�[�̑}���Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̑}���Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ���p�҃O���[�v�A�N�Z�X�֘A���폜����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void deleteAclRelation(String aclId, User user, Connection conn, Model errors) throws IOException {
		Statement stmt1 = null;
		try {
			StringBuilder sbSql1 = new StringBuilder("delete USER_GRP_ACL_RELATION");
			sbSql1.append(" where ");
			sbSql1.append("ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("' ");

			stmt1 = conn.createStatement();
			stmt1.executeUpdate(sbSql1.toString());

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
			}

			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�}�X�^�[�̍폜�Ɏ��s" + ErrorUtility.error2String(e));
			// for MUR
			//			category.error("�}�X�^�[�̍폜�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * �O���[�v�R�[�h���X�g�쐬
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<String> getGroupCodeList(User user, Model errors) throws IOException {
		ArrayList<String> groupCodeList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select USER_GRP_CODE");
			sbSql1.append(" from USER_GROUP_MASTER order by USER_GRP_CODE");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				groupCodeList.add(rs1.getString("USER_GRP_CODE"));
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
		return groupCodeList;

	}

	/**
	 * ���̓`�F�b�N
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private boolean inputCheck(AccessLevelMasterMaintenanceForm accessLevelMasterMaintenanceForm) {
		boolean sts = false;
		String aclId = null;
		String aclName = null;
		for (int i = 0; i < accessLevelMasterMaintenanceForm.getRecList().size(); i++) {
			AccessLevelMasterMaintenanceElement accessLevelMasterMaintenanceElement = accessLevelMasterMaintenanceForm.getRecList(i);
			aclId = accessLevelMasterMaintenanceElement.getAclId();
			accessLevelMasterMaintenanceElement.setAclIdStyle("");
			if (aclId == null || aclId.length() == 0) {
				accessLevelMasterMaintenanceElement.setAclIdStyle("background-color:#FF0000;"); // color:#FF0000
				accessLevelMasterMaintenanceForm.addErrorMsg("���A�N�Z�X���x���h�c����͂��Ă��������B");

				sts = true;
			} else if (aclId.length() > accessLevelMasterMaintenanceForm.tableInfo.getColInfo("ACL_ID").getData_length()) {
				accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;"); //
				accessLevelMasterMaintenanceForm.addErrorMsg("�����͂����A�N�Z�X���x���h�c�̕��������������܂��B");

				sts = true;
			}
			aclName = accessLevelMasterMaintenanceElement.getAclName();
			accessLevelMasterMaintenanceElement.setAclNameStyle("");
			if (aclName == null || aclName.length() == 0) {
			} else if (aclName.length() > accessLevelMasterMaintenanceForm.tableInfo.getColInfo("ACL_NAME").getData_length()) {
				accessLevelMasterMaintenanceElement.setAclIdStyle("color:#FF0000;"); //
				accessLevelMasterMaintenanceForm.addErrorMsg("�����͂����A�N�Z�X���x�����̕��������������܂��B");

				sts = true;
			}
		}

		return sts;
	}

	/**
	 * �A�N�Z�X���x���}�X�^�[�J�������擾
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private TableInfo getAclLevelMasterAttrList(User user, Model errors) throws IOException {
		TableInfo tableInfo = null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			tableInfo = TableInfoDB.getTableInfoArray("ACCESS_LEVEL_MASTER", conn);
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
	 * �A�N�Z�X���x�����g�p�����`�F�b�N����B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private boolean aclLevelUsedCheck(String aclId, User user, Model errors) throws IOException {
		//	    ArrayList drwgTableList = TableInfoArray.getTableArry();
		boolean sts = false;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		int hit = 0;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select  count(*) CNT from INDEX_DB");
			sbSql1.append(" where ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("'");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if (rs1.next()) {
				hit = rs1.getInt("CNT");
				if (hit > 0) {
					sts = true;
				}
			}
			StringBuilder sbSql2 = new StringBuilder("select  count(*) CNT from NOTIFY_NO");
			sbSql2.append(" where ACL_ID='");
			sbSql2.append(aclId);
			sbSql2.append("'");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql2.toString());
			rs1 = stmt1.executeQuery(sbSql2.toString());
			if (rs1.next()) {
				hit = rs1.getInt("CNT");
				if (hit > 0) {
					sts = true;
				}
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
		return sts;

	}

	/**
	 * ���[�U�[�h�c�����łɓo�^����Ă��邩����������B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private int aclIdDuplicateChk(String aclId, User user, Model errors) throws IOException {
		int recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select count(*) CNT");
			sbSql1.append(" from ACCESS_LEVEL_MASTER ");
			sbSql1.append(" where ACL_ID='");
			sbSql1.append(aclId);
			sbSql1.append("'");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			if (rs1.next()) {
				recCount = rs1.getInt("CNT");
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
		return recCount;

	}
}
