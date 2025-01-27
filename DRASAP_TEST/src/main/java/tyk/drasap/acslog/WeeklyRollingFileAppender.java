/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tyk.drasap.acslog;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * <code>DailyRollingFileAppender</code> の機能を改修したクラスです。
 *
 * <p>
 * ログファイルのサイズが <b>RollOverDow</b> で示される曜日に、日付が切り替わる時にローテーションが行われます。
 * </p>
 *
 * <p>
 * 本クラスで追加されたプロパティの種類とデフォルト値は以下の通りです。
 * </p>
 *
 * <table border="1">
 * <tr>
 * <th><b>プロパティ名</b></th>
 * <th><b>説明</b></th>
 * <th><b>デフォルト値</b></th>
 * </tr>
 * <tr>
 * <td nowrap="true"><b>RollOverDow</b></td>
 * <td>ローテーションを実行する曜日コード</td>
 * <td nowrap="true">1</td>
 * </tr>
 * <tr>
 * <td nowrap="true"><b>DatePattern</b></td>
 * <td>日付パターン</td>
 * <td nowrap="true">'.'yyyy-MM-dd</td>
 * </tr>
 * </table>
 *
 * <p>
 * 週次でローテーションさせたい場合は、上記2つのプロパティを設定ファイル等で
 * 設定します。<br>
 * </p>
 *
 * @author 2013/09/27 yamagishi
 * @version 2013/10/29 yamagishi
 */
public class WeeklyRollingFileAppender extends FileAppender {

	// The code assumes that the following constants are in a increasing
	// sequence.
	static final int TOP_OF_TROUBLE = -1;
	static final int TOP_OF_MINUTE = 0;
	static final int TOP_OF_HOUR = 1;
	static final int HALF_DAY = 2;
	static final int TOP_OF_DAY = 3;
	/** Given Day Of Week.
	 * 1:Sunday, 2:Monday, 3:Tuesday, 4:Wednesday, 5:Thursday, 6:Friday, 7:Saturday */
	//	static final int TOP_OF_WEEK = 4;
	static final int GIVEN_DAY_OF_WEEK = 4; // 2013.09.27 yamagishi add.
	static final int TOP_OF_MONTH = 5;

	// The gmtTimeZone is used only in computeCheckPeriod() method.
	static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	/**
	 * The roll over day of week. By default, the day of week is set to "1:Sunday"
	 * meaning weekly rollover on the day.
	 */
	private String rollOverDow = "1"; // 2013.09.27 yamagishi add.

	/**
	 * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd"
	 * meaning daily rollover.
	 */
	private String datePattern = "'.'yyyy-MM-dd";

	/**
	 * The log file will be renamed to the value of the scheduledFilename
	 * variable when the next interval is entered. For example, if the rollover
	 * period is one hour, the log file will be renamed to the value of
	 * "scheduledFilename" at the beginning of the next hour.
	 *
	 * The precise time when a rollover occurs depends on logging activity.
	 */
	private String scheduledFilename;

	/**
	 * The next time we estimate a rollover should occur.
	 */
	private long nextCheck = System.currentTimeMillis() - 1;
	Date now = new Date();
	SimpleDateFormat sdf;
	DrasapRollingCalendar rc = new DrasapRollingCalendar();

	/*----------------------------------------------------------------------------*/

	/**
	 * The default constructor simply calls its FileAppender#FileAppender
	 * parents constructor.
	 */
	public WeeklyRollingFileAppender() {
		super();
	}

