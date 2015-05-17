dbTagger
========
The dbTagger plug-in adds pop-up dialogs to the oXygen XML Editor to facilitate finding identifiers or names (for example, of persons, places, bibliographic records in TEI documents) in local or remote databases and inserting them into XML documents.

Status
------
[![Build Status](https://travis-ci.org/aerhard/dbTagger.svg?branch=master)](https://travis-ci.org/aerhard/dbTagger)


Features
--------

- Configurable server look-ups
- User-defined templates to format the data retrieved 
- User-defined keyboard shortcuts
- Configuration editor
- Support for multiple configurations side by side

Prerequisites
-------------

oXygen XML Editor 15.0+

Installation
------------

In oXygen, select `Options / Preferences / Add-ons` in the main menu and add the following URL to the update sites: 

```
https://raw.githubusercontent.com/aerhard/dbTagger/master/target/update/latest.xml
```

Click `OK` and let oXygen install the plug-in by selecting `Help / Check for add-ons updates ...`.

(You can alternatively clone this repository and extract the jar file to the `target` folder in oXygen's plug-in directory.) 

Usage
-----

If you are running an eXist database on localhost:8080, upload the file `src/test/xql/dbtagger/dbtagger.xql` to the database collection `/db/dbtagger` and view a demo server response by opening a new XML file, selecting some text and clicking on one of the top buttons in the dbTagger menu (to the left of the main menu's `Window` item). 

The demo server requests point to `localhost:8080/exist/rest/db/dbtagger/dbtagger.xql`. Select `Configure ...` in the dbTagger menu to change the connection settings, add / delete items, modify templates or adapt the shortcuts. 


Server setup
------------

The plugin expects the server to return a response in JSON format. The first array in the JSON object, "cols", must contain key-value pairs of the column names in the results table, each wrapped in a separate object; the second array contains the data of the table body:

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
				"Conductor"
			], [
				"id-2", 
				"Singer", 
				"Arranger"
			]] 
	}

(See the file `src/test/xql/dbtagger/dbtagger.xql` for a sample server script.)

When a new search window is opened, the plug-in adds the GET parameter `first=true` to the request which will not be added to subsequent search requests from the search field of the dialog. This way, the first request can be processed distinctly on the server. I use this feature to strip titles like "Herr", "Frl." etc. from the first query string (which is generated from the selection in the XML document); subsequent queries typed by the user in the search text field get processed without removing these components.

Templates
-------------------

For each item in the config menu, you can specify a template which determines in what form the data from the server will be added to the XML document. In order to add the content of a certain column, write `$(n)` into the template (n is the number of the column, starting with 1). `$(selection)` will insert the previously selected text at the specified position (if it's missing, the selected text gets overwritten). 

* ${1} - returns the value of the first column of the selected row in the search results table
* ${2} - returns the value of the second column
	etc.
* ${selection} - inserts the initially selected text into the document

Examples of templates:

* `<rs type="person" key="${1}">${selection}</rs>` - will wrap the selected text in an &lt;rs&gt; element; the key attribute of the element gets the value of the first data column
* `<repository key="${1}">${2}</repository>` - will replace the selected text with the value in the second column, wrapped in a &lt;repository&gt; element.

Keyboard shortcuts
------------------

Specify keyboard shortcuts for single search actions by opening the config dialog (`dbTagger / Configure ...`) and entering the new shortcut in the last column. Shortcuts will not override oXygen's shortcuts. If you would like to use a shortcut already occupied, you'll have change the shortcut settings in oXygen accordingly.

UI Languages
------------

Installed are English and German translations. If you want to use UI elements in another language, add a file with your translations to `src/main/resources` and perform a new build.

Development
-----------

The plug-in is organized as a [Maven project](http://maven.apache.org/), which depends on the [oXygen 16 SDK](http://www.oxygenxml.com/oxygen_sdk.html). When Maven is installed on your computer, build the plug-in by running `mvn clean install` from the root directory of the project. 
