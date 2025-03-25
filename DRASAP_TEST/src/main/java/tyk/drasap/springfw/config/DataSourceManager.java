package tyk.drasap.springfw.config;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.ErrorUtility;

public class DataSourceManager {
	/** Logger（log4j） */
	private static final Logger category = Logger.getLogger(DataSourceManager.class.getName());

	/** DataSource */
	private static volatile DataSource ds;

	/** ロック用オブジェクト */
	private static final Object lock = new Object();

	private DataSourceManager() {
		// private コンストラクタでインスタンス化防止
	}

	public static DataSource getInstance() {
		if (ds == null) {
			DataSource newDs = createDataSource();
			synchronized (lock) {
				if (ds == null) {
					ds = newDs;
				}
			}
		}
		return ds;
	}

	private static DataSource createDataSource() {
		try {
			DataSource newDs = DataSourceFactory.getOracleDataSource();
			if (newDs == null) {
				category.error("DataSourceがnullのままです。");
				throw new RuntimeException("DataSourceがnullのままです。");
			}
			return newDs;
		} catch (Exception e) {
			category.error("DataSourceの取得に失敗\n" + ErrorUtility.error2String(e));
			throw new RuntimeException("DataSourceの取得に失敗", e);
		}
	}
}
