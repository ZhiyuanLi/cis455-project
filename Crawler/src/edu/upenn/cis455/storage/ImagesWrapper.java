// A BerkeleyDB wrapper for the Indexer images DB
// Author: Christopher Besser
package edu.upenn.cis455.storage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
public class ImagesWrapper
{
	// added for singleton
	private static String dbPath;
	private static Environment environment;
	private static EntityStore store;
	private static PrimaryIndex<String, WebDocument> webDocIndex;
	private static File datebaseDir;
	/**
	 * Construct a new Database wrapper
	 */
	public ImagesWrapper()
	{
	}

	/**
	 * Construct a new Database wrapper
	 * @param path - the path to the database
	 */
	public ImagesWrapper(String path)
	{
		dbPath = path;
		setup();
	}

	/**
	 * setup database, environment and fields
	 */
	private void setup()
	{
		createDatabase();
		setupEnvironment();
	}

	/**
	 * create database using dbPath
	 */
	private void createDatabase()
	{
		// create database
		File f = new File(dbPath);
		if (f.exists())
		{
			datebaseDir = f;
		}
		else
		{
			System.out.println("Database created");
			f.mkdir();
			datebaseDir = f;
		}
	}

	/**
	 * set up environments and initial fields
	 */
	public void setupEnvironment()
	{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setReadOnly(false);
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setReadOnly(false);
		storeConfig.setAllowCreate(true);
		storeConfig.setTransactional(true);
		// initial fields
		environment = new Environment(datebaseDir, envConfig);
		store = new EntityStore(environment, "EntityStore", storeConfig);
		webDocIndex = store.getPrimaryIndex(String.class, WebDocument.class);
	}

	/**
	 * close database
	 */
	public void close()
	{
		if (store != null)
		{
			store.close();
		}
		if (environment != null)
		{
			environment.close();
		}
	}

	/**
	 * get web document index
	 * @return Returns the list of documents
	 */
	public PrimaryIndex<String, WebDocument> getWebDocuments()
	{
		return webDocIndex;
	}

	/**
	 * get webDoc as a list using entity cursor
	 * @return Returns all stored documents
	 */
	public List<WebDocument> getDocumentList()
	{
		List<WebDocument> documentList = new ArrayList<WebDocument>();
		EntityCursor<WebDocument> cursor = webDocIndex.entities();
		try
		{
			Iterator<WebDocument> i = cursor.iterator();
			while (i.hasNext())
			{
				documentList.add(i.next());
			}
		}
		finally
		{
			cursor.close();
		}
		return documentList;
	}

	/**
	 * add web document to webDocIndex
	 * @param webDoc - the document to add
	 */
	public void addDocument(WebDocument webDoc)
	{
		webDocIndex.put(webDoc);
	}

	/**
	 * get web document given url
	 * @param url - the url of the requested document
	 * @return Returns the associated document
	 */
	public WebDocument getDocument(String url)
	{
		return webDocIndex.get(url);
	}

	/**
	 * delete web document given url
	 * @param url - the URL of the document to remove
	 */
	public void deleteDocument(String url)
	{
		webDocIndex.delete(url);
	}
}
