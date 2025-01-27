package tyk.drasap.springfw.cleanup;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

@Component
public class ResourceCleanup {

	@PreDestroy
	public void cleanup() {
		System.out.println("ResourceCleanup start ......");

		// JDBC ドライバーの解除
		Enumeration<java.sql.Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			java.sql.Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// その他のリソースのクリーンアップ
	}
}