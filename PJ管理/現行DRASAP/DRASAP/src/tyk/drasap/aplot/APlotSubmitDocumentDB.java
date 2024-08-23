/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSubmitDocumentDB.java
 * Name         : A-PLOT�o�} �o�}�w�����[�h�L�������g�i�t�@�C���j] �N���X
 * Description  : A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�h�L�������g�i�t�@�C���j]���Ǘ�����N���X.
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
import java.util.HashMap;

import org.apache.log4j.Category;

import tyk.drasap.common.DrasapUtil;

/**
 * A-PLOT�p�� �o�}�������W���[���֏o�}�w�����[�h�L�������g�i�t�@�C���j]���Ǘ�����N���X.
 *
 * @author hideki_sugiyama
 *
 */
@SuppressWarnings("serial")
public class APlotSubmitDocumentDB extends AbstractAPlotSchemaBase {


	/** ����T�C�Y�̃X�P�[�����O���[�h�ϊ��p�}�b�v�e�[�u��. */
	static final private HashMap<String, String> zoomModeMap = new HashMap<String, String>() { {
		put("ORG","�T�C�Y�w��");
		put("A0","�T�C�Y�w��");
		put("A1","�T�C�Y�w��");
		put("A2","�T�C�Y�w��");
		put("A3","�T�C�Y�w��");
		put("A4","�T�C�Y�w��");
		//put("A0","�T�C�Y�w��");
		//put("A0L","�T�C�Y�w��");
		//put("A1L","�T�C�Y�w��");
		//put("A2L","�T�C�Y�w��");
		//put("A3L","�T�C�Y�w��");
		//put("A4L","�T�C�Y�w��");
		put("70.7%","�{���w��");
		put("50%","�{���w��");
		put("35.4%","�{���w��");
		put("25%","�{���w��");
	}};

	/** ����T�C�Y�̔{���ϊ��p�}�b�v�e�[�u��. */
	static final private HashMap<String, String> fixedZoomMap = new HashMap() { {
		put("ORG","���{");
		put("A0","A0");
		put("A1","A1");
		put("A2","A2");
		put("A3","A3");
		put("A4","A4");
		//put("A0","A0");
		//put("A0L","A0L");
		//put("A1L","A1L");
		//put("A2L","A2L");
		//put("A3L","A3L");
		//put("A4L","A4L");
	}};

	/** ����T�C�Y�̃p�[�Z���g�{���}�b�v�e�[�u��. */
	static final private HashMap<String, String> PercentageZoomMap = new HashMap() { {
		put("70.7%","70.7");
		put("50%","50");
		put("35.4%","35.4");
		put("25%","25");
	}};

	//�Ώې}�ʂ�A0L�̏ꍇ�A�ݒ�ύX�K�v�̉�ʎw��T�C�Y�̃e�[�u��
	static final private HashMap<String,String> targetPaperSizeForA0L = new HashMap(){ {
		put("A0", "A0");
		put("A1", "A1");
		put("A2", "A2");
		put("A3", "A3");
		put("A4", "A4");
	}};

	/**
	 * �R���X�g���N�^�[.
	 * @param schema �Ή��X�L�[�}��.
	 */
	public APlotSubmitDocumentDB(String schema) {
		super(schema);
	}


	/**
	 * ����T�C�Y����X�P�[�����O���[�h������ɕϊ�.
	 *
	 * �y�X�P�[�����O���[�h�z
	 *   ��ʂŎw�肳�ꂽ�T�C�Y�����L�̏ꍇ�́u�T�C�Y�w��v��Ԃ�.
	 *    �uORG�v�A�uA0�v�A�uA1�v�A�uA2�v�A�uA3�v�A�uA4�v�A�uA0L�v�A�uA1L�v�A�uA2L�v�A�uA3L�v�A�uA4L�v
	 *   ��ʂŎw�肳�ꂽ�T�C�Y�����L�̏ꍇ�́u�{���w��v��Ԃ�.
	 *    �u70.7%�v�A�u50%�v�A�u35.4%�v�A�u25%�v
	 * @param printSize ����T�C�Y.
	 * @return �X�P�[�����O���[�h.
	 */
	public static String toZoomMode(String printSize) {
		// �}�b�v�e�[�u���ɑΉ����镶�����Ԃ�.
		return zoomModeMap.containsKey(printSize) ? zoomModeMap.get(printSize) : "";
	}


