package tyk.drasap.common;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * 日付チェックするクラス。
 */
public class DateCheck {
	/**
	 * 文字列を受け取り、８桁の数値のみの日付へ変更する(例:2001/2/15→20010215)。
	 * 変更できないときは -1 を返す。
	 * @return int
	 * @param ymd 年月日。次の形式で受けられる。
	 * 「/」区切りなし
	 *    1) 8ケタ YYYYMMDD
	 *    2) 6ケタ YYMMDD
	 * 「/」区切りあり
	 *    この場合、MMまたはDDは1けたでも可 （例） 2002/3/8
	 *    年も2けた対応できる 02/5/15 -> 20020515へ
	 * 変更履歴
	 * Kumiko Watanabe(2001/08/20)
	 * 2ケタ年対応 Fumihiko Hirata(2002/4/5)
	 */
	public static int convertIntYMD(String ymd) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(ymd, "/");
			int cnt = tokenizer.countTokens();
			switch (cnt) {
			case 1:
				int intYmd = Integer.parseInt(ymd);
				// 年を確認
				int y4 = intYmd / 10000;//年を切り出し
				int md = intYmd - y4 * 10000;//月日を切り出し
				if (y4 > -1 && y4 < 100) {
					// 年が2ケタなら4けたへ
					y4 = convertYear2to4(y4);
				}
				return y4 * 10000 + md;

			case 3:
				int yyyy = Integer.parseInt(tokenizer.nextToken());
				int mm = Integer.parseInt(tokenizer.nextToken());
				int dd = Integer.parseInt(tokenizer.nextToken());

				if (yyyy < 0 || yyyy > 9999) {
					return -1;
				}
				if (yyyy < 100) {
					// 年が2ケタなら4けたへ
					yyyy = convertYear2to4(yyyy);
				}
				if (mm < 0 || mm > 99 || dd < 0 || dd > 99) {
					return -1;
				}
				return yyyy * 10000 +
						mm * 100 +
						dd;

			default:
				return -1;
			}

		} catch (NumberFormatException ne) {
			// Integer生成時に例外発生
			return -1;
		} // End try-catch
	}

	/**
	 * 2ケタの年を4ケタの年へ変換。
	 * 変換基準は「現在へ近い4ケタの年」へ変換。
	 * 例：現在2002年とすれば 98 -> 1998
	 *                       04 -> 2004
	 * @param year2 2ケタ年
	 */
	public static int convertYear2to4(int year2) {
		// 0から99までOK
		if (year2 < 0 || year2 > 99) {
			return 0;
		}
		Calendar today = Calendar.getInstance();
		int nowYear = today.get(Calendar.YEAR);
		int yyyy = nowYear / 100 * 100;// 西暦の頭2ケタを取得 2102年なら 2100
		//とりあえず、そのままプラス
		int year4 = yyyy + year2;//仮
		if (Math.abs(year4 - nowYear) <= 50) {
			// 現在年との差が50年以下なら、OK
			return year4;

		}
		if (year4 - nowYear > 0) {
			// 仮が大なら 100年引く
			return year4 - 100;

		} else {
			return year4 + 100;
		}

	}

	/**
	 * 年月日を受け取り、int配列で返す。
	 * @return int[] index0=年、1=月、2=日
	 * @param ymd int
	 */
	public static int[] parseYMD(int ymd) {
		int yyyy = ymd / 10000;//年
		int mm = (ymd - yyyy * 10000) / 100;// 月
		int dd = ymd - yyyy * 10000 - mm * 100;

		int[] ret = new int[3];
		ret[0] = yyyy;
		ret[1] = mm;
		ret[2] = dd;

		return ret;
	}

	/**
	 * 日付 yyyymmddを受け取って、カレンダーで正しい日付か判断する
	 * @return boolean
	 * @param ymd 年月日 YYYY/MM/DD
	 */
	public static boolean isDate(int ymd) {
		int[] ymdA = parseYMD(ymd);
		int yyyy = ymdA[0];//年
		int mm = ymdA[1];// 月
		int dd = ymdA[2];// 日

		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setLenient(false); // 拡大解釈をしない
		cal.set(yyyy, mm - 1, dd);

		// 例外が発生すれば、日付は正しくない
		try {
			cal.getTime();
			return true;

		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}
