$Id: readme.txt,v 1.3 2005/03/18 04:41:29 fumi Exp $

//////////////////////////////////////
// 本番機とテスト機で注意すべき違い
//////////////////////////////////////
1) WEB-INF/web.xml
	データソースの取得方法に違いがあるため、
	resource-refタグは、WebLogicではコメントアウトする必要がある。
2) WEB-INF/classes/drasap.properties
	web.container・・・tomcat,weblogic。Datasourceの取得方法などを切り替え
	oce.mabiki.path・・・OCE間引きモジュールの絶対パス
	oce.banner.path・・・OCEバナーモジュールの絶対パス
3) WEB-INF/classes/EnDeEnv.properties
	EnDe_File_Path・・・絶対パス
	SYSpass_File_Path・・・絶対パス
4) WEB-INF/classes/log4j.properties
	DRASAPのログ出力に関する設定ファイル
	
//////////////////////////////////////
// テスト機環境について
//////////////////////////////////////
マシン名		hoko2493
IPアドレス	172.16.210.193
OS			Solaris
ユーザー		drasap/drasap

Oracle		8.1.7
スキーマ	drasap/drasap
SID			drasap1

Tomcat		4.1.24
Tomcat環境	/oracle/drasap/tomcat4.1.24

Webアプリケーション	DRASAP
JDBC Datasource	server.xmlで定義。JNDI名：jdbc/DRASAP
web.xmlにもresource-refタグで定義。

OCEのモジュールを使用するために．．．
setenv PATH ${PATH}:/oracle/drasap/oce_modules/bin
PDFLibを使用するために．．．
setenv PATH ${PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java

//////////////////////////////////////
// 本番機環境について
//////////////////////////////////////
マシン名		hoko2491
IPアドレス	172.16.210.191
OS			Solaris
ユーザー		cf20/cf20

Oracle		8.1.7
スキーマ	drasap/drasap
SID			drasap

WebLogic	6.1 SP3
			/oracle/bea/wlserver6.1

Webアプリケーション	DRASAP
接続プール	DRASAP Oracle Connection Pool
			jdbc:oracle:oci8:@drasap
JDBC Datasource	JNDI名：jdbc/drasap_oracle

OCEのモジュールを使用するために．．．
setenv PATH ${PATH}:/oracle/drasap/oce_modules/bin
PDFLibを使用するために．．．
setenv PATH ${PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv PDFLIBLICENSEFILE /oracle/drasap/PDFlib_License/licensefile

//////////////////////////////////////
// 村瀬電気の開発環境について
//////////////////////////////////////
EclipseのTomcat Plug-in でテスト
http://localhost:8080/DRASAP/
ユーザー 123456 パスワード fumi
