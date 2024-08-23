package tyk.drasap.genzu_irai;

/**
 * 原図庫作業依頼における行データ。
 * チェックルーチンで使用するために、新たに定義した。
 * @author fumi
 */
public class RequestFormLineData {
	private String gouki;// 号機
	private String genzu;// 原図内容
	private String kaisiNo;// 開始番号
	private String syuuryouNo;//終了番号
	private String busuu;//部数
	private String syukusyou;//縮小区分
	private String size;//縮小サイズ
	
	/**
	 * コンストラクタ
	 */
	public RequestFormLineData(String gouki, String genzu, String kaisiNo, String syuuryouNo,
				String busuu, String syukusyou, String size) {
		this.gouki = gouki;// 号機
		this.genzu = genzu;// 原図内容
		this.kaisiNo = kaisiNo;// 開始番号
		this.syuuryouNo = syuuryouNo;//終了番号
		this.busuu = busuu;//部数
		this.syukusyou = syukusyou;//縮小区分
		this.size = size;//縮小サイズ
	}

	//---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public String getBusuu() {
		return busuu;
	}

	/**
	 * @return
	 */
	public String getGenzu() {
		return genzu;
	}

	/**
	 * @return
	 */
	public String getGouki() {
		return gouki;
	}

	/**
	 * @return
	 */
	public String getKaisiNo() {
		return kaisiNo;
	}

	/**
	 * @return
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @return
	 */
	public String getSyukusyou() {
		return syukusyou;
	}

	/**
	 * @return
	 */
	public String getSyuuryouNo() {
		return syuuryouNo;
	}

}
