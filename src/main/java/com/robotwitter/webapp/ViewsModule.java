
package com.robotwitter.webapp;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import com.robotwitter.webapp.messages.IMessagesContainer;
import com.robotwitter.webapp.messages.IMessagesProvider;




/**
 * A module for resolving all view dependencies using Guice.
 *
 * @author Itay Khazon
 */
public class ViewsModule extends AbstractModule
{
	
	/**
	 * Instantiates a new views module.
	 *
	 * @param views
	 *            a mapping of all accessible views
	 * @param messagesProvider
	 *            the provider of messages containers for the views
	 */
	public ViewsModule(ViewMap views, IMessagesProvider messagesProvider)
	{
		this.views = views;
		this.messagesProvider = messagesProvider;
	}


	/**
	 * Binds an instance of {@link IMessagesContainer} to instances of a given
	 * {@link com.vaadin.navigator.View} given their name.
	 * <p>
	 * The given view class must contain a named dependency for the messsages
	 * container with the given name. The messages provider should also be able
	 * to provide a messages container of the same name.
	 *
	 * @param name
	 *            the view's name
	 */
	private void bindMessagesContainer(String name)
	{
		bind(IMessagesContainer.class)
			.annotatedWith(Names.named(name))
			.toInstance(messagesProvider.get(name));
	}
	
	
	@Override
	protected final void configure()
	{
		// Bind message containers
		views.keySet().forEach(name -> bindMessagesContainer(name));
	}
	
	
	
	/** A mapping of all accessible views. */
	private final ViewMap views;
	
	/** Provides messages containers for the views. */
	IMessagesProvider messagesProvider;
	
}
