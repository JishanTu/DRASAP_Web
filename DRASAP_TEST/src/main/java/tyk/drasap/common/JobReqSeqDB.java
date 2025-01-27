package tyk.drasap.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���}�ɍ�ƈ˗��ł̃W���uID���擾����N���X
 */
public class JobReqSeqDB {
	/**
	 * �W���u���w�肵�āA�W���uID���擾����B
	 * �R�l�N�V�����ɂ��ẮA���̃��\�b�h�̒��ŕ������x���̐ݒ�Ȃǂ��s���B���̂��ߐV�����擾�����΂���̃R�l�N�V������n�����ƁB
	 * @param job A=�}�ʓo�^�˗� B=�}�ʏo�͎w�� C=���}�ؗp�˗� D=�}�ʈȊO�ĕt
	 * @param conn ���̃��\�b�h�̒��ŕ������x���̐ݒ�Ȃǂ��s���B���̂��ߐV�����擾�����΂���̃R�l�N�V������n�����ƁB
	 * @return �W���uID��Ԃ��B��) A040108-001
	 * @throws Exception
	 */
	public static String getJobId(String job, Connection conn) throws Exception {
		// job�̊m�F�E�E�EA,B,C,D�̂݋���
		StringBuilder jobId = new StringBuilder();//
		String seqColumnName = null;// �擾�Ώۂ̃J������
		if ("A".equals(job)) {
			seqColumnName = "SEQ_A";
			jobId.append("A");
		} else if ("B".equals(job)) {
			seqColumnName = "SEQ_B";
			jobId.append("B");
		} else if ("C".equals(job)) {
			seqColumnName = "SEQ_C";
			jobId.append("C");
		} else if ("D".equals(job)) {
			seqColumnName = "SEQ_D";
			jobId.append("D");
		} else {
			throw new IllegalArgumentException("JOB�̎w���A,B,C,D�̉��ꂩ�̂݉\�ł��B");
		}
		// �{�����t�̎擾
		String ymd = new SimpleDateFormat("yyMMdd").format(new Date());// YYMMDD�`���̖{�����t
		jobId.append(ymd);
		jobId.append('-');
		//
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			// �g�����U�N�V������ݒ肷��
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			// for update�Ō���
			String strSql1 = "select * from JOB_REQUEST_SEQUENCE" +
					" where SEQ_DATE = '" + ymd + "'" +
					" for update";
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(strSql1);
			if (rs1.next()) {
				// ���łɓ������t�L�[�����݂���ꍇ
				String id = rs1.getString(seqColumnName);
				String newId = null;
				if (id == null) {
					// ����JOB�ŃV�[�P���X�����擾�̏ꍇ
					newId = "001";
				} else {
					// ����JOB�ŃV�[�P���X���擾�ς݂̏ꍇ
					int intId = Integer.parseInt(id);
					intId++;// �C���N�������g����
					if (intId > 999) {
						throw new RuntimeException("999�܂Ŏg�p���Ă��܂��B����ȏ�̔ԍ����擾�ł��܂���B");
					}
					newId = String.valueOf(intId);
					while (newId.length() < 3) {
						newId = "0" + newId;
					}
				}
				jobId.append(newId);
				// �X�V����
				String strSql2 = "update JOB_REQUEST_SEQUENCE set " +
						seqColumnName + "='" + newId + "'" +
						" where SEQ_DATE = '" + ymd + "'";
				stmt1.executeUpdate(strSql2);
			} else {
				// �������t�L�[�����݂��Ȃ��ꍇ
				jobId.append("001");
				String strSql3 = "insert into JOB_REQUEST_SEQUENCE(SEQ_DATE, " + seqColumnName + ")" +
						" values('" + ymd + "', '001')";
				stmt1.executeUpdate(strSql3);
			}
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
			try {
				rs1.close();
			} catch (Exception e) {
			}
			try {
				stmt1.close();
			} catch (Exception e) {
			}
		}
		return jobId.toString();
	}

	/**
	 * �e�X�g�̂��߂̃��\�b�h
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("JobReqSeqDB�̃e�X�g");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@orasrv:1521:SMP", "DRASAP", "DRASAP");
			System.out.println(JobReqSeqDB.getJobId("C", conn));
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("JobReqSeqDB�̃e�X�g���I��");
		System.exit(0);
	}

}
