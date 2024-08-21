package tyk.drasap.common;

/**
 * システムの情報を保持する。
 * この内容は管理者設定マスターから読み取る。
 * ログイン時に取得する。
 *
 * @version 2013/06/27 yamagishi
 */
public class DrasapInfo {
	String viewDBDrive = "F:";
	int searchWarningCount=1000;// 検索における警告件数
	int searchLimitCount=20000;// 検索における打切件数(利用可能最大件数)
	//String mabikiDpi="200";// ビュワーでA0LからA0に間引く処理での解像度dpi・・・廃止 '04.Mar.2
	int printRequestMax = 100;// 参考図出力最大件数
	int aclvChangablePosition = 999;// アクセスレベル変更可能な職位
	int minimumIuputDrwgChar = 0;// 検索における図番属性の最低限の文字数
	// 間引きサイズの未設定にも対応する。'04.Jul.19変更 by Hirata。
	String mabiki100dpiSize = null;// この図面サイズ以上(含む)では、100dpiに間引きする。(ビューで)
	String mabiki200dpiSize = null;// mabiki100dpiSizeより小さく、この図面サイズ以上(含む)では、200dpiに間引きする。(ビューで)
	String viewStampW = "10";// 閲覧用スタンプ位置(W)
	String viewStampL = "10";// 閲覧用スタンプ位置(L)
	String viewStampDeep = "70";// 閲覧用スタンプ文字濃度
	String viewStampDateFormat = "yyyy/MM/dd";// 閲覧用スタンプ日付形式
									// SimpleDateFormatで使う文字列を保持
// 2013.07.12 yamagishi add. start
	String correspondingStampW = "1"; 		// 該当図用スタンプ位置(W方向)
	String correspondingStampL = "17820";	// 該当図用スタンプ位置(L方向)
	String correspondingStampDeep = "180";	// 該当図用スタンプ文字濃淡
	String correspondingStampStr = "★";	// 該当図用スタンプ文字
// 2013.07.12 yamagishi add. end
	boolean dispDrwgNoWithView = true;// 閲覧用スタンプでの図番を印字する？
	int multipleDrwgNoMax = 1000; // 2013.06.27 yamagishi add.
// 2013.07.10 yamagishi add. start
	String correspondingValue = "●";
	String confidentialValue = "▲";
	String strictlyConfidentialValue = "●";
// 2013.07/10 yamagishi add. end

	// ---------------------------------------------------------- コンストラクタ
	/**
	 * コンストラクタ
	 */
// 2013.07.10 yamagishi modified. start
//	public DrasapInfo(String viewDBDrive, String newSearchWarningCount, String newSearchLimitCount,
//			String newPrintRequestMax, String newAclvChangablePosition,
//			String newMinimumIuputDrwgChar,
//			String newMabiki100dpiSize,	String newMabiki200dpiSize,
//			String newViewStampW, String newViewStampL, String newViewStampDeep,
//			String newViewStampDateFormat, String newDispDrwgNoWithView) {
	public DrasapInfo(String viewDBDrive, String newSearchWarningCount, String newSearchLimitCount,
			String newPrintRequestMax, String newAclvChangablePosition,
			String newMinimumIuputDrwgChar,
			String newMabiki100dpiSize,	String newMabiki200dpiSize,
			String newViewStampW, String newViewStampL, String newViewStampDeep,
			String newViewStampDateFormat, String newDispDrwgNoWithView,
			String newMultipleDrwgNoMax,
			String newCorrespondingValue, String newConfidentialValue, String newStrictlyConfidentialValue,
			String newCorrespondingStampW, String newCorrespondingStampL,
			String newCorrespondingStampDeep, String newCorrespondingStampStr) {
// 2013.07.10 yamagishi modified. end

		if (viewDBDrive != null) this.viewDBDrive = viewDBDrive;
		// 検索における警告件数
		if(newSearchWarningCount != null){
			try{
				this.searchWarningCount = Integer.parseInt(newSearchWarningCount);
			} catch(NumberFormatException ne){
			}
		}
		// 検索における打切件数(利用可能最大件数)
		if(newSearchLimitCount != null){
			try{
				this.searchLimitCount = Integer.parseInt(newSearchLimitCount);
			} catch(NumberFormatException ne){
			}
		}
		// 間引き処理の解像度・・・廃止 '04.Mar.2
		//if(newMabikiDpi != null){
		//	mabikiDpi = newMabikiDpi;
		//}
		// 参考図出力における最大件数
		if(newPrintRequestMax != null){
			try{
				this.printRequestMax = Integer.parseInt(newPrintRequestMax);
			} catch(NumberFormatException ne){
			}
		}
		// アクセスレベル変更可能な職位
		if(newAclvChangablePosition != null){
			try{
				this.aclvChangablePosition = Integer.parseInt(newAclvChangablePosition);
			} catch(NumberFormatException ne){
			}
		}
		// 検索における図番属性の最低限の文字数
		if(newMinimumIuputDrwgChar != null){
			try{
				this.minimumIuputDrwgChar = Integer.parseInt(newMinimumIuputDrwgChar);
			} catch(NumberFormatException ne){
			}
		}
		// 100dpiに間引きする図面サイズ
		if(newMabiki100dpiSize != null){
			this.mabiki100dpiSize = newMabiki100dpiSize;
		}
		// 200dpiに間引きする図面サイズ
		if(newMabiki200dpiSize != null){
			this.mabiki200dpiSize = newMabiki200dpiSize;
		}
		// 閲覧用スタンプ位置(W)
		if(newViewStampW != null){
			this.viewStampW = newViewStampW;
		}
		// 閲覧用スタンプ位置(L)
		if(newViewStampL != null){
			this.viewStampL = newViewStampL;
		}
		// 閲覧用スタンプ文字濃度
		if(newViewStampDeep != null){
			this.viewStampDeep = newViewStampDeep;
		}
		// 閲覧用スタンプ日付形式
		if(newViewStampDateFormat != null){
			if(newViewStampDateFormat.equals("1")){
				// 1なら
				this.viewStampDateFormat = "yy/MM/dd";
			} else {
				// それ以外なら
				this.viewStampDateFormat = "yyyy/MM/dd";
			}
		}
		// 閲覧用スタンプでの図番を印字する？
		if(newDispDrwgNoWithView != null){
			// 1なら図番を印字する
			this.dispDrwgNoWithView = "1".equals(newDispDrwgNoWithView);
		}
// 2013.06.27 yamagishi add. start
		// 複数図番指定時の検索可能件数
		if (newMultipleDrwgNoMax != null) {
			try {
				this.multipleDrwgNoMax = Integer.parseInt(newMultipleDrwgNoMax);
			} catch (NumberFormatException ne) {
			}
		}
// 2013.06.27 yamagishi add. end
// 2013.07.10 yamagishi add. start
		// 該当図入力値
		if (newCorrespondingValue != null) {
			this.correspondingValue = newCorrespondingValue;
		}
		// 機密管理図入力値（秘）
		if (newConfidentialValue != null) {
			this.confidentialValue = newConfidentialValue;
		}
		// 機密管理図入力値（極秘）
		if (newStrictlyConfidentialValue != null) {
			this.strictlyConfidentialValue = newStrictlyConfidentialValue;
		}
		// 該当図用スタンプ位置(W方向)
		if (newCorrespondingStampW != null) {
			this.correspondingStampW = newCorrespondingStampW;
		}
		// 該当図用スタンプ位置(L方向)
		if (newCorrespondingStampL != null) {
			this.correspondingStampL = newCorrespondingStampL;
		}
		// 該当図用スタンプ文字濃淡
		if (newCorrespondingStampDeep != null) {
			this.correspondingStampDeep = newCorrespondingStampDeep;
		}
		// 該当図用スタンプ文字
		if (newCorrespondingStampStr != null) {
			this.correspondingStampStr = newCorrespondingStampStr;
		}
// 2013.07.10 yamagishi add. end
	}
	// ---------------------------------------------------------- getter,setter
	/**
	 * @return
	 */
	public int getSearchLimitCount() {
		return searchLimitCount;
	}

