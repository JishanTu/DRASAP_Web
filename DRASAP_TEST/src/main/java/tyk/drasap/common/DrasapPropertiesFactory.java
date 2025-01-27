package tyk.drasap.common;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * このシステムのプロパティであるdrasap.propertiesを読み込んで返す。
 * 最初の1度だけ読み込む。
 *
 * @version 2013/06/13 yamagishi
 */
@Component
public class DrasapPropertiesFactory {
	private static Properties drasapProp = null;
	private final static java.lang.String TARGET_FILE_NAME = "drasap.properties";
	// 2013.06/13 yamagishi add. start
	public static final String BEA_HOME = "BEA_HOME";
	public static final String CATALINA_HOME = "CATALINA_HOME";
	public static final String OCE_AP_SERVER_HOME = "OCE_AP_SERVER_HOME";
	public static final String OCE_AP_SERVER_BASE = "oce.AP_SERVER_BASE";
	// 2013.06/13 yamagishi add. end

	private static String serverHomePath = null;

	public DrasapPropertiesFactory() {
		super();
		try {
			if (serverHomePath == null) {
				serverHomePath = System.getenv(BEA_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = System.getenv(CATALINA_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = System.getenv(OCE_AP_SERVER_HOME);
			}
			if (serverHomePath == null) {
				serverHomePath = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(OCE_AP_SERVER_BASE);
			}
		} catch (Exception e) {
			return;
		}
	}

	// ------------------------------------------------- Method
	/**
	 * クラスローダーを使用して、このシステムのプロパティであるdrasap.propertiesを読み込んで返す。
	 * 最初の1度だけ読み込む。
	 * @param obj このオブジェクトのクラスローダーを使用する
	 */
	public static Properties getDrasapProperties(Object obj) {
		if (drasapProp == null) {
			try {
				drasapProp = new Properties();
				drasapProp.load(obj.getClass().getClassLoader().getResourceAsStream(TARGET_FILE_NAME));
			} catch (Exception e) {
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
	public static String getScreenId(Object screenObj) {
		// システムのプロパティを取得して、
		// オブジェクトの完全クラス名をキーにして、画面IDを取得
		return getDrasapProperties(screenObj).getProperty(screenObj.getClass().getName());
	}

	public static String getFullPath(String key) {
		if (StringUtils.isBlank(key)) {
			return serverHomePath;
		}
		return serverHomePath + DrasapPropertiesFactory.getDrasapProperties(new DrasapPropertiesFactory()).getProperty(key);
	}
}
