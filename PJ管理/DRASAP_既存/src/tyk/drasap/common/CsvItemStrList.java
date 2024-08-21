package tyk.drasap.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CsvItemStrList {
	private CsvFile cf = new CsvFile();
	public ArrayList<ArrayList<String>> dataArray = new ArrayList<ArrayList<String>>();
	public CsvItemStrList(String fName) throws FileNotFoundException, IOException {
		this.cf.open(fName);
		String lineData = null;
		while ((lineData = this.cf.getLine()) != null) {
			dataArray.add(this.cf.getDataItem(lineData));
		}
		cf.close();
	}
	public ArrayList<String> getLineData(int line) {
		if (line >= dataArray.size()) {
			return null;
		}
		return dataArray.get(line);
	}
	public ArrayList<String> searchLineData(String key) {
		if (key == null) return null;
		for (int i = 0; i < dataArray.size(); i++) {
			ArrayList<String> lineData =  dataArray.get(i);
			if (lineData.get(0).equals(key)) {
				return lineData;
			}
		}
		return null;
	}
}
