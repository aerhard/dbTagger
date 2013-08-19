dbTagger
========

oXygen XML plugin for customized database driven tagging.

This plugin is intended to speed up tagging XML documents in oXygen with data stored in a database, 
e.g. id numbers of persons, places, bibliographic records or standardized repository names. 
It provides a popup search form triggered by menu buttons or shortcut keys and user configurable 
insertion templates. From the popup menu, content can be inserted in an Author or Text Editor Pane. 

Prerequisites
-------------

oXygen XML Editor 14.1+

Installation
------------

* Download and extract the zip file. 
* Close oXygen.
* Copy the content of the "dist" folder (= one folder named "dbTagger") to the "plugins" subdirectory of your oXygen installation.
* Restart oXygen.
* In the main menu, there will be a new item "dbTagger", containing four search buttons (the search dialogs will only pop up if there is a text or author editor panel opened) and a preferences menu. 
* If you are running an eXist database instance on localhost, load the content of the folder "xql" (a folder named "dbTagger" containing one xql file) from the extracted zip to the eXist root folder. The pre-defined queries of the the dbTagger oXygen plugin will point to this location. To try it out, open an xml file in oXygen, select a passage of text and the click on one of the search buttons in the dbTagger menu to launch a demo query.
* Otherwise, adjust the query strings in the preferences menu according to your server configuration; by default, they are passing two search parameters, as in localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=per&q=. Note that the second parameter doesn't have a value; it will get its value from the selected text in the editor panel.

Expected server response
------------------------

The plugin expects the server to return a two-column XML table (no tbody between table and tr):

    <table>
    	<tr>
    		<td>key1</td>
    		<td>description1</td>
    	</tr>
    	<tr>
    		<td>key2</td>
    		<td>description2</td>
    	</tr>
    	<tr>
    		<td>key3</td>
    		<td>description3</td>
    	</tr>
    </table>

Insertion templates
-------------------

In the preferences menu, you can specify for each query, in which way the values of the results table will be combined with other text. There are three variables you can insert at any place of your template:

* ${key} - returns the value of the first column of the results table
* ${text} - returns the value of the second column
* ${selection} - inserts the initially selected text into the template

Examples:

* &lt;rs type="person" key="${key}"&gt;${selection}&lt;/rs&gt; - will surround the selected text
* &lt;repository key="${key}"&gt;${text}&lt;/repository&gt; - will replace the selected text

