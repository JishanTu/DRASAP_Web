package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;

/**
 * ���[�U�[�}�X�^�[�ƑΉ�����DB�N���X�B
 * @version 2013/09/17 yamagishi
 */
public class UserDB {
	private static Logger category = Logger.getLogger(UserDB.class.getName());

	/** �p�X���[�h�ő包�� */
	public static final int PASSWORD_MAX_LENGTH = 20;

	/**
	 * �擾�������[�U�[�����Auser�ɕt������B
	 * �p�X���[�h�s�v�B�|�[�^�������SysPass�`�F�b�N�����ꍇ�Ɏg�p����B
	 * ��������� true��Ԃ��B
	 * ID,�p�X���[�h�̔�r�ł͑啶������������ʂ��Ȃ��B
	 * @param user
	 * @param id
	 * @param conn
	 * @return true=�����Bfalse=ID�܂��̓p�X���[�h���������Ȃ��B
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, Connection conn)
			throws Exception {
		// �p�X���[�h�`�F�b�N���Ȃ��B
		// �_�~�[�p�X���[�h�Ƃ��� "" ��n���B
		return addUserInfo(user, id, "", false, conn);
	}

	/**
	 * �擾�������[�U�[�����Auser�ɕt������B
	 * ��������� true��Ԃ��B
	 * ID,�p�X���[�h�̔�r�ł͑啶������������ʂ��Ȃ��B
	 * @param user
	 * @param id
	 * @param passwd
	 * @param conn
	 * @return true=�����Bfalse=ID�܂��̓p�X���[�h���������Ȃ��B
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, String passwd, Connection conn)
			throws Exception {
		// �p�X���[�h�`�F�b�N����
		return addUserInfo(user, id, passwd, true, conn);
	}

	/**
	 * �p�X���[�h�̗L�������`�F�b�N
	 * @param user
	 * @param errors
	 * @param conn
	 * @return�@ 0: �ύX�� <br/> 1: �ύX�L(�p�X���[�h�����[�UID) <br/> 2: �ύX�L(�L�������؂�)
	 * @throws Exception
	 */
	public static int checkPasswordExpiry(User user, Model errors, Connection conn) throws Exception {
		String userId = user.getId();
		String currentPass = UserDB.getPassword(userId, conn);

		if (StringUtils.isBlank(currentPass) || userId.equals(currentPass)) {
			// �p�X���[�h���ݒ� �������� �p�X���[�h�����[�UID�Ɠ���
			return 1;
		}

		/*
		 *  �p�X���[�h�L�������̊m�F
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar passwdUpCalendar = Calendar.getInstance();

		// �p�X���[�h��`�t�@�C������l�擾
		UserDef userdef = new UserDef();
		HashMap<String, String> passwdDefMap = userdef.getPasswdDefinition(errors);

		// �p�X���[�h�L�����������`�F�b�N
		int pwdLmtDay = Integer.parseInt(passwdDefMap.get(UserDef.PWD_LMT_DAY));

		// ���ݓ����̎擾
		Calendar nowCal = Calendar.getInstance();

		// �����b���N���A
		nowCal.clear(Calendar.MINUTE);
		nowCal.clear(Calendar.SECOND);
		nowCal.clear(Calendar.MILLISECOND);
		nowCal.set(Calendar.HOUR_OF_DAY, 0);

		// �p�X���[�h�ݒ���擾
		Date pwdUpDate = user.getPasswdUpdDate();

		if (pwdUpDate == null) {
			// �p�X���[�h�ݒ�������ݒ�̏ꍇ�͍Đݒ�Ώ�
			return 2;
		}

		passwdUpCalendar.setTime(pwdUpDate); // DATE -> Calendar

		// �p�X���[�h�ݒ�� + �L����������
		passwdUpCalendar.add(Calendar.DATE, pwdLmtDay);

		Date passwdLimitDate = passwdUpCalendar.getTime(); // Calendar -> DATE
		Date nowDate = nowCal.getTime(); // Calendar -> DATE

		category.debug("Now Date=" + sdf.format(nowDate));
		category.debug("Password Limit Date=" + sdf.format(passwdLimitDate));

		// �p�X���[�h�ݒ�� + �L���������� < ���ݓ����̏ꍇ�̓p�X���[�h�ύX
		if (nowDate.after(passwdLimitDate)) {
			return 2;
		}

		return 0;
	}

	/**
	 * �擾�������[�U�[�����Auser�ɕt������B
	 * ��������� true��Ԃ��B
	 * ID�̔�r�ł͑啶������������ʂ��Ȃ��B
	 * 2019.12.06 yamamoto ���O�C����ʐV�ݑΉ��Ńp�X���[�h�͑啶����������ʂ���悤�ɑΉ�
	 * @param user
	 * @param id
	 * @param passwd
	 * @param checkPswd �p�X���[�h���`�F�b�N����Ȃ�Atrue�B�`�F�b�N���Ȃ��Ȃ�false�B
	 * @param conn
	 * @return true=�����Bfalse=ID�܂��̓p�X���[�h���������Ȃ��B
	 * @throws Exception
	 */
	public static boolean addUserInfo(User user, String id, String passwd, boolean checkPswd, Connection conn)
			throws Exception {
		// id��啶����
		id = id.toUpperCase();
		//		passwd = passwd.toUpperCase();
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// id�Ō������s��
			// ������Statement�ɕύX�B '04.Mar.2 Hirata
			// �����A����������悤�ɕύX
			pstmt = conn.prepareStatement("select *" +
					" from USER_MASTER, DEPARTMENT_MASTER" +
					" where USER_MASTER.DEPT_CODE=DEPARTMENT_MASTER.DEPT_CODE(+)" +
					" and TRIM(USER_ID)=?" +
					// �������E�E�Enull�܂��͍����ȑO(�������܂�)
					" and (VALID_DATE is null or TRUNC(VALID_DATE) <= TRUNC(sysdate))" +
					// �������E�E�Enull�܂��͍�������(�����͊܂܂Ȃ�)
					" and (EXPIRED_DATE is null or TRUNC(sysdate) < TRUNC(EXPIRED_DATE))");
			pstmt.setString(1, id.trim());
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				// ���[�U�[ID����v���Ȃ�
				return false;
			}
			String passwd_M = rs.getString("PASSWD");
			// �p�X���[�h�`�F�b�N���s�v�A�܂��̓p�X���[�h����v������
			// �ύX '04/04/13 by Hirata
			if (checkPswd && !passwd.equals(passwd_M)) {
				// �p�X���[�h����v���Ȃ�
				return false;
			}
			user.setId(id);
			user.setName(rs.getString("USER_NAME"));
			user.setNameE(rs.getString("ALPH_NAME"));
			user.setDept(rs.getString("DEPT_CODE"));
			user.setDeptName(rs.getString("DEPARTMENT"));
			// ���������J�������X�g��ݒ�
			for (int i = 1; i <= User.searchSelColNum; i++) {
				user.setSearchSelCol(i - 1, rs.getString("SEARCH_SELCOL" + i));
			}
			// �������ʃJ�������X�g��ݒ�
			for (int j = 1; j <= User.viewSelColNum; j++) {
				user.setViewSelCol(j - 1, rs.getString("VIEW_SELCOL" + j));
			}
			user.setOnlyNewestFlag(rs.getString("LATEST_REV_DISP_FLAG"));
			user.setDisplayCount(rs.getString("DISPLAY_COUNT"));
			user.setAdminFlag(rs.getString("ADMIN_FLAG"));// �Ǘ��҃t���O
			user.setPosition(rs.getString("POSITION"));// �E��
			// 2013.07.24 yamagishi add. start
			user.setAclUpdateFlag(rs.getString("ACL_UPDATE_FLAG")); // �A�N�Z�X���x���ύX���t���O
			user.setAclBatchUpdateFlag(rs.getString("ACL_BATCH_UPDATE_FLAG")); // �A�N�Z�X���x���ꊇ�X�V�c�[�����t���O
			user.setDlManagerFlag(rs.getString("DL_MANAGER_FLAG")); // �c�k�}�l�[�W�����p�\�t���O
			// 2013.07.24 yamagishi add. end
			// 2019.09.20 yamamoto add. start
			user.setPasswdUpdDate(rs.getDate("PASSWD_UPD_DATE")); // �p�X���[�h�ݒ��
			user.setReproUserFlag(rs.getString("REPRO_USER_FLAG")); // ���}�Ƀ��[�U�t���O
			user.setDwgRegReqFlag(rs.getString("DWG_REG_REQ_FLAG")); // �}�ʓo�^�˗��t���O
			// 2019.09.20 yamamoto add. end
			// 2020.02.10 yamamoto add. start
			user.setMultiPdfFlag(rs.getString("MULTI_PDF_FLAG")); // �}���`PDF�o�͋��t���O
			// 2020.02.10 yamamoto add. end
			String defaultUserGroup = rs.getString("USER_GRP_CODE");// ��������̗��p�҃O���[�v
			user.setDefaultUserGroup(defaultUserGroup);
			user.setThumbnailSize(rs.getString("THUMBNAIL_SIZE"));
			user.setResultDispMode(rs.getString("RESULT_DISP_MODE"));
			// ���p�\�ȑS�Ă̗��p�҃O���[�v���擾
			ArrayList<String> userGroupCodeArray = new ArrayList<String>();
			if (defaultUserGroup != null) {// ��������̗��p�҃O���[�v
				userGroupCodeArray.add(defaultUserGroup);
			}
			if (rs.getString("USER_GRP_CODE01") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v01
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE01"));
			}
			if (rs.getString("USER_GRP_CODE02") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v02
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE02"));
			}
			if (rs.getString("USER_GRP_CODE03") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v03
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE03"));
			}
			if (rs.getString("USER_GRP_CODE04") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v04
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE04"));
			}
			if (rs.getString("USER_GRP_CODE05") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v05
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE05"));
			}
			if (rs.getString("USER_GRP_CODE06") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v06
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE06"));
			}
			if (rs.getString("USER_GRP_CODE07") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v07
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE07"));
			}
			if (rs.getString("USER_GRP_CODE08") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v08
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE08"));
			}
			if (rs.getString("USER_GRP_CODE09") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v09
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE09"));
			}
			if (rs.getString("USER_GRP_CODE10") != null) {// ���[�U�[�}�X�^�̗��p�҃O���[�v10
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE10"));
			}
			// 2013.09.17 yamagishi add. start
			while (rs.next()) {
				// ���[�U�������̌�������ɏ������Ă���ꍇ
				userGroupCodeArray.add(rs.getString("USER_GRP_CODE"));// ��������̗��p�҃O���[�v
			}
			// 2013.09.17 yamagishi add. end
			ArrayList<UserGroup> userGroupes = UserGroupDB.getUserGroupArray(userGroupCodeArray, conn);
			// �擾�������p�҃O���[�v���Auser��add���Ă���
			for (int i = 0; i < userGroupes.size(); i++) {
				user.addUserGroup(userGroupes.get(i));
			}

			return true;
		} catch (Exception e) {
			throw e;

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

	/**
	 * DB����p�X���[�h�擾����
	 *
	 * @param id
	 * @param conn
	 * @return ����F�p�X���[�h������ <br>
	 *         �ُ�Fnull/���[�UID������ <br> �󕶎�/�p�X���[�h���ݒ�
	 * @throws Exception
	 */
	public static String getPassword(String id, Connection conn) throws Exception {

		String passwd = null;

		// id��啶����
		id = id.toUpperCase();
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// id�Ō������s��
			// �����A����������
			pstmt = conn.prepareStatement("select *" +
					" from USER_MASTER, DEPARTMENT_MASTER" +
					" where USER_MASTER.DEPT_CODE=DEPARTMENT_MASTER.DEPT_CODE(+)" +
					" and TRIM(USER_ID)=?" +
					// �������E�E�Enull�܂��͍����ȑO(�������܂�)
					" and (VALID_DATE is null or TRUNC(VALID_DATE) <= TRUNC(sysdate))" +
					// �������E�E�Enull�܂��͍�������(�����͊܂܂Ȃ�)
					" and (EXPIRED_DATE is null or TRUNC(sysdate) < TRUNC(EXPIRED_DATE))");
			pstmt.setString(1, id.trim());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				passwd = rs.getString("PASSWD");

				// �p�X���[�h�����ݒ�̏ꍇ�͋󕶎���ݒ肷��
				if (passwd == null) {
					passwd = "";
				}
			}

		} catch (Exception e) {
			throw e;

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

		return passwd;
	}

