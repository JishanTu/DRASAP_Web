package tyk.drasap.search;

import org.apache.struts.action.ActionForm;

/**
 * �������ʂ���A�o�͎w���������Ƃ��ɁATiff�ȊO��I�����Ă����Ƃ��ɁA
 * �m�F����A�N�V�����ɑΉ������t�H�[��
 * @author fumi
 * �쐬��: 2004/01/19
 */
public class SearchWarningNotPrintableForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String act;
	// ----------------------------------------------- getter, setter
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
