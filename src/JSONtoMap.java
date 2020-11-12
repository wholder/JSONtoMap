import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
  *  Scaled-down JSON parser that works with a subset of the JSON spec and returns a nested
  *  structure of Maps and Lists that contain only String keys and values.
  *  Author: Wayne Holder, 2020
  *  License: MIT (https://opensource.org/licenses/MIT)
 */


public class JSONtoMap {
  interface JNode {
    void add (String key, Object val);
  }

  static class JList extends ArrayList<Object> implements JNode {
    public void add (String key, Object val) {
      if (val instanceof String) {
        Map<String,String> keyVal = new HashMap<>();
        keyVal.put(key, (String) val);
        add(keyVal);
      } else {
        add(val);
      }
    }
  }

  static class JMap extends LinkedHashMap<String,Object> implements JNode {
    public void add (String key, Object val) {
      put(key, val);
    }
  }

  /**
   * Parse a simplfied version of JSON and convert into a nested structire of Map and List objects
   * @param src JSON file source in a String
   * @return output Map
   */
  public static Map<String,Object> parse (String src) {
    List<JNode> stack = new ArrayList<>();
    JNode top = null;
    String str = null;
    String key = null;
    char[] ca = src.toCharArray();
    for (int idx = 0; idx < ca.length; idx++) {
      char cc = ca[idx];
      switch (cc) {
      case '{':
      case '[':
        JNode nextMap = cc == '{' ? new JMap() :  new JList();
        if (top != null) {
          top.add(key, nextMap);
          stack.add(top);
        }
        top = nextMap;
        key = null;
        break;
      case '}':
      case ']':
        if (stack.size() > 0) {
          top = stack.remove(stack.size() - 1);
        }
        break;
      case '\"':
        StringBuilder buf = new StringBuilder();
        while ((cc = ca[++idx]) != '\"') {
          buf.append(cc);
        }
        str = buf.toString();
        if (key != null) {
          top.add(key, str);
          key = null;
        }
        break;
      case ':':
        if (key == null) {
          key = str;
        }
        break;
      }
    }
    return (JMap) top;
  }

  private static void expand (StringBuilder buf, Object obj, String indent) {
    if (obj instanceof Map<?,?>) {
      Map<?,?> map = (Map<?,?>) obj;
      int size = map.size();
      int count = 0;
      for (Object key : map.keySet()) {
        Object val = map.get(key);
        if (val instanceof String) {
          buf.append(indent).append("\"").append(key).append("\" : \"").append(val).append("\"");
        } else if (val instanceof Map) {
          buf.append(indent).append("\"").append(key).append("\" : {\n");
          expand(buf, val, indent + " ");
          buf.append(indent).append("}");
        } else if (val instanceof List) {
          buf.append(indent).append("\"").append(key).append("\" : [\n");
          expand(buf, val, indent + " ");
          buf.append(indent).append("]");
        }
        buf.append(++count < size ? ",\n" : "\n");
      }
    } else if (obj instanceof List<?>) {
      List<?> list = (List<?>) obj;
      int size = list.size();
      int count = 0;
      for (Object val : list) {
        buf.append(indent).append("{\n");
        expand(buf, val, indent + " ");
        buf.append(indent).append("}");
        buf.append(++count < size ? ",\n" : "\n");
      }
    }
  }

  /**
   * Convert a structure of nested Map and List objects into an indented JSON file
   * @param obj input Map
   * @return indented JSON file contents in a String
   */
  public static String expand (Map<String,Object> obj) {
    StringBuilder buf = new StringBuilder("{\n");
    expand(buf, obj, " ");
    buf.append("}");
    return buf.toString();
  }

  /**
   * Test code - reads JSON file and cpnverts to nested Map structure then converts
   * back to indented JSON file and prints contents
   * @param args command line args
   * @throws IOException I/O Exception
   */
  public static void main (String[] args) throws IOException {
    InputStream fis = new FileInputStream("resources/test.json");
    byte[] data = new byte[fis.available()];
    fis.read(data);
    fis.close();
    String src = new String(data, StandardCharsets.UTF_8);
    Map<String,Object> out = parse(src);
    System.out.println(expand(out));
  }
}
