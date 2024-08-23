/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP �}�ԎQ��WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSystemMasterDB.java
 * Name         : A-PLOT�o�} �Ǘ��Ґݒ�}�X�^���Ǘ��N���X.
 * Description  : A-PLOT�o�}�ɕK�v�ȊǗ��Ґݒ�}�X�^�����Ǘ�����.
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
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Category;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.ErrorUtility;
import tyk.drasap.common.UserException;

/**
 * A-PLOT�o�} �Ǘ��Ґݒ�}�X�^���Ǘ��N���X.
 * A-PLOT�o�}�ɕK�v�ȊǗ��Ґݒ�}�X�^�����Ǘ�����.
 * �{�N���X�̓V���O���g���N���X�ƂȂ��Ă���A��x�擾�������͍ċN������܂ōĎ擾����Ȃ�.
 * DB���X�V�����ꍇ�͍ċN�����K�v�ƂȂ�.
 *
 *
 * �y�g�p���@�z
 *
 *    APlotSystemMasterDB data = APlotSystemMasterDB.getInstance();
 *    // Vaults�p�X���擾.
 *    String path = data.getVaultsFolderPath();
 *
 * @author hideki_sugiyama
 */
public class APlotSystemMasterDB {

	/** Logger�ilog4j�j */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotSystemMasterDB.class.getName());
	/** DB�ڑ��f�[�^�\�[�X */
	private static DataSource ds;
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSource�̎擾�Ɏ��s\n" + ErrorUtility.error2String(e));
		}
	}

	/** �}�ʃt�@�C��vaults�t�H���_�p�X. */
	private String vaultsFolderPath = null;


	/** �}�ʃt�@�C���X�v�[����t�H���_�p�X. */
	private HashMap<Integer, String> spoolFolderPath = new HashMap<>();


	/** �}�ʃt�@�C���X�v�[����t�H���_�p�X. */
	private HashMap<Integer, String> documentFolderPath = new HashMap<>();


	/**
	 * �X�^���v�p�ݒ�N���X.
	 * �֋X��A�����N���X�ō\���̂̂悤�Ɉ���.
	 * @author hideki_sugiyama
	 *
	 */
	public class APlotStampData {
		/** ����ʒu X */
		public  String positionX = "";
		/** ����ʒu Y */
		public  String positionY = "";
		/** ���_ */
		public  String origin = "";
		/** ID */
		public String id = "";
		/** �o�i�[�T�C�Y */
		public  String zoom = "";
		/** �����Z�W */
		public String color = "";
		/** ���t�`��. */
		public String dateFormat = "";
		/** �o�i�[������1. */
		public String stampText1 = "";
	}

	/** �Y���}�X�^���v�V�X�e���ݒ�. */
	private APlotStampData stamp1setting = new APlotStampData();

	/** �Q�l�}�X�^���v�V�X�e���ݒ�. */
	private APlotStampData stamp2setting = new APlotStampData();

	/**
	 * �V���O���g���N���X.
	 */
	public static APlotSystemMasterDB _instance = null;


	/**
	 * �R���X�g���N�^.
	 * @throws SQLException
	 * @throws UserException
	 */
	private APlotSystemMasterDB() throws SQLException, UserException {
		// ������.
		init();
		//
		initCheck( );
	}

	/**
	 * �V���O���g���N���X�̎擾.
	 * @return �C���X�^���X.
	 * @throws SQLException
	 * @throws UserException
	 */
	public static APlotSystemMasterDB getInstance() throws SQLException, UserException {

		if ( _instance == null ) {
			_instance = new APlotSystemMasterDB();
		}
		return _instance;
	}


	/**
	 * �V�X�e���ݒ�e�[�u��(ADMIN_SETTING_MASTER)����APLOT�ɕK�v�Ȓl���擾.
	 *
	 * �y�擾�l�z
	 *   001:ViewDB�h���C�u��
	 *   800:�X�v�[����t�H���_�iOJ1�j
	 *   801:�X�v�[����t�H���_�iOJ2�j
	 *   805:�h�L�������g�t���p�X�iOJ1�j
	 *   806:�h�L�������g�t���p�X�iOJ2�j
	 *
	 *   �Q�l�}�o�͗p�X�^���v�ݒ�
	 *   810:�Q�l�}�o�͗p�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
	 *   811:�Q�l�}�o�͗p�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
	 *   812:�Q�l�}�o�͗p�X�^���v���_
	 *   813:�Q�l�}�o�͗p�X�^���vID
	 *   814:�Q�l�}�o�͗p�X�^���v�X�P�[�����O
	 *   815:�Q�l�}�o�͗p�X�^���v�����Z�W�@0-100�܂ł̐��l
	 *   816:�Q�l�}�o�͗p�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
	 *   719:�Q�l�}�o�͗p�X�^���v������
	 *
	 *   �Q�l�}�o�͗p�Y���}�X�^���v�ݒ�
	 *   820:�Q�l�}�o�͗p�Y���}�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
	 *   821:�Q�l�}�o�͗p�Y���}�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
	 *   822:�Q�l�}�o�͗p�Y���}�X�^���v���_
	 *   823:�Q�l�}�o�͗p�Y���}�X�^���vID
	 *   824:�Q�l�}�o�͗p�Y���}�X�^���v�X�P�[�����O
	 *   825:�Q�l�}�o�͗p�Y���}�X�^���v�����Z�W�@0-100�܂ł̐��l
	 *   826:�Q�l�}�o�͗p�Y���}�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
	 *   736:�Q�l�}�o�͗p�Y���}�X�^���v������
	 *
	 * @return sql������.
	 */
	private static String selectAPlotSystemValues() {
		//
		StringBuilder sb = new StringBuilder("");
		sb.append("'001'");   // ViewDB�h���C�u��
		sb.append(",'800'");  // �X�v�[����t�H���_�iOJ1�j
		sb.append(",'801'");  // �X�v�[����t�H���_�iOJ2�j
		sb.append(",'805'");  // �h�L�������g�t���p�X�iOJ1�j
		sb.append(",'806'");  // �h�L�������g�t���p�X�iOJ2�j
		// �Q�l�}�o�͗p�X�^���v�ݒ�
		sb.append(",'810'"); // �Q�l�}�o�͗p�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
		sb.append(",'811'"); // �Q�l�}�o�͗p�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
		sb.append(",'812'"); // �Q�l�}�o�͗p�X�^���v���_
		sb.append(",'813'"); // �Q�l�}�o�͗p�X�^���vID
		sb.append(",'814'"); // �Q�l�}�o�͗p�X�^���v�X�P�[�����O
		sb.append(",'815'"); // �Q�l�}�o�͗p�X�^���v�����Z�W�@0-100�܂ł̐��l
		sb.append(",'816'"); // �Q�l�}�o�͗p�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
		sb.append(",'719'"); // �Q�l�}�o�͗p�X�^���v������
		// �Q�l�}�o�͗p�Y���}�X�^���v�ݒ�
		sb.append(",'820'"); // �Q�l�}�o�͗p�Y���}�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
		sb.append(",'821'"); // �Q�l�}�o�͗p�Y���}�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
		sb.append(",'822'"); // �Q�l�}�o�͗p�Y���}�X�^���v���_
		sb.append(",'823'"); // �Q�l�}�o�͗p�Y���}�X�^���vID
		sb.append(",'824'"); // �Q�l�}�o�͗p�Y���}�X�^���v�X�P�[�����O
		sb.append(",'825'"); // �Q�l�}�o�͗p�Y���}�X�^���v�����Z�W�@0-100�܂ł̐��l
		sb.append(",'826'"); // �Q�l�}�o�͗p�Y���}�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
		sb.append(",'736'"); // �Q�l�}�o�͗p�Y���}�X�^���v������
		return String.format("SELECT * FROM ADMIN_SETTING_MASTER WHERE SETTING_ID in (%s) AND STATUS = 1", sb.toString());
	}

	/**
	 * ������.
	 * @throws SQLException
	 */
	private void init() throws SQLException {
		Connection conn = ds.getConnection();
		conn.setAutoCommit(true);// ��g�����U�N�V����
		Statement stmt = conn.createStatement();

		this.spoolFolderPath.clear();
		this.documentFolderPath.clear();

		// �V�X�e���l���擾.
		ResultSet rs = stmt.executeQuery(selectAPlotSystemValues());
		try {
			// �擾������񂩂�X�L�[�}�����擾.
			while(rs.next()){

				String id = rs.getString("SETTING_ID");
				if ( "001".equals(id) ) {
					// 001: ViewDB�h���C�u�� �̏ꍇ
					this.vaultsFolderPath = rs.getString("VALUE");
				} else if ( "800".equals(id) ) {
					// 800: �X�v�[����t�H���_�iOJ1�j
					this.spoolFolderPath.put(new Integer(1), rs.getString("VALUE"));
				} else if ( "801".equals(id) ) {
					// 801: �X�v�[����t�H���_�iOJ2�j
					this.spoolFolderPath.put(new Integer(2), rs.getString("VALUE"));
				} else if ( "805".equals(id) ) {
					// 805: �h�L�������g�t���p�X�iOJ1�j
					this.documentFolderPath.put(new Integer(1), rs.getString("VALUE"));
				} else if ( "806".equals(id) ) {
					// 806: �h�L�������g�t���p�X�iOJ2�j
					this.documentFolderPath.put(new Integer(2), rs.getString("VALUE"));

				} else if ( "810".equals(id) ) { // �Q�l�}�o�͗p�X�^���v�ݒ�
					// �Q�l�}�o�͗p�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
					this.stamp2setting.positionX = rs.getString("VALUE");
				} else if ( "811".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
					this.stamp2setting.positionY = rs.getString("VALUE");
				} else if ( "812".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v���_
					this.stamp2setting.origin = rs.getString("VALUE");
				} else if ( "813".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���vID
					this.stamp2setting.id = rs.getString("VALUE");
				} else if ( "814".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v�X�P�[�����O
					this.stamp2setting.zoom = rs.getString("VALUE");
				} else if ( "815".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v�����Z�W�@0-100�܂ł̐��l
					this.stamp2setting.color = rs.getString("VALUE");
				} else if ( "816".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
					if ( "0".equals(rs.getString("VALUE")) ) {
						this.stamp2setting.dateFormat = "yyyy/MM/dd";
					} else if ( "1".equals(rs.getString("VALUE")) ) {
						this.stamp2setting.dateFormat = "yy/MM/dd";
					}
				} else if ( "719".equals(id) ) {
					// �Q�l�}�o�͗p�X�^���v������
					this.stamp2setting.stampText1 = rs.getString("VALUE");

				} else if ( "820".equals(id) ) { // �Q�l�}�o�͗p�Y���}�X�^���v�ݒ�
					// �Q�l�}�o�͗p�Y���}�X�^���v�ʒu(X����) �p���̉E�����_����̉������̋���(mm)
					this.stamp1setting.positionX = rs.getString("VALUE");
				} else if ( "821".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v�ʒu(Y����) �p���̉E�����_����̏c�����̋���(mm)
					this.stamp1setting.positionY = rs.getString("VALUE");
				} else if ( "822".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v���_
					this.stamp1setting.origin = rs.getString("VALUE");
				} else if ( "823".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���vID
					this.stamp1setting.id = rs.getString("VALUE");
				} else if ( "824".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v�X�P�[�����O
					this.stamp1setting.zoom = rs.getString("VALUE");
				} else if ( "825".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v�����Z�W�@0-100�܂ł̐��l
					this.stamp1setting.color = rs.getString("VALUE");
				} else if ( "826".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v���t�`���@0:YYYY/MM/DD�A1:YY/MM/DD
					if ( "0".equals(rs.getString("VALUE")) ) {
						this.stamp1setting.dateFormat = "yyyy/MM/dd";
					} else if ( "1".equals(rs.getString("VALUE")) ) {
						this.stamp1setting.dateFormat = "yy/MM/dd";
					}
				} else if ( "736".equals(id) ) {
					// �Q�l�}�o�͗p�Y���}�X�^���v������
					this.stamp1setting.stampText1 = rs.getString("VALUE");
				}
			}
		} catch (SQLException ex) {
			category.fatal("A-PLOT�o�} �Ǘ��Ґݒ�}�X�^���擾��SQL�G���[", ex);
			throw ex;
		} finally {
			if ( rs != null ) rs.close();
		}

		//
	}

	/**
	 * �O�̂��ߕK�{�̊��ݒ肪����Ă��邩�m�F.
	 * @throws UserException
	 */
	private void initCheck( ) throws UserException {
		if ( this.vaultsFolderPath == null || "".equals(this.vaultsFolderPath) ) {
			throw new UserException("�Ǘ��Ґݒ�}�X�^��[ViewDB�h���C�u��]���ݒ肳��Ă��܂���.");
		}
		if ( this.spoolFolderPath.size() == 0 ) {
			throw new UserException("�Ǘ��Ґݒ�}�X�^��[�X�v�[����t�H���_]���ݒ肳��Ă��܂���.");
		}
		if ( this.documentFolderPath.size() == 0 ) {
			throw new UserException("�Ǘ��Ґݒ�}�X�^��[�h�L�������g�t���p�X]���ݒ肳��Ă��܂���.");
		}
		if ( "".equals(this.stamp1setting.id) ) {
			throw new UserException("�Ǘ��Ґݒ�}�X�^��[�Q�l�}�o�͗p�X�^���vID]���ݒ肳��Ă��܂���.");
		}
		if ( "".equals(this.stamp2setting.id) ) {
			throw new UserException("�Ǘ��Ґݒ�}�X�^��[�Q�l�}�o�͗p�Y���}�X�^���vID]���ݒ肳��Ă��܂���.");
		}

	}

	/**
	 * �}�ʃt�@�C���̊i�[�t�H���_�p�X���擾.
	 * @return �}�ʃt�@�C���̊i�[�t�H���_�p�X.
	 */
	public String getVaultsFolderPath() {
		return this.vaultsFolderPath;
	}

	/**
	 * �}�ʃt�@�C���̃X�v�[����t�H���_�p�X���擾.
	 * @return �}�ʃt�@�C���̃X�v�[����t�H���_�p�X.
	 */
	public String getSpoolFolderPath(int schemaNo) {
		return this.spoolFolderPath.get(new Integer(schemaNo));
	}

	/**
	 * �}�ʃt�@�C���̃t���p�X���擾.
	 * @return �}�ʃt�@�C���̃t���p�X.
	 */
	public String getDocumentFolderPath(int schemaNo) {
		return this.documentFolderPath.get(new Integer(schemaNo));
	}


	/**
	 * �X�^���v1�̐ݒ���擾.
	 * @return �X�^���v1�̐ݒ���.
	 */
	public APlotStampData getStamp1Data() {
		return this.stamp1setting;
	}

	/**
	 * �X�^���v1�̐ݒ���擾.
	 * @return �X�^���v1�̐ݒ���.
	 */
	public APlotStampData getStamp2Data() {
		return this.stamp2setting;
	}


}
