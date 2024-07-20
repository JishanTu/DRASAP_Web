package tyk.drasap.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * �v�����^�[���X�g(PRINTER_LIST)�ƑΉ������N���X�B
 */
public class USER_ID_CONVERSION_DB {
	/**
	 * �O�����[�U�h�c����DRASAP���[�U�h�c�����߂�B
	 * @param user_id_col	���[�UID�ϊ��e�[�u���̃J������
	 * @param ext_user_id	�O�����[�U�h�c
	 * @param conn
	 * @return				DRASAP���[�U�h�c
	 * @throws Exception
	 */
	public static String getDrasapUserId(String user_id_col, String ext_user_id, Connection conn)
			throws Exception {
		String drasapUserId = null;
		// �`�F�b�N
		if (user_id_col == null || user_id_col.length() == 0) {
			throw new UserException("user_id_col is null");
		}
		if (ext_user_id == null || ext_user_id.length() == 0) {
			throw new UserException("ext_user_id is null");
		}
		ext_user_id = ext_user_id.trim();
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// �v�����^ID�Ō�������
			StringBuilder sbSql1 = new StringBuilder("select user_id from USER_ID_CONVERSION");
			sbSql1.append(" where TRIM(" + user_id_col + ")='" + ext_user_id + "'");
			//
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			if (!rs1.next()) {
				throw new UserException(user_id_col + "." + ext_user_id + " undefined");
			}
			drasapUserId = rs1.getString("user_id");

			if (drasapUserId == null || drasapUserId.length() == 0) {
				throw new UserException(user_id_col + "." + ext_user_id + " undefined");
			}

		} catch (Exception e) {
			throw e;

		} finally {
			// CLOSE����
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
		//
		return drasapUserId;
	}

}