	/**
	 * Instantiate a RollingFileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the ouput
	 * destination for this appender.
	 *
	 * <p>
	 * If the <code>append</code> parameter is true, the file will be appended
	 * to. Otherwise, the file desginated by <code>filename</code> will be
	 * truncated before being opened.
	 */
	public WeeklyRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
	}

	/**
	 * Instantiate a FileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the output
	 * destination for this appender.
	 *
	 * <p>
	 * The file will be appended to.
	 */
	public WeeklyRollingFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
	}

	/**
	 * Instantiate a <code>WeeklyRollingFileAppender</code> and open the file
	 * designated by <code>filename</code>. The opened filename will become the
	 * ouput destination for this appender.
	 */
	public WeeklyRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {
		super(layout, filename, true);
		this.datePattern = datePattern;
		activateOptions();
	}

	public WeeklyRollingFileAppender(Layout layout, String filename, String datePattern, boolean append) throws IOException {
		super(layout, filename, append);
		this.datePattern = datePattern;
		activateOptions();
	}

	/*----------------------------------------------------------------------------*/

	@Override
	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
		if (append) {
			File f = new File(fileName);
			((CountingQuietWriter) qw).setCount(f.length());
		}
	}

	@Override
	public void activateOptions() {
		super.activateOptions();
		if (datePattern != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			File file = new File(fileName);
			if (rc.type == GIVEN_DAY_OF_WEEK) { // 2013.09.27 yamagishi add.
				String preDatedFilename = fileName
						+ sdf.format(rc.getPreDate(rc.getLatestCheckMillis(new Date(file.lastModified()), getIntRollOverDow())));
				scheduledFilename = preDatedFilename;
			} else {
				scheduledFilename = fileName
						+ sdf.format(new Date(file.lastModified()));
			}
		} else {
			LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
		}
	}

	void printPeriodicity(int type) {
		switch (type) {
		case TOP_OF_MINUTE:
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;
		case TOP_OF_HOUR:
			LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
			break;
		case HALF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
			break;
		case TOP_OF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;
		case GIVEN_DAY_OF_WEEK:
			//			LogLog.debug("Appender [" + name + "] to be rolled at start of week."); // 2013.09.27 yamagishi modified.
			LogLog.debug("Appender [" + name + "] to be rolled at the given day of week.");
			break;
		case TOP_OF_MONTH:
			LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
			break;
		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	// This method computes the roll over period by looping over the periods,
	// starting with the shortest, and stopping when the r0 is different from from r1,
	// where r0 is the epoch formatted according to the datePattern (supplied by the user)
	// and r1 is the epoch+nextMillis(i) formatted according to the datePattern.
	// All date formatting is done in GMT and not local format
	// because the test logic is based on comparisons relative to 1970-01-01 00:00:00 GMT (the epoch).
	int computeCheckPeriod() {
		DrasapRollingCalendar rollingCalendar = new DrasapRollingCalendar(gmtTimeZone, Locale.ENGLISH);
		// set sate to 1970-01-01 00:00:00 GMT
		Date epoch = new Date(0);
		if (rollOverDow != null && rollOverDow.length() > 0) { // 2013.09.27 yamagishi add.
			rollingCalendar.setType(GIVEN_DAY_OF_WEEK);
			return GIVEN_DAY_OF_WEEK;
		}
		if (datePattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone); // do all date
				// formatting in GMT
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				//				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch, getIntRollOverDow()));
				String r1 = simpleDateFormat.format(next);
				//				System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble...
	}

	/**
	 * Rollover the current file to a new file.
	 */
	void rollOver() throws IOException {

		/* Compute filename, but only if datePattern is specified */
		if (rollOverDow == null) {
			errorHandler.error("Missing RollOverDow option in rollOver().");
			return;
		}
		if (datePattern == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}

		String datedFilename = fileName + sdf.format(now);
		// It is too early to roll over because we are still within the
		// bounds of the current interval. Rollover will occur once the
		// next interval is reached.
		//		if (scheduledFilename.equals(datedFilename)) { // 2013.09.27 yamagishi modified.
		if (scheduledFilename.compareTo(datedFilename) >= 0) {
			return;
		}

		rotateLogFiles();

		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}

		if (rc.type == GIVEN_DAY_OF_WEEK) {
			String preDatedFilename = fileName + sdf.format(rc.getPreDate(nextCheck));
			if (scheduledFilename.equals(preDatedFilename)) { // 2013.10.29 yamagishi modified.
				preDatedFilename = fileName
						+ sdf.format(rc.getPreDate(rc.getNextCheckMillis(new Date(nextCheck), getIntRollOverDow())));
			}
			scheduledFilename = preDatedFilename;
		} else {
			scheduledFilename = datedFilename;
		}
	}

	/**
	 * This method differentiates WeeklylyRollingFileAppender from its super class.
	 *
	 * <p>
	 * Before actually logging, this method will check whether it is time to do a rollover.
	 * If it is, it will schedule the next rollover time and then rollover.
	 */
	@Override
	protected void subAppend(LoggingEvent event) {
		// 日付ベースのローテーションが発生するかどうかチェックし、
		// 発生する場合はローテーションを行う。
		long n = System.currentTimeMillis();
		if (n >= nextCheck) {
			now.setTime(n);
			//			nextCheck = rc.getNextCheckMillis(now); // 2013.09.27 yamagishi modified.
			nextCheck = rc.getLatestCheckMillis(now, getIntRollOverDow());
			try {
				rollOver();
			} catch (IOException ioe) {
				if (ioe instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogLog.error("rollOver() failed.", ioe);
			}
		}
		// 週次のローテーションが発生した場合は、ローテーション後に
		// ログを書き込む必要があるため、ここでsuper.subAppend()を呼び出す
		super.subAppend(event);
	}

	/**
	 * 日付でローテーションを行う
	 */
	private void rotateLogFiles() {
		// close current file, and rename it to datedFilename
		closeFile();

		// ログファイル名に日付を付与する
		File toFile = new File(scheduledFilename);
		if (toFile.exists()) {
			toFile.delete();
		}

		File fromFile = new File(fileName);
		fromFile.renameTo(toFile);
	}

	/*----------------------------------------------------------------------------*/

	@Override
	protected void setQWForFiles(Writer writer) {
		qw = new CountingQuietWriter(writer, errorHandler);
	}

	// 2013.09.27 yamagishi add. start
	/**
	 * The <b>RollOverDow</b> takes a string from 1:Sunday to 7:Saturday
	 * This options determines the weekly rollover schedule.
	 */
	public void setRollOverDow(String dowCode) {
		rollOverDow = dowCode;
	}

	/** Returns the value of the <b>RollOverDow</b> option. */
	public String getRollOverDow() {
		return rollOverDow;
	}

	/** Returns the Integer value of the <b>RollOverDow</b> option. */
	public Integer getIntRollOverDow() {
		if (rollOverDow == null || rollOverDow.length() <= 0) {
			return null;
		}
		return Integer.valueOf(rollOverDow);
	}
	// 2013.09.27 yamagishi add. end

	/**
	 * The <b>DatePattern</b> takes a string in the same format as expected by
	 * {@link SimpleDateFormat}.
	 */
	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	/** Returns the value of the <b>DatePattern</b> option. */
	public String getDatePattern() {
		return datePattern;
	}
}
