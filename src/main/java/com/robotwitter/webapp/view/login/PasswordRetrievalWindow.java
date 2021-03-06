
package com.robotwitter.webapp.view.login;


import com.vaadin.server.FontAwesome;

import com.robotwitter.webapp.control.login.IPasswordRetrievalController;
import com.robotwitter.webapp.messages.IMessagesContainer;
import com.robotwitter.webapp.util.IFormComponent;
import com.robotwitter.webapp.util.WindowWithDescription;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;




/**
 * Represents a window for password retrieval.
 *
 * @author Hagai Akibayov
 */
public class PasswordRetrievalWindow extends WindowWithDescription
{

	/**
	 * Initialises a new password retrieval window.
	 *
	 * @param messages
	 *            the container of messages to display
	 * @param controller
	 *            the password retrieval controller
	 */
	public PasswordRetrievalWindow(
		IMessagesContainer messages,
		IPasswordRetrievalController controller)
	{
		this.messages = messages;

		// Set window properties
		setCaption(messages.get("PasswordRetrievalWindow.caption")); 
		setDescription(messages.get("PasswordRetrievalWindow.instructions")); 
		setIcon(FontAwesome.ENVELOPE);
		
		// Initialise the form
		PasswordRetrievalForm retrievalForm =
			new PasswordRetrievalForm(
				messages,
				controller,
				this::handleSuccessfulRetrieval);
		setContent(retrievalForm);
		
		// Set CSS styles
		retrievalForm.addStyleName(FORM_STYLENAME);
		addStyleName(STYLENAME);
	}


	/**
	 * Handles a successful attempt at a password retrieval.
	 *
	 * @param form
	 *            the password retrieval form
	 */
	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
	private void handleSuccessfulRetrieval(IFormComponent form)
	{
		// Create description
		String desc = messages.get("PasswordRetrievalWindow.notify.sent-to"); 
		desc += " <b>" + form.get(PasswordRetrievalForm.EMAIL) + "</b>. ";  
		desc += messages.get("PasswordRetrievalWindow.notify.check-spam"); 
		
		// Set the window's content
		setCaption(messages.get("PasswordRetrievalWindow.notify.caption")); 
		setDescription(desc);
		setIcon(FontAwesome.CHECK_CIRCLE);
		removeContent();
		
		// Clicking anywhere will close the window
		addClickListener(event -> close());
	}
	
	
	
	/** The CSS class name to apply to the password retrieval form. */
	private static final String FORM_STYLENAME = "PasswordRetrievalWindow-form"; 

	/** Serialisation version unique ID. */
	private static final long serialVersionUID = 1L;
	
	/** The CSS class name to apply to this component. */
	private static final String STYLENAME = "PasswordRetrievalWindow"; 

	/** The messages displayed by this view. */
	protected IMessagesContainer messages;

}
