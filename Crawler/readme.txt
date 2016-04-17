Final Project readme
========================How to run server and crawler========================

You can run the crawler by:
1: Please make sure you add "sudo" in the command line to get same permission access as servets.
2: you can use command line given in test guild with sudo added to front:
sudo java -cp crawler.jar:[all jar files] + edu.upenn.cis455.crawler.Crawler URL DBPath IndexerDBPath SeedURLsPath MaxSize [MaxNumOfFiles]

========================web.xml database path========================
web.xml: database path in my submission is: /home/cis455/workspace/HW2/src/database. If you want to change to another folder, just replace you path into param-value under context-param with param-name BDBstore.
Database will be created if not exist and retrieved if already exist.
Indexer database path is /home/cis455/workspace/HW2/src/indexdatabase. Replace the value within indexDBstore's param-value in web.xml. 
