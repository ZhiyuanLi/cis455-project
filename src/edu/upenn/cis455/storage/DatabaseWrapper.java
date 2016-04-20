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

public class DatabaseWrapper {
	// added for singleton
	// private volatile static DatabaseWrapper singletonDatabase;

	private static String dbPath;

	private static Environment environment;
	private static EntityStore store;

	private static PrimaryIndex<String, User> userIndex;
	private static PrimaryIndex<String, WebDocument> webDocIndex;
	private static PrimaryIndex<String, Channel> channelIndex;

	private static File datebaseDir;

	// ******** singleton ************
	// private DatabaseWrapper() {
	// }
	//
	// private DatabaseWrapper(String path) {
	// dbPath = path;
	// setup();
	// }
	//
	// public static DatabaseWrapper getSingletonDatabase(String path){
	// System.out.println("Path is "+ path);
	// if(singletonDatabase == null){
	// System.out.println("Not exist when get singleton database");
	// synchronized (DatabaseWrapper.class){
	// if(singletonDatabase == null){
	// System.out.println("Not Exist again");
	// singletonDatabase = new DatabaseWrapper(path);
	// }
	// }
	// }else{
	// System.out.println("Exist when get singleton database");
	// }
	// return singletonDatabase;
	// }
	// ******** singleton end ************

	public DatabaseWrapper() {
	}

	public DatabaseWrapper(String path) {
		dbPath = path;
		setup();
	}

	// ************* setup ****************
	/**
	 * setup database, environment and fields
	 */
	private void setup() {
		createDatabase();
		setupEnvironment();
	}

	/**
	 * create database using dbPath
	 */
	private void createDatabase() {
		// create database
		File f = new File(dbPath);
		if (f.exists()) {
			datebaseDir = f;
		} else {
			System.out.println("Database created");
			f.mkdir();
			datebaseDir = f;
		}
	}

	/**
	 * set up environments and initial fields
	 */
	public void setupEnvironment() {
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
		// environment.sync();
		store = new EntityStore(environment, "EntityStore", storeConfig);
		userIndex = store.getPrimaryIndex(String.class, User.class);
		webDocIndex = store.getPrimaryIndex(String.class, WebDocument.class);
		channelIndex = store.getPrimaryIndex(String.class, Channel.class);
	}

	/**
	 * close database
	 */
	public void close() {
		if (store != null) {
			store.close();
		}
		if (environment != null) {
			environment.close();
		}
	}

	// **************** user *****************

	/**
	 * get user index
	 * 
	 * @return
	 */
	public PrimaryIndex<String, User> getUsers() {
		return userIndex;
	}

	/**
	 * add user to user index
	 * 
	 * @param user
	 */
	public void addUser(User user) {
		userIndex.put(user);
	}

	/**
	 * check if user already exist
	 * 
	 * @param userName
	 * @return
	 */
	public boolean containsUser(String userName) {
		return userIndex.contains(userName);
	}

	/**
	 * get user given user name
	 * 
	 * @param userName
	 * @return
	 */
	public User getUser(String userName) {
		return userIndex.get(userName);
	}

	/**
	 * get users as a list using entity cursor
	 * 
	 * @return
	 */
	public List<User> getUserList() {
		List<User> userList = new ArrayList<User>();
		EntityCursor<User> cursor = userIndex.entities();

		try {
			Iterator<User> i = cursor.iterator();
			while (i.hasNext()) {
				userList.add(i.next());
			}
		} finally {
			cursor.close();
		}
		return userList;
	}

	/**
	 * delete user in user index and also channels under this user
	 * 
	 * @param userName
	 * @return
	 */
	public void deleteUser(String userName) {
		// 1. delete user from user index
		userIndex.delete(userName);
		// 2. delete all channels under this user
		EntityCursor<Channel> cursor = channelIndex.entities();
		try {
			Iterator<Channel> i = cursor.iterator();
			while (i.hasNext()) {
				// only delete all channels if channel's user name match with
				// current user name
				Channel channel = i.next();
				if (channel.getUserName() != null
						&& channel.getUserName().equals(userName)) {
					channelIndex.delete(channel.getCName());
				}
			}
		} finally {
			cursor.close();
		}
	}

	// **************** channel *****************

	/**
	 * get channel index
	 * 
	 * @return
	 */
	public PrimaryIndex<String, Channel> getChannels() {
		return channelIndex;
	}

	/**
	 * get channels as a list using entity cursor
	 * 
	 * @return
	 */
	public List<Channel> getChannelList() {
		List<Channel> channelList = new ArrayList<Channel>();
		EntityCursor<Channel> cursor = channelIndex.entities();

		try {
			Iterator<Channel> i = cursor.iterator();
			while (i.hasNext()) {
				channelList.add(i.next());
			}
		} finally {
			cursor.close();
		}
		return channelList;
	}

	/**
	 * get channel object given channel name
	 * 
	 * @param cName
	 * @return
	 */
	public Channel getChannel(String cName) {
		return channelIndex.get(cName);
	}

	/**
	 * add a channel to database
	 * 
	 * @param channel
	 */
	public void addChannel(Channel channel) {
		if (channel == null)
			return;
		channelIndex.put(channel);
	}

	/**
	 * add a channel to database
	 * 
	 * @param channel
	 * @param userName
	 */
	public void addChannel(Channel channel, String userName) {
		channelIndex.put(channel);
		User user = userIndex.get(userName);
		user.addChannelName(channel.getCName());
		userIndex.put(user);
	}

	/**
	 * remove a channel from user who own this channel
	 * 
	 * @param username
	 * @param cname
	 */
	public void deleteChannel(String userName, String cName) {
		// 1. delete channel from channel index
		channelIndex.delete(cName);
		// 2. delete channel under corresponding user
		User channelUser = userIndex.get(userName);
		channelUser.removeChannel(cName);
		userIndex.delete(userName);
		userIndex.put(channelUser);
	}

	// **************** web document *****************
	/**
	 * get web document index
	 * 
	 * @return
	 */
	public PrimaryIndex<String, WebDocument> getWebDocuemnts() {
		return webDocIndex;
	}

	/**
	 * get webDoc as a list using entity cursor
	 * 
	 * @return
	 */
	public List<WebDocument> getDocumentList() {
		List<WebDocument> documentList = new ArrayList<WebDocument>();
		EntityCursor<WebDocument> cursor = webDocIndex.entities();

		try {
			Iterator<WebDocument> i = cursor.iterator();
			while (i.hasNext()) {
				documentList.add(i.next());
			}
		} finally {
			cursor.close();
		}
		return documentList;
	}

	/**
	 * add web document to webDocIndex
	 * 
	 * @param webDoc
	 */
	public void addDocument(WebDocument webDoc) {
		// System.out.println("Add Doc " + webDoc.getURL());
		// System.out.println("size is " +
		// webDoc.getDocumentContent().length());
		webDocIndex.put(webDoc);
	}

	/**
	 * get web document given url
	 * 
	 * @param url
	 * @return
	 */
	public WebDocument getDocument(String url) {
		return webDocIndex.get(url);
	}

	/**
	 * delete web document given url
	 * 
	 * @param url
	 */
	public void deleteDocument(String url) {
		webDocIndex.delete(url);
	}
}
