1、爬虫，来获取网站的html数据
	nutch，Python（主流），wget（c语言）
	
	wget -o /tmp/wget.log -P /root/data  --no-parent --no-verbose -m -D news.cctv.com   -N --convert-links --random-wait -A html,HTML,shtml,SHTML http://news.cctv.com
2、数据抽取：从网页中抽取数据
3、把抽取出来的数据同ES建立索引


4、搜索


news.cctv.com/2017/10/06/ARTIZbHyClb2f7DMTDr1uDO9171006.shtml