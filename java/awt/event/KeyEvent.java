/*
 * @(#)KeyEvent.java	1.27 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.awt.Event;
import java.awt.Component;
import java.awt.Toolkit;

/**
 * The component-level keyboard event.
 *
 * @version 1.23 08/04/97
 * @author Carl Quinn
 * @author Amy Fowler
 */
public class KeyEvent extends InputEvent {

    /**
     * Marks the first integer id for the range of key event ids.
     */
    public static final int KEY_FIRST = 400;

    /**
     * Marks the last integer id for the range of key event ids.
     */
    public static final int KEY_LAST  = 402;

    /**
     * The key typed event type.  This type is generated by a combination
     * of a key press followed by a key release.
     */
    public static final int KEY_TYPED = KEY_FIRST;

    /**
     * The key pressed event type.
     */
    public static final int KEY_PRESSED = 1 + KEY_FIRST; //Event.KEY_PRESS

    /**
     * The key released event type.
     */
    public static final int KEY_RELEASED = 2 + KEY_FIRST; //Event.KEY_RELEASE

    /**
     * Virtual key codes.  These codes report which keyboard key has
     * been pressed, rather than any character generated by one or more
     * keys being pressed.  
     *
     * For example, pressing the Shift key will cause a KEY_PRESSED event 
     * with a VK_SHIFT keyCode, while pressing the 'a' key will result in 
     * a VK_A keyCode.  After the 'a' key is released, a KEY_RELEASED event 
     * will be fired with VK_A, followed by a KEY_TYPED event with a keyChar 
     * value of 'A'.  Key combinations which do not result in characters,
     * such as action keys like F1, will not generate KEY_TYPED events.
     *
     * Note: not all keyboards or systems are capable of generating all
     * virtual key codes.  No attempt is made in Java to artificially
     * generate these keys.
     *
     * WARNING:  aside from those keys where are defined by the Java language
     * (VK_ENTER, VK_BACK_SPACE, and VK_TAB), do not rely on the values of these
     * constants.  Sun reserves the right to change these values as needed
     * to accomodate a wider range of keyboards in the future.  
     */
    public static final int VK_ENTER          = '\n';
    public static final int VK_BACK_SPACE     = '\b';
    public static final int VK_TAB            = '\t';
    public static final int VK_CANCEL         = 0x03;
    public static final int VK_CLEAR          = 0x0C;
    public static final int VK_SHIFT          = 0x10;
    public static final int VK_CONTROL        = 0x11;
    public static final int VK_ALT            = 0x12;
    public static final int VK_PAUSE          = 0x13;
    public static final int VK_CAPS_LOCK      = 0x14;
    public static final int VK_ESCAPE         = 0x1B;
    public static final int VK_SPACE          = 0x20;
    public static final int VK_PAGE_UP        = 0x21;
    public static final int VK_PAGE_DOWN      = 0x22;
    public static final int VK_END            = 0x23;
    public static final int VK_HOME           = 0x24;
    public static final int VK_LEFT           = 0x25;
    public static final int VK_UP             = 0x26;
    public static final int VK_RIGHT          = 0x27;
    public static final int VK_DOWN           = 0x28;
    public static final int VK_COMMA          = 0x2C;
    public static final int VK_PERIOD         = 0x2E;
    public static final int VK_SLASH          = 0x2F;

