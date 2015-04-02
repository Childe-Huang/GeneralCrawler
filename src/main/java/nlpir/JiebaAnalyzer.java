package nlpir;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线分析器（推荐）
 * 通过线上接口对文章进行关键词提取，支持多线程
 * 接口：http://jiebademo.ap01.aws.af.cm/extract
 * post参数：text="广西瓜农在湛采用薄膜覆盖技术种西瓜"&topk=5
 * Created by Bin on 2015/3/22.
 */
public class JiebaAnalyzer {
    //日志记录
    public static final Logger log = LoggerFactory.getLogger(JiebaAnalyzer.class);

    public String getKeyWords(String sInput, int nCountKey) {
        String html = null;
        String keywords = null;
        String url = "http://jiebademo.ap01.aws.af.cm/extract";
        try {
            Connection.Response res = Jsoup.connect(url)
                    .data("text", sInput, "topk", String.valueOf(nCountKey))
                    .method(Connection.Method.POST)
                    .timeout(10000)
                    .execute();
            Document doc = res.parse();
            keywords = doc.select("textarea[name=tags]").text();
        } catch (IOException e) {
            log.info("Exception: occur IOException while visiting" + url);
            e.printStackTrace();
        }
        return keywords;
    }
}
