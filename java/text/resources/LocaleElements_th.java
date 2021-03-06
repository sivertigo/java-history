/*
 * @(#)LocaleElements_th.java	1.4 01/12/10
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

public class LocaleElements_th extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "th_TH" }, // locale id based on iso codes
            { "LocaleID", "041e" }, // Windows id
            { "ShortLanguage", "tha" }, // iso-3 abbrev lang name
            { "ShortCountry", "THA" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "th", "\u0e44\u0e17\u0e22" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "TH", "\u0e1b\u0e23\u0e30\u0e40\u0e17\u0e28\u0e44\u0e17\u0e22" }
                }
            },
            { "MonthNames", 
                new String[] { 
                    "\u0e21\u0e01\u0e23\u0e32\u0e04\u0e21", // january
                    "\u0e01\u0e38\u0e21\u0e20\u0e32\u0e1e\u0e31\u0e19\u0e18\u0e4c", // february
                    "\u0e21\u0e35\u0e19\u0e32\u0e04\u0e21", // march
                    "\u0e40\u0e21\u0e29\u0e32\u0e22\u0e19", // april
                    "\u0e1e\u0e24\u0e29\u0e20\u0e32\u0e04\u0e21", // may
                    "\u0e21\u0e34\u0e16\u0e38\u0e19\u0e32\u0e22\u0e19", // june
                    "\u0e01\u0e23\u0e01\u0e0e\u0e32\u0e04\u0e21", // july
                    "\u0e2a\u0e34\u0e07\u0e2b\u0e32\u0e04\u0e21", // august
                    "\u0e01\u0e31\u0e19\u0e22\u0e32\u0e22\u0e19", // september
                    "\u0e15\u0e38\u0e25\u0e32\u0e04\u0e21", // october
                    "\u0e1e\u0e24\u0e28\u0e08\u0e34\u0e01\u0e32\u0e22\u0e19", // november
                    "\u0e18\u0e31\u0e19\u0e27\u0e32\u0e04\u0e21", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations", 
                new String[] { 
                    "\u0e21.\u0e04.", // abb january
                    "\u0e01.\u0e1e.", // abb february
                    "\u0e21\u0e35.\u0e04.", // abb march
                    "\u0e40\u0e21.\u0e22.", // abb april
                    "\u0e1e.\u0e04.", // abb may
                    "\u0e21\u0e34.\u0e22.", // abb june
                    "\u0e01.\u0e04.", // abb july
                    "\u0e2a.\u0e04.", // abb august
                    "\u0e01.\u0e22.", // abb september
                    "\u0e15.\u0e04.", // abb october
                    "\u0e1e.\u0e22.", // abb november
                    "\u0e18.\u0e04.", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames", 
                new String[] { 
                    "\u0e27\u0e31\u0e19\u0e2d\u0e32\u0e17\u0e34\u0e15\u0e22\u0e4c", // Sunday
                    "\u0e27\u0e31\u0e19\u0e08\u0e31\u0e19\u0e17\u0e23\u0e4c", // Monday
                    "\u0e27\u0e31\u0e19\u0e2d\u0e31\u0e07\u0e04\u0e32\u0e23", // Tuesday
                    "\u0e27\u0e31\u0e19\u0e1e\u0e38\u0e18", // Wednesday
                    "\u0e27\u0e31\u0e19\u0e1e\u0e24\u0e2b\u0e31\u0e2a\u0e1a\u0e14\u0e35", // Thursday
                    "\u0e27\u0e31\u0e19\u0e28\u0e38\u0e01\u0e23\u0e4c", // Friday
                    "\u0e27\u0e31\u0e19\u0e40\u0e2a\u0e32\u0e23\u0e4c" // Saturday
                }
            },
            { "DayAbbreviations", 
                new String[] { 
                    "\u0e2d\u0e32.", // abb Sunday
                    "\u0e08.", // abb Monday
                    "\u0e2d.", // abb Tuesday
                    "\u0e1e.", // abb Wednesday
                    "\u0e1e\u0e24.", // abb Thursday
                    "\u0e28.", // abb Friday
                    "\u0e2a." // abb Saturday
                }
            },
            { "AmPmMarkers", 
                new String[] { 
                    "\u0e01\u0e48\u0e2d\u0e19\u0e40\u0e17\u0e35\u0e48\u0e22\u0e07", // am marker
                    "\u0e2b\u0e25\u0e31\u0e07\u0e40\u0e17\u0e35\u0e48\u0e22\u0e07" // pm marker
                }
            },
            { "Eras", 
                new String[] { // era strings
                    "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48", 
                    "\u0e04.\u0e28." 
                }
            },
            { "NumberPatterns", 
                new String[] { 
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "#,##0.00 \u0e1a\u0e32\u0e17;-#,##0.00 \u0e1a\u0e32\u0e17", // currency pattern
                    "#,##0%" // percent pattern
                }
            },
            { "CurrencyElements", 
                new String[] { 
                    "\u0e1a\u0e32\u0e17", // local currency symbol
                    "THB", // intl currency symbol
                    "." // monetary decimal separator
                }
            },
            { "DateTimePatterns", 
                new String[] { 
                    "H' \u0e19\u0e32\u0e2c\u0e34\u0e01\u0e32 'm' \u0e19\u0e32\u0e17\u0e35 'ss' \u0e27\u0e34\u0e19\u0e32\u0e17\u0e35'", // full time pattern
                    "H' \u0e19\u0e32\u0e2c\u0e34\u0e01\u0e32 'm' \u0e19\u0e32\u0e17\u0e35'", // long time pattern
                    "H:mm:ss", // medium time pattern
                    "H:mm", // short time pattern
                    "EEEE'\u0e17\u0e35\u0e48 'd MMMM G yyyy", // full date pattern
                    "d MMMM yyyy", // long date pattern
                    "d MMM yyyy", // medium date pattern
                    "d/M/yyyy", // short date pattern
                    "{1}, {0}" // date-time pattern
                }
            }
        };
    }
}
