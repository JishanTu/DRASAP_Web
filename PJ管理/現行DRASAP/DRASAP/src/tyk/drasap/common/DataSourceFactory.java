package tyk.drasap.common;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * DataSourceのFactoryクラス。
 */
public class DataSourceFactory {
	static DataSource ds = null;
//	static int portNo = 8080;
	// ---------------------------------------------------- method
	/**
	 * 最初の1度だけインスタンス化して、以降は同じインスタンスを返す。
	 */
	public static DataSource getOracleDataSource() throws Exception {
//		return getOracleDataSource(portNo);
//	}
//	public static DataSource getOracleDataSource(int iPortNo) throws Exception {
//		portNo = iPortNo;
//		Map<String, String> envMap = System.getenv();
		if(ds == null){
			Properties appProp = DrasapPropertiesFactory.getDrasapProperties(
									new DataSourceFactory()); 
			if("tomcat".equals(appProp.getProperty("web.container"))){
				// Tomcatの場合
				Context initContext = new InitialContext();
				Context envContext  = (Context)initContext.lookup("java:/comp/env");
//				ds = (DataSource)envContext.lookup("jdbc/drasap_oracle");
				ds = (DataSource)envContext.lookup(appProp.getProperty("oracle.jdbc.name"));
				
			} else {
//				String serverport = Integer.toString(portNo);
				// weblogicの場合
				Context ctx = null;
				// 接続のプロパティをハッシュテーブルに格納する。
				Hashtable<String, String> ht = new Hashtable<String, String>();
				ht.put(Context.INITIAL_CONTEXT_FACTORY,
						"weblogic.jndi.WLInitialContextFactory");
				//ht.put(Context.PROVIDER_URL, "t3://localhost:7001");
//				ht.put(Context.PROVIDER_URL, "t3://localhost:8081");
	 			ht.put(Context.PROVIDER_URL, "t3://localhost:" + appProp.getProperty("web.serverport"));
//	 			ht.put(Context.PROVIDER_URL, "t3://localhost:" + serverport);
				//JNDI ルックアップに対するコンテキストを取得する。
				ctx = new InitialContext(ht);
//				ds = (javax.sql.DataSource) ctx.lookup ("jdbc/drasap_oracle");
				ds = (javax.sql.DataSource) ctx.lookup (appProp.getProperty("oracle.jdbc.name"));
			}
			
		}
		return ds;
	}

}
