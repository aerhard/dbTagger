xquery version "3.0";

declare namespace tei = "http://www.tei-c.org/ns/1.0";
declare namespace json="http://www.json.org";

declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:method "json";
declare option output:media-type "application/json";


declare variable $coll 				as xs:string 	:= request:get-parameter('coll', '');
declare variable $q 					as xs:string 	:= request:get-parameter('q', '');
declare variable $luceneOptions 	as node()		:= <options>
																		<default-operator>and</default-operator>
																		<leading-wildcard>yes</leading-wildcard>
																	</options>;
declare variable $queryPath		as xs:string	:=	'/db/test';


(:~
~ Static three-columns demo response
:)
declare function local:demoResponse () as node() {

	<response>
		<cols>
			<name>Key</name>
		</cols>
		<cols>
			<name>Name</name>
		</cols>
		<cols>
			<name>Description</name>
		</cols>
		<data json:array="true">
			<json:value>0</json:value>
			<json:value>This is a static demo server response, {xmldb:get-current-user()}.</json:value>
			<json:value>param "coll"="{$coll}", param "q"="{$q}"</json:value>
		</data>
		<data json:array="true">
			<json:value>1</json:value>
			<json:value>Mottl</json:value>
			<json:value>Dirigent</json:value>
		</data>
		<data json:array="true">
			<json:value>2</json:value>
			<json:value>Singer</json:value>
			<json:value>Kopist</json:value>
		</data>
		<data json:array="true">
			<json:value>3</json:value>
			<json:value>Bla</json:value>
			<json:value>x</json:value>
		</data>

	</response>

};


(:~
~ Test: Empty results
:)
declare function local:demoResponse1 () as node() {

	<response>
		<cols>
			<name>Key</name>
		</cols>
		<cols>
			<name>Name</name>
		</cols>
		<cols>
			<name>Description</name>
		</cols>
	</response>

};


(:~
~ Test: Throw a server exception
:)
declare function local:demoResponse2 () as node() { '' };


(:~
~ Example database query (two-column server response)
:)
declare function local:query () as node()* {

	let $hits := 
		if ($q='' or $coll='') 
		then () 
		else collection($queryPath)/tei:TEI[ft:query(., $q, $luceneOptions)]
	return
		element response {
			for $colName in ('Key', 'Name')
			return
				<cols>
					<name>{$colName}</name>
				</cols>
			,		
			for $i in $hits 
			return
				<data json:array="true">
					<json:value>{substring-before(util:document-name($i), '.')}</json:value>
					<json:value>{($i//tei:persName)[1]/text()}</json:value>
				</data>
		}
		
};


(:local:query():)
if ($q != "1") then local:demoResponse1() else
if ($q != "2") then local:demoResponse2() else
local:demoResponse()
