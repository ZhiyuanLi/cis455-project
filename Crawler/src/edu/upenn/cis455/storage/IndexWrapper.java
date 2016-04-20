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
public class IndexWrapper
{
	// added for singleton
	private static String dbPath;
	private static Environment environment;
	private static EntityStore store;
	private static PrimaryIndex<String, User> userIndex;
	private static PrimaryIndex<String, WebDocument> webDocIndex;
	private static PrimaryIndex<String, Channel> channelIndex;
	private static File datebaseDir;

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
		userIndex = store.getPrimaryIndex(String.class, User.class);
		webDocIndex = store.getPrimaryIndex(String.class, WebDocument.class);
		channelIndex = store.getPrimaryIndex(String.class, Channel.class);
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
	 * get user index
	 * @return Returns the index of users
	 */
	public PrimaryIndex<String, User> getUsers()
	{
		return userIndex;
	}

	/**
	 * add user to user index
	 * @param user - the user to add
	 */
	public void addUser(User user)
	{
		userIndex.put(user);
	}

	/**
	 * check if user already exist
	 * @param userName - the name to search for
	 * @return Returns true if the user exists in the index
	 */
	public boolean containsUser(String userName)
	{
		return userIndex.contains(userName);
	}

	/**
	 * get user given user name
	 * @param userName - the username to search for
	 * @return Returns the associated user
	 */
	public User getUser(String userName)
	{
		return userIndex.get(userName);
	}

	/**
	 * get users as a list using entity cursor
	 * @return - returns all users
	 */
	public List<User> getUserList()
	{
		List<User> userList = new ArrayList<User>();
		EntityCursor<User> cursor = userIndex.entities();
		try
		{
			Iterator<User> i = cursor.iterator();
			while (i.hasNext())
			{
				userList.add(i.next());
			}
		}
		finally
		{
			cursor.close();
		}
		return userList;
	}

	/**
	 * delete user in user index and also channels under this user
	 * @param userName - the username to remove
	 */
	public void deleteUser(String userName)
	{
		// 1. delete user from user index
		userIndex.delete(userName);
		// 2. delete all channels under this user
		EntityCursor<Channel> cursor = channelIndex.entities();
		try
		{
			Iterator<Channel> i = cursor.iterator();
			while (i.hasNext())
			{
				// only delete all channels if channel's user name match with current user name
				Channel channel = i.next();
				if (channel.getUserName() != null && channel.getUserName().equals(userName))
				{
					channelIndex.delete(channel.getCName());
				}
			}
		}
		finally
		{
			cursor.close();
		}
	}

	/**
	 * get channel index
	 * @return - Returns the list of channels
	 */
	public PrimaryIndex<String, Channel> getChannels()
	{
		return channelIndex;
	}

	/**
	 * get channels as a list using entity cursor
	 * @return Returns the list of channels
	 */
	public List<Channel> getChannelList()
	{
		List<Channel> channelList = new ArrayList<Channel>();
		EntityCursor<Channel> cursor = channelIndex.entities();
		try
		{
			Iterator<Channel> i = cursor.iterator();
			while (i.hasNext())
			{
				channelList.add(i.next());
			}
		}
		finally
		{
			cursor.close();
		}
		return channelList;
	}

	/**
	 * get channel object given channel name
	 * @param cName - the name of the channel
	 * @return Returns the channel
	 */
	public Channel getChannel(String cName)
	{
		return channelIndex.get(cName);
	}

	/**
	 * add a channel to database
	 * @param channel - the channel to add
	 */
	public void addChannel(Channel channel)
	{
		if (channel == null)
		{
			return;
		}
		channelIndex.put(channel);
	}

	/**
	 * add a channel to database
	 * @param channel - the channel to add
	 * @param userName - the username to associate the channel to
	 */
	public void addChannel(Channel channel, String userName)
	{
		channelIndex.put(channel);
		User user = userIndex.get(userName);
		user.addChannelName(channel.getCName());
		userIndex.put(user);
	}

	/**
	 * remove a channel from user who own this channel
	 * @param username - the user whose channel should be removed
	 * @param cname - the name of the channel
	 */
	public void deleteChannel(String userName, String cName)
	{
		// 1. delete channel from channel index
		channelIndex.delete(cName);
		// 2. delete channel under corresponding user
		User channelUser = userIndex.get(userName);
		channelUser.removeChannel(cName);
		userIndex.delete(userName);
		userIndex.put(channelUser);
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
