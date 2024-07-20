package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ACL�A�b�v���[�h�f�[�^�e�[�u���ƑΉ�����DB�N���X�B
 *
 * @author 2013/07/09 yamagishi
 */
public class AclUploadDB {
	/**
	 * ���[�U�[�̎���ACL��񃊃X�g�̌�����Ԃ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param conn
	 * @return count ACL�A�b�v���[�h�f�[�^���X�g����
	 * @throws Exception
	 */
	public static long getAclUploadCount(String aclUpdateNo, Connection conn) throws Exception {
		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(RECORD_NO) as COUNT from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// ACL�A�b�v���[�h�f�[�^�����擾
				count = rs.getLong("COUNT");
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
		return count;
	}

	/**
	 * ���[�U�[�̎���ACL�����A���X�g�ŕԂ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param conn
	 * @return aclUploadList ACL�A�b�v���[�h�f�[�^���X�g
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getAclUploadList(String aclUpdateNo, Connection conn) throws Exception {
		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select * from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"order by RECORD_NO asc";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			while (rs.next()) {
				// ACL�A�b�v���[�h�f�[�^
				aclUpload = new AclUpload();
				aclUpload.setAclUpdateNo(rs.getString("ACL_UPDATE_NO"));
				aclUpload.setUserId(rs.getString("USER_ID"));
				aclUpload.setUserName(rs.getString("USER_NAME"));
				aclUpload.setRecordNo(rs.getString("RECORD_NO"));
				aclUpload.setMachineJp(rs.getString("MACHINE_JP"));
				aclUpload.setMachineNo(rs.getString("MACHINE_NO"));
				aclUpload.setDrwgNo(rs.getString("DRWG_NO"));
				aclUpload.setMachineCode(rs.getString("MACHINE_CODE"));
				aclUpload.setDetailNo(rs.getString("DETAIL_NO"));
				aclUpload.setPages(rs.getString("PAGES"));
				aclUpload.setItemNo(rs.getString("ITEM_NO"));
				aclUpload.setItemNoShort(rs.getString("ITEM_NO_SHORT"));
				aclUpload.setGrpCode(rs.getString("GRP_CODE"));
				aclUpload.setCorrespondingFlag(rs.getString("CORRESPONDING_FLAG"));
				aclUpload.setConfidentialFlag(rs.getString("CONFIDENTIAL_FLAG"));
				aclUpload.setPreUpdateAcl(rs.getString("PRE_UPDATE_ACL"));
				aclUpload.setPreUpdateAcl(rs.getString("PRE_UPDATE_ACL_NAME"));
				aclUpload.setPostUpdateAcl(rs.getString("POST_UPDATE_ACL"));
				aclUpload.setPostUpdateAcl(rs.getString("POST_UPDATE_ACL_NAME"));
				aclUpload.setItemName(rs.getString("ITEM_NAME"));
				aclUpload.setMessage(rs.getString("MESSAGE"));
				aclUpload.setAclUpdate(rs.getDate("ACL_UPDATE"));

				// ���X�g�ǉ�
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * ���[�U�[�̎���ACL�����A�\���p���X�g�ŕԂ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param drasapInfo �Ǘ��Ґݒ���
	 * @param conn
	 * @return aclUploadList ACL�A�b�v���[�h�f�[�^���X�g
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getAclUploadDispList(String aclUpdateNo, DrasapInfo drasapInfo,
			Connection conn) throws Exception {

		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ITEM_NO, ITEM_NAME, CORRESPONDING_FLAG, CONFIDENTIAL_FLAG, GRP_CODE, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL_NAME, MESSAGE " +
					"from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"order by RECORD_NO asc";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			while (rs.next()) {
				// ACL�A�b�v���[�h�f�[�^
				aclUpload = new AclUpload();
				aclUpload.setItemNo(rs.getString("ITEM_NO"));
				aclUpload.setItemName(rs.getString("ITEM_NAME"));
				aclUpload.setCorrespondingFlag(rs.getString("CORRESPONDING_FLAG"));
				if ("1".equals(aclUpload.getCorrespondingFlag())) {
					// �Y��
					aclUpload.setCorrespondingValue(drasapInfo.getCorrespondingValue());
				}
				aclUpload.setConfidentialFlag(rs.getString("CONFIDENTIAL_FLAG"));
				if ("2".equals(aclUpload.getConfidentialFlag()) && drasapInfo.getConfidentialValue() != null) {
					// ��
					aclUpload.setConfidentialValue(drasapInfo.getConfidentialValue());
				} else if ("3".equals(aclUpload.getConfidentialFlag())) {
					// �ɔ�
					aclUpload.setConfidentialValue(drasapInfo.getStrictlyConfidentialValue());
				}
				aclUpload.setGrpCode(rs.getString("GRP_CODE"));
				aclUpload.setPreUpdateAclName(rs.getString("PRE_UPDATE_ACL_NAME"));
				aclUpload.setPostUpdateAclName(rs.getString("POST_UPDATE_ACL_NAME"));
				aclUpload.setMessage(rs.getString("MESSAGE"));

				// ���X�g�ǉ�
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * �A�b�v���[�h�t�@�C�����AACL�A�b�v���[�h�f�[�^�e�[�u���ɓo�^����B
	 * @param aclUpload
	 * @param conn
	 * @return resultCount �o�^����
	 * @throws Exception
	 */
	public static long insertAclUpload(AclUpload aclUpload, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "insert into ACL_UPLOAD_TABLE (" +
					"ACL_UPDATE_NO, USER_ID, USER_NAME, RECORD_NO, MACHINE_JP, " +
					"MACHINE_NO, DRWG_NO, MACHINE_CODE, DETAIL_NO, PAGES, ITEM_NO, ITEM_NO_SHORT, " +
					"CORRESPONDING_FLAG, CONFIDENTIAL_FLAG, GRP_CODE, PRE_UPDATE_ACL, " +
					"PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, ITEM_NAME, MESSAGE, ACL_UPDATE) " +
					"values (?, ?, ?, lpad(?,8,'0'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)";
			pstmt = conn.prepareStatement(sql);

			int paramIndex = 0;
			pstmt.setString(++paramIndex, aclUpload.getAclUpdateNo()); // �Ǘ�NO
			pstmt.setString(++paramIndex, aclUpload.getUserId()); // ���[�UID
			pstmt.setString(++paramIndex, aclUpload.getUserName()); // ����
			pstmt.setString(++paramIndex, aclUpload.getRecordNo()); // ���R�[�h�ԍ�
			pstmt.setString(++paramIndex, aclUpload.getMachineJp()); // ���u
			pstmt.setString(++paramIndex, aclUpload.getMachineNo()); // ���uNO
			pstmt.setString(++paramIndex, aclUpload.getDrwgNo()); // ��z�}��
			pstmt.setString(++paramIndex, aclUpload.getMachineCode()); // ���u�R�[�h
			pstmt.setString(++paramIndex, aclUpload.getDetailNo()); // ���הԍ�
			pstmt.setString(++paramIndex, aclUpload.getPages()); // ��
			pstmt.setString(++paramIndex, aclUpload.getItemNo()); // �i��
			pstmt.setString(++paramIndex, aclUpload.getItemNoShort()); // �i�ԁi�󔒁A�n�C�t���u-�v�����������p�啶���j
			pstmt.setString(++paramIndex, aclUpload.getCorrespondingFlag()); // �Y���}�敪
			pstmt.setString(++paramIndex, aclUpload.getConfidentialFlag()); // �@���Ǘ��}�敪
			pstmt.setString(++paramIndex, aclUpload.getGrpCode()); // �O���[�v
			pstmt.setString(++paramIndex, aclUpload.getPreUpdateAcl()); // �ύX�O�A�N�Z�X���x��
			pstmt.setString(++paramIndex, aclUpload.getPreUpdateAclName()); // �ύX�O�A�N�Z�X���x����
			pstmt.setString(++paramIndex, aclUpload.getPostUpdateAcl()); // �ύX��A�N�Z�X���x��
			pstmt.setString(++paramIndex, aclUpload.getPostUpdateAclName()); // �ύX��A�N�Z�X���x����
			pstmt.setString(++paramIndex, aclUpload.getItemName()); // �i���i�K�i�^���j
			pstmt.setString(++paramIndex, aclUpload.getMessage()); // ���b�Z�[�W
			resultCount += pstmt.executeUpdate();

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
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * ACL�A�b�v���[�h�f�[�^�e�[�u���̃f�[�^���폜����B
	 * @param aclUpdateType (A:�A�N�Z�X���x���ꊇ�X�V, B:�A�N�Z�X���x���ύX)
	 * @param user
	 * @param conn
	 * @return count �폜����
	 * @throws Exception
	 */
	public static long deleteAclUpload(String aclUpdateType, User user, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO < (? || to_char(sysdate, 'YYMMDD') || '000') " +
					"and USER_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateType);
			pstmt.setString(2, user.getId());
			resultCount = pstmt.executeUpdate();

			// �R�~�b�g
			conn.commit();

		} catch (Exception e) {
			// ���[���o�b�N
			conn.rollback();
			throw e;
		} finally {
			// CLOSE����
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * ACL�A�b�v���[�h�f�[�^�e�[�u���̃f�[�^���폜����B
	 * @param aclUpdateNo
	 * @param conn
	 * @return count �폜����
	 * @throws Exception
	 */
	public static long deleteAclUpload(String aclUpdateNo, Connection conn) throws Exception {
		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from ACL_UPLOAD_TABLE where ACL_UPDATE_NO = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			resultCount = pstmt.executeUpdate();

			// �R�~�b�g
			conn.commit();

		} catch (Exception e) {
			// ���[���o�b�N
			conn.rollback();
			throw e;
		} finally {
			// CLOSE����
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * �������X�V�p�ɁA�d����������ACL�A�b�v���[�h�f�[�^�̃��X�g�ŕԂ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param conn
	 * @return aclUploadList ACL�A�b�v���[�h�f�[�^���X�g
	 * @throws Exception
	 */
	public static ArrayList<AclUpload> getDistinctAclUploadList(String aclUpdateNo, Connection conn)
			throws Exception {

		ArrayList<AclUpload> aclUploadList = new ArrayList<AclUpload>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ACL_UPDATE_NO, USER_ID, USER_NAME, ITEM_NO_SHORT, PRE_UPDATE_ACL, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, MESSAGE " +
					"from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? " +
					"group by ACL_UPDATE_NO, USER_ID, USER_NAME, ITEM_NO_SHORT,PRE_UPDATE_ACL, PRE_UPDATE_ACL_NAME, POST_UPDATE_ACL, POST_UPDATE_ACL_NAME, MESSAGE " +
					"order by min(RECORD_NO)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();

			AclUpload aclUpload = null;
			String value = null;
			String itemNoShort = null;
			while (rs.next()) {
				// ACL�A�b�v���[�h�f�[�^
				aclUpload = new AclUpload();
				aclUpload.setAclUpdateNo(rs.getString("ACL_UPDATE_NO"));
				aclUpload.setUserId(rs.getString("USER_ID"));
				aclUpload.setUserName(rs.getString("USER_NAME"));

				// �������e�[�u���X�V�p�Ƀt�H�[�}�b�g
				value = rs.getString("ITEM_NO_SHORT");
				if (value != null && value.length() > 0) {
					aclUpload.setItemNo(value);
					// trim�����B�n�C�t���u-�v�������B
					itemNoShort = StringCheck.trimWsp(value).replace("-", "");
					// ���p�啶���ɕϊ�����
					itemNoShort = StringCheck.changeDbToSbAscii(itemNoShort).toUpperCase();
					aclUpload.setItemNoShort(itemNoShort);
				}
				if ((value = rs.getString("PRE_UPDATE_ACL")) != null && value.length() > 0) {
					aclUpload.setPreUpdateAcl(value);
				}
				if ((value = rs.getString("PRE_UPDATE_ACL_NAME")) != null && value.length() > 0) {
					aclUpload.setPreUpdateAclName(value);
				}
				if ((value = rs.getString("POST_UPDATE_ACL")) != null && value.length() > 0) {
					aclUpload.setPostUpdateAcl(value);
				}
				if ((value = rs.getString("POST_UPDATE_ACL_NAME")) != null && value.length() > 0) {
					aclUpload.setPostUpdateAclName(value);
				}
				if ((value = rs.getString("MESSAGE")) != null && value.length() > 0) {
					aclUpload.setMessage(value);
				}
				// ���X�g�ǉ�
				aclUploadList.add(aclUpload);
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
		return aclUploadList;
	}

	/**
	 * ACL�A�b�v���[�h�f�[�^�e�[�u���̃��b�Z�[�W���X�V����B
	 * @param aclUpdateNo
	 * @param message
	 * @param conn
	 * @param itemNosShorts[]
	 * @return resultCount �X�V����
	 * @throws Exception
	 */
	public static long updateMessage(String aclUpdateNo, String message, Connection conn, String... itemNoShorts)
			throws Exception {

		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "update ACL_UPLOAD_TABLE set MESSAGE = ? " +
					"where ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and MESSAGE is null";
			pstmt = conn.prepareStatement(sql);
			for (String itemNoShort : itemNoShorts) {
				pstmt.setString(1, message);
				pstmt.setString(2, aclUpdateNo);
				pstmt.setString(3, itemNoShort);
				resultCount = pstmt.executeUpdate();

				// �R�~�b�g
				conn.commit();
			}
		} catch (Exception e) {
			// ���[���o�b�N
			conn.rollback();
			throw e;
		} finally {
			// CLOSE����
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}

	/**
	 * �i�Ԃ���ѕύX�O��̃A�N�Z�X���x������v����i�Ԃ̌�����Ԃ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param itemNoShort
	 * @param preUpdateAcl
	 * @param postUpdateAcl
	 * @param conn
	 * @return count �����ƈ�v����ACL�A�b�v���[�h�f�[�^�e�[�u������
	 * @throws Exception
	 */
	public static long getItemNoAclCount(String aclUpdateNo, String itemNoShort, String preUpdateAcl, String postUpdateAcl,
			Connection conn) throws Exception {

		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(ITEM_NO) as COUNT from ACL_UPLOAD_TABLE " +
					"where ACL_UPDATE_NO = ? and ITEM_NO_SHORT = ? and PRE_UPDATE_ACL = ? and POST_UPDATE_ACL = ? and MESSAGE is null";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			pstmt.setString(2, itemNoShort);
			pstmt.setString(3, preUpdateAcl);
			pstmt.setString(4, postUpdateAcl);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// ACL�A�b�v���[�h�f�[�^�����擾
				count = rs.getLong("COUNT");
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
		return count;
	}

	/**
	 * ACL�A�b�v���[�h�f�[�^�e�[�u����1��2�i�ԏ���Map�̃��X�g�ŕԂ��B
	 * @param aclUpdateNo �Ǘ�NO
	 * @param conn
	 * @return <Map>List 1��2�i�ԏ��̃��X�g
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getDrwgNoMapList(String aclUpdateNo, Connection conn)
			throws Exception {

		ArrayList<HashMap<String, String>> drwgNoMapList = new ArrayList<HashMap<String, String>>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		try {
			String sql = "select org.ITEM_NO_SHORT    as ITEM_NO_SHORT, " +
					"twin.ITEM_NO_SHORT   as TWIN_DRWG_NO, " +
					"org.POST_UPDATE_ACL  as POST_UPDATE_ACL, " +
					"twin.POST_UPDATE_ACL as TWIN_POST_UPDATE_ACL, " +
					"org.MESSAGE          as MESSAGE, " +
					"id1.DRWG_NO          as ID1_DRWG_NO, " +
					"id1.TWIN_DRWG_NO     as ID1_TWIN_DRWG_NO, " +
					"id1.ACL_ID           as ID1_PRE_UPDATE_ACL, " +
					"id2.DRWG_NO          as ID2_DRWG_NO, " +
					"id2.TWIN_DRWG_NO     as ID2_TWIN_DRWG_NO, " +
					"id2.ACL_ID           as ID2_PRE_UPDATE_ACL " +
					"from ACL_UPLOAD_TABLE org " +
					"left outer join INDEX_DB id1 on id1.DRWG_NO = org.ITEM_NO_SHORT and id1.TWIN_DRWG_NO is not null " + // �A�b�v���[�h�f�[�^�D�}�ԂŌ���
					"left outer join INDEX_DB id2 on id2.TWIN_DRWG_NO = org.ITEM_NO_SHORT " + // �A�b�v���[�h�f�[�^�D1��2�i�Ԑ}�ԂŌ���
					"left outer join ACL_UPLOAD_TABLE twin on twin.ACL_UPDATE_NO = org.ACL_UPDATE_NO and twin.ITEM_NO_SHORT = nvl(id1.TWIN_DRWG_NO, ID2.DRWG_NO) " + // �A�b�v���[�h�f�[�^����1��2�i�Ԃ�����
					"where org.ACL_UPDATE_NO = ? " +
					"and (id1.DRWG_NO is not null or id1.TWIN_DRWG_NO is not null or " +
					" id2.DRWG_NO is not null or id2.TWIN_DRWG_NO is not null)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			rs = pstmt.executeQuery();
			metaData = rs.getMetaData();

			HashMap<String, String> orgTwinDrwgNoMap = null;
			String columnName = null;
			while (rs.next()) {
				orgTwinDrwgNoMap = new HashMap<String, String>();

				// ACL�A�b�v���[�h�f�[�^�i1��2�i�ԏ��j
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnLabel(i);
					orgTwinDrwgNoMap.put(columnName, rs.getString(columnName));
				}
				// ���X�g�ǉ�
				drwgNoMapList.add(orgTwinDrwgNoMap);
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
		return drwgNoMapList;
	}

	/**
	 * 1��2�i�ԑΉ��}�Ԃ��AACL�A�b�v���[�h�f�[�^�e�[�u���ɓo�^����B
	 * @param aclUpdateNo
	 * @param itemNoShort 1��2�i�Ԃ̌��}��
	 * @param conn
	 * @return �o�^����
	 * @throws Exception
	 */
	public static long insertSelectTwinDrwgNo(String aclUpdateNo, String itemNoShort,
			Connection conn) throws Exception {

		long resultCount = 0;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			String sql = "insert into ACL_UPLOAD_TABLE " +
					"select  aut.ACL_UPDATE_NO, " + // ACL_UPDATE_NO
					"aut.USER_ID, " + // USER_ID
					"aut.USER_NAME, " + // USER_NAME
					"lpad(tmp.NEW_RECORD_NO,8,'0'), " + // RECORD_NO
					"id.MACHINE_JP, " + // MACHINE_JP
					"id.MACHINE_NO, " + // MACHINE_NO
					"aut.DRWG_NO, " + // DRWG_NO
					"aut.MACHINE_CODE, " + // MACHINE_CODE
					"aut.DETAIL_NO, " + // DETAIL_NO
					"aut.PAGES, " + // PAGES
					"id.DRWG_NO, " + // ITEM_NO
					"id.DRWG_NO, " + // ITEM_NO_SHORT
					"aut.CORRESPONDING_FLAG, " + // CORRESPONDING_FLAG
					"aut.CONFIDENTIAL_FLAG, " + // CONFIDENTIAL_FLAG
					"aut.GRP_CODE, " + // GRP_CODE
					"id.ACL_ID, " + // PRE_UPDATE_ACL
					"alm.ACL_NAME , " + // PRE_UPDATE_ACL_NAME
					"aut.POST_UPDATE_ACL, " + // POST_UPDATE_ACL
					"aut.POST_UPDATE_ACL_NAME, " + // POST_UPDATE_ACL_NAME
					"id.MACHINE_JP, " + // ITEM_NAME
					"null, " + // MESSAGE
					"null " + // ACL_UPDATE
					"from INDEX_DB id " +
					"inner join ACL_UPLOAD_TABLE aut on aut.ITEM_NO_SHORT = id.TWIN_DRWG_NO " +
					"left outer join ACCESS_LEVEL_MASTER alm on alm.ACL_ID = id.ACL_ID, " +
					"(select to_number(max(RECORD_NO))+1 as NEW_RECORD_NO from ACL_UPLOAD_TABLE where ACL_UPDATE_NO = ?) tmp " +
					"where aut.ACL_UPDATE_NO = ? and aut.ITEM_NO_SHORT = ? and rownum <= 1 " +
					"and not exists (" +
					"select * from ACL_UPLOAD_TABLE ine where ine.ACL_UPDATE_NO = aut.ACL_UPDATE_NO and ine.ITEM_NO_SHORT = id.DRWG_NO" +
					")";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aclUpdateNo);
			pstmt.setString(2, aclUpdateNo);
			pstmt.setString(3, itemNoShort);
			resultCount = pstmt.executeUpdate();

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
				pstmt.close();
			} catch (Exception e) {
			}
		}
		return resultCount;
	}
}
