package tyk.drasap.common;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * ���t�`�F�b�N����N���X�B
 */
public class DateCheck {
	/**
	 * ��������󂯎��A�W���̐��l�݂̂̓��t�֕ύX����(��:2001/2/15��20010215)�B
	 * �ύX�ł��Ȃ��Ƃ��� -1 ��Ԃ��B
	 * @return int
	 * @param ymd �N�����B���̌`���Ŏ󂯂���B
	 * �u/�v��؂�Ȃ�
	 *    1) 8�P�^ YYYYMMDD
	 *    2) 6�P�^ YYMMDD
	 * �u/�v��؂肠��
	 *    ���̏ꍇ�AMM�܂���DD��1�����ł��� �i��j 2002/3/8
	 *    �N��2�����Ή��ł��� 02/5/15 -> 20020515��
	 * �ύX����
	 * Kumiko Watanabe(2001/08/20)
	 * 2�P�^�N�Ή� Fumihiko Hirata(2002/4/5)
	 */
	public static int convertIntYMD(String ymd) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(ymd, "/");
			int cnt = tokenizer.countTokens();
			switch (cnt) {
			case 1:
				int intYmd = Integer.parseInt(ymd);
				// �N���m�F
				int y4 = intYmd / 10000;//�N��؂�o��
				int md = intYmd - y4 * 10000;//������؂�o��
				if (y4 > -1 && y4 < 100) {
					// �N��2�P�^�Ȃ�4������
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
					// �N��2�P�^�Ȃ�4������
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
			// Integer�������ɗ�O����
			return -1;
		} // End try-catch
	}

	/**
	 * 2�P�^�̔N��4�P�^�̔N�֕ϊ��B
	 * �ϊ���́u���݂֋߂�4�P�^�̔N�v�֕ϊ��B
	 * ��F����2002�N�Ƃ���� 98 -> 1998
	 *                       04 -> 2004
	 * @param year2 2�P�^�N
	 */
	public static int convertYear2to4(int year2) {
		// 0����99�܂�OK
		if (year2 < 0 || year2 > 99) {
			return 0;
		}
		Calendar today = Calendar.getInstance();
		int nowYear = today.get(Calendar.YEAR);
		int yyyy = nowYear / 100 * 100;// ����̓�2�P�^���擾 2102�N�Ȃ� 2100
		//�Ƃ肠�����A���̂܂܃v���X
		int year4 = yyyy + year2;//��
		if (Math.abs(year4 - nowYear) <= 50) {
			// ���ݔN�Ƃ̍���50�N�ȉ��Ȃ�AOK
			return year4;

		}
		if (year4 - nowYear > 0) {
			// ������Ȃ� 100�N����
			return year4 - 100;

		} else {
			return year4 + 100;
		}

	}

	/**
	 * �N�������󂯎��Aint�z��ŕԂ��B
	 * @return int[] index0=�N�A1=���A2=��
	 * @param ymd int
	 */
	public static int[] parseYMD(int ymd) {
		int yyyy = ymd / 10000;//�N
		int mm = (ymd - yyyy * 10000) / 100;// ��
		int dd = ymd - yyyy * 10000 - mm * 100;

		int[] ret = new int[3];
		ret[0] = yyyy;
		ret[1] = mm;
		ret[2] = dd;

		return ret;
	}

	/**
	 * ���t yyyymmdd���󂯎���āA�J�����_�[�Ő��������t�����f����
	 * @return boolean
	 * @param ymd �N���� YYYY/MM/DD
	 */
	public static boolean isDate(int ymd) {
		int[] ymdA = parseYMD(ymd);
		int yyyy = ymdA[0];//�N
		int mm = ymdA[1];// ��
		int dd = ymdA[2];// ��

		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setLenient(false); // �g����߂����Ȃ�
		cal.set(yyyy, mm - 1, dd);

		// ��O����������΁A���t�͐������Ȃ�
		try {
			cal.getTime();
			return true;

		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}
