package util;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by Bin on 2015/3/12.
 * 网页字符集嗅探器，用于猜测网页编码所使用的字符集
 */
public class CharacterTool {
    private static final Logger log = LoggerFactory.getLogger(CharacterTool.class);

    public static String guessEncoding(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }

    public static String reEncoding(String originString, int encodingProcess) {
        //html解决中文乱码
        try {
            byte[] htmlBytes = originString.getBytes();
            String charset = CharacterTool.guessEncoding(htmlBytes);
            //encodingProcess为0，不操作；为1，先转码为ISO-8859-1
            if (encodingProcess == 1) {
                //目前发现博客园cnblog，微博手机版下面这句话不能加>_<
               htmlBytes = originString.getBytes("ISO-8859-1");
            }

            //System.out.print(charset);
            originString = new String(htmlBytes, charset);
        } catch (UnsupportedEncodingException ex) {
            log.info("exception:occur UnsupportedEncodingException while solving the problem of Chinese garbled");
        }
        return originString;
    }
}
