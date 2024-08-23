package tyk.drasap.common;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

/** @version 2013/06/13 yamagishi */
public class DrasapFileAppender extends RollingFileAppender {
    /**
     * コンストラクタです。
     */
    public DrasapFileAppender() {
        super();
        setFile(getFile());
    }

    /**
     * コンストラクタです。
     *
     * @param layout レイアウトオブジェクト
     * @param filename 出力ログファイル名
     * @throws IOException IO例外が発生した場合
     */
    public DrasapFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
        setFile(getFile());
    }

    /**
     * コンストラクタです。
     *
     * @param layout レイアウトオブジェクト
     * @param filename 出力ログファイル名
     * @param append ログファイルに追記するかどうかのフラグ
     * @throws IOException IO例外が発生した場合
     */
    public DrasapFileAppender(Layout layout, String filename, boolean append)
            throws IOException {
        super(layout, filename, append);
        setFile(getFile());
    }


    /**
     * JVM の起動引数（-Dlog.file.name）からファイル名を取得するメソッドです。<br/>
     * {@link FileAppender#getFile()}をオーバーライドします。
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
