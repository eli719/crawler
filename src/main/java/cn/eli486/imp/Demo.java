package cn.eli486.imp;

import cn.eli486.config.TitleConfig;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo extends Abstraction {
    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        WebUtil.httpGet (client, "http://www.baheal.com/Flow/Home/Index");
        params.put ("user", "240");
        params.put ("password", "Welcome103");
        WebUtil.httpPost (client, params, "http://www.baheal.com/Flow/Home/Index");
    }

    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client, String orgName, List<String> title) {
        Map<String, String> params = new HashMap<String, String> ();
        String sDate = DateUtil.getUserDefinedDay (-59);
        String eDate = DateUtil.getUserDefinedDay (+1);
        params.put ("start", "0");
        params.put ("limit", "10000");
        params.put ("corp_supplier_id", "");
        params.put ("corp_supplier_name", "");
        params.put ("starttime", sDate);
        params.put ("endtime", eDate);
        String str = WebUtil.httpPost (client, params, "http://www.baheal.com/Flow/Flow/Purchase");
        String[] res = str.split ("id");
        List<List<String>> content = new ArrayList<> ();
        List<String> rows = null;
        for (int i = 1; i < res.length; i++) {
            rows = new ArrayList<String> ();
            String salesDate = StringUtil.getValue (res[i], "日期\":\"", "\"", 1).split (" ")[0];
            content = addRows (content,rows, title);
            addCell (rows, salesDate, 1);
            addCell (rows, StringUtil.getValue (res[i], "销售方名称\":\"", "\",", 1), 2);
            addCell (rows, StringUtil.getValue (res[i], "产品名称\":\"", "\"", 1), 3);
            addCell (rows, StringUtil.getValue (res[i], "产品规格\":\"", "\"", 1), 4);
            addCell (rows, StringUtil.getValue (res[i], "数量\":", ",\"", 1), 5);
            addCell (rows, StringUtil.getValue (res[i], "批号\":\"", "\"", 1), 6);
            addCell (rows, StringUtil.getValue (res[i], "单位\":\"", "\"", 1), 7);
            addCell (rows, StringUtil.getValue (res[i], "有效期\":\"", "\"", 1), 8);
        }
        return content;
    }

    @Override
    public List<String> createPurchase () {
        return null;
    }

    @Override
    public List<List<String>> getSale (CloseableHttpClient client, String orgName, List<String> title) throws IOException {
        Map<String, String> params = new HashMap<String, String> ();
        String sDate = DateUtil.getUserDefinedDay (-59);
        String eDate = DateUtil.getUserDefinedDay (+1);
        params.put ("start", "0");
        params.put ("limit", "10000");
        params.put ("corp_supplier_id", "");
        params.put ("corp_supplier_name", "");
        params.put ("starttime", sDate);
        params.put ("endtime", eDate);
        String str = WebUtil.httpPost (client, params, "http://www.baheal.com/Flow/Flow/Sales");
        String[] res = str.split ("num");
        List<List<String>> content = new ArrayList<> ();
        List<String> rows = null;
        for (int i = 1; i < res.length; i++) {
            rows = new ArrayList<String> ();
            String salesDate = StringUtil.getValue (res[i], "日期\":\"", "\"", 1).split (" ")[0];
            content = addRows (content,rows, title);
            addCell (rows, salesDate, 1);
            addCell (rows, StringUtil.getValue (res[i], "采购方代码\":\"", "\"", 1), 3);
            addCell (rows, StringUtil.getValue (res[i], "采购方名称\":\"", "\"", 1), 2);
            addCell (rows, StringUtil.getValue (res[i], "产品名称\":\"", "\"", 1), 4);
            addCell (rows, StringUtil.getValue (res[i], "产品规格\":\"", "\"", 1), 5);
            addCell (rows, (StringUtil.getValue (res[i], "数量\":", "\"", 1)).substring (0, (StringUtil.getValue (res[i], "数量\":", "\"", 1)).length () - 1), 6);
            addCell (rows, StringUtil.getValue (res[i], "批号\":\"", "\"", 1), 12);
            addCell (rows, StringUtil.getValue (res[i], "单位\":\"", "\"", 1), 13);
        }
        return content;
    }


    @Override
    public List<String> createSale () {
        return null;
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client, String orgName, List<String> title) throws IOException {

        Map<String, String> params = new HashMap<> ();
        params.put ("start", "0");
        params.put ("limit", "200");
        params.put ("corp_supplier_id", "");
        params.put ("corp_supplier_name", "");
        String str = WebUtil.httpPost (client, params, "http://www.baheal.com/Flow/Flow/Stock");
        String[] res = str.split ("num");
        List<List<String>> content = new ArrayList<> ();
        List<String> rows = null;
        for (int i = 1; i < res.length; i++) {
            rows = new ArrayList<String> ();
            content = addRows (content,rows, title);
            addCell (rows, DateUtil.getBeforeDayAgainstToday (0, "yyyy-MM-dd"), 1);
            addCell (rows, StringUtil.getValue (res[i], "产品名称\":\"", "\",", 1), 2);
            addCell (rows, StringUtil.getValue (res[i], "\"产品代码\":\"", "\",", 1), 3);
            addCell (rows, StringUtil.getValue (res[i], "数量\":", ",\"", 1), 4);
            addCell (rows, StringUtil.getValue (res[i], "批号\":\"", "\",", 1), 5);
            addCell (rows, StringUtil.getValue (res[i], "有效期\":\"", "\",", 1), 7);
            addCell (rows, StringUtil.getValue (res[i], "单位\":\"", "\",", 1), 6);
        }
        return content;
    }


    @Override
    public List<String> createStock () {
        return TitleConfig.getTitle ().get (0);
    }

    public static void main (String[] args) throws Exception {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        Map<String, String> stringStringMap = new HashMap<> ();
        new Demo ().exec (client, stringStringMap, "a", "b");
    }
}


