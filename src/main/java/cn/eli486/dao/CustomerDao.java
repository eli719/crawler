package cn.eli486.dao;

import cn.eli486.entity.Customer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 * 对Customer数据进行crud
 */
@Component
public class CustomerDao {
    private Map<String, Customer> mapCustomers;

    private  CustomerDao () {
        try {
            //加载json获取定时任务
//            File f = new File (this.getClass ().getResource ("/customer.json").getPath ());
            ClassPathResource resource = new ClassPathResource ("customer.json");
            StringBuilder builder =new StringBuilder ();
            InputStreamReader inputStreamReader=new InputStreamReader (resource.getInputStream (),"utf-8");
            BufferedReader bufferedReader=new BufferedReader (inputStreamReader);
            String s = null;
            while ((s=bufferedReader.readLine ())!=null) {
                    builder.append (s);
            }
            bufferedReader.close ();
            JSONObject parse = JSON.parseObject (builder.toString ());
            JSONArray customers = parse.getJSONArray ("customers");
            String s1 = customers.toJSONString ();

            List<Customer> customers1 = JSON.parseArray (s1, Customer.class);
            this.mapCustomers = new HashMap<> ();
            for (Customer customer : customers1
            ) {
                this.mapCustomers.put (customer.getOrgcode (), customer);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

        public void save (Customer customer) {
        if (customer.getOrgcode () != null) {
            mapCustomers.replace (customer.getOrgcode (), customer);
        }
    }

    public Collection<Customer> getAll () {
        return mapCustomers.values ();
    }

    public Customer get (String orgcode) {
        return mapCustomers.get (orgcode);
    }

    public void delete (String orgcode) {
        Customer customer = mapCustomers.get (orgcode);
        customer.setStatus (false);
        mapCustomers.remove (orgcode);
    }
}