	/**
	 * ����T�C�Y����Œ�{��������ɕϊ�.
	 * @param zoomMode �X�P�[�����O���[�h
	 * @param dwgSize �}�ʃT�C�Y
	 * @param printSize ����T�C�Y.
	 * @return �Œ�{��.�p�[�Z���g�{���������ꍇ�͋󕶎���Ԃ�.
	 */
	public static String toFixedZoom(String zoomMode, String dwgSize, String printSize) {

		String fixedMode = "";

		if( zoomMode.equals("�T�C�Y�w��")){
			// �}�b�v�e�[�u���ɑΉ����镶�����Ԃ�.
			fixedMode = fixedZoomMap.containsKey(printSize) ? fixedZoomMap.get(printSize) : "";

			//�w�肳�ꂽ�T�C�Y���A���T�C�Y���傫���ꍇ�́A���T�C�Y�i"A0", "A1","A2"�Ȃǁj���Z�b�g���A�������������ꍇ�́A�w�肳�ꂽ�}�ʃT�C�Y���Z�b�g�B
			if( printSize.equals("ORG") == false &&  DrasapUtil.compareDrwgSize(printSize, dwgSize) > 0 ){ //������ŗp���T�C�Y���r����ƁAA0 < A4, A1 < A4
				fixedMode = dwgSize;
			}

			//�o�͑Ώۂ̐}�ʂ�A0L�̏ꍇ�ŁA��ʂŎw�肵���T�C�Y���uA0�v�A�uA1�v�A�uA2�v�A�uA3�v�A�uA4�v�̏ꍇ�́A�u�p���Ƀt�B�b�g�v���Z�b�g����B
			if( (dwgSize.equalsIgnoreCase("A0L") || dwgSize.equalsIgnoreCase("A1L") || dwgSize.equalsIgnoreCase("A2L") ) && targetPaperSizeForA0L.containsKey(printSize)){
				fixedMode = "�p���Ƀt�B�b�g";
			}
		}

		return fixedMode;
	}

	/**
	 * ����T�C�Y����p�[�Z���g�{��������ɕϊ�.
	 * @param printSize ����T�C�Y.
	 * @return �p�[�Z���g�{��.�Œ�{���������ꍇ�͋󕶎���Ԃ�.
	 */
	public static String toPercentageZoom(String printSize) {
		// �}�b�v�e�[�u���ɑΉ����镶�����Ԃ�.
		return PercentageZoomMap.containsKey(printSize) ? PercentageZoomMap.get(printSize) : "";
	}

	/**
	 * //�o�͑Ώۂ̐}�ʂ�A0L�̏ꍇ�ŁA��ʂŎw�肵���T�C�Y���uA0�v�A�uA1�v�A�uA2�v�A�uA3�v�A�uA4�v�̏ꍇ�́A���̒l���Z�b�g����B
	 * @param dwgSize �o�͑Ώۂ̐}�ʃT�C�Y
	 * @param printSize�@��ʂ̎w��T�C�Y
	 * @return
	 */
	public static String toMediaSize( String dwgSize, String printSize, String printerMasSize ){
		String mediaSize = "����";

		if( (dwgSize.equalsIgnoreCase("A0L") || dwgSize.equalsIgnoreCase("A1L") || dwgSize.equalsIgnoreCase("A2L") )  && targetPaperSizeForA0L.containsKey(printSize)){
			//�o�͑Ώۂ̐}�ʂ�A0L�̏ꍇ�ŁA��ʂŎw�肵���T�C�Y���uA0�v�A�uA1�v�A�uA2�v�A�uA3�v�A�uA4�v�̏ꍇ
			mediaSize = printSize;
			//�ő����\�T�C�Y���傫���ꍇ�́A�ő����\�T�C�Y���Z�b�g����
			if( DrasapUtil.compareDrwgSize(printSize, printerMasSize) > 0 ){
				mediaSize = printerMasSize;
			}
		}

		return mediaSize;
	}

