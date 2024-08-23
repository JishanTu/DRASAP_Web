/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitJobDB.java
 * Name         : A-PLOT�o�} �o�}�w�����[�W���u���x��] �N���X
 * Description  : A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�W���u���x��]���Ǘ�����N���X.
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�W���u���x��]���Ǘ�����N���X.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitJobDB extends AbstractAPlotSchemaBase {

	/**
	 * �R���X�g���N�^�[.
	 * @param schema �Ή��X�L�[�}��.
	 * @param jobId �W���uID.
	 * @param jobName �W���u��.
	 * @param userId �o�}���[�U�[ID.
	 * @param userName �o�}���[�U�[��.
	 */
	public APlotSubmitJobDB(String schema, String jobId, String jobName, String userId, String userName) {
		super(schema);

		// ��{�f�[�^��ݒ�.
		this.put("JOB_ID", jobId);
		this.put("JOB_NAME", jobName);
		this.put("SUBMITTER_ID", userId);
		this.put("SUBMITTER_NAME", userName);
	}

	/**
	 * JOBID�擾
	 * @return JOBID
	 */
	public String getJobID() {
		return this.get("JOB_ID").toString();
	}

	/**
	 * �{�f�[�^�}��SQL��Ԃ�.
	 * @return SQL��.
	 */
	public String insertSql() {
		StringBuilder sb = new StringBuilder("");
		sb.append("INSERT INTO ").append(getSchemaName()).append(".SUBMIT_JOB (");
		sb.append("JOB_ID").append(", ");
		sb.append("JOB_NAME").append(", ");
		sb.append("SUBMITTER_ID").append(", ");
		sb.append("SUBMITTER_NAME").append(", ");
		sb.append("COVER_PAGE").append(", ");
		sb.append("PRIORITY").append(", ");
		sb.append("RECEIVE_QUEUE_ID").append(", ");
		sb.append("SUBMIT_DATE");
		sb.append(") VALUES (");
		sb.append("'").append(get("JOB_ID").toString()).append("',");
		sb.append("'").append(get("JOB_NAME").toString()).append("',");
		sb.append("'").append(get("SUBMITTER_ID").toString()).append("',");
		sb.append("'").append(get("SUBMITTER_NAME").toString()).append("',");
		sb.append("'").append(get("COVER_PAGE").toString()).append("',");
		sb.append("'").append(get("PRIORITY").toString()).append("',");
		sb.append("'").append(get("RECEIVE_QUEUE_ID").toString()).append("',");
		sb.append("SYSDATE");
		sb.append(")");
		return sb.toString();
	}


	/**
	 * JOBID���̔�.
	 * @param conn �f�[�^�x�[�X�R�l�N�^�[
	 * @param shortId �v�����^�V���[�gID
	 * @return JOBID��Ԃ�.
	 * @throws SQLException
	 */
	public static String getNewJobID(Connection conn, String schemaName, int schemaNo, String shortId) throws SQLException {

		// ���t���擾.
		String yymm = new SimpleDateFormat("yyMM").format(new Date());

		// �w��̃X�L�[�}��ID�A�ԊǗ�(OJ_SEQUENCE_X)����A�Ԃ��擾.
		int seqNo = APlotOJSequenceXDB.getNewSeq(
				conn,
				schemaName, // �X�L�[�}��.
				APlotOJSequenceXDB.JOB_ID_KIND1, // R�Œ�
				shortId, // �V���[�gID
				APlotOJSequenceXDB.JOB_ID_KIND3_JOB, //
				new SimpleDateFormat("yyMM").format(new Date()));

		// �uID��ʁi�啪�ށj + �X�L�[�}�ԍ� + ID��ʁi�����ށj + ID��ʁi�����ށj + �N���iYYMM�`���j + '-'�i�Œ�j + �A�ԁi00000000�`���j�v
		return String.format("%s%d%s%s%s-%07d", APlotOJSequenceXDB.JOB_ID_KIND1, schemaNo, shortId, APlotOJSequenceXDB.JOB_ID_KIND3_JOB, yymm, seqNo);
	}



}
