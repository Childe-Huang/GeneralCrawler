package nlpir;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * 本地分析器
 * 通过调用dll文件，对文章进行关键词提取，仅支持单线程
 * Created by Bin on 2015/3/20.
 */
public class LocalAnalyzer {
    //日志记录
    public static final Logger log = LoggerFactory.getLogger(LocalAnalyzer.class);
    // 定义接口CLibrary，继承自com.sun.jna.Library
    public interface CLibrary extends Library {
        // 定义并初始化接口的静态变量
        /*CLibrary Instance = (CLibrary) Native.loadLibrary(
                "G:\\毕业设计\\20141230101836_ICTCLAS2015\\lib\\win64\\NLPIR", CLibrary.class);*/
        CLibrary Instance = (CLibrary) Native.loadLibrary(
                ".\\src\\main\\resources\\DLL\\win64\\NLPIR", CLibrary.class);
        public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
        public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
        public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
        public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
        public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
        public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
        public String NLPIR_GetLastErrorMsg();
        public void NLPIR_Exit();
    }

    public static String transString(String aidString, String ori_encoding, String new_encoding) {
        try {
            return new String(aidString.getBytes(ori_encoding), new_encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getKeyWords(String sInput, int nCountKey) {
        String argu = ".\\src\\main\\resources";
        // String system_charset = "GBK";//GBK----0
        String system_charset = "UTF-8";
        int charset_type = 1;

        int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
        String nativeBytes = null;
        String keywords = null;

        if (0 == init_flag) {
            nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
            System.err.println("初始化失败！fail reason is "+nativeBytes);
            //log.info("Error: Initialization failed! Fail reason is" + nativeBytes);
            return null;
        }

        try {
            /*nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);

            System.out.println("分词结果为： ");
            System.out.println(nativeBytes);

            CLibrary.Instance.NLPIR_AddUserWord("要求美方加强对输 n");
            CLibrary.Instance.NLPIR_AddUserWord("华玉米的产地来源 n");
            nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
            System.out.println("增加用户词典后分词结果为： ");
            System.out.println(nativeBytes);

            CLibrary.Instance.NLPIR_DelUsrWord("要求美方加强对输");
            nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
            System.out.println("删除用户词典后分词结果为： ");
            System.out.println(nativeBytes);
*/
            //自定义的keywords个数，最大为50，但不保证能提取出足够的关键词
            keywords = CLibrary.Instance.NLPIR_GetKeyWords(sInput, nCountKey, false);
            CLibrary.Instance.NLPIR_Exit();
        } catch (Exception ex) {
            log.info("Exception: Occur exception while extracting keywords.");
            ex.printStackTrace();
        }
        return keywords;
    }
}
