package tyk.drasap.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import tyk.drasap.common.DrasapPropertiesFactory;
import tyk.drasap.common.DrasapUtil;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.TableInfo;
import tyk.drasap.common.TableInfoDB;
import tyk.drasap.common.User;
import tyk.drasap.common.UserKeyColDB;
import tyk.drasap.common.UserKeyColInfo;
import tyk.drasap.errlog.ErrorLoger;
import tyk.drasap.springfw.action.BaseAction;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �e�[�u�������e�i���X��ʂ�Action�B
 */
@Controller
public class TableMaintenanceAction extends BaseAction {
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
	@PostMapping("/tableMaintenance")
	public Object execute(
			TableMaintenanceForm form,
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
		TableMaintenanceForm tableMaintenanceForm = form;
		if ("init".equals(session.getAttribute("act"))) {
			tableMaintenanceForm.setAct("init");
			session.removeAttribute("act");
		}

		tableMaintenanceForm.clearErrorMsg();
		//
		if ("init".equals(tableMaintenanceForm.getAct())) {
			ArrayList<String> tableList = getTableList(user, errors);
			Connection conn = null;
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			UserKeyColDB UserKeyColDB = new UserKeyColDB(user, conn);
			try {
				conn.close();
			} catch (Exception e) {
			}
			tableMaintenanceForm = new TableMaintenanceForm();
			tableMaintenanceForm.setTableList(tableList);
			tableMaintenanceForm.UserKeyColDB = UserKeyColDB;
			String initTable = tableMaintenanceForm.getTableList().get(0).toString();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(initTable, UserKeyColDB, user, errors);
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(initTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return "success";
		}
		if ("update".equals(tableMaintenanceForm.getAct())) {
			if (inputCheck(tableMaintenanceForm)) {
				return "update";
			}
			updateTableMaintenanceForm(tableMaintenanceForm, user, errors);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			if (Objects.isNull(errors.getAttribute("message"))) {
				return "update";
			}
			//saveErrors(request, errors);
			request.setAttribute("errors", errors);
			return "error";
		}
		if ("delete".equals(tableMaintenanceForm.getAct())) {
			deleteTableMaintenanceForm(tableMaintenanceForm, user, errors);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			if (!Objects.isNull(errors.getAttribute("message"))) {
				//saveErrors(request, errors);
				request.setAttribute("errors", errors);
				return "error";
			}
		} else if ("addrecord".equals(tableMaintenanceForm.getAct())) {
			TableMaintenanceRec tableMaintenanceRec = addNewRecord(tableMaintenanceForm);
			tableMaintenanceForm.addRecList(tableMaintenanceRec);
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
		} else if ("search".equals(tableMaintenanceForm.getAct())) {
			tableMaintenanceForm.clearWhereStr();
			String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(selectTable, tableMaintenanceForm.UserKeyColDB, user, errors);
			tableMaintenanceForm.setFromRecNo(0);
			tableMaintenanceForm.setToRecNo(tableMaintenanceForm.getRecNoPerPage());
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return "search";
		} else if ("wheresearch".equals(tableMaintenanceForm.getAct())) {
			String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrList = getTableAttrList(selectTable, tableMaintenanceForm.UserKeyColDB, user, errors);
			tableMaintenanceForm.setFromRecNo(0);
			tableMaintenanceForm.setToRecNo(tableMaintenanceForm.getRecNoPerPage());
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, attrList, tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setAttrList(attrList);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
			return "search";
		} else if ("prevpage".equals(tableMaintenanceForm.getAct())) {
			if (tableMaintenanceForm.getFromRecNo() <= 0) {
				return "success";
			}
			long prevStartRecNo = tableMaintenanceForm.getFromRecNo() - tableMaintenanceForm.getRecNoPerPage();
			long prevEndNo = tableMaintenanceForm.getFromRecNo();

			tableMaintenanceForm.setFromRecNo(prevStartRecNo);
			tableMaintenanceForm.setToRecNo(prevEndNo);
			String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
		} else if ("nextpage".equals(tableMaintenanceForm.getAct())) {
			if (tableMaintenanceForm.getToRecNo() >= tableMaintenanceForm.getRecCount()) {
				return "success";
			}
			long nextStartRecNo = tableMaintenanceForm.getToRecNo();
			long nextEndNo = tableMaintenanceForm.getToRecNo() + tableMaintenanceForm.getRecNoPerPage();
			if (nextEndNo >= tableMaintenanceForm.getRecCount()) {
				nextEndNo = tableMaintenanceForm.getRecCount();
			}

			tableMaintenanceForm.setFromRecNo(nextStartRecNo);
			tableMaintenanceForm.setToRecNo(nextEndNo);
			String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
		} else if ("directpage".equals(tableMaintenanceForm.getAct())) {
			long startRec = Long.parseLong(tableMaintenanceForm.getSelectPage());
			long endRec = startRec + tableMaintenanceForm.getRecNoPerPage();
			if (endRec >= tableMaintenanceForm.getRecCount()) {
				endRec = tableMaintenanceForm.getRecCount();
			}

			tableMaintenanceForm.setFromRecNo(startRec);
			tableMaintenanceForm.setToRecNo(endRec);
			String selectTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceRec> tableValue = getTableValue(selectTable, tableMaintenanceForm.getAttrList(), tableMaintenanceForm, user, errors);
			tableMaintenanceForm.setRecList(tableValue);
			session.removeAttribute("tableMaintenanceForm");
			session.setAttribute("tableMaintenanceForm", tableMaintenanceForm);
		} else if ("export".equals(tableMaintenanceForm.getAct())) {
			String selectTable = tableMaintenanceForm.getSelectTable();
			createCsv(request, response, selectTable, user, tableMaintenanceForm, errors);
			return new ResponseEntity<>(HttpStatus.OK);
		} else if ("inport".equals(tableMaintenanceForm.getAct())) {
			tableMaintenanceForm.getSelectTable();
			uploadCsv(request, user, tableMaintenanceForm, errors);
		}

		return "success";
	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateTableMaintenanceForm(TableMaintenanceForm tableMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			//			String selectedTable = tableMaintenanceForm.getSelectTable();
			// �`�F�b�N���Ă�����̂���koushinn
			//            ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();
			for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
				TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
				if (tableMaintenanceRec.isNew()) {
					if (duplicateChk(i, tableMaintenanceForm, user, errors) > 0) {
						tableMaintenanceForm.addErrorMsg("�����̃��R�[�h�͂��łɑ��݂��Ă��܂��B");
						continue;
					}
					insertRecord(i, tableMaintenanceForm, user, conn, errors);
				} else if (tableMaintenanceRec.isCheck()) {
					updateRecord(i, tableMaintenanceForm, user, conn, errors);
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
	private void deleteTableMaintenanceForm(TableMaintenanceForm tableMaintenanceForm, User user, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);// ��g�����U�N�V����
			String selectedTable = tableMaintenanceForm.getSelectTable();
			// �`�F�b�N���Ă�����̂���koushinn
			//            ArrayList attrInfo = tableMaintenanceForm.getAttrList();
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
				tableMaintenanceForm.addErrorMsg("���̃e�[�u��[" + selectedTable + "]�̓L�[���������߃����e�i���X�ł��܂���B");
				return;
			}
			for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
				TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
				if (tableMaintenanceRec.isCheck()) {
					StringBuilder sbSql1 = new StringBuilder("delete ");
					sbSql1.append(selectedTable);
					if (userKeyColInfo.getNoKeyCol() > 0) {
						sbSql1.append(" where ");
					}
					for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
						if (j != 0) {
							sbSql1.append("and ");
						}
						sbSql1.append(userKeyColInfo.getkeyCol(j));
						sbSql1.append("='");
						int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
						TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
						sbSql1.append(tableMaintenanceVal.getVal());
						sbSql1.append("' ");
					}

					stmt1 = conn.createStatement();
					stmt1.executeUpdate(sbSql1.toString());

					tableMaintenanceForm.recList.remove(i);
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
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
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
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void updateRecord(int recno, TableMaintenanceForm tableMaintenanceForm, User user, Connection conn, Model errors) throws IOException {
		PreparedStatement pstmt1 = null;
		TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try {
			String selectedTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();

			StringBuilder sbSql1 = new StringBuilder("update ");
			sbSql1.append(selectedTable);
			sbSql1.append(" set ");
			for (int j = 0; j < attrInfo.size(); j++) {
				if (j != 0) {
					sbSql1.append(",");
				}
				TableMaintenanceElement tableMaintenanceElement = attrInfo.get(j);
				sbSql1.append(tableMaintenanceElement.getColumn_name());
				if ("MODIFIED_DATE".equals(tableMaintenanceElement.getColumn_name())) {
					sbSql1.append("=sysdate ");
				} else if ("DATE".equals(tableMaintenanceElement.getData_type())) {
					sbSql1.append("=TO_DATE(?,'YY/MM/DD HH24:MI:SS')");
				} else {
					sbSql1.append("=? ");
				}
			}
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
				tableMaintenanceForm.addErrorMsg("���̃e�[�u��[" + selectedTable + "]�̓L�[���������߃����e�i���X�ł��܂���B");
				return;
			}
			sbSql1.append("where ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				if (j != 0) {
					sbSql1.append("and ");
				}
				sbSql1.append(userKeyColInfo.getkeyCol(j));
				sbSql1.append("='");
				int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
				sbSql1.append(tableMaintenanceVal.getVal());
				sbSql1.append("' ");
			}

			pstmt1 = conn.prepareStatement(sbSql1.toString());
			int idx = 1;
			for (int j = 0; j < tableMaintenanceRec.getValList().size(); j++) {
				TableMaintenanceElement tableMaintenanceElement = attrInfo.get(j);
				if ("MODIFIED_DATE".equals(tableMaintenanceElement.getColumn_name())) {
					continue;
				}
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				pstmt1.setString(idx++, tableMaintenanceVal.getVal());
			}
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
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				pstmt1.close();
			} catch (Exception e) {
			}
			tableMaintenanceRec.setNew(false);
			tableMaintenanceRec.setCheck(false);
		}
	}

	/**
	 * ���̓`�F�b�N���s���B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private boolean inputCheck(TableMaintenanceForm tableMaintenanceForm) {
		boolean sts = false;
		ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();
		for (int i = 0; i < tableMaintenanceForm.getRecList().size(); i++) {
			TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(i);
			for (int j = 0; j < attrInfo.size(); j++) {
				TableMaintenanceElement tableMaintenanceElement = attrInfo.get(j);
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				tableMaintenanceVal.setDispStyle("");
				if (tableMaintenanceElement.isKey()) {
					if (tableMaintenanceVal.getVal() == null || tableMaintenanceVal.getVal().length() == 0) {
						tableMaintenanceVal.setDispStyle("background-color:#FF0000;"); // color:#FF0000
						tableMaintenanceForm.addErrorMsg("��" + tableMaintenanceElement.getColumn_name() + "����͂��Ă��������B");

						sts = true;
					} else if (tableMaintenanceVal.getVal().length() > tableMaintenanceForm.getAttrList(j).getData_length()) {
						tableMaintenanceVal.setDispStyle("color:#FF0000;"); //
						tableMaintenanceForm.addErrorMsg("�����͂���" + tableMaintenanceElement.getColumn_name() + "�̕��������������܂��B");

						sts = true;
					}
				} else if ("N".equals(tableMaintenanceElement.getNullable())) { // not null
					if (tableMaintenanceVal.getVal() == null || tableMaintenanceVal.getVal().length() == 0) {
						tableMaintenanceVal.setDispStyle("background-color:#FF0000;"); // color:#FF0000
						tableMaintenanceForm.addErrorMsg("��" + tableMaintenanceElement.getColumn_name() + "����͂��Ă��������B");

						sts = true;
					}
				}
			}
		}

		return sts;
	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void insertRecord(int recno, TableMaintenanceForm tableMaintenanceForm, User user, Connection conn, Model errors) throws IOException {
		Statement stmt1 = null;
		TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try {
			String selectedTable = tableMaintenanceForm.getSelectTable();
			ArrayList<TableMaintenanceElement> attrInfo = tableMaintenanceForm.getAttrList();

			StringBuilder sbSql1 = new StringBuilder("insert into ");
			sbSql1.append(selectedTable);
			sbSql1.append(" (");
			for (int j = 0; j < attrInfo.size(); j++) {
				if (j != 0) {
					sbSql1.append(",");
				}
				TableMaintenanceElement tableMaintenanceElement = attrInfo.get(j);
				sbSql1.append(tableMaintenanceElement.getColumn_name());
			}
			sbSql1.append(")");
			sbSql1.append(" values(");
			for (int j = 0; j < tableMaintenanceRec.getValList().size(); j++) {
				if (j != 0) {
					sbSql1.append(",");
				}
				TableMaintenanceElement tableMaintenanceElement = attrInfo.get(j);
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(j);
				if ("DATE".equals(tableMaintenanceElement.getData_type())) {
					sbSql1.append("TO_DATE('");
					sbSql1.append(tableMaintenanceVal.getVal());
					sbSql1.append("','YY/MM/DD HH24:MI:SS')");
				} else {
					sbSql1.append("'");
					sbSql1.append(tableMaintenanceVal.getVal());
					sbSql1.append("'");
				}
			}
			sbSql1.append(")");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			stmt1.executeUpdate(sbSql1.toString());
			try {
				stmt1.close();
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
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				stmt1.close();
			} catch (Exception e) {
			}
			tableMaintenanceRec.setNew(false);
			tableMaintenanceRec.setCheck(false);
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
	private TableMaintenanceRec addNewRecord(TableMaintenanceForm tableMaintenanceForm) throws IOException {
		ArrayList<TableMaintenanceElement> attrList = new ArrayList<TableMaintenanceElement>();

		attrList = tableMaintenanceForm.getAttrList();
		ArrayList<TableMaintenanceVal> recValue = new ArrayList<TableMaintenanceVal>();
		TableMaintenanceRec tableMaintenanceRec = new TableMaintenanceRec();
		for (int i = 0; i < attrList.size(); i++) {
			TableMaintenanceVal tableMaintenanceVal = new TableMaintenanceVal();
			tableMaintenanceVal.val = null;
			recValue.add(tableMaintenanceVal);
		}
		tableMaintenanceRec.setRec_no(Integer.toString(tableMaintenanceForm.getRecList().size() + 1));
		tableMaintenanceRec.setValList(recValue);
		tableMaintenanceRec.setNew(true);
		tableMaintenanceRec.setCheck(true);
		return tableMaintenanceRec;

	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<TableMaintenanceElement> getTableAttrList(String selectTable, UserKeyColDB UserKeyColDB, User user, Model errors) throws IOException {
		ArrayList<TableMaintenanceElement> attrList = new ArrayList<TableMaintenanceElement>();
		Connection conn = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			TableInfo tableInfo = TableInfoDB.getTableInfoArray(selectTable, conn);
			for (int i = 0; i < tableInfo.getNoCol(); i++) {
				TableMaintenanceElement attrInfo = new TableMaintenanceElement();
				attrInfo.setColumn_name(tableInfo.getColInfo(i).getColumn_name());
				attrInfo.setData_type(tableInfo.getColInfo(i).getData_type());
				attrInfo.setdata_length(tableInfo.getColInfo(i).getData_length());
				attrInfo.setKey("0");
				attrInfo.setNullable(tableInfo.getColInfo(i).getNullable());
				for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
					if (tableInfo.getColInfo(i).getColumn_name().equals(userKeyColInfo.getkeyCol(j))) {
						attrInfo.setKey("1");
						break;
					}
				}
				attrList.add(attrInfo);
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("�e�[�u�����̎擾�ɂɎ��s\n" + ErrorUtility.error2String(e));
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return attrList;

	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<String> getTableList(User user, Model errors) throws IOException {
		ArrayList<String> tableList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select table_name from ");
			sbSql1.append("user_tables");
			sbSql1.append(" where ");
			//			sbSql1.append(" TABLESPACE_NAME like '%");
			//			sbSql1.append(user.getSchema());
			//			sbSql1.append("%' and");
			sbSql1.append(" dropped='NO' ");
			//			sbSql1.append(" and TABLE_NAME like '%MASTER%'");
			sbSql1.append(" order by TABLE_NAME ");
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			while (rs1.next()) {
				tableList.add(rs1.getString("TABLE_NAME"));
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
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
		return tableList;

	}

	/**
	 * �����\�����ڂ���������B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private ArrayList<TableMaintenanceRec> getTableValue(String selectTable, ArrayList<TableMaintenanceElement> attrList, TableMaintenanceForm tableMaintenanceForm, User user, Model errors) throws IOException {
		ArrayList<TableMaintenanceRec> tableValue = new ArrayList<TableMaintenanceRec>();
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			long recCount = getRecCount(tableMaintenanceForm.getWhereStr(), selectTable, user, errors);
			tableMaintenanceForm.setRecCount(recCount);
			//		    if (recCount > 20000) {
			//                tableMaintenanceForm.addErrorMsg("�����̃e�[�u���̓��R�[�h�����������߁A�Ώۂɂ��܂���B");
			//                return tableValue;
			//		    }
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			TableMaintenanceElement tableMaintenanceElement = null;
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
				tableMaintenanceForm.addErrorMsg("���̃e�[�u��[" + selectTable + "]�̓L�[���������߃����e�i���X�ł��܂���B");
				return tableValue;
			}

			StringBuilder sbSql1 = new StringBuilder("select * from (");
			sbSql1.append("select row_number() over (");
			sbSql1.append(" order by ");
			if (userKeyColInfo.getNoKeyCol() > 0) {
				for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
					if (j != 0) {
						sbSql1.append(", ");
					}
					sbSql1.append(userKeyColInfo.getkeyCol(j));
				}
			} else {
				tableMaintenanceElement = attrList.get(0);
				sbSql1.append(tableMaintenanceElement.getColumn_name());
			}
			sbSql1.append(") ct, ");
			for (int i = 0; i < attrList.size(); i++) {
				if (i > 0) {
					sbSql1.append(", ");
				}
				tableMaintenanceElement = attrList.get(i);
				if ("DATE".equals(tableMaintenanceElement.getData_type())) {
					sbSql1.append("TO_CHAR (");
					sbSql1.append(tableMaintenanceElement.getColumn_name());
					sbSql1.append(",'YY/MM/DD HH24:MI:SS') ");
				}
				sbSql1.append(tableMaintenanceElement.getColumn_name());
			}
			sbSql1.append(" from ");
			sbSql1.append(selectTable);

			if (tableMaintenanceForm.getWhereStr() != null && tableMaintenanceForm.getWhereStr().length() > 0) {
				sbSql1.append(" where " + tableMaintenanceForm.getWhereStr());
			}
			sbSql1.append(")");
			sbSql1.append(" where ct >=");
			sbSql1.append(tableMaintenanceForm.getFromRecNo());
			sbSql1.append(" AND ct < ");
			sbSql1.append(tableMaintenanceForm.getToRecNo());
			if (userKeyColInfo.getNoKeyCol() > 0) {
				sbSql1.append(" order by ");
			}
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				if (j != 0) {
					sbSql1.append(", ");
				}
				sbSql1.append(userKeyColInfo.getkeyCol(j));
			}
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			while (rs1.next()) {
				ArrayList<TableMaintenanceVal> recValue = new ArrayList<TableMaintenanceVal>();
				TableMaintenanceRec tableMaintenanceRec = new TableMaintenanceRec();
				for (int i = 0; i < attrList.size(); i++) {
					TableMaintenanceVal tableMaintenanceVal = new TableMaintenanceVal();
					tableMaintenanceElement = attrList.get(i);
					tableMaintenanceVal.val = rs1.getString(tableMaintenanceElement.getColumn_name());
					recValue.add(tableMaintenanceVal);
				}
				tableMaintenanceRec.setRec_no(Integer.toString(rs1.getRow()));
				tableMaintenanceRec.setValList(recValue);
				tableValue.add(tableMaintenanceRec);
			}

			createPageList(tableMaintenanceForm);
			long pageNo = tableMaintenanceForm.getFromRecNo();
			tableMaintenanceForm.setSelectPage(Long.toString(pageNo));
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
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
		return tableValue;

	}

	/**
	 * ���R�[�h�����擾����B��O�����������ꍇ�Aerrors��add����B
	 * @param userGroupCode ���[�U�[�O���[�v�R�[�h
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private long getRecCount(String whereStr, String selectTable, User user, Model errors) throws IOException {
		long recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			StringBuilder sbSql1 = new StringBuilder("select count(*) CNT ");
			sbSql1.append("from ");
			sbSql1.append(selectTable);
			if (whereStr != null && whereStr.length() > 0) {
				sbSql1.append(" where " + whereStr);
			}
			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			if (rs1.next()) {
				recCount = rs1.getLong("CNT");
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
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

	/**
	 * ���R�[�h�����łɓo�^����Ă��邩�`�F�b�N����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private int duplicateChk(int recno, TableMaintenanceForm tableMaintenanceForm, User user, Model errors) throws IOException {
		int recCount = 0;
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		TableMaintenanceRec tableMaintenanceRec = tableMaintenanceForm.getRecList(recno);
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����
			// �V�K���R�[�h�̏d���`�F�b�N
			String selectedTable = tableMaintenanceForm.getSelectTable();

			StringBuilder sbSql1 = new StringBuilder("select count(*) CNT from ");
			sbSql1.append(selectedTable);

			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectedTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
				tableMaintenanceForm.addErrorMsg("���̃e�[�u��[" + selectedTable + "]�̓L�[���������߃����e�i���X�ł��܂���B");
				return recCount;
			}
			sbSql1.append(" where ");
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				if (j != 0) {
					sbSql1.append("and ");
				}
				sbSql1.append(userKeyColInfo.getkeyCol(j));
				sbSql1.append("='");
				int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
				TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
				sbSql1.append(tableMaintenanceVal.getVal());
				sbSql1.append("' ");
			}

			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());

			if (rs1.next()) {
				recCount = rs1.getInt("CNT");
			}

			if (recCount > 0) {
				for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
					int idx = tableMaintenanceForm.getColNo(userKeyColInfo.getkeyCol(j));
					TableMaintenanceVal tableMaintenanceVal = tableMaintenanceRec.getValList(idx);
					tableMaintenanceVal.setDispStyle("color:#FF0000;"); // color:#FF0000
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
		return recCount;

	}

	/**
	 * ���R�[�h�����łɓo�^����Ă��邩�`�F�b�N����B��O�����������ꍇ�Aerrors��add����B
	 * @param user
	 * @param errors
	 * @throws IOException
	 * @throws
	 */
	private void createPageList(TableMaintenanceForm tableMaintenanceForm) throws IOException {
		ArrayList<String> pageList = new ArrayList<String>();
		ArrayList<String> pageNameList = new ArrayList<String>();
		String fromRec = "";
		String toRec = "";
		for (long i = 0; i < tableMaintenanceForm.getRecCount(); i = i + tableMaintenanceForm.getRecNoPerPage()) {
			fromRec = Long.toString(i + 1);
			toRec = Long.toString(Math.min(i + tableMaintenanceForm.getRecNoPerPage(), tableMaintenanceForm.getRecCount()));
			pageNameList.add(fromRec + "�`" + toRec);
			pageList.add(Long.toString(i));
		}
		tableMaintenanceForm.setPageList(pageList);
		tableMaintenanceForm.setPageNameList(pageNameList);

	}

	/**
	 * �w�肵���t�@�C���e�[�u����CSV�`���ō쐬����B
	 * �쐬�Ɏ��s�����ꍇ�Aerrors�ɏ����o���B
	 * @param outFile �o�͐�̃t�@�C��
	 * @param searchResultForm
	 * @param allAttr �S�����Ȃ�true
	 * @param user
	 * @param errors
	 */
	private void createCsv(HttpServletRequest request, HttpServletResponse response,
			String selectTable, User user, TableMaintenanceForm tableMaintenanceForm, Model errors) {
		OutputStreamWriter out = null;
		try {
			HttpSession session = request.getSession();
			// �e���|�����̃t�H���_�̃t���p�X
			String tempDirName = DrasapUtil.getRealTempPath(request);
			String outFileName = tempDirName + File.separator + session.getId() + "_" + new Date().getTime();
			File outFile = new File(outFileName);

			// �o�̓X�g���[���̏���
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile)),
					"Windows-31J");
			outputCsv(out, user, selectTable, tableMaintenanceForm, errors);
			try {
				out.close();
			} catch (Exception e) {
			}
			//
			String streamFileName = selectTable + ".csv";// �w�b�_�ɃZ�b�g����t�@�C����
			response.setContentType("text/comma-separated-values");
			response.setHeader("Content-Disposition", "attachment;" +
					" filename=" + new String(streamFileName.getBytes("Windows-31J"), "ISO8859_1"));
			//���̂܂܂��Ɠ��{�ꂪ������
			//response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			response.setContentLength((int) outFile.length());

			BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(outFile));
			int c;
			while ((c = in.read()) != -1) {
				outStream.write(c);
			}
			in.close();
			outStream.flush();
			outStream.close();
			//
			//			category.debug("outStream.close()");
			// CSV�t�@�C�����폜����
			if (outFile.delete()) {
				//				category.debug(outFileName + " ���폜����");
			}
		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.csv", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this, "�������ʂ̃t�@�C���o�͂Ɏ��s" + e.getMessage());

			// for MUR
			//			category.error("�������ʂ̃t�@�C���o�͂Ɏ��s\n" + ErrorUtility.error2String(e));

		} finally {
		}

	}

	/**
	 * �ێ����Ă��錟�����ʂ����ɁACSV�t�@�C�����쐬����B
	 * @param out
	 * @param allAttr �S�����Ȃ� true
	 * @throws IOException
	 */
	public void outputCsv(OutputStreamWriter out, User user, String selectTable, TableMaintenanceForm tableMaintenanceForm, Model errors) throws IOException {
		Connection conn = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(true);// ��g�����U�N�V����

			TableMaintenanceElement tableMaintenanceElement = null;
			UserKeyColDB UserKeyColDB = tableMaintenanceForm.UserKeyColDB;
			UserKeyColInfo userKeyColInfo = UserKeyColDB.getKeyCol(selectTable);
			if (userKeyColInfo.getNoKeyCol() == 0) {
				tableMaintenanceForm.addErrorMsg("���̃e�[�u��[" + selectTable + "]�̓L�[���������߃����e�i���X�ł��܂���B");
				return;
			}

			StringBuilder sbSql1 = new StringBuilder("select * from ");
			sbSql1.append(selectTable);

			if (userKeyColInfo.getNoKeyCol() > 0) {
				sbSql1.append(" order by ");
			}
			for (int j = 0; j < userKeyColInfo.getNoKeyCol(); j++) {
				if (j != 0) {
					sbSql1.append(", ");
				}
				sbSql1.append(userKeyColInfo.getkeyCol(j));
			}

			// �w�b�_�[���쐬
			ArrayList<TableMaintenanceElement> attrList = tableMaintenanceForm.getAttrList();
			for (int i = 0; i < attrList.size(); i++) {
				tableMaintenanceElement = attrList.get(i);
				if (i > 0) {
					out.write(',');
				}
				out.write(tableMaintenanceElement.getColumn_name());
			}
			out.write("\r\n");

			stmt1 = conn.createStatement();
			//			category.debug(sbSql1.toString());
			rs1 = stmt1.executeQuery(sbSql1.toString());
			String val = "";
			while (rs1.next()) {
				for (int i = 0; i < attrList.size(); i++) {
					tableMaintenanceElement = attrList.get(i);
					if (i > 0) {
						out.write(',');
					}
					val = rs1.getString(tableMaintenanceElement.getColumn_name());
					if (val != null && val.length() != 0) {
						out.write(val);
					}
				}
				out.write("\r\n");
			}

		} catch (Exception e) {
			// for ���[�U�[
			MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.failed.search.list", new Object[] { e.getMessage() }, null));
			// for �V�X�e���Ǘ���
			ErrorLoger.error(user, this,
					DrasapPropertiesFactory.getDrasapProperties(this).getProperty("err.sql"));
			// for MUR
			//			category.error("���������Ɉ�v���������̃J�E���g�Ɏ��s\n" + ErrorUtility.error2String(e));
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
	 * �w�肵���t�@�C���e�[�u����CSV�`���ō쐬����B
	 * �쐬�Ɏ��s�����ꍇ�Aerrors�ɏ����o���B
	 * @param outFile �o�͐�̃t�@�C��
	 * @param searchResultForm
	 * @param allAttr �S�����Ȃ�true
	 * @param user
	 * @param errors
	 * @throws IOException
	 */
	private void uploadCsv(HttpServletRequest request, User user, TableMaintenanceForm tableMaintenanceForm, Model errors) throws IOException {
		BufferedInputStream inBuffer = null;
		BufferedOutputStream outBuffer = null;

		InputStream is;
		try {
			MultipartFile fileUp = tableMaintenanceForm.getFileUp();
			is = fileUp.getInputStream();
			inBuffer = new BufferedInputStream(is);
			// �e���|�����̃t�H���_�̃t���p�X
			String tempDirName = DrasapUtil.getRealTempPath(request);
			FileOutputStream fos = new FileOutputStream(tempDirName + File.separator + fileUp.getName());

			outBuffer = new BufferedOutputStream(fos);

			int contents = 0;
			while ((contents = inBuffer.read()) != -1) {
				outBuffer.write(contents);
			}

			outBuffer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			inBuffer.close();
			outBuffer.close();

			//            fileUp.destroy();
		}

	}
}
