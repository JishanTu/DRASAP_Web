package tyk.drasap.springfw.cleanup;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import tyk.drasap.common.ErrorUtility;
import tyk.drasap.springfw.config.DataSourceManager;

@Component
public class ResourceCleanup {
	/** Logger（log4j） */
	private static Logger category = Logger.getLogger(DataSourceManager.class.getName());

	@PreDestroy
	public void cleanup() {
		System.out.println("ResourceCleanup start ......");

		// 1. Oracle JDBC の Timer スレッドを停止
		stopOracleJdbcTimers();

		// 2. JDBC ドライバーの解除
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				System.out.println("Deregistered JDBC driver: " + driver);
				category.info("Deregistered JDBC driver: " + driver);
			} catch (SQLException e) {
				category.error("Deregistered JDBC driver: " + ErrorUtility.error2String(e));
			}
		}

		// 3. ExecutorService のシャットダウン (もしスレッドプールを使用している場合)
		shutdownExecutors();

		System.out.println("ResourceCleanup completed.");
	}

	// Oracle JDBC の Timer スレッドを停止するメソッド
	private void stopOracleJdbcTimers() {
		try {
			java.lang.reflect.Field field = Class.forName("oracle.jdbc.diagnostics.Diagnostic").getDeclaredField("CLOCK");
			field.setAccessible(true);
			Object timer = field.get(null);
			if (timer instanceof Timer) {
				((Timer) timer).cancel();
				System.out.println("Oracle JDBC Timer thread stopped.");
				category.info("Oracle JDBC Timer thread stopped.");
			}
		} catch (Exception e) {
			category.error("Failed to stop Oracle JDBC Timer thread: " + ErrorUtility.error2String(e));
		}
	}

	// ExecutorService を安全にシャットダウンするメソッド (必要なら)
	private void shutdownExecutors() {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().contains("oracle")) {
				System.out.println("Found running Oracle thread: " + t.getName());
				category.info("Found running Oracle thread: " + t.getName());
			}
		}
	}
}
