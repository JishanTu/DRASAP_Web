package tyk.drasap.search;

import org.apache.struts.util.MessageResources;

/**
 * DLマネージャのリクエスト情報を保持するクラス
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
	 * actを取得します。
	 * @return act
	 */
	public String getAct() {
		return act;
	}

	/**
	 * labelMessageを取得します。
	 * @return labelMessage
	 */
	public String getLabelMessage() {
		return labelMessage;
	}
	/**
	 * labelMessageを設定します。
	 * @param labelMessage labelMessage
	 */
	public void setLabelMessage(String labelMessage) {
		this.labelMessage = labelMessage;
	}

	/**
	 * 開くボタン押下時のアクションか判定。
	 * @return true/false
	 */
	public boolean isActOpen() {
		return "open".equals(act);
	}
	/**
	 * 保存ボタン押下時のアクションか判定。
	 * @return true/false
	 */
	public boolean isActSave() {
		return "save".equals(act);
	}
	/**
	 * DLマネージャエラー発生時のアクションか判定。
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
