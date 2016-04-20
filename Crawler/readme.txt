Web crawler readme
========================How to run crawler from command line========================
You can run the crawler through command line by:
1. Please ensure the following jar files exist within the ./target/WEB-INF/lib/ directory:
	jtidy-r938.jar
	je-5.0.34.jar
	jsoup-1.9.1.jar
	servlet-api.jar
2. In this directory, run "ant build"
3. Run "java -cp crawler.jar:" + jar1 + ":" + jar2 + ... + jar4 + "edu.upenn.cis455.crawler.Crawler" StartURL DBPath IndexerDBPath SeedURLsPath MaxSize logPath [MaxNumOfFiles]
	StartURL may be any valid URL (either normalized or not)
	DBPath is the path to the PageRank BerkeleyDB location
	IndexerDBPath is the path to the Indexer BerkeleyDB location
	SeedURLsPath need be the path to an existing .txt file. This argument is unused if running through command prompt.
	MaxSize is the maximum size, in MB, of a page to crawl
	logPath is the path to a .txt file to write logs to. This will be appended to, so delete it between runs.
	MaxNumOfFiles is optional. Specifies the number of pages to crawl before stopping. Defaults to Integer.MAX_VALUE

========================web.xml database path=======================================
web.xml: database path in my submission is: /home/cis455/workspace/HW2/src/database. If you want to change to another folder, just change the path under param-value within the context-param with param-name BDBstore.
Database will be created if it does not already exist and is retrieved if it does already exist.
Indexer database path is /home/cis455/workspace/HW2/src/indexdatabase. Replace the value within indexDBstore's param-value in web.xml to move it.

========================test cases==================================================
To run test cases through command line, please
1. Ensure that the jar junit.jar is located in the ./target/WEB-INF/lib/ directory, along with all other jars listed above.
2. Run "ant build" within this directory to produce the project jar file.
3. Run "ant test" within this directory to compile and run the test cases. The results will print to console.

========How to run crawler Hadoop MapReduce version (standalone mode)===============

========How to run crawler Hadoop MapReduce version through AWS Elastic Map Reduce==

