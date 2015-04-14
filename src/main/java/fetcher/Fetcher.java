package fetcher;

import config.JedisConfig;
import model.Links;
import net.Proxys;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.CharacterTool;
import util.RegexRule;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Bin on 2015/3/2.
 * Fetcher 抓取器类
 * FetcherThread 抓取器线程类
 */
public class Fetcher{
    //日志记录
    public static final Logger log = LoggerFactory.getLogger(Fetcher.class);
    //抓取器启动线程数
    private int threads;
    //抓取失败重试次数
    private int retry;
    //当前抓取所在的深度
    private int depth;
    //任务队列
    private FetchQueue fetchQueue;
    //正则规则
    private RegexRule regexRule;
    //IP代理
    private Proxys proxys;
    //cookies
    private String cookies;
    //编码处理方式
    private int encodingProcess;
    //CustomizationFactory 定制器工厂
    CustomizationFactory customizationFactory = null;
    //CustomizationFactory 定制器
    Customization customization = null;

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setRegexRule(RegexRule regexRule) {
        this.regexRule = regexRule;
    }

    public void setProxys(Proxys proxys) {
        this.proxys = proxys;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public void setEncodingProcess(int encodingProcess) {
        this.encodingProcess = encodingProcess;
    }

    public void setCustomizationFactory(CustomizationFactory customizationFactory) {
        this.customizationFactory = customizationFactory;
    }

    public void grab(int depth) {
        //当前所在深度
        this.depth = depth;

        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        FetcherThread[] fetcherThreads = new FetcherThread[threads];
        for (int i = 0; i < threads; i++) {
            fetcherThreads[i] = new FetcherThread();
            threadPool.execute(fetcherThreads[i]);
        }
        //判断Java线程池任务是否执行完毕
        threadPool.shutdown();
        while (true) {
            if (threadPool.isTerminated()) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.info("Exception: occur InterruptedException while executing Thread.sleep() in function Fetcher.grab()");
            }
        }
        //将临时队列tempQueue的fetchItem加入任务队列fetchQueue
        fetchQueue = FetchQueue.getFetchQueue();
        int size = TempQueue.getFetchQueue().size();
        for (int i = 0; i < size; i++) {
            fetchQueue.add(TempQueue.getFetchQueue().poll());
        }
    }


    private class FetcherThread extends Thread {

        //FetcherThread执行状态标志
        private boolean running;
        //状态：执行中
        private static final boolean RUNNING = true;
        //状态：停止
        private static final boolean STOPPED = false;
        //FetchQueue队列元素fetchQueueItem
        private FetchQueueItem fetchQueueItem = null;
        //任务队列
        private FetchQueue fetchQueue = FetchQueue.getFetchQueue();
        //临时队列（按所在深度构建）
        private TempQueue tempQueue = TempQueue.getFetchQueue();

        @Override
        public void run() {
            //临时队列
            tempQueue = TempQueue.getFetchQueue();
            //创建redis操作实例
            JedisPool jedisPool = new JedisPool(JedisConfig.address, JedisConfig.port);
            Jedis jedis = jedisPool.getResource();
            //抽取的链接
            Links links = null;

            running = true;
            while (running) {
                fetchQueueItem = fetchQueue.poll();
                if (fetchQueueItem == null) {
                    /**
                     * 另外的解决方案：也许存在优化空间，限制待执行任务队列长度，防止内存溢出
                     * if(添种线程池任务执行中) Thread.sleep(time);
                     * else return;
                     */
                    try {
                        this.sleep(2000);
                    } catch (InterruptedException e) {
                        log.info("Exception:occur InterruptedException while executing Thread.sleep() in function FetcherThread.run()");
                    }
                    fetchQueueItem = fetchQueue.poll();
                    if (fetchQueueItem == null)
                        return;
                }
                if (jedis.get(fetchQueueItem.getUrl()) == JedisConfig.BEEN_GRABBED) {
                    continue;
                }
                //通过url访问得到响应，分析响应取得doc和link，link线程入队
                HttpClient httpClient = new DefaultHttpClient();
                String url = null;
                try {
                    HttpParams params = httpClient.getParams();
                    params.setParameter(CoreProtocolPNames.USER_AGENT,
                            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
                    HttpConnectionParams.setConnectionTimeout(params, 15000);
                    HttpConnectionParams.setSoTimeout(params, 15000);
                    url = fetchQueueItem.getUrl();
                    jedis.set(url, JedisConfig.BEEN_GRABBED);
                    HttpGet httpget = new HttpGet(url);
                    if (proxys != null) {
                        httpget.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxys.getRandomProxy());
                    }
                    if (cookies != null) {
                        httpget.setHeader(new BasicHeader("Cookie", cookies));
                    }
                    HttpResponse response = httpClient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    String html = EntityUtils.toString(entity);
                    //html解决中文乱码
                    html = CharacterTool.reEncoding(html, encodingProcess);
                    Document doc = Jsoup.parse(html, url);
                    //个性化定制爬虫核心
                    customization = customizationFactory.createCustomization();
                    if (customization != null) {
                        links = customization.customizeAndGetLinks(fetchQueueItem, doc);
                    }
                    for (String href: links) {
                        //去重，要验证redis数据库中是否已经存在，无则插redis并入队，有则抛弃
                        if (!jedis.exists(href)) {
                            jedis.set(href, JedisConfig.UN_GRABBED);
                            tempQueue.add(new FetchQueueItem(href, depth));
                        }
                    }
                    httpget.releaseConnection();
                } catch (ClientProtocolException e){
                    log.info("exception: occur ClientProtocolException while visiting " + url);
                    e.printStackTrace();
                } catch (IOException e){
                    log.info("exception: occur IOException while visiting " + url);
                    e.printStackTrace();
                } catch (Exception e){
                    log.info("exception: occur UnknownException while visiting " + url +" Please check Fetcher.java.");
                    e.printStackTrace();
                }finally{
                    httpClient.getConnectionManager().shutdown();
                }
            }
        }
    }
}