	/**
	 * ���[�U�}�X�^�X�V
	 *
	 * @param id
	 * @param column
	 * @param value
	 * @param conn
	 * @throws Exception
	 */
	public static void updateUserMaster(String id, String column, String value, Connection conn) throws Exception {
		// id��啶����
		id = id.toUpperCase();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// ���[�U�}�X�^�X�V
			stmt = conn.createStatement();

			// SQL����g�ݗ��Ă�
			String sql = "update USER_MASTER" +
					" set " + column + "='" + value.trim() + "'";

			if ("PASSWD".equals(column)) {
				// �p�X���[�h�X�V����
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date nowDate = new Date();

				sql = sql + "     ,PASSWD_UPD_DATE=TO_DATE('" + sdf.format(nowDate) + "','YYYY/MM/DD')";
				category.debug("nowdate:" + sdf.format(nowDate));
			}
			sql = sql + " where USER_ID='" + id.trim() + "'";

			category.debug("SQL:" + stmt.toString());
			stmt.executeUpdate(sql);

			// �R�~�b�g
			conn.commit();
		} catch (Exception e) {
			// ���[���o�b�N
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			// CLOSE����
			try {
				if (Objects.nonNull(rs)) {
					rs.close();
				}
			} catch (Exception e) {
			}
			try {
				if (Objects.nonNull(stmt)) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
