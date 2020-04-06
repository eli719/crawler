package cn.eli486.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Deprecated
public class Testc {
    public static void main (String[] args) {
        Map<String,String > map = new HashMap<> ();
        map.put ("aaa@@eee","bbb");
        map.put ("ccc@@qqq","ddd");
        boolean b = map.containsKey ("@@");
        boolean contains = map.keySet ().contains ("@@");
        System.out.println (contains);
        System.out.println (b);
        Iterator<String> iterator = map.keySet ().iterator ();
        while (iterator.hasNext ()) {
            String next = iterator.next ();
            boolean contains1 = next.contains ("@@");
            System.out.println (contains1);
            System.out.println (next.split ("@@")[1]);

        }
    }
}
