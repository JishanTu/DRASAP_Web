package tyk.drasap.search;

import org.springframework.context.MessageSource;

/**
 * DL�}�l�[�W���̃��N�G�X�g����ێ�����N���X
 *
 * @author 2013/08/02 yamagishi
 */
public class DLManagerInfo {

	String act = null;
	String labelMessage = null;
	MessageSource resources = null;

	/** constructor */
	public DLManagerInfo(String act, MessageSource resources) {
		this.act = act;
		this.resources = resources;
	}

	/**
	 * act���擾���܂��B
	 * @return act
	 */
	public String getAct() {
		return this.act;
	}

	/**
	 * labelMessage���擾���܂��B
	 * @return labelMessage
	 */
	public String getLabelMessage() {
		return this.labelMessage;
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
		return "open".equals(this.act);
	}

	/**
	 * �ۑ��{�^���������̃A�N�V����������B
	 * @return true/false
	 */
	public boolean isActSave() {
		return "save".equals(this.act);
	}

	/**
	 * DL�}�l�[�W���G���[�������̃A�N�V����������B
	 * @return true/false
	 */
	public boolean isActError() {
		return "error".equals(this.act);
	}

	/**
	 * @see MessageResources.getMessage(String key)
	 */
	public String getMessage(String key) {
		return this.resources.getMessage(key, null, null);
	}

	/**
	 * @see MessageResources.getMessage(String key Object arg0)
	 */
	public String getMessage(String key, Object arg0) {
		return this.resources.getMessage(key, new Object[] { arg0 }, null);
	}
}
