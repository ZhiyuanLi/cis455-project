package edu.upenn.cis455.crawler;
import edu.upenn.cis455.storage.*;
public class PrintDBs
{
	/**
	 * Print the DB contents
	 * @param db1 - the name of the first db
	 * @param db2 - the name of the second db
	 * @param db3 - the name of the third db
	 * @throws Exception if a DB cannot be opened
	 */
	public static void print(DatabaseWrapper db1, IndexWrapper db2, ImagesWrapper db3) throws Exception
	{
		for (WebDocument doc: db1.getDocumentList())
		{
			System.out.println("Body: " + doc.getDocumentContent());
		}
		for (WebDocument doc: db2.getDocumentList())
		{
			System.out.println("Title: " + doc.getDocumentTitle());
			System.out.println("Content: " + doc.getDocumentContent());
		}
		for (WebDocument doc: db3.getDocumentList())
		{
			System.out.println("ImageURLs: " + doc.getDocumentContent());
		}
	}

	/**
	 * Run the program through command prompt
	 * @param args - the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		if (args.length != 3)
		{
			System.out.println("Usage: PrintDBs <PageRankDB name> <IndexerDB name> <ImageDB name>");
			return;
		}
		DatabaseWrapper db1 = new DatabaseWrapper(args[0]);
		IndexWrapper db2 = new IndexWrapper(args[1]);
		ImagesWrapper db3 = new ImagesWrapper(args[2]);
		print(db1, db2, db3);
		db1.close();
		db2.close();
		db3.close();
	}
}
