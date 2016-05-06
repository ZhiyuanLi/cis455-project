Web crawler readme
========================Required .jar files======================================================
Please ensure the following jar files exist within the ./src/lib/ directory:
	jtidy-r938.jar. Google search the jar name. Download it from sourceforge (first link).
	jsoup-1.9.1.jar. Google search the jar name. Download it from the second link, on jsoup.org
	hadoop-core-1.0.3.jar. Google search the jar name. Download it from mvnrepository.com.
	junit.jar. Google search "junit download." On the first link, junit.org, click "Download and Install" near the middle of the page.
	rabin-hash-function-2.0.jar. Google seearch the jar name. Download it from sourceforge (first link).

=======================How to run the crawler from the command line==============================
1. In this directory, run "ant build"
2. Run "java -cp crawler.jar edu.upenn.cis455.crawler.CrawlerThreaded" <Thread count> <page count> <input path> <pagerank input root name> <indexer titles root name> <indexer bodies root name> <indexer images root name>
	Thread count dictates the number of threads used. On the ubuntu-trusty-14.04-amd64-server (the one we used for testing), this should be about 150.
	Page count specifies approximately how many pages are crawled.
	inputPath is the path to a .txt file containing the seeds.
	pagerankinputrootname is the root name of the pagerank input files. Each worker appends workerID + ".txt".
	indexertitlesrootname is the root name of the indexer titles input files. Each worker appends workerID + ".txt".
	indexerbodiesrootname is the root name of the pagerank bodies input files. Each worker appends workerID + ".txt".
	indexerimagesrootname is the root name of the indexer images input files. Each worker appends workerID + ".txt".
3. Please delete output files before starting again. Duplicates in the files can screw up results.

=====================================Test cases==================================================
To run test cases through command line, please
1. Run "ant build" within this directory to produce the project jar file.
2. Run "ant test" within this directory to compile and run the test cases. The results will print to console.

==================================How to run the crawler through AWS EC2=========================
1. Create an AmazonAWS instance through the AmazonEC2 console. Take note of the IP address.
2. Run "ant build" in this directory to compile the project into a jar file.
3. When the instance is live, scp the jar file to your instance. Place it in a directory named lib in root. Provided you used the included build.xml, you do not need to transfer the other jars.
4. scp the seed file to the instance.
5. ssh to your instance.
6. Use mkdir to produce folders "index," "image," "title," and "links". Otherwise the root directory will become too cluttered
7. Follow steps 2+3 in the crawler command line instructions above.
8. Transfer the output files to persistent storage like S3.
9. Terminate the instance through the AmazonEC2 console to avoid extraneous charges.
