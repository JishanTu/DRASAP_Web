package tyk.drasap.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProfileString {
	private Properties prop;
	private Object parent;
	public ProfileString(Object obj, String iniFileName) throws IOException, FileNotFoundException {
		super();

//		FileInputStream fis = new FileInputStream(iniFileName);
		this.parent = obj;
		prop = new Properties();
		InputStream fis = this.parent.getClass().getClassLoader().getResourceAsStream(iniFileName);
		if (fis == null) {
			throw new FileNotFoundException();
		}
		prop.load(fis);
	}
    public String getValue(String key) {
    	return prop.getProperty(key,"");
    }
    public String getValue(String key, String arg0) {
    	return DrasapUtil.createMessage(prop.getProperty(key,""), arg0);
    }
	public String getValue(String key, String arg0, String arg1){
		return DrasapUtil.createMessage(prop.getProperty(key,""), arg0, arg1);
	}
	public String getValue(String key, String arg0, String arg1, String arg2){
		return DrasapUtil.createMessage(prop.getProperty(key,""), arg0, arg1, arg2);
	}
	public String getValue(String key, String arg0, String arg1, String arg2, String arg3){
		return DrasapUtil.createMessage(prop.getProperty(key,""), arg0, arg1, arg2, arg3);
	}
//	public static String getValue(String msg, String arg0, String arg1, String arg2, String arg3){
//		if(msg == null){
//			return "";
//		}
//		if(arg0 != null){
//			int index = msg.indexOf("{0}");
//			if(index > -1){
//				msg = msg.substring(0, index) + arg0 + msg.substring(index+3);
//			}
//		}
//		if(arg1 != null){
//			int index = msg.indexOf("{1}");
//			if(index > -1){
//				msg = msg.substring(0, index) + arg1 + msg.substring(index+3);
//			}
//		}
//		if(arg2 != null){
//			int index = msg.indexOf("{2}");
//			if(index > -1){
//				msg = msg.substring(0, index) + arg2 + msg.substring(index+3);
//			}
//		}
//		if(arg3 != null){
//			int index = msg.indexOf("{3}");
//			if(index > -1){
//				msg = msg.substring(0, index) + arg3 + msg.substring(index+3);
//			}
//		}
//		return msg;
//	}
}
