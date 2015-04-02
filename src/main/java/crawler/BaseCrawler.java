package crawler;

import config.JedisConfig;
import fetcher.*;
import model.Links;
import net.Proxys;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.RegexRule;

import java.util.ArrayList;

/**
 * Created by Bin on 2015/3/2.
 */
public class BaseCrawler implements Customization, CustomizationFactory {
    //日志记录
    public static final Logger log = LoggerFactory.getLogger(BaseCrawler.class);
    //爬虫入口种子
    private ArrayList<String> seeds = new ArrayList<String>();
    //爬虫目录种子（爬取时无条件入队）
    private ArrayList<String> catalogSeeds = new ArrayList<String>();
    //爬虫正则规则（含正正则和负正则）
    private RegexRule regexRules = new RegexRule();
    //爬虫开启线程数
    private int threads;
    //网页抓取器
    private Fetcher fetcher;
    //定时任务标志（true则保持非关系数据库缓存）
    private boolean isTimingTask = false;
    //redis主机地址（默认127.0.0.1）
    private String jedisAddress = "127.0.0.1";
    //redis端口号（默认6379）
    private String port = "6379";
    //爬虫状态
    private boolean status;
    //失败重试次数（默认为3次）
    private int retry = 3;
    //CustomizationFactory 定制器工厂
    CustomizationFactory customizationFactory = this;
    //IP代理
    private Proxys proxys = null;
    //cookies
    private String cookies = null;
    //编码处理方式
    private int encodingProcess;

    //状态：执行中
    private static final boolean RUNNING = true;
    //状态：停止
    private static final boolean STOPPED = false;

    public void setSeeds(ArrayList<String> seeds) {
        this.seeds = seeds;
    }

    public void setCatalogSeeds(ArrayList<String> catalogSeeds) {
        this.catalogSeeds = catalogSeeds;
    }

    public void setRegexRules(RegexRule regexRules) {
        this.regexRules = regexRules;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setIsTimingTask(boolean timingTask) {
        this.isTimingTask = timingTask;
    }

    public void setJedisAddress(String jedisAddress) {
        this.jedisAddress = jedisAddress;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public void addSeed(String seed) {
        seeds.add(seed);
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

    @Override
    public Customization createCustomization(){
        return this;
    }

    @Override
    public Links customizeAndGetLinks(FetchQueueItem fetchQueueItem, Document document) {
        return null;
    }

    public void start(int depth) {
        JedisPool jedisPool = new JedisPool(JedisConfig.address, JedisConfig.port);
        Jedis jedis = jedisPool.getResource();
        //非定时任务清空redis
        if (!isTimingTask) {
            jedis.flushAll();
            //Java的单例对象不会被自动回收或者说不会被及时回收，需对其进行手动回收，否则也许你会在本次操作中看到上次爬取的数据。
            FetchQueue.releaseResource();
            TempQueue.releaseResource();
        }
        //爬虫启动需要至少一个种子url
        if (seeds.isEmpty() && catalogSeeds.isEmpty()) {
            log.info("error: you must add at least one seed.");
            return;
        }
        //创建任务队列，注入目录种子和普通种子
        FetchQueue fetchQueue = FetchQueue.getFetchQueue();
        if (!catalogSeeds.isEmpty()) {
            for (String catalogSeed : catalogSeeds) {
                FetchQueueItem fetchQueueItem = new FetchQueueItem(catalogSeed, 1);
                fetchQueue.add(fetchQueueItem);
                jedis.set(fetchQueueItem.getUrl(), JedisConfig.CATALOG_SEED);
            }
        }
        if (!seeds.isEmpty()) {
            for (String seed : seeds) {
                FetchQueueItem fetchQueueItem = new FetchQueueItem(seed, 1);
                if (!jedis.exists(fetchQueueItem.getUrl())) {
                    jedis.set(fetchQueueItem.getUrl(), JedisConfig.COMMON_SEED);
                    fetchQueue.add(fetchQueueItem);
                }
            }
        }
        jedisPool.returnResource(jedis);

        status = RUNNING;
        for (int i = 1; i <= depth; i++) {
            if (status == STOPPED) {
                break;
            }
            log.info("BaseCrawler starts grabbing in depth " + i);
            Fetcher fetcher = new Fetcher();
            fetcher.setRetry(retry);
            fetcher.setThreads(threads);
            fetcher.setRegexRule(regexRules);
            fetcher.setProxys(proxys);
            fetcher.setCookies(cookies);
            fetcher.setCustomizationFactory(customizationFactory);
            fetcher.setEncodingProcess(encodingProcess);
            fetcher.grab(i+1);
            log.info("BaseCrawler finished grabbing in depth "+ i);
        }
        log.info("Grabbing Task Is Completed.");
    }
}
