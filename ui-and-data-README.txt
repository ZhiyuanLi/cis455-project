========================================== How to download the required files =================================
Please ensure that the following file is downloaded:
   1) aws-java-sdk package. You can download this by Googling "aws java sdk" then downloading from the first result (Amazon AWS).
   2) big.txt. This can be downloaded from http://raelcunha.com/spell-correct/. Place this file in the same directory as SpellCheck.java.

========================================= How to launch the front end through Amazon EC2 ======================
   1) Within src/edu/upenn/cis455/servlet/SearchServlet.java, change the file paths stored to the variable geoPath to the path to the
	processed geolocation data output by the PageRank step.
   2) Still within SearchServlet.java, change the file path stored to variable dictPath to the path to the dictionary file downloaded
	in the above dependencies step.
   3) Within SearchServlet.java, change the file path stored to variable titlePath to the path to the Indexer forward index.
   4) Within SearchServlet.java, change the file path stored to variable rankPath to the path to the PageRank output.
   5) Use the provided buildServlet.xml to compile the job into a war file.
   6) Launch an EC2 instance through the AWS console.
   7) When the instance is live, use the instance's public IP address to scp the .war file onto the instance. 
   8) scp the spellcheck file big.txt to the instance.
   9) ssh to the same instance.
   10) When on the instance, install Jetty using "sudo apt-get install jetty".
   11) cp the .war file to /usr/share/jetty/webapps/.
   12) cd /usr/share/jetty/
   13) Run the command "sudo java -jar start.jar" to launch the server.
   14) In a Web Browser, navigate to EC2-public-DNS:8080/servlet/search/
