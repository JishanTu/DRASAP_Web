/**
 * -----------------------------------------------------------------------------------
 * JTEKT DRASAP 図番参照WEB
 * -----------------------------------------------------------------------------------
 * Project Name : tyk.drasap.aplot
 * File Name    : APlotSystemMasterDB.java
 * Name         : A-PLOT出図 管理者設定マスタ情報管理クラス.
 * Description  : A-PLOT出図に必要な管理者設定マスタ情報を管理する.
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
 * A-PLOT出図 管理者設定マスタ情報管理クラス.
 * A-PLOT出図に必要な管理者設定マスタ情報を管理する.
 * 本クラスはシングルトンクラスとなっており、一度取得した情報は再起動するまで再取得されない.
 * DBを更新した場合は再起動が必要となる.
 *
 *
 * 【使用方法】
 *
 *    APlotSystemMasterDB data = APlotSystemMasterDB.getInstance();
 *    // Vaultsパスを取得.
 *    String path = data.getVaultsFolderPath();
 *
 * @author hideki_sugiyama
 */
public class APlotSystemMasterDB {

	/** Logger（log4j） */
	@SuppressWarnings("deprecation")
	private static Category category = Category.getInstance(APlotSystemMasterDB.class.getName());
	/** DB接続データソース */
	private static DataSource ds;
	static{
		try{
			ds = DataSourceFactory.getOracleDataSource();
		} catch(Exception e){
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
		}
	}

	/** 図面ファイルvaultsフォルダパス. */
	private String vaultsFolderPath = null;


	/** 図面ファイルスプール先フォルダパス. */
	private HashMap<Integer, String> spoolFolderPath = new HashMap<>();


	/** 図面ファイルスプール先フォルダパス. */
	private HashMap<Integer, String> documentFolderPath = new HashMap<>();


	/**
	 * スタンプ用設定クラス.
	 * 便宜上、内部クラスで構造体のように扱う.
	 * @author hideki_sugiyama
	 *
	 */
	public class APlotStampData {
		/** 刻印位置 X */
		public  String positionX = "";
		/** 刻印位置 Y */
		public  String positionY = "";
		/** 原点 */
		public  String origin = "";
		/** ID */
		public String id = "";
		/** バナーサイズ */
		public  String zoom = "";
		/** 文字濃淡 */
		public String color = "";
		/** 日付形式. */
		public String dateFormat = "";
		/** バナー文字列1. */
		public String stampText1 = "";
	}

	/** 該当図スタンプシステム設定. */
	private APlotStampData stamp1setting = new APlotStampData();

	/** 参考図スタンプシステム設定. */
	private APlotStampData stamp2setting = new APlotStampData();

	/**
	 * シングルトンクラス.
	 */
	public static APlotSystemMasterDB _instance = null;


	/**
	 * コンストラクタ.
	 * @throws SQLException
	 * @throws UserException
	 */
	private APlotSystemMasterDB() throws SQLException, UserException {
		// 初期化.
		init();
		//
		initCheck( );
	}

