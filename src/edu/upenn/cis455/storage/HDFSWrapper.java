package edu.upenn.cis455.storage;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.util.*;

public class HDFSWrapper {
	 public static void main(String[] args) throws IOException, URISyntaxException 
	 {
	  //1. Get the instance of Configuration
	  Configuration configuration = new Configuration();
	  //2. URI of the file to be read
	  URI uri = new URI("hdfs://localhost:9000/user/input/index_input1");
	  //3. Get the instance of the HDFS 
	  FileSystem hdfs = FileSystem.get(uri, configuration);
	  //4. A reference to hold the InputStream
	  InputStream inputStream = null;
	  try{
	   //5. Prepare the Path, i.e similar to File class in Java, Path represents file in HDFS
	   Path path = new Path(uri);
	   //6. Open a Input Stream to read the data from HDFS
	   inputStream = hdfs.open(path);
	   //7. Use the IOUtils to flush the data from the file to console
	   IOUtils.copyBytes(inputStream, System.out, 4096, false);
	  }finally{
	   //8. Close the InputStream once the data is read
	   IOUtils.closeStream(inputStream);
	  }

	 }

}
