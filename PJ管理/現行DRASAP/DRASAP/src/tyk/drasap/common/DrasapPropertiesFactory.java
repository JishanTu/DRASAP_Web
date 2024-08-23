package tyk.drasap.common;

import java.util.Properties;

/**
 * このシステムのプロパティであるdrasap.propertiesを読み込んで返す。
 * 最初の1度だけ読み込む。
 *
 * @version 2013/06/13 yamagishi
 */
public class DrasapPropertiesFactory {
	private static Properties drasapProp = null;
	private final static java.lang.String TARGET_FILE_NAME = "drasap.properties";
// 2013.06/13 yamagishi add. start
	public static final String BEA_HOME = "BEA_HOME";
	public static final String CATALINA_HOME = "CATALINA_HOME";
	public static final String OCE_AP_SERVER_HOME = "OCE_AP_SERVER_HOME";
	public static final String OCE_AP_SERVER_BASE = "oce.AP_SERVER_BASE";
// 2013.06/13 yamagishi add. end

	// ------------------------------------------------- Method
	/**
	 * クラスローダーを使用して、このシステムのプロパティであるdrasap.propertiesを読み込んで返す。
	 * 最初の1度だけ読み込む。
	 * @param obj このオブジェクトのクラスローダーを使用する
	 */
	public static Properties getDrasapProperties(Object obj){
		if(drasapProp == null){
			try{
				drasapProp = new Properties();
				drasapProp.load(obj.getClass().getClassLoader().getResourceAsStream(TARGET_FILE_NAME));
			} catch (Exception e){
				// 例外が発生したら、nullをセットする
				drasapProp = null;
			}
		}
		return drasapProp;
	}
	/**
	 * 指定したオブジェクトの画面IDを取得する。
	 * @param screenObj
	 * @return 画面ID
	 */
	public static String getScreenId(Object screenObj){
		// システムのプロパティを取得して、
		// オブジェクトの完全クラス名をキーにして、画面IDを取得
		return getDrasapProperties(screenObj).
					getProperty(screenObj.getClass().getName());
	}

}
