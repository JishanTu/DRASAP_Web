package tyk.drasap.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//public class CsvFile {
public class CsvFile {
	public String errorString;
//	private File f = null;
	private BufferedReader reader = null;
	public CsvFile() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public boolean open(String fName) throws FileNotFoundException {
//		f = new File(fName);
//		if(!f.exists()){
//			return false;
//		}
		try {
			reader = new BufferedReader(new FileReader(fName));
		} catch (FileNotFoundException e) {
			try {if (reader != null) reader.close();} catch (IOException e1) {}
			throw e;
		}
		return true;
	}
	public String getLine() throws IOException {
		String lineData = null;

		try {
			if ((lineData = reader.readLine()) == null) {
				return null;
			}
		} catch (IOException e) {
			try {reader.close();} catch (IOException e1) {}
			throw e;
		}
		return lineData;
	}
	public ArrayList<String> getDataItem(String lineData) {
		if (lineData == null) return null;

		ArrayList<String> dataItem = new ArrayList<String>();
		String arrayElm = "";
		int sts = 0;
		char[] linebuf = lineData.toCharArray();
		for (int i = 0; i < linebuf.length; i++) {
			switch (sts) {
			case 0:
				switch (linebuf[i]) {
				case ' ':
					arrayElm += linebuf[i];
					sts = 1;
					break;
				case '"':
					arrayElm += linebuf[i];
					sts = 2;
					break;
				case ',':
					arrayElm = arrayElm.trim();
					arrayElm = this.trim(arrayElm,'"');
					dataItem.add(arrayElm);
					arrayElm = "";
					sts = 0;
					break;
				default:
					arrayElm += linebuf[i];
					sts = 1;
					break;
				}
				break;
			case 1:
				switch (linebuf[i]) {
				case ' ':
					arrayElm += linebuf[i];
					sts = 1;
					break;
				case '"':
					arrayElm += linebuf[i];
					sts = 1;
					break;
				case ',':
					arrayElm = arrayElm.trim();
					arrayElm = this.trim(arrayElm,'"');
					dataItem.add(arrayElm);
					arrayElm = "";
					sts = 0;
					break;
				default:
					arrayElm += linebuf[i];
					sts = 1;
					break;
				}
				break;
			case 2:
				switch (linebuf[i]) {
				case ' ':
					arrayElm += linebuf[i];
					sts = 2;
					break;
				case '"':
					arrayElm += linebuf[i];
					sts = 0;
					break;
				case ',':
					arrayElm += linebuf[i];
					sts = 2;
					break;
				default:
					arrayElm += linebuf[i];
					sts = 2;
					break;
				}
				break;
			default:
				break;

			}

		}
		arrayElm = arrayElm.trim();
		arrayElm = this.trim(arrayElm,'"');
		dataItem.add(arrayElm);
		return dataItem;
	}
	public void close() {
		try {if (reader != null) reader.close();} catch (IOException e1) {}

	}
    public String trim(String str, char ch) {
    	int len = str.length();
    	int st = 0;
    	int off = 0;      /* avoid getfield opcode */
    	char[] val = str.toCharArray();    /* avoid getfield opcode */

    	while ((st < len) && (val[off + st] <= ch)) {
    	    st++;
    	}
    	while ((st < len) && (val[off + len - 1] <= ch)) {
    	    len--;
    	}
    	return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
        }
}
