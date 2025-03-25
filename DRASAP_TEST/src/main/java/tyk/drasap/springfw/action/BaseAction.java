package tyk.drasap.springfw.action;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import tyk.drasap.springfw.config.DataSourceManager;

public class BaseAction {
	// --------------------------------------------------------- Instance Variables
	protected Logger category;
	/** DB接続データソース */
	protected DataSource ds;
	@Autowired
	protected MessageSource messageSource;
	// --------------------------------------------------------- Methods

	/** コンストラクタ */
	public BaseAction() {
		// ロガーを初期化
		category = Logger.getLogger(this.getClass().getName());

		// DataSource取得
		ds = DataSourceManager.getInstance();
	}

	public void setMessageSource(MessageSource ms) {
		if (Objects.isNull(messageSource)) {
			messageSource = ms;
		}
	}
}
