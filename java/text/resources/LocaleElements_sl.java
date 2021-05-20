/*
 * @(#)LocaleElements_sl.java	1.10 01/12/10
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

public class LocaleElements_sl extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "sl_SI" }, // locale id based on iso codes
            { "LocaleID", "0424" }, // Windows id
            { "ShortLanguage", "slv" }, // iso-3 abbrev lang name
            { "ShortCountry", "SVN" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "sl", "Sloven\u0161\u010dina" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "SI", "Slovenija" }
                }
            },
            { "MonthNames",
                new String[] {
                    "januar", // january
                    "februar", // february
                    "marec", // march
                    "april", // april
                    "maj", // may
                    "junij", // june
                    "julij", // july
                    "avgust", // august
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
                    "mar", // abb march
                    "apr", // abb april
                    "maj", // abb may
                    "jun", // abb june
                    "jul", // abb july
                    "avg", // abb august
                    "sep", // abb september
                    "okt", // abb october
                    "nov", // abb november
                    "dec", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames",
                new String[] {
                    "Nedelja", // Sunday
                    "Ponedeljek", // Monday
                    "Torek", // Tuesday
                    "Sreda", // Wednesday
                    "\u010cetrtek", // Thursday
                    "Petek", // Friday
                    "Sobota" // Saturday
                }
            },
            { "DayAbbreviations",
                new String[] {
                    "Ned", // abb Sunday
                    "Pon", // abb Monday
                    "Tor", // abb Tuesday
                    "Sre", // abb Wednesday
                    "\u010cet", // abb Thursday
                    "Pet", // abb Friday
                    "Sob" // abb Saturday
                }
            },
            { "Eras",
                new String[] { // era strings
                    "pr.n.\u0161.",
                    "po Kr."
                }
            },
            { "NumberPatterns",
                new String[] {
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "tol #,##0.##;-tol #,##0.##", // currency pattern
                    "#,##0%" // percent pattern
                }
            },
            { "NumberElements",
                new String[] {
                    ",", // decimal separator
                    ".", // group (thousands) separator
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
                    "tol", // local currency symbol
                    "SIT", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns",
                new String[] {
                    "H:mm:ss z", // full time pattern
                    "H:mm:ss z", // long time pattern
                    "H:mm:ss", // medium time pattern
                    "H:mm", // short time pattern
                    "EEEE, yyyy, MMMM d", // full date pattern
                    "EEEE, yyyy, MMMM d", // long date pattern
                    "yyyy.M.d", // medium date pattern
                    "y.M.d", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "CollationElements",
                /* for sl_SI, default sorting except for the following: */

                /* add d<stroke> between d and e. */
                /* add l<stroke> between l and m. */
                /* add nj "ligature" between n and o. */
                /* add z<abovedot> after z.       */
                "& C < c\u030c , C\u030c "           // C < c-caron
                + "< c\u0301 , C\u0301 "             // c-acute
                + "& D < \u01f3 , \u01f2 , \u01f1 "  // dz
                + "< \u01c6 , \u01c5 , \u01c4 "      // dz-caron
                + "< \u0111 , \u0110 "               // d-stroke
                + "& L < \u0142 , \u0141 "           // l < l-stroke
                + "& N < nj , nJ , Nj , NJ "         // ligature updated
                + "& S < s\u030c , S\u030c "         // s < s-caron
                + "< s\u0301, S\u0301 "              // s-acute
                + "& Z < z\u030c , Z\u030c "         // z < z-caron
                + "< z\u0301 , Z\u0301 "             // z-acute
                + "< z\u0307 , Z\u0307 "             // z-dot-above
            }
        };
    }
}
