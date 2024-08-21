package tyk.drasap.search;

import org.apache.struts.util.MessageResources;

/**
 * DL�}�l�[�W���̃��N�G�X�g����ێ�����N���X
 *
 * @author 2013/08/02 yamagishi
 */
public class DLManagerInfo {

	String act = null;
	String labelMessage = null;
	MessageResources resources = null;

	/** constructor */
	public DLManagerInfo(String act, MessageResources resources) {
		this.act = act;
		this.resources = resources;
	}

	/**
	 * act���擾���܂��B
	 * @return act
	 */
	public String getAct() {
		return act;
	}

	/**
	 * labelMessage���擾���܂��B
	 * @return labelMessage
	 */
	public String getLabelMessage() {
		return labelMessage;
	}
	/**
	 * labelMessage��ݒ肵�܂��B
	 * @param labelMessage labelMessage
	 */
	public void setLabelMessage(String labelMessage) {
		this.labelMessage = labelMessage;
	}

	/**
	 * �J���{�^���������̃A�N�V����������B
	 * @return true/false
	 */
	public boolean isActOpen() {
		return "open".equals(act);
	}
	/**
	 * �ۑ��{�^���������̃A�N�V����������B
	 * @return true/false
	 */
	public boolean isActSave() {
		return "save".equals(act);
	}
	/**
	 * DL�}�l�[�W���G���[�������̃A�N�V����������B
	 * @return true/false
	 */
	public boolean isActError() {
		return "error".equals(act);
	}

	/**
	 * @see MessageResources.getMessage(String key)
	 */
	public String getMessage(String key) {
		return resources.getMessage(key);
	}
	/**
	 * @see MessageResources.getMessage(String key Object arg0)
	 */
	public String getMessage(String key, Object arg0) {
		return resources.getMessage(key, arg0);
	}
}
