/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : AbstractAPlotSchemaBase.java
 * Name         : A-PLOT�o�}�f�[�^�x�[�X�e�[�u���x�[�X���ۃN���X
 * Description  :
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.util.HashMap;

/**
 * A-PLOT�o�}�f�[�^�x�[�X�e�[�u���x�[�X���ۃN���X.
 * �{�N���X�͒��ۃN���X�Ȃ̂ŁA���ڎg�p���邱�Ƃ͂ł��Ȃ�.
 * @author hideki_sugiyama
 * @
 */
@SuppressWarnings("serial")
public abstract class  AbstractAPlotSchemaBase extends HashMap<String, Object> {

	/** �X�L�[�}���̐ړ���. */
	public static final String SCHEMA_PREFIX = "oj";


	/** �X�L�[�}��. */
	private String schemaName = null;

	/**
	 * �R���X�g���N�^ .
	 * @param schema �X�L�[�}��.
	 */
	public AbstractAPlotSchemaBase(String schema) {
		this.schemaName = schema;
	}


	/**
	 * �X�L�[�}���擾.
	 * @return schemaName
	 */
	public String getSchemaName() {
		return this.schemaName;
	}

	/**
	 * insert����l���V���O���N�H�[�g�Ŋ���
	 * @param val �l
	 * @param type �l�̌^
	 * @return �ϊ������l
	 */
	protected String quart(String val, String type) {
		String retVal = val;
		if ( "char".equalsIgnoreCase(type) ||
				"varchar".equalsIgnoreCase(type) ||
				"timestamp".equalsIgnoreCase(type) ) {
			// ����
			retVal = "'" + val + "'";
		}
		return retVal;
	}


	/**
	 * �X�L�[�}������X�L�[�}�ԍ��ɕϊ�����.
	 * @param schemaName ��.
	 * @return �X�L�[�}�ԍ�.
	 */
	public int getSchemaNo() {
		// �X�L�[�}�ԍ��ݒ�.
		String str = this.schemaName.toLowerCase();
		String no = str.replace(SCHEMA_PREFIX, "");
		if ( !"".equals(no) ) {
			return Integer.parseInt(no);
		}
		return 0;
	}

	@Override
	public Object put(String key, Object value) {
		// ������̏ꍇ�̓g��������.
		if ( value instanceof String ) {
			super.put(key, value.toString().trim());
		}
		// ����ȊO�͂��̂܂ܓ����.
		return super.put(key, value);
	}

}
