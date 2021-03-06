package com.robotwitter.database.interfaces;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.robotwitter.database.interfaces.returnValues.SqlError;
import com.robotwitter.database.primitives.DBFollower;

/**
 * @author Amir and Shmulik
 */

public interface IDatabaseFollowers {

	/**
	 * Delete the following connection between two users
	 * 
	 * @param followedId
	 *            The followed user in the deleted connection
	 * @param followerId
	 *            The follower user in the deleted connection
	 * @return Return the status code. It could be either SUCCESS,
	 *         DOES_NOT_EXIST, INVALID_PARAMS
	 */
	public SqlError deleteFollow(long followedId, long followerId);

	/**
	 * Delete the information of a follower
	 * 
	 * @param followerId
	 *            The follower to delete
	 * @return Return the status code. It could be either SUCCESS,
	 *         DOES_NOT_EXIST, INVALID_PARAMS
	 */
	public SqlError deleteFollower(long followerId);

	/**
	 * Delete all the follower links that follow this user.
	 * 
	 * @param followedId
	 *            The followed user in the deleted connection
	 * @return Return the status code. It could be either SUCCESS,
	 *         INVALID_PARAMS
	 */
	public SqlError deleteUserFollowersLinks(long followedId);

	/**
	 * @param userId 
	 * 			the user whose followers we want
	 * @param path 
	 * 			the path of the file we want to write the data to
	 * @return Returns the status code.
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public SqlError exportToFile(long userId, String path) throws FileNotFoundException, UnsupportedEncodingException;

	/**
	 * @param followerId
	 *            The id of the follower to get
	 * @return The follower associated with this Id
	 */
	public DBFollower get(long followerId);

	/**
	 * @param name
	 *            The actual name of the followers you want to get
	 * @return The followers associated with this specific name
	 */
	public ArrayList<DBFollower> getByName(String name);

	/**
	 * @param screenName
	 *            The screen name of the followers you want to get
	 * @return The followers associated with this specific screen name
	 */
	public ArrayList<DBFollower> getByScreenName(String screenName);
	
	/**
	 * @param userId
	 *            The id of the user you want to get it's followers ids
	 * @return The DBFollower type of the followers of this user
	 */
	public ArrayList<DBFollower> getFollowers(long userId);
	
	/**
	 * @param userId
	 *            The id of the user you want to get it's followers ids
	 * @return The ids of the followers of this user
	 */
	public ArrayList<Long> getFollowersId(long userId);

	/**
	 * @param follower
	 *            The follower you want to insert into the database
	 * @return whether the insert was successful. It could be either SUCCESS,
	 *         ALREADY_EXIST, INVALID_PARAMS
	 */
	public SqlError insert(DBFollower follower);

	/**
	 * @param userId
	 *            The id of the user being followed
	 * @param followerId
	 *            The id of the follower
	 * @return whether the insert was successful. It could be either SUCCESS,
	 *         ALREADY_EXIST, INVALID_PARAMS
	 */
	public SqlError insert(long userId, long followerId);

	/**
	 * @param followerId
	 *            The id of the user to check
	 * @return whether a follower with this id exists
	 */
	public boolean isExists(long followerId);

	/**
	 * @param followedId
	 *            The id of the followed in the connection
	 * @param followerId
	 *            The id of the follower in the connection
	 * @return whether a follower with this id exists
	 */
	public boolean isExists(long followedId, long followerId);

	/**
	 * @param name
	 *            the actual name of the followers to check
	 * @return whether a follower with this name exists
	 */
	public boolean isExistsByName(String name);
	
	/**
	 * @param ScreenName
	 *            The screen name of the follower to check
	 * @return whether a follower with this screen name exists
	 */
	public boolean isExistsByScreenName(String ScreenName);
	
	/**
	 * @param follower
	 *            The follower to update
	 * @return Returns the status code. It could be either SUCCESS,
	 *         DOES_NOT_EXIST, INVALID_PARAMS
	 */
	public SqlError update(DBFollower follower);
}
