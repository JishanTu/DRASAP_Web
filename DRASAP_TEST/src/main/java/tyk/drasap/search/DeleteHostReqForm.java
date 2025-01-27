package tyk.drasap.search;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import tyk.drasap.common.DateCheck;
import tyk.drasap.common.MsgInfo;
import tyk.drasap.springfw.form.BaseForm;
import tyk.drasap.springfw.utils.MessageSourceUtil;

/**
 * �폜��ʂɑΉ�
 */
public class DeleteHostReqForm extends BaseForm {
	/**
	 *
	 */
	private static final String NORMAL = "color:#0000FF;";
	private static final String ERR = "color:#FF0000;";
	String act;// �����𕪂��邽�߂̑���
	String seachKind;
	ArrayList<String> condition = new ArrayList<String>(10);
	ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
	boolean deleteOK = false;

	@Override
	public Model validate(HttpServletRequest request, Model errors, MessageSource messageSource) {

		msgList.clear();
		if ("delete".equals(act)) {

			//            ActionErrors errors = new ActionErrors();
			// HOST�˗��I���`�F�b�N
			if (seachKind == null || !"delSeisan".equals(seachKind) && !"delPrt".equals(seachKind)) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.seachKind", null, null));
				return errors;
			}
			if (condition.size() <= 0) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.condition", null, null));
				return errors;
			}
			// �˗��ԍ��`�F�b�N
			int condition_count = 0;
			for (int i = 0; i < condition.size(); i++) {
				// �����͂͂Ƃ΂�
				if (condition.get(i) == null || condition.get(i).length() == 0) {
					continue;
				}
				condition_count++;
				// ���͌����`�F�b�N
				if (condition.get(i).length() != 14) {
					if ("delSeisan".equals(seachKind)) {
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.delSeisan", null, null));
					} else {
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.delPrt", null, null));
					}
					continue;
				}
				// ����
				if ("delSeisan".equals(seachKind)) {
					if ("A".equals(condition.get(i).substring(8, 9)) || "C".equals(condition.get(i).substring(8, 9))) { // �{��
					} else {
						MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.delSeisan", null, null));
					}
				} else if ("B".equals(condition.get(i).substring(8, 9)) || "D".equals(condition.get(i).substring(8, 9))) { // �{��
				} else {
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.delPrt", null, null));
				}
				int ymd = DateCheck.convertIntYMD(condition.get(i).substring(0, 8));
				if (ymd == -1 || !DateCheck.isDate(ymd)) {
					// ���t�Ƃ��ĉ��߂ł��Ȃ�
					MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.condition.dateformat", new Object[] { condition.get(i).substring(0, 8) }, null));
				}
			}
			if (condition_count == 0) {
				MessageSourceUtil.addAttribute(errors, "message", messageSource.getMessage("search.delHost.miss.condition", null, null));
			}
		}
		return errors;
	}

	// --------------------------------------------------------- Methods
	// --------------------------------------------------------- getter,setter
	public DeleteHostReqForm() {
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

	public void clearCondition() {
		condition.clear();
	}

	public ArrayList<String> getCondition() {
		return condition;
	}

	public void setCondition(ArrayList<String> condition) {
		this.condition = condition;
	}

	public String getSeachKind() {
		return seachKind;
	}

	public void setSeachKind(String seachKind) {
		this.seachKind = seachKind;
	}

	public void cleartMsgList() {
		msgList.clear();
	}

	public void addMsgList(String iraiNo, String str) {
		MsgInfo msgInfo = new MsgInfo(iraiNo, str, NORMAL);
		msgList.add(msgInfo);
	}

	public void addErrMsgList(String iraiNo, String str) {
		MsgInfo msgInfo = new MsgInfo(iraiNo, str, ERR);
		msgList.add(msgInfo);
	}

	public ArrayList<MsgInfo> getMsgList() {
		return msgList;
	}

	public MsgInfo getMsg(String iraiNo) {
		for (int i = 0; i < msgList.size(); i++) {
			if (msgList.get(i).getIraiNo().equals(iraiNo)) {
				return msgList.get(i);
			}
		}
		return null;
	}

	public void setMsgList(ArrayList<MsgInfo> msgList) {
		this.msgList = msgList;
	}

	public boolean isDeleteOK() {
		return deleteOK;
	}

	public void setDeleteOK(boolean deleteOK) {
		this.deleteOK = deleteOK;
	}
}
