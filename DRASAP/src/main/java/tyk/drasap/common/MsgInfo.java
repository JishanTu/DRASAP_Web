package tyk.drasap.common;

/**
 * 各テーブルの内容を保持する。
 * テーブルメンテナンス用。
 */
public class MsgInfo {
	String iraiNo = "";
	String msg = "";
	String msgStyle = "color:#0000FF;";

	public MsgInfo() {
		super();
	}

	public MsgInfo(String iraiNo, String msg, String msgStyle) {
		super();
		this.iraiNo = iraiNo;
		this.msg = msg;
		this.msgStyle = msgStyle;
	}

	// ------------------------------------------------------- getter,setter
	public String getIraiNo() {
		return iraiNo;
	}

	public void setIraiNo(String iraiNo) {
		this.iraiNo = iraiNo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsgStyle() {
		return msgStyle;
	}

	public void setMsgStyle(String msgStyle) {
		this.msgStyle = msgStyle;
	}

}
