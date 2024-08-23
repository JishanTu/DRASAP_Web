#! /bin/sh
#
#���� jsp_cp_fumi.sh
#
#���� JSP�v���R���p�C�����s�Ȃ��B
#
#�߂�l 0 ����I��
#       1 �ُ�I��

#### JSP�v���R���p�C�����邽�߂̊��ϐ��̃Z�b�g ##############
#### �����͕ύX���Ȃ�����

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
#  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH" #�R�����g�A�E�g by MUR���c 2005.Jan.27
  JAVA_OPTIONS="-hotspot $JAVA_OPTIONS"

CLASSPATH=/oracle/bea/jdk131:$WL_HOME:$WL_HOME/lib/weblogic.jar

PATH=$WL_HOME/bin:$JAVA_HOME/jre/bin:$JAVA_HOME/bin:$PATH

#### DRASAP_HOME�̃Z�b�g #########################################

DRASAP_HOME=/oracle/bea/wlserver6.1/config/mydomain/applications/DRASAP

#### CLASS FILE �쐬��t�H���_�̎w�� ############################
#### �i_tmp_war_DRASAP/�ȉ��͎����I�ɍ쐬�����j

CLASSDIR=${DRASAP_HOME}/WEB-INF/_tmp_war_DRASAP

#### �Ώۂ����b�Z�[�W�\������ #############################
#
echo "---------- DRASAP ��JSP�v���R���p�C�����J�n���܂��B----------"

date > timestamp.temp             # �J�n���Ԃ�BEGIN_TIME�ɃZ�b�g
read BEGIN_TIME < timestamp.temp

ls ${DRASAP_HOME}/*/*.jsp |     # �w�肵���f�B���N�g����jsp�t�@�C���Ń��[�v�����A
                                  # ������p�C�v�ŁAwhile���[�v�ւȂ��B
while read JSPFILE                # 1�s���ǂݍ��݁AJSPFILE�փZ�b�g
do
	echo $JSPFILE
	# 1�t�@�C������ JSP�v���R���p�C������
	java -cp $CLASSPATH weblogic.jspc -d $CLASSDIR $JSPFILE
	# ������� -verbose�I�v�V�������w��
	# java -cp $CLASSPATH weblogic.jspc -verbose -d $CLASSDIR $JSPFILE
done                              # while���[�v�̏I��

date > timestamp.temp             # �I�����Ԃ�END_TIME�ɃZ�b�g
read END_TIME < timestamp.temp
rm -f timestamp.temp              # ���Ԏ擾�p�Ɏg�p�����t�@�C���̌�Еt��

echo "---------- DRASAP ��JSP�v���R���p�C���I���B----------"
echo "Begin  $BEGIN_TIME"
echo "End    $END_TIME"

exit 0
