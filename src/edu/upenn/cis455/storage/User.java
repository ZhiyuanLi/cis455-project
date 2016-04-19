package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * User entity in database
 * 
 * @author weisong
 *
 */
@Entity
public class User {

	private String password;
	private List<String> channelNames = new ArrayList<String>();

	@PrimaryKey
	private String userName;

	public User() {
	}

	public User(String userName) {
		this.userName = userName;
	}

	// *********** basic *************

	/**
	 * set user name
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * get user name
	 * 
	 * @return
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * set password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * get password
	 * 
	 * @return
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * set channel names
	 * 
	 * @param channelNames
	 */
	public void setChannelNames(List<String> channelNames) {
		this.channelNames = channelNames;
	}

	/**
	 * get channel names
	 * 
	 * @return
	 */
	public List<String> getChannelNames() {
		return this.channelNames;
	}

	// ************ advanced ***************

	public void addChannelName(String channelName) {
		channelNames.add(channelName);
	}

	public void removeChannel(String name) {
		if (this.channelNames.contains(name)) {
			this.channelNames.remove(name);
		}
	}

}
