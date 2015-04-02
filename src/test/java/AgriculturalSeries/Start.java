package AgriculturalSeries;

/**
 * 启动爬虫 http://www.gdagri.gov.cn/zxpt/snnyxxlb/
 * AgriculturalSeries.ProvincialInformationCrawler 省内农业信息联播爬虫
 * Created by Bin on 2015/3/25.
 */
public class Start {
    public static void main(String[] args) throws Exception {
        EnterpriseCrawler gdagriAppCrawler2 = new AgriculturalSeries.EnterpriseCrawler();
        gdagriAppCrawler2.start(2);
        //DynamicWorkCrawler dynamicWorkCrawler = new AgriculturalSeries.DynamicWorkCrawler();
        //dynamicWorkCrawler.start(2);
        //ProvincialInformationCrawler provincialInformationCrawler = new AgriculturalSeries.ProvincialInformationCrawler();
        //provincialInformationCrawler.start(2);
        //PriceQuotationsCrawler priceQuotationsCrawler = new AgriculturalSeries.PriceQuotationsCrawler();
        //priceQuotationsCrawler.start(2);
        //ConsumerKnowledgeCrawler consumerKnowledgeCrawler = new AgriculturalSeries.ConsumerKnowledgeCrawler();
        //consumerKnowledgeCrawler.start(2);
        //OfficialNoticeCrawler officialNoticeCrawler = new OfficialNoticeCrawler();
        //officialNoticeCrawler.start(2);
    }
}
