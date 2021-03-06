# GeneralCrawler
聚焦爬虫通用框架
###项目描述：
本项目旨在通过使用JAVA语言实现一个基于目标网页特征（网页内容特征和URL正则特征）和广度优先搜索策略的多线程聚焦爬虫程序框架。通过使用此框架可以简单、高效地完成具备个性化需求的爬虫程序的开发定制。

###项目特性：
* 1）HttpClient模拟浏览器发送请求（目前只支持get请求）
* 2）集成Jsoup解析器，用于HTML页面解析
* 3）使用Redis对URL快速去重
* 4）使用slf4j作为日志门面

###项目功能：
* 1）支持个性化性质
* 2）支持定时任务增量爬取数据
* 3）支持使用cookie模拟登录
* 4）支持设置代理列表并随机切换
* 5）集成SpringJDBC和JDBC，支持数据持久化
* 6）集成ICTCLAS，支持关键词提取
