/*
 * @(#)JTextComponent.java	1.133 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.awt.im.InputMethodRequests;
import java.awt.font.TextHitInfo;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



/**
 * JTextComponent is the base class for swing text components.  It
 * tries to be compatible with the java.awt.TextComponent class
 * where it can reasonably do so.  Also provided are other services
 * for additional flexibility (beyond the pluggable UI and bean
 * support).
 * <dl>
 * <dt><b><font size=+1>Caret Changes</font></b>
 * <dd>
 * The caret is a pluggable object in swing text components.
 * Notification of changes to the caret position and the selection
 * are sent to implementations of the CaretListener interface that
 * have been registered with the text component.  The UI will
 * install a default caret unless a customized caret has been 
 * set.
 * <dt><b><font size=+1>Commands</font></b>
 * <dd>
 * <p>
 * Text components provide a number of commands that can be used
 * to manipulate the component.  This is essentially the way that
 * the component expresses its capabilities.  These are expressed
 * in terms of the swing Action interface, using the TextAction
 * implementation.  The set of commands supported by the text
 * component can be found with the 
 * {@link #getActions} method.  These actions
 * can be bound to key events, fired from buttons, etc.
 *
 * <dt><b><font size=+1>Text Input</font></b>
 * <dd>
 * <p>
 * The text components support flexible and internationalized text input, using 
 * keymaps and the input method framework, while maintaining compatibility with 
 * the AWT listener model.
 * <p>
 * A {@link javax.swing.text.Keymap} lets an application bind key strokes to actions. 
 * In order to allow keymaps to be shared across multiple text components, they 
 * can use actions that extend TextAction. TextAction can determine which 
 * JTextComponent most recently has or had focus and therefore is the subject of 
 * the action (In the case that the ActionEvent sent to the action doesn't contain 
 * the target text component as its source). 
 * <p>
 * The <a href="../../../../guide/intl/spec.html">input method framework</a> lets 
 * text components interact with input methods, separate software components that 
 * preprocess events to let users enter thousands of different characters using 
 * keyboards with far fewer keys. JTextComponent is an <em>active client</em> of 
 * the framework, so it implements the preferred user interface for interacting 
 * with input methods. As a consequence, some key events do not reach the text 
 * component because they are handled by an input method, and some text input 
 * reaches the text component as committed text within an {@link 
 * java.awt.event.InputMethodEvent} instead of as a key event. The complete text 
 * input is the combination of the characters in keyTyped key events and committed 
 * text in input method events.
 * <p>
 * The AWT listener model lets applications attach event listeners to components 
 * in order to bind events to actions. Swing encourages the use of keymaps instead 
 * of listeners, but maintains compatibility with listeners by giving the 
 * listeners a chance to steal an event by consuming it.
 * <p>
 * Keyboard event and input method events are handled in the following stages, 
 * with each stage capable of consuming the event:
 * <table border=1>
 * <tr><td>Stage<td>KeyEvent      <td>InputMethodEvent
 * <tr><td>1.   <td>input methods <td>(generated here)
 * <tr><td>2.   <td>focus manager <td>
 * <tr><td>3.   <td>registered key listeners<td>registered input method listeners
 * <tr><td>4.   <td>              <td>input method handling in JTextComponent
 * <tr><td>5.   <td colspan=2>keymap handling using the current keymap
 * <tr><td>6.   <td>keyboard handling in JComponent (e.g. accelerators, component navigation, etc.)<td>
 * </table>
 * <p>
 * To maintain compatibility with applications that listen to key events but are 
 * not aware of input method events, the input method handling in stage 4 provides 
 * a compatibility mode for components that do not process input method events. 
 * For these components, the committed text is converted to keyTyped key events 
 * and processed in the key event pipeline starting at stage 3 instead of in the 
 * input method event pipeline.
 * <p>
 * By default the component will create a keymap (named <b>DEFAULT_KEYMAP</b>) 
 * that is shared by all JTextComponent instances as the default keymap.  
 * Typically a look-and-feel implementation will install a different keymap
 * that resolves to the default keymap for those bindings not found in the 
 * different keymap. The minimal bindings include:
 * <ul>
 * <li>inserting content into the editor for the
 *  printable keys.
 * <li>removing content with the backspace and del
 *  keys.
 * <li>caret movement forward and backward
 * </ul>
 *
 * <dt><b><font size=+1>Model/View Split</font></b>
 * <dd>
 * <p>
 * The text components have a model-view split.  A text component pulls 
 * together the objects used to represent the model, view, and controller. 
 * The text document model may be shared by other views which act as observers 
 * of the model (e.g. a document may be shared by multiple components).
 *
 * <p align=center><img src="doc-files/editor.gif" HEIGHT=358 WIDTH=587></p>
 *
 * <p>
 * The model is defined by the {@link Document} interface.
 * This is intended to provide a flexible text storage mechanism
 * that tracks change during edits and can be extended to more sophisticated
 * models.  The model interfaces are meant to capture the capabilities of
 * expression given by SGML, a system used to express a wide variety of
 * content.
 * Each modification to the document causes notification of the
 * details of the change to be sent to all observers in the form of a 
 * {@link DocumentEvent} which allows the views to stay up to date with the model.
 * This event is sent to observers that have implemented the 
 * {@link DocumentListener}
 * interface and registered interest with the model being observed.
 *
 * <dt><b><font size=+1>Location Information</font></b>
 * <dd>
 * The capability of determining the location of text in
 * the view is provided.  There are two methods, {@link #modelToView}
 * and {@link #viewToModel} for determining this information.
 * <dt><b><font size=+1>Undo/Redo support</font></b>
 * <dd>
 * Support for an edit history mechanism is provided to allow
 * undo/redo operations.  The text component does not itself
 * provide the history buffer by default, but does provide
 * the UndoableEdit records that can be used in conjunction
 * with a history buffer to provide the undo/redo support.
 * The support is provided by the Document model, which allows
 * one to attach UndoableEditListener implementations.
 *
 * <dt><b><font size=+1>Thread Safety</font></b>
 * <dd>
 * The swing text components provide some support of thread
 * safe operations.  Because of the high level of configurability
 * of the text components, it is possible to circumvent the
 * protection provided.  The protection primarily comes from
 * the model, so the documentation of AbstractDocument
 * describes the assumptions of the protection provided.
 * The methods that are safe to call asynchronously are marked
 * with comments.
 * </dl>
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *     attribute: isContainer false
 * 
 * @author  Timothy Prinzing
 * @version 1.133 11/29/01
 * @see Document
 * @see DocumentEvent
 * @see DocumentListener
 * @see Caret
 * @see CaretEvent
 * @see CaretListener
 * @see TextUI
 * @see View
 * @see ViewFactory

















































































































































































































































































































/* */
public abstract class JTextComponent extends JComponent implements Scrollable, Accessible
{
    /**
     * Creates a new JTextComponent.
     * Listeners for caret events are established, and the pluggable
     * UI installed.  The component is marked as editable.  No layout manager
     * is used, because layout is managed by the view subsystem of text.
     * The document model is set to null.
     */
    public JTextComponent() {
        super();
	
	// enable InputMethodEvent for on-the-spot pre-editing
	enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.INPUT_METHOD_EVENT_MASK);
	needToSendKeyTypedEvent = !isProcessInputMethodEventOverridden();
	






