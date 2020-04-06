package cn.eli486.dto;

import cn.eli486.config.TitleConfig;
import cn.eli486.entity.Customer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class PageInfo {
    public static Map<String, Customer> map;
    public static Map<String, Customer> verifyMap;

    public static void instance () {
        map = init ("customer.json");
        verifyMap = init ("verifyCustomer.json");
    }



    private static Map<String, Customer> init (String filename) {
        JSONArray customers = TitleConfig.parseJsonFile (filename, "customers");
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
