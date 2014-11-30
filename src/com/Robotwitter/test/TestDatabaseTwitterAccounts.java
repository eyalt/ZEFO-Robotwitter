/**
 * 
 */

package com.Robotwitter.test;


import static org.junit.Assert.*;

import org.junit.Test;

import com.Robotwitter.Database.IDatabase;
import com.Robotwitter.Database.MySQLDBUserModule;
import com.Robotwitter.Database.MySqlDatabaseTwitterAcounts;
import com.Robotwitter.Database.MySqlDatabaseUser;
import com.Robotwitter.DatabasePrimitives.DBTwitterAccount;
import com.google.inject.Guice;
import com.google.inject.Injector;




/**
 * @author Shmulik
 *
 */
public class TestDatabaseTwitterAccounts
{
	
	@SuppressWarnings({ "nls", "boxing" })
	@Test
	public void test()
	{
		try
		{
			Injector injector = Guice.createInjector(new MySQLDBUserModule());
			IDatabase db =
				injector.getInstance(MySqlDatabaseTwitterAcounts.class);
			DBTwitterAccount shmulikAccount =
				new DBTwitterAccount(
					"Shmulik@gmail.com",
					"tokenblalblala",
					"tokenSecret",
					(long) 123456789);
			if (!db.isExists(shmulikAccount))
			{
				db.insert(shmulikAccount);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}