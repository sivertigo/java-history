/*
 * @(#)LocaleElements_lv.java	1.8 01/12/10
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

public class LocaleElements_lv extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "lv_LV" }, // locale id based on iso codes
            { "LocaleID", "0426" }, // Windows id
            { "ShortLanguage", "lav" }, // iso-3 abbrev lang name
            { "ShortCountry", "LVA" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "lv", "Latvie\u0161u" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "LV", "Latvija" }
                }
            },
            { "MonthNames",
                new String[] {
                    "janv\u0101ris", // january
                    "febru\u0101ris", // february
                    "marts", // march
                    "apr\u012blis", // april
                    "maijs", // may
                    "j\u016bnijs", // june
                    "j\u016blijs", // july
                    "augusts", // august
                    "septembris", // september
                    "oktobris", // october
                    "novembris", // november
                    "decembris", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations",
                new String[] {
                    "Jan", // abb january
                    "Feb", // abb february
                    "Mar", // abb march
                    "Apr", // abb april
                    "Maijs", // abb may
                    "J\u016bn", // abb june
                    "J\u016bl", // abb july
                    "Aug", // abb august
                    "Sep", // abb september
                    "Okt", // abb october
                    "Nov", // abb november
                    "Dec", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames",
                new String[] {
                    "sv\u0113tdiena", // Sunday
                    "pirmdiena", // Monday
                    "otrdiena", // Tuesday
                    "tre\u0161diena", // Wednesday
                    "ceturdien", // Thursday
                    "piektdiena", // Friday
                    "sestdiena" // Saturday
                }
            },
            { "DayAbbreviations",
                new String[] {
                    "Sv", // abb Sunday
                    "P", // abb Monday
                    "O", // abb Tuesday
                    "T", // abb Wednesday
                    "C", // abb Thursday
                    "Pk", // abb Friday
                    "S" // abb Saturday
                }
            },
            { "Eras",
                new String[] { // era strings
                    "pm\u0113",
                    "m\u0113"
                }
            },
            { "NumberPatterns",
                new String[] {
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "#,##0.## Ls;-#,##0.## Ls", // currency pattern
                    "#,##0%" // percent pattern
                }
            },
            { "NumberElements",
                new String[] {
                    ",", // decimal separator
                    "\u00a0", // group (thousands) separator
                    ";", // list separator
                    "%", // percent sign
                    "0", // native 0 digit
                    "#", // pattern digit
                    "-", // minus sign
                    "E", // exponential
                    "\u2030", // per mille
                    "\u221e", // infinity
                    "\ufffd" // NaN
                }
            },
            { "CurrencyElements",
                new String[] {
                    "Ls", // local currency symbol
                    "LVL", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns",
                new String[] {
                    "HH:mm:ss z", // full time pattern
                    "HH:mm:ss z", // long time pattern
                    "HH:mm:ss", // medium time pattern
                    "HH:mm", // short time pattern
                    "EEEE, yyyy, d MMMM", // full date pattern
                    "EEEE, yyyy, d MMMM", // long date pattern
                    "yyyy.d.M", // medium date pattern
                    "yy.d.M", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "CollationElements",
                /* for LV_LV, accents sorted backwards plus the following: */

                "@" /* sort accents bkwd */
                + "& C < c\u030c , C\u030c "  // C < c-caron
                + "& G < g\u0327 , G\u0327 "  // G < g-cedilla
                + "& I < y, Y"                // tal : i < y
                + "& K < k\u0327 , K\u0327 "  // K < k-cedilla
                + "& L < l\u0327 , L\u0327 "  // L < l-cedilla
                + "& N < n\u0327 , N\u0327 "  // N < n-cedilla
                + "& S < s\u030c , S\u030c "  // S < s-caron
                + "& Z < z\u030c , Z\u030c "  // Z < z-caron
            }
        };
    }
}
