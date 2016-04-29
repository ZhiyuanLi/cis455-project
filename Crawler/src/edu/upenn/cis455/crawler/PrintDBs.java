package edu.upenn.cis455.crawler;
import edu.upenn.cis455.storage.*;
public class PrintDBs
{
	private static int inc;
	/**
	 * Print the DB contents
	 * @param db1 - the name of the second db
	 * @param db2 - the name of the third db
	 * @throws Exception if a DB cannot be opened
	 */
	public static void print(IndexWrapper db1, ImagesWrapper db2) throws Exception
	{
		if (inc == 1)
		{
			System.out.println();
			for (WebDocument doc: db1.getDocumentList())
			{
				System.out.println("URL = " + doc.getURL());
				System.out.println("Title: " + doc.getDocumentTitle());
				System.out.println("Content: " + doc.getDocumentContent());
			}
			System.out.println();
			for (WebDocument doc: db2.getDocumentList())
			{
				System.out.println("URL = " + doc.getURL());
				System.out.println("ImageURLs: " + doc.getDocumentContent());
			}
		}
		System.out.println("Indexer DB Length: " + db1.getDocumentList().size());
		System.out.println("Image DB Length: " + db2.getDocumentList().size());
	}

	/**
	 * Run the program through command prompt
	 * @param args - the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		if (args.length == 3)
		{
			if (args[0].equals("-l"))
			{
				inc = 1;
			}
			else
			{
				System.out.println("Usage: PrintDBs [-l] <IndexerDB name> <ImageDB name>");
				return;
			}
		}
		else if ((args.length < 2) || (args.length > 3))
		{
			System.out.println("Usage: PrintDBs [-l] <IndexerDB name> <ImageDB name>");
			return;
		}
		IndexWrapper db1 = new IndexWrapper(args[inc]);
		ImagesWrapper db2 = new ImagesWrapper(args[1 + inc]);
		print(db1, db2);
		db1.close();
		db2.close();
	}
}
