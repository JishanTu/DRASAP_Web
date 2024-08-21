package tyk.drasap.common;

import java.io.IOException;
import java.util.Properties;

/**
 * メッセージを管理するクラス。
 * ActionErrorを使用できない場合、フレームを経由したパターンなどで使用。
 * @author fumi
 * 作成日: 2004/01/23
 */
public class MessageManager {
	private static MessageManager manager = null;
	private static Properties messageProp = null;
	private final static java.lang.String TARGET_FILE_NAME = "message.properties";

	/**
	 *
	 */
	protected MessageManager() {
	}
	/**
	 * インスタンスを返す、唯一のメソッド
	 * @return
	 */
	public static MessageManager getInstance(){
		// インスタンス化されていなければ
		if(manager == null){
			manager = new MessageManager();
			messageProp = new Properties();
			try{
				messageProp.load(manager.getClass().getClassLoader().getResourceAsStream(TARGET_FILE_NAME));
			} catch(IOException e){
				messageProp = null;
			}
		}
		return manager;
	}
	/**
	 * メッセージを返す。リプレースホルダ{0},{1},{2},{3}を使用する
	 * @param key
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return
	 */
	public String getMessage(String key, String arg0, String arg1, String arg2, String arg3){
		String msg = messageProp.getProperty(key);
		if(msg == null){
			return "";
		}
		if(arg0 != null){
			int index = msg.indexOf("{0}");
			if(index > -1){
				msg = msg.substring(0, index) + arg0 + msg.substring(index+3);
			}
		}
		if(arg1 != null){
			int index = msg.indexOf("{1}");
			if(index > -1){
				msg = msg.substring(0, index) + arg1 + msg.substring(index+3);
			}
		}
		if(arg2 != null){
			int index = msg.indexOf("{2}");
			if(index > -1){
				msg = msg.substring(0, index) + arg2 + msg.substring(index+3);
			}
		}
		if(arg3 != null){
			int index = msg.indexOf("{3}");
			if(index > -1){
				msg = msg.substring(0, index) + arg3 + msg.substring(index+3);
			}
		}
		return msg;
	}
	/**
	 * メッセージを返す。リプレースホルダ{0},{1},{2}を使用する
	 * @param key
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public String getMessage(String key, String arg0, String arg1, String arg2){
		return getMessage(key, arg0, arg1, arg2, null);
	}
	/**
	 * メッセージを返す。リプレースホルダ{0},{1}を使用する
	 * @param key
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public String getMessage(String key, String arg0, String arg1){
		return getMessage(key, arg0, arg1, null, null);
	}
	/**
	 * メッセージを返す。リプレースホルダ{0}を使用する
	 * @param key
	 * @param arg0
	 * @return
	 */
	public String getMessage(String key, String arg0){
		return getMessage(key, arg0, null, null, null);
	}
	/**
	 * メッセージを返す。リプレースホルダは使用しない
	 * @param key
	 * @return
	 */
	public String getMessage(String key){
		return getMessage(key, null, null, null, null);
	}

}
