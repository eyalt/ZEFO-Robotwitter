/**
 * 
 */

package com.robotwitter.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;


/**
 * @author Itay
 * 
 *         Implements the retrieveFollowersAmount using 2 application only calls
 *         to the API
 */
public class NaiveTwitterFollowerRetriever
	implements
		ITwitterFollowersRetriever
{
	private Twitter twitterConnector;
	
	
	
	/**
	 * @param tf
	 *            a TwitterFactory configured for the robotwitter app to create
	 *            Twitter instances
	 */
	public NaiveTwitterFollowerRetriever(TwitterFactory tf)
	{
		this.twitterConnector = tf.getInstance();
	}
	
	
	/* (non-Javadoc) @see
	 * com.robotwitter.twitter.ITwitterFollowersRetriever#retrieveFollowersAmount
	 * (com.robotwitter.twitter.TwitterAccount) */
	@Override
	public int retrieveFollowersAmount(long userID)
	{
		try
		{
			User twitterUser = this.twitterConnector.showUser(userID);
			return twitterUser.getFollowersCount();
		} catch (TwitterException e)
		{
			// User doesn't exist, or network issues.
			return -1;
		}
		
	}
	
}
