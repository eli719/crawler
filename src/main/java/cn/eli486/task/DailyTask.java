package cn.eli486.task;

import cn.eli486.entity.Customer;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author eli
 * 定时任务执行类
 */
public class DailyTask implements Runnable {
    private String orgcode;
    private String orgname;
    private String cron;
    private String classname;
    private Customer customer;
    private boolean status;

    public DailyTask (Customer customer) {
        this.orgcode=customer.getOrgcode ();
        this.orgname=customer.getOrgname ();
        this.customer = customer;
        this.classname = customer.getAction ();
        this.cron = customer.getDailyTime ();
        this.status = customer.isStatus ();
    }

    @Override
    public void run () {
        try {

            Abstraction abstraction = (Abstraction) Class.forName (classname).newInstance ();
            abstraction.setMerge (customer.isMerge ());

            CloseableHttpClient client = WebUtil.getHttpClient ();
            Set<String> strings = customer.getParams ().keySet ();
            Iterator<String> iterator = strings.iterator ();
            if(iterator.hasNext ()) {
                String next = iterator.next ();
                //区分多账号
                if (next.contains ("@@")) {
                    String[] para1 = next.split ("@@");
                    String[] para2 = customer.getParams ().get (next).split ("@@");
                    for (int i = 0; i < para1.length; i++) {
                        Map<String, String> params = new HashMap<> ();
                        params.put (para1[i], para2[i]);
                        abstraction.exec (client, params, customer.getOrgcode (), customer.getOrgname ());
                    }
                } else {
                    Map<String, String> params = new HashMap<> ();
                    params.putAll (customer.getParams ());
                    abstraction.exec (client, params, customer.getOrgcode (), customer.getOrgname ());
                }
            }

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public boolean isStatus () {
        return status;
    }

    public void setStatus (boolean status) {
        this.status = status;
    }

    public String getClassname () {
        return classname;
    }

    public String getCron () {
        return cron;
    }

    public void setCron (String cron) {
        this.cron = cron;
    }

    public String getOrgcode () {
        return orgcode;
    }

    public void setOrgcode (String orgcode) {
        this.orgcode = orgcode;
    }

    public String getOrgname () {
        return orgname;
    }

    public void setOrgname (String orgname) {
        this.orgname = orgname;
    }

    public void setClassname (String classname) {
        this.classname = classname;
    }

    public Customer getCustomer () {
        return customer;
    }

    public void setCustomer (Customer customer) {
        this.customer = customer;
    }
}
