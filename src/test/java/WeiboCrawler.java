import crawler.BaseCrawler;
import fetcher.FetchQueueItem;
import model.CrawlerData;
import model.Links;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.CrawlerDataDao;
import util.RegexRule;

/**
 * 微博手机版网页，使用cookie模拟登录
 * Created by Bin on 2015/3/30.
 */
public class WeiboCrawler extends BaseCrawler{
    RegexRule regexRule;

    public WeiboCrawler() {
        regexRule = new RegexRule();
        regexRule.addRule("http://news.cnblogs.com/n/[0-9]+/");
        regexRule.addRule("-.*jpg.*");
        regexRule.addRule("-.*pdf.*");
        regexRule.addRule("-.*doc.*");
        this.setRegexRules(regexRule);
        String seedUrl = "http://weibo.cn";//用户名：皂快薯昏
        this.setCookies("_T_WM=4f8d221c822efc3a4854826c07f3758d; SUB=_2A254HFtVDeTxGeNL6lsR9CnFzzmIHXVb_2UdrDV6PUJbrdANLXXgkW09XopQBxz-KVDvYM6i-gwxAoF7Ow..; gsid_CTandWM=4u53e9d41clZLtAn2Pk5rn9KTff");
        this.addSeed(seedUrl);
        this.setThreads(1);
    }

    /**
     * 个性化定制内容，可以将实现精确抽取的代码写在此函数内（抽取规则参见Jsoup）
     * @param fetchQueueItem
     * @param document
     */
    @Override
    public Links customizeAndGetLinks(FetchQueueItem fetchQueueItem, Document document) {
        Links links = new Links();
        links = links.getLinksFromDoc(document, regexRule);
        String html = document.html();
        System.out.println(html);
        return links;
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler weiboCrawler = new WeiboCrawler();
        weiboCrawler.start(1);
    }
}
