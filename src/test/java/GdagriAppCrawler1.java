import crawler.BaseCrawler;
import model.CrawlerData;
import fetcher.FetchQueueItem;
import model.Links;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.CrawlerDataDao;
import util.RegexRule;

/**
 * Created by Bin on 2015/3/23.
 */
public class GdagriAppCrawler1 extends BaseCrawler {
    RegexRule regexRule;

    public GdagriAppCrawler1() {
        regexRule = new RegexRule();
        regexRule.addRule("http://www.gdagri.gov.cn/zwgk/gzdt/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("http://www.gdagri.gov.cn/zxpt/nyyw/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("http://www.gdagri.gov.cn/zxpt/snnyxxlb/[a-z]+/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("http://www.gdagri.gov.cn/fwpt/scfw/jgxq/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("http://www.gdagri.gov.cn/fwpt/scfw/xfcs/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("http://www.gdagri.gov.cn/zwgk/tzgg/[0-9]+/t[0-9]+_[0-9]+.html");
        regexRule.addRule("-.*jpg.*");
        regexRule.addRule("-.*pdf.*");
        regexRule.addRule("-.*doc.*");
        this.setRegexRules(regexRule);
        String gzdtUrl;     //工作动态（政务要闻）url
        String nyywUrl;     //农业要闻url
        String snnyxxlb;    //省内农业信息联播url
        String jgxq;        //价格详情url
        String xfcs = "http://www.gdagri.gov.cn/fwpt/scfw/xfcs/";//消费常识url（网站上仅有一页，单独处理）
        String tzgg;        //通知公告url
        this.addSeed(xfcs);
        for (int i = 0 ; i < 2; i++) {
            //资讯类数据种子
            gzdtUrl = "http://www.gdagri.gov.cn/zwgk/gzdt/";
            nyywUrl = "http://www.gdagri.gov.cn/zxpt/nyyw/";
            snnyxxlb = "http://www.gdagri.gov.cn/zxpt/snnyxxlb/";
            //服务类数据种子
            jgxq = "http://www.gdagri.gov.cn/fwpt/scfw/jgxq/";
            //公告类数据种子
            tzgg = "http://www.gdagri.gov.cn/zwgk/tzgg/";
            if (i > 0) {
                gzdtUrl = gzdtUrl + "index_" + i + ".html";
                nyywUrl = nyywUrl + "index_" + i + ".html";
                snnyxxlb = snnyxxlb + "index_" + i + ".html";
                jgxq = jgxq + "index_" + i + ".html";
                tzgg = tzgg + "index_" + i + ".html";
            }
            this.addSeed(gzdtUrl);
            this.addSeed(nyywUrl);
            this.addSeed(snnyxxlb);
            this.addSeed(jgxq);
            this.addSeed(tzgg);
        }
        this.setThreads(50);
    }

    /**
     * 个性化定制内容，可以将实现精确抽取的代码写在此函数内（抽取规则参见Jsoup）
     * @param fetchQueueItem
     * @param document
     */
    @Override
    public Links customizeAndGetLinks(FetchQueueItem fetchQueueItem, Document document) {
        Links links = new Links();
        if (fetchQueueItem.getLayer() == 1) {
            links = links.getLinksFromDoc(document, "div[class=glright]", regexRule);
        } else {
            //抽取文章标题
            String title = document.select("body h2").get(0).text();
            //抽取文章附加信息，如发布时间和来源等
            String extra_imfo = document.select("p[class=con_info]").text().replaceAll(Jsoup.parse("&nbsp;").text(), " ");
            extra_imfo.substring(0, 10);
            //抽取文章正文元素
            Elements content = document.select("div[class=TRS_Editor]");
            String content_html = content.html();
            String content_text = content.text();
            //分析器
            //LocalAnalyzer analyzer1 = new LocalAnalyzer();
            //String keywords = analyzer1.getKeyWords(content_text, 5);
            //OnlineAnalyzer analyzer2 = new OnlineAnalyzer();
            //String keywords = analyzer2.getKeyWords(content_text, 5);
            //JiebaAnalyzer analyzer3 = new JiebaAnalyzer();
            //String keywords = analyzer3.getKeyWords(content_text, 5);
            //System.out.println(keywords);
            //备注
            String url = fetchQueueItem.getUrl();
            String memo = "";
            if (url.contains("gzdt")) {
                memo = "工作动态";
            } else if (url.contains("nyyw")) {
                memo = "农业要闻";
            } else if (url.contains("snnyxxlb")) {
                memo = "省内农业信息联播";
            } else if (url.contains("jgxq")) {
                memo = "价格详情";
            } else if (url.contains("xfcs")) {
                memo = "消费常识";
            } else if (url.contains("tzgg")) {
                memo = "通知公告";
            }

            //数据库信息写入
            CrawlerData gdagriCrawlerData = new CrawlerData();
            gdagriCrawlerData.setTitle(title);
            gdagriCrawlerData.setExtra_imfo(extra_imfo);
            gdagriCrawlerData.setContent_html(content_html);
            gdagriCrawlerData.setContent_text(content_text);
            gdagriCrawlerData.setMemo(memo);
            gdagriCrawlerData.setSource_url(url);
            //gdagriCrawlerData.setKeywords(keywords);
            CrawlerDataDao.insertData(gdagriCrawlerData);
        }
        return links;
    }
}
