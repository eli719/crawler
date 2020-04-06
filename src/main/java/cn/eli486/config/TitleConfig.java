package cn.eli486.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author eli
 */
public class TitleConfig {

    public static List<List<String> > getTitle(){
        ClassPathResource resource = new ClassPathResource ("title.json");
        StringBuilder builder = new StringBuilder ();
        BufferedReader bufferedReader = null;
        String s = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader (resource.getInputStream (), "utf-8");
            bufferedReader = new BufferedReader (inputStreamReader);
            while ((s = bufferedReader.readLine ()) != null) {
                builder.append (s);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close ();
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        JSONObject parse = JSON.parseObject (builder.toString ());
        JSONArray customers = parse.getJSONArray ("titles");
        List<List<String>> lists=new ArrayList<> ();
        Iterator<Object> iterator = customers.iterator ();
        while (iterator.hasNext ()){
            Object next = iterator.next ();
            lists.add ((List<String>) next);
        }
        return lists;

    }


    public static void main (String[] args) {
        List<List<String>> title = getTitle ();
        System.out.println (title);
        List<String> strings = title.get (0);
    }
}
