/*
 * @(#)LocaleElements_de_AT.java	1.10 01/12/10
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996, 1997 - All Rights Reserved
 *
 * Portions copyright (c) 2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

/**
 *
 * Table of Java supplied standard locale elements
 *
 * automatically generated by java LocaleTool LocaleElements.java
 *
 * Date Created: Wed Aug 21 15:47:57  1996
 *
 *     Locale Elements and Patterns:  last update 10/23/96
 *
 *
 */

// WARNING : the format of this file will change in the future!

package java.text.resources;

import java.util.ListResourceBundle;

public class LocaleElements_de_AT extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "de_AT" }, // locale id based on iso codes
            { "LocaleID", "0c07" }, // Windows id
            { "ShortCountry", "AUT" }, // iso-3 abbrev country name
            { "MonthNames", 
                new String[] { 
                    "J\u00e4nner", // january
                    "Februar", // february
                    "M\u00e4rz", // march
                    "April", // april
                    "Mai", // may
                    "Juni", // june
                    "Juli", // july
                    "August", // august
                    "September", // september
                    "Oktober", // october
                    "November", // november
                    "Dezember", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations", 
                new String[] { 
                    "J\u00e4n", // abb january
                    "Feb", // abb february
                    "M\u00e4r", // abb march
                    "Apr", // abb april
                    "Mai", // abb may
                    "Jun", // abb june
                    "Jul", // abb july
                    "Aug", // abb august
                    "Sep", // abb september
                    "Okt", // abb october
                    "Nov", // abb november
                    "Dez", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "NumberPatterns", 
                new String[] { 
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "\u00f6S #,##0.00;-\u00f6S #,##0.00", // currency pattern
                    "#,##0%" // percent pattern
                }
            },
            { "CurrencyElements", 
                new String[] { 
                    "\u00f6S", // local currency symbol
                    "ATS", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns", 
                new String[] { 
                    "HH:mm' Uhr 'z", // full time pattern
                    "HH:mm:ss z", // long time pattern
                    "HH:mm:ss", // medium time pattern
                    "HH:mm", // short time pattern
                    "EEEE, dd. MMMM yyyy", // full date pattern
                    "dd. MMMM yyyy", // long date pattern
                    "dd.MM.yyyy", // medium date pattern
                    "dd.MM.yy", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "DateTimeElements", 
                new String[] { 
                    "2", // first day of week
                    "1" // min days in first week
                }
            }
        };
    }
}
