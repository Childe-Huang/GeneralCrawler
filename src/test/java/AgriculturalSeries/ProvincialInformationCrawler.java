package AgriculturalSeries;

import crawler.BaseCrawler;
import fetcher.FetchQueueItem;
import model.CrawlerData;
import model.Links;
import nlpir.LocalAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.CrawlerDataDao;
import util.RegexRule;

/**
 * 省内信息联播爬虫
 * Created by Bin on 2015/3/25.
 */
public class ProvincialInformationCrawler extends BaseCrawler {
    RegexRule regexRule;

    public ProvincialInformationCrawler() {
        regexRule = new RegexRule();
        regexRule.addRule("http://www.gdagri.gov.cn/.+.html");
        regexRule.addRule("-.*jpg.*");
        regexRule.addRule("-.*pdf.*");
        regexRule.addRule("-.*doc.*");
        this.setRegexRules(regexRule);
        String seedUrl;
        for (int i = 0; i < 3; i++) {
            seedUrl = "http://www.gdagri.gov.cn/zxpt/snnyxxlb/";
            if (i != 0) {
                seedUrl = seedUrl + "index_" + i + ".html";
            }
            this.addSeed(seedUrl);
        }
        this.setEncodingProcess(1);
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
        if (fetchQueueItem.getLayer() == 1) {
            links = links.getLinksFromDoc(document, "div[class=glright]", regexRule);
        } else {
            //抽取文章标题
            String title = document.select("body h2").get(0).text();
            //System.out.println(title);
            //抽取文章附加信息，如发布时间和来源等
            String extra_imfo = document.select("p[class=con_info]").text().replaceAll(Jsoup.parse("&nbsp;").text(), " ");
            extra_imfo = extra_imfo.substring(0, 10);
            //System.out.println(extra_imfo);
            //抽取文章正文元素
            Elements content = document.select("div[class=TRS_Editor]");
            String content_html = content.html();
            String content_text = content.text();
            //分析器
            LocalAnalyzer analyzer1 = new LocalAnalyzer();
            String keywords = analyzer1.getKeyWords(content_text, 5);
            /*OnlineAnalyzer analyzer2 = new OnlineAnalyzer();
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
            gdagriCrawlerData.setMemo("省内信息联播");
            gdagriCrawlerData.setKeywords(keywords);
            CrawlerDataDao.insertData(gdagriCrawlerData);
        }
        return links;
    }
}
