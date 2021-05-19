/*
 * @(#)LocaleElements_nl.java	1.7 01/12/10
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

public class LocaleElements_nl extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "nl_NL" }, // locale id based on iso codes
            { "LocaleID", "0413" }, // Windows id
            { "ShortLanguage", "nld" }, // iso-3 abbrev lang name
            { "ShortCountry", "NLD" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "nl", "Nederlands" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "NL", "Nederland" },
                    { "BE", "Belgi\u00eb" }
                }
            },
            { "MonthNames", 
                new String[] { 
                    "januari", // january
                    "februari", // february
                    "maart", // march
                    "april", // april
                    "mei", // may
                    "juni", // june
                    "juli", // july
                    "augustus", // august
                    "september", // september
                    "oktober", // october
                    "november", // november
                    "december", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations", 
                new String[] { 
                    "jan", // abb january
                    "feb", // abb february
                    "mrt", // abb march
                    "apr", // abb april
                    "mei", // abb may
                    "jun", // abb june
                    "jul", // abb july
                    "aug", // abb august
                    "sep", // abb september
                    "okt", // abb october
                    "nov", // abb november
                    "dec", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames", 
                new String[] { 
                    "zondag", // Sunday
                    "maandag", // Monday
                    "dinsdag", // Tuesday
                    "woensdag", // Wednesday
                    "donderdag", // Thursday
                    "vrijdag", // Friday
                    "zaterdag" // Saturday
                }
            },
            { "DayAbbreviations", 
                new String[] { 
                    "zo", // abb Sunday
                    "ma", // abb Monday
                    "di", // abb Tuesday
                    "wo", // abb Wednesday
                    "do", // abb Thursday
                    "vr", // abb Friday
                    "za" // abb Saturday
                }
            },
            { "NumberPatterns", 
                new String[] { 
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "fl #,##0.00;fl #,##0.00-", // currency pattern
                    "#,##0%" // percent pattern
                }
            },
            { "NumberElements", 
                new String[] { 
                    ",", // decimal separator
                    ".", // group (thousandsnds) separator
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
                    "fl", // local currency symbol
                    "NLG", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns", 
                new String[] { 
                    "H:mm:ss' uur' z", // full time pattern
                    "H:mm:ss z", // long time pattern
                    "H:mm:ss", // medium time pattern
                    "H:mm", // short time pattern
                    "EEEE d MMMM yyyy", // full date pattern
                    "d MMMM yyyy", // long date pattern
                    "d-MMM-yy", // medium date pattern
                    "d-M-yy", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "DateTimeElements", 
                new String[] { 
                    "2", // first day of week
                    "4" // min days in first week
                }
            }
        };
    }
}
