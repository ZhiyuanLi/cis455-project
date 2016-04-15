HW2 MS2 readme
========================How to run server and crawler========================
You can run the server by 
1:ant build in current folder
2:move servlet.war to /usr/share/jetty
3:use Jetty server to run localhost:8080/servlet/create xpath to start.
4: Please make sure you add "sudo" in the command line.

You can run the crawler by:
1: Please make sure you add "sudo" in the command line to get same permission access as servets.
2: you can use command line given in test guild with sudo added to front:
sudo java -cp crawler.jar:[all jar files] + edu.upenn.cis455.crawler.XPathCrawler URL DBDPath MaxSize [MaxNumOfFiles]


========================web.xml database path========================
web.xml: database path in my submission is: /home/cis455/workspace/HW2/src/database. If you want to change to another folder, just replace you path into param-value under context-param with param-name BDBstore.
Database will be created if not exist and retrieved if already exist.

========================HTML servlet relative path========================
HTML servlet relative path:
To start: please user /create to start testing!
1: /create for user to create an account
2: /login to login account
3: /logout to logout account, should be directed from button on page after user login, should not direct access
4: /channel+something else to manage channel and display, should be directed from button on page after user login, should not direct access


========================rss.xml for RSS 2.0========================
RSS 2.0
rss.xml and warandpeace.xp is under /rss folder.


========================Extra Credits========================
1. Finished Extra Credit 6.1: Advanced crawler design. 
 Design pattern is based on Mercator which has URL Frontier and Content Seen test. The URL Frontier is a separate class and Content Seen test method in XPathCrawler class. (15%)

2. Finished Extra Credit 6.3: Channel subscription
   Will display user channels when user login besides displaying all channels. User can also select which spacific channels to delete and seen detail information. (5%)


