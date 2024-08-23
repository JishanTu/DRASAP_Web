package tyk.drasap.common;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

/** @version 2013/06/13 yamagishi */
public class DrasapFileAppender extends RollingFileAppender {
    /**
     * �R���X�g���N�^�ł��B
     */
    public DrasapFileAppender() {
        super();
        setFile(getFile());
    }

    /**
     * �R���X�g���N�^�ł��B
     *
     * @param layout ���C�A�E�g�I�u�W�F�N�g
     * @param filename �o�̓��O�t�@�C����
     * @throws IOException IO��O�����������ꍇ
     */
    public DrasapFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
        setFile(getFile());
    }

    /**
     * �R���X�g���N�^�ł��B
     *
     * @param layout ���C�A�E�g�I�u�W�F�N�g
     * @param filename �o�̓��O�t�@�C����
     * @param append ���O�t�@�C���ɒǋL���邩�ǂ����̃t���O
     * @throws IOException IO��O�����������ꍇ
     */
    public DrasapFileAppender(Layout layout, String filename, boolean append)
            throws IOException {
        super(layout, filename, append);
        setFile(getFile());
    }


    /**
     * JVM �̋N�������i-Dlog.file.name�j����t�@�C�������擾���郁�\�b�h�ł��B<br/>
     * {@link FileAppender#getFile()}���I�[�o�[���C�h���܂��B
     *
     * @see org.apache.log4j.FileAppender#getFile()
     */
    public String getFile() {
// 2013.06.13 yamagishi modified. start
//    	String filename = DrasapPropertiesFactory.getDrasapProperties(this).getProperty("oce.BEA_BASE");
    	String filename = DrasapPropertiesFactory.getDrasapProperties(this).getProperty(DrasapPropertiesFactory.OCE_AP_SERVER_BASE);
// 2013.06.13 yamagishi modified. end
 //       if (filename == null) {
            filename = super.getFile();
 //       }
        return filename == null ? "" :  filename;
    }

}