	/**
	 * シングルトンクラスの取得.
	 * @return インスタンス.
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
	 * システム設定テーブル(ADMIN_SETTING_MASTER)からAPLOTに必要な値を取得.
	 *
	 * 【取得値】
	 *   001:ViewDBドライブ名
	 *   800:スプール先フォルダ（OJ1）
	 *   801:スプール先フォルダ（OJ2）
	 *   805:ドキュメントフルパス（OJ1）
	 *   806:ドキュメントフルパス（OJ2）
	 *
	 *   参考図出力用スタンプ設定
	 *   810:参考図出力用スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
	 *   811:参考図出力用スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
	 *   812:参考図出力用スタンプ原点
	 *   813:参考図出力用スタンプID
	 *   814:参考図出力用スタンプスケーリング
	 *   815:参考図出力用スタンプ文字濃淡　0-100までの数値
	 *   816:参考図出力用スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
	 *   719:参考図出力用スタンプ文字列
	 *
	 *   参考図出力用該当図スタンプ設定
	 *   820:参考図出力用該当図スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
	 *   821:参考図出力用該当図スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
	 *   822:参考図出力用該当図スタンプ原点
	 *   823:参考図出力用該当図スタンプID
	 *   824:参考図出力用該当図スタンプスケーリング
	 *   825:参考図出力用該当図スタンプ文字濃淡　0-100までの数値
	 *   826:参考図出力用該当図スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
	 *   736:参考図出力用該当図スタンプ文字列
	 *
	 * @return sql文字列.
	 */
	private static String selectAPlotSystemValues() {
		//
		StringBuilder sb = new StringBuilder("");
		sb.append("'001'");   // ViewDBドライブ名
		sb.append(",'800'");  // スプール先フォルダ（OJ1）
		sb.append(",'801'");  // スプール先フォルダ（OJ2）
		sb.append(",'805'");  // ドキュメントフルパス（OJ1）
		sb.append(",'806'");  // ドキュメントフルパス（OJ2）
		// 参考図出力用スタンプ設定
		sb.append(",'810'"); // 参考図出力用スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
		sb.append(",'811'"); // 参考図出力用スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
		sb.append(",'812'"); // 参考図出力用スタンプ原点
		sb.append(",'813'"); // 参考図出力用スタンプID
		sb.append(",'814'"); // 参考図出力用スタンプスケーリング
		sb.append(",'815'"); // 参考図出力用スタンプ文字濃淡　0-100までの数値
		sb.append(",'816'"); // 参考図出力用スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
		sb.append(",'719'"); // 参考図出力用スタンプ文字列
		// 参考図出力用該当図スタンプ設定
		sb.append(",'820'"); // 参考図出力用該当図スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
		sb.append(",'821'"); // 参考図出力用該当図スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
		sb.append(",'822'"); // 参考図出力用該当図スタンプ原点
		sb.append(",'823'"); // 参考図出力用該当図スタンプID
		sb.append(",'824'"); // 参考図出力用該当図スタンプスケーリング
		sb.append(",'825'"); // 参考図出力用該当図スタンプ文字濃淡　0-100までの数値
		sb.append(",'826'"); // 参考図出力用該当図スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
		sb.append(",'736'"); // 参考図出力用該当図スタンプ文字列
		return String.format("SELECT * FROM ADMIN_SETTING_MASTER WHERE SETTING_ID in (%s) AND STATUS = 1", sb.toString());
	}

	/**
	 * 初期化.
	 * @throws SQLException
	 */
	private void init() throws SQLException {
		Connection conn = ds.getConnection();
		conn.setAutoCommit(true);// 非トランザクション
		Statement stmt = conn.createStatement();

		this.spoolFolderPath.clear();
		this.documentFolderPath.clear();

		// システム値を取得.
		ResultSet rs = stmt.executeQuery(selectAPlotSystemValues());
		try {
			// 取得した情報からスキーマ名を取得.
			while(rs.next()){

				String id = rs.getString("SETTING_ID");
				if ( "001".equals(id) ) {
					// 001: ViewDBドライブ名 の場合
					this.vaultsFolderPath = rs.getString("VALUE");
				} else if ( "800".equals(id) ) {
					// 800: スプール先フォルダ（OJ1）
					this.spoolFolderPath.put(new Integer(1), rs.getString("VALUE"));
				} else if ( "801".equals(id) ) {
					// 801: スプール先フォルダ（OJ2）
					this.spoolFolderPath.put(new Integer(2), rs.getString("VALUE"));
				} else if ( "805".equals(id) ) {
					// 805: ドキュメントフルパス（OJ1）
					this.documentFolderPath.put(new Integer(1), rs.getString("VALUE"));
				} else if ( "806".equals(id) ) {
					// 806: ドキュメントフルパス（OJ2）
					this.documentFolderPath.put(new Integer(2), rs.getString("VALUE"));

				} else if ( "810".equals(id) ) { // 参考図出力用スタンプ設定
					// 参考図出力用スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
					this.stamp2setting.positionX = rs.getString("VALUE");
				} else if ( "811".equals(id) ) {
					// 参考図出力用スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
					this.stamp2setting.positionY = rs.getString("VALUE");
				} else if ( "812".equals(id) ) {
					// 参考図出力用スタンプ原点
					this.stamp2setting.origin = rs.getString("VALUE");
				} else if ( "813".equals(id) ) {
					// 参考図出力用スタンプID
					this.stamp2setting.id = rs.getString("VALUE");
				} else if ( "814".equals(id) ) {
					// 参考図出力用スタンプスケーリング
					this.stamp2setting.zoom = rs.getString("VALUE");
				} else if ( "815".equals(id) ) {
					// 参考図出力用スタンプ文字濃淡　0-100までの数値
					this.stamp2setting.color = rs.getString("VALUE");
				} else if ( "816".equals(id) ) {
					// 参考図出力用スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
					if ( "0".equals(rs.getString("VALUE")) ) {
						this.stamp2setting.dateFormat = "yyyy/MM/dd";
					} else if ( "1".equals(rs.getString("VALUE")) ) {
						this.stamp2setting.dateFormat = "yy/MM/dd";
					}
				} else if ( "719".equals(id) ) {
					// 参考図出力用スタンプ文字列
					this.stamp2setting.stampText1 = rs.getString("VALUE");

				} else if ( "820".equals(id) ) { // 参考図出力用該当図スタンプ設定
					// 参考図出力用該当図スタンプ位置(X方向) 用紙の右下原点からの横方向の距離(mm)
					this.stamp1setting.positionX = rs.getString("VALUE");
				} else if ( "821".equals(id) ) {
					// 参考図出力用該当図スタンプ位置(Y方向) 用紙の右下原点からの縦方向の距離(mm)
					this.stamp1setting.positionY = rs.getString("VALUE");
				} else if ( "822".equals(id) ) {
					// 参考図出力用該当図スタンプ原点
					this.stamp1setting.origin = rs.getString("VALUE");
				} else if ( "823".equals(id) ) {
					// 参考図出力用該当図スタンプID
					this.stamp1setting.id = rs.getString("VALUE");
				} else if ( "824".equals(id) ) {
					// 参考図出力用該当図スタンプスケーリング
					this.stamp1setting.zoom = rs.getString("VALUE");
				} else if ( "825".equals(id) ) {
					// 参考図出力用該当図スタンプ文字濃淡　0-100までの数値
					this.stamp1setting.color = rs.getString("VALUE");
				} else if ( "826".equals(id) ) {
					// 参考図出力用該当図スタンプ日付形式　0:YYYY/MM/DD、1:YY/MM/DD
					if ( "0".equals(rs.getString("VALUE")) ) {
						this.stamp1setting.dateFormat = "yyyy/MM/dd";
					} else if ( "1".equals(rs.getString("VALUE")) ) {
						this.stamp1setting.dateFormat = "yy/MM/dd";
					}
				} else if ( "736".equals(id) ) {
					// 参考図出力用該当図スタンプ文字列
					this.stamp1setting.stampText1 = rs.getString("VALUE");
				}
			}
		} catch (SQLException ex) {
			category.fatal("A-PLOT出図 管理者設定マスタ情報取得でSQLエラー", ex);
			throw ex;
		} finally {
			if ( rs != null ) rs.close();
		}

		//
	}

