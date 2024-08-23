/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotPrintRequest.java
 * Name         : A-PLOT�o�}�w���N���X
 * Description  : ��ʂ���u�o�}�v���s�����ۂ�A-PLOT�o�}�w�����s���N���X.
 * -----------------------------------------------------------------------------------
 * Author       : 2018/02/21 hideki_sugiyama
 * -----------------------------------------------------------------------------------
 * Modify       :
 *   Date       Name
 *
 * -----------------------------------------------------------------------------------
 */
package tyk.drasap.aplot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Category;

import tyk.drasap.acslog.AccessLoger;
import tyk.drasap.common.AclvMasterDB;
import tyk.drasap.common.Printer;
import tyk.drasap.common.User;
import tyk.drasap.common.UserException;
import tyk.drasap.printlog.PrintLoger;
import tyk.drasap.search.SearchResultElement;


/**
 * ��ʂ���u�o�}�v���s�����ۂ�A-PLOT�o�}�w�����s��.
 *
 * �Ăяo��������͉��L�̃��\�b�h���Ăяo��.
 *
 *  // A-PLOT�o�}�w��.
 *  APlotPrintRequest.insertRequest(
 *             searchResultList,   -----  ��ʏ�\������Ă���}�Ԃ̌�������.
 *             printer,            -----  ��ʏ�I�����ꂽ�v�����^���.
 *             user,               -----  �o�}�w�������s�������[�U�[���.
 *             conn                -----  �f�[�^�x�[�X�ڑ����.
 *             );
 *
 * �o�}�Ώۂ́A��ʃ`�F�b�N�{�b�N�X��I�������Y���}�o�}�Ώۂ݂̂ƂȂ�.
 *
 *
 * @author hideki_sugiyama
 *
 */
public class APlotPrintRequest {

