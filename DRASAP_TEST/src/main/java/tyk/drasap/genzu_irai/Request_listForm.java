package tyk.drasap.genzu_irai;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import tyk.drasap.springfw.form.BaseForm;

/**
 * ���}�ɍ�ƈ˗����X�g�̃t�H�[��
 */
public class Request_listForm extends BaseForm {
	/**
	 *
	 */
	String action;//�A�N�V����
	String time;//(���}�ɍ�ƈ˗����X�g�Ŏ��ԕ\������)
	ArrayList<Request_listElement> iraiList = new ArrayList<Request_listElement>();//���}�ɍ�ƈ˗����X�g
	ArrayList<String> listErrors = new ArrayList<String>();//�G���[���X�g
	ArrayList<String> checkKeyList = new ArrayList<String>();//�`�F�b�N���ڂ̃L�[
	ArrayList<String> checkNameList = new ArrayList<String>();//�`�F�b�N���ڂ̃��X�g
	//ArrayList messageKeyList = new ArrayList();//���b�Z�[�W���ڂ̃L�[
	ArrayList<String> messageNameList = new ArrayList<String>();//���b�Z�[�W���ڂ̃��X�g
	ArrayList<RequestPriElement> printList = new ArrayList<RequestPriElement>();//�����ʗp�̃��X�g

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * Returns the results.
	 * @return ArrayList
	 */
	public ArrayList<Request_listElement> getIraiList() {
		return iraiList;
	}

	/**
	 * Sets the results.
	 * @param results The results to set
	 */
	public void setIraiList(ArrayList<Request_listElement> iraiList) {
		this.iraiList = iraiList;
	}

	public ArrayList<String> getListErrors() {
		return listErrors;
	}

	public void setListErrors(ArrayList<String> listErrors) {
		this.listErrors = listErrors;
	}

	public ArrayList<String> getCheckKeyList() {
		return checkKeyList;
	}

	public void setCheckKeyList(ArrayList<String> checkKeyList) {
		this.checkKeyList = checkKeyList;
	}

	public ArrayList<String> getCheckNameList() {
		return checkNameList;
	}

	public void setCheckNameList(ArrayList<String> checkNameList) {
		this.checkNameList = checkNameList;
	}

	//    public ArrayList getMessageKeyList() {
	//        return messageKeyList;
	//    }
	//
	//    public void setMessageKeyList(ArrayList messageKeyList) {
	//        this.messageKeyList = messageKeyList;
	//    }

	public ArrayList<String> getMessageNameList() {
		return messageNameList;
	}

	public void setMessageNameList(ArrayList<String> messageNameList) {
		this.messageNameList = messageNameList;
	}

	public ArrayList<RequestPriElement> getPrintList() {
		return printList;
	}

	public void setPrintList(ArrayList<RequestPriElement> printList) {
		this.printList = printList;
	}

	/**
	 * Returns the item.
	 * @return EcPartsSearch
	 */
	public Request_listElement getItem(int index) {
		if (iraiList.isEmpty()) {
			// �Z�b�V�����^�C���A�E�g����Exception�����̉����
			return new Request_listElement(
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
		return iraiList.get(index);

	}

	public void reset(HttpServletRequest request) {
		action = "";
	}

}
