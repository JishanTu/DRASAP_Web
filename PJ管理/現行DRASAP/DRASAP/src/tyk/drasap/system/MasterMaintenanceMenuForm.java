package tyk.drasap.system;


import org.apache.struts.action.ActionForm;

/**
 * �}�X�^�[�����e�i���X���j���[��ʂɑΉ�
 */
public class MasterMaintenanceMenuForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2248665929277142616L;
	String act;// �����𕪂��邽�߂̑���

	// --------------------------------------------------------- Methods

	// --------------------------------------------------------- getter,setter
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

}
