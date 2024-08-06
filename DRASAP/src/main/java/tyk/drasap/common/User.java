package tyk.drasap.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import tyk.drasap.search.SearchResultElement;

/**
 * ログインしたユーザーを表すクラス。
 * ユーザー管理マスターテーブル(USER_MASTER)に対応。
 * @author FUMI
 * @version 2013/09/13 yamagishi
 */
public class User {
	//	private static Category category = Category.getInstance(User.class.getName());
	// 2013.08.03 yamagishi add. start
	//    private static DataSource ds;
	//
	//    static {
	//        try {
	//            ds = DataSourceFactory.getOracleDataSource();
	//        } catch (Exception e) {
	//        }
	//    }
	// 2013.08.03 yamagishi add. end

	String id = "";// ユーザーID
	String name = "";// 名前(和)
	String nameE = "";// 名前(英)
	String host = "";// ホスト名 request#getRemoteAddrで取得
	String dept = "";// 原価部門
	String deptName = "";// 原価部門名
	String defaultUserGroup = "";// デフォルトのユーザーグループ。所属している原価部門から導かれる。
	ArrayList<UserGroup> userGroups = new ArrayList<UserGroup>();// 所属しているユーザーグループ。内部はUserGroup。
	ArrayList<Printer> enablePrinters = new ArrayList<Printer>();// 利用可能なプリンター。内部はPrinter。
	String displayCount = "";// 検索の1ページ当たりの表示件数
	String thumbnailSize = "M";// サムネイルサイズ
	public static int searchSelColNum = 10;
	public static int viewSelColNum = 12;
	private ArrayList<String> searchSelColList = new ArrayList<>(); // 検索条件カラムリスト
	private ArrayList<String> viewSelColList = new ArrayList<>(); // 検索結果カラムリスト
	// 2013.07.24 yamagishi add. start
	private String aclUpdateFlag = "";
	private String aclBatchUpdateFlag = "";
	private String dlManagerFlag = "";
	// 2013.07.24 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	private Date passwdUpdDate = null;
	private String reproUserFlag = "";
	private String dwgRegReqFlag = "";
	// 2019.09.20 yamamoto add. end
	// 2020.02.10 yamamoto add. start
	private String multiPdfFlag = "";
	// 2020.02.10 yamamoto add. end
	private String language = "Japanese";
	private boolean onlyNewest = false;
	//	String language = "English";
	boolean admin = false;// 管理者なら true
	boolean delAdmin = false;// 管理者なら true
	int position;// 職位
	// ユーザーの権限を表す。
	// 所属しているユーザーグループによって制御される。
	boolean viewStamp;// VIEWのスタンプ。trueならスタンプする
	boolean eucStamp;// EUCプリンタのスタンプ。trueならスタンプする
	boolean pltrStamp;// 専用プリンタのスタンプ。trueならスタンプする
	boolean reqImport;// 図面登録依頼可能ならtrue。
	boolean reqPrint;// 図面出力指示可能ならtrue。
	boolean reqCheckout;// 原図借用依頼可能ならtrue。
	boolean reqOther;// 図面以外焼付依頼可能ならtrue。
	// これ以降の xxxxxDisplay は、図面検索の画面において使用可能かを表す。
	// trueなら使用可能。
	boolean drwgNoDisplay;// 図番を表示可能ならtrue。
	boolean createDateDisplay;// 作成日時を表示可能ならtrue。
	boolean createUserDisplay;// 作成者を表示可能ならtrue。
	boolean machineJpDisplay;// 装置名称(和文)を表示可能ならtrue。
	boolean machineEnDisplay;// 装置名称(英文)を表示可能ならtrue。
	boolean usedForDisplay;// 用途を表示可能ならtrue。
	boolean materialDisplay;// 材質を表示可能ならtrue。
	boolean treatmentDisplay;// 熱・表面処理を表示可能ならtrue。
	boolean procurementDisplay;// 調達区分を表示可能ならtrue。
	boolean supplyerJpDisplay;// メーカー名(和文)を表示可能ならtrue。
	boolean supplyerEnDisplay;// メーカー名(英文)を表示可能ならtrue。
	boolean supplyerTypeDisplay;// メーカー形式を表示可能ならtrue。
	boolean attach01Display;// 添付図番1を表示可能ならtrue。
	boolean attach02Display;// 添付図番2を表示可能ならtrue。
	boolean attach03Display;// 添付図番3を表示可能ならtrue。
	boolean attach04Display;// 添付図番4を表示可能ならtrue。
	boolean attach05Display;// 添付図番5を表示可能ならtrue。
	boolean attach06Display;// 添付図番6を表示可能ならtrue。
	boolean attach07Display;// 添付図番7を表示可能ならtrue。
	boolean attach08Display;// 添付図番8を表示可能ならtrue。
	boolean attach09Display;// 添付図番9を表示可能ならtrue。
	boolean attach10Display;// 添付図番10を表示可能ならtrue。
	boolean machineNoDisplay;// 装置Noを表示可能ならtrue。
	boolean machineNameDisplay;// 機種名称を表示可能ならtrue。
	boolean machineSpec1Display;// 装置仕様1を表示可能ならtrue。
	boolean machineSpec2Display;// 装置仕様2を表示可能ならtrue。
	boolean machineSpec3Display;// 装置仕様3を表示可能ならtrue。
	boolean machineSpec4Display;// 装置仕様4を表示可能ならtrue。
	boolean machineSpec5Display;// 装置仕様5を表示可能ならtrue。
	boolean drwgTypeDisplay;// 図面種類を表示可能ならtrue。
	boolean drwgSizeDisplay;// 図面サイズを表示可能ならtrue。
	boolean issueDisplay;// 提出区分を表示可能ならtrue。
	boolean supplyDisplay;// 消耗区分を表示可能ならtrue。
	boolean cadTypeDisplay;// CAD種別を表示可能ならtrue。
	boolean engineerDisplay;// 設計者名を表示可能ならtrue。
	boolean prohibitDisplay;// 使用禁止区分を表示可能ならtrue。
	boolean prohibitDateDisplay;// 使用禁止日時を表示可能ならtrue。
	boolean prohibitEmpnoDisplay;// 使用禁止者職番を表示可能ならtrue。
	boolean prohibitNameDisplay;// 使用禁止者名前を表示可能ならtrue。
	boolean pagesDisplay;// ページ数を表示可能ならtrue。
	boolean aclDisplay;// アクセスレベルを表示可能ならtrue。
	boolean aclUpdateDisplay;// アクセスレベル変更日時を表示可能ならtrue。
	boolean aclEmpnoDisplay;// アクセスレベル最終変更者職番を表示可能ならtrue。
	boolean aclNameDisplay;// アクセスレベル最終変更者名前を表示可能ならtrue。
	boolean attachMaxDisplay;// 添付図数を表示可能ならtrue。
	boolean latestDisplay;// 最新図番区分を表示可能ならtrue。
	boolean replaceDisplay;// 差替フラグを表示可能ならtrue。
	boolean createDivDisplay;// 作成部署コードを表示可能ならtrue。
	boolean mediaIdDisplay;// メディアIDを表示可能ならtrue。
	boolean twinDrwgNoDisplay;// 1物2品番図番を表示可能ならtrue。
	// 閲覧フォーマットをアクセスレベル毎に持つ
	// key=アクセスレベル  value= 1：TIFF、2：PDF
	HashMap<String, String> viewPrintDoc = new HashMap<String, String>();
	// アクセス権限をHashMapで持つ
	// key=アクセスレベル value=アクセスレベル値{1,2,3}
	HashMap<String, String> aclMap = new HashMap<String, String>();
	String maxAclValue = "0";// このユーザーが持つ最高のアクセスレベル値・・・検索開始ボタンのロックで使用する
	private String sys_id = null;

