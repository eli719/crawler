package cn.eli486.dto;

import cn.eli486.entity.Customer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class PageInfo {
    private static Map<String, Customer> map;
    private static Map<String, Customer> verifyMap;

    public static void instance () {
        map = init ("customer.json");
        verifyMap = init ("verifyCustomer.json");
    }



    static Map<String, Customer> init (String filename) {

        ClassPathResource resource = new ClassPathResource (filename);
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
        JSONArray customers = parse.getJSONArray ("customers");
        String customersStr = customers.toJSONString ();
        List<Customer> customersList = JSON.parseArray (customersStr, Customer.class);
        Map<String, Customer> map = new HashMap<> ();
        for (Customer customer : customersList
        ) {
            map.put (customer.getOrgcode (), customer);
        }
        return map;
    }

    public static Collection<Customer> getMap () {
        return map.values ();
    }

    public static Collection<Customer> getVerifyMap () {
        return verifyMap.values ();
    }

    public static Customer getOne (String orgCode) {
        return map.get (orgCode);
    }
}
