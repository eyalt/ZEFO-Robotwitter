/**
 * 
 */
package com.Robotwitter.twitter;

/**
 * @author Itay
 *
 */
public interface ITwitterAttacher
{	
	/**
	 * @param account
	 * @return
	 */
	String getAuthorizationURL(TwitterAccount account);
	
	/**
	 * @param account
	 * @param pin
	 * @throws IllegalPinException
	 */
	void attachAccount(String userEmail,TwitterAccount account, String pin) throws IllegalPinException;
}
