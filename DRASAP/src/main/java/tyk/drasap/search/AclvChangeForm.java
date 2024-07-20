package tyk.drasap.search;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author fumi
 * �쐬��: 2004/01/20
 * @version 2013/07/25 yamagishi
 */
public class AclvChangeForm extends ActionForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String act;// �����𕪂��邽�߂�
	ArrayList aclvChangeList = new ArrayList();// �A�N�Z�X���x���̕ύX�Ώۂ��i�[����B������AclvChangeElement�B
	ArrayList aclvNotChangeList = new ArrayList();// �I�����Ă������A�ύX�ł��Ȃ��}�ʂ��i�[����B
												//�����͐}��(�t�H�[�}�b�g�ς݂�)
	ArrayList aclvNameList = new ArrayList();// �A�N�Z�X���x���̃v���_�E���̖��̃��X�g
	ArrayList aclvKeyList = new ArrayList();// �A�N�Z�X���x���̃v���_�E����Key���X�g
	ArrayList errorMessages = new ArrayList();// �G���[���b�Z�[�W�̕\��
// 2013.07.25 yamagishi add. start
	HashMap<String, String> aclMap = new HashMap<String, String>();
// 2013.07.25 yamagishi add. end

	// ------------------------------------------------------------------- method
	/* (�� Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// act�������ABACK_INPUT�܂���CONFIRMED���w�肳�ꂽ�Ƃ�
		// �܂�E�E�E�m�F��ʂ���߂�Ƃ��́A
		// �`�F�b�N�{�b�N�X�����������Ȃ�
		// ����: reset���Ăяo���ꂽ���_�ł́A�܂�act�ɐݒ肳��Ă��Ȃ����߁A
		// 			request����p�����[�^�Ƃ��Ď擾���āA���肵�Ă���B
		if(! "BACK_INPUT".equals(request.getParameter("act")) && ! "CONFIRMED".equals(request.getParameter("act"))){

			// �`�F�b�N�{�b�N�X�̏�����
			for(int i = 0; i < aclvChangeList.size(); i++){
				getAclvChangeElement(i).setSelected(false);
			}
		}
	}
	/**
	 * iterate�^�O�ɑΉ������邽�߂̃��\�b�h
	 * @param index
	 * @return
	 */
	public AclvChangeElement getAclvChangeElement(int index){
		return (AclvChangeElement) this.aclvChangeList.get(index);
	}
	// ------------------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public ArrayList getAclvChangeList() {
		return aclvChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvNotChangeList() {
		return aclvNotChangeList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvKeyList() {
		return aclvKeyList;
	}

	/**
	 * @return
	 */
	public ArrayList getAclvNameList() {
		return aclvNameList;
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
	public ArrayList getErrorMessages() {
		return errorMessages;
	}

// 2013.07.25 yamagishi add. start
	/**
	 * @return aclMap
	 */
	public HashMap<String, String> getAclMap() {
		return aclMap;
	}
// 2013.07.25 yamagishi add. end
}
