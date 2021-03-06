/**
 *
 */

package com.robotwitter.twitter;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.robotwitter.database.interfaces.IDatabaseFollowers;
import com.robotwitter.database.interfaces.IDatabaseNumFollowers;
import com.robotwitter.database.interfaces.IDatabaseTwitterAccounts;
import com.robotwitter.database.primitives.DBFollowersNumber;
import com.robotwitter.database.primitives.DBTwitterAccount;




/**
 * @author Itay
 *
 *         a class handling the backfilling of the followers id database,
 *         meaning that once per day, it will wake up, and retrieve the
 *         followers id's of the user, and update the database accordingly.
 */
public class FollowerIdsBackfiller implements IUserBackfiller
{
	/**
	 * @param followersDB
	 * @param accountsDB
	 * @param factory
	 */
	@Inject
	public FollowerIdsBackfiller(
		IDatabaseFollowers followersDB,
		IDatabaseNumFollowers followersNumDB,
		IDatabaseTwitterAccounts accountsDB,
		@Named("User based factory") TwitterFactory factory)
	{
		this.accountsDB = accountsDB;
		this.followersDB = followersDB;
		this.followersNumDB = followersNumDB;
		twitterConnector = factory.getInstance();

		// Uninitialised
		userAccount = null;
		yesterdaysFirstPage = null;
		yesterdayFollowerNumber = -1;
		workTimer = null;

	}