        caretEvent = new MutableCaretEvent(this);
        addMouseListener(caretEvent);
        addFocusListener(caretEvent);
        setEditable(true);
        setLayout(null); // layout is managed by View hierarchy
        updateUI();
    }

    /**
     * Fetches the user-interface factory for this text-oriented editor.
     *
     * @return the factory
     */
    public TextUI getUI() { return (TextUI)ui; }

    /**
     * Sets the user-interface factory for this text-oriented editor
     *
     * @param ui the factory
     */
    public void setUI(TextUI ui) {
        super.setUI(ui);
    }

    /**
     * Reloads the pluggable UI.  The key used to fetch the
     * new interface is <b>getUIClassID()</b>.  The type of
     * the UI is <b>TextUI</b>.  invalidate() is called after
     * setting the UI.
     */
    public void updateUI() {
        setUI((TextUI)UIManager.getUI(this));
        invalidate();
    }


    /**
     * Returns true if this component is completely opaque.  
     * This is used in painting backgrounds.
     * 
     * @return true if this component is completely opaque.
     */
    public boolean isOpaque() {
        return opaque;
    }

    /**
     * Sets whether or not the UI should render a background.
     *
     * @param o true if should render a background
     */
    public void setOpaque(boolean o) {
        opaque = o;
    }

    /**
     * Adds a caret listener for notification of any changes
     * to the caret.
     *
     * @param listener the listener
     * @see javax.swing.event.CaretEvent
     */
    public void addCaretListener(CaretListener listener) {
        listenerList.add(CaretListener.class, listener);
    }

    /**
     * Removes a caret listener.
     *
     * @param listener the listener
     * @see javax.swing.event.CaretEvent
     */
    public void removeCaretListener(CaretListener listener) {
        listenerList.remove(CaretListener.class, listener);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.  The listener list is processed in a
     * last-to-first manner.
     *
     * @param e the event
     * @see EventListenerList
     */
    protected void fireCaretUpdate(CaretEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CaretListener.class) {
                ((CaretListener)listeners[i+1]).caretUpdate(e);
            }
        }
    }

    /**
     * Associates the editor with a text document.
     * The currently registered factory is used to build a view for
     * the document, which gets displayed by the editor after revalidation.
     * A PropertyChange event ("document") is propagated to each listener.
     *
     * @param doc  the document to display/edit
     * @see #getDocument
     * @beaninfo
     *  description: the text document model
     *        bound: true
     *       expert: true
     */
    public void setDocument(Document doc) {
        if (accessibleContext != null) {
            model.removeDocumentListener(
                ((AccessibleJTextComponent)accessibleContext));
        }
        Document old = model;
        model = doc;
        firePropertyChange("document", old, doc);
        revalidate();
        repaint();
        if (accessibleContext != null) {
            model.addDocumentListener(
                ((AccessibleJTextComponent)accessibleContext));
        }
    }

    /**
     * Fetches the model associated with the editor.  This is
     * primarily for the UI to get at the minimal amount of
     * state required to be a text editor.  Subclasses will
     * return the actual type of the model which will typically
     * be something that extends Document.
     *
     * @return the model
     */
    public Document getDocument() {
        return model;
    }

    /**
     * Fetches the command list for the editor.  This is
     * the list of commands supported by the plugged-in UI
     * augmented by the collection of commands that the
     * editor itself supports.  These are useful for binding
     * to events, such as in a keymap.
     *
     * @return the command list
     */
    public Action[] getActions() {      
        return getUI().getEditorKit(this).getActions(); 
    }

    /**
     * Sets margin space between the text component's border
     * and its text.  The text component's default Border
     * object will use this value to create the proper margin.
     * However, if a non-default border is set on the text component, 
     * it is that Border object's responsibility to create the
     * appropriate margin space (else this property will effectively 
     * be ignored).  This causes a redraw of the component.
     * A PropertyChange event ("margin") is sent to all listeners.
     *
     * @param m the space between the border and the text
     * @beaninfo
     *  description: desired space between the border and text area
     *        bound: true
     */
    public void setMargin(Insets m) {
        Insets old = margin;
        margin = m;
        firePropertyChange("margin", old, m);
        invalidate();
    }

    /**
     * Returns the margin between the text component's border and
     * its text.  
     *
     * @return the margin
     */
    public Insets getMargin() {
        return margin;
    }

    /**
     * Fetches the caret that allows text-oriented navigation over
     * the view.  
     *
     * @return the caret
     */
    public Caret getCaret() {
        return caret;
    }

    /**
     * Sets the caret to be used.  By default this will be set
     * by the UI that gets installed.  This can be changed to
     * a custom caret if desired.  Setting the caret results in a
     * PropertyChange event ("caret") being fired.
     *
     * @param c the caret
     * @see #getCaret
     * @beaninfo
     *  description: the caret used to select/navigate
     *        bound: true
     *       expert: true
     */
    public void setCaret(Caret c) {
        if (caret != null) {
            caret.removeChangeListener(caretEvent);
            caret.deinstall(this);
        }
        Caret old = caret;
        caret = c;
        if (caret != null) {
            caret.install(this);
            caret.addChangeListener(caretEvent);
        }
        firePropertyChange("caret", old, caret);
    }

    /**
     * Fetches the object responsible for making highlights.
     *
     * @return the highlighter
     */
    public Highlighter getHighlighter() {
        return highlighter;
    }

    /**
     * Sets the highlighter to be used.  By default this will be set
     * by the UI that gets installed.  This can be changed to
     * a custom highlighter if desired.  The highlighter can be set to
     * null to disable it.  A PropertyChange event ("highlighter") is fired
     * when a new highlighter is installed.
     *
     * @param h the highlighter
     * @see #getHighlighter
     * @beaninfo
     *  description: object responsible for background highlights
     *        bound: true
     *       expert: true
     */
    public void setHighlighter(Highlighter h) {
        if (highlighter != null) {
            highlighter.deinstall(this);
        }
        Highlighter old = highlighter;
        highlighter = h;
        if (highlighter != null) {
            highlighter.install(this);
        }
        firePropertyChange("highlighter", old, h);
    }

    /**
     * Sets the keymap to use for binding events to
     * actions.  Setting to null effectively disables keyboard input.
     * A PropertyChange event ("keymap") is fired when a new keymap
     * is installed.
     *
     * @param map the keymap
     * @see #getKeymap
     * @beaninfo
     *  description: set of key event to action bindings to use
     *        bound: true
     */
    public void setKeymap(Keymap map) {
        Keymap old = keymap;
        keymap = map;
        firePropertyChange("keymap", old, keymap);
    }

    /**
     * Fetches the keymap currently active in this text
     * component.
     *
     * @return the keymap
     */
    public Keymap getKeymap() {
        return keymap;
    }

    /**
     * Adds a new keymap into the keymap hierarchy.  Keymap bindings
     * resolve from bottom up so an attribute specified in a child
     * will override an attribute specified in the parent.
     *
     * @param nm   the name of the keymap (must be unique within the
     *   collection of named keymaps in the document).  The name may 
     *   be null if the keymap is unnamed, but the caller is responsible
     *   for managing the reference returned as an unnamed keymap can't
     *   be fetched by name.  
     * @param parent the parent keymap.  This may be null if unspecified
     *   bindings need not be resolved in some other keymap.
     * @return the keymap
     */
    public static Keymap addKeymap(String nm, Keymap parent) {
        Keymap map = new DefaultKeymap(nm, parent); 
        if (nm != null) {
            // add a named keymap, a class of bindings
            keymapTable.put(nm, map);
        }
        return map;
    }

    /**
     * Removes a named keymap previously added to the document.  Keymaps
     * with null names may not be removed in this way.
     *
     * @param nm  the name of the keymap to remove
     * @return the keymap that was removed
     */
    public static Keymap removeKeymap(String nm) {
        return (Keymap) keymapTable.remove(nm);
    }

    /**
     * Fetches a named keymap previously added to the document.
     * This does not work with null-named keymaps.
     *
     * @param nm  the name of the keymap
     * @return the keymap
     */
    public static Keymap getKeymap(String nm) {
        return (Keymap) keymapTable.get(nm);
    }

    /**
     * Binding record for creating key bindings.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class KeyBinding {
        
        /**
         * The key.
         */
        public KeyStroke key;

        /**
         * The name of the action for the key.
         */
        public String actionName;

        /**
         * Creates a new key binding.
         *
         * @param key the key
         * @param actionName the name of the action for the key
         */
        public KeyBinding(KeyStroke key, String actionName) {
            this.key = key;
            this.actionName = actionName;
        }
    }

    /**
     * <p>
     * Loads a keymap with a bunch of 
     * bindings.  This can be used to take a static table of
     * definitions and load them into some keymap.  The following
     * example illustrates an example of binding some keys to
     * the cut, copy, and paste actions associated with a 
     * JTextComponent.  A code fragment to accomplish
     * this might look as follows:
     * <pre><code>
     *
     *   static final JTextComponent.KeyBinding[] defaultBindings = {
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.copyAction),
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.pasteAction),
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.cutAction),
     *   };
     *
     *   JTextComponent c = new JTextPane();
     *   Keymap k = c.getKeymap();
     *   JTextComponent.loadKeymap(k, defaultBindings, c.getActions());
     * 
     * </code></pre>
     * The sets of bindings and actions may be empty but must be non-null.
     *
     * @param map the keymap
     * @param bindings the bindings
     * @param actions the set of actions
     */
    public static void loadKeymap(Keymap map, KeyBinding[] bindings, Action[] actions) {
        Hashtable h = new Hashtable();
        for (int i = 0; i < actions.length; i++) {
            Action a = actions[i];
            String value = (String)a.getValue(Action.NAME);
            h.put((value!=null ? value:""), a);
        }
        for (int i = 0; i < bindings.length; i++) {
            Action a = (Action) h.get(bindings[i].actionName);
            if (a != null) {
                map.addActionForKeyStroke(bindings[i].key, a);
            }
        }
    }

    /**
     * Maps an event to an action if one is defined in the 
     * installed keymap, and perform the action.  If the action is 
     * performed, the event is consumed.
     *
     * @returns true if an action was performed, false otherwise.
     */
    private final boolean mapEventToAction(KeyEvent e) {
        Keymap binding = getKeymap();
        if (binding != null) {
            KeyStroke k = KeyStroke.getKeyStrokeForEvent(e);
            Action a = binding.getAction(k);
            if (a != null) {
                String command = null;
                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                    command = String.valueOf(e.getKeyChar());
                }
                ActionEvent ae =  new ActionEvent(this, 
                                                  ActionEvent.ACTION_PERFORMED, 
                                                  command, e.getModifiers());
                a.actionPerformed(ae);
                e.consume();
                return true;
            }
        }
        return false;
    }

    /**
     * Fetches the current color used to render the 
     * caret.
     *
     * @return the color
     */
    public Color getCaretColor() {
        return caretColor;
    }

    /**
     * Sets the current color used to render the
     * caret.  Setting to null effectively restores the default color.
     * Setting the color results in a PropertyChange event ("caretColor")
     * being fired.
     *
     * @param c the color
     * @see #getCaretColor
     * @beaninfo
     *  description: the color used to render the caret
     *        bound: true
     *    preferred: true
     */
    public void setCaretColor(Color c) {
        Color old = caretColor;
        caretColor = c;
        firePropertyChange("caretColor", old, caretColor);
    }

    /**
     * Fetches the current color used to render the 
     * selection.
     *
     * @return the color
     */
    public Color getSelectionColor() {
        return selectionColor;
    }

    /**
     * Sets the current color used to render the
     * selection.  Setting the color to null is the same as setting
     * Color.white.  Setting the color results in a PropertyChange event
     * ("selectionColor").
     *
     * @param c the color
     * @see #getSelectionColor
     * @beaninfo
     *  description: color used to render selection background
     *        bound: true
     *    preferred: true
     */
    public void setSelectionColor(Color c) {
        Color old = selectionColor;
        selectionColor = c;
        firePropertyChange("selectionColor", old, selectionColor);
    }

    /**
     * Fetches the current color used to render the 
     * selected text.
     *
     * @return the color
     */
    public Color getSelectedTextColor() {
        return selectedTextColor;
    }

    /**
     * Sets the current color used to render the
     * selected text.  Setting the color to null is the same as Color.black.
     * Setting the color results in a PropertyChange event
     * ("selectedTextColor") being fired.
     *
     * @param c the color
     * @see #getSelectedTextColor
     * @beaninfo
     *  description: color used to render selected text
     *        bound: true
     *    preferred: true
     */
    public void setSelectedTextColor(Color c) {
        Color old = selectedTextColor;
        selectedTextColor = c;
        firePropertyChange("selectedTextColor", old, selectedTextColor);
    }

    /**
     * Fetches the current color used to render the 
     * selected text.
     *
     * @return the color
     */
    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    /**
     * Sets the current color used to render the
     * disabled text.  Setting the color fires off a
     * PropertyChange event ("disabledTextColor").
     *
     * @param c the color
     * @see #getDisabledTextColor
     * @beaninfo
     *  description: color used to render disabled text
     *        bound: true
     *    preferred: true
     */
    public void setDisabledTextColor(Color c) {
        Color old = disabledTextColor;
        disabledTextColor = c;
        firePropertyChange("disabledTextColor", old, disabledTextColor);
    }

    /**
     * Replaces the currently selected content with new content
     * represented by the given string.  If there is no selection
     * this amounts to an insert of the given text.  If there
     * is no replacement text this amounts to a removal of the
     * current selection.  
     * <p>
     * This is the method that is used by the default implementation
     * of the action for inserting content that gets bound to the
     * keymap actions.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param content  the content to replace the selection with
     */
    public void replaceSelection(String content) {
        Document doc = getDocument();
        if (doc != null) {
            try {
                int p0 = Math.min(caret.getDot(), caret.getMark());
                int p1 = Math.max(caret.getDot(), caret.getMark());
                if (p0 != p1) {
                    doc.remove(p0, p1 - p0);
                }
                if (content != null && content.length() > 0) {
                    doc.insertString(p0, content, null);
                }
            } catch (BadLocationException e) {
                getToolkit().beep();
            }
        }
    }

    /**
     * Fetches a portion of the text represented by the
     * component.  Returns an empty string if length is 0.
     *
     * @param offs the offset >= 0
     * @param len the length >= 0
     * @return the text
     * @exception BadLocationException if the offset or length are invalid
     */
    public String getText(int offs, int len) throws BadLocationException {
        return getDocument().getText(offs, len);
    }

    /**
     * Converts the given location in the model to a place in
     * the view coordinate system.
     *
     * @param pos the position >= 0
     * @return the coordinates as a rectangle, with (r.x, r.y) as the location
     *   in the coordinate system
     * @exception BadLocationException if the given position does not 
     *   represent a valid location in the associated document
     * @see TextUI#modelToView
     */
    public Rectangle modelToView(int pos) throws BadLocationException {
        return getUI().modelToView(this, pos);
    }

    /**
     * Converts the given place in the view coordinate system
     * to the nearest representative location in the model.
     *
     * @param pt the location in the view to translate
     * @return the offset >= 0 from the start of the document
     * @see TextUI#viewToModel
     */
    public int viewToModel(Point pt) {
        return getUI().viewToModel(this, pt);
    }

    /**
     * Transfers the currently selected range in the associated
     * text model to the system clipboard, removing the contents
     * from the model.  The current selection is reset.  Does nothing
     * for null selections.
     */
    public void cut() {
	if (isEditable() && isEnabled()) {
	    try {
		Clipboard clipboard = getToolkit().getSystemClipboard();
		int p0 = Math.min(caret.getDot(), caret.getMark());
		int p1 = Math.max(caret.getDot(), caret.getMark());
		if (p0 != p1) {
		    Document doc = getDocument();
		    String srcData = doc.getText(p0, p1 - p0);
		    StringSelection contents = new StringSelection(srcData);
		    clipboard.setContents(contents, defaultClipboardOwner);
		    doc.remove(p0, p1 - p0);
		}
	    } catch (BadLocationException e) {
	    }
	} else {
	    getToolkit().beep();
	}
    }

    /**
     * Transfers the currently selected range in the associated
     * text model to the system clipboard, leaving the contents
     * in the text model.  The current selection is remains intact.
     * Does nothing for null selections.
     */
    public void copy() {
        try {
            Clipboard clipboard = getToolkit().getSystemClipboard();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            if (p0 != p1) {
                Document doc = getDocument();
                String srcData = doc.getText(p0, p1 - p0);
                StringSelection contents = new StringSelection(srcData);
                clipboard.setContents(contents, defaultClipboardOwner);
            }
        } catch (BadLocationException e) {
        }
    }
    
    /**
     * Transfers the contents of the system clipboard into the
     * associated text model.  If there is a selection in the
     * associated view, it is replaced with the contents of the
     * clipboard.  If there is no selection, the clipboard contents
     * are inserted in front of the current insert position in 
     * the associated view.  If the clipboard is empty, does nothing.
     * @see #replaceSelection
     */ 
    public void paste() {
	Clipboard clipboard = getToolkit().getSystemClipboard();
	Transferable content = clipboard.getContents(this);
	if (isEditable() && isEnabled() && (content != null)) {
	    try {
		String dstData = (String)(content.getTransferData(DataFlavor.stringFlavor));
		replaceSelection(dstData);
	    } catch (Exception e) {
		getToolkit().beep();
	    }
	} else {
	    getToolkit().beep();
	}

    }

    /**
     * Moves the caret to a new position, leaving behind a
     * mark defined by the last time setCaretPosition was
     * called.  This forms a selection.
     *
     * @param pos the position
     * @see #setCaretPosition
     */
    public void moveCaretPosition(int pos) {
        caret.moveDot(pos);
    }

    /**
     * The bound property name for the focus accelerator.
     */ 
    public static final String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";

    /**
     * Sets the key accelerator that will cause the receiving text
     * component to get the focus.  The accelerator will be the 
     * key combination of the <em>alt</em> key and the character
     * given (converted to upper case).  By default, there is no focus
     * accelerator key.  Any previous key accelerator setting will be
     * superseded.  A '\0' key setting will be registered, and has the
     * effect of turning off the focus accelerator.  When the new key
     * is set, a PropertyChange event (FOCUS_ACCELERATOR_KEY) will be fired.
     *
     * @param aKey the key
     * @see #getFocusAccelerator
     * @beaninfo
     *  description: accelerator character used to grab focus
     *        bound: true
     */
    public void setFocusAccelerator(char aKey) {
        aKey = Character.toUpperCase(aKey);
        KeyStroke[] keyStrokes = getRegisteredKeyStrokes();
        int i,c;
        for(i=0,c=keyStrokes.length;i<c;i++) {
            if(getActionForKeyStroke(keyStrokes[i]) == focusAction) {
                if(keyStrokes[i].getKeyChar() == aKey)
                    return;
                else
                    unregisterKeyboardAction(keyStrokes[i]);
                break;
            }
        }
        if(aKey != '\0') {
            registerKeyboardAction(focusAction,KeyStroke.getKeyStroke(aKey,ActionEvent.ALT_MASK),
                                   JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
        char old = focusAccelerator;
        focusAccelerator = aKey;
        firePropertyChange(FOCUS_ACCELERATOR_KEY, old, focusAccelerator);
    }

    /**
     * Returns the key accelerator that will cause the receiving
     * text component to get the focus.  Return '\0' if no focus
     * accelerator has been set.
     *
     * @return the key
     */
    public char getFocusAccelerator() {
        return focusAccelerator;
    }

    /**
     * Initializes from a stream.  This creates a
     * model of the type appropriate for the component
     * and initializes the model from the stream.
     * By default this will load the model as plain
     * text.  Previous contents of the model are discarded.
     *
     * @param in The stream to read from
     * @param desc An object describing the stream.  This
     *   might be a string, a File, a URL, etc.  Some kinds
     *   of documents (such as html for example) might be
     *   able to make use of this information.  If non-null, it is
     *   added as a property of the document.
     * @exception IOException as thrown by the stream being
     *  used to initialize.
     * @see EditorKit#createDefaultDocument
     * @see #setDocument
     * @see PlainDocument
     */
    public void read(Reader in, Object desc) throws IOException {
        EditorKit kit = getUI().getEditorKit(this);
        Document doc = kit.createDefaultDocument();
        if (desc != null) {
            doc.putProperty(Document.StreamDescriptionProperty, desc);
        }
        try {
            kit.read(in, doc, 0);
            setDocument(doc);
        } catch (BadLocationException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Stores the contents of the model into the given
     * stream.  By default this will store the model as plain
     * text.
     *
     * @param out the output stream
     * @exception IOException on any I/O error
     */
    public void write(Writer out) throws IOException {
        Document doc = getDocument();
        try {
            getUI().getEditorKit(this).write(out, doc, 0, doc.getLength());
        } catch (BadLocationException e) {
            throw new IOException(e.getMessage());
        }
    }

    // --- java.awt.Component methods ----------------------------

    /** 
     * Notifies this component that it has been removed from its
     * container.  This is used to remove the reference to this 
     * component as the last focused component, if such a reference
     * exists (i.e. to support TextAction).
     */
    public void removeNotify() {
	super.removeNotify();
	if (focusedComponent == this) {
	    focusedComponent = null;
	}
    }

    /**
     * Enables or disables this component, depending on the value of the 
     * parameter <code>b</code>. An enabled component can respond to user 
     * input and generate events. Components are enabled initially by default.
     * A repaint() is done after setting the new state.
     *
     * @param     b   If <code>true</code>, this component is 
     *            enabled; otherwise this component is disabled.
     * @see #isEnabled
     * @since JDK1.1
     */
    public void setEnabled(boolean b) {
	super.setEnabled(b);
	repaint();
    }

    /**
     * Returns true if the focus can be traversed.  This would be false
     * for components like a disabled button.
     *
     * @return true if the focus is traversable
     */
    public boolean isFocusTraversable() {
        return isEnabled();
    }

    /**
     * Processes any key events that the component itself 
     * recognizes.  This will be called after the focus
     * manager and any interested listeners have been
     * given a chance to steal away the event.  This 
     * method will only be called is the event has not
     * yet been consumed.  This method is called prior
     * to the keyboard UI logic.
     * <p>
     * This is implemented to take a default action, typically
     * inserting the character into the document as content.  Subclasses
     * would normally override this method if they process some
     * key events themselves.  If the event is processed,
     * it should be consumed.
     *
     * @param e the event
     */
    protected void processComponentKeyEvent(KeyEvent e) {
        int id = e.getID();
        switch(id) {
        case KeyEvent.KEY_TYPED:
            if (mapEventToAction(e) == false) {
                // default behavior is to input translated
                // characters as content if the character
                // hasn't been mapped in the keymap.
                Keymap binding = getKeymap();
                    if (binding != null) {
                        Action a = binding.getDefaultAction();
                        if (a != null) {
                            ActionEvent ae = new ActionEvent(this, 
                                                             ActionEvent.ACTION_PERFORMED, 
                                                             String.valueOf(e.getKeyChar()),
                                                             e.getModifiers());
                            a.actionPerformed(ae);
                            e.consume();
                        }
                    }
            }
            break;
        case KeyEvent.KEY_PRESSED:
            mapEventToAction(e);
            break;
        case KeyEvent.KEY_RELEASED:
            mapEventToAction(e);
            break;
        }
    } 

    // --- java.awt.TextComponent methods ------------------------

    /**
     * Sets the position of the text insertion caret for the TextComponent.
     * Note that the caret tracks change, so this may move if the underlying
     * text of the component is changed.  If the document is null, does
     * nothing.
     *
     * @param position the position
     * @beaninfo
     * description: the caret position
     */
    public void setCaretPosition(int position) {
        Document doc = getDocument();
        if (doc != null) {
	    if (position > doc.getLength() || position < 0) {
		throw new IllegalArgumentException("bad position: " + position);
	    }
            caret.setDot(position);
        }
    }

    /**
     * Returns the position of the text insertion caret for the 
     * text component.
     *
     * @return the position of the text insertion caret for the
     *  text component >= 0
     */
    public int getCaretPosition() {
        return caret.getDot();
    }

    /**
     * Sets the text of this TextComponent to the specified text.  If the
     * text is null or empty, has the effect of simply deleting the old text.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param t the new text to be set
     * @see #getText
     * @beaninfo
     * description: the text of this component
     */
    public void setText(String t) {
        try {
            Document doc = getDocument();
            doc.remove(0, doc.getLength());
            doc.insertString(0, t, null);
        } catch (BadLocationException e) {
            getToolkit().beep();
        }
    }

    /**
     * Returns the text contained in this TextComponent.  If the underlying
     * document is null, will give a NullPointerException.
     *
     * @return the text
     * @see #setText
     */
    public String getText() {
        Document doc = getDocument();
        String txt;
        try {
            txt = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            txt = null;
        }
        return txt;
    }

    /**
     * Returns the selected text contained in this TextComponent.  If
     * the selection is null or the document empty, returns null.
     *
     * @return the text
     * @exception IllegalArgumentException if the selection doesn't
     *  have a valid mapping into the document for some reason
     * @see #setText
     */
    public String getSelectedText() {
        String txt = null;
        int p0 = Math.min(caret.getDot(), caret.getMark());
        int p1 = Math.max(caret.getDot(), caret.getMark());
        if (p0 != p1) {
            try {
                Document doc = getDocument();
                txt = doc.getText(p0, p1 - p0);
            } catch (BadLocationException e) {
		throw new IllegalArgumentException(e.getMessage());
            }
        }
        return txt;
    }

    /**
     * Returns the boolean indicating whether this TextComponent is
     * editable or not.
     *
     * @return the boolean value
     * @see #setEditable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the specified boolean to indicate whether or not this
     * TextComponent should be editable.  A PropertyChange event ("editable")
     * is fired when the state is changed.
     *
     * @param b the boolean to be set
     * @see #isEditable
     * @beaninfo
     * description: specifies if the text can be edited
     */
    public void setEditable(boolean b) {
	if (b != editable) {
	    boolean oldVal = editable;
	    editable = b;
	    firePropertyChange("editable", new Boolean(oldVal), new Boolean(editable));
	    repaint();
	}
    }

    /**
     * Returns the selected text's start position.  Return 0 for an
     * empty document, or the value of dot if no selection.
     *
     * @return the start position >= 0
     */
    public int getSelectionStart() {
        int start = Math.min(caret.getDot(), caret.getMark());
        return start;
    }

    /**
     * Sets the selection start to the specified position.  The new
     * starting point is constrained to be before or at the current
     * selection end.
     * <p>
     * This is available for backward compatiblitity to code 
     * that called this method on java.awt.TextComponent.  This is
     * implemented to forward to the Caret implementation which
     * is where the actual selection is maintained.
     *
     * @param selectionStart the start position of the text >= 0
     * @beaninfo
     * description: starting location of the selection.
     */
    public void setSelectionStart(int selectionStart) {
        /* Route through select method to enforce consistent policy
         * between selectionStart and selectionEnd.
         */
        select(selectionStart, getSelectionEnd());
    }

    /**
     * Returns the selected text's end position.  Return 0 if the document
     * is empty, or the value of dot if there is no selection.
     *
     * @return the end position >= 0
     */
    public int getSelectionEnd() {
        int end = Math.max(caret.getDot(), caret.getMark());
        return end;
    }

    /**
     * Sets the selection end to the specified position.  The new
     * end point is constrained to be at or after the current
     * selection start.
     * <p>
     * This is available for backward compatiblitity to code 
     * that called this method on java.awt.TextComponent.  This is
     * implemented to forward to the Caret implementation which
     * is where the actual selection is maintained.
     *
     * @param selectionEnd the end position of the text >= 0
     * @beaninfo
     * description: ending location of the selection.
     */
    public void setSelectionEnd(int selectionEnd) {
        /* Route through select method to enforce consistent policy
         * between selectionStart and selectionEnd.
         */
        select(getSelectionStart(), selectionEnd);
    }
    
    /**
     * Returns the selected text's start position.  Return 0 for an
     * empty document, or the value of dot if no selection.
     *
     * @return the start position >= 0
     */
     int getSelectionStart(Position.Bias[] bias) {
        DefaultCaret c = (DefaultCaret)caret;
        if( c.getDot() < c.getMark() ) {
            bias[0] = c.getDotBias();
            return c.getDot();
        } else {
            bias[0] = c.getMarkBias();
            return c.getMark();
        }
    }

    /**
     * Returns the selected text's end position.  Return 0 if the document
     * is empty, or the value of dot if there is no selection.
     *
     * @return the end position >= 0
     */
    int getSelectionEnd(Position.Bias[] bias) {
        DefaultCaret c = (DefaultCaret)caret;
        if( c.getDot() > c.getMark() ) {
            bias[0] = c.getDotBias();
            return c.getDot();
        } else {
            bias[0] = c.getMarkBias();
            return c.getMark();
        }
    }

    /**
     * Selects the text found between the specified start and end 
     * locations.  This call is provided for backward compatibility.
     * It is routed to a call to setCaretPosition
     * followed by a call to moveCaretPostion.  The preferred way
     * to manage selection is by calling those methods directly.
     *
     * @param selectionStart the start position of the text >= 0
     * @param selectionEnd the end position of the text >= 0
     * @see #setCaretPosition
     * @see #moveCaretPosition
     */
    public void select(int selectionStart, int selectionEnd) {
	// argument adjustment done by java.awt.TextComponent
	if (selectionStart < 0) {
	    selectionStart = 0;
	}
	if (selectionEnd > getDocument().getLength()) {
	    selectionEnd = getDocument().getLength();
	}
	if (selectionEnd < selectionStart) {
	    selectionEnd = selectionStart;
	}
	if (selectionStart > selectionEnd) {
	    selectionStart = selectionEnd;
	}

        setCaretPosition(selectionStart);
        moveCaretPosition(selectionEnd);
    }

    /**
     * Selects all the text in the TextComponent.  Does nothing on a null
     * or empty document.
     */
    public void selectAll() {
        Document doc = getDocument();
        if (doc != null) {
            setCaretPosition(0);
            moveCaretPosition(doc.getLength());
        }
    }

    // --- Scrollable methods ---------------------------------------------

    /**
     * Returns the preferred size of the viewport for a view component.
     * This is implemented to do the default behavior of returning
     * the preferred size of the component.
     * 
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }


    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.  Ideally, 
     * components should handle a partially exposed row or column by 
     * returning the distance required to completely expose the item.
     * <p>
     * The default implementation of this is to simply return 10% of
     * the visible area.  Subclasses are likely to be able to provide
     * a much more reasonable value.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or
     *   SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than
     *   zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @exception IllegalArgumentException for an invalid orientation
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch(orientation) {
        case SwingConstants.VERTICAL:
            return visibleRect.height / 10;
        case SwingConstants.HORIZONTAL:
            return visibleRect.width / 10;
        default:
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }


    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one block
     * of rows or columns, depending on the value of orientation. 
     * <p>
     * The default implementation of this is to simply return the visible
     * area.  Subclasses will likely be able to provide a much more 
     * reasonable value.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or
     *   SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero
     *  for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     * @exception IllegalArgumentException for an invalid orientation
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch(orientation) {
        case SwingConstants.VERTICAL:
            return visibleRect.height;
        case SwingConstants.HORIZONTAL:
            return visibleRect.width;
        default:
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }  
    

    /**
     * Return true if a viewport should always force the width of this 
     * Scrollable to match the width of the viewport.  For example a normal 
     * text view that supported line wrapping would return true here, since it
     * would be undesirable for wrapped lines to disappear beyond the right
     * edge of the viewport.  Note that returning true for a Scrollable
     * whose ancestor is a JScrollPane effectively disables horizontal
     * scrolling.
     * <p>
     * Scrolling containers, like JViewport, will use this method each 
     * time they are validated.  
     * 
     * @return true if a viewport should force the Scrollables
     *   width to match its own.
     */
    public boolean getScrollableTracksViewportWidth() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	}
	return false;
    }

    /**
     * Return true if a viewport should always force the height of this 
     * Scrollable to match the height of the viewport.  For example a 
     * columnar text view that flowed text in left to right columns 
     * could effectively disable vertical scrolling by returning
     * true here.
     * <p>
     * Scrolling containers, like JViewport, will use this method each 
     * time they are validated.  
     * 
     * @return true if a viewport should force the Scrollables height
     *   to match its own.
     */
    public boolean getScrollableTracksViewportHeight() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
	}
	return false;
    }