	/**
	 * @return
	 */
	public int getSearchWarningCount() {
		return searchWarningCount;
	}

	/**
	 * @return
	 */
	public int getPrintRequestMax() {
		return printRequestMax;
	}

	/**
	 * @return
	 */
	public int getAclvChangablePosition() {
		return aclvChangablePosition;
	}

	/**
	 * @return
	 */
	public int getMinimumIuputDrwgChar() {
		return minimumIuputDrwgChar;
	}

// 2013.07.02 yamagishi add. start
	/**
	 * correspondingStampWを取得します。
	 * @return correspondingStampW
	 */
	public String getCorrespondingStampW() {
		return correspondingStampW;
	}
	/**
	 * correspondingStampLを取得します。
	 * @return correspondingStampL
	 */
	public String getCorrespondingStampL() {
		return correspondingStampL;
	}
	/**
	 * correspondingStampDeepを取得します。
	 * @return correspondingStampDeep
	 */
	public String getCorrespondingStampDeep() {
		return correspondingStampDeep;
	}
	/**
	 * correspondingStampStrを取得します。
	 * @return correspondingStampStr
	 */
	public String getCorrespondingStampStr() {
		return correspondingStampStr;
	}
// 2013.07.02 yamagishi add. end

	/**
	 * @return
	 */
	public boolean isDispDrwgNoWithView() {
		return dispDrwgNoWithView;
	}

	/**
	 * @return
	 */
	public String getViewStampDateFormat() {
		return viewStampDateFormat;
	}

	/**
	 * @return
	 */
	public String getViewStampDeep() {
		return viewStampDeep;
	}

	/**
	 * @return
	 */
	public String getViewStampL() {
		return viewStampL;
	}

	/**
	 * @return
	 */
	public String getViewStampW() {
		return viewStampW;
	}

	/**
	 * @return
	 */
	public String getMabiki100dpiSize() {
		return mabiki100dpiSize;
	}

	/**
	 * @return
	 */
	public String getMabiki200dpiSize() {
		return mabiki200dpiSize;
	}
	public String getViewDBDrive() {
		return viewDBDrive;
	}
	public void setViewDBDrive(String viewDBDrive) {
		this.viewDBDrive = viewDBDrive;
	}
// 2013.06.27 yamagishi add. start
	/**
	 * multipleDrwgNoMaxを取得します。
	 * @return multipleDrwgNoMax
	 */
	public int getMultipleDrwgNoMax() {
		return multipleDrwgNoMax;
	}
// 2013.06.27 yamagishi add. end

// 2013.07.10 yamagishi add. start
	/**
	 * correspondingValueを取得します。
	 * @return correspondingValue
	 */
	public String getCorrespondingValue() {
		return correspondingValue;
	}
	/**
	 * confidentialValueを取得します。
	 * @return confidentialValue
	 */
	public String getConfidentialValue() {
		return confidentialValue;
	}
	/**
	 * strictlyConfidentialValueを取得します。
	 * @return strictlyConfidentialValue
	 */
	public String getStrictlyConfidentialValue() {
		return strictlyConfidentialValue;
	}
// 2013.07.10 yamagishi add. end
}