	/* (non-Javadoc) @see
	 * com.robotwitter.twitter.UserBackfiller#setUser(java.lang.Long) */
	@Override
	public void setUser(Long userID)
	{
		userAccount = accountsDB.get(userID);
		if (userAccount == null) { throw new RuntimeException(
			"Tried to track a user that doesn't exist!"); }
		twitterConnector.setOAuthAccessToken(new AccessToken(userAccount
			.getToken(), userAccount.getPrivateToken()));

		yesterdaysFirstPage = getFirstFollowersPage();
		try
		{
			yesterdayFollowerNumber =
				twitterConnector
					.showUser(userAccount.getUserId())
					.getFollowersCount();
		} catch (final TwitterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (workTimer != null)
		{
			workTimer.cancel();
			workTimer = null;
		}
	}


	/* (non-Javadoc) @see com.robotwitter.twitter.UserBackfiller#start() */
	@Override
	public void start()
	{
		workTimer = new Timer(true);
		workTimer.scheduleAtFixedRate(new TimerTask()
		{

			@Override
			public void run()
			{
				doDailyTask();

			}
		}, new Date(), MILLIS_IN_SECOND * FULL_DAY);
	}


	/* (non-Javadoc) @see com.robotwitter.twitter.UserBackfiller#stop() */
	@Override
	public void stop()
	{
		workTimer.cancel();
	}


	/**
	 * @param followerNumberEntry
	 */
	private void addToFollowerNumberDatabase(
		DBFollowersNumber followerNumberEntry)
	{
		followersNumDB.insert(followerNumberEntry);
	}


	/**
	 * @param todaysFollowerNumber
	 * @param newFollowersNumber
	 * @param newUnfollowerNumber
	 * @return
	 */
	private DBFollowersNumber buildFollowerNumberEntry(
		int todaysFollowerNumber,
		int newFollowersNumber,
		int newUnfollowerNumber)
	{
		return new DBFollowersNumber(
			userAccount.getUserId(),
			new Timestamp(new Date().getTime()),
			todaysFollowerNumber,
			newFollowersNumber,
			newUnfollowerNumber);
	}


	/**
	 * @param yesterdaysFirstPage
	 * @param todaysFirstPage
	 * @return
	 */
	private int calcNewFollowersNumber(
		IDs yesterdaysFirstPage,
		IDs todaysFirstPage)
	{
		final long[] yesterdaysIDs = yesterdaysFirstPage.getIDs();
		final long[] todaysIDs = todaysFirstPage.getIDs();
		
		if (todaysIDs.length < HIGHEST_LIMIT) { return todaysIDs.length
			- yesterdaysIDs.length; }

		for (int i = 0; i < Math.min(yesterdaysIDs.length, GIVE_UP_LIMIT); i++)
		{
			for (int j = 0; j < todaysIDs.length; j++)
			{
				if (yesterdaysIDs[i] == todaysIDs[j]) { return i - j; }
			}
		}
		return HIGHEST_LIMIT;
	}


	/**
	 *
	 */
	private void clearYesterdaysFollowersDatabase()
	{
		followersDB.deleteUserFollowersLinks(userAccount.getUserId());
	}


	/**
	 *
	 */
	private void doDailyTask()
	{
		IDs firstPage = getFirstFollowersPage();
		while (firstPage == null)
		{
			sleepFor(REPAIR_TIME);
			firstPage = getFirstFollowersPage();
		}

		// Use todays number of followers, yesterdays number of followers,
		// and the first page from today and yesterday to calculate the
		// number of followers gained and lost.
		final int todaysFollowerNumber =
			saveTodaysFollowersToDatabase(firstPage);
		final int newFollowersNumber =
			calcNewFollowersNumber(yesterdaysFirstPage, firstPage);
		final int newUnfollowerNumber =
			newFollowersNumber
				- (todaysFollowerNumber - yesterdayFollowerNumber);
		updateFollowerNumberDatabase(
			todaysFollowerNumber,
			Math.min(newFollowersNumber, 0),
			Math.min(newUnfollowerNumber, 0));

		updateYesterdaysVariables(firstPage, todaysFollowerNumber);
	}


	/**
	 * @return
	 */
	private IDs getFirstFollowersPage()
	{
		IDs $;
		try
		{
			$ = twitterConnector.getFollowersIDs(-1);
		} catch (final TwitterException e)
		{
			e.printStackTrace();
			return null;
		}
		return $;
	}


	/**
	 * @param followersPage
	 * @return
	 */
	private IDs getNextFollowersPage(IDs followersPage)
	{
		try
		{
			followersPage =
				twitterConnector.getFollowersIDs(followersPage.getNextCursor());
		} catch (final TwitterException e)
		{
			e.printStackTrace();
			sleepFor(REPAIR_TIME);
		}
		return followersPage;
	}


	/**
	 * @param firstPage
	 * @return the total number of followers backfilled
	 */
	private int saveTodaysFollowersToDatabase(IDs firstPage)
	{
		int $ = firstPage.getIDs().length;
		IDs nextPage = firstPage;
		clearYesterdaysFollowersDatabase();

		do
		{
			updatePageToDatabase(nextPage);
			if (nextPage.getRateLimitStatus().getRemaining() == 0)
			{
				// sleep until we can do more API calls.
				sleepFor(nextPage.getRateLimitStatus().getResetTimeInSeconds() + 1);
			}
			nextPage = getNextFollowersPage(nextPage);
			$ += nextPage.getIDs().length;
		} while (nextPage.getNextCursor() != 0
			&& nextPage.getRateLimitStatus().getRemaining() > 0);
		return $;
	}


	/**
	 * @param timeInSeconds
	 */
	private void sleepFor(long timeInSeconds)
	{
		try
		{
			Thread.sleep(1000 * timeInSeconds);
		} catch (final InterruptedException e)
		{
			// Its ok, we are just waking up!
		}

	}


	/**
	 * @param todaysFollowerNumber
	 * @param newFollowersNumber
	 * @param newUnfollowerNumber
	 */
	private void updateFollowerNumberDatabase(
		int todaysFollowerNumber,
		int newFollowersNumber,
		int newUnfollowerNumber)
	{
		final DBFollowersNumber followerNumberEntry =
			buildFollowerNumberEntry(
				todaysFollowerNumber,
				newFollowersNumber,
				newUnfollowerNumber);
		addToFollowerNumberDatabase(followerNumberEntry);
	}


	/**
	 * @param followersPage
	 */
	private void updatePageToDatabase(IDs followersPage)
	{
		for (final Long followerID : followersPage.getIDs())
		{
			followersDB.insert(userAccount.getUserId(), followerID);
		}
	}


	/**
	 * @param firstPage
	 * @param todaysFollowerNumber
	 */
	private void updateYesterdaysVariables(
		IDs firstPage,
		int todaysFollowerNumber)
	{
		yesterdaysFirstPage = firstPage;
		yesterdayFollowerNumber = todaysFollowerNumber;

	}



	private static final int HIGHEST_LIMIT = 5000;

	private static final int GIVE_UP_LIMIT = 100;

	private static final long MILLIS_IN_SECOND = 1000;

	private static final long FULL_DAY = 60 * 60 * 24;

	private static final long REPAIR_TIME = 60 * 15;

	private IDs yesterdaysFirstPage;

	private int yesterdayFollowerNumber;

	private DBTwitterAccount userAccount;

	private Timer workTimer;

	private final Twitter twitterConnector;

	private final IDatabaseNumFollowers followersNumDB;

	private final IDatabaseFollowers followersDB;

	private final IDatabaseTwitterAccounts accountsDB;
}
