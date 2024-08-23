package tyk.drasap.search;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import tyk.drasap.common.DateCheck;
import tyk.drasap.common.MsgInfo;



/**
 * �폜��ʂɑΉ�
 */
public class DeleteHostReqForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static final String NORMAL = "color:#0000FF;";
    private static final String ERR = "color:#FF0000;";
	String act;// �����𕪂��邽�߂̑���
	String seachKind;
	ArrayList<String> condition = new ArrayList<String>(10);
	ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
	boolean deleteOK = false;

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		if("delete".equals(act)){
			
			ActionErrors errors = new ActionErrors();
			// HOST�˗��I���`�F�b�N
			if (seachKind == null || (!seachKind.equals("delSeisan") && !seachKind.equals("delPrt"))) {
				errors.add("seachKind", new ActionMessage("search.delHost.miss.seachKind"));
				return errors;
			}
			if (condition.size() <= 0) {
				errors.add("condition", new ActionMessage("search.delHost.miss.condition"));
				return errors;
			}
			// �˗��ԍ��`�F�b�N
			int condition_count = 0;
			for (int i = 0; i < condition.size(); i++) {
				// �����͂͂Ƃ΂�
				if (condition.get(i) == null || condition.get(i).length() == 0) continue;
				condition_count++;
				// ���͌����`�F�b�N 
				if (condition.get(i).length() != 14) {
					if (seachKind.equals("delSeisan")) {
						errors.add("condition["+Integer.toString(i)+"]", new ActionMessage("search.delHost.miss.delSeisan"));
					} else {
						errors.add("condition["+Integer.toString(i)+"]", new ActionMessage("search.delHost.miss.delPrt"));
					}
					continue;
				}
				// ����
				if (seachKind.equals("delSeisan")) {
					if (condition.get(i).substring(8, 9).equals("A")) {	// �{��
					} else if (condition.get(i).substring(8, 9).equals("C")) { // �����J
					} else {
						errors.add("condition["+Integer.toString(i)+"]", new ActionMessage("search.delHost.miss.delSeisan"));
					}
				} else {
					if (condition.get(i).substring(8, 9).equals("B")) {	// �{��
					} else if (condition.get(i).substring(8, 9).equals("D")) { // �����J
					} else {
						errors.add("condition["+Integer.toString(i)+"]", new ActionMessage("search.delHost.miss.delPrt"));
					}
				}
				int ymd = DateCheck.convertIntYMD(condition.get(i).substring(0, 8));
				if(ymd == -1 || !DateCheck.isDate(ymd)){
					// ���t�Ƃ��ĉ��߂ł��Ȃ�
					errors.add("condition["+Integer.toString(i)+"]", new ActionMessage("search.delHost.miss.condition.dateformat", condition.get(i).substring(0, 8)));
				}
			}
			if (condition_count == 0) {
				errors.add("condition", new ActionMessage("search.delHost.miss.condition"));
			}
			return errors;
		}
		return super.validate(mapping, request);
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
		this.msgList.clear();
	}
	public void addMsgList(String iraiNo, String str) {
		MsgInfo msgInfo = new MsgInfo(iraiNo, str, NORMAL);
		this.msgList.add(msgInfo);
	}
	public void addErrMsgList(String iraiNo, String str) {
		MsgInfo msgInfo = new MsgInfo(iraiNo, str, ERR);
		this.msgList.add(msgInfo);
	}
	public ArrayList<MsgInfo> getMsgList() {
		return msgList;
	}
	public MsgInfo getMsg(String iraiNo) {
		for (int i = 0; i < msgList.size(); i++) {
			if (msgList.get(i).getIraiNo().equals(iraiNo)) return msgList.get(i);
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
