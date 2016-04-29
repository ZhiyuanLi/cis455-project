Web crawler readme
========================Required .jar files================================================
Please ensure the following jar files exist within the ./src/lib/ directory:
	jtidy-r938.jar
	je-5.0.34.jar
	jsoup-1.9.1.jar
	servlet-api.jar
	hadoop-core-1.0.3.jar
	junit.jar
	rabin-hash-function-2.0.jar

=======================How to run the crawler from the command line==============================
1. In this directory, run "ant build"
2. Run "java -cp crawler.jar edu.upenn.cis455.crawler.Crawler" <IndexerDBPath> <imageDBPath> <SeedURLsPath> <MaxSize> <logPath> [MaxNumOfFiles]
	IndexerDBPath is the path to the Indexer BerkeleyDB location. Will be created if it does not exist. Delete between runs if you wish to start over, because it is appended to if it exists.
	ImageDBPath is the path to the Indexer BerkeleyDB location. Will be created if it does not exist. Delete between runs if you wish to start over, because it is appended to if it exists.
	SeedURLsPath is the path to an existing .txt file filled with seed urls. A sample one is located at ./src/urls/seeds.txt.
	MaxSize is the maximum size, in KB, of a page to crawl.
	logPath is the path to a .txt file to write logs to. This will be appended to if it exists, so delete it between runs if you wish to start over. Will be created if it does not exist.
	MaxNumOfFiles is optional. Specifies the number of pages to crawl before stopping. Defaults to Integer.MAX_VALUE
3. If in step 2 you deleted one of the databases or the log file, you must delete all three. Otherwise the crawler will output files that do not match.

========================Test cases==================================================
To run test cases through command line, please
1. Run "ant build" within this directory to produce the project jar file.
2. Run "ant test" within this directory to compile and run the test cases. The results will print to console.

========How to run crawler Hadoop MapReduce version (standalone mode)===============
You can locally run the MapReduce job in pseudo-distributed mode through the following:
1. Run "ant build" within this directory to produce the project jar file.
2. In this directory, type "hadoop jar crawler.jar edu.upenn.cis455.crawler.CrawlerDriver" <seedsPath> <IndexerDB Path> <ImagesDB path> <Temp1 Dir> <Temp2 Dir> <MaxSize> <URLs log Path> <numWorkers> [MaxPages]
	seedPath is the path to an existing .txt file containing seeds, 1 per line.
	IndexerDB path need not exist. If it does, it will be appended to. Delete this directory if you wish to restart the crawl.
	ImagesDB path need not exist. If it does, it will be appended to. Delete this directory if you wish to restart the crawl.
	Temp1 dir is the output path for the shuffle step. This directory should not exist prior to starting. It will be deleted if it does. Deleted after termination.
	Temp2 dir is the output path for the crawler step. This directory should not exist prior to starting. It will be deleted if it does. Deleted when done.
	MaxSize specifies the maximum file size, in KB, of a page to crawl.
	URL Log path specifies the path where a log .txt file is created. If it exists, it is appended to. Delete it before starting if you wish to crawl from the beginning. Do not place in same directory as seedPath.
	numWorkers sets how many workers process the job.
	iterations is optional. If not specified, the job runs indefinitely. Specifies how many times to launch the crawl job.
3. If in step 2 you deleted IndexerDB, ImagesDB, or the log file, please delete all three. Otherwise the output files will not agree.

========How to run crawler Hadoop MapReduce version through AWS Elastic Map Reduce==
1. Create an AmazonAWS instance through the AmazonEC2 console. Take note of the IP address.
2. Run "ant build" in this directory to compile the project into a jar file.
3. When the instance is live, scp the jar file to your instance. Place it in a directory named lib in root. Provided you used the included build.xml, you do not need to transfer the other jars.
4. scp the seed file to the instance.
5. ssh to your instance.
6. Follow steps 3+4 in the Hadoop MapReduce crawler standalone mode instructions above.
7. Transfer the output files to persistent storage like S3.
8. Terminate the instance through the AmazonEC2 console to avoid extraneous charges.
