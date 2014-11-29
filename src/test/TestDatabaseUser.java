/**
 * 
 */

package test;


import org.junit.Test;

import Database.ConnectionEstablisher;
import Database.IDatabase;
import Database.MySQLConEstablisher;
import Database.MySqlDatabaseUser;
import DatabasePrimitives.DBUser;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;




/**
 * @author Shmulik
 *
 */
public class TestDatabaseUser
{
	@SuppressWarnings("javadoc")
	private class MySQLDBUserModule extends AbstractModule
	{
		
		/**
		 * 
		 */
		public MySQLDBUserModule()
		{}
		
		
		/* (non-Javadoc) @see com.google.inject.AbstractModule#configure() */
		@SuppressWarnings("nls")
		@Override
		protected void configure()
		{
			bind(String.class)
				.annotatedWith(Names.named("DB Server"))
				.toInstance("localhost");
			bind(String.class)
				.annotatedWith(Names.named("DB Schema"))
				.toInstance("yearlyproj_db");
			
			bind(ConnectionEstablisher.class).to(MySQLConEstablisher.class);
		}
		
	}
	
	
	
	/**
	 * Testing the user database table features
	 */
	@SuppressWarnings("nls")
	@Test
	public void test()
	{
		try
		{
			Injector injector = Guice.createInjector(new MySQLDBUserModule());
			IDatabase db = injector.getInstance(MySqlDatabaseUser.class);
			db.insert(new DBUser("shmulik", "shulikjkech@gmail.com", "sh"));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
