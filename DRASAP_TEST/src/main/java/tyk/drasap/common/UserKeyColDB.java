package tyk.drasap.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ���[�U�[�e�[�u���̃L�[�J�������擾
 * @author Y.eto
 * �쐬��: 2006/05/10
 */
public class UserKeyColDB {
	private ArrayList<UserKeyColInfo> keyColList = new ArrayList<UserKeyColInfo>();

	/**
	 * �擾�������[�U�[�����Auser�ɕt������B
	 * ��������� true��Ԃ��B
	 * @param user
	 * @param conn
	 * @return �Ȃ�
	 * @throws Exception
	 */
	public UserKeyColDB(User user, Connection conn) throws Exception {
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// ���[�U�[�O���[�vCD�Ō�������
			StringBuilder sbSql1 = new StringBuilder("select col.TABLE_NAME TABLE_NAME, col.COLUMN_NAME COLUMN_NAME ");
			sbSql1.append(" FROM ALL_CONS_COLUMNS col, ALL_CONSTRAINTS con");
			sbSql1.append(" WHERE col.Owner = UPPER('DRASAP') ");
			sbSql1.append(" AND col.CONSTRAINT_NAME = con.CONSTRAINT_NAME");
			sbSql1.append(" AND con.CONSTRAINT_TYPE = 'P'");
			sbSql1.append(" AND con.Owner = UPPER('DRASAP')");
			sbSql1.append(" ORDER BY col.TABLE_NAME, col.POSITION");
			//
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(sbSql1.toString());
			//
			String tableName = "";
			UserKeyColInfo userKeyColInfo = null;
			while (rs1.next()) {
				if (!tableName.equals(rs1.getString("TABLE_NAME"))) {
					userKeyColInfo = new UserKeyColInfo();
					userKeyColInfo.setTableName(rs1.getString("TABLE_NAME"));
					userKeyColInfo.addKeyCol(rs1.getString("COLUMN_NAME"));
					tableName = rs1.getString("TABLE_NAME");
					this.keyColList.add(userKeyColInfo);
				} else {
					userKeyColInfo.addKeyCol(rs1.getString("COLUMN_NAME"));
				}
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
	}

	/**
	 * @return
	 */
	public ArrayList<UserKeyColInfo> getKeyColList() {
		return this.keyColList;
	}

	/**
	 * @return
	 */
	public UserKeyColInfo getKeyCol(String tableName) {
		for (int i = 0; i < this.keyColList.size(); i++) {
			UserKeyColInfo userKeyColInfo = this.keyColList.get(i);
			if (userKeyColInfo.getTableName().equals(tableName)) {
				return userKeyColInfo;
			}
		}
		return new UserKeyColInfo();
	}

}
