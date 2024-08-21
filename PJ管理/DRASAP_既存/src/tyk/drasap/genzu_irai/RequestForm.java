package tyk.drasap.genzu_irai;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import tyk.drasap.common.StringCheck;

/** 
 * 原図庫作業依頼のForm
 */
public class RequestForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList iraiList = new ArrayList();//依頼内容のリスト
	ArrayList list = new ArrayList();//プリンタのリスト
	ArrayList genzuNameList = new ArrayList();//原図内容のリスト
	ArrayList syukusyouList = new ArrayList();//縮小リスト
	ArrayList saizuList = new ArrayList();//サイズリスト
	ArrayList syutuList = new ArrayList();//出力先リスト
	
	String irai;//依頼内容
	String syutu;//出力先
	String hiddenSyutu;
	String gouki1;//号口・号機
	String gouki2;
	String gouki3;
	String gouki4;
	String gouki5;
	String genzu1;//原図内容
	String genzu2;
	String genzu3;
	String genzu4;
	String genzu5;
	String kaisiNo1;//開始番号
	String kaisiNo2;
	String kaisiNo3;
	String kaisiNo4;
	String kaisiNo5;
	String syuuryouNo1;//終了番号
	String syuuryouNo2;
	String syuuryouNo3;
	String syuuryouNo4;
	String syuuryouNo5;
	String busuu1;//部数
	String busuu2;
	String busuu3;
	String busuu4;
	String busuu5;
	String syukusyou1;//縮小区分
	String syukusyou2;
	String syukusyou3;
	String syukusyou4;
	String syukusyou5;
	String size1;//縮小サイズ
	String size2;
	String size3;
	String size4;
	String size5;
	String hiddenNo1;//行番号のhidden
	String hiddenNo2;
	String hiddenNo3;
	String hiddenNo4;
	String hiddenNo5;
	String zumenAll; //展開した図面が全て登録済の場合は"1"にする
	String action;//アクション
	String printer_flag;//ログイン者の原価部門の利用グループ専用プリンタスタンプフラグ
	String hani_s;//範囲指定(開始)
	String hani_e;//範囲指定(終了)
	String errlog;//開始または終了番号でのエラー用
	String errNumber;//開始と終了番号のエラー用
	String hani_t;//
	String hani_sitei;//範囲指定でエラー用に使用
	
	//---------------------------------------------------------- Methods
	/**
	 * 1行目に何らかの入力があれば true
	 * @return
	 */
	public boolean isInputedLine1(){
		// 号機
		if(! "".equals(gouki1)) return true;
		// 原図内容
		if(! "".equals(genzu1)) return true;
		// 開始図番
		if(! "".equals(kaisiNo1)) return true;
		// 終了図番
		if(! "".equals(syuuryouNo1)) return true;
		// 部数
		if(! "".equals(busuu1)) return true;
		// 縮小
		if("1".equals(syukusyou1)) return true;
		// サイズ
		if(! "".equals(size1)) return true;
		
		return false;
	}
	/**
	 * 2行目に何らかの入力があれば true
	 * @return
	 */
	public boolean isInputedLine2(){
		// 号機
		if(! "".equals(gouki2)) return true;
		// 原図内容
		if(! "".equals(genzu2)) return true;
		// 開始図番
		if(! "".equals(kaisiNo2)) return true;
		// 終了図番
		if(! "".equals(syuuryouNo2)) return true;
		// 部数
		if(! "".equals(busuu2)) return true;
		// 縮小
		if("1".equals(syukusyou2)) return true;
		// サイズ
		if(! "".equals(size2)) return true;
		
		return false;
	}
	/**
	 * 3行目に何らかの入力があれば true
	 * @return
	 */
	public boolean isInputedLine3(){
		// 号機
		if(! "".equals(gouki3)) return true;
		// 原図内容
		if(! "".equals(genzu3)) return true;
		// 開始図番
		if(! "".equals(kaisiNo3)) return true;
		// 終了図番
		if(! "".equals(syuuryouNo3)) return true;
		// 部数
		if(! "".equals(busuu3)) return true;
		// 縮小
		if("1".equals(syukusyou3)) return true;
		// サイズ
		if(! "".equals(size3)) return true;
		
		return false;
	}
	/**
	 * 4行目に何らかの入力があれば true
	 * @return
	 */
	public boolean isInputedLine4(){
		// 号機
		if(! "".equals(gouki4)) return true;
		// 原図内容
		if(! "".equals(genzu4)) return true;
		// 開始図番
		if(! "".equals(kaisiNo4)) return true;
		// 終了図番
		if(! "".equals(syuuryouNo4)) return true;
		// 部数
		if(! "".equals(busuu4)) return true;
		// 縮小
		if("1".equals(syukusyou4)) return true;
		// サイズ
		if(! "".equals(size4)) return true;
		
		return false;
	}
	/**
	 * 5行目に何らかの入力があれば true
	 * @return
	 */
	public boolean isInputedLine5(){
		// 号機
		if(! "".equals(gouki5)) return true;
		// 原図内容
		if(! "".equals(genzu5)) return true;
		// 開始図番
		if(! "".equals(kaisiNo5)) return true;
		// 終了図番
		if(! "".equals(syuuryouNo5)) return true;
		// 部数
		if(! "".equals(busuu5)) return true;
		// 縮小
		if("1".equals(syukusyou5)) return true;
		// サイズ
		if(! "".equals(size5)) return true;
		
		return false;
	}
	/**
	 * 入力された文字を、半角の大文字に変更する。
	 * また図番については、ハイフン抜きに整形する。図面登録依頼、図面出力指示のときのみ。変更 '04/07/22。
	 */
	public void formatInpuedData(){
		// 号口・号機
		gouki1 = StringCheck.changeDbToSbAscii(gouki1).toUpperCase();
		gouki2 = StringCheck.changeDbToSbAscii(gouki2).toUpperCase();
		gouki3 = StringCheck.changeDbToSbAscii(gouki3).toUpperCase();
		gouki4 = StringCheck.changeDbToSbAscii(gouki4).toUpperCase();
		gouki5 = StringCheck.changeDbToSbAscii(gouki5).toUpperCase();
		// 開始番号
		kaisiNo1 = StringCheck.changeDbToSbAscii(kaisiNo1).toUpperCase();
		kaisiNo2 = StringCheck.changeDbToSbAscii(kaisiNo2).toUpperCase();
		kaisiNo3 = StringCheck.changeDbToSbAscii(kaisiNo3).toUpperCase();
		kaisiNo4 = StringCheck.changeDbToSbAscii(kaisiNo4).toUpperCase();
		kaisiNo5 = StringCheck.changeDbToSbAscii(kaisiNo5).toUpperCase();
		//終了番号
		syuuryouNo1 = StringCheck.changeDbToSbAscii(syuuryouNo1).toUpperCase();
		syuuryouNo2 = StringCheck.changeDbToSbAscii(syuuryouNo2).toUpperCase();
		syuuryouNo3 = StringCheck.changeDbToSbAscii(syuuryouNo3).toUpperCase();
		syuuryouNo4 = StringCheck.changeDbToSbAscii(syuuryouNo4).toUpperCase();
		syuuryouNo5 = StringCheck.changeDbToSbAscii(syuuryouNo5).toUpperCase();
		// 番号をハイフン抜きに整形するのは、図面登録依頼、図面出力指示のときのみ。
		// 変更 '04/07/22 by Hirata.
		if("図面登録依頼".equals(irai) || "図面出力指示".equals(irai)){			
			// 開始番号
			kaisiNo1 = changeZubanNoHyphen(kaisiNo1);
			kaisiNo2 = changeZubanNoHyphen(kaisiNo2);
			kaisiNo3 = changeZubanNoHyphen(kaisiNo3);
			kaisiNo4 = changeZubanNoHyphen(kaisiNo4);
			kaisiNo5 = changeZubanNoHyphen(kaisiNo5);
			// 終了番号
			syuuryouNo1 = changeZubanNoHyphen(syuuryouNo1);
			syuuryouNo2 = changeZubanNoHyphen(syuuryouNo2);
			syuuryouNo3 = changeZubanNoHyphen(syuuryouNo3);
			syuuryouNo4 = changeZubanNoHyphen(syuuryouNo4);
			syuuryouNo5 = changeZubanNoHyphen(syuuryouNo5);
		}
		//部数
		busuu1 = StringCheck.changeDbToSbAscii(busuu1).toUpperCase();
		busuu2 = StringCheck.changeDbToSbAscii(busuu2).toUpperCase();
		busuu3 = StringCheck.changeDbToSbAscii(busuu3).toUpperCase();
		busuu4 = StringCheck.changeDbToSbAscii(busuu4).toUpperCase();
		busuu5 = StringCheck.changeDbToSbAscii(busuu5).toUpperCase();
	}
	/**
	 * 指定された図番をハイフン抜きにして返す。
	 * @param src
	 * @return
	 */
	private String changeZubanNoHyphen(String src){
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(src, "-");
		while(st.hasMoreElements()){
			sb.append(st.nextElement());
		}
		return sb.toString();
	}
	/**
	 * チェックルーチン用の1行目データを返す
	 * @return
	 */
	public RequestFormLineData getLineData1(){
		return new RequestFormLineData(gouki1, genzu1, kaisiNo1, syuuryouNo1,
								busuu1, syukusyou1, size1);
	}
	/**
	 * チェックルーチン用の2行目データを返す
	 * @return
	 */
	public RequestFormLineData getLineData2(){
		return new RequestFormLineData(gouki2, genzu2, kaisiNo2, syuuryouNo2,
								busuu2, syukusyou2, size2);
	}
	/**
	 * チェックルーチン用の3行目データを返す
	 * @return
	 */
	public RequestFormLineData getLineData3(){
		return new RequestFormLineData(gouki3, genzu3, kaisiNo3, syuuryouNo3,
								busuu3, syukusyou3, size3);
	}
	/**
	 * チェックルーチン用の4行目データを返す
	 * @return
	 */
	public RequestFormLineData getLineData4(){
		return new RequestFormLineData(gouki4, genzu4, kaisiNo4, syuuryouNo4,
								busuu4, syukusyou4, size4);
	}
	/**
	 * チェックルーチン用の5行目データを返す
	 * @return
	 */
	public RequestFormLineData getLineData5(){
		return new RequestFormLineData(gouki5, genzu5, kaisiNo5, syuuryouNo5,
								busuu5, syukusyou5, size5);
	}
	
	/**
	 * ActionForm#resetのオーバーライド
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		gouki1="";
		gouki2="";
		gouki3="";
		gouki4="";
		gouki5="";
		kaisiNo1="";
		kaisiNo2="";
		kaisiNo3="";
		kaisiNo4="";
		kaisiNo5="";
		syuuryouNo1="";
		syuuryouNo2="";
		syuuryouNo3="";
		syuuryouNo4="";
		syuuryouNo5="";
		busuu1="";
		busuu2="";
		busuu3="";
		busuu4="";
		busuu5="";
		action = "";
		genzu1 = "";
		genzu2 = "";
		genzu3 = "";
		genzu4 = "";
		genzu5 = "";
		irai = "";
		hiddenNo1 = "";
		hiddenNo2 = "";
		hiddenNo3 = "";
		hiddenNo4 = "";
		hiddenNo5 = "";
		syukusyou1 = "";
		syukusyou2 = "";
		syukusyou3 = "";
		syukusyou4 = "";
		syukusyou5 = "";
		size1 = "";
		size2 = "";
		size3 = "";
		size4 = "";
		size5 = "";
	}

	//---------------------------------------------------------- getter,setter
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public ArrayList getGenzuNameList() {
		return genzuNameList;
	}
	public ArrayList getSyukusyouList() {
		return syukusyouList;
	}
	public ArrayList getSaizuList() {
		return saizuList;
	}
	public void setGenzuNameList(ArrayList genzuNameList) {
		this.genzuNameList = genzuNameList;
	}
	public ArrayList getSyutuList() {
		return syutuList;
	}
	public void setSyutuList(ArrayList syutuList) {
		this.syutuList = syutuList;
	}
	public void setSyukusyouList(ArrayList syukusyouList) {
		this.syukusyouList = syukusyouList;
	}
	public void setSaizuList(ArrayList saizuList) {
		this.saizuList = saizuList;
	}	
	public String getIrai(){
		return irai;
	}
	public void setIrai(String irai){
		this.irai = StringCheck.latinToUtf8(irai);
	}	
	public String getSyutu(){
		return syutu;
	}
	public void setSyutu(String syutu){
		this.syutu = StringCheck.latinToUtf8(syutu);
	}
	public String getHiddenSyutu(){
		return hiddenSyutu;
	}
	public void setHiddenSyutu(String hiddenSyutu){
		this.hiddenSyutu = StringCheck.latinToUtf8(hiddenSyutu);
	}
	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action = action;
	}
	public String getGenzu1(){
		return genzu1;
	}
	public void setGenzu1(String genzu1){
		this.genzu1 = StringCheck.latinToUtf8(genzu1);
	}
	public String getGenzu2(){
		return genzu2;
	}
	public void setGenzu2(String genzu2){
		this.genzu2 = StringCheck.latinToUtf8(genzu2);
	}
	public String getGenzu3(){
		return genzu3;
	}
	public void setGenzu3(String genzu3){
		this.genzu3 = StringCheck.latinToUtf8(genzu3);
	}	
	public String getGenzu4(){
		return genzu4;
	}
	public void setGenzu4(String genzu4){
		this.genzu4 = StringCheck.latinToUtf8(genzu4);
	}
	public String getGenzu5(){
		return genzu5;
	}
	public void setGenzu5(String genzu5){
		this.genzu5 = StringCheck.latinToUtf8(genzu5);
	}
	public String getGouki1(){
		return gouki1;
	}
	public void setGouki1(String gouki1){
		this.gouki1 = StringCheck.latinToUtf8(gouki1);
	}
	public String getGouki2(){
		return gouki2;
	}
	public void setGouki2(String gouki2){
		this.gouki2 = StringCheck.latinToUtf8(gouki2);
	}
	public String getGouki3(){
		return gouki3;
	}
	public void setGouki3(String gouki3){
		this.gouki3 = StringCheck.latinToUtf8(gouki3);
	}
	public String getGouki4(){
		return gouki4;
	}
	public void setGouki4(String gouki4){
		this.gouki4 = StringCheck.latinToUtf8(gouki4);
	}
	public String getGouki5(){
		return gouki5;
	}
	public void setGouki5(String gouki5){
		this.gouki5 = StringCheck.latinToUtf8(gouki5);
	}
	public String getKaisiNo1(){
		return kaisiNo1;
	}
	public void setKaisiNo1(String kaisiNo1){
		this.kaisiNo1 = StringCheck.latinToUtf8(kaisiNo1);
	}
	public String getKaisiNo2(){
		return kaisiNo2;
	}
	public void setKaisiNo2(String kaisiNo2){
		this.kaisiNo2 = StringCheck.latinToUtf8(kaisiNo2);
	}
	public String getKaisiNo3(){
		return kaisiNo3;
	}
	public void setKaisiNo3(String kaisiNo3){
		this.kaisiNo3 = StringCheck.latinToUtf8(kaisiNo3);
	}
	public String getKaisiNo4(){
		return kaisiNo4;
	}
	public void setKaisiNo4(String kaisiNo4){
		this.kaisiNo4 = StringCheck.latinToUtf8(kaisiNo4);
	}
	public String getKaisiNo5(){
		return kaisiNo5;
	}
	public void setKaisiNo5(String kaisiNo5){
		this.kaisiNo5 = StringCheck.latinToUtf8(kaisiNo5);
	}
	public String getSyuuryouNo1(){
		return syuuryouNo1;
	}
	public void setSyuuryouNo1(String syuuryouNo1){
		this.syuuryouNo1 = StringCheck.latinToUtf8(syuuryouNo1);
	}
	public String getSyuuryouNo2(){
		return syuuryouNo2;
	}
	public void setSyuuryouNo2(String syuuryouNo2){
		this.syuuryouNo2 = StringCheck.latinToUtf8(syuuryouNo2);
	}
	public String getSyuuryouNo3(){
		return syuuryouNo3;
	}
	public void setSyuuryouNo3(String syuuryouNo3){
		this.syuuryouNo3 = StringCheck.latinToUtf8(syuuryouNo3);
	}
	public String getSyuuryouNo4(){
		return syuuryouNo4;
	}
	public void setSyuuryouNo4(String syuuryouNo4){
		this.syuuryouNo4 = StringCheck.latinToUtf8(syuuryouNo4);
	}
	public String getSyuuryouNo5(){
		return syuuryouNo5;
	}
	public void setSyuuryouNo5(String syuuryouNo5){
		this.syuuryouNo5 = StringCheck.latinToUtf8(syuuryouNo5);
	}
	public String getBusuu1(){
		return busuu1;
	}
	public void setBusuu1(String busuu1){
		this.busuu1 = StringCheck.latinToUtf8(busuu1);
	}
	public String getBusuu2(){
		return busuu2;
	}
	public void setBusuu2(String busuu2){
		this.busuu2 = StringCheck.latinToUtf8(busuu2);
	}
	public String getBusuu3(){
		return busuu3;
	}
	public void setBusuu3(String busuu3){
		this.busuu3 = StringCheck.latinToUtf8(busuu3);
	}
	public String getBusuu4(){
		return busuu4;
	}
	public void setBusuu4(String busuu4){
		this.busuu4 = StringCheck.latinToUtf8(busuu4);
	}
	public String getBusuu5(){
		return busuu5;
	}
	public void setBusuu5(String busuu5){
		this.busuu5 = StringCheck.latinToUtf8(busuu5);
	}
	public String getSyukusyou1(){
		return syukusyou1;
	}
	public void setSyukusyou1(String syukusyou1){
		this.syukusyou1 = syukusyou1;
	}
	public String getSyukusyou2(){
		return syukusyou2;
	}
	public void setSyukusyou2(String syukusyou2){
		this.syukusyou2 = syukusyou2;
	}
	public String getSyukusyou3(){
		return syukusyou3;
	}
	public void setSyukusyou3(String syukusyou3){
		this.syukusyou3 = syukusyou3;
	}
	public String getSyukusyou4(){
		return syukusyou4;
	}
	public void setSyukusyou4(String syukusyou4){
		this.syukusyou4 = syukusyou4;
	}
	public String getSyukusyou5(){
		return syukusyou5;
	}
	public void setSyukusyou5(String syukusyou5){
		this.syukusyou5 = syukusyou5;
	}
	public String getSize1(){
		return size1;
	}
	public void setSize1(String size1){
		this.size1 = size1;
	}
	public String getSize2(){
		return size2;
	}
	public void setSize2(String size2){
		this.size2 = size2;
	}
	public String getSize3(){
		return size3;
	}
	public void setSize3(String size3){
		this.size3 = size3;
	}
	public String getSize4(){
		return size4;
	}
	public void setSize4(String size4){
		this.size4 = size4;
	}
	public String getSize5(){
		return size5;
	}
	public void setSize5(String size5){
		this.size5 = size5;
	}
	public String getHiddenNo1(){
		return hiddenNo1;
	}
	public void setHiddenNo1(String hiddenNo1){
		this.hiddenNo1 = StringCheck.latinToUtf8(hiddenNo1);
	}
	public String getHiddenNo2(){
		return hiddenNo2;
	}
	public void setHiddenNo2(String hiddenNo2){
		this.hiddenNo2 = StringCheck.latinToUtf8(hiddenNo2);
	}
	public String getHiddenNo3(){
		return hiddenNo3;
	}
	public void setHiddenNo3(String hiddenNo3){
		this.hiddenNo3 = StringCheck.latinToUtf8(hiddenNo3);
	}
	public String getHiddenNo4(){
		return hiddenNo4;
	}
	public void setHiddenNo4(String hiddenNo4){
		this.hiddenNo4 = StringCheck.latinToUtf8(hiddenNo4);
	}
	public String getHiddenNo5(){
		return hiddenNo5;
	}
	public void setHiddenNo5(String hiddenNo5){
		this.hiddenNo5 = StringCheck.latinToUtf8(hiddenNo5);
	}
	public String getZumenAll(){
		return zumenAll;
	}
	public void setZumenAll(String zumenAll){
		this.zumenAll = zumenAll;
	}
	public String getPrinter_flag(){
		return printer_flag;
	}
	public void setPrinter_flag(String printer_flag){
		this.printer_flag = printer_flag;
	}
	public String getHani_s(){
		return hani_s;
	}
	public void setHani_s(String hani_s){
		this.hani_s = hani_s;
	}
	public String getHani_e(){
		return hani_e;
	}
	public void setHani_e(String hani_e){
		this.hani_e = hani_e;
	}
	
	public String getErrlog(){
		return errlog;
	}
	public void setErrlog(String errlog){
		this.errlog = errlog;
	}
	public String getErrNumber(){
		return errNumber;
	}
	public void setErrNumber(String errNumber){
		this.errNumber = errNumber;
	}
	public String getHani_t(){
		return hani_t;
	}
	public void setHani_t(String hani_t){
		this.hani_t = hani_t;
	}
	public String getHani_sitei(){
		return hani_sitei;
	}
	public void setHani_sitei(String hani_sitei){
		this.hani_sitei = hani_sitei;
	}
	/**
	 * @return
	 */
	public ArrayList getIraiList() {
		return iraiList;
	}

}
