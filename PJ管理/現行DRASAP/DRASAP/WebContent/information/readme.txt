$Id: readme.txt,v 1.3 2005/03/18 04:41:29 fumi Exp $

//////////////////////////////////////
// �{�ԋ@�ƃe�X�g�@�Œ��ӂ��ׂ��Ⴂ
//////////////////////////////////////
1) WEB-INF/web.xml
	�f�[�^�\�[�X�̎擾���@�ɈႢ�����邽�߁A
	resource-ref�^�O�́AWebLogic�ł̓R�����g�A�E�g����K�v������B
2) WEB-INF/classes/drasap.properties
	web.container�E�E�Etomcat,weblogic�BDatasource�̎擾���@�Ȃǂ�؂�ւ�
	oce.mabiki.path�E�E�EOCE�Ԉ������W���[���̐�΃p�X
	oce.banner.path�E�E�EOCE�o�i�[���W���[���̐�΃p�X
3) WEB-INF/classes/EnDeEnv.properties
	EnDe_File_Path�E�E�E��΃p�X
	SYSpass_File_Path�E�E�E��΃p�X
4) WEB-INF/classes/log4j.properties
	DRASAP�̃��O�o�͂Ɋւ���ݒ�t�@�C��
	
//////////////////////////////////////
// �e�X�g�@���ɂ���
//////////////////////////////////////
�}�V����		hoko2493
IP�A�h���X	172.16.210.193
OS			Solaris
���[�U�[		drasap/drasap

Oracle		8.1.7
�X�L�[�}	drasap/drasap
SID			drasap1

Tomcat		4.1.24
Tomcat��	/oracle/drasap/tomcat4.1.24

Web�A�v���P�[�V����	DRASAP
JDBC Datasource	server.xml�Œ�`�BJNDI���Fjdbc/DRASAP
web.xml�ɂ�resource-ref�^�O�Œ�`�B

OCE�̃��W���[�����g�p���邽�߂ɁD�D�D
setenv PATH ${PATH}:/oracle/drasap/oce_modules/bin
PDFLib���g�p���邽�߂ɁD�D�D
setenv PATH ${PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java

//////////////////////////////////////
// �{�ԋ@���ɂ���
//////////////////////////////////////
�}�V����		hoko2491
IP�A�h���X	172.16.210.191
OS			Solaris
���[�U�[		cf20/cf20

Oracle		8.1.7
�X�L�[�}	drasap/drasap
SID			drasap

WebLogic	6.1 SP3
			/oracle/bea/wlserver6.1

Web�A�v���P�[�V����	DRASAP
�ڑ��v�[��	DRASAP Oracle Connection Pool
			jdbc:oracle:oci8:@drasap
JDBC Datasource	JNDI���Fjdbc/drasap_oracle

OCE�̃��W���[�����g�p���邽�߂ɁD�D�D
setenv PATH ${PATH}:/oracle/drasap/oce_modules/bin
PDFLib���g�p���邽�߂ɁD�D�D
setenv PATH ${PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:/oracle/drasap/PDFlib-5.0.3-SunOS-sparc/bind/java
setenv PDFLIBLICENSEFILE /oracle/drasap/PDFlib_License/licensefile

//////////////////////////////////////
// �����d�C�̊J�����ɂ���
//////////////////////////////////////
Eclipse��Tomcat Plug-in �Ńe�X�g
http://localhost:8080/DRASAP/
���[�U�[ 123456 �p�X���[�h fumi
