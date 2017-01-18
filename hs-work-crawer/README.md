# crawler
simple crawler program witch use  crawler4j
部分通用参数在conf/system.properties中配置
但是因为抓取规则的不确定，一些参数需要根据场景再代码中设置，具体参见ebay.EbayCrawlerMain

注：
如果变更了代理的抓取链接，那么CrawlerFreeProxy需要微微做调整，现在绑定死了对应代理列表页的抓取规则