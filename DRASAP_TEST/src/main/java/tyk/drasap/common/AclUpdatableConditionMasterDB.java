package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * ACL�ύX�۔��f�}�X�^�[�ƑΉ�����DB�N���X�B
 *
 * @author 2013/07/22 yamagishi
 */
public class AclUpdatableConditionMasterDB {
	/**
	 * �����ƕύX�O��̃A�N�Z�X���x������v���錏����Ԃ��B
	 * @param preUpdateAcl
	 * @param postUpdateAcl
	 * @param conn
	 * @return count ACL�ύX�۔��f�}�X�^�[�̊Y������
	 * @throws Exception
	 */
	public static long getAclUpdatableConditionCount(String preUpdateAcl, String postUpdateAcl,
			Connection conn) throws Exception {
		long count = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(*) as COUNT from ACL_UPDATABLE_CONDITION_MASTER " +
					"where PRE_UPDATE_ACL = ? and POST_UPDATE_ACL = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, preUpdateAcl);
			pstmt.setString(2, postUpdateAcl);
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
}
