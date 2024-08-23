/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitRecipientDB.java
 * Name         : A-PLOT�o�} �o�}�w�����[�z�z�惌�x��] �N���X
 * Description  : A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�z�z�惌�x��]���Ǘ�����N���X.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

/**
 * A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�z�z�惌�x��]���Ǘ�����N���X.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitRecipientDB extends AbstractAPlotSchemaBase {

	/**
	 * �R���X�g���N�^�[.
	 * @param schema �Ή��X�L�[�}��.
	 */
	public APlotSubmitRecipientDB(String schema) {
		super(schema);
	}


	/**
	 * �{�f�[�^�}��SQL��Ԃ�.
	 * @return SQL��.
	 */
	public String insertSql() {
		String[][] ATTRS = {
				{ "JOB_ID", "char" },
				{ "RECIPIENT_ID", "char" },
				{ "RECIPIENT_SUBID", "char" },
				{ "RECIPIENT_NAME", "varchar" },
				{ "SEQUENCE_NO", "numeric" },
				{ "PRINTER_ID", "char" },
				{ "NOTE", "varchar" },
				{ "DUE_MODE", "varchar" },
				{ "DUE_YEAR", "varchar" },
				{ "DUE_MONTH", "varchar" },
				{ "DUE_DAY", "varchar" },
				{ "DUE_HOUR", "varchar" },
				{ "DUE_MINUTES", "varchar" },
				{ "OUTPUT_STATUS", "varchar" },
				{ "OUTPUT_DETAIL_STATUS", "varchar" },
				{ "OUTPUT_DATE", "timestamp" },
				{ "LAST_JOBTICKET_ID", "varchar" },
				{ "OUTPUT_ORDER", "char" },
				{ "DATA_FOLDER_PATH", "varchar" },
				{ "OUTPUT_WARN_MSG", "varchar" },
				{ "DISTRIBUTIONED", "char" },
		};
		StringBuilder insertAttrs = new StringBuilder("");
		StringBuilder insertValues = new StringBuilder("");
		for ( String[] attrName : ATTRS ) {
			if ( insertAttrs.length() > 0 ) {
				// ���̑����ȍ~�̓J���}.
				insertAttrs.append(",");
				insertValues.append(",");
			}

			if ( this.containsKey(attrName[0]) && this.get(attrName[0]) != null ) {
				// ����.
				insertAttrs.append(attrName[0]);
				// �l.
				insertValues.append(quart(get(attrName[0]).toString(), attrName[1]));
			} else {
				// ����.
				insertAttrs.append(attrName[0]);
				// NULL��ݒ�.
				insertValues.append("NULL");
			}
		}
		return String.format("INSERT INTO %s.SUBMIT_RECIPIENT ( %s ) VALUES ( %s )", this.getSchemaName(), insertAttrs.toString(), insertValues.toString());
	}

	/**
	 * �X�V���b�N��������Z���N�g����Ԃ�.
	 * @param jobId �W���uId.
	 * @return SQL��.
	 */
	public String forUpdateSql(String jobId) {
		return String.format(
				"SELECT * FROM %s.SUBMIT_RECIPIENT WHERE JOB_ID = '%s' for update", this.getSchemaName(), jobId);
	}

	/**
	 * �X�e�[�^�X�X�V�pSQL��Ԃ�.
	 * @param jobId �W���uId.
	 * @param status �X�e�[�^�X������
	 * @return SQL��.
	 */
	public String updateStatusSql(String jobId, String recipientId, String status) {
		return String.format(
				"UPDATE %s.SUBMIT_RECIPIENT SET OUTPUT_STATUS = '%s' WHERE JOB_ID = '%s' AND RECIPIENT_ID = '%s'", this.getSchemaName(), status, jobId, recipientId);
	}



}