	/**
	 * 念のため必須の環境設定がされているか確認.
	 * @throws UserException
	 */
	private void initCheck( ) throws UserException {
		if ( this.vaultsFolderPath == null || "".equals(this.vaultsFolderPath) ) {
			throw new UserException("管理者設定マスタに[ViewDBドライブ名]が設定されていません.");
		}
		if ( this.spoolFolderPath.size() == 0 ) {
			throw new UserException("管理者設定マスタに[スプール先フォルダ]が設定されていません.");
		}
		if ( this.documentFolderPath.size() == 0 ) {
			throw new UserException("管理者設定マスタに[ドキュメントフルパス]が設定されていません.");
		}
		if ( "".equals(this.stamp1setting.id) ) {
			throw new UserException("管理者設定マスタに[参考図出力用スタンプID]が設定されていません.");
		}
		if ( "".equals(this.stamp2setting.id) ) {
			throw new UserException("管理者設定マスタに[参考図出力用該当図スタンプID]が設定されていません.");
		}

	}

	/**
	 * 図面ファイルの格納フォルダパスを取得.
	 * @return 図面ファイルの格納フォルダパス.
	 */
	public String getVaultsFolderPath() {
		return this.vaultsFolderPath;
	}

	/**
	 * 図面ファイルのスプール先フォルダパスを取得.
	 * @return 図面ファイルのスプール先フォルダパス.
	 */
	public String getSpoolFolderPath(int schemaNo) {
		return this.spoolFolderPath.get(new Integer(schemaNo));
	}

	/**
	 * 図面ファイルのフルパスを取得.
	 * @return 図面ファイルのフルパス.
	 */
	public String getDocumentFolderPath(int schemaNo) {
		return this.documentFolderPath.get(new Integer(schemaNo));
	}


	/**
	 * スタンプ1の設定情報取得.
	 * @return スタンプ1の設定情報.
	 */
	public APlotStampData getStamp1Data() {
		return this.stamp1setting;
	}

	/**
	 * スタンプ1の設定情報取得.
	 * @return スタンプ1の設定情報.
	 */
	public APlotStampData getStamp2Data() {
		return this.stamp2setting;
	}


}
