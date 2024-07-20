package tyk.drasap.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ACL�Ǘ�NO�̔ԃe�[�u���ƑΉ�����DB�N���X�B
 *
 * @author 2013/07/18 yamagishi
 */
public class AclUpdateNoSequenceDB {

	/**
	 * ACL�Ǘ�NO���̔Ԃ��ĕԂ��B�̔ԃ��W�b�N�Ŕr����������{�B
	 * @param aclUpdateType ACL�X�V��ʁiA: �A�N�Z�X���x���ꊇ�X�V, B: �A�N�Z�X���x���ύX�j
	 * @param conn
	 * @return aclUpdateNo �Ǘ�NO
	 */
	public static synchronized String getAclUpdateNo(String aclUpdateType, Connection conn) throws Exception {
		long count = 0;
		String aclSeqNoOld = null; // ���ݒl
		String aclUpdateDateOld = null; // ���ݒl
		String aclSeqNo = null;
		String aclUpdateDate = null;
		String aclUpdateNo = null;

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);

			// �N����(yymmdd)�擾
			aclUpdateDate = new SimpleDateFormat("yyMMdd").format(new Date());

			String sql1 = "select ACL_SEQ_NO, ACL_UPDATE_DATE from ACL_UPDATE_NO_SEQUENCE " +
					"where ACL_UPDATE_TYPE = ? " +
					"for update";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, aclUpdateType);
			rs = pstmt1.executeQuery();

			if (rs.next()) {
				// ���݂̃V�[�P���X�ԍ�
				aclSeqNoOld = rs.getString("ACL_SEQ_NO");
				// ���݂̔N����
				aclUpdateDateOld = rs.getString("ACL_UPDATE_DATE");
			} else {
				// ����̂ݎ��s
				String sql = "insert into ACL_UPDATE_NO_SEQUENCE " +
						"(ACL_UPDATE_TYPE, ACL_UPDATE_DATE, ACL_SEQ_NO) " +
						"values (?, ?, '000')";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, aclUpdateType);
				pstmt.setString(2, aclUpdateDate);
				// �����f�[�^���쐬
				count = pstmt.executeUpdate();
				if (count <= 0) {
					throw new Exception("�����f�[�^�쐬���s�B");
				}
			}

			// �V�[�P���X�ԍ��̔�
			if (aclUpdateDateOld != null && aclUpdateDate.equals(aclUpdateDateOld)) {

				// ���t�P�ʂŘA�ԁi�O�[������3���j
				aclSeqNo = String.format("%1$03d", Integer.parseInt(aclSeqNoOld) + 1);

				if (aclSeqNo.length() > 3) {
					// ����A3���𒴂��Ă��܂����ꍇ�A������
					aclSeqNo = "001";
				}
			} else {
				// ���t���قȂ�ׁA������
				aclSeqNo = "001";
			}

			String sql2 = "update ACL_UPDATE_NO_SEQUENCE " +
					"set ACL_UPDATE_DATE = ?, ACL_SEQ_NO = ? " +
					"where ACL_UPDATE_TYPE = ?";
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, aclUpdateDate);
			pstmt2.setString(2, aclSeqNo);
			pstmt2.setString(3, aclUpdateType);
			count = pstmt2.executeUpdate();
			// �R�~�b�g
			conn.commit();

			if (count > 0) {
				// �Ǘ�NO [A|B + yymmdd + 3���A��]
				aclUpdateNo = aclUpdateType + aclUpdateDate + aclSeqNo;
			}

		} catch (Exception e) {
			// ���[���o�b�N
			try { conn.rollback(); } catch (Exception e2) {}
			throw e;
		} finally {
			// CLOSE����
			try { rs.close(); } catch (Exception e) {}
			try { pstmt1.close(); } catch (Exception e) {}
			try { pstmt2.close(); } catch (Exception e) {}
		}
		return aclUpdateNo;
	}
}
