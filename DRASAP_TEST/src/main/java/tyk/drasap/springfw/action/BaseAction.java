package tyk.drasap.springfw.action;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import tyk.drasap.common.DataSourceFactory;
import tyk.drasap.common.ErrorUtility;

public class BaseAction {
	protected static DataSource ds;
	static {
		try {
			ds = DataSourceFactory.getOracleDataSource();
		} catch (Exception e) {
			//category.error("DataSource‚ÌŽæ“¾‚ÉŽ¸”s\n" + ErrorUtility.error2String(e));
			System.out.print("!!!! DataSource‚ÌŽæ“¾‚ÉŽ¸”s\n" + ErrorUtility.error2String(e));
		}
	}
	// --------------------------------------------------------- Instance Variables
	protected Logger category;
	@Autowired
	protected MessageSource messageSource;
	// --------------------------------------------------------- Methods

	public BaseAction() {
		category = Logger.getLogger(this.getClass().getName());
	}

	public void setMessageSource(MessageSource ms) {
		if (Objects.isNull(messageSource)) {
			messageSource = ms;
		}
	}
}
