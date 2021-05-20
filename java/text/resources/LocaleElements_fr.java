/*
 * @(#)LocaleElements_fr.java	1.8 01/12/10
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

public class LocaleElements_fr extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "fr_FR" }, // locale id based on iso codes
            { "LocaleID", "040c" }, // Windows id
            { "ShortLanguage", "fra" }, // iso-3 abbrev lang name
            { "ShortCountry", "FRA" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "fr", "fran\u00e7ais" },
                    { "en", "anglais" },
                    { "de", "allemand" },
                    { "da", "danois" },
                    { "es", "espagnol" },
                    { "el", "grec" },
                    { "fi", "finnois" },
                    { "it", "italien" },
                    { "ja", "japonais" },
                    { "nl", "hollandais" },
                    { "no", "norv\u00e9gien" },
                    { "pt", "portugais" },
                    { "sv", "su\u00e9dois" },
                    { "tr", "turc" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "FR", "France" },
                    { "US", "\u00c9tats-Unis" },
                    { "DK", "Danemark" },
                    { "DE", "Allemagne" },
                    { "AT", "Autriche" },
                    { "GR", "Gr\u00e8ce" },
                    { "ES", "Espagne" },
                    { "FI", "Finlande" },
                    { "IT", "Italie" },
                    { "CH", "Suisse" },
                    { "BE", "Belgique" },
                    { "CA", "Canada" },
                    { "JP", "Japon" },
                    { "NL", "Pays-Bas" },
                    { "NO", "Norv\u00e8ge" },
                    { "PT", "Portugal" },
                    { "SE", "Su\u00e8de" },
                    { "TR", "Turquie" }
                }
            },
            { "MonthNames", 
                new String[] { 
                    "janvier", // january
                    "f\u00e9vrier", // february
                    "mars", // march
                    "avril", // april
                    "mai", // may
                    "juin", // june
                    "juillet", // july
                    "ao\u00fbt", // august
                    "septembre", // september
                    "octobre", // october
                    "novembre", // november
                    "d\u00e9cembre", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations", 
                new String[] { 
                    "janv.", // abb january
                    "f\u00e9vr.", // abb february
                    "mars", // abb march
                    "avr.", // abb april
                    "mai", // abb may
                    "juin", // abb june
                    "juil.", // abb july
                    "ao\u00fbt", // abb august
                    "sept.", // abb september
                    "oct.", // abb october
                    "nov.", // abb november
                    "d\u00e9c.", // abb december
                    "" // abb mo month 13 if applicable
                }
            },
            { "DayNames", 
                new String[] { 
                    "dimanche", // Sunday
                    "lundi", // Monday
                    "mardi", // Tuesday
                    "mercredi", // Wednesday
                    "jeudi", // Thursday
                    "vendredi", // Friday
                    "samedi" // Saturday
                }
            },
            { "DayAbbreviations", 
                new String[] { 
                    "dim.", // abb Sunday
                    "lun.", // abb Monday
                    "mar.", // abb Tuesday
                    "mer.", // abb Wednesday
                    "jeu.", // abb Thursday
                    "ven.", // abb Friday
                    "sam." // abb Saturday
                }
            },
            { "Eras", 
                new String[] { // era strings
                    "BC", 
                    "ap. J.-C." 
                }
            },
            { "NumberPatterns", 
                new String[] { 
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "#,##0.00 F;-#,##0.00 F", // currency pattern
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
                    "F", // local currency symbol
                    "FRF", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns", 
                new String[] { 
                    "HH' h 'mm z", // full time pattern
                    "HH:mm:ss z", // long time pattern
                    "HH:mm:ss", // medium time pattern
                    "HH:mm", // short time pattern
                    "EEEE d MMMM yyyy", // full date pattern
                    "d MMMM yyyy", // long date pattern
                    "d MMM yy", // medium date pattern
                    "dd/MM/yy", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "DateTimeElements", 
                new String[] { 
                    "2", // first day of week
                    "1" // min days in first week
                }
            },
            { "CollationElements", "@" } 
        };
    }
}
