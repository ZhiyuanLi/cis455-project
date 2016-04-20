Web crawler readme
========================How to run crawler from command line========================
You can run the crawler through command line by:
1. Please ensure the following jar files exist within the ./target/WEB-INF/lib/ directory:
	jtidy-r938.jar
	je-5.0.34.jar
	jsoup-1.9.1.jar
	servlet-api.jar
2: Run "java -cp crawler.jar:" + jar1 + ":" + jar2 + ... + jar4 + "edu.upenn.cis455.crawler.Crawler" StartURL DBPath IndexerDBPath SeedURLsPath MaxSize [MaxNumOfFiles]
	StartURL may be any valid URL (either normalized or not)
	DBPath is the path to the PageRank BerkeleyDB location
	IndexerDBPath is the path to the Indexer BerkeleyDB location
	SeedURLsPath need be the path to an existing .txt file. This argument is unused if running through command prompt.
	MaxSize is the maximum size, in MB, of a page to crawl
	MaxNumOfFiles is optional. Specifies the number of pages to crawl before stopping. Defaults to Integer.MAX_VALUE

========================web.xml database path========================
web.xml: database path in my submission is: /home/cis455/workspace/HW2/src/database. If you want to change to another folder, just change the path under param-value within the context-param with param-name BDBstore.
Database will be created if it does not already exist and is retrieved if it does already exist.
Indexer database path is /home/cis455/workspace/HW2/src/indexdatabase. Replace the value within indexDBstore's param-value in web.xml to move it.