    /** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
    public static final int VK_0              = 0x30;
    public static final int VK_1              = 0x31;
    public static final int VK_2              = 0x32;
    public static final int VK_3              = 0x33;
    public static final int VK_4              = 0x34;
    public static final int VK_5              = 0x35;
    public static final int VK_6              = 0x36;
    public static final int VK_7              = 0x37;
    public static final int VK_8              = 0x38;
    public static final int VK_9              = 0x39;

    public static final int VK_SEMICOLON      = 0x3B;
    public static final int VK_EQUALS         = 0x3D;

    /** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
    public static final int VK_A              = 0x41;
    public static final int VK_B              = 0x42;
    public static final int VK_C              = 0x43;
    public static final int VK_D              = 0x44;
    public static final int VK_E              = 0x45;
    public static final int VK_F              = 0x46;
    public static final int VK_G              = 0x47;
    public static final int VK_H              = 0x48;
    public static final int VK_I              = 0x49;
    public static final int VK_J              = 0x4A;
    public static final int VK_K              = 0x4B;
    public static final int VK_L              = 0x4C;
    public static final int VK_M              = 0x4D;
    public static final int VK_N              = 0x4E;
    public static final int VK_O              = 0x4F;
    public static final int VK_P              = 0x50;
    public static final int VK_Q              = 0x51;
    public static final int VK_R              = 0x52;
    public static final int VK_S              = 0x53;
    public static final int VK_T              = 0x54;
    public static final int VK_U              = 0x55;
    public static final int VK_V              = 0x56;
    public static final int VK_W              = 0x57;
    public static final int VK_X              = 0x58;
    public static final int VK_Y              = 0x59;
    public static final int VK_Z              = 0x5A;

    public static final int VK_OPEN_BRACKET   = 0x5B;
    public static final int VK_BACK_SLASH     = 0x5C;
    public static final int VK_CLOSE_BRACKET  = 0x5D;

    public static final int VK_NUMPAD0        = 0x60;
    public static final int VK_NUMPAD1        = 0x61;
    public static final int VK_NUMPAD2        = 0x62;
    public static final int VK_NUMPAD3        = 0x63;
    public static final int VK_NUMPAD4        = 0x64;
    public static final int VK_NUMPAD5        = 0x65;
    public static final int VK_NUMPAD6        = 0x66;
    public static final int VK_NUMPAD7        = 0x67;
    public static final int VK_NUMPAD8        = 0x68;
    public static final int VK_NUMPAD9        = 0x69;
    public static final int VK_MULTIPLY       = 0x6A;
    public static final int VK_ADD            = 0x6B;
    public static final int VK_SEPARATER      = 0x6C;
    public static final int VK_SUBTRACT       = 0x6D;
    public static final int VK_DECIMAL        = 0x6E;
    public static final int VK_DIVIDE         = 0x6F;
    public static final int VK_F1             = 0x70;
    public static final int VK_F2             = 0x71;
    public static final int VK_F3             = 0x72;
    public static final int VK_F4             = 0x73;
    public static final int VK_F5             = 0x74;
    public static final int VK_F6             = 0x75;
    public static final int VK_F7             = 0x76;
    public static final int VK_F8             = 0x77;
    public static final int VK_F9             = 0x78;
    public static final int VK_F10            = 0x79;
    public static final int VK_F11            = 0x7A;
    public static final int VK_F12            = 0x7B;
    public static final int VK_DELETE         = 0x7F; /* ASCII DEL */
    public static final int VK_NUM_LOCK       = 0x90;
    public static final int VK_SCROLL_LOCK    = 0x91;

    public static final int VK_PRINTSCREEN    = 0x9A;
    public static final int VK_INSERT         = 0x9B;
    public static final int VK_HELP           = 0x9C;
    public static final int VK_META           = 0x9D;

    public static final int VK_BACK_QUOTE     = 0xC0;
    public static final int VK_QUOTE          = 0xDE;

    /** for Asian Keyboard */
    public static final int VK_FINAL          = 0x18;
    public static final int VK_CONVERT        = 0x1C;
    public static final int VK_NONCONVERT     = 0x1D;
    public static final int VK_ACCEPT         = 0x1E;
    public static final int VK_MODECHANGE     = 0x1F;
    public static final int VK_KANA           = 0x15;
    public static final int VK_KANJI          = 0x19;
    
    /**
     * KEY_TYPED events do not have a defined keyCode.
     */
    public static final int VK_UNDEFINED      = 0x0;

    /**
     * KEY_PRESSED and KEY_RELEASED events which do not map to a
     * valid Unicode character do not have a defined keyChar.
     */
    public static final char CHAR_UNDEFINED   = 0x0;

    int  keyCode;
    char keyChar;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -2352130953028126954L;