	// ------------------------------------------ コンストラクター
	/**
	 * @param newHost ホスト名
	 */
	public User(String newHost) {
		host = newHost;
	}

	// ------------------------------------------ Method
	/**
	 * ユーザーがもつ利用者グループ権限をたす。
	 * この処理中でユーザーの権限も変更されていく。
	 * 権限の基本は、広く取る。ユーザーグループの1つでもtrueなら、ユーザーとしてtrueに。
	 * しかしスタンプ処理に関しては、所属の原価部門から導かれる権限と同一にする。
	 * 使用可能なプリンタに関しても、この処理中で行われる。
	 * @param userGroup
	 * @throws Exception
	 */
	public void addUserGroup(UserGroup userGroup) throws Exception {
		userGroups.add(userGroup);
		// ユーザーグループの権限を元に、ユーザーの権限をセットする
		// 基本は権限を広くとる。
		reqImport = reqImport || userGroup.reqImport;
		reqPrint = reqPrint || userGroup.reqPrint;
		reqCheckout = reqCheckout || userGroup.reqCheckout;
		reqOther = reqOther || userGroup.reqOther;
		drwgNoDisplay = drwgNoDisplay || userGroup.drwgNoDisplay;
		createDateDisplay = createDateDisplay || userGroup.createDateDisplay;
		createUserDisplay = createUserDisplay || userGroup.createUserDisplay;
		machineJpDisplay = machineJpDisplay || userGroup.machineJpDisplay;
		machineEnDisplay = machineEnDisplay || userGroup.machineEnDisplay;
		usedForDisplay = usedForDisplay || userGroup.usedForDisplay;
		materialDisplay = materialDisplay || userGroup.materialDisplay;
		treatmentDisplay = treatmentDisplay || userGroup.treatmentDisplay;
		procurementDisplay = procurementDisplay || userGroup.procurementDisplay;
		supplyerJpDisplay = supplyerJpDisplay || userGroup.supplyerJpDisplay;
		supplyerEnDisplay = supplyerEnDisplay || userGroup.supplyerEnDisplay;
		supplyerTypeDisplay = supplyerTypeDisplay || userGroup.supplyerTypeDisplay;
		attach01Display = attach01Display || userGroup.attach01Display;
		attach02Display = attach02Display || userGroup.attach02Display;
		attach03Display = attach03Display || userGroup.attach03Display;
		attach04Display = attach04Display || userGroup.attach04Display;
		attach05Display = attach05Display || userGroup.attach05Display;
		attach06Display = attach06Display || userGroup.attach06Display;
		attach07Display = attach07Display || userGroup.attach07Display;
		attach08Display = attach08Display || userGroup.attach08Display;
		attach09Display = attach09Display || userGroup.attach09Display;
		attach10Display = attach10Display || userGroup.attach10Display;
		machineNoDisplay = machineNoDisplay || userGroup.machineNoDisplay;
		machineNameDisplay = machineNameDisplay || userGroup.machineNameDisplay;
		machineSpec1Display = machineSpec1Display || userGroup.machineSpec1Display;
		machineSpec2Display = machineSpec2Display || userGroup.machineSpec2Display;
		machineSpec3Display = machineSpec3Display || userGroup.machineSpec3Display;
		machineSpec4Display = machineSpec4Display || userGroup.machineSpec4Display;
		machineSpec5Display = machineSpec5Display || userGroup.machineSpec5Display;
		drwgTypeDisplay = drwgTypeDisplay || userGroup.drwgTypeDisplay;
		drwgSizeDisplay = drwgSizeDisplay || userGroup.drwgSizeDisplay;
		issueDisplay = issueDisplay || userGroup.issueDisplay;
		supplyDisplay = supplyDisplay || userGroup.supplyDisplay;
		cadTypeDisplay = cadTypeDisplay || userGroup.cadTypeDisplay;
		engineerDisplay = engineerDisplay || userGroup.engineerDisplay;
		prohibitDisplay = prohibitDisplay || userGroup.prohibitDisplay;
		prohibitDateDisplay = prohibitDateDisplay || userGroup.prohibitDateDisplay;
		prohibitEmpnoDisplay = prohibitEmpnoDisplay || userGroup.prohibitEmpnoDisplay;
		prohibitNameDisplay = prohibitNameDisplay || userGroup.prohibitNameDisplay;
		pagesDisplay = pagesDisplay || userGroup.pagesDisplay;
		aclDisplay = aclDisplay || userGroup.aclDisplay;
		aclUpdateDisplay = aclUpdateDisplay || userGroup.aclUpdateDisplay;
		aclEmpnoDisplay = aclEmpnoDisplay || userGroup.aclEmpnoDisplay;
		aclNameDisplay = aclNameDisplay || userGroup.aclNameDisplay;
		attachMaxDisplay = attachMaxDisplay || userGroup.attachMaxDisplay;
		latestDisplay = latestDisplay || userGroup.latestDisplay;
		replaceDisplay = replaceDisplay || userGroup.replaceDisplay;
		createDivDisplay = createDivDisplay || userGroup.createDivDisplay;
		mediaIdDisplay = mediaIdDisplay || userGroup.mediaIdDisplay;
		twinDrwgNoDisplay = twinDrwgNoDisplay || userGroup.twinDrwgNoDisplay;
		// スタンプ処理区分に関しては、デフォルトのユーザーグループの値とする
		//
		// 訂正：スタンプ処理区分に関しても権限を広くとる。'04.May.12 by Hirata
		viewStamp = viewStamp || userGroup.viewStamp;// VIEWのスタンプ。trueならスタンプする
		eucStamp = eucStamp || userGroup.eucStamp;// EUCプリンタのスタンプ。trueならスタンプする
		pltrStamp = pltrStamp || userGroup.pltrStamp;// 専用プリンタのスタンプ。trueならスタンプする
		// この利用者グループが使用可能なプリンタを、ユーザーの使用可能なプリンタとしてセットする。

		for (int i = 0; i < userGroup.enablePrinters.size(); i++) {
			Printer printer = userGroup.enablePrinters.get(i);// 利用者グループが使用可能なプリンタ
			// すでに同じプリンタが登録されていないか確認する
			boolean registered = false;// すでに登録ずみプリンタならtrue
			for (int j = 0; j < enablePrinters.size(); j++) {
				// プリンタIDを比較する
				if (printer.getId().equals(enablePrinters.get(j).getId())) {
					registered = true;
					break;
				}
			}
			if (!registered) {// 未登録なら、ユーザーの使用可能なプリンタとしてセットする。
				enablePrinters.add(printer);
			}
		}
		// 利用者グループのアクセスレベルを、このユーザーに移す。
		// このとき広くなるように移す
		Iterator<String> keyIterator = userGroup.aclMap.keySet().iterator();// ユーザーグループのアクセスレベルのキー
		while (keyIterator.hasNext()) {
			String aclId = keyIterator.next();// ユーザーグループのアクセスレベルのキー
			String grpAclValue = userGroup.aclMap.get(aclId);// このグループの設定値
			if (aclMap.containsKey(aclId)) {
				// すでに同じアクセスレベルが設定されていたら
				String uesrAclValue = aclMap.get(aclId);// ユーザーの設定値
				if (grpAclValue.compareTo(uesrAclValue) == 0) {
					// グループに設定されている設定値が同じの場合、"TIFF"を優先
					if ("1".equals(userGroup.viewPrintDoc)) {
						viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
					}
				}
				if (grpAclValue.compareTo(uesrAclValue) > 0) {
					// グループに設定されている設定値が大きければ、ユーザーにセットし直す
					aclMap.put(aclId, grpAclValue);
					viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
				}
			} else {
				// まだこのアクセスレベルが未設定なら
				aclMap.put(aclId, grpAclValue);
				viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
			}
			// このユーザーが持つ最高のアクセスレベルをセット
			if (grpAclValue.compareTo(maxAclValue) > 0) {
				maxAclValue = grpAclValue;
			}
		}
	}

