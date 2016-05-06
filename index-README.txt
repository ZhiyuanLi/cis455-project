======================================= How to download the required files =====================================
Please ensure that the following files are downloaded. All can be downloaded from Maven. Just Google the jar
	names and click the first or second link.
   1) hadoop-mapreduce-client-app-2.7.1.jar
   2) hadoop-mapreduce-client-common-2.7.1.jar
   3) hadoop-mapreduce-client-core-2.7.1.jar
   4) hadoop-mapreduce-client-hs-2.7.1.jar
   5) hadoop-mapreduce-client-hs-plugins-2.7.1.jar
   6) hadoop-mapreduce-client-jobclient-2.7.1-tests.jar
   7) hadoop-mapreduce-client-jobclient-2.7.1.jar
   8) hadoop-mapreduce-client-shuffle-2.7.1.jar
   9) hadoop-common-2.7.1.jar
   10) hadoop-nfs-2.7.1.jar

======================================= How to run the indexer on EMR ==========================================
   1) Create a new bucket on S3. Within this bucket create a title output directory, body output directory, logs directory, and jars directory.
   2) Create the indexer jar using indexer-build.xml.
   3) Within the step execution, the parameters should be <title input directory> <title output directory>.
	The input directory should contain the crawler title data outputs.
   4) When the job terminates, clone the instance and change the step execution parameters to <html bodies directory> <bodies output directory>.
	This input directory should contain the crawler html text output.
