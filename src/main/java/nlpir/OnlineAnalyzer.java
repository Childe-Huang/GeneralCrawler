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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线分析器（推荐）
 * 通过线上接口对文章进行关键词提取，支持多线程
 * 接口：http://202.38.128.96:96/nlpir/index/getAllContent.do
 * post参数：content="广西瓜农在湛采用薄膜覆盖技术种西瓜"&id=keywords&nMaxKeyLimit=5
 * Created by Bin on 2015/3/22.
 */
public class OnlineAnalyzer {
    //日志记录
    public static final Logger log = LoggerFactory.getLogger(OnlineAnalyzer.class);

    public String getKeyWords(String sInput, int nCountKey) {
        String keywords = null;
        String url = "http://202.38.128.96:96/nlpir/index/getAllContent.do";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String content = sInput;
        //content = new String(content.getBytes(), "ISO-8859-1");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("content", content));
        nvps.add(new BasicNameValuePair("id", "keywords"));
        nvps.add(new BasicNameValuePair("nMaxKeyLimit", String.valueOf(nCountKey)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            HttpResponse response2 = httpclient.execute(httpPost);
            HttpEntity entity2 = response2.getEntity();
            keywords = EntityUtils.toString(entity2, "UTF-8");
            keywords = keywords.substring(1, keywords.length()-2);
        } catch (UnsupportedEncodingException e) {
            log.info("Exception: occur UnsupportedEncodingException. ");
            e.printStackTrace();
        } catch (ClientProtocolException e){
            log.info("Exception: occcur ClientProtocolException while visting." + url);
        } catch (IOException e){
            log.info("Exception: occcur IOException while visting." + url);
        } finally {
            httpPost.releaseConnection();
        }
        httpclient.getConnectionManager().shutdown();
        return keywords;
    }
}