	// 2013.08.03 yamagishi add. start
	/**
	 * isPrintableのオーバロードメソッド。Connectionがない場合に対応。
	 * ※connectionの管理はActionで行う為、多用しないこと。
	 * @see isPrintable(SearchResultElement searchResultElement, Connection conn)
	 */

	//    @Deprecated
	//    public boolean isPrintable(SearchResultElement searchResultElement) {
	//        Connection conn = null;
	//        try {
	//            // DBから取得
	//            conn = ds.getConnection();
	//            return isPrintable(searchResultElement, conn, false);
	//        } catch (Exception e) {
	//            return false;
	//        } finally {
	//            if (conn != null) {
	//                try {
	//                    conn.close();
	//                } catch (Exception e) {
	//                }
	//            }
	//        }
	//    }
	// 2013.08.03 yamagishi add. end
	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * このユーザーが、この図面を印刷可能かを返す。
	//	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	//	 * 1) 印刷権をもつか・・・アクセスレベル値が2以上またはAdmin権限を持つ
	//	 * 2) Tiffであるか
	//	 * @param searchResultElement 指定した図面
	//	 * @return View時にTiffで表示ならtrue。印刷不可のPDFならfalse。
	//	 */
	//	public boolean isPrintable(SearchResultElement searchResultElement){
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 2
	//			&& ! isAdmin()){
	//			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
	//			// 2より小さい・・・印刷権がない
	//			// かつ、管理者でない
	//			return false;
	//
	//		} else if(! "1".equals(searchResultElement.getFileType())){
	//			// 図面タイプがTiffでなければ、印刷不可
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * このユーザーが、この図面を印刷可能かを返す。
	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	 * 1) 印刷権をもつか・・・アクセスレベル値が2以上またはAdmin権限を持つ
	 * 2) Tiffであるか
	 * @param searchResultElement 指定した図面
	 * @param conn
	 * @return View時にTiffで表示ならtrue。印刷不可のPDFならfalse。
	 */
	public boolean isPrintable(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DBから取得
		return this.isPrintable(searchResultElement, conn, false);
	}

	/**
	 * このユーザーが、この図面を印刷可能かを返す。
	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	 * 1) 印刷権をもつか・・・アクセスレベル値が2以上またはAdmin権限を持つ
	 * 2) Tiffであるか
	 * @param searchResultElement 指定した図面
	 * @param conn
	 * @param refSession デフォルトはfalse
	 * 			true: セッションからキャッシュを取得, false: DBから取得
	 * @return View時にTiffで表示ならtrue。印刷不可のPDFならfalse。
	 */
	public boolean isPrintable(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// アクセスレベル取得
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 2
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 2
				&& !isAdmin() || !"1".equals(searchResultElement.getFileType())) {
			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
			// 2より小さい・・・印刷権がない
			// かつ、管理者でない
			// 図面タイプがTiffでなければ、印刷不可
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * このユーザーが、指定プリンタで印刷可能かを返す。
	//	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	//	 * 1) 印刷権をもつか・・・アクセスレベル値が1以上またはAdmin権限を持つ
	//	 * 2) Tiffであるか
	//	 * @param searchResultElement 指定した図面
	//	 * @return 指定プリンタで印刷可能なら true
	//	 */
	//	public boolean isPrintableByReq(SearchResultElement searchResultElement){
	/* OCEのための特別なログ出力 **************************
	category.debug("指定された図面は " + searchResultElement.getDrwgNo() + "、ACL_ID=" + searchResultElement.getAttr("ACL_ID"));
	category.debug("このときのユーザーのAclMapは");
	Iterator tempKeyItr = aclMap.keySet().iterator();
	while(tempKeyItr.hasNext()){
		Object aclId = tempKeyItr.next();
		category.debug("ACL_ID=" + (String)aclId + ", ACL_VALUE=" + (String)aclMap.get(aclId));
	}
	category.debug("この結果求められるAclValueは" + (String)this.aclMap.get(searchResultElement.getAttr("ACL_ID")));
	*******************************************************/
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 1
	//			&& ! isAdmin()){
	//			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
	//			// 1より小さい・・・検索参照権がない。検索参照権限があれば指定プリンタで印刷可能。'04.May.6変更
	//			// かつ、管理者でない
	//			return false;
	//
	//		} else if(! "1".equals(searchResultElement.getFileType())){
	//			// 図面タイプがTiffでなければ、印刷不可
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * このユーザーが、指定プリンタで印刷可能かを返す。
	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	 * 1) 印刷権をもつか・・・アクセスレベル値が1以上またはAdmin権限を持つ
	 * 2) Tiffであるか
	 * @param searchResultElement 指定した図面
	 * @param conn
	 * @return 指定プリンタで印刷可能なら true
	 */
	public boolean isPrintableByReq(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DBから取得
		return this.isPrintableByReq(searchResultElement, conn, false);
	}

	/**
	 * このユーザーが、指定プリンタで印刷可能かを返す。
	 * View時にTiffで表示ならtrue。印刷不可のPDFならfalse。'04.May.6変更
	 * 1) 印刷権をもつか・・・アクセスレベル値が1以上またはAdmin権限を持つ
	 * 2) Tiffであるか
	 * @param searchResultElement 指定した図面
	 * @param conn
	 * @param refSession デフォルトはfalse
	 * 			true: セッションからキャッシュを取得, false: DBから取得
	 * @return 指定プリンタで印刷可能なら true
	 */
	public boolean isPrintableByReq(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// アクセスレベル取得
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 1
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 1
				&& !isAdmin() || !"1".equals(searchResultElement.getFileType())) {
			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
			// 1より小さい・・・検索参照権がない。検索参照権限があれば指定プリンタで印刷可能。'04.May.6変更
			// かつ、管理者でない
			// 図面タイプがTiffでなければ、印刷不可
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * 指定された図番が、このユーザーで変更可能か?
	//	 * @param searchResultElement
	//	 * @return
	//	 */
	//	public boolean isChangableAclv(SearchResultElement searchResultElement){
	//		if(Integer.parseInt(this.aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 3
	//			&& ! isAdmin()){
	//			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
	//			// 3より小さい・・・変更権がない
	//			// かつ、管理者でない
	//			return false;
	//		}
	//		return true;
	//	}
	/**
	 * 指定された図番が、このユーザーで変更可能か?
	 * @param searchResultElement
	 * @param conn
	 * @return
	 */
	public boolean isChangableAclv(SearchResultElement searchResultElement, Connection conn) throws Exception {
		// DBから取得
		return this.isChangableAclv(searchResultElement, conn);
	}

	/**
	 * 指定された図番が、このユーザーで変更可能か?
	 * @param searchResultElement
	 * @param conn
	 * @param refSession デフォルトはfalse
	 * 			true: セッションからキャッシュを取得, false: DBから取得
	 * @return
	 */
	public boolean isChangableAclv(SearchResultElement searchResultElement, Connection conn, boolean refSession) throws Exception {
		// アクセスレベル取得
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		//		if (Integer.parseInt(this.getAclMap(conn).get(searchResultElement.getAttr("ACL_ID"))) < 3
		//				&& !isAdmin()) {
		if (Integer.parseInt(aclMap.get(searchResultElement.getAttr("ACL_ID"))) < 3
				&& !isAdmin()) {
			// 図面のアクセスレベルIDから取得できる、このユーザーのアクセルレベル値が
			// 3より小さい・・・変更権がない
			// かつ、管理者でない
			return false;
		}
		return true;
	}
	// 2013.06.25 yamagishi modified. end

	/**
	 * このユーザーが、アクセスレベルを変更可能ならture。
	 * 「000 < ユーザーの職位 <= 特定職位」の条件を満たす必要がある。
	 * @param drasapInfo
	 * @return
	 */
	public boolean hasChangableAclvAuth(DrasapInfo drasapInfo) {
		// 条件を変更。'04.May.19変更 by Hirata
		return 0 < position &&
				position <= drasapInfo.getAclvChangablePosition();
		//return (this.admin || this.position >= drasapInfo.getAclvChangablePosition());
	}

	/**
	 * このユーザーの使用可能なプリンター一覧から、指定されたプリンターのオブジェクトを返す
	 * @param printerId
	 * @return
	 */
	public Printer getPrinter(String printerId) {
		for (int i = 0; i < getEnablePrinters().size(); i++) {
			Printer printer = getEnablePrinters().get(i);
			if (printer.getId().equals(printerId)) {
				return printer;
			}
		}
		return null;
	}
	// ------------------------------------------ getter,setter

	/**
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getNameE() {
		return nameE;
	}

	/**
	 * @param string
	 */
	public void setId(String newId) {
		id = newId;
		if (id == null) {
			id = "";
		}
	}

	/**
	 * @param string
	 */
	public void setName(String newName) {
		name = newName;
		if (name == null) {
			name = "";
		}
	}

	/**
	 * @param string
	 */
	public void setNameE(String newNameE) {
		nameE = newNameE;
		if (nameE == null) {
			nameE = "";
		}
	}

	/**
	 * @return
	 */
	public String getDefaultUserGroup() {
		return defaultUserGroup;
	}

	/**
	 * @param string
	 */
	public void setDefaultUserGroup(String string) {
		defaultUserGroup = string;
		if (defaultUserGroup == null) {
			defaultUserGroup = "";
		}
	}

	/**
	 * @return
	 */
	public String getDept() {
		return dept;
	}

	/**
	 * @param string
	 */
	public void setDept(String string) {
		dept = string;
		if (dept == null) {
			dept = "";
		}
	}

	/**
	 * @return
	 */
	public String getSearchSelCol(int idx) {
		return searchSelColList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getSearchSelColList() {
		return searchSelColList;
	}

	/**
	 * @param string
	 */
	public void setSearchSelCol(int idx, String string) {
		String val = StringUtils.isEmpty(string) ? "" : string;
		searchSelColList.add(idx, val);
	}

	/**
	 * @param string
	 */
	public void setSearchSelColList(ArrayList<String> list) {
		searchSelColList = list;
	}

	/**
	 * @return
	 */
	public String getThumbnailSize() {
		return thumbnailSize;
	}

	/**
	 * @param string
	 */
	public void setThumbnailSize(String thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	/**
	 * @return
	 */
	public String getDisplayCount() {
		return displayCount;
	}

	/**
	 * @param string
	 */
	public void setDisplayCount(String string) {
		displayCount = string;
		if (displayCount == null) {
			displayCount = "";
		}
	}

	// 2013.06.25 yamagishi modified. start
	/**
	 * DBからの取得をデフォルトとする為、パッケージに変更
	 * @return
	 */
	//	public HashMap<String, String> getAclMap() {
	HashMap<String, String> getAclMap() {
		return aclMap;
	}

	// 2013.06.25 yamagishi modified. end
	// 2013.06.25 yamagishi add. start
	/**
	 * @return aclMap
	 */
	public HashMap<String, String> getAclMap(Connection conn) throws Exception {
		// ユーザーが持つアクセスレベルのセットを取得
		aclMap = resetUserAcl(UserGrpAclRelationDB.getAclList(id, conn));
		return aclMap;
	}
	// 2013.06.25 yamagishi add. end

	/**
	 * @return
	 */
	public String getMaxAclValue() {
		return maxAclValue;
	}

	/**
	 * @return
	 */
	public boolean isReqImport() {
		return reqImport;
	}

	/**
	 * @return
	 */
	public boolean isReqPrint() {
		return reqPrint;
	}

	/**
	 * @return
	 */
	public boolean isReqOther() {
		return reqOther;
	}

	/**
	 * @return
	 */
	public boolean isAclDisplay() {
		return aclDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclEmpnoDisplay() {
		return aclEmpnoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclNameDisplay() {
		return aclNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAclUpdateDisplay() {
		return aclUpdateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isAttach01Display() {
		return attach01Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach02Display() {
		return attach02Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach03Display() {
		return attach03Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach04Display() {
		return attach04Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach05Display() {
		return attach05Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach06Display() {
		return attach06Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach07Display() {
		return attach07Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach08Display() {
		return attach08Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach09Display() {
		return attach09Display;
	}

	/**
	 * @return
	 */
	public boolean isAttach10Display() {
		return attach10Display;
	}

	/**
	 * @return
	 */
	public boolean isAttachMaxDisplay() {
		return attachMaxDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCadTypeDisplay() {
		return cadTypeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateDateDisplay() {
		return createDateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateDivDisplay() {
		return createDivDisplay;
	}

	/**
	 * @return
	 */
	public boolean isCreateUserDisplay() {
		return createUserDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgNoDisplay() {
		return drwgNoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgSizeDisplay() {
		return drwgSizeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isDrwgTypeDisplay() {
		return drwgTypeDisplay;
	}

	/**
	 * @return
	 */
	public boolean isEngineerDisplay() {
		return engineerDisplay;
	}

	/**
	 * @return
	 */
	public boolean isEucStamp() {
		return eucStamp;
	}

	/**
	 * @return
	 */
	public boolean isIssueDisplay() {
		return issueDisplay;
	}

	/**
	 * @return
	 */
	public boolean isLatestDisplay() {
		return latestDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineEnDisplay() {
		return machineEnDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineJpDisplay() {
		return machineJpDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineNameDisplay() {
		return machineNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineNoDisplay() {
		return machineNoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec1Display() {
		return machineSpec1Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec2Display() {
		return machineSpec2Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec3Display() {
		return machineSpec3Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec4Display() {
		return machineSpec4Display;
	}

	/**
	 * @return
	 */
	public boolean isMachineSpec5Display() {
		return machineSpec5Display;
	}

	/**
	 * @return
	 */
	public boolean isMaterialDisplay() {
		return materialDisplay;
	}

	/**
	 * @return
	 */
	public boolean isMediaIdDisplay() {
		return mediaIdDisplay;
	}

	/**
	 * @return
	 */
	public boolean isPagesDisplay() {
		return pagesDisplay;
	}

	/**
	 * @return
	 */
	public boolean isPltrStamp() {
		return pltrStamp;
	}

	/**
	 * @return
	 */
	public boolean isProcurementDisplay() {
		return procurementDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitDateDisplay() {
		return prohibitDateDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitDisplay() {
		return prohibitDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitEmpnoDisplay() {
		return prohibitEmpnoDisplay;
	}

	/**
	 * @return
	 */
	public boolean isProhibitNameDisplay() {
		return prohibitNameDisplay;
	}

	/**
	 * @return
	 */
	public boolean isReplaceDisplay() {
		return replaceDisplay;
	}

	/**
	 * @return
	 */
	public boolean isReqCheckout() {
		return reqCheckout;
	}

	/**
	 * @return
	 */
	public boolean isSupplyDisplay() {
		return supplyDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerEnDisplay() {
		return supplyerEnDisplay;
	}

	/**
	 * @return
	 */
	public boolean isTreatmentDisplay() {
		return treatmentDisplay;
	}

	/**
	 * @return
	 */
	public boolean isUsedForDisplay() {
		return usedForDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerJpDisplay() {
		return supplyerJpDisplay;
	}

	/**
	 * @return
	 */
	public boolean isSupplyerTypeDisplay() {
		return supplyerTypeDisplay;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAllEmptyViewSelCol() {
		boolean ret = true;
		for (int i = 0; i < viewSelColList.size(); i++) {
			ret = ret & StringUtils.isEmpty(viewSelColList.get(i));
			if (!ret) {
				break;
			}
		}
		return ret;
	}

	/**
	 * @return
	 */
	public String getViewSelCol(int idx) {
		return viewSelColList.get(idx);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getViewSelColList() {
		return viewSelColList;
	}

	/**
	 * @param string
	 */
	public void setViewSelCol(int idx, String string) {
		String val = StringUtils.isEmpty(string) ? "" : string;
		viewSelColList.add(idx, val);
	}

	/**
	 * @param string
	 */
	public void setViewSelColList(ArrayList<String> list) {
		viewSelColList = list;
	}

	// 2013.09.13 yamagishi add. start
	/**
	 * aclUpdateFlagを取得します。
	 * @return aclUpdateFlag
	 */
	public String getAclUpdateFlag() {
		return aclUpdateFlag;
	}

	/**
	 * aclBatchUpdateFlagを取得します。
	 * @return aclBatchUpdateFlag
	 */
	public String getAclBatchUpdateFlag() {
		return aclBatchUpdateFlag;
	}

	/**
	 * dlManagerFlagを取得します。
	 * @return dlManagerFlag
	 */
	public String getDlManagerFlag() {
		return dlManagerFlag;
	}

	/**
	 * DLマネージャが利用可能か true/falseで返す。
	 */
	public boolean isDLManagerAvailable() {
		return ("1".equals(dlManagerFlag) || "2".equals(dlManagerFlag)) == true;
	}

	/**
	 * DLマネージャの保存ボタンが利用可能か返す。
	 *  1:保存可能、0:保存不可
	 */
	public String getDLManagerSaveEnabledFlag() {
		return "2".equals(dlManagerFlag) ? "1" : "0";
	}

	// 2013.09.13 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	/**
	 * passwdUpdDateを取得します。
	 * @return passwdUpdDate
	 */
	public Date getPasswdUpdDate() {
		return passwdUpdDate;
	}

	/**
	 * 原図庫ユーザか true/falseで返す。
	 */
	public boolean isReproUser() {
		return "1".equals(reproUserFlag) == true;
	}

	/**
	 * 図面登録依頼可能か true/falseで返す。
	 */
	public boolean isdwgRegReqFlag() {
		return "1".equals(dwgRegReqFlag) == true;
	}

	/**
	 * アクセスレベル一括更新作業ツールが利用可能か true/falseで返す。
	 *  @return NULL / 0：false <br/> 1：true
	 */
	public boolean isAclBatchUpdateFlag() {

		if (StringUtils.isEmpty(aclBatchUpdateFlag)) {
			return false;
		}
		return "1".equals(aclBatchUpdateFlag) == true;
	}

	// 2019.09.20 yamamoto add. end

	// 2013.07.24 yamagishi add. start
	/**
	 * aclUpdateFlagを設定します。
	 * @param aclUpdateFlag aclUpdateFlag
	 */
	public void setAclUpdateFlag(String string) {
		aclUpdateFlag = string;
		if (aclUpdateFlag == null) {
			aclUpdateFlag = "";
		}
	}

	/**
	 * aclBatchUpdateFlagを設定します。
	 * @param aclBatchUpdateFlag aclBatchUpdateFlag
	 */
	public void setAclBatchUpdateFlag(String string) {
		aclBatchUpdateFlag = string;
		if (aclBatchUpdateFlag == null) {
			aclBatchUpdateFlag = "";
		}
	}

	/**
	 * dlManagerFlagを設定します。
	 * @param dlManagerFlag dlManagerFlag
	 */
	public void setDlManagerFlag(String string) {
		dlManagerFlag = string;
		if (dlManagerFlag == null) {
			dlManagerFlag = "";
		}
	}

	// 2013.07.24 yamagishi add. end
	// 2019.09.20 yamamoto add. start
	/**
	 * passwdUpdDateを設定します。
	 * @param string
	 */
	public void setPasswdUpdDate(Date date) {
		passwdUpdDate = date;
		//		if (passwdUpdDate == null) passwdUpdDate = "";
	}

	/**
	 * reproUserFlagを設定します。
	 * @param string
	 */
	public void setReproUserFlag(String string) {
		reproUserFlag = string;
		if (reproUserFlag == null) {
			reproUserFlag = "";
		}
	}

	/**
	 * dwgRegReqFlagを設定します。
	 * @param string
	 */
	public void setDwgRegReqFlag(String string) {
		dwgRegReqFlag = string;
		if (dwgRegReqFlag == null) {
			dwgRegReqFlag = "";
		}
	}

	// 2019.09.20 yamamoto add end.
	// 2020.02.10 yamamoto add start.
	/**
	 * multiPdfFlagを設定します。
	 * @param string
	 */
	public void setMultiPdfFlag(String string) {
		multiPdfFlag = string;
	}

	/**
	 * マルチPDF出力が利用可能か true/falseで返す。
	 * @param string
	 */
	public boolean isMultiPdf() {
		return "1".equals(multiPdfFlag) == true;
	}
	// 2020.02.10 yamamoto add end.

	/**
	 * @return
	 */
	public ArrayList<Printer> getEnablePrinters() {
		return enablePrinters;
	}

	/**
	 * @return
	 */
	public boolean isViewStamp() {
		return viewStamp;
	}

	/**
	 * @return
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @return
	 */
	public boolean isDelAdmin() {
		return delAdmin;
	}

	/**
	 * 管理者フラグをセットする。1なら管理者とする。
	 * @param b
	 */
	public void setAdminFlag(String newAdminFlag) {
		admin = "1".equals(newAdminFlag);

		if ("2".equals(newAdminFlag)) {
			admin = true;
			delAdmin = true;
		}
	}

	public boolean isOnlyNewest() {
		return onlyNewest;
	}

	public void setOnlyNewestFlag(String onlyNewestFlag) {
		onlyNewest = "1".equals(onlyNewestFlag);
	}

	/**
	 * 職位を返す。intで返す。
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 職位をセットする。このときcharからintに変換する。
	 * 変換できない場合は、0をセットする。
	 * @param i
	 */
	public void setPosition(String newPosition) {
		try {
			position = Integer.parseInt(newPosition.trim());
		} catch (Exception e) {
			position = 0;
		}
	}

	/**
	 * @return
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @param string
	 */
	public void setDeptName(String string) {
		deptName = string;
		if (deptName == null) {
			deptName = "";
		}
	}

	public String getLanguage() {
		return language;
	}

	public String getLanKey() {
		if ("Japanese".equals(language)) {
			return "jp";
		}
		if ("English".equals(language)) {
			return "en";
		}
		return "jp";
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSys_id() {
		return sys_id;
	}

	public void setSys_id(String sysId) {
		sys_id = sysId;
	}

	public boolean isTwinDrwgNoDisplay() {
		return twinDrwgNoDisplay;
	}

	// 2013.06.25 yamagishi modified. start
	//	/**
	//	 * @return viewPrintDoc
	//	 */
	//	public String getViewPrintDoc(String aclId) {
	//		String aclValue = this.getAclMap().get(aclId);
	//		if (aclValue == null || aclValue.equals("1")) {
	//			return "noprintPdf";
	//		} else {
	//			if (viewPrintDoc.get(aclId).equals("1")) {
	//				return "tiff";
	//			} else if (viewPrintDoc.get(aclId).equals("2")) {
	//				return "printablePdf";
	//			} else {
	//				return null;
	//			}
	//		}
	//	}
	/**
	 * @param aclId
	 * @param conn
	 * @return viewPrintDoc
	 */
	public String getViewPrintDoc(String aclId, Connection conn) throws Exception {
		// DBから取得
		return this.getViewPrintDoc(aclId, conn, false);
	}

	/**
	 * @param aclId
	 * @param conn
	 * @param refSession デフォルトはfalse
	 * 			true: セッションからキャッシュを取得, false: DBから取得
	 * @return viewPrintDoc
	 */
	public String getViewPrintDoc(String aclId, Connection conn, boolean refSession) throws Exception {
		// アクセスレベル取得
		HashMap<String, String> aclMap = refSession ? this.getAclMap() : this.getAclMap(conn);
		String aclValue = aclMap.get(aclId);
		if (aclValue == null || "1".equals(aclValue)) {
			return "noprintPdf";
		}
		if ("1".equals(viewPrintDoc.get(aclId))) {
			return "tiff";
		}
		if ("2".equals(viewPrintDoc.get(aclId))) {
			return "printablePdf";
		}
		return null;
	}
	// 2013.06.25 yamagishi modified. end

	/**
	 * @return viewPrintDoc
	 */
	public HashMap<String, String> getViewPrintDoc() {
		return viewPrintDoc;
	}

	/**
	 * @param viewPrintDoc セットする viewPrintDoc
	 */
	public void setViewPrintDoc(HashMap<String, String> viewPrintDoc) {
		this.viewPrintDoc = viewPrintDoc;
	}

	// 2013.06.25 yamagishi add. start
	/**
	 * 利用者グループのアクセスレベルを、このユーザーに移す。
	 * このとき広くなるように移す（※再取得用、ACLの最新を反映する）
	 * @param newAclList
	 * @throws Exception
	 */
	private HashMap<String, String> resetUserAcl(ArrayList<UserGrpAclRelation> newAclList) {

		viewPrintDoc.clear(); // 設定済み閲覧フォーマットをクリア
		aclMap.clear(); // 設定済みアクセスレベルをクリア

		String aclId = null; // ユーザーグループのアクセスレベルのキー
		String grpAclValue = null; // このグループのアクセスレベル設定値
		UserGroup userGroup = null;

		// 利用者グループのアクセスレベルを、このユーザーに移す。
		// このとき広くなるように移す
		for (UserGrpAclRelation userGrpAcl : newAclList) {
			aclId = userGrpAcl.getAclId();
			grpAclValue = userGrpAcl.getAclValue();
			for (UserGroup element : userGroups) {
				if (element.cd.equals(userGrpAcl.getUserGrpCode())) {
					userGroup = element;
					break;
				}
			}

			if (aclMap.containsKey(aclId)) {
				// すでに同じアクセスレベルが設定されていたら
				String uesrAclValue = aclMap.get(aclId);// ユーザーの設定値
				if (grpAclValue.compareTo(uesrAclValue) == 0) {
					// グループに設定されている設定値が同じの場合、"TIFF"を優先
					if ("1".equals(userGroup.viewPrintDoc)) {
						viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
					}
				}
				if (grpAclValue.compareTo(uesrAclValue) > 0) {
					// グループに設定されている設定値が大きければ、ユーザーにセットし直す
					aclMap.put(aclId, grpAclValue);
					viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
				}
			} else {
				// まだこのアクセスレベルが未設定なら
				aclMap.put(aclId, grpAclValue);
				viewPrintDoc.put(aclId, userGroup.viewPrintDoc);
			}
			// このユーザーが持つ最高のアクセスレベルをセット
			if (grpAclValue.compareTo(maxAclValue) > 0) {
				maxAclValue = grpAclValue;
			}
		}
		return aclMap;
	}
	// 2013.06.25 yamagishi add. end

}
