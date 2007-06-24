/* ***** BEGIN LICENSE BLOCK *****
 * Licensed under Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * Full Terms at http://mozile.mozdev.org/license.html
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Playsophy code (www.playsophy.com).
 *
 * The Initial Developer of the Original Code is Playsophy
 * Portions created by the Initial Developer are Copyright (C) 2002-2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *	Conor Dowling <ronoc@playsophy.com>
 *	James A. Overton <james@overton.ca>
 *
 * ***** END LICENSE BLOCK ***** */

/*
 * mozileLoader V0.52
 *
 * Revised 2003-11-20 to deal propery with the mozileChromeLoader.js
 *
 * Loads mozile for a page if in a Geiko browser. This is the only javascript
 * file that a user needs to explicitly include in a page. This shields Mozile
 * from IE.
 *
 * Methods: http://devedge.netscape.com/viewsource/2002/browser-detection/
 *          http://devedge.netscape.com/library/manuals/2001/xpinstall/1.0/Chap4.html#1005439
 *
 * POST05:
 * - set default directory for config file location in extension [TBD: James]
 * - support loading config file based on a user's explicit in-document "link"
 * - distinguish old Geiko browsers (once tested to see which have
 * problems)
 * - if IE:
 *   - put up msg to upgrade to Geiko based browser
 *   - load IE toolbar
 */

mozile_js_files = new Array();
mozile_js_files.push("eDOM.js");
mozile_js_files.push("eDOMXHTML.js");
mozile_js_files.push("domlevel3.js");
mozile_js_files.push("mozCE.js");
mozile_js_files.push("mozWrappers.js");
mozile_js_files.push("mozIECE.js");
mozile_js_files.push("mozDataTransport.js");
mozile_js_files.push("jsdav.js");
mozile_js_files.push("mozilekb.js");
mozile_js_files.push("mozileSave.js");
//mozile_js_files.push("mozilehtmltb.js");

	// Don't change this!
var MOZILE_EXTENSION_DIR = "chrome://mozile/content/"; // default location of mozile is in an extension

var MOZILE_ROOT_DIR;

	// There are three options here, they may change in the future:
	// 1. Default: Don't set this. Mozile will look in the same directory that the rest of the scripts are found in for the mozileCOnfig.xml file.
	// 2. Site Specified: Set this to the directory on your site where Mozile will look for the mozileConfig.xml file.
	// 3. User Specified: Set this to MOZILE_ROOT_DIR and Mozile will look for the user's mozileConfig.xml file installed as chrome.If Mozile is not installed it will look in the scripts directory.
var MOZILE_CONFIG_DOC_DIR;
//var MOZILE_CONFIG_DOC_DIR = "http://foo.com/bar/";
//var MOZILE_CONFIG_DOC_DIR = MOZILE_EXTENSION_DIR;

var MOZILE_CONFIG_DOC; // starts at null - must be loaded

//alert("Loader: "+ MOZILE_ROOT_DIR + MOZILE_CONFIG_DOC_DIR);

// Detect Gecko but exclude Safari (for now); for now, only support XHTML
if((navigator.product == 'Gecko') && (navigator.userAgent.indexOf("Safari") == -1))
{
//alert("POINT 1");
	// navigator.productSub > '20020801' (test to see what the date should be)

	// POST05: if document.documentElement != HTML then ... or no "head" ...
	var head = document.getElementsByTagName("head")[0];

	if(head)
	{
//alert("POINT 2");

		// get the location of this script and reuse it for the others
//alert("POINT 3: " + head.childNodes.length);
		for(var i=0; i<head.childNodes.length; i++)
		{
//alert("POINT 4: " + head.childNodes[i].nodeName);
			var mozileLoaderRE = /(.*)mozileLoader.js$/;
//alert("POINT 5: " + mozileLoaderRE);
			if(head.childNodes[i].nodeName == "SCRIPT")
			{
				var src = head.childNodes[i].src;
//alert("POINT 6: " + typeof(src));
				var result = mozileLoaderRE.exec(src);
//alert("POINT 7: " + result.substr(10));
//alert("POINT 7: " + typeof(result));
				if(result)
				{
						// If the MOZILE_DOC_DIR has not been set, set it to the location of the script.
					if(!MOZILE_CONFIG_DOC_DIR) {
						MOZILE_CONFIG_DOC_DIR = result[1];
					}
					MOZILE_ROOT_DIR = result[1];
					break;
				}
			}
		}

//alert("POINT 8: " + MOZILE_ROOT_DIR);
//alert("POINT 9: " + MOZILE_CONFIG_DOC_DIR);
		//alert(MOZILE_CONFIG_DOC_DIR);

			// take care of the mozileConfig.xml file.
		MOZILE_CONFIG_DOC = document.implementation.createDocument("", "mozileConfig", null);
		MOZILE_CONFIG_DOC.async = true;
		MOZILE_CONFIG_DOC.addEventListener("load", __MOZILE_CONFIG_Loaded, false);
//alert("POINT 10: " + MOZILE_CONFIG_DOC);
//alert("POINT 11: " + MOZILE_CONFIG_DOC_DIR + "mozileConfig.xml");
		try {
			if(!MOZILE_CONFIG_DOC.load(MOZILE_CONFIG_DOC_DIR + "mozileConfig.xml")) {
//alert("POINT 12: ");
				MOZILE_CONFIG_DOC = null;
			}
		}
		catch(e)
		{
//alert("POINT 13: " + e);
			MOZILE_CONFIG_DOC = null;
		}
//alert("POINT 14: " + MOZILE_CONFIG_DOC);


		// if there is no Mozile installed already then use a remote Mozile hosted along with this Loader file
		if(!InstallTrigger.getVersion("mozile"))
		{
//alert("POINT 15");

			for (var i=0; i < mozile_js_files.length; i++)
			{
				var scr = document.createElementNS("http://www.w3.org/1999/xhtml","script");
				var src = MOZILE_ROOT_DIR + mozile_js_files[i];
//alert("POINT 16: " + src);
				scr.setAttribute("src", src);
				scr.setAttribute("language","JavaScript");
				head.appendChild(scr);
				head.removeChild(scr); // add and then remove Mozile file. It is loaded but won't show in view-source!
			}
		}

			// if Mozile is installed as chrome then just add the mozileChromeLoader.js and it'll take care of the rest
		else {
//alert("POINT 17");
			var scr = document.createElementNS("http://www.w3.org/1999/xhtml","script");
			var src = MOZILE_EXTENSION_DIR + "mozileChromeLoader.js";
			scr.setAttribute("src", src);
			scr.setAttribute("language","JavaScript");
			head.appendChild(scr);
		}


	}
	else
		alert("*** ALERT: MozileLoader only works in XHTML - load Mozile JS explicitly in XML files");
}

//alert("POINT 18");

function __MOZILE_CONFIG_Loaded(e)
{
	// catch parsing errors
	if (!MOZILE_CONFIG_DOC.documentElement || MOZILE_CONFIG_DOC.documentElement.tagName == "parsererror") {
		MOZILE_CONFIG_DOC = null;
			// a more informative error message would be good...
		alert("Configuration file " + MOZILE_CONFIG_DOC_DIR + "mozileConfig.xml could not be loaded!");
	}
}
