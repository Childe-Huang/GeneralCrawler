package model;

import config.JedisConfig;
import fetcher.TempQueue;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.RegexRule;

import java.util.ArrayList;

/**
 * 链接抽取类
 * Created by Bin on 2015/3/24.
 */
public class Links extends ArrayList<String> {
    private static JedisPool jedisPool = new JedisPool(JedisConfig.address, JedisConfig.port);
    private static Jedis jedis = jedisPool.getResource();
    private TempQueue tempQueue = TempQueue.getFetchQueue();

    /**
     * 通过整页doc和正则规则抽取链接
     * @param doc
     * @param regexRule
     */
    public Links getLinksFromDoc(Document doc, RegexRule regexRule) {
        Elements as = doc.select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            if (regexRule.satisfy(href)) {
                this.add(href);
            }
        }
        return this;
    }

    /**
     * 通过整页doc、CSS选择器、正则规则抽取链接
     * @param doc
     * @param cssSelector
     * @param regexRule
     */
    public Links getLinksFromDoc(Document doc, String cssSelector, RegexRule regexRule) {
        Elements as = doc.select(cssSelector).select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            if (regexRule.satisfy(href)) {
                this.add(href);
            }
        }
        return this;
    }

    /**
     * 通过整页doc抽取链接
     * @param doc
     * @return
     */
    public Links getLinksFromDoc(Document doc) {
        Elements as = doc.select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            this.add(href);
        }
        return this;
    }

    /**
     * 通过整页doc和CSS选择器抽取链接
     * @param doc
     * @param cssSelector
     * @return
     */
    public Links getLinksFromDoc(Document doc, String cssSelector) {
        Elements as = doc.select(cssSelector).select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            this.add(href);
        }
        return this;
    }
}