/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this JComponent.
     * A new context is created if necessary.
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTextComponent();
        }
        return accessibleContext;
    }

    /**
     * Accessibility implementation for JTextComponent.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public class AccessibleJTextComponent extends AccessibleJComponent 
    implements AccessibleText, CaretListener, DocumentListener {

        int caretPos;

        /**
         * Constructs an AccessibleJTextComponent.  Adds a listener to track
         * caret change.
         */
        public AccessibleJTextComponent() {
            Document doc = JTextComponent.this.getDocument();
            if (doc != null) {
                doc.addDocumentListener(this);
            }
            JTextComponent.this.addCaretListener(this);
            caretPos = getCaretPosition();
        }

        /**
         * Handles caret updates (fire appropriate property change event,
         * which are AccessibleContext.ACCESSIBLE_CARET_PROPERTY and
         * AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY).
         * This keeps track of the dot position internally.  When the caret
         * moves, the internal position is updated after firing the event.
         *
         * @param e the CaretEvent
         */
        public void caretUpdate(CaretEvent e) {
            int dot = e.getDot();
            int mark = e.getMark();
            if (caretPos != dot) {
                // the caret moved
                firePropertyChange(ACCESSIBLE_CARET_PROPERTY,
                    new Integer(caretPos), new Integer(dot));
                caretPos = dot;
            }
            if (mark != dot) {
                // there is a selection
                firePropertyChange(ACCESSIBLE_SELECTION_PROPERTY, null, 
                    getSelectedText());
            }
        }

        // DocumentListener methods

        /**
         * Handles document insert (fire appropriate property change event
         * which is AccessibleContext.ACCESSIBLE_TEXT_PROPERTY).
         * This tracks the dot via the event.
         *
         * @param e the DocumentEvent
         */
        public void insertUpdate(DocumentEvent e) {
            Caret c = JTextComponent.this.getCaret();
            Integer dot = new Integer(c.getDot());
            firePropertyChange(ACCESSIBLE_TEXT_PROPERTY, null, dot);
        }

        /**
         * Handles document remove (fire appropriate property change event,
         * which is AccessibleContext.ACCESSIBLE_TEXT_PROPERTY).
         * This tracks the dot via the event.
         *
         * @param e the DocumentEvent
         */
        public void removeUpdate(DocumentEvent e) {
            Caret c = JTextComponent.this.getCaret();
            Integer dot = new Integer(c.getDot());
            firePropertyChange(ACCESSIBLE_TEXT_PROPERTY, null, dot);
        }

        /**
         * Handles document remove (fire appropriate property change event,
         * which is AccessibleContext.ACCESSIBLE_TEXT_PROPERTY).
         * This tracks the dot via the event.
         *
         * @param e the DocumentEvent
         */
        public void changedUpdate(DocumentEvent e) {
            Caret c = JTextComponent.this.getCaret();
            Integer dot = new Integer(c.getDot());
            firePropertyChange(ACCESSIBLE_TEXT_PROPERTY, null, dot);
        }

        /**
         * Gets the state set of the JTextComponent.  
         * The AccessibleStateSet of an object is composed of a set of 
         * unique AccessibleState's.  A change in the AccessibleStateSet 
         * of an object will cause a PropertyChangeEvent to be fired
         * for the AccessibleContext.ACCESSIBLE_STATE_PROPERTY property.
         *
         * @return an instance of AccessibleStateSet containing the
         * current state set of the object
         * @see AccessibleStateSet
         * @see AccessibleState
         * @see #addPropertyChangeListener
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (JTextComponent.this.isEditable()) {
                states.add(AccessibleState.EDITABLE);
            }
            return states;
        }


        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object (AccessibleRole.TEXT)
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TEXT;
        }

        /**
         * Gets the AccessibleText interface associated with this object.
         *
         * @return an instance of AccessibleText 
         */
        public AccessibleText getAccessibleText() {
            return this;
        }


        // --- interface AccessibleText methods ------------------------

        /**
         * Many of these methods are just convenience methods; they
         * just call the equivalent on the parent
         */

        /**
         * Given a point in local coordinates, return the zero-based index
         * of the character under that Point.  If the point is invalid,
         * this method returns -1.
         *
         * @param p the Point in local coordinates
         * @return the zero-based index of the character under Point p.
         */
        public int getIndexAtPoint(Point p) {
            if (p == null) {
                return -1;
            }
            return JTextComponent.this.viewToModel(p);
        }

        /**
         * Determines the bounding box of the character at the given
         * index into the string.  The bounds are returned in local
         * coordinates.  If the index is invalid a null rectangle
         * is returned.
         *
         * @param i the index into the String >= 0
         * @return the screen coordinates of the character's bounding box
         */
        public Rectangle getCharacterBounds(int i) {
            if (i < 0 || i > model.getLength()-1) {
                return null;
            }
            Rectangle rect;
            try {
                rect = modelToView(i);
            } catch (BadLocationException e) {
                rect = null;
            }
            return rect;
        }

        /**
         * Returns the number of characters (valid indicies)
         *
         * @return the number of characters >= 0
         */
        public int getCharCount() {
            return model.getLength();
        }

        /**
         * Returns the zero-based offset of the caret.
         *
         * Note: The character to the right of the caret will have the
         * same index value as the offset (the caret is between
         * two characters).
         *
         * @return the zero-based offset of the caret.
         */
        public int getCaretPosition() {
            return JTextComponent.this.getCaretPosition();
        }

        /**
         * Returns the AttributeSet for a given character (at a given index).
         *
         * @param i the zero-based index into the text
         * @return the AttributeSet of the character
         */
        public AttributeSet getCharacterAttribute(int i) {
            Element e = null;
            for (e = model.getDefaultRootElement(); ! e.isLeaf(); ) {
                int index = e.getElementIndex(i);
                e = e.getElement(index);
            }
            return e.getAttributes();
        }

        /**
         * Returns the start offset within the selected text.
         * If there is no selection, but there is
         * a caret, the start and end offsets will be the same.
         * Return 0 if the text is empty, or the caret position
         * if no selection.
         *
         * @return the index into the text of the start of the selection >= 0
         */
        public int getSelectionStart() {
            return JTextComponent.this.getSelectionStart();
        }

        /**
         * Returns the end offset within the selected text.
         * If there is no selection, but there is
         * a caret, the start and end offsets will be the same.
         * Return 0 if the text is empty, or the caret position
         * if no selection.
         *
         * @return the index into teh text of the end of the selection >= 0
         */
        public int getSelectionEnd() {
            return JTextComponent.this.getSelectionEnd();
        }

        /**
         * Returns the portion of the text that is selected.
         *
         * @return the text, null if no selection
         */
        public String getSelectedText() {
            return JTextComponent.this.getSelectedText();
        }

        /**
         * Returns the String at a given index. 
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         * or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence,
         *   null for an invalid index or part
         */
        public String getAtIndex(int part, int index) {
            if (index < 0 || index >= model.getLength()) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
                try {
                    return model.getText(index, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
                    int end = words.following(index);
                    return s.substring(words.previous(), end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator sentence = BreakIterator.getSentenceInstance();
                    sentence.setText(s);
                    int end = sentence.following(index);
                    return s.substring(sentence.previous(), end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }

        /**
         * Returns the String after a given index.
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         * or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence, null for an invalid
         *  index or part
         */
        public String getAfterIndex(int part, int index) {
            if (index < 0 || index >= model.getLength()) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
		if (index+1 >= model.getLength()) {
		   return null;
		}
                try {
                    return model.getText(index+1, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
                    int start = words.following(index);
		    if (start == BreakIterator.DONE || start >= s.length()) {
			return null;
		    }
		    int end = words.following(start);
		    if (end == BreakIterator.DONE || end >= s.length()) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator sentence = BreakIterator.getSentenceInstance();
                    sentence.setText(s);
                    int start = sentence.following(index);
		    if (start == BreakIterator.DONE || start >= s.length()) {
			return null;
		    }
		    int end = sentence.following(start);
		    if (end == BreakIterator.DONE || end >= s.length()) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }


        /**
         * Returns the String before a given index.
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         *   or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence, null for an invalid index
         *  or part
         */
        public String getBeforeIndex(int part, int index) {
            if (index < 0 || index > model.getLength()-1) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
		if (index == 0) {
		    return null;
		}
                try {
                    return model.getText(index-1, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
//                    int end = words.next(index);
                    int end = words.following(index);
                    end = words.previous();
		    int start = words.previous();
		    if (start == BreakIterator.DONE) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = model.getText(0, model.getLength());
                    BreakIterator sentence = BreakIterator.getSentenceInstance();
                    sentence.setText(s);
//                    int end = sentence.next(index);
                    int end = sentence.following(index);
                    end = sentence.previous();
		    int start = sentence.previous();
		    if (start == BreakIterator.DONE) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }

    }


    // --- serialization ---------------------------------------------

    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException 
    {
        s.defaultReadObject();
        caretEvent = new MutableCaretEvent(this);
        addMouseListener(caretEvent);
        addFocusListener(caretEvent);
    }

    // --- member variables ----------------------------------

    /**
     * The document model.
     */
    private Document model;

    /**
     * The caret used to display the insert position
     * and navigate throught the document. 
     *
     * PENDING(prinz)  
     * This should be serializable, default installed
     * by UI.
     */
    private transient Caret caret;

    /**
     * The object responsible for managing highlights.
     *
     * PENDING(prinz)  
     * This should be serializable, default installed
     * by UI.
     */
    private transient Highlighter highlighter;

    /**
     * The current key bindings in effect.
     *
     * PENDING(prinz)  
     * This should be serializable, default installed
     * by UI.
     */
    private transient Keymap keymap;

    /**
     * is the component opaque?
     */
    private boolean opaque;

    private transient MutableCaretEvent caretEvent;
    private Color caretColor;
    private Color selectionColor;
    private Color selectedTextColor;
    private Color disabledTextColor;
    private boolean editable;
    private Insets margin;
    private char focusAccelerator;
    private Action focusAction = new FocusAction();

    private static ClipboardOwner defaultClipboardOwner = new ClipboardObserver();


    /**
     * Returns a string representation of this JTextComponent. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JTextComponent.
     */
    protected String paramString() {
        String opaqueString = (opaque ?
			       "true" : "false");
        String editableString = (editable ?
				 "true" : "false");
        String caretColorString = (caretColor != null ?
				   caretColor.toString() : "");
        String selectionColorString = (selectionColor != null ?
				       selectionColor.toString() : "");
        String selectedTextColorString = (selectedTextColor != null ?
					  selectedTextColor.toString() : "");
        String disabledTextColorString = (disabledTextColor != null ?
					  disabledTextColor.toString() : "");
        String marginString = (margin != null ?
			       margin.toString() : "");

        return super.paramString() +
        ",caretColor=" + caretColorString +
        ",disabledTextColor=" + disabledTextColorString +
        ",editable=" + editableString +
        ",margin=" + marginString +
        ",opaque=" + opaqueString +
        ",selectedTextColor=" + selectedTextColorString +
        ",selectionColor=" + selectionColorString;
    }

    static class ClipboardObserver implements ClipboardOwner {

        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    }

    /**
     * package level access to focused text component
     * so that JTextAction implementations can be 
     * reused across JTextComponent implementations.
     */
    static final JTextComponent getFocusedComponent() {
        return focusedComponent;
    }

    private static Hashtable keymapTable = null;
    private JTextComponent editor;
    private static JTextComponent focusedComponent;
    
    //
    // member variables used for on-the-spot input method 
    // editing style support
    // 
    private InputMethodRequests inputMethodRequestsHandler;
    private AttributedString composedText;
    private String composedTextContent;
    private Position composedTextStart;
    private Position composedTextEnd;
    private ComposedTextCaret composedTextCaret;
    private transient Caret originalCaret;
    private boolean needToSendKeyTypedEvent;
    

    static class DefaultKeymap implements Keymap {

        DefaultKeymap(String nm, Keymap parent) {
            this.nm = nm; 
            this.parent = parent;
            bindings = new Hashtable();
        }

        /**
         * Fetch the default action to fire if a 
         * key is typed (ie a KEY_TYPED KeyEvent is received)
         * and there is no binding for it.  Typically this
         * would be some action that inserts text so that 
         * the keymap doesn't require an action for each 
         * possible key.
         */
        public Action getDefaultAction() {
            if (defaultAction != null) {
                return defaultAction;
            }
            return (parent != null) ? parent.getDefaultAction() : null;
        }

        /**
         * Set the default action to fire if a key is typed.
         */
        public void setDefaultAction(Action a) {
            defaultAction = a;
        }

        public String getName() {
            return nm;
        }

        public Action getAction(KeyStroke key) {
            Action a = (Action) bindings.get(key);
            if ((a == null) && (parent != null)) {
                a = parent.getAction(key);
            }
            return a;
        }

        public KeyStroke[] getBoundKeyStrokes() {
            KeyStroke[] keys = new KeyStroke[bindings.size()];
            int i = 0;
            for (Enumeration e = bindings.keys() ; e.hasMoreElements() ;) {
                keys[i++] = (KeyStroke) e.nextElement();
            }
            return keys;
        } 

        public Action[] getBoundActions() {
            Action[] actions = new Action[bindings.size()];
            int i = 0;
            for (Enumeration e = bindings.elements() ; e.hasMoreElements() ;) {
                actions[i++] = (Action) e.nextElement();
            }
            return actions;
        } 

        public KeyStroke[] getKeyStrokesForAction(Action a) {
	    if (a == null) {
		return null;
	    }
	    KeyStroke[] retValue = null;
	    // Determine local bindings first.
	    Vector keyStrokes = null;
	    for (Enumeration enum = bindings.keys();
		 enum.hasMoreElements();) {
		Object key = enum.nextElement();
		if (bindings.get(key) == a) {
		    if (keyStrokes == null) {
			keyStrokes = new Vector();
		    }
		    keyStrokes.addElement(key);
		}
	    }
	    // See if the parent has any.
	    if (parent != null) {
		KeyStroke[] pStrokes = parent.getKeyStrokesForAction(a);
		if (pStrokes != null) {
		    // Remove any bindings defined in the parent that
		    // are locally defined.
		    int rCount = 0;
		    for (int counter = pStrokes.length - 1; counter >= 0;
			 counter--) {
			if (isLocallyDefined(pStrokes[counter])) {
			    pStrokes[counter] = null;
			    rCount++;
			}
		    }
		    if (rCount > 0 && rCount < pStrokes.length) {
			if (keyStrokes == null) {
			    keyStrokes = new Vector();
			}
			for (int counter = pStrokes.length - 1; counter >= 0;
			     counter--) {
			    if (pStrokes[counter] != null) {
				keyStrokes.addElement(pStrokes[counter]);
			    }
			}
		    }
		    else if (rCount == 0) {
			if (keyStrokes == null) {
			    retValue = pStrokes;
			}
			else {
			    retValue = new KeyStroke[keyStrokes.size() +
						    pStrokes.length];
			    keyStrokes.copyInto(retValue);
			    System.arraycopy(pStrokes, 0, retValue,
					keyStrokes.size(), pStrokes.length);
			    keyStrokes = null;
			}
		    }
		}
	    }
	    if (keyStrokes != null) {
		retValue = new KeyStroke[keyStrokes.size()];
		keyStrokes.copyInto(retValue);
	    }
            return retValue;
        }

        public boolean isLocallyDefined(KeyStroke key) {
            return bindings.containsKey(key);
        }

        public void addActionForKeyStroke(KeyStroke key, Action a) {
            bindings.put(key, a);
        }

        public void removeKeyStrokeBinding(KeyStroke key) {
            bindings.remove(key);
        }

        public void removeBindings() {
            bindings.clear();
        }

        public Keymap getResolveParent() {
            return parent;
        }

        public void setResolveParent(Keymap parent) {
            this.parent = parent;
        }

        /**
         * String representation of the keymap... potentially 
         * a very long string.
         */
        public String toString() {
            return "Keymap[" + nm + "]" + bindings;
        }

        String nm;
        Keymap parent;
        Hashtable bindings;
        Action defaultAction;
    }

    /**
     * This is the name of the default keymap that will be shared by all
     * JTextComponent instances unless they have had a different
     * keymap set. 
     */
    public static final String DEFAULT_KEYMAP = "default";

    /**
     * Default bindings for the default keymap if no other bindings
     * are given.  
     */
    static final KeyBinding[] defaultBindings = {
        new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
                       DefaultEditorKit.deletePrevCharAction),
        new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                       DefaultEditorKit.deleteNextCharAction),
        new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                       DefaultEditorKit.forwardAction),
        new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                       DefaultEditorKit.backwardAction)
    };

    static {
        try {
            keymapTable = new Hashtable(17);
            Keymap binding = addKeymap(DEFAULT_KEYMAP, null);
            binding.setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
            EditorKit kit = new DefaultEditorKit();
            loadKeymap(binding, defaultBindings, kit.getActions());
        } catch (Throwable e) {
            e.printStackTrace();
            keymapTable = new Hashtable(17);
        }
    }

    /**
     * Event to use when firing a notification of change to caret 
     * position.  This is mutable so that the event can be reused
     * since caret events can be fairly high in bandwidth.
     */
    static class MutableCaretEvent extends CaretEvent implements ChangeListener, MouseListener, FocusListener {

        MutableCaretEvent(JTextComponent c) {
            super(c);
        }

        final void fire() {
            JTextComponent c = (JTextComponent) getSource();
            if (c != null) {
                Caret caret = c.getCaret();
                dot = caret.getDot();
                mark = caret.getMark();
                c.fireCaretUpdate(this);
            }
        }

        public final String toString() {
            return "dot=" + dot + "," + "mark=" + mark;
        }

        // --- CaretEvent methods -----------------------

        public final int getDot() {
            return dot;
        }

        public final int getMark() {
            return mark;
        }

        // --- ChangeListener methods -------------------

        public final void stateChanged(ChangeEvent e) {
            if (! dragActive) {
                fire();
            }
        }

        // --- FocusListener methods --------------------------------

        /**
         * Stashes the current focused JTextComponent reference
         * for JTextAction instances to use if the ActionEvent
         * doesn't contain the target text component.
         *
         * @param e the focus event
         * @see JTextAction
         * @see FocusListener#focusGained
         */
        public void focusGained(FocusEvent e) {
            focusedComponent = (JTextComponent) getSource();
        }

        /**
         * Removes reference to focused text component that
         * instances of JTextAction use.
         *
         * @param e the focus event
         * @see JTextAction
         * @see FocusListener#focusLost
         */
        public void focusLost(FocusEvent e) {
            // temp focus loss from menus causes problems
            //focusedComponent = null;
        }

        // --- MouseListener methods -----------------------------------
    
        /**
         * Requests focus on the associated
         * text component, and try to set the cursor position.
         *
         * @param e the mouse event
         * @see MouseListener#mousePressed
         */
        public final void mousePressed(MouseEvent e) {
            dragActive = true;
        }

        /**
         * Called when the mouse is released.
         *
         * @param e the mouse event
         * @see MouseListener#mouseReleased
         */
        public final void mouseReleased(MouseEvent e) {
            dragActive = false;
            fire();
        }

        public final void mouseClicked(MouseEvent e) {
        }

        public final void mouseEntered(MouseEvent e) {
        }

        public final void mouseExited(MouseEvent e) {
        }

        private boolean dragActive;
        private int dot;
        private int mark;
    }

    class FocusAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            requestFocus();
        }

        public boolean isEnabled() {
            if(isEditable())
                return true;
            else
                return false;
        }
    }

    
    //
    // Process any input method events that the component itself 
    // recognizes. The default on-the-spot handling for input method 
    // composed(uncommitted) text is done here after all input 
    // method listeners get called for stealing the events.
    //
    protected void processInputMethodEvent(InputMethodEvent e) {
	// let listeners handle the events
	super.processInputMethodEvent(e);

	if (!e.isConsumed()) {
            if (! isEditable()) {
                getToolkit().beep();
            } else {
  	        switch (e.getID()) {
		case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
		    replaceInputMethodText(e);

		    // fall through

		case InputMethodEvent.CARET_POSITION_CHANGED:
		    setInputMethodCaretPosition(e);
		    break;
		}
	    }

	    e.consume();
	}
    }

    //
    // Overrides this method to become an active input method client.
    //
    public InputMethodRequests getInputMethodRequests() {
    	if (inputMethodRequestsHandler == null) {
	    inputMethodRequestsHandler = 
	        (InputMethodRequests)new InputMethodRequestsHandler();
	}

	return inputMethodRequestsHandler;
    }

    //
    // Overrides this method to watch the listener installed.
    //
    public void addInputMethodListener(InputMethodListener l) {
    	super.addInputMethodListener(l);
	if (l != null) {
	    needToSendKeyTypedEvent = false;
	}
    }
    
    //
    // Default implementation of the InputMethodRequests interface.
    //
    class InputMethodRequestsHandler implements InputMethodRequests, Serializable {
    	public AttributedCharacterIterator cancelLatestCommittedText(
						Attribute[] attributes) {
	    return new AttributedString("").getIterator();
    	}

    	public AttributedCharacterIterator getCommittedText(int beginIndex, 
					int endIndex, Attribute[] attributes) {
	    return new AttributedString("").getIterator();
    	}

    	public int getCommittedTextLength() {
	    return 0;
    	}

    	public int getInsertPositionOffset() {
	    return getCaretPosition();
    	}

    	public TextHitInfo getLocationOffset(int x, int y) {
	    if (composedText == null) {
	        return null;
	    } else { 
	        Point p = getLocationOnScreen();
	        p.x = x - p.x;
	        p.y = y - p.y;
	        int pos = viewToModel(p);
	        if ((pos >= composedTextStart.getOffset()) && (pos <= composedTextEnd.getOffset())) {
	            return TextHitInfo.leading(pos - composedTextStart.getOffset());
	        } else {
	            return null;
	        }
	    }	
    	}

    	public Rectangle getTextLocation(TextHitInfo offset) {
	    Rectangle r;
		
	    try {
	        r = modelToView(getCaretPosition());
		if (r != null) {
		    Point p = getLocationOnScreen();
		    r.translate(p.x, p.y);
		}
	    } catch (BadLocationException ble) {
	        r = null;
	    }

	    if (r == null)
	        r = new Rectangle();

	    return r;
	}

    	public AttributedCharacterIterator getSelectedText(
						Attribute[] attributes) {
	    String selection = JTextComponent.this.getSelectedText();
	    if (selection != null) {
	        return new AttributedString(selection).getIterator();
	    } else {
	        return null;
	    }
    	}
    }

    //
    // Replaces the current input method (composed) text according to
    // the passed input method event. This method also inserts the 
    // committed text into the document. 
    //
    private void replaceInputMethodText(InputMethodEvent e) {
    	int commitCount = e.getCommittedCharacterCount();
	AttributedCharacterIterator text = e.getText();
	int composedTextIndex;

	// old composed text deletion
	Document doc = getDocument();
	if (composedTextStart != null) {
	    try {	
	        int removeOffs = composedTextStart.getOffset();
	        doc.remove(removeOffs, composedTextEnd.getOffset()-removeOffs);
	    } catch (BadLocationException ble) {}
	    composedTextStart = composedTextEnd = null;
	    composedText = null;
	    composedTextContent = null;
	}
	
	if (text != null) {
	    text.first();
	    
	    // committed text insertion
	    if (commitCount > 0) {
		// Need to generate KeyTyped events for the committed text for components
		// that are not aware they are active input method clients.
		if (needToSendKeyTypedEvent) {
		    for (char c = text.current(); commitCount > 0; 
		         c = text.next(), commitCount--) {
			KeyEvent ke = new KeyEvent(this, KeyEvent.KEY_TYPED, 
						   System.currentTimeMillis(),
						   0, KeyEvent.VK_UNDEFINED, c);
			processKeyEvent(ke);
		    }
		} else {
		    StringBuffer strBuf = new StringBuffer();
		    for (char c = text.current(); commitCount > 0; 
		         c = text.next(), commitCount--) {
			strBuf.append(c);
		    }
		
		    // map it to an ActionEvent
		    mapCommittedTextToAction(new String(strBuf));
		}
	    }

	    // new composed text insertion
	    composedTextIndex = text.getIndex();
	    if (composedTextIndex < text.getEndIndex()) {
		createComposedString(composedTextIndex, text);
		SimpleAttributeSet attrSet = new SimpleAttributeSet();	
		attrSet.addAttribute(StyleConstants.ComposedTextAttribute, 
					composedText);
		try {	
		    replaceSelection(null);
		    doc.insertString(caret.getDot(), composedTextContent, 
		    			attrSet);
		    composedTextStart = doc.createPosition(caret.getDot() -
						composedTextContent.length());
		    composedTextEnd = doc.createPosition(caret.getDot());
		} catch (BadLocationException ble) {
	            composedTextStart = composedTextEnd = null;
	            composedText = null;
	            composedTextContent = null;
		}	
	    }
	}
    }

    private void createComposedString(int composedIndex, 
    					AttributedCharacterIterator text) {
	Document doc = getDocument();
        StringBuffer strBuf = new StringBuffer();

	// create attributed string with no attributes
	for (char c = text.setIndex(composedIndex); 
             c != CharacterIterator.DONE; c = text.next()) {
	    strBuf.append(c);
	}
	
	composedTextContent = new String(strBuf);	
	composedText = new AttributedString(text, composedIndex,
	                                    text.getEndIndex());
    }
    
    //
    // Map committed text to an ActionEvent. If the committed text length is 1,
    // treat it as a KeyStroke, otherwise or there is no KeyStroke defined, 
    // treat it just as a default action.
    //
    private void mapCommittedTextToAction(String committedText) {
        Keymap binding = getKeymap();
        if (binding != null) {
            Action a = null;
            if (committedText.length() == 1) {
		KeyStroke k = KeyStroke.getKeyStroke(committedText.charAt(0));
                a = binding.getAction(k);
	    }

	    if (a == null) { 
	        a = binding.getDefaultAction();
	    }

            if (a != null) {
                ActionEvent ae = new ActionEvent(this, 
                                                 ActionEvent.ACTION_PERFORMED, 
                                                 committedText);
                a.actionPerformed(ae);
            }
        }
    }

    //
    // Sets the caret position according to the passed input method
    // event. Also, sets/resets composed text caret appropriately.
    //
    private void setInputMethodCaretPosition(InputMethodEvent e) {
	int dot;
	
	if (composedTextStart != null) {
	    dot = composedTextStart.getOffset();
	    if (!(caret instanceof ComposedTextCaret)) {
		if (composedTextCaret == null) {
		    composedTextCaret = new ComposedTextCaret();
		}
		originalCaret = caret;	
		// Sets composed text caret
	        exchangeCaret(originalCaret, composedTextCaret);
	    }

	    TextHitInfo caretPos = e.getCaret();
	    if (caretPos != null) {
		dot += caretPos.getInsertionIndex();
	    }
	    caret.setDot(dot);
	} else if (caret instanceof ComposedTextCaret) {
	    dot = caret.getDot();
	    // Restores original caret
	    exchangeCaret(caret, originalCaret);
	    caret.setDot(dot);
	}
    }

    private void exchangeCaret(Caret oldCaret, Caret newCaret) {
	int blinkRate = oldCaret.getBlinkRate();
	setCaret(newCaret);
	caret.setBlinkRate(blinkRate);
	caret.setVisible(hasFocus());
    }

    //
    // Checks whether the client code overrides processInputMethodEvent.  If it is overridden,
    // need not to generate KeyTyped events for committed text. If it's not, behave as an 
    // passive input method client.
    //
    private boolean isProcessInputMethodEventOverridden() {
	Boolean ret = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    Class[] classes = new Class[1];
		    classes[0] = InputMethodEvent.class;

		    for (Class c = JTextComponent.this.getClass(); 
		         c != JTextComponent.class; c = c.getSuperclass()) {
	    		try {
			    Method m = c.getDeclaredMethod("processInputMethodEvent", classes);
			    return Boolean.TRUE;
	    		} catch (NoSuchMethodException nsme) {
	    		    continue;
	    		}
		    }

		    return Boolean.FALSE;
		}
            }
        );

	return ret.booleanValue();
    }
    
    //
    // Caret implementation for editing the composed text.
    //
    class ComposedTextCaret extends DefaultCaret implements Serializable {
	Color bg;

	//	
	// Get the background color of the component
	//	
	public void install(JTextComponent c) {
	    super.install(c);

	    Document doc = c.getDocument();
	    if (doc instanceof StyledDocument) {
		StyledDocument sDoc = (StyledDocument)doc;
		Element elem = sDoc.getCharacterElement(c.composedTextStart.getOffset());
	        AttributeSet attr = elem.getAttributes();
	        bg = sDoc.getBackground(attr);
	    }
	
	    if (bg == null) {
	        bg = c.getBackground();
	    }
	}
        
	//
	// Draw caret in XOR mode.
        // 
	public void paint(Graphics g) {
	    if(isVisible()) {
	        try {
		    Rectangle r = component.modelToView(getDot());
		    g.setXORMode(bg);
		    g.drawLine(r.x, r.y, r.x, r.y + r.height - 1);
		    g.setPaintMode();
	        } catch (BadLocationException e) {
		    // can't render I guess
		    //System.err.println("Can't render cursor");
	        }
	    }
	}

	//
	// If some area other than the composed text is clicked by mouse,
	// issue endComposition() to force commit the composed text.
	//
	protected void positionCaret(MouseEvent me) {
	    JTextComponent host = component;
	    Point pt = new Point(me.getX(), me.getY());
	    int offset = host.viewToModel(pt);
	    if ((offset < host.composedTextStart.getOffset()) ||
	        (offset > host.composedTextEnd.getOffset())) {
		try {
		    // Issue endComposition
		    Position newPos = host.getDocument().createPosition(offset); 
		    host.getInputContext().endComposition();

		    // Post a caret positioning runnable to assure that the positioning
		    // occurs *after* committing the composed text.
		    EventQueue.invokeLater(new DoSetCaretPosition(host, newPos));
		} catch (BadLocationException ble) {
		    System.err.println(ble);
		}
	    } else {
	        // Normal processing
	        super.positionCaret(me);
	    }
	}
    }

    //
    // Runnable class for invokeLater() to set caret position later.
    //
    private class DoSetCaretPosition implements Runnable {
        JTextComponent host; 
	Position newPos;

	DoSetCaretPosition(JTextComponent host, Position newPos) {
	    this.host = host;
	    this.newPos = newPos;
	}

	public void run() {
	    host.setCaretPosition(newPos.getOffset());
	}
    }
    
}