	/**
	 * �{�f�[�^�}��SQL��Ԃ�.
	 * @return SQL��.
	 */
	public String insertSql() {

		String[][] ATTRS = {
				{ "JOB_ID", "CHAR" },
				{ "RECIPIENT_ID", "CHAR" },
				{ "RECIPIENT_SUBID", "CHAR" },
				{ "DOCUMENT_ID", "CHAR" },
				{ "DOCUMENT_NAME", "VARCHAR" },
				{ "DOCUMENT_KIND", "CHAR" },
				{ "SEQUENCE_NO", "NUMERIC" },
				{ "FILE_PATH_NAME", "VARCHAR" },
				{ "DATA_FORMAT", "VARCHAR" },
				{ "COPIES", "NUMERIC" },
				{ "SIZE_MODE", "VARCHAR" },
				{ "MEDIA_SIZE", "VARCHAR" },
				{ "MEDIA_Y", "REAL" },
				{ "MEDIA_X", "REAL" },
				{ "ZOOM_MODE", "VARCHAR" },
				{ "FIXED_ZOOM", "VARCHAR" },
				{ "PERCENTAGE_ZOOM", "VARCHAR" },
				{ "RANKDOWN_ZOOM", "VARCHAR" },
				{ "SIZEMAPA0_ZOOM", "VARCHAR" },
				{ "SIZEMAPA1_ZOOM", "VARCHAR" },
				{ "SIZEMAPA2_ZOOM", "VARCHAR" },
				{ "SIZEMAPA3_ZOOM", "VARCHAR" },
				{ "SIZEMAPA4_ZOOM", "VARCHAR" },
				{ "SIZEMAPA0L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA1L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA2L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA3L_ZOOM", "VARCHAR" },
				{ "SIZEMAPA4L_ZOOM", "VARCHAR" },
				{ "PERCENTAGE_ZOOM_X", "VARCHAR" },
				{ "PERCENTAGE_ZOOM_Y", "VARCHAR" },
				{ "ORIENTATION", "VARCHAR" },
				{ "ROTATION", "VARCHAR" },
				{ "MEDIA_TYPE", "VARCHAR" },
				{ "FINISHING", "VARCHAR" },
				{ "OUTPUT_LOCATION", "VARCHAR" },
				{ "CUT_TYPE", "VARCHAR" },
				{ "MEDIA_SOURCE", "VARCHAR" },
				{ "MIRROR", "VARCHAR" },
				{ "TOP_MARGIN", "REAL" },
				{ "RIGHT_MARGIN", "REAL" },
				{ "LEFT_MARGIN", "REAL" },
				{ "BOTTOM_MARGIN", "REAL" },
				{ "LEADING_EDGE", "REAL" },
				{ "TRAILING_EDGE", "REAL" },
				{ "BINDING_TYPE", "VARCHAR" },
				{ "BINDING_EDGE", "REAL" },
				{ "STAMP1_KIND", "VARCHAR" },
				{ "STAMP1_POSITION_X", "REAL" },
				{ "STAMP1_POSITION_Y", "REAL" },
				{ "STAMP1_ORIGIN", "VARCHAR" },
				{ "STAMP1_ID", "VARCHAR" },
				{ "STAMP1_ZOOM", "VARCHAR" },
				{ "STAMP1_TEXT1", "VARCHAR" },
				{ "STAMP1_TEXT2", "VARCHAR" },
				{ "STAMP1_TEXT3", "VARCHAR" },
				{ "STAMP1_COLOR", "VARCHAR" },
				{ "TEXTSTAMP1_TEXT", "VARCHAR" },
				{ "TEXTSTAMP1_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP1_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP1_FONT", "VARCHAR" },
				{ "TEXTSTAMP1_SIZE", "VARCHAR" },
				{ "TEXTSTAMP1_COLOR", "VARCHAR" },
				{ "STAMP2_KIND", "VARCHAR" },
				{ "STAMP2_POSITION_X", "REAL" },
				{ "STAMP2_POSITION_Y", "REAL" },
				{ "STAMP2_ORIGIN", "VARCHAR" },
				{ "STAMP2_ID", "VARCHAR" },
				{ "STAMP2_ZOOM", "VARCHAR" },
				{ "STAMP2_TEXT1", "VARCHAR" },
				{ "STAMP2_TEXT2", "VARCHAR" },
				{ "STAMP2_TEXT3", "VARCHAR" },
				{ "STAMP2_COLOR", "VARCHAR" },
				{ "TEXTSTAMP2_TEXT", "VARCHAR" },
				{ "TEXTSTAMP2_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP2_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP2_FONT", "VARCHAR" },
				{ "TEXTSTAMP2_SIZE", "VARCHAR" },
				{ "TEXTSTAMP2_COLOR", "VARCHAR" },
				{ "STAMP3_KIND", "VARCHAR" },
				{ "STAMP3_POSITION_X", "REAL" },
				{ "STAMP3_POSITION_Y", "REAL" },
				{ "STAMP3_ORIGIN", "VARCHAR" },
				{ "STAMP3_ID", "VARCHAR" },
				{ "STAMP3_ZOOM", "VARCHAR" },
				{ "STAMP3_TEXT1", "VARCHAR" },
				{ "STAMP3_TEXT2", "VARCHAR" },
				{ "STAMP3_TEXT3", "VARCHAR" },
				{ "STAMP3_COLOR", "VARCHAR" },
				{ "TEXTSTAMP3_TEXT", "VARCHAR" },
				{ "TEXTSTAMP3_ORIGIN", "VARCHAR" },
				{ "TEXTSTAMP3_ROTATION", "VARCHAR" },
				{ "TEXTSTAMP3_FONT", "VARCHAR" },
				{ "TEXTSTAMP3_SIZE", "VARCHAR" },
				{ "TEXTSTAMP3_COLOR", "VARCHAR" },
				{ "OUTPUT_STATUS", "VARCHAR" },
				{ "OUTPUT_DETAIL_STATUS", "VARCHAR" },
				{ "OUTPUT_DATE", "TIMESTAMP" },
				{ "JOBTICKET_ID", "VARCHAR" },
				{ "ROTATE_EXTRA180", "VARCHAR" },
				{ "REINFORCE", "VARCHAR" },
				{ "PUNCH", "VARCHAR" },
				{ "DATA_SIZE", "VARCHAR" },
				{ "ORG_FILE_NAME", "VARCHAR" },
				{ "SHIFT_ALIGNMENT", "VARCHAR" },
				{ "SHIFT_X", "REAL" },
				{ "SHIFT_Y", "REAL" },
				{ "OUTPUT_WARN_MSG", "VARCHAR" },
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

		return String.format("INSERT INTO %s.SUBMIT_DOCUMENT ( %s ) VALUES ( %s )", this.getSchemaName(), insertAttrs.toString(), insertValues.toString());
	}

	/**
	 * �h�L�������gID���̔�.
	 *
	 * @param conn �f�[�^�x�[�X�R�l�N�^�[
	 * @param shortId �v�����^�V���[�gID
	 * @return JOBID��Ԃ�.
	 * @throws SQLException
	 */
	public static String getNewDocumentID(Connection conn, String schemaName, int schemaNo, String shortId) throws SQLException {

		// ���t���擾.
		String yymm = new SimpleDateFormat("yyMM").format(new Date());

		// �w��̃X�L�[�}��ID�A�ԊǗ�(OJ_SEQUENCE_X)����A�Ԃ��擾.
		int seqNo = APlotOJSequenceXDB.getNewSeq(
				conn,
				schemaName, // �X�L�[�}��.
				APlotOJSequenceXDB.JOB_ID_KIND1, // R�Œ�
				shortId, // �V���[�gID
				APlotOJSequenceXDB.JOB_ID_KIND3_DOC, //
				new SimpleDateFormat("yyMM").format(new Date()));

		// �uID��ʁi�啪�ށj + �X�L�[�}�ԍ� + ID��ʁi�����ށj + ID��ʁi�����ށj + �N���iYYMM�`���j + '-'�i�Œ�j + �A�ԁi00000000�`���j�v
		return String.format("%s%d%s%s%s-%07d", APlotOJSequenceXDB.JOB_ID_KIND1, schemaNo, shortId, APlotOJSequenceXDB.JOB_ID_KIND3_DOC, yymm, seqNo);
	}


}
