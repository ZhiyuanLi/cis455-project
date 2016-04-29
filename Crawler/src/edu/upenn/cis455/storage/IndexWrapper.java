// BerkeleyDB wrapper for the indexer HTML body DB
// Author: Christopher Besser
package edu.upenn.cis455.storage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
//import com.sleepycat.persist.Cursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import java.util.HashSet;
public class IndexWrapper
{
	// added for singleton
	private static String dbPath;
	private static Environment environment;
	private static EntityStore store;
	private static PrimaryIndex<String, WebDocument> webDocIndex;
	private static SecondaryIndex<String, String, WebDocument> hashIndex;
	private static File databaseDir;
	/**
	 * Construct a new Database wrapper
	 */
	public IndexWrapper()
	{
	}

	/**
	 * Construct a new Database wrapper
	 * @param path - the path to the database
	 */
	public IndexWrapper(String path)
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
			databaseDir = f;
		}
		else
		{
			f.mkdir();
			databaseDir = f;
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
		environment = new Environment(databaseDir, envConfig);
		store = new EntityStore(environment, "EntityStore", storeConfig);
		webDocIndex = store.getPrimaryIndex(String.class, WebDocument.class);
		hashIndex = store.getSecondaryIndex(webDocIndex, String.class, "hashValue");
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
	 * Add a document as a hit to another document
	 * @param firstURL - the document to gain a hit
	 * @param otherURL - the duplicate content URL
	 */
	public void addHit(String firstURL, String otherURL)
	{
		//WebDocument theKey = new WebDocument(firstURL.getBytes("UTF-8"));
		//WebDocument theData = new WebDocument();
		WebDocument doc = getDocument(firstURL);
		HashSet<String> hits = doc.getHits();
		hits.add(otherURL);
		doc.setHits(hits);
		webDocIndex.put(doc);
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
	 * Determine if the frontier has a hash value
	 * @param hash - the hashcode
	 * @return Returns true if the hashcode corresponds to a content
	 */
	public boolean containsHash(String hash)
	{
		EntityCursor<WebDocument> cursor = hashIndex.subIndex(hash).entities();
		for (WebDocument doc: cursor)
		{
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * Return the URL corresponding to the hash value
	 * @param hash - the hashcode
	 * @return Returns the URL corresponding to the hash value
	 */
	public String getHash(String hash)
	{
		EntityCursor<WebDocument> cursor = hashIndex.subIndex(hash).entities();
		for (WebDocument doc: cursor)
		{
			cursor.close();
			return doc.getURL();
		}
		cursor.close();
		return null;
	}

	/**
	 * get webDoc as a list using entity cursor
	 * @return Returns all stored documents
	 */
	public List<WebDocument> getDocumentList()
	{
		List<WebDocument> documentList;
		documentList = new ArrayList<WebDocument>();
		EntityCursor<WebDocument> cursor = webDocIndex.entities();
		Iterator<WebDocument> i = cursor.iterator();
		while (i.hasNext())
		{
			documentList.add(i.next());
		}
		cursor.close();
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
