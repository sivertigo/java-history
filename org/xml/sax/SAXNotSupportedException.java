// SAXNotSupportedException.java - unsupported feature or value.
// Written by David Megginson, sax@megginson.com
// NO WARRANTY!  This class is in the Public Domain.

// $Id: SAXNotSupportedException.java,v 1.1.2.1 2001/11/15 19:46:10 edwingo Exp $


package org.xml.sax;

/**
 * Exception class for an unsupported operation.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>An XMLReader will throw this exception when it recognizes a
 * feature or property identifier, but cannot perform the requested
 * operation (setting a state or value).  Other SAX2 applications and
 * extensions may use this class for similar purposes.</p>
 *
 * @since SAX 2.0
 * @author David Megginson, 
 *         <a href="mailto:sax@megginson.com">sax@megginson.com</a>
 * @version 2.0r2pre w/ JAXP 1.1 signatures
 * @see org.xml.sax.SAXNotRecognizedException 
 */
public class SAXNotSupportedException extends SAXException
{

//     /**
//      * Construct a new exception with no message.
//      */
//     public SAXNotSupportedException ()
//     {
// 	super();
//     }


    /**
     * Construct a new exception with the given message.
     *
     * @param message The text message of the exception.
     */
    public SAXNotSupportedException (String message)
    {
	super(message);
    }

}

// end of SAXNotSupportedException.java
