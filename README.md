dbTagger
========
The dbTagger plug-in adds pop-up dialogs to the oXygen XML Editor to facilitate finding identifiers or names (for example, of persons, places, bibliographic records in TEI documents) in local or remote databases and inserting them into XML documents.

Features
--------

- Configurable server look-ups
- User-defined templates to format the retrieved information 
- User-defined keyboard shortcuts
- Configuration editor
- Support for multiple configurations side by side

Prerequisites
-------------

oXygen XML Editor 15.0+

Installation
------------

In oXygen, navigate to `Options / Preferences / Add-ons` and add the following URL to the update sites: 

```
https://raw.githubusercontent.com/aerhard/dbTagger/master/target/update/extension.xml
```

Click on `OK` and let oXygen install the plug-in by navigating to `Help / Check for add-ons updates ...`. When the plug-in is installed, restart oXygen.

(You can alternatively clone this repository and extract the jar file to the `target` folder in oXygen's plug-in directory. The option for automatic updates will not be available then.) 

Usage
-----

If you are running an eXist database on localhost:8080, you can upload the file `src/test/xql/dbtagger/dbtagger.xql` to the database collection `/db/dbtagger`. 

You can then view a demo server response by opening a new XML file, selecting some text and clicking on one of the top buttons in the dbTagger menu (to the left of the main menu's `Window` item). 

Preconfigured are two demo server requests, pointing to `localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=per&q=`. Select `Configure ...` in the dbTagger menu to change the connection settings, add / delete items, modify templates or adapt the shortcuts to your preferences. 


Server setup
------------

The plugin expects the server to return a response in JSON format. The first array in the JSON object, "cols", has to contain key-value pairs of the column names, each wrapped in a separate object; the second array contains the data filling the table body in the search results list:

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

When a new search window is opened, the plug-in adds the GET parameter `first=true` to the request which will not be added to subsequent search requests from the search field of the dialog. This way, the first request - based on the text selected in the editor pane - can be handled differently than the following requests based on the user input. 

Templates
-------------------

The way data from the server is added to the XML file can be specified by a template for each item in the config list. A dbTagger template is a string representation of the resulting XML text which contains information where to insert the data of the search results table. 

In order to provide a reference to the content of a certain column, write `$(n)` into the template (n is the number of the column, starting with 1). `$(selection)` will insert the previously selected text at the specified position - if it's missing, the selected text gets overwritten. 

* ${1} - returns the value of the first column of the results table
* ${2} - returns the value of the second column
	etc.
* ${selection} - inserts the initially selected text into the template

Template examples:

* `<rs type="person" key="${1}">${selection}</rs>` - will wrap the selected text in an &lt;rs&gt; element; the key attribute of the element gets the value of the first data column
* `<repository key="${1}">${2}</repository>` - will replace the selected text by the value in the second column, wrapped in a &lt;repository&gt; element.

Note: In templates for the oXygen author pane, you have to specify namespaces (for example `<rs xmlns="http://www.tei-c.org/ns/1.0" type="person" key="${1}">${selection}</rs>`). In text editor templates, namespaces can be omitted.

Keyboard shortcuts
------------------

You can specify shortcuts by nagivating to the `dbTagger / Configure ...` menu item and entering your shortcut in the last table column. When you assign a keyboard shortcut, check that it isn't already taken by oXygen (in which case the assignment would take no effect).

UI Languages
------------

Currently supported are English and German. For a new language, add a file with your translations to `src/main/resources` and perform a new build.

Development
-----------

The plug-in is organized as a [Maven project](http://maven.apache.org/), which depends on the [oXygen 16 SDK](http://www.oxygenxml.com/oxygen_sdk.html). When Maven is installed on your computer, build the plug-in by running `mvn clean install` from the root directory of the project. 

Further Notes
-------------

Tested with the standalone oXygen XML Editor in Windows 7. The pre-defined shortcut keys possibly need adjustment (`meta` instead of `control`) when you're using the plugin on a MAC (that's not tested yet).
User preferences are stored as binary files in the oxygen app folder, in Windows: `%appdata%\com.oxygenxml`