	/** Logger�ilog4j�j */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotPrintRequest.class.getName());

	/**
	 * �v�����^�}�X�^�擾.
	 * @param conn DB�R�l�N�V����.
	 * @param printer �I���v�����^���i��ʂ��j
	 * @return �v�����^�}�X�^�i�z��j
	 * @throws UserException
	 * @throws SQLException
	 */
	private static APlotPrinterMasterDB[] askPrintMaster(Connection conn, Printer printer) throws UserException, SQLException {

		Statement stmt = conn.createStatement();

		// �v�����^�}�X�^�� DRASAP.PRINTER_ASSIGN_MASTER����擾.
		APlotPrinterMasterDB[] printMaster =
				APlotPrinterMasterDB.getPrinterMaster(stmt,
							new String[] { printer.getId() } // �v�����^Id.
				);

		// ���̏ꍇ��1���̂݃q�b�g����͂�.
		if ( printMaster == null ) {
			// A-PLOT�Ȃ�
			throw new UserException("[" + printer.getDisplayName() + "] �v�����^����`����Ă��܂���B");
		} else if ( printMaster.length > 1 ) {
			// A-PLOT�Ȃ�
			throw new UserException("[" + printer.getDisplayName() + "] �v�����^��������`����Ă��܂��B");
		}

		return printMaster;
	}

	/**
	 * �z�z��}�X�^�擾.
	 * @param conn DB�R�l�N�V����.
	 * @param printMaster �v�����^�}�X�^���
	 * @return �z�z��}�X�^�i�z��j
	 * @throws UserException
	 * @throws SQLException
	 */
	private static APlotRecipientMasterDB[] askRecipientMaster(Connection conn, APlotPrinterMasterDB printMaster) throws UserException, SQLException {

		Statement stmt = conn.createStatement();

		// �z�z��}�X�^�� �w��X�L�[�}�� RECIPIENT_MASTER ����擾.
		APlotRecipientMasterDB[] recipieMaster =
				APlotRecipientMasterDB.getRecipientMaster(stmt,
						printMaster.getSchemaName(),   // �X�L�[�}��.
						new String[] { printMaster.getPrinterId() }        // �v�����^Id.
				);

		// ���̏ꍇ��1���̂݃q�b�g����͂�.
		if ( recipieMaster == null ) {
			// A-PLOT�Ȃ�
			throw new UserException("[" + printMaster.getPrinterName() + "] �v�����^����`����Ă��܂���B");
		}

		return recipieMaster;
	}


	/**
	 * �T�u�~�b�g�W���u�f�[�^���쐬����.
	 * @param conn DB�R�l�N�V����.
	 * @param printMaster �v�����^�}�X�^���.
	 * @param user ���[�U�[���.
	 * @return �T�u�~�b�g�W���u�f�[�^.
	 * @throws SQLException
	 */
	private static APlotSubmitJobDB createSubmitJob(Connection conn, APlotPrinterMasterDB printMaster , User user) throws SQLException {

		//
		String schema = printMaster.getSchemaName();

		// JOBID���̔�.
		String jobId = APlotSubmitJobDB.getNewJobID(conn,
				schema,                 // �X�L�[�}��.
				printMaster.getSchemaNo(), // �X�L�[�}�ԍ�.
				printMaster.getShortId());                // �V���[�gID.

		// �Q�l�}�o�}�̃W���u�f�[�^���쐬.
		APlotSubmitJobDB jobData = new APlotSubmitJobDB(schema, jobId, "�Q�l�}�o�}", user.getId(), user.getName());
		// �����ݒ�.
		jobData.put("COVER_PAGE", "�Ȃ�");  // �\���i�W�������j�쐬�w��
		jobData.put("PRIORITY", "��");      // �D��x
		String printId = printMaster.getPrinterId();
		printId = printId.trim();
		int printerIdLength = printId.length();
		int cutPos = printerIdLength - 3;
		if( cutPos < 0 ){
			cutPos = 0;
		}

		String queueId = printId.substring(cutPos);
		jobData.put("RECEIVE_QUEUE_ID", queueId);      // ��t�L���[ID

		return jobData;
	}

	/**
	 * �T�u�~�b�g�z�z������쐬����.
	 * @param conn DB�R�l�N�V����.
	 * @param job �W���u�f�[�^.
	 * @param recipieMaster �z�z��}�X�^���.
	 * @return �T�u�~�b�g�z�z����.
	 * @throws SQLException
	 */
	private static APlotSubmitRecipientDB createSubmitRecipient(Connection conn, APlotSubmitJobDB job, APlotRecipientMasterDB recipieMaster) throws SQLException {

		APlotSubmitRecipientDB recipe = new APlotSubmitRecipientDB(job.getSchemaName());
		recipe.put("JOB_ID", job.getJobID());
		recipe.put("RECIPIENT_ID", recipieMaster.get("RECIPIENT_ID")); // �z�z��ID
		recipe.put("RECIPIENT_SUBID", "1");// �z�z��ID���A�Ԃ�1�Œ�̂͂�.
		recipe.put("RECIPIENT_NAME", recipieMaster.get("RECIPIENT_NAME")); // �z�z�於
		recipe.put("SEQUENCE_NO",  "1"); // �V�[�P���X�ԍ� 1�Œ� ????
		recipe.put("PRINTER_ID", recipieMaster.get("PRINTER_ID"));
		recipe.put("DUE_MODE", "���Ȃ�");    // �����w�胂�[�h
//			recipient.put("OUTPUT_STATUS", ""); ��ŏo�}�҂��ɕς���.
		recipe.put("OUTPUT_ORDER", recipieMaster.get("OUTPUT_ORDER")); // �r����
		recipe.put("DATA_FOLDER_PATH", recipieMaster.get("DATA_FOLDER_PATH")); // �]����f�B���N�g��

		return recipe;
	}

	/**
	 * �T�u�~�b�g�h�L�������g�f�[�^���쐬����.
	 * @param conn DB�R�l�N�V����.
	 * @param printMaster �v�����^�}�X�^���.
	 * @param recipieMaster �z�z��}�X�^���.
	 * @param job �W���u�f�[�^.
	 * @param idx �}��.
	 * @param element �}�ʏ��i��ʂ��j
	 * @param userStampText �Q�l�}�X�^���v�����󂷂邩
	 * @return �T�u�~�b�g�h�L�������g�f�[�^.
	 * @throws SQLException
	 * @throws IOException
	 * @throws UserException
	 */
	private static APlotSubmitDocumentDB createSubmitDocument(Connection conn, APlotPrinterMasterDB printMaster, APlotRecipientMasterDB recipieMaster, APlotSubmitJobDB job, int idx, SearchResultElement element, String userStampText) throws SQLException, IOException, UserException {

		// �X�L�[�}.
		String schemaName = job.getSchemaName();
		// �X�L�[�}�ԍ�.
		int schemaNo = job.getSchemaNo();


		// �h�L�������gID���̔�.
		String docId = APlotSubmitDocumentDB.getNewDocumentID(conn,
				schemaName,                   // �X�L�[�}��.
				schemaNo,                     // �X�L�[�}�ԍ�.
				printMaster.getShortId());    // �V���[�gID.

		// ---------------------------------------------------------------------
		// ADMIN_SETTING_MASTER����V�X�e���l���擾.
		// �}�ʃt�@�C���i�[�t�H���_�p�X�iValuts�j.
		String valutsFolderPath = APlotSystemMasterDB.getInstance().getVaultsFolderPath();
		//
		// �}�ʃt�@�C���X�v�[����t�H���_�p�X.
		String spoolFolderPath = APlotSystemMasterDB.getInstance().getSpoolFolderPath(schemaNo);

		// �h�L�������g�t���p�X.
		String documentFolderPath = APlotSystemMasterDB.getInstance().getDocumentFolderPath(schemaNo);

		// �X�v�[����փt�@�C�����R�s�[(���s������폜����).
		String fromFile = Paths.get(valutsFolderPath, element.getPathName(), element.getFileName()).normalize().toString();

		// �R�s�[��t�@�C���p�X
		String toFile =  Paths.get(spoolFolderPath, docId + "_" + element.getFileName()).normalize().toString();

		// �h�L�������g�t���p�X.
		String docFile = Paths.get(documentFolderPath, docId + "_" + element.getFileName()).normalize().toString();

		// �t�@�C�����R�s�[.
		copyFile(fromFile, toFile);

		// �h�L�������g�f�[�^�쐬.
		APlotSubmitDocumentDB doc = new APlotSubmitDocumentDB(job.getSchemaName());
		doc.put("JOB_ID", job.getJobID());							// �W���uID
		doc.put("RECIPIENT_ID", recipieMaster.get("RECIPIENT_ID"));	// �z�z��ID
		doc.put("RECIPIENT_SUBID", "1");							// �z�z��ID���A��
		doc.put("DOCUMENT_ID", docId);								// �h�L�������gID
		doc.put("DOCUMENT_NAME", element.getDrwgNo());				// �h�L�������g��(�}�ԍ�)
		doc.put("DOCUMENT_KIND", "D");								// �h�L�������g���
		doc.put("SEQUENCE_NO", String.format("%05d", idx+1));		// �V�[�P���X�ԍ�
		doc.put("FILE_PATH_NAME", docFile);							// �t�@�C���t���p�X��
		doc.put("DATA_FORMAT", "TIFF");								// �f�[�^�t�H�[�}�b�g
		doc.put("COPIES", element.getCopies());						// ����
		doc.put("SIZE_MODE", "�T�C�Y�w��");							// �p���T�C�Y���[�h

		//���}�̃T�C�Y
		String dwgSize = element.getAttr("DRWG_SIZE");
		//��ʎw��̈���T�C�Y
		String printSize = element.getPrintSize();
		//�v�����^�̍ő�o�̓T�C�Y
		String printerMaxSize = element.getPrinterMaxSize();

		doc.put("MEDIA_SIZE", APlotSubmitDocumentDB.toMediaSize(dwgSize, printSize, printerMaxSize) );								// �p���T�C�Y�w��

		// �X�P�[�����O���[�h.
		String zoomMode = APlotSubmitDocumentDB.toZoomMode(element.getPrintSize());
		doc.put("ZOOM_MODE",zoomMode ); // �X�P�[�����O���[�h
		doc.put("FIXED_ZOOM",
				APlotSubmitDocumentDB.toFixedZoom( zoomMode, dwgSize, printSize)); // �X�P�[�����O���[�h

		doc.put("PERCENTAGE_ZOOM",
				APlotSubmitDocumentDB.toPercentageZoom(element.getPrintSize())); // �X�P�[�����O���[�h
		doc.put("PERCENTAGE_ZOOM_X", "100");	// X�����p�[�Z���g�{��
		doc.put("PERCENTAGE_ZOOM_Y", "100");	// Y�����p�[�Z���g�{��
		doc.put("ORIENTATION", "�w��Ȃ�");		// �������
		doc.put("ROTATION", "0");				// �C���[�W�̉�]

		if ( "DEDICATED".equals(printMaster.get("OUTPUT_DEVICE").toString()) ) {
			// �v�����^�}�X�^���̏o�͑��u���uDEDICATED�v�̏ꍇ�̂ݐݒ�.
			doc.put("MEDIA_TYPE", recipieMaster.get("MEDIA_TYPE"));				// �p�����
			doc.put("FINISHING", recipieMaster.get("FINISHING"));				// �܂�
			doc.put("OUTPUT_LOCATION", recipieMaster.get("OUTPUT_LOCATION"));	// �r����
			doc.put("CUT_TYPE", recipieMaster.get("CUT_TYPE"));				// �J�b�g���@
			doc.put("MEDIA_SOURCE", recipieMaster.get("MEDIA_SOURCE"));		// �������u
			doc.put("LEADING_EDGE", recipieMaster.get("LEADING_EDGE"));		// ��[�]��
			doc.put("TRAILING_EDGE", recipieMaster.get("TRAILING_EDGE"));	// ��[�]��
			doc.put("BINDING_EDGE", recipieMaster.get("BINDING_EDGE"));		//�Ԃ���
		}
		doc.put("MIRROR", "off");				// �C���[�W���]

		// STAMP 1 �i�Y���}�j
		String dwgNo = element.getDrwgNo();
		boolean isCorrpd = false;

		try{
			isCorrpd = AclvMasterDB.isCorresponding(dwgNo, conn);
		}
		catch( Exception ex ){
			throw new UserException(ex.getMessage());
		}

		String stampKind = "None";

		if( isCorrpd ){
			stampKind = "IMAGE";
			APlotSystemMasterDB.APlotStampData stamp1 = APlotSystemMasterDB.getInstance().getStamp1Data();
			doc.put("STAMP1_KIND",stampKind);		// �X�^���v 1 ���
			doc.put("STAMP1_POSITION_X", stamp1.positionX);	// �X�^���v 1 ����ʒu X
			doc.put("STAMP1_POSITION_Y", stamp1.positionY);	// �X�^���v 1 ����ʒu Y
			doc.put("STAMP1_ORIGIN", stamp1.origin);		// �X�^���v 1���_
			doc.put("STAMP1_ID", stamp1.id);				// �X�^���v 1 ID
			doc.put("STAMP1_ZOOM", stamp1.zoom);			// �C���[�W�X�^���v/�o�i�[ 1 �T�C�Y
			doc.put("STAMP1_TEXT1",stamp1.stampText1);		// �C���[�W�X�^���v/�o�i�[ 1 �}�������� 1
			doc.put("STAMP1_COLOR", stamp1.color);			// �C���[�W�X�^���v/�o�i�[ 1 �����Z�W
		}
		else{
			doc.put("STAMP1_KIND",stampKind);		// �X�^���v 1 ���
		}

		// STAMP 2�i�Q�l�}�j
		if ( userStampText != null && !"".equals(userStampText) ) {
			APlotSystemMasterDB.APlotStampData stamp2 = APlotSystemMasterDB.getInstance().getStamp2Data();
			doc.put("STAMP2_KIND","IMAGE");		// �X�^���v 1 ���
			doc.put("STAMP2_POSITION_X", stamp2.positionX);	// �X�^���v 1 ����ʒu X
			doc.put("STAMP2_POSITION_Y", stamp2.positionY);	// �X�^���v 1 ����ʒu Y
			doc.put("STAMP2_ORIGIN",  stamp2.origin);		// �X�^���v 1���_
			doc.put("STAMP2_ID", stamp2.id);			// �X�^���v 1 ID
			doc.put("STAMP2_ZOOM", stamp2.zoom);			// �C���[�W�X�^���v/�o�i�[ 1 �T�C�Y
			doc.put("STAMP2_TEXT1",userStampText);	// �C���[�W�X�^���v/�o�i�[ 1 �}�������� 1
			doc.put("STAMP2_COLOR", stamp2.color);	// �C���[�W�X�^���v/�o�i�[ 1 �����Z�W
		} else {
			doc.put("STAMP2_KIND","None");		// �X�^���v 1 ���
		}

		// STAMP 3
		doc.put("STAMP3_KIND", "None");			// �X�^���v���3(JTEKT�̓X�^���v3�͎g�p���Ȃ��j

		//
		doc.put("ROTATE_EXTRA180", "off");		// �C���[�W180�x��]
		doc.put("REINFORCE", "off");			// ���C���t�H�[�X
		doc.put("PUNCH", "off");				// �p���`��
		doc.put("DATA_SIZE", element.getAttr("DRWG_SIZE") );			// �f�[�^�T�C�Y,INDEX_DB.DRWG_SIZE���Z�b�g����
		doc.put("ORG_FILE_NAME", element.getFileName());		// �I���W�i���t�@�C����
		doc.put("SHIFT_ALIGNMENT", "center");	// ����ʒu�V�t�g�ʒu���킹
		doc.put("SHIFT_X", 0);	// ����ʒu�V�t�gX����
		doc.put("SHIFT_Y", 0);	// ����ʒu�V�t�gY����

		return doc;
	}


	/**
	 * �Ή�����X�L�[�}�̏o�}�w������o�^����.
	 *
	 * @param conn
	 * @param job
	 * @param recipeList
	 * @param docList
	 * @return
	 * @throws SQLException
	 */
	private static int insertDatas(Connection conn, APlotSubmitJobDB job, ArrayList<APlotSubmitRecipientDB> recipeList, ArrayList<APlotSubmitDocumentDB> docList) throws SQLException {
		int cnt = 0; // �o�^��.

		// �g�����U�N�V������ݒ肷��
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		try{
			// �W���u�f�[�^�o�^.
			String sql = job.insertSql();
			category.debug(sql);
			stmt.executeUpdate(sql);
			category.info("A-PLOT�o�} �����X�L�[�}:" + job.getSchemaName() + " JOB ID:" + job.getJobID() );

			// �z�z����o�^.
			for ( APlotSubmitRecipientDB recipie : recipeList ) {
				sql = recipie.insertSql();
				category.debug(sql);
				stmt.executeUpdate(sql);
				category.info("A-PLOT�o�} �z�z��ID:" + recipie.get("RECIPIENT_ID").toString() + " �z�z��:" + recipie.get("RECIPIENT_NAME").toString());
			}
			// �h�L�������g���o�^.
			for ( APlotSubmitDocumentDB doc : docList ) {
				sql = doc.insertSql();
				category.debug(sql);
				int ret = stmt.executeUpdate(sql);
				category.info("A-PLOT�o�} �h�L�������gID:" + doc.get("DOCUMENT_ID").toString() + " �h�L�������g��:" + doc.get("DOCUMENT_NAME").toString());
				cnt = ret + cnt;
			}

			// �ŏI�I�ɔz�z����̃X�e�[�^�X���o�}�҂��ɂ���i�Ӗ����邩�킩��Ȃ����ǁj
			for ( APlotSubmitRecipientDB recipie : recipeList ) {
				sql = recipie.updateStatusSql( job.getJobID(), recipie.get("RECIPIENT_ID").toString() ,"�o�}�҂�");
				category.debug(sql);
				stmt.executeUpdate(sql);
				category.info("A-PLOT�o�} �X�e�[�^�X�ύX �z�z��ID:" + recipie.get("RECIPIENT_ID").toString() + " �X�e�[�^�X:�o�}�҂�");
			}

		} catch (SQLException ex) {
			category.fatal("A-PLOT�o�} �o�}�������W���[���֏o�}�w�����o�^��SQL�G���[", ex);
			throw ex;
		} finally {
		}
		category.info("A-PLOT�o�} �h�L�������g�o�^��:" + cnt);
		return cnt;
	}

	/**
	 * �t�@�C�����R�s�[����.
	 * �R�s�[��̃t�H���_���Ȃ��ꍇ�͎����I�ɍ쐬����.
	 * @param fromFilePath ���t�@�C���̃t���p�X.
	 * @param toDirPath �R�s�[��t�H���_�p�X.
	 * @throws IOException
	 */
	private static void copyFile(String fromFilePath, String toFilePath ) throws IOException {

		try {
			// ���t�@�C��.
			File fromFile = new File(fromFilePath);
			// �R�s�[��t�@�C��.
			File toFile = new File(toFilePath);

			// �R�s�[��t�H���_.
			File toDir = new File(toFile.getParent());

			if ( !fromFile.exists() || !fromFile.isFile() ) {
				// �w��t�@�C�������݂��Ȃ�.
				throw new IOException("�w��̃t�@�C�������݂��܂���. [" + fromFile.getPath() + "]");
			}

			// �R�s�[��t�H���_�����݂��Ȃ��ꍇ�͍쐬����.
			if ( !toDir.exists() ) {
				// �t�H���_�쐬.
				if ( !toDir.mkdirs() ) {
					// �t�H���_�̍쐬�Ɏ��s.
					throw new IOException("�t�H���_�̍쐬���ł��܂���ł���. [" + toDir.getPath() + "]");
				}
			} else if ( !toDir.isDirectory() ) {
				// �w��̃p�X���t�H���_�ł͂Ȃ�.
				throw new IOException("�w��̃R�s�[��p�X���t�H���_�ł͂Ȃ�. [" + toDir.getPath() + "]");
			}
			category.debug("copy " + fromFile.toPath() + " -> " + toFile.toPath());
			// New I/O�Ńt�@�C�����R�s�[.
			Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING );

		} catch (IOException ex) {
			category.fatal("A-PLOT�o�} �}�ʃt�@�C���X�v�[�������ŃG���[", ex);
			throw ex;
		} finally {

		}
	}


	/**
	 * SearchResultElement�̃��X�g�����ɁAAPLOT�e�[�u���ɏ����o���B
	 * ���̂Ƃ��ASearchResultElement�́A�I������Ă��āA����\�Ȑ}�ʂł�����̂̂�
	 *
	 * �o�͕�����A-PLOT�v�����^�ɕύX.
	 * �w��̃v�����^�[ID�ɊY������X�L�[�}�[��PRINTER_ASSIGN_MASTER�e�[�u������擾���A�o�͏������LJOB�f�[�^�Ƃ��ēo�^����.
	 *  submit_job �i�T�u�~�b�g�W���u�j
	 *  submit_recipient �i�T�u�~�b�g�z�z��j
	 *  submit_document �i�T�u�~�b�g�h�L�������g�j
	 *
	 * @param searchResultList ��ʏ�\������Ă���}�Ԃ̌�������.
	 * @param printer ��ʏ�I�����ꂽ�v�����^���.
	 * @param user �o�}�w�������s�������[�U�[���.
	 * @param conn �f�[�^�x�[�X�ڑ����.
	 * @return �o�͌���.
	 * @throws Exception
	 */
	public static int insertRequest(ArrayList<SearchResultElement> searchResultList, Printer printer, User user, Connection conn) throws Exception {

		// �߂�l�F
		int cnt = 0;

		// �{�����t.
		Date outputDate = new Date();

		// �G���[���̃��O�o�͗p�}��.
		String logDrwgNo = null;
		try {
			// �����R�~�b�g�����Ȃ�.
			conn.setAutoCommit(false);

			// �X�^���v2�i�Q�l�}�j�p�ݒ�F���t�`��.
			String stamp2outputDate = new SimpleDateFormat(APlotSystemMasterDB.getInstance().getStamp2Data().dateFormat).format(outputDate);

			// �v�����^�}�X�^�� DRASAP.PRINTER_ASSIGN_MASTER����擾.
			APlotPrinterMasterDB[] printMaster = askPrintMaster(conn, printer);

			// �z�z��}�X�^�� �w��X�L�[�}�� RECIPIENT_MASTER ����擾.
			APlotRecipientMasterDB[] recipieMaster = askRecipientMaster(conn, printMaster[0]);

			for(int drwIdx = 0; drwIdx < searchResultList.size(); drwIdx++ ) {
				// �I�����Ă������\�i�Y���X�^���v����Ώہj�}�Ԃ̂ݑΏ�.
				SearchResultElement element = (SearchResultElement) searchResultList.get(drwIdx);
				if (element.isSelected() && user.isPrintableByReq(element, conn, (drwIdx > 0))) {
					// �W���u�̓o�^ >>>>>>>>>>>>>>>>>>>>>>>>�A�PJob�P�}�ԂɕύX����

					// �Q�l�}�o�}�̃W���u�f�[�^���쐬.
					APlotSubmitJobDB jobData = createSubmitJob(conn, printMaster[0], user);

					// �}�Ԃ����M���O���邽�߈ꎞ�ۊǂ��郊�X�g
					List<String> updatedDrwgNoList = new ArrayList<String>();

					// �z�z����̃f�[�^���쐬.
					// �T�u�~�b�g�z�z��(JTEKT�̎Q�l�}�o�}�̓f�[�^��1�������肦�Ȃ��͂��j
					ArrayList<APlotSubmitRecipientDB> recipientData = new ArrayList<APlotSubmitRecipientDB>();
					recipientData.add(createSubmitRecipient(conn, jobData, recipieMaster[0]));

					// �T�u�~�b�g�h�L�������g �I��}�ʐ�������o�^.
					ArrayList<APlotSubmitDocumentDB> documentData = new ArrayList<APlotSubmitDocumentDB>();
	//			for(int drwIdx = 0; drwIdx < searchResultList.size(); drwIdx++ ) {
					//
//					SearchResultElement element = (SearchResultElement) searchResultList.get(drwIdx);

					// �I�����Ă������\�i�Y���X�^���v����Ώہj�}�Ԃ̂ݑΏ�.
	//�]�v��Job�̔Ԃ����Ȃ����߃��[�v�擪�ֈړ�				if (element.isSelected() && user.isPrintableByReq(element, conn, (drwIdx > 0))) {
						// �G���[���O�p�ɕێ�.
						logDrwgNo = element.getDrwgNo();

						// �Q�l�}�o�}�X�^���v. ���̕ϐ�����̏ꍇ�͎Q�l�}�X�^���v�����󂵂Ȃ�.
						String userStampText = "";
						if( ( printer.isEucPrinter() && user.isEucStamp() ) || user.isPltrStamp() ){
							userStampText = String.format("%s%s  %s", APlotSystemMasterDB.getInstance().getStamp2Data().stampText1, stamp2outputDate , user.getName());
						}

						// �h�L�������g�f�[�^�쐬.
						documentData.add(createSubmitDocument(conn, printMaster[0], recipieMaster[0], jobData, drwIdx, element, userStampText ));

						// ���O���o��.
						PrintLoger.info(PrintLoger.ACT_WRITE, user, element.getDrwgNo());

						// ��Ń��M���O���邽�߂Ƀ��X�g�Ɉꎞ�ۊǂ���
						updatedDrwgNoList.add(element.getDrwgNo());
	//			}

					// ����Ώې}�ʂ�����ꍇ�Ƀf�[�^��o�^����. >>>>>>>>>>>>>>>>>>>>
					int insertedCnt = insertDatas(conn, jobData, recipientData, documentData);
					cnt += insertedCnt;

					// �A�N�Z�X���O���܂Ƃ߂čs��
					AccessLoger.loging(user, AccessLoger.FID_OUT_DRWG,
							(String[])updatedDrwgNoList.toArray(new String[0]), user.getSys_id());
				}

				conn.commit();
			}
		} catch (SQLException | UserException | IOException ex) {
			// �G���[�̂��߃��O���m�F.
			if ( logDrwgNo != null ) { PrintLoger.info(PrintLoger.FAILED_WRITE, user, logDrwgNo); }
			conn.rollback();
			throw ex;
		} finally {
		}
		return cnt;
	}

}
