package GraduationProject;

import crawler.BaseCrawler;
import fetcher.FetchQueueItem;
import model.CrawlerData;
import model.Links;
import nlpir.LocalAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import util.CrawlerDataDao;
import util.JDBCHelper;
import util.RegexRule;

/**
 * 广东农业信息网-农业要闻 http://www.gdagri.gov.cn/zxpt/nyyw/
 * Created by Bin on 2015/4/3.
 */
public class AgriculturalNewsCrawler extends BaseCrawler {

    public static final Logger log = LoggerFactory.getLogger(AgriculturalNewsCrawler.class);
    RegexRule regexRule;
    JdbcTemplate jdbcTemplate = null;

    public AgriculturalNewsCrawler() {
        regexRule = new RegexRule();
        regexRule.addRule("http://www.gdagri.gov.cn/.+.html");
        regexRule.addRule("-.*jpg.*");
        regexRule.addRule("-.*pdf.*");
        regexRule.addRule("-.*doc.*");
        this.setRegexRules(regexRule);

        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:mysql://localhost/crawler?useUnicode=true&characterEncoding=utf8",
                "root", "sa", 5, 30);
        if (jdbcTemplate == null) {
            log.info("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
            return;
        }

        String seedUrl;
        for (int i = 0; i < 3; i++) {
            seedUrl = "http://www.gdagri.gov.cn/zxpt/nyyw/";
            if (i != 0) {
                seedUrl = seedUrl + "index_" + i + ".html";
            }
            this.addSeed(seedUrl);
        }
        this.setEncodingProcess(1);
        this.setThreads(10);
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
            System.out.println(title);
            //抽取文章附加信息，如发布时间和来源等
            String extra_imfo = document.select("p[class=con_info]").text().replaceAll(Jsoup.parse("&nbsp;").text(), " ");
            System.out.println(extra_imfo);
            String releaseTime = extra_imfo.substring(0, 19);
            String releaseSource = extra_imfo.substring(20, extra_imfo.length());
            //抽取文章正文元素
            Elements content = document.select("div[class=TRS_Editor]");
            String content_html = content.html();
            String content_text = content.text();

            //数据库信息写入
            if (jdbcTemplate != null) {
                int updates=jdbcTemplate.update(
                    "insert into article (UUID, ASOURCE, ADATE, ATITLE, ACONTENT, AURL, ATYPE, AOTHER, AIMGURL) value(?,?,?,?,?,?,?,?,?)",
                        1001, releaseSource, releaseTime, title, content_html, fetchQueueItem.getUrl(), 7, null, null);
                if(updates==1){
                    log.info("mysql插入成功");
                }
            }
        }
        return links;
    }

    public static void main(String[] args) throws Exception {
        AgriculturalNewsCrawler agriculturalNewsCrawler = new AgriculturalNewsCrawler();
        agriculturalNewsCrawler.start(2);
    }
}
