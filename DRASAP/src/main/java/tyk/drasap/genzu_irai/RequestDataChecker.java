package tyk.drasap.genzu_irai;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;

/**
 * 原図庫作業依頼の1行ずつのデータチェックを行うクラス。
 * StrategyパターンのStrategy役。
 * @author fumi
 */
public interface RequestDataChecker {
	/**
	 * 原図庫作業依頼の1行ずつのデータチェックを行う。
	 * @param lineData
	 * @param requestForm
	 * @param errors
	 * @param flag
	 * @param kaisyuFlag
	 * @param goukiFlag
	 * @param lenghFlag
	 * @param iraiFlag
	 * @param zumenFlag
	 * @param busuuFlag
	 * @param sizeFlag
	 * @param suryoFlag
	 */
	public void checkLineData(RequestFormLineData lineData, RequestForm requestForm, Model errors,
			HashSet<String> usedErrMsgSet, Logger category);

}