    /**
     * Constructs a KeyEvent object with the specified source component,
     * type, modifiers, and key.
     * @param source the object where the event originated
     * @id the event type
     * @when the time the event occurred
     * @modifiers the modifier keys down during event
     * @keyCode the integer code representing the key of the event 
     * @keyChar the Unicode character generated by this event, or NUL
     */
    public KeyEvent(Component source, int id, long when, int modifiers,
                    int keyCode, char keyChar) {
        super(source, id, when, modifiers);

        if (id == KEY_TYPED && keyChar == CHAR_UNDEFINED) {
            throw new IllegalArgumentException("invalid keyChar");
        }
        if (id == KEY_TYPED && keyCode != VK_UNDEFINED) {
            throw new IllegalArgumentException("invalid keyCode");
        }

        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    /*
     * @deprecated, as of JDK1.1 - Do NOT USE; will be removed in 1.1.1.
     */
    public KeyEvent(Component source, int id, long when, int modifiers,
                    int keyCode) {
        this(source, id, when, modifiers, keyCode, (char)keyCode);
    }

    /**
     * Returns the integer key-code associated with the key in this event.
     * For KEY_TYPED events, keyCode is VK_UNDEFINED.
     */
    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyChar(char keyChar) {
        this.keyChar = keyChar;
    }

    /**
     * Change the modifiers for a KeyEvent.  
     * <p>
     * NOTE:  use of this method is not recommended, because many AWT
     * implementations do not recognize modifier changes.  This is
     * especially true for KEY_TYPED events where the shift modifier
     * is changed.
     * @deprecated, as of JDK1.1.4
     */
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Returns the character associated with the key in this event.
     * If no valid Unicode character exists for this key event, keyChar
     * is CHAR_UNDEFINED.
     */
    public char getKeyChar() {
        return keyChar;
    }

    /**
     * Returns a String describing the keyCode, such as "HOME", "F1" or "A".
     * These strings can be localized by changing the awt.properties file.
     */
    public static String getKeyText(int keyCode) {
        if (keyCode >= VK_0 && keyCode <= VK_9 || 
            keyCode >= VK_A && keyCode <= VK_Z) {
            return String.valueOf((char)keyCode);
        }

        // Check for other ASCII keyCodes.
        int index = ",./;=[\\]".indexOf(keyCode);
        if (index >= 0) {
            return String.valueOf((char)keyCode);
        }
	
        switch(keyCode) {
          case VK_ENTER: return Toolkit.getProperty("AWT.enter", "Enter");
          case VK_BACK_SPACE: return Toolkit.getProperty("AWT.backSpace", "Backspace");
          case VK_TAB: return Toolkit.getProperty("AWT.tab", "Tab");
          case VK_CANCEL: return Toolkit.getProperty("AWT.cancel", "Cancel");
          case VK_CLEAR: return Toolkit.getProperty("AWT.clear", "Clear");
          case VK_SHIFT: return Toolkit.getProperty("AWT.shift", "Shift");
          case VK_CONTROL: return Toolkit.getProperty("AWT.control", "Control");
          case VK_ALT: return Toolkit.getProperty("AWT.alt", "Alt");
          case VK_PAUSE: return Toolkit.getProperty("AWT.pause", "Pause");
          case VK_CAPS_LOCK: return Toolkit.getProperty("AWT.capsLock", "Caps Lock");
          case VK_ESCAPE: return Toolkit.getProperty("AWT.escape", "Escape");
          case VK_SPACE: return Toolkit.getProperty("AWT.space", "Space");
          case VK_PAGE_UP: return Toolkit.getProperty("AWT.pgup", "Page Up");
          case VK_PAGE_DOWN: return Toolkit.getProperty("AWT.pgdn", "Page Down");
          case VK_END: return Toolkit.getProperty("AWT.end", "End");
          case VK_HOME: return Toolkit.getProperty("AWT.home", "Home");
          case VK_LEFT: return Toolkit.getProperty("AWT.left", "Left");
          case VK_UP: return Toolkit.getProperty("AWT.up", "Up");
          case VK_RIGHT: return Toolkit.getProperty("AWT.right", "Right");
          case VK_DOWN: return Toolkit.getProperty("AWT.down", "Down");

          case VK_MULTIPLY: return Toolkit.getProperty("AWT.multiply", "NumPad *");
          case VK_ADD: return Toolkit.getProperty("AWT.add", "NumPad +");
          case VK_SEPARATER: return Toolkit.getProperty("AWT.separater", "NumPad ,");
          case VK_SUBTRACT: return Toolkit.getProperty("AWT.subtract", "NumPad -");
          case VK_DECIMAL: return Toolkit.getProperty("AWT.decimal", "NumPad .");
          case VK_DIVIDE: return Toolkit.getProperty("AWT.divide", "NumPad /");

          case VK_F1: return Toolkit.getProperty("AWT.f1", "F1");
          case VK_F2: return Toolkit.getProperty("AWT.f2", "F2");
          case VK_F3: return Toolkit.getProperty("AWT.f3", "F3");
          case VK_F4: return Toolkit.getProperty("AWT.f4", "F4");
          case VK_F5: return Toolkit.getProperty("AWT.f5", "F5");
          case VK_F6: return Toolkit.getProperty("AWT.f6", "F6");
          case VK_F7: return Toolkit.getProperty("AWT.f7", "F7");
          case VK_F8: return Toolkit.getProperty("AWT.f8", "F8");
          case VK_F9: return Toolkit.getProperty("AWT.f9", "F9");
          case VK_F10: return Toolkit.getProperty("AWT.f10", "F10");
          case VK_F11: return Toolkit.getProperty("AWT.f11", "F11");
          case VK_F12: return Toolkit.getProperty("AWT.f12", "F12");
          case VK_DELETE: return Toolkit.getProperty("AWT.delete", "Delete");
          case VK_NUM_LOCK: return Toolkit.getProperty("AWT.numLock", "Num Lock");
          case VK_SCROLL_LOCK: return Toolkit.getProperty("AWT.scrollLock", "Scroll Lock");
          case VK_PRINTSCREEN: return Toolkit.getProperty("AWT.printScreen", "Print Screen");
          case VK_INSERT: return Toolkit.getProperty("AWT.insert", "Insert");
          case VK_HELP: return Toolkit.getProperty("AWT.help", "Help");
          case VK_META: return Toolkit.getProperty("AWT.meta", "Meta");
          case VK_BACK_QUOTE: return Toolkit.getProperty("AWT.backQuote", "Back Quote");
          case VK_QUOTE: return Toolkit.getProperty("AWT.quote", "Quote");
			 
          case VK_FINAL: return Toolkit.getProperty("AWT.final", "Final");
          case VK_CONVERT: return Toolkit.getProperty("AWT.convert", "Convert");
          case VK_NONCONVERT: return Toolkit.getProperty("AWT.noconvert", "No Convert");
          case VK_ACCEPT: return Toolkit.getProperty("AWT.accept", "Accept");
          case VK_MODECHANGE: return Toolkit.getProperty("AWT.modechange", "Mode Change");
          case VK_KANA: return Toolkit.getProperty("AWT.kana", "Kana");
	  case VK_KANJI: return Toolkit.getProperty("AWT.kanji", "Kanji");
        }

        if (keyCode >= VK_NUMPAD0 && keyCode <= VK_NUMPAD9) {
            String numpad = Toolkit.getProperty("AWT.numpad", "NumPad");
	    char c = (char)(keyCode - VK_NUMPAD0 + '0');
            return numpad + "-" + c;
        }

        String unknown = Toolkit.getProperty("AWT.unknown", "Unknown keyCode");
        return unknown + ": 0x" + Integer.toString(keyCode, 16);
    }

    /**
     * Returns a String describing the modifier key(s), such as "Shift",
     * or "Ctrl+Shift".  These strings can be localized by changing the 
     * awt.properties file.
     */
    public static String getKeyModifiersText(int modifiers) {
        StringBuffer buf = new StringBuffer();
        if ((modifiers & InputEvent.META_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.meta", "Meta"));
            buf.append("+");
        }
        if ((modifiers & InputEvent.CTRL_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.control", "Ctrl"));
            buf.append("+");
        }
        if ((modifiers & InputEvent.ALT_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.alt", "Alt"));
            buf.append("+");
        }
        if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.shift", "Shift"));
            buf.append("+");
        }
        if (buf.length() > 0) {
            buf.setLength(buf.length()-1); // remove trailing '+'
        }
        return buf.toString();
    }

    /** Returns whether or not the key in this event is an "action" key,
     *  as defined in Event.java.
     */
    public boolean isActionKey() {
        switch (keyCode) {
          case VK_HOME:
          case VK_END:
          case VK_PAGE_UP:
          case VK_PAGE_DOWN:
          case VK_UP:
          case VK_DOWN:
          case VK_LEFT:
          case VK_RIGHT:
          case VK_F1:
          case VK_F2:
          case VK_F3:
          case VK_F4:
          case VK_F5:
          case VK_F6:
          case VK_F7:
          case VK_F8:
          case VK_F9:
          case VK_F10:
          case VK_F11:
          case VK_F12:
          case VK_PRINTSCREEN:
          case VK_SCROLL_LOCK:
          case VK_CAPS_LOCK:
          case VK_NUM_LOCK:
          case VK_PAUSE:
          case VK_INSERT:
              return true;
        }
        return false;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case KEY_PRESSED:
              typeStr = "KEY_PRESSED";
              break;
          case KEY_RELEASED:
              typeStr = "KEY_RELEASED";
              break;
          case KEY_TYPED:
              typeStr = "KEY_TYPED";
              break;
          default:
              typeStr = "unknown type";
        }

        String str = typeStr + ",keyCode=" + keyCode;
        if (isActionKey() || keyCode == VK_ENTER || keyCode == VK_BACK_SPACE || 
	    keyCode == VK_TAB || keyCode == VK_ESCAPE || keyCode == VK_DELETE ||
	    (keyCode >= VK_NUMPAD0 && keyCode <= VK_NUMPAD9)) {
            str += "," + getKeyText(keyCode);
	} else if (keyChar == '\n' || keyChar == '\b' ||
	    keyChar == '\t' || keyChar == VK_ESCAPE || keyChar == VK_DELETE) {
            str += "," + getKeyText(keyChar);
        } else {
            str += ",keyChar='" + keyChar + "'";
        }
        if (modifiers > 0) {
            str += ",modifiers=" + getKeyModifiersText(modifiers);
        }
        return str;
    }

}
