#! /bin/sh
#
#書式 jsp_cp_fumi.sh
#
#処理 JSPプリコンパイルを行なう。
#
#戻り値 0 正常終了
#       1 異常終了

#### JSPプリコンパイルするための環境変数のセット ##############
#### ここは変更しないこと

JAVA_HOME=/oracle/bea/jdk131
WL_HOME=/oracle/bea/wlserver6.1

JAVACMD=java
JAVA_OPTIONS="-ms1024m -mx2048m"

  if [ -n "$LD_LIBRARY_PATH" ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WL_HOME/lib/solaris:$WL_HOME/lib/solaris/oci817_8
  else
    LD_LIBRARY_PATH=$WL_HOME/lib/solaris:$WL_HOME/lib/solaris/oci817_8
  fi
  PATH=$WL_HOME/lib/solaris:$PATH
  export LD_LIBRARY_PATH PATH
#  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH" #コメントアウト by MUR平田 2005.Jan.27
  JAVA_OPTIONS="-hotspot $JAVA_OPTIONS"

CLASSPATH=/oracle/bea/jdk131:$WL_HOME:$WL_HOME/lib/weblogic.jar

PATH=$WL_HOME/bin:$JAVA_HOME/jre/bin:$JAVA_HOME/bin:$PATH

#### DRASAP_HOMEのセット #########################################

DRASAP_HOME=/oracle/bea/wlserver6.1/config/mydomain/applications/DRASAP

#### CLASS FILE 作成先フォルダの指定 ############################
#### （_tmp_war_DRASAP/以下は自動的に作成される）

CLASSDIR=${DRASAP_HOME}/WEB-INF/_tmp_war_DRASAP

#### 対象をメッセージ表示する #############################
#
echo "---------- DRASAP をJSPプリコンパイルを開始します。----------"

date > timestamp.temp             # 開始時間をBEGIN_TIMEにセット
read BEGIN_TIME < timestamp.temp

ls ${DRASAP_HOME}/*/*.jsp |     # 指定したディレクトリのjspファイルでループさせ、
                                  # それをパイプで、whileループへつなぐ。
while read JSPFILE                # 1行ずつ読み込み、JSPFILEへセット
do
	echo $JSPFILE
	# 1ファイルずつ JSPプリコンパイルする
	java -cp $CLASSPATH weblogic.jspc -d $CLASSDIR $JSPFILE
	# こちらは -verboseオプションを指定
	# java -cp $CLASSPATH weblogic.jspc -verbose -d $CLASSDIR $JSPFILE
done                              # whileループの終了

date > timestamp.temp             # 終了時間をEND_TIMEにセット
read END_TIME < timestamp.temp
rm -f timestamp.temp              # 時間取得用に使用したファイルの後片付け

echo "---------- DRASAP のJSPプリコンパイル終了。----------"
echo "Begin  $BEGIN_TIME"
echo "End    $END_TIME"

exit 0
