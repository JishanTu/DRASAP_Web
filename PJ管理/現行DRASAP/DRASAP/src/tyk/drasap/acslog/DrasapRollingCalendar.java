package tyk.drasap.acslog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * DrasapRollingCalendar is a helper class to WeeklyRollingFileAppender.
 * Given a periodicity type and the current time, it computes the start of the next interval.
 *
 * @author 2013/09/27 yamagishi
 * @version 2013/10/29 yamagishi
 */
class DrasapRollingCalendar extends GregorianCalendar {
	private static final long serialVersionUID = 6656624315352912182L;

	int type = WeeklyRollingFileAppender.TOP_OF_TROUBLE;

	DrasapRollingCalendar() {
		super();
	}

	DrasapRollingCalendar(TimeZone tz, Locale locale) {
		super(tz, locale);
	}

	void setType(int type) {
		this.type = type;
	}

	public long getLatestCheckMillis(Date now, Integer dow) { // 2013.10.29 yamagishi add.
		return getNextCheckDate(now, dow, false).getTime();
	}

	public long getNextCheckMillis(Date now, Integer dow) {
		return getNextCheckDate(now, dow, true).getTime();
	}

	public Date getNextCheckDate(Date now, Integer dow, boolean isNext) {
		this.setTime(now);

		switch (type) {
		case WeeklyRollingFileAppender.TOP_OF_MINUTE:
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MINUTE, 1);
			break;
		case WeeklyRollingFileAppender.TOP_OF_HOUR:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case WeeklyRollingFileAppender.HALF_DAY:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			int hour = get(Calendar.HOUR_OF_DAY);
			if (hour < 12) {
				this.set(Calendar.HOUR_OF_DAY, 12);
			} else {
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case WeeklyRollingFileAppender.TOP_OF_DAY:
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, 1);
			break;
//		case WeeklyRollingFileAppender.TOP_OF_WEEK: // 2013.10.29 yamagishi modified.
//			this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
		case WeeklyRollingFileAppender.GIVEN_DAY_OF_WEEK:
			int nowDow = this.get(Calendar.DAY_OF_WEEK);
			this.set(Calendar.DAY_OF_WEEK, (dow == null) ? getFirstDayOfWeek() : dow);
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			if (isNext) {
				this.add(Calendar.WEEK_OF_YEAR, 1);
			} else {
				this.add(Calendar.WEEK_OF_YEAR, (dow == null || nowDow >= dow) ? 1 : 0); // ójì˙ÇâﬂÇ¨ÇƒÇ¢ÇΩÇÁóÇèT
			}
			break;
		case WeeklyRollingFileAppender.TOP_OF_MONTH:
			this.set(Calendar.DATE, 1);
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MONTH, 1);
			break;
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return getTime();
	}

	public Date getPreDate(long now) { // 2013.09.27 yamagishi add.
		this.setTimeInMillis(now);
		this.add(Calendar.DATE, -1);
		return getTime();
	}
}
