package cn.eli486.dao;

import cn.eli486.entity.Customer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 *没用到
 */
@Deprecated
public class VerifyDao {
    private  static Map<String,Customer> map;
    static {
        try {
            File f = new File (VerifyDao.class.getResource ("/Verifycustomer.json").getPath ());
            String s = null;
            s = FileUtils.readFileToString (f, "utf-8");
            JSONObject parse = JSON.parseObject (s);
            JSONArray customers = parse.getJSONArray ("customers");
            String s1 = customers.toJSONString ();

            List<Customer> customers1 = JSON.parseArray (s1, Customer.class);
            map = new HashMap<> ();
            for (Customer customer : customers1
            ) {
                map.put (customer.getOrgcode (), customer);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    public Collection<Customer> getAll () {
        return map.values ();
    }
}
