package com.robotwitter.database.interfaces;

import java.util.ArrayList;

import com.robotwitter.database.interfaces.returnValues.SqlError;
import com.robotwitter.database.primitives.DBFollower;

public interface IDatabaseFollowers {

	/**
	 * @param name
	 *            The actual name of the followers you want to get
	 * @return The followers associated with this specific name
	 */
	public ArrayList<DBFollower> getByName(String name);

	/**
	 * @param screenName
	 *            The screen name of the follower you want to get
	 * @return The follower associated with this specific screen name
	 */
	public DBFollower getByScreenName(String screenName);

	/**
	 * @param follower
	 *            The follower you want to insert into the database
	 * @return whether the insert was successful. It could be either SUCCESS,
	 *         DOES_NOT_EXIST, INVALID_PARAMS
	 */
	public SqlError insert(DBFollower follower);

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