/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : AbstractAPlotSchemaBase.java
 * Name         : A-PLOT�o�} ID�̔ԃN���X.
 * Description  : A-PLOT�o�}��JOB ID�A�h�L�������gID�̍̔Ԃ��s���N���X.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * A-PLOT�v�����^�o�͗pID�̔ԃN���X.
 * �{�N���X�̓X�^�e�B�b�N�g�p��O��Ƃ��Ă���.
 * �X�L�[�}���̃e�[�u�� OJ-ID�A�ԊǗ�[OJ_SEQUENCE_X]���g�p���ĘA�Ԃ��Ǘ�����.
 *
 * �y�g�p���@�z
 *
 *    int seqNo = APlotOJSequenceXDB.getNewSeq(
 *                  conn,   -----  �f�[�^�x�[�X�R�l�N�V�������w��.
 *                  "OJ1",  -----  �X�L�[�}�����w��.
 *                  "R",    -----  ID��ʁi�啪�ށj���w��. JTEKT�ł́uR�v�i�Œ�j�B
 *                  "B",    -----  ID��ʁi�����ށj���w��. JTEKT�ł̓v�����^�V���[�gID.
 *                  "J",    -----  ID��ʁi�����ށj���w��. �W���u�̏ꍇ�́uJ�v�A�h�L�������g�̏ꍇ�́uD�v�B
 *                  "1802"  -----  ID���Ԏ��̃V�X�e�������̔N���iYYMM�`���j.
 *                  );
 *    ���̔ԃ��\�b�h���Ńg�����U�N�V�����͕���.���ԊǗ��͂��Ȃ�.
 *
 * @author hideki_sugiyama
 *
 */
public class APlotOJSequenceXDB {

	/** �W���uID�擾����ID��ʁi�啪�ށj�̕���.  */
	public static final String JOB_ID_KIND1 = "R";
	/** �W���uID�擾����ID��ʁi�����ށj�W���uID.  */
	public static final String JOB_ID_KIND3_JOB = "J";
	/** �W���uID�擾����ID��ʁi�����ށj�h�L�������g.  */
	public static final String JOB_ID_KIND3_DOC = "D";

	/** Logger�ilog4j�j */
	private static Logger category = Logger.getLogger(APlotOJSequenceXDB.class.getName());

	/** �R���X�g���N�^. */
	private APlotOJSequenceXDB() {
	}

	/**
	 * OJ-ID�A�ԊǗ��e�[�u��(OJ_SEQUENCE_X)����JOBID�̘A�Ԃ��擾�i���b�N�����˂�j.
	 * @param schemaName �X�L�[�}��.
	 * @param kd1 ID��ʁi�啪�ށj���w��. JTEKT�ł́uR�v�i�Œ�j�B
	 * @param kd2 ID��ʁi�����ށj���w��. JTEKT�ł̓v�����^�V���[�gID.
	 * @param kd3 ID��ʁi�����ށj���w��. �W���u�̏ꍇ�́uJ�v�A�h�L�������g�̏ꍇ�́uD�v�B
	 * @param yymm ID���Ԏ��̃V�X�e�������̔N���iYYMM�`���j.
	 * @return SQL�������Ԃ�.
	 */
	private static String createSQL_selectSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm) {

		// �A�Ԃ���������SQL��Ԃ��ifor update).
		return String.format(
				"SELECT SEQVAL FROM %s.OJ_SEQUENCE_X" +
						" WHERE ID_KIND1 = '%s' AND ID_KIND2 = '%s' AND ID_KIND3 = '%s' AND YYMM = '%s' for update",
				schemaName, kd1, kd2, kd3, yymm);
	}

	/**
	 * OJ-ID�A�ԊǗ��e�[�u��(OJ_SEQUENCE_X)�̘A�ԃ��R�[�h���쐬(�����̔Ԏ�).
	 * @param schemaName �X�L�[�}��.
	 * @param kd1 ID��ʁi�啪�ށj���w��. JTEKT�ł́uR�v�i�Œ�j�B
	 * @param kd2 ID��ʁi�����ށj���w��. JTEKT�ł̓v�����^�V���[�gID.
	 * @param kd3 ID��ʁi�����ށj���w��. �W���u�̏ꍇ�́uJ�v�A�h�L�������g�̏ꍇ�́uD�v�B
	 * @param yymm ID���Ԏ��̃V�X�e�������̔N���iYYMM�`���j.
	 * @param seqNo �����ԍ�.
	 * @return SQL�������Ԃ�.
	 */
	private static String createSQL_insertSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm, int seqNo) {

		// �A�Ԃ��쐬����SQL��Ԃ�.
		return String.format(
				"INSERT INTO %s.OJ_SEQUENCE_X(ID_KIND1, ID_KIND2, ID_KIND3, YYMM, SEQVAL) values (" +
						" '%s', '%s', '%s', '%s', %d)",
				schemaName, kd1, kd2, kd3, yymm, seqNo);
	}

	/**
	 * OJ-ID�A�ԊǗ��e�[�u��(OJ_SEQUENCE_X)�̘A�Ԃ��X�V.
	 * @param schemaName �X�L�[�}��.
	 * @param kd1 ID��ʁi�啪�ށj���w��. JTEKT�ł́uR�v�i�Œ�j�B
	 * @param kd2 ID��ʁi�����ށj���w��. JTEKT�ł̓v�����^�V���[�gID.
	 * @param kd3 ID��ʁi�����ށj���w��. �W���u�̏ꍇ�́uJ�v�A�h�L�������g�̏ꍇ�́uD�v�B
	 * @param yymm ID���Ԏ��̃V�X�e�������̔N���iYYMM�`���j.
	 * @param seqNo ���̔ԍ�.
	 * @return SQL�������Ԃ�.
	 */
	private static String createSQL_updateSeqNo(String schemaName, String kd1, String kd2, String kd3, String yymm, int seqNo) {

		// �A�Ԃ��X�V����SQL��Ԃ�.
		return String.format(
				"UPDATE %s.OJ_SEQUENCE_X SET SEQVAL = %d " +
						" WHERE ID_KIND1 = '%s' AND ID_KIND2 = '%s' AND ID_KIND3 = '%s' AND YYMM = '%s'",
				schemaName, seqNo, kd1, kd2, kd3, yymm);
	}

	/**
	 * �W���uID�̊���U��.
	 * OJ-ID�A�ԊǗ�[OJ_SEQUENCE_X]�e�[�u���ō̔Ԃ��s��.
	 * �{���\�b�h���Ńg�����U�N�V���������.
	 *
	 * @param kd1 �啪��(�uR�v�Œ�).
	 * @param kd2 ������(�v�����^�̃V���[�gID).
	 * @param kd3 ������(�W���uID�̏ꍇ��[J]�A�h�L�������gID�̏ꍇ��[D]).
	 * @param yymm �N��(YYMM�`��).
	 * @return �V�����A��.
	 * @throws SQLException
	 */
	public static int getNewSeq(Connection conn, String schemaName, String kd1, String kd2, String kd3, String yymm) throws SQLException {

		int seqNo = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// �g�����U�N�V������ݒ肷��
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(createSQL_selectSeqNo(schemaName, kd1, kd2, kd3, yymm));
			if (rs.next()) {
				seqNo = rs.getInt("SEQVAL");
				// �C���N�������g.
				seqNo++;
			}
			if (seqNo <= 0) {
				// ���o�^�V�[�P���XNo�Ȃ̂ŏ����l��1�Ƃ���.
				seqNo = 1;
				// �f�[�^���쐬.
				stmt.executeUpdate(createSQL_insertSeqNo(schemaName, kd1, kd2, kd3, yymm, seqNo));
			} else {
				// �f�[�^���X�V.
				stmt.executeUpdate(createSQL_updateSeqNo(schemaName, kd1, kd2, kd3, yymm, seqNo));
			}
			// �R�~�b�g
			conn.commit();
		} catch (SQLException ex) {
			category.fatal("A-PLOT�o�}�̔Ԃ�SQL�G���[", ex);
			conn.rollback();
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return seqNo;
	}

}
