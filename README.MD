### JSONtoMap

JSONtoMap is a greatly scaled-down JSON parser and companion expander that works with a subset of the JSON spec and returns a nested structure of Maps and Lists that contain only String keys and values.   I wrote JSONtoMap because I needed something better than a Java Properties files that supported a simple, heirarchical data format consisting of String data.  The output from the `parse()` function is a Map that an contain other Maps and Lists.  Note: no access spacial functions are provided so you'll need to use the regular access functions for Maps and Lists to get at the data in the Map returned from `parse()`.  

The two main functions provided in JSONtoMap are:

`  public static Map<String,Object> parse (String src)`  

which parses a simplfied version of JSON and convert into a nested structure of Map and List objects, and:

`  public static String expand (Map<String,Object> obj)`

which converts a structure of nested Map and List objects (such as the output from `parse()`) into an indented JSON file in a String.  Here is a typical example of what returned when you call `expand()` on a Map returned from `parse()`:

#### Example JSON output from `expand()`

<pre>
{
 "firstName" : "John",
 "lastName" : "Smith",
 "isAlive" : "true",
 "age" : "27",
 "address" : {
  "streetAddress" : "21 2nd Street",
  "city" : "New York",
  "state" : "NY",
  "postalCode" : "10021-3100",
 },
 "phoneNumbers" : [
   "type" : "home",
   "number" : "212 555-1234",
   "type" : "office",
   "number" : "646 555-4567",
 ],
 "children" : [
 ],
 "spouse" : "",
}
</pre>