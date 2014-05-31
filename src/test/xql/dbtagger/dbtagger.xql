xquery version "3.0";

declare namespace tei = "http://www.tei-c.org/ns/1.0";
declare namespace json="http://www.json.org";

declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:method "json";
declare option output:media-type "application/json";


declare variable $coll             as xs:string   := request:get-parameter('coll', '');
declare variable $q                as xs:string   := request:get-parameter('q', '');
declare variable $first            as xs:string   := request:get-parameter('first', '');
declare variable $luceneOptions    as node()      := <options>
                                                        <default-operator>and</default-operator>
                                                        <leading-wildcard>yes</leading-wildcard>
                                                     </options>;
declare variable $queryPath      as xs:string     := '/db/test';


(:~
~ Three-columns demo response
:)
declare function local:demoResponse () as node() {

   <response>
      <cols>
         <name>Key</name>
      </cols>
      <cols>
         <name>Name</name>
      </cols>
      <data json:array="true">
         <json:value>a</json:value>
         <json:value>DEMO SERVER RESPONSE</json:value>
      </data>
      <data json:array="true">
         <json:value>b</json:value>
         <json:value>Type in a string, hit "Enter" and check the "q" parameter in the next row.</json:value>
      </data>
      <data json:array="true">
         <json:value>c</json:value>
         <json:value>User: "{xmldb:get-current-user()}". Parameters: q: "{$q}", coll: "{$coll}", first: "{$first}".</json:value>
      </data>
      <data json:array="true">
         <json:value>d</json:value>
         <json:value>Select a table entry and press the "OK" button (or double click on the entry) to insert a new fragment to the current editor pane.</json:value>
      </data>
      <data json:array="true">
         <json:value>1</json:value>
         <json:value>Search for "1" to get a multi-column response.</json:value>
      </data>
      <data json:array="true">
         <json:value>2</json:value>
         <json:value>Search for "2" to a get column names, but no data. (no error)</json:value>
      </data>
      <data json:array="true">
         <json:value>3</json:value>
         <json:value>Search for "3" to cause a server exception. ERROR</json:value>
      </data>
      <data json:array="true">
         <json:value>4</json:value>
         <json:value>Search for "4" to request malformed data. ERROR</json:value>
      </data>
   </response>

};



(:~
~ Three-columns demo response
:)
declare function local:demoResponse1 () as node() {

   <response>
      <cols>
         <name>x</name>
      </cols>
      <cols>
         <name>MULTI</name>
      </cols>
      <cols>
         <name>COLUMN</name>
      </cols>
      <cols>
         <name>SERVER</name>
      </cols>
      <cols>
         <name>RESPONSE</name>
      </cols>
      <cols>
         <name>Data3</name>
      </cols>
      <cols>
         <name>Data4</name>
      </cols>
      <data json:array="true">
         <json:value>d11</json:value>
         <json:value>d12</json:value>
         <json:value>d13</json:value>
         <json:value>d14</json:value>
         <json:value>d15</json:value>
         <json:value>d16</json:value>
         <json:value>d17</json:value>
      </data>
      <data json:array="true">
         <json:value>d21</json:value>
         <json:value>d22</json:value>
         <json:value>d23</json:value>
         <json:value>d24</json:value>
         <json:value>d25</json:value>
         <json:value>d26</json:value>
         <json:value>d27</json:value>
      </data>
   </response>

};




(:~
~ Column names, but no data -> OK
:)
declare function local:demoResponse2 () as node() {

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
~ Server error -> error msg
:)
declare function local:demoResponse3 () as node() { '' };


(:~
~ Return wrong number of columns in data array -> error msg
:)
declare function local:demoResponse4 () as node() {

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
         <json:value>1</json:value>
         <!-- MISSING COLUMN -->
      </data>
   </response>

};



(:~
~ Example database query (two-column server response)
:)
(:declare function local:query () as node()* {

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
      
};:)



if ($q = "1") then local:demoResponse1() else
if ($q = "2") then local:demoResponse2() else
if ($q = "3") then local:demoResponse3() else
if ($q = "4") then local:demoResponse3() else
local:demoResponse()
(:local:query():)