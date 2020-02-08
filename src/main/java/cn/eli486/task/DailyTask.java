package cn.eli486.task;

import cn.eli486.entity.Customer;
import cn.eli486.excel.ExcelDemo;
import cn.eli486.util.WebUtil;
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
    private String cron;
    private String classname;
    private Customer customer;
    private boolean status;

    public DailyTask (Customer customer) {
        this.customer = customer;
        this.classname = customer.getAction ();
        this.cron = customer.getDailyTime ();
        this.status = customer.isStatus ();
    }

    @Override
    public void run () {
        try {

            ExcelDemo excelDemo = (ExcelDemo) Class.forName (classname).newInstance ();
            excelDemo.setMerge (customer.isMerge ());

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
                        excelDemo.exec (client, params, customer.getOrgcode (), customer.getOrgname ());
                    }
                } else {
                    excelDemo.exec (client, customer.getParams (), customer.getOrgcode (), customer.getOrgname ());
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
}
