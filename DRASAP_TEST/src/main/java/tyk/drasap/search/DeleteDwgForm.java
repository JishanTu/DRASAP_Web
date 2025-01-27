package tyk.drasap.search;

import java.util.ArrayList;

import tyk.drasap.springfw.form.BaseForm;

/**
 * çÌèúâÊñ Ç…ëŒâû
 */
public class DeleteDwgForm extends BaseForm {
	/**
	 *
	 */
	String act;// èàóùÇï™ÇØÇÈÇΩÇﬂÇÃëÆê´
	ArrayList<String> colNameList = new ArrayList<String>();//
	ArrayList<String> colNameJPList = new ArrayList<String>();//
	ArrayList<DeleteDwgElement> recList = new ArrayList<DeleteDwgElement>();// DeleteDwgElement
	ArrayList<DeleteDwgFileInfo> fileList = new ArrayList<DeleteDwgFileInfo>();// DeleteDwgFileInfo
	boolean deleteOK = false;
	String previewIdx = "0";
	String msg1 = "";
	String msg2 = "";

	// --------------------------------------------------------- Methods
	// --------------------------------------------------------- getter,setter
	public DeleteDwgForm() {
		act = "";
	}

	/**
	 * @return
	 */
	public String getAct() {
		return act;
	}

	/**
	 * @param string
	 */
	public void setAct(String string) {
		act = string;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getColNameList() {
		return colNameList;
	}

	/**
	 * @param list
	 */
	public void setColNameList(ArrayList<String> list) {
		colNameList = list;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getColNameJPList() {
		return colNameJPList;
	}

	/**
	 * @param list
	 */
	public void setColNameJPList(ArrayList<String> list) {
		colNameJPList = list;
	}

	/**
	 * @return
	 */
	public DeleteDwgElement getRecList(int index) {
		return recList.get(index);
	}

	/**
	 * @return
	 */
	public String getRecvalue(int index, String colname) {
		for (int i = 0; i < colNameList.size(); i++) {
			if (colNameList.get(i).toString().equals(colname)) {
				return recList.get(index).getVal(i);
			}
		}
		return "";
	}

	/**
	 * @return
	 */
	public DeleteDwgElement getRecList(Integer index) {
		return recList.get(index.intValue());
	}

	/**
	 * @return
	 */
	public ArrayList<DeleteDwgElement> getRecList() {
		return recList;
	}

	/**
	 * @param list
	 */
	public void setRecList(ArrayList<DeleteDwgElement> list) {
		recList = list;
	}

	/**
	 * @param list
	 */
	public void addRecList(DeleteDwgElement newObj) {
		recList.add(newObj);
	}

	/**
	 * @return
	 */
	public boolean isDeleteOK() {
		return deleteOK;
	}

	/**
	 * @return
	 */
	public void setDeleteOK(boolean flg) {
		deleteOK = flg;
	}

	/**
	 * @return Returns the previewIdx.
	 */
	public String getPreviewIdx() {
		return previewIdx;
	}

	/**
	 * @param previewIdx The previewIdx to set.
	 */
	public void setPreviewIdx(String previewIdx) {
		this.previewIdx = previewIdx;
	}

	public String getMsg1() {
		return msg1;
	}

	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}

	public String getMsg2() {
		return msg2;
	}

	public void setMsg2(String msg2) {
		this.msg2 = msg2;
	}

	/**
	 * @return
	 */
	public DeleteDwgFileInfo getFileList(int index) {
		return fileList.get(index);
	}

	/**
	 * @return
	 */
	public DeleteDwgFileInfo getFileList(Integer index) {
		return fileList.get(index.intValue());
	}

	/**
	 * @return
	 */
	public ArrayList<DeleteDwgFileInfo> getFileList() {
		return fileList;
	}

	/**
	 * @param list
	 */
	public void setFileList(ArrayList<DeleteDwgFileInfo> list) {
		fileList = list;
	}

	/**
	 * @param list
	 */
	public void addFileList(DeleteDwgFileInfo newObj) {
		if (newObj != null) {
			fileList.add(newObj);
		}
	}
}
