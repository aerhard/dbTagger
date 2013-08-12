xquery version "1.0";

(:
import module namespace al = "prefs" at "xmldb:exist:///db/apps/adb/modules/prefs.xq";
:)

declare option exist:serialize 'indent=no'; 

declare variable $coll := request:get-parameter('coll', '');
declare variable $q := request:get-parameter('q', '');

(: ******** demo server response ******** :)
(::)
<table>
	<tr>
		<td>key-0</td>
		<td>This is a demo server response not performing a real database search.</td>
	</tr>
	<tr>
		<td>key-1</td>
		<td>User: {xmldb:get-current-user()}, get parameter "coll": "{$coll}", get parameter "q": "{$q}"</td>
	</tr>
		<tr>
		<td>key-2</td>
		<td>(You can customize the search parameters in the plugin's user preferences dialog.)</td>
	</tr>
	<tr>
		<td>key-3</td>
		<td>(To set up a database search, modify dbtagger.xql in eXist or change the request path in the plugin's user preferences to call your own script.)</td>
	</tr>
	<tr>
		<td>key-4</td>
		<td>(Double click on an item in this list or press "Enter" to insert a document fragment generated from the selected item.)</td>
	</tr>
	<tr>
		<td>key-5</td>
		<td>(To change the way items get inserted, modify the insertion templates in the user preferences dialog.)</td>
	</tr>
	<tr>
		<td>key-6</td>
		<td>description-6</td>
	</tr>
	<!-- etc. -->
</table>





(:
declare function local:returnPersons ($q as xs:string) as item()* {
	let $hits := 
		if ($q='') 
		then () 
		else collection($al:qpfadper)//doc[ft:query(., $q, $al:luceneopt)]
	return
		element table {
			for $i in $hits 
			return
			<tr>
				<td>{substring-before(util:document-name($i), '.')}</td>
				<td>{$i//name/text()}</td>
				<td/>
			</tr>
		}
};

declare function local:returnRepos ($q as xs:string) as item()* {
	let $hits := 
		if ($q = "")
		then
			doc($al:instdatei)/institutionen/institution[not(name="")] 
		else
			doc($al:instdatei)/institutionen/institution[contains(./name, $q) or contains(./ort, $q)]
	return
		element table {
			for $i in $hits 
			return
			<tr>
				<td>{data($i/@xml:id)}</td>
				<td>{$i/name/text()}{
					if ($i/ort/text()) then concat(' (', $i/ort/text(), ')') else ()
				}</td>
			</tr>
			}
};

if ($coll="repo") then local:returnRepos($q) (\: coll="repo", q="" -> full repo table in oxygen :\)
else
if ($coll="per") then local:returnPersons($q) (\: coll="per", q="" -> empty persons table in oxygen :\)
else
	() (\: not(coll = ('repo', 'per')) -> error message in oxygen :\)

:)