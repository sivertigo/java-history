/*
 * @(#)TextComponent.java	1.53 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.TextComponentPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import sun.awt.SunToolkit;


/**
 * The <code>TextComponent</code> class is the superclass of 
 * any component that allows the editing of some text. 
 * <p>
 * A text component embodies a string of text.  The 
 * <code>TextComponent</code> class defines a set of methods 
 * that determine whether or not this text is editable. If the
 * component is editable, it defines another set of methods
 * that supports a text insertion caret. 
 * <p>
 * In addition, the class defines methods that are used 
 * to maintain a current <em>selection</em> from the text. 
 * The text selection, a substring of the component's text, 
 * is the target of editing operations. It is also referred
 * to as the <em>selected text</em>.
 *
 * @version	1.53, 11/29/01
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public class TextComponent extends Component {

    /**
     * The value of the text.
     * A null value is the same as "".
     *
     * @serial
     * @see setText()
     * @see getText()
     */
    String text;

    /**
     * A boolean indicating whether or not this TextComponent is editable.
     * It will be <code>true</code> if the text component
     * is editable and <code>false</code> if not.
     *
     * @serial
     * @see isEditable()
     */
    boolean editable = true;

    /**
     * The selection refers to the selected text, and the selectionStart
     * is the start position of the selected text.
     *
     * @serial
     * @see getSelectionStart()
     * @see setSelectionStart()
     */
    int selectionStart;

    /**
     * The selection refers to the selected text, and the selectionEnd
     * is the end position of the selected text.
     *
     * @serial
     * @see getSelectionEnd()
     * @see setSelectionEnd()
     */
    int selectionEnd;

    /**
     * true if this TextComponent has access to the System clipboard
     */
    transient private boolean canAccessClipboard;

    transient protected TextListener textListener;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -2214773872412987419L;

    /**
     * Constructs a new text component initialized with the 
     * specified text. Sets the value of the cursor to 
     * <code>Cursor.TEXT_CURSOR</code>.
     * @param      text       the text to be displayed. If
     *             <code>text</code> is <code>null</code>, the empty
     *             string <code>""</code> will be displayed.
     * @see        java.awt.Cursor
     */
    TextComponent(String text) {
	this.text = (text != null) ? text : "";
	setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	checkSystemClipboardAccess();
    }

    private void enableInputMethodsIfNecessary() {
	if (checkForEnableIM) {
            checkForEnableIM = false;
	    try {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                enableInputMethods(((SunToolkit) toolkit).enableInputMethodsForTextComponent());
            } catch (Exception e) {
                // if something bad happens, just don't enable input methods
	    }
        }
    }

    public void enableInputMethods(boolean enable) {
        checkForEnableIM = false;
        super.enableInputMethods(enable);
    }

    boolean areInputMethodsEnabled() {
        // moved from the constructor above to here and addNotify below, 
        // this call will initialize the toolkit if not already initialized.
        if (checkForEnableIM) {
            enableInputMethodsIfNecessary(); 
        }

        // TextComponent handles key events without touching the eventMask or
        // having a key listener, so just check whether the flag is set
        return (eventMask & AWTEvent.INPUT_METHODS_ENABLED_MASK) != 0;
    }

    /**
     * Makes this Component displayable by connecting it to a
     * native screen resource.
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see       java.awt.TextComponent#removeNotify
     */  
    public void addNotify() {
        super.addNotify();
        enableInputMethodsIfNecessary();
    }

    /**
     * Removes the TextComponent's peer.  The peer allows us to modify
     * the appearance of the TextComponent without changing its
     * functionality.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    TextComponentPeer peer = (TextComponentPeer)this.peer;
	    if (peer != null) {
	        text = peer.getText();
		selectionStart = peer.getSelectionStart();
		selectionEnd = peer.getSelectionEnd();
	    }
	    super.removeNotify();
	}
    }

    /**
     * Sets the text that is presented by this 
     * text component to be the specified text. 
     * @param       t   the new text.
     *                  If this parameter is <code>null</code> then
     *                  the text is set to the empty string "".
     * @see         java.awt.TextComponent#getText  
     */
    public synchronized void setText(String t) {
	text = (t != null) ? t : "";
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setText(text);
	}
    }

    /**
     * Gets the text that is presented by this text component.
     * @see     java.awt.TextComponent#setText
     */
    public synchronized String getText() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	}
	return text;
    }

    /**
     * Gets the selected text from the text that is
     * presented by this text component.  
     * @return      the selected text of this text component.
     * @see         java.awt.TextComponent#select
     */
    public synchronized String getSelectedText() {
	return getText().substring(getSelectionStart(), getSelectionEnd());
    }

    /**
     * Indicates whether or not this text component is editable.
     * @return     <code>true</code> if this text component is
     *                  editable; <code>false</code> otherwise.
     * @see        java.awt.TextComponent#setEditable
     * @since      JDK1ble
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Sets the flag that determines whether or not this
     * text component is editable.
     * <p>
     * If the flag is set to <code>true</code>, this text component 
     * becomes user editable. If the flag is set to <code>false</code>, 
     * the user cannot change the text of this text component. 
     * @param     b   a flag indicating whether this text component 
     *                      is user editable.
     * @see       java.awt.TextComponent#isEditable
     */
    public synchronized void setEditable(boolean b) {
	editable = b;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setEditable(b);
	}
    }

    /**
     * Gets the start position of the selected text in 
     * this text component. 
     * @return      the start position of the selected text. 
     * @see         java.awt.TextComponent#setSelectionStart
     * @see         java.awt.TextComponent#getSelectionEnd
     */
    public synchronized int getSelectionStart() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionStart = peer.getSelectionStart();
	}
	return selectionStart;
    }

    /**
     * Sets the selection start for this text component to  
     * the specified position. The new start point is constrained 
     * to be at or before the current selection end. It also
     * cannot be set to less than zero, the beginning of the 
     * component's text.
     * If the caller supplies a value for <code>selectionStart</code>
     * that is out of bounds, the method enforces these constraints
     * silently, and without failure.
     * @param       selectionStart   the start position of the 
     *                        selected text.
     * @see         java.awt.TextComponent#getSelectionStart
     * @see         java.awt.TextComponent#setSelectionEnd
     * @since       JDK1.1
     */
    public synchronized void setSelectionStart(int selectionStart) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(selectionStart, getSelectionEnd());
    }

    /**
     * Gets the end position of the selected text in 
     * this text component. 
     * @return      the end position of the selected text. 
     * @see         java.awt.TextComponent#setSelectionEnd
     * @see         java.awt.TextComponent#getSelectionStart
     */
    public synchronized int getSelectionEnd() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionEnd = peer.getSelectionEnd();
	}
	return selectionEnd;
    }

    /**
     * Sets the selection end for this text component to  
     * the specified position. The new end point is constrained 
     * to be at or after the current selection start. It also
     * cannot be set beyond the end of the component's text.
     * If the caller supplies a value for <code>selectionEnd</code>
     * that is out of bounds, the method enforces these constraints
     * silently, and without failure.
     * @param       selectionEnd   the end position of the 
     *                        selected text.
     * @see         java.awt.TextComponent#getSelectionEnd
     * @see         java.awt.TextComponent#setSelectionStart
     * @since       JDK1.1
     */
    public synchronized void setSelectionEnd(int selectionEnd) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(getSelectionStart(), selectionEnd);
    }
    
    /**
     * Selects the text between the specified start and end positions.
     * <p>
     * This method sets the start and end positions of the 
     * selected text, enforcing the restriction that the start position 
     * must be greater than or equal to zero.  The end position must be 
     * greater than or equal to the start position, and less than or 
     * equal to the length of the text component's text.  The 
     * character positions are indexed starting with zero.  
     * The length of the selection is endPosition-startPosition, so the 
     * character at endPosition is not selected.  
     * If the start and end positions of the selected text are equal,  
     * all text is deselected.  
     * <p> 
     * If the caller supplies values that are inconsistent or out of 
     * bounds, the method enforces these constraints silently, and 
     * without failure. Specifically, if the start position or end 
     * position is greater than the length of the text, it is reset to 
     * equal the text length. If the start position is less than zero, 
     * it is reset to zero, and if the end position is less than the 
     * start position, it is reset to the start position.
     * 
     * @param        selectionStart the zero-based index of the first 
                       character to be selected.  
     * @param        selectionEnd the zero-based end position of the 
                       text to be selected. The character at 
                       selectionEnd is not selected. 
     * @see          java.awt.TextComponent#setSelectionStart
     * @see          java.awt.TextComponent#setSelectionEnd
     * @see          java.awt.TextComponent#selectAll
     */
    public synchronized void select(int selectionStart, int selectionEnd) {
	String text = getText();
	if (selectionStart < 0) {
	    selectionStart = 0;
	}
	if (selectionStart > text.length()) {
	    selectionStart = text.length();
	}
	if (selectionEnd > text.length()) {
	    selectionEnd = text.length();
	}
	if (selectionEnd < selectionStart) {
	    selectionEnd = selectionStart;
	}

	this.selectionStart = selectionStart;
	this.selectionEnd = selectionEnd;

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selectionStart, selectionEnd);
	}
    }

    /**
     * Selects all the text in this text component.
     * @see        java.awt.TextComponent@select
     */
    public synchronized void selectAll() {
	String text = getText();
	this.selectionStart = 0;
	this.selectionEnd = getText().length();

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selectionStart, selectionEnd);
	}
    }

    /**
     * Sets the position of the text insertion caret for 
     * this text component.
     * 
     * @param        position the position of the text insertion caret.
     * @exception    IllegalArgumentException if the value supplied
     *                   for <code>position</code> is less than zero.
     * @since        JDK1.1
     */
    public synchronized void setCaretPosition(int position) {
	if (position < 0) {
	    throw new IllegalArgumentException("position less than zero.");
	}

	int maxposition = getText().length();
	if (position > maxposition) {
	    position = maxposition;
	}

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setCaretPosition(position);
	} else {
	    throw new IllegalComponentStateException("Cannot set caret position until after the peer has been created");
	}
    }

    /**
     * Gets the position of the text insertion caret for 
     * this text component.
     * @return       the position of the text insertion caret.
     * @since        JDK1.1
     */
    public synchronized int getCaretPosition() {
        TextComponentPeer peer = (TextComponentPeer)this.peer;
	int position = 0;

	if (peer != null) {
	    position = peer.getCaretPosition();
	} 
	return position;
    }

    /**
     * Adds the specified text event listener to receive text events 
     * from this text component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param l the text event listener
     */ 
    public synchronized void addTextListener(TextListener l) {
	if (l == null) {
	    return;
	}
	textListener = AWTEventMulticaster.add(textListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified text event listener so that it no longer
     * receives text events from this text component
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param         	l     the text listener.
     * @see           	java.awt.event.TextListener
     * @see           	java.awt.Button#addTextListener
     * @since         	JDK1.1
     */
    public synchronized void removeTextListener(TextListener l) {
	if (l == null) {
	    return;
	}
	textListener = AWTEventMulticaster.remove(textListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == TextEvent.TEXT_VALUE_CHANGED) {
            if ((eventMask & AWTEvent.TEXT_EVENT_MASK) != 0 ||
                textListener != null) {
                return true;
            } 
            return false;
        }
        return super.eventEnabled(e);
    }     

    /**
     * Processes events on this text component. If the event is a
     * TextEvent, it invokes the processTextEvent method,
     * else it invokes its superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof TextEvent) {
            processTextEvent((TextEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes text events occurring on this text component by
     * dispatching them to any registered TextListener objects.
     * NOTE: This method will not be called unless text events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A TextListener object is registered via addTextListener()
     * b) Text events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the text event
     */ 
    protected void processTextEvent(TextEvent e) {
        if (textListener != null) {
            int id = e.getID();
	    switch (id) {
	    case TextEvent.TEXT_VALUE_CHANGED:
		textListener.textValueChanged(e);
		break;
	    }
        }
    }

    /**
     * Returns the parameter string representing the state of this text 
     * component. This string is useful for debugging. 
     * @return      the parameter string of this text component.
     */
    protected String paramString() {
	String str = super.paramString() + ",text=" + getText();
	if (editable) {
	    str += ",editable";
	}
	return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
    }

    /**
     * Assigns a valid value to the canAccessClipboard instance variable.
     */
    private void checkSystemClipboardAccess() {
        canAccessClipboard = true;
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    try {
	        sm.checkSystemClipboardAccess();
	    }
	    catch (SecurityException e) {
	        canAccessClipboard = false;
	    }
	}
    }

    /*
     * Serialization support.
     */
    /**
     * The textComponent SerializedDataVersion.
     *
     * @serial
     */
    private int textComponentSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable TextListener(s) as optional data.
     * The non-serializable TextListener(s) are detected and
     * no attempt is made to serialize them.
     *
     * @serialData Null terminated sequence of zero or more pairs.
     *             A pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             textListenerK indicating and TextListener object.
     *
     * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component.textListenerK
     */
    private void writeObject(java.io.ObjectOutputStream s)
      throws IOException 
    {
        // Serialization support.  Since the value of the fields
        // selectionStart, selectionEnd, and text aren't necessarily
        // up to date, we sync them up with the peer before serializing.
        TextComponentPeer peer = (TextComponentPeer)this.peer;
        if (peer != null) {
            text = peer.getText();
            selectionStart = peer.getSelectionStart();
            selectionEnd = peer.getSelectionEnd();
        }

        s.defaultWriteObject();

        AWTEventMulticaster.save(s, textListenerK, textListener);
        s.writeObject(null);
    }

    /**
     * Read the ObjectInputStream, and if it isn't null, 
     * add a listener to receive text events fired by the 
     * TextComponent.  Unrecognized keys or values will be 
     * ignored.
     * 
     * @see removeTextListener()
     * @see addTextListener()
     */
    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException 
    {
        s.defaultReadObject();

        // Make sure the state we just read in for text, 
        // selectionStart and selectionEnd has legal values
	this.text = (text != null) ? text : "";
        select(selectionStart, selectionEnd);

        Object keyOrNull;
        while(null != (keyOrNull = s.readObject())) {
	    String key = ((String)keyOrNull).intern();

	    if (textListenerK == key) {
	        addTextListener((TextListener)(s.readObject()));
            } else { 
                // skip value for unrecognized key
	        s.readObject();
            }
        }
	enableInputMethodsIfNecessary();
	checkSystemClipboardAccess();
    }

    private boolean checkForEnableIM = true;
}
