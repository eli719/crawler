package cn.eli486.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author eli
 * 没用到
 */
@Deprecated
public class Customers {
    private Customer[] customers;
    public Customers (){
        List<Customer> customers1 = null;
        try {
            File f = new File (this.getClass().getResource("/customer.json").getPath ());
            String s = null;
            s = FileUtils.readFileToString (f,"utf-8");
            JSONObject parse = JSON.parseObject (s);
            JSONArray customers = parse.getJSONArray ("customers");
            String s1 = customers.toJSONString ();

            customers1 = JSON.parseArray (s1, Customer.class);
           this.customers=customers1.toArray (new Customer[0]);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    public Customer[] getCustomers () {
        return customers;
    }

    public void setCustomers (Customer[] customers) {
        this.customers = customers;
    }
}
