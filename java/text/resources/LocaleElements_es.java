/*
 * @(#)LocaleElements_es.java	1.11 01/12/10
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

public class LocaleElements_es extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
            { "LocaleString", "es_ES" }, // locale id based on iso codes
            { "LocaleID", "0c0a" }, // Windows id
            { "ShortLanguage", "esp" }, // iso-3 abbrev lang name
            { "ShortCountry", "ESP" }, // iso-3 abbrev country name
            { "Languages", // language names
                new String[][] {
                    { "es", "espa\u00f1ol" }
                }
            },
            { "Countries", // country names
                new String[][] {
                    { "ES", "Espa\u00f1a" },
                    { "AR", "Argentina" },
                    { "BO", "Bolivia" },
                    { "CL", "Chile" },
                    { "CO", "Colombia" },
                    { "CR", "Costa Rica" },
                    { "DO", "Rep\u00fablica Dominicana" },
                    { "EC", "Ecuador" },
                    { "GT", "Guatemala" },
                    { "HN", "Honduras" },
                    { "MX", "M\u00e9xico" },
                    { "NI", "Nicaragua" },
                    { "PA", "Panam\u00e1" },
                    { "PE", "Per\u00fa" },
                    { "PR", "Puerto Rico" },
                    { "PY", "Paraguay" },
                    { "SV", "El SalvadorUY" },
                    { "UY", "Uruguay"  },
                    { "VE", "Venezuela" }
                }
            },
            { "MonthNames", 
                new String[] { 
                    "enero", // january
                    "febrero", // february
                    "marzo", // march
                    "abril", // april
                    "mayo", // may
                    "junio", // june
                    "julio", // july
                    "agosto", // august
                    "septiembre", // september
                    "octubre", // october
                    "noviembre", // november
                    "diciembre", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations", 
                new String[] { 
                    "ene", // abb january
                    "feb", // abb february
                    "mar", // abb march
                    "abr", // abb april
                    "may", // abb may
                    "jun", // abb june
                    "jul", // abb july
                    "ago", // abb august
                    "sep", // abb september
                    "oct", // abb october
                    "nov", // abb november
                    "dic", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames", 
                new String[] { 
                    "domingo", // Sunday
                    "lunes", // Monday
                    "martes", // Tuesday
                    "mi\u00e9rcoles", // Wednesday
                    "jueves", // Thursday
                    "viernes", // Friday
                    "s\u00e1bado" // Saturday
                }
            },
            { "DayAbbreviations", 
                new String[] { 
                    "dom", // abb Sunday
                    "lun", // abb Monday
                    "mar", // abb Tuesday
                    "mi\u00e9", // abb Wednesday
                    "jue", // abb Thursday
                    "vie", // abb Friday
                    "s\u00e1b" // abb Saturday
                }
            },
            { "NumberPatterns", 
                new String[] { 
                    "#,##0.###;-#,##0.###", // decimal pattern
                    "#,##0.00 Pts;-#,##0.00 Pts", // currency pattern
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
                    "Pts", // local currency symbol
                    "ESP", // intl currency symbol
                    "," // monetary decimal separator
                }
            },
            { "DateTimePatterns", 
                new String[] { 
                    "HH'H'mm'' z", // full time pattern
                    "H:mm:ss z", // long time pattern
                    "H:mm:ss", // medium time pattern
                    "H:mm", // short time pattern
                    "EEEE d' de 'MMMM' de 'yyyy", // full date pattern
                    "d' de 'MMMM' de 'yyyy", // long date pattern
                    "dd-MMM-yy", // medium date pattern
                    "d/MM/yy", // short date pattern
                    "{1} {0}" // date-time pattern
                }
            },
            { "DateTimeElements", 
                new String[] { 
                    "2", // first day of week
                    "1" // min days in first week
                }
            },
            { "CollationElements", "& N < n\u0303, N\u0303 " } 
        };
    }
}
