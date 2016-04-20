package edu.upenn.cis455.storage;
import java.util.ArrayList;
import java.util.List;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
/**
 * User entity in database
 * @author weisong
 */
@Entity
public class User
{
	private String password;
	private List<String> channelNames = new ArrayList<String>();
	@PrimaryKey
	private String userName;

	/**
	 * Create a new user
	 */
	public User()
	{
	}

	/**
	 * Create a new user
	 * @param userName - the name of the new user
	 */
	public User(String userName)
	{
		this.userName = userName;
	}

	/**
	 * set user name
	 * @param userName - the name of the user
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * get user name
	 * @return Returns the username
	 */
	public String getUserName()
	{
		return this.userName;
	}

	/**
	 * set password
	 * @param password - the new password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * get password
	 * @return - Returns the user's password
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * set channel names
	 * @param channelNames - the names of the user's channels
	 */
	public void setChannelNames(List<String> channelNames)
	{
		this.channelNames = channelNames;
	}

	/**
	 * get channel names
	 * @return Returns this user's channels
	 */
	public List<String> getChannelNames()
	{
		return this.channelNames;
	}

	/**
	 * Add a new channel
	 * @param channelName - the name of the new Channel
	 */
	public void addChannelName(String channelName)
	{
		channelNames.add(channelName);
	}

	/**
	 * Delete the channel
	 * @param name - the name of the channel
	 */
	public void removeChannel(String name)
	{
		if (this.channelNames.contains(name))
		{
			this.channelNames.remove(name);
		}
	}

}
