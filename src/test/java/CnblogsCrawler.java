import crawler.BaseCrawler;
import fetcher.FetchQueueItem;
import model.CrawlerData;
import model.Links;
import nlpir.LocalAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import util.CrawlerDataDao;
import util.JDBCHelper;
import util.RegexRule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Bin on 2015/3/27.
 */
public class CnblogsCrawler extends BaseCrawler {
    RegexRule regexRule;
    //JdbcTemplate jdbcTemplate = null;

    public CnblogsCrawler() {
        regexRule = new RegexRule();
        regexRule.addRule("http://news.cnblogs.com/n/[0-9]+/");
        regexRule.addRule("-.*jpg.*");
        regexRule.addRule("-.*pdf.*");
        regexRule.addRule("-.*doc.*");
        this.setRegexRules(regexRule);
        String seedUrl;
        for (int i = 1; i < 301; i++) {
            seedUrl = "http://news.cnblogs.com/";
            if (i != 1) {
                seedUrl = seedUrl + "n/page/" + i;
            }
            this.addSeed(seedUrl);
        }
        this.setThreads(10);
        this.setEncodingProcess(0);
        /*jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/crawler?useUnicode=true&characterEncoding=utf8",
                "root", "sa", 10, 40);
        if (jdbcTemplate == null) {
            log.info("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
            return;
        }*/
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
            links = links.getLinksFromDoc(document, "div[class=news_block]", regexRule);
        } else {
            //抽取文章标题
            String title = document.title();
            //System.out.println(title);
            //抽取文章附加信息，如发布时间和来源等
            String extra_imfo = document.select("div#news_info").text().replaceAll(Jsoup.parse("&nbsp;").text(), " ");
            extra_imfo = extra_imfo.substring(17, 33);
            //System.out.println(extra_imfo);
            //抽取文章正文元素
            Elements content = document.select("div#news_body");
            String content_html = content.html();
            String content_text = content.text();
            //分析器
            /*LocalAnalyzer analyzer1 = new LocalAnalyzer();
            String keywords = analyzer1.getKeyWords(content_text, 5);
            OnlineAnalyzer analyzer2 = new OnlineAnalyzer();
            String keywords = analyzer2.getKeyWords(content_text, 5);
            JiebaAnalyzer analyzer3 = new JiebaAnalyzer();
            String keywords = analyzer3.getKeyWords(content_text, 5);
            System.out.println(keywords);*/

            //数据库信息写入
            CrawlerData gdagriCrawlerData = new CrawlerData();
            gdagriCrawlerData.setTitle(title);
            gdagriCrawlerData.setExtra_imfo(extra_imfo);
            gdagriCrawlerData.setContent_html(content_html);
            gdagriCrawlerData.setContent_text(content_text);
            gdagriCrawlerData.setSource_url(fetchQueueItem.getUrl());
            gdagriCrawlerData.setMemo("博客园新闻");
            //gdagriCrawlerData.setKeywords(keywords);
            CrawlerDataDao.insertData(gdagriCrawlerData);

            /*java.util.Date date = new java.util.Date();//获得系统时间.
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);//将时间格式转换成符合Timestamp要求的格式.
            Timestamp newdate = Timestamp.valueOf(nowTime);//把时间转换
            if (jdbcTemplate != null) {
                int updates=jdbcTemplate.update(
                        "insert into t_crawler_cnblogs (title, extra_imfo, content_html, content_text, comment, memo, oper_time, source_url, keywords) value(?,?,?,?,?,?,?,?,?)",
                        title, extra_imfo, content_html, content_text, null, "博客园新闻", newdate, fetchQueueItem.getUrl(), null);
                if(updates==1){
                    log.info("mysql插入成功");
                }
            }*/
        }
        return links;
    }

    public static void main(String[] args) throws Exception {
        CnblogsCrawler cnblogsCrawler = new CnblogsCrawler();
        cnblogsCrawler.start(2);
    }
}
