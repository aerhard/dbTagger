dbTagger
========

oXygen XML plugin for customized database driven tagging.

This plugin is intended to speed up tagging XML documents in oXygen's editor or author view by providing database lookups via http in configurable popup windows. Use cases might include the insertion of stored id numbers or the canonical names of persons, places, bibliographic records etc.  

Prerequisites
-------------

oXygen XML Editor 15.0+

Installation
------------

* Download and extract the zip file. 
* Close oXygen.
* Copy the content of the "dist" folder (= the subfolder "dbTagger") to the "plugins" subdirectory of your oXygen installation.
* Restart oXygen.
* In the main menu, there will be a new menu item "dbTagger", containing four search buttons (the search dialogs will only pop up if there is a text or author editor panel opened) and a preferences menu. 
* If you are running an eXist database instance on localhost:8080, load the content of the folder "xql" (a folder named "dbTagger" containing one xql file) from the extracted zip to the eXist root folder. The pre-defined queries of the dbTagger oXygen plugin will point to this location. To try it out, open an xml file in oXygen, select a passage of text and the click on one of the search buttons in the dbTagger menu to launch a demo query.
* Otherwise, adjust the query strings in the preferences menu according to your server configuration; by default, they are passing two search parameters, as in localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=per&q=. Note that the second parameter doesn't have a value; it will get its value from the selection in the editor panel.

Expected server response
------------------------

The plugin expects the server to return a JSON response, providing data to fill up a selection table with one or more rows. The first array in the JSON object, "cols", contains key-value pairs of the column names, each wrapped in a separate object; the second array contains the data for the table body:

	{ 
		"cols" : [{ 
				"name" : "Key" 
			}, { 
				"name" : "Name" 
			}, { 
				"name" : "Description" 
			}], 
		"data" : [[
				"id-1", 
				"Mottl", 
				"Dirigent"
			], [
				"id-2", 
				"Singer", 
				"Arrangeur"
			]] 
	}

(See the file dbtagger.xql in the xql/dbtagger directory for an example of an eXist server script.)

Insertion templates
-------------------

In the preferences menu, you can specify one or more content templates, which will be combined with the specified columns in the selected results table row. To specify, which columns to add, insert $(n) for each row n into the template. The string $(selection) will re-insert the previously selected text at the specified position. 

* ${1} - returns the value of the first column of the results table
* ${2} - returns the value of the second column
	etc.
* ${selection} - inserts the initially selected text into the template

Examples:

* &lt;rs type="person" key="${1}"&gt;${selection}&lt;/rs&gt; - will wrap the selected text with an rs element and a key attribute with the value of the first column
* &lt;repository key="${1}"&gt;${2}&lt;/repository&gt; - will replace the selected text

The plugin generates a separate menu entry and popup menu for each template you specify. 
When you assign a shortcut key to a template, be sure that your allocation doen't contradict the other shortcuts in oXygen as the plugin shortcould should not work then. In such a case, change the settings either in the plugin's or in oXygen's settings.

Further Notes
-------------

Tested with the standalone oXygen editor in Windows 7. 
The pre-defined shortcut keys probably need adjustment ("meta" instead of "control"?) when you're using the plugin on a MAC.
User preferences are stored as binary files in the oxygen app folder, in Windows: %appdata%\com.oxygenxml  
