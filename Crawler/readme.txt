Web crawler readme
========================How to run crawler from command line========================
You can run the crawler through command line by:
1. Please ensure the following jar files exist within the ./target/WEB-INF/lib/ directory:
	jtidy-r938.jar
	je-5.0.34.jar
	jsoup-1.9.1.jar
	servlet-api.jar
2. In this directory, run "ant build"
3. Run "java -cp crawler.jar:" + jar1 + ":" + jar2 + ... + jar4 + "edu.upenn.cis455.crawler.Crawler" <DBPath> <IndexerDBPath> <SeedURLsPath> <MaxSize> <logPath> [MaxNumOfFiles]
	DBPath is the path to the PageRank BerkeleyDB location. Will be created if it does not exist. Delete between runs if you wish to start over, because it is appended to if it exists.
	IndexerDBPath is the path to the Indexer BerkeleyDB location. Will be created if it does not exist. Delete between runs if you wish to start over, because it is appended to if it exists.
	SeedURLsPath is the path to an existing .txt file filled with seed urls. A sample one is located at ./src/urls/seeds.txt.
	MaxSize is the maximum size, in MB, of a page to crawl.
	logPath is the path to a .txt file to write logs to. This will be appended to if it exists, so delete it between runs if you wish to start over. Will be created if it does not exist.
	MaxNumOfFiles is optional. Specifies the number of pages to crawl before stopping. Defaults to Integer.MAX_VALUE
4. If in step 3 you deleted one of the databases or the log file, you must delete all three. Otherwise the crawler will output files that do not match.

========================web.xml database path=======================================
web.xml: database path in my submission is: /home/cis455/FinalProjectGithub/cis455-project/src/database. If you want to change to another folder, just change the path under param-value within the context-param with param-name BDBstore.
Database will be created if it does not already exist and is retrieved if it does already exist.
Indexer database path is /home/cis455/FinalProjectGithub/cis455-project/src/indexdatabase. Replace the value within indexDBstore's param-value in web.xml to move it.

========================test cases==================================================
To run test cases through command line, please
1. Please ensure that junit.jar is located in the ./target/WEB-INF/lib/ directory, along with all other jars listed above.
2. Run "ant build" within this directory to produce the project jar file.
3. Run "ant test" within this directory to compile and run the test cases. The results will print to console.

========How to run crawler Hadoop MapReduce version (standalone mode)===============
You can locally run the MapReduce job in pseudo-distributed mode through the following:
1. Run "ant build" within this directory to produce the project jar file.
2. Ensure that the jar hadoop-core-1.0.3.jar appears within the ./lib/ directory.
3. In this directory, type "hadoop jar crawler.jar edu.upenn.cis455.crawler.CrawlerDriver" <seedsPath> <PageRankDB Path> <IndexerDB Path> <Temp1 Dir> <Temp2 Dir> <MaxSize> <URLs log Path> [MaxPages]
	seedPath is the path to an existing .txt file containing seeds, 1 per line.
	PageRankDB path need not exist. If it does, it will be appended to. Delete this directory if you wish to restart the crawl.
	IndexerDB path need not exist. If it does, it will be appended to. Delete this directory if you wish to restart the crawl.
	Temp1 dir is the output path for the shuffle step. This directory should not exist prior to starting. It will be deleted if it does. Deleted after termination.
	Temp2 dir is the output path for the crawler step. This directory should not exist prior to starting. It will be deleted if it does. Deleted when done.
	MaxSize specifies the maximum file size, in MB, of a page to crawl.
	URL Log path specifies the path where a log .txt file is created. If it exists, it is appended to. Delete it before starting if you wish to crawl from the beginning.
	MaxPages is optional. If not specified, the job runs indefinitely. Specifies roughly how many pages to crawl.
4. If the PageRankDB, IndexerDB, or log file were deleted, delete all three. Otherwise the output files will not agree.

========How to run crawler Hadoop MapReduce version through AWS Elastic Map Reduce==
1. Create an AmazonAWS instance through the AmazonEC2 console. Take note of the IP address.
2. Run "ant build" in this directory to compile the project into a jar file.
3. When the instance is live, scp the jar file to your instance. Place it in a directory named lib in root.
4. scp all aformentioned jar files except hadoop-core-1.0.3.jar to the instance.
5. ssh to your instance.
6. Follow steps 3+4 in the Hadoop MapReduce crawler standalone mode instructions above.
7. If desired, scp the output files back to the local system.
8. Terminate the instance through the AmazonEC2 console.
